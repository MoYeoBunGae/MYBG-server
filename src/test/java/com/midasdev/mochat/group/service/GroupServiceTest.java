package com.midasdev.mochat.group.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.midasdev.mochat.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mochat.group.domain.Group;
import com.midasdev.mochat.group.repository.GroupSpringDataRepository;
import com.midasdev.mochat.group.service.component.InvitationCodeGenerator;
import com.midasdev.mochat.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupSpringDataRepository groupSpringDataRepository;
    @Mock
    private InvitationCodeGenerator invitationCodeGenerator;

    @InjectMocks
    private GroupService groupService;

    @Test
    @DisplayName("중복된 초대 코드를 가진 그룹 저장시 재시도")
    public void 중복된_초대_코드를_가진_그룹_저장시_재시도() {
        // given
        String duplicateCode = "AAAAAAAA";
        String uniqueCode = "BBBBBBBB";
        String groupName = "name";
        String profileImageUrl = "profileImageUrl";

        when(invitationCodeGenerator.generateRandomCode())
                .thenReturn(duplicateCode)
                .thenReturn(uniqueCode);

        Member member = Member.builder()
                              .id(1L)
                              .name(groupName)
                              .profileImageUrl(profileImageUrl)
                              .deleted(false)
                              .build();

        Group expectedgroup = Group.builder()
                                   .name(groupName)
                                   .invitationCode(uniqueCode)
                                   .owner(member)
                                   .build();

        doThrow(new DataIntegrityViolationException("Duplicated code"))
                .when(groupSpringDataRepository).save(argThat(group -> group.getInvitationCode().equals(duplicateCode)));
        doReturn(expectedgroup)
                .when(groupSpringDataRepository).save(argThat(group -> group.getInvitationCode().equals(uniqueCode)));

        GroupCreateRequest groupCreateRequest = new GroupCreateRequest("group", profileImageUrl);

        // when
        Group createdGroup = groupService.createGroup(member, groupCreateRequest);

        // then
        assertThat(createdGroup).isNotNull();
        assertThat(createdGroup.getInvitationCode()).isEqualTo(uniqueCode);

        verify(invitationCodeGenerator, times(2)).generateRandomCode();
        verify(groupSpringDataRepository, times(2)).save(any(Group.class));

    }
}