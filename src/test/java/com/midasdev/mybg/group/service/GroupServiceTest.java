package com.midasdev.mybg.group.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group.service.component.InvitationCodeGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    private static final int CODE_LENGTH = 8;

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private InvitationCodeGenerator invitationCodeGenerator;

    @InjectMocks
    private GroupService groupService;

    // TODO: 테스트 수정 필요
//    @Test
//    @DisplayName("중복된 초대 코드를 가진 그룹 저장시 재시도")
//    public void 중복된_초대_코드를_가진_그룹_저장시_재시도() {
//        // given
//        String duplicateCode = "AAAAAAAA";
//        String uniqueCode = "BBBBBBBB";
//        String groupName = "name";
//        String profileImageUrl = "profileImageUrl";
//
//        when(invitationCodeGenerator.generateRandomCode(CODE_LENGTH))
//                .thenReturn(duplicateCode)
//                .thenReturn(uniqueCode);
//
//        Member member = Member.builder()
//                              .id(1L)
//                              .name(groupName)
//                              .profileImageUrl(profileImageUrl)
//                              .deleted(false)
//                              .build();
//
//        Group expectedgroup = Group.builder()
//                                   .name(groupName)
//                                   .invitationCode(uniqueCode)
//                                   .owner(member)
//                                   .build();
//
//        doThrow(new DataIntegrityViolationException("Duplicated code"))
//                .when(groupSpringDataRepository).save(argThat(group -> group.getInvitationCode().equals(duplicateCode)));
//        doReturn(expectedgroup)
//                .when(groupSpringDataRepository).save(argThat(group -> group.getInvitationCode().equals(uniqueCode)));
//
//        GroupCreateRequest groupCreateRequest = new GroupCreateRequest("group", profileImageUrl);
//
//        // when
//        Group createdGroup = groupService.createGroup(member, groupCreateRequest);
//
//        // then
//        assertThat(createdGroup).isNotNull();
//        assertThat(createdGroup.getInvitationCode()).isEqualTo(uniqueCode);
//
//        verify(invitationCodeGenerator, times(2)).generateRandomCode(CODE_LENGTH);
//        verify(groupSpringDataRepository, times(2)).save(any(Group.class));
//
//    }
}