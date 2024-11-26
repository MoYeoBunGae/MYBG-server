package com.midasdev.mochat.group.service;

import com.midasdev.mochat.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mochat.group.domain.Group;
import com.midasdev.mochat.group.domain.InvitationCode;
import com.midasdev.mochat.group.repository.GroupSpringDataRepository;
import com.midasdev.mochat.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupSpringDataRepository groupSpringDataRepository;

    public Group createGroup(Member member, GroupCreateRequest groupCreateRequest) {
        InvitationCode invitationCode = InvitationCode.createRandomCode();
        Group group = Group.builder()
                .owner(member)
                .name(groupCreateRequest.name())
                .profileImageUrl(groupCreateRequest.profileImageUrl())
                .invitationCode(invitationCode)
                .deleted(false)
                .build();

        return groupSpringDataRepository.save(group);
    }

}
