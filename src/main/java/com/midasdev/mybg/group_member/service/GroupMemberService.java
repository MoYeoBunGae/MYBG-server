package com.midasdev.mybg.group_member.service;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.s3.S3ImageService;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.domain.GroupStatistics;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group.repository.GroupStatisticsRepository;
import com.midasdev.mybg.group_member.controller.dto.request.GroupJoinRequest;
import com.midasdev.mybg.group_member.controller.dto.request.GroupMemberProfileUpdateRequest;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.group_member.repository.GroupMemberSpringDataRepository;
import com.midasdev.mybg.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final S3ImageService s3ImageService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupStatisticsRepository groupStatisticsRepository;

    @Transactional
    public GroupMember joinGroup(Member member, GroupJoinRequest groupJoinRequest) {
        // group 검증
        Long requestedGroupId = groupJoinRequest.groupId();
        Group group = groupRepository.findWithStatisticsById(requestedGroupId)
                                     .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_ID,
                                                                                 requestedGroupId));
        // group에 가입 가능한지 검증
        if (group.isFull()) {
            throw new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_CAPACITY_REACHED, group.getId());
        }

        // owner 인지 검증
        if (isAlreadyGroupMember(member, group)) {
            throw new ApplicationException(ApplicationExceptionType.ALREADY_JOINED_GROUP, member.getId(), group.getId());
        }

        // nickname 이 null이면 member의 name을 사용
        String nickname = groupJoinRequest.nickname() == null ? member.getName() : groupJoinRequest.nickname();

        GroupMember groupMember = GroupMember.builder()
                                             .nickname(nickname)
                                             .member(member)
                                             .group(group)
                                             .build();
        return saveGroupMember(groupMember, group);
    }

    private GroupMember saveGroupMember(GroupMember groupMember, Group group) {
        GroupMember savedGroupMember = groupMemberRepository.save(groupMember);
        GroupStatistics groupStatistics = groupStatisticsRepository.findById(group.getId()).orElseThrow(
                () -> new ApplicationException(ApplicationExceptionType.GROUP_STATISTICS_NOT_FOUND_BY_ID, group.getId()));

        groupStatistics.increaseTotalMemberCount();
        return savedGroupMember;
    }


    private boolean isAlreadyGroupMember(Member member, Group group) {
        return group.isOwner(member) || groupMemberRepository.findByMemberAndGroup(member, group).isPresent();
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
        if (!groupMember.isOwnedBy(member.getId())) {
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
            String imageUrl = s3ImageService.upload(image, "group-member-profile-image");
            // TODO: 트랜젝션 롤백시 S3 이미지 삭제 배치 작업 필요
            groupMember.updateProfileImageUrl(imageUrl);
        }

        return groupMember;
    }
}
