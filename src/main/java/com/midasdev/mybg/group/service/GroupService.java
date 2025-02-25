package com.midasdev.mybg.group.service;

import com.midasdev.mybg.global.application.DefaultProfileImageService;
import com.midasdev.mybg.global.application.DefaultProfileImageType;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.assertion.Assertion;
import com.midasdev.mybg.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group.repository.GroupSpringDataRepository;
import com.midasdev.mybg.group.service.component.InvitationCodeGenerator;
import com.midasdev.mybg.group_member.repository.GroupMemberSpringDataRepository;
import com.midasdev.mybg.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private static final int CODE_LENGTH = 8;

    private final DefaultProfileImageService defaultProfileImageService;
    private final GroupSpringDataRepository groupSpringDataRepository;
    private final GroupRepository groupRepository;
    private final InvitationCodeGenerator invitationCodeGenerator;
    private final GroupMemberSpringDataRepository groupMemberSpringDataRepository;

    private Group findGroupById(Long groupId) {
        return groupSpringDataRepository.findByIdAndDeletedIsFalse(groupId)
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
                           .deleted(false)
                           .build();

        while(true) {
            try {
                group.updateInvitationCode(invitationCodeGenerator.generateRandomCode(CODE_LENGTH));
                return groupSpringDataRepository.save(group);
            } catch (DataIntegrityViolationException e) {
                log.warn("Duplicated invitation code. Retry to generate invitation code.");
            }
        }
    }

    @Transactional(readOnly = true)
    public Group findGroupByInvitationCode(String invitationCode) {
        validateInvitationCode(invitationCode);
        return groupSpringDataRepository.findByInvitationCode(invitationCode)
                                        .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_INVITATION_CODE, invitationCode));
    }

    private void validateInvitationCode(String invitationCode) {
        Assertion.with(invitationCode)
                 .setValidation(code -> code.length() == CODE_LENGTH)
                 .validateOrThrow(() -> new ApplicationException(ApplicationExceptionType.INVALID_INVITATION_CODE, invitationCode));
    }

    public List<Group> findGroupsByMember(Member member) {
        return groupRepository.findGroupsByMemberId(member.getId());
    }

    public int countGroupMembers(Long groupId) {
        Group group = findGroupById(groupId);

        // TODO: 그룹 통계 테이블 활용하도록 수정
        return groupMemberSpringDataRepository.countByGroup(group);
    }

}
