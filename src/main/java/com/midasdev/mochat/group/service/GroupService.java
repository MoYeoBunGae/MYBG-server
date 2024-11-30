package com.midasdev.mochat.group.service;

import com.midasdev.mochat.global.application.DefaultProfileImageType;
import com.midasdev.mochat.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mochat.group.domain.Group;
import com.midasdev.mochat.group.domain.InvitationCode;
import com.midasdev.mochat.group.repository.GroupSpringDataRepository;
import com.midasdev.mochat.member.domain.Member;
import com.midasdev.mochat.global.application.DefaultProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final DefaultProfileImageService defaultProfileImageService;
    private final GroupSpringDataRepository groupSpringDataRepository;

    public Group createGroup(Member member, GroupCreateRequest groupCreateRequest) {
        String profileImageUrl = StringUtils.hasText(groupCreateRequest.profileImageUrl())
                ? groupCreateRequest.profileImageUrl()
                : defaultProfileImageService.createRandomProfileImageUrl(DefaultProfileImageType.GROUP);
        Group group = Group.builder()
                           .owner(member)
                           .name(groupCreateRequest.name())
                           .profileImageUrl(profileImageUrl)
                           .invitationCode(new InvitationCode())
                           .deleted(false)
                           .build();

        return groupSpringDataRepository.save(group);
    }

}
