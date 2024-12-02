package com.midasdev.mochat.group.service;

import com.midasdev.mochat.global.application.DefaultProfileImageService;
import com.midasdev.mochat.global.application.DefaultProfileImageType;
import com.midasdev.mochat.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mochat.group.domain.Group;
import com.midasdev.mochat.group.repository.GroupSpringDataRepository;
import com.midasdev.mochat.member.domain.Member;
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
                group.updateInvitationCode();
                return groupSpringDataRepository.save(group);
            } catch (DataIntegrityViolationException e) {
                log.warn("Duplicated invitation code. Retry to generate invitation code.");
            }
        }
    }

}
