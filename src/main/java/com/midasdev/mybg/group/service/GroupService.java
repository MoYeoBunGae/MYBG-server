package com.midasdev.mybg.group.service;

import com.midasdev.mybg.global.application.DefaultProfileImageService;
import com.midasdev.mybg.global.application.DefaultProfileImageType;
import com.midasdev.mybg.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.repository.GroupSpringDataRepository;
import com.midasdev.mybg.group.service.component.InvitationCodeGenerator;
import com.midasdev.mybg.member.domain.Member;
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

    private final DefaultProfileImageService defaultProfileImageService;
    private final GroupSpringDataRepository groupSpringDataRepository;
    private final InvitationCodeGenerator invitationCodeGenerator;

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
                group.updateInvitationCode(invitationCodeGenerator.generateRandomCode());
                return groupSpringDataRepository.save(group);
            } catch (DataIntegrityViolationException e) {
                log.warn("Duplicated invitation code. Retry to generate invitation code.");
            }
        }
    }

}
