package com.midasdev.mybg.group_member.service;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.s3.S3Directory;
import com.midasdev.mybg.global.s3.S3ImageService;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group.service.GroupFinder;
import com.midasdev.mybg.group_member.controller.dto.request.GroupJoinRequest;
import com.midasdev.mybg.group_member.controller.dto.request.GroupMemberProfileUpdateRequest;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final S3ImageService s3ImageService;
    private final GroupFinder groupFinder;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * groupMemberId 기반으로 조회 후 로그인 사용자 본인 소유인지 검증
     */
    private GroupMember findGroupMemberByIdAndMember(Long groupMemberId, Member loginMember) {
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                                                       .orElseThrow(() -> new ApplicationException(
                                                               ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND, groupMemberId));

        if (!groupMember.belongsTo(loginMember.getId())) {
            throw new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_DOES_NOT_BELONG_TO_MEMBER,
                                           groupMemberId, loginMember.getId());
        }

        return groupMember;
    }

    @Transactional
    public GroupMember joinGroup(Member member, GroupJoinRequest groupJoinRequest) {
        // group 검증
        Long requestedGroupId = groupJoinRequest.groupId();
        Group group = groupFinder.findGroupById(requestedGroupId);

        // group에 가입 가능한지 검증
        if (group.isFull()) {
            throw new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_CAPACITY_REACHED, group.getId());
        }

        // 이미 가입된 그룹인지 검증
        // TODO: soft delete 반영하도록 수정
        if (isAlreadyGroupMember(member, group)) {
            throw new ApplicationException(ApplicationExceptionType.ALREADY_JOINED_GROUP, member.getId(), group.getId());
        }

        // nickname 이 null이면 member의 name을 사용
        String nickname = groupJoinRequest.nickname() == null ? member.getName() : groupJoinRequest.nickname();

        GroupMember groupMember = GroupMember.builder()
                                             .nickname(nickname)
                                             .member(member)
                                             // TODO: 기본 프로필 이미지 URL로 변경
                                             .memberProfileImageUrl(member.getProfileImageUrl())
                                             .group(group)
                                             .build();
        return saveGroupMember(groupMember, group);
    }

    private GroupMember saveGroupMember(GroupMember groupMember, Group group) {
        GroupMember savedGroupMember = groupMemberRepository.save(groupMember);
        group.addMember();
        return savedGroupMember;
    }


    private boolean isAlreadyGroupMember(Member member, Group group) {
        return group.isOwnedBy(member) || groupMemberRepository.findByMemberAndGroup(member, group).isPresent();
    }

    @Transactional
    public GroupMember updateProfile(Long groupMemberId, Member member, GroupMemberProfileUpdateRequest request) {
        // 1. 그룹 멤버 조회
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                                                       .orElseThrow(() -> new ApplicationException(
                                                               ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND,
                                                               member.getId(), groupMemberId
                                                       ));

        // 2. 요청 유저의 소유 여부 검증
        if (!groupMember.belongsTo(member.getId())) {
            throw new ApplicationException(
                    ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND,
                    member.getId(), groupMemberId
            );
        }

        // 3. 닉네임 업데이트
        if (request.nickname() != null) {
            groupMember.updateNickname(request.nickname());
        }

        // 4. 이미지 업데이트
        MultipartFile image = request.image();
        if (image != null && !image.isEmpty()) {
            String imageUrl = s3ImageService.upload(image, S3Directory.GROUP_MEMBER_PROFILE_IMAGES);
            // TODO: 트랜젝션 롤백시 S3 이미지 삭제 배치 작업 필요
            groupMember.updateProfileImageUrl(imageUrl);
        }

        return groupMember;
    }

    /**
     * 그룹 나가기
     * - 그룹의 소유자는 나갈 수 없음
     */
    @Transactional
    public void leaveGroup(Long groupMemberId, Member loginMember) {
        GroupMember groupMember = findGroupMemberByIdAndMember(groupMemberId, loginMember);

        Group group = groupMember.getGroup();

        // 소유자는 나갈 수 없음
        if (group.isOwnedBy(loginMember)) {
            throw new ApplicationException(ApplicationExceptionType.GROUP_OWNER_CANNOT_LEAVE, group.getId());
        }

        groupMember.leave();
        group.removeMember();
    }
}
