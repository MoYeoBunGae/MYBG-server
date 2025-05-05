package com.midasdev.mybg.group.service;

import com.midasdev.mybg.global.application.DefaultProfileImageService;
import com.midasdev.mybg.global.application.DefaultProfileImageType;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.s3.S3Directory;
import com.midasdev.mybg.global.s3.S3ImageService;
import com.midasdev.mybg.global.util.assertion.Assertion;
import com.midasdev.mybg.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mybg.group.controller.dto.request.GroupUpdateRequest;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.domain.GroupStatistics;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group.repository.GroupSpringDataRepository;
import com.midasdev.mybg.group.repository.GroupStatisticsRepository;
import com.midasdev.mybg.group.service.component.InvitationCodeGenerator;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberSpringDataRepository;
import com.midasdev.mybg.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private static final int CODE_LENGTH = 8;

    private final DefaultProfileImageService defaultProfileImageService;
    private final S3ImageService s3ImageService;
    private final GroupSpringDataRepository groupSpringDataRepository;
    private final GroupRepository groupRepository;
    private final InvitationCodeGenerator invitationCodeGenerator;
    private final GroupMemberSpringDataRepository groupMemberSpringDataRepository;
    private final GroupStatisticsRepository groupStatisticsRepository;

    private Group findGroupById(Long groupId) {
        return groupSpringDataRepository.findByIdAndDeletedIsFalse(groupId)
                                        .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_ID, groupId));
    }

    public Group findGroupWithStatisticsById(Long groupId) {
        return groupRepository.findWithStatisticsById(groupId)
                              .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_ID, groupId));
    }

    @Transactional
    public Group createGroup(Member member, GroupCreateRequest groupCreateRequest) {
        String profileImageUrl = StringUtils.hasText(groupCreateRequest.profileImageUrl())
                ? groupCreateRequest.profileImageUrl()
                : defaultProfileImageService.createRandomProfileImageUrl(DefaultProfileImageType.GROUP);

        Group group = Group.builder()
                           .owner(member)
                           .name(groupCreateRequest.name())
                           .profileImageUrl(profileImageUrl)
                           .maxMemberCount(groupCreateRequest.maxMemberCount())
                           .deleted(false)
                           .build();

        String invitationCode = generateUniqueRandomInvitationCode();

        group.updateInvitationCode(invitationCode);
        Group savedGroup = groupSpringDataRepository.save(group);

        // 그룹 생성자를 그룹 멤버로 추가
        addOwnerToGroupMember(member, savedGroup);

        // 통계 테이블 row 생성
        createGroupStatistics(savedGroup);

        return savedGroup;
    }

    private void addOwnerToGroupMember(Member member, Group savedGroup) {
        GroupMember owner = GroupMember.builder()
                                       .nickname(member.getName())
                                       .group(savedGroup)
                                       // TODO: 그룹 생성 시 그룹 내의 방장 프로필 이미지 설정 여부 검토
                                       .memberProfileImageUrl(member.getProfileImageUrl())
                                       .member(member)
                                       .build();
        groupMemberSpringDataRepository.save(owner);
    }

    private String generateUniqueRandomInvitationCode() {
        String code;
        do {
            code = invitationCodeGenerator.generateRandomCode(CODE_LENGTH);
        } while (groupSpringDataRepository.existsByInvitationCode(code));
        return code;
    }

    private void createGroupStatistics(Group group) {
        groupStatisticsRepository.save(GroupStatistics.builder()
                                                      .group(group)
                                                      .totalMemberCount(1)
                                                      .totalBungaeCount(0)
                                                      .build());
    }

    @Transactional(readOnly = true)
    public Group findGroupByInvitationCode(String invitationCode) {
        validateInvitationCode(invitationCode);
        return groupRepository.findByInvitationCode(invitationCode)
                              .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_INVITATION_CODE,
                                                                          invitationCode));
    }

    private void validateInvitationCode(String invitationCode) {
        Assertion.with(invitationCode)
                 .setValidation(code -> code.length() == CODE_LENGTH)
                 .validateOrThrow(() -> new ApplicationException(ApplicationExceptionType.INVALID_INVITATION_CODE, invitationCode));
    }

    public List<Group> findGroupsByMember(Member member) {
        return groupRepository.findGroupsWithStatisticsByMemberId(member.getId());
    }

    public int countGroupMembers(Long groupId) {
        Group group = findGroupById(groupId);
        GroupStatistics groupStatistics = groupStatisticsRepository.findById(group.getId())
                                                                   .orElseThrow(() -> new ApplicationException(
                                                                           ApplicationExceptionType.GROUP_STATISTICS_NOT_FOUND_BY_ID, group.getId()));
        return groupStatistics.getTotalMemberCount();
    }

    /**
     * 그룹 정보 수정
     * - 그룹 이름, 최대 인원 수, 프로필 이미지를 수정할 수 있다
     * - 그룹 오너만 수정 가능하며, 각 필드가 null이 아닌 경우에만 변경
     */
    @Transactional
    public Group updateGroup(Long groupId, Member member, GroupUpdateRequest request) {
        // 1. 그룹 조회
        Group group = groupRepository.findById(groupId)
                                     .orElseThrow(() -> new ApplicationException(
                                             ApplicationExceptionType.GROUP_NOT_FOUND_BY_ID,
                                             groupId
                                     ));

        // 2. 그룹 소유자인지 검증
        if (!group.isOwnedBy(member)) {
            throw new ApplicationException(
                    ApplicationExceptionType.GROUP_UPDATE_FORBIDDEN,
                    member.getId(), groupId
            );
        }

        // 3. 그룹 이름 변경
        if (request.name() != null) {
            group.updateName(request.name());
        }

        // 4. 최대 인원 수 변경
        if (request.maxMemberCount() != null) {
            group.updateMaxMemberCount(request.maxMemberCount());
        }

        // 5. 프로필 이미지 업로드 및 변경
        MultipartFile image = request.profileImage();
        if (image != null && !image.isEmpty()) {
            String imageUrl = s3ImageService.upload(image, S3Directory.GROUP_PROFILE_IMAGES);
            group.updateProfileImageUrl(imageUrl);
        }

        return group;
    }


}
