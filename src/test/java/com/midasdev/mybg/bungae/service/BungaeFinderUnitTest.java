package com.midasdev.mybg.bungae.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.midasdev.mybg.TestConstant;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.fixture.BungaeFixture;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.fixture.GroupFixture;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.fixture.GroupMemberFixture;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BungaeFinderUnitTest {

    @Mock
    private BungaeRepository bungaeRepository;

    @InjectMocks
    private BungaeFinder bungaeFinder;

    @Test
    @DisplayName("BF-1-SU-1: 존재하는 번개 ID로 조회 시 번개 반환")
    void BF_1_SU_1() {
        // given
        Long bungaeId = 1L;
        Member member = MemberFixture.create();
        Group group = GroupFixture.create(member);
        GroupMember host = GroupMemberFixture.create(group, member);
        Bungae expectedBungae = BungaeFixture.createWithRecruiting(group, host);
        given(bungaeRepository.findByIdAndDeletedIsFalse(bungaeId))
                .willReturn(Optional.of(expectedBungae));

        // when
        Bungae actualBungae = bungaeFinder.findById(bungaeId);

        // then
        assertThat(actualBungae).isEqualTo(expectedBungae);
    }

    @Test
    @DisplayName("BF-1-SU-2: 존재하지 않는 번개 ID로 조회 시 예외 발생")
    void BF_1_SU_2() {
        // given
        Long nonExistentBungaeId = 999L;
        given(bungaeRepository.findByIdAndDeletedIsFalse(nonExistentBungaeId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bungaeFinder.findById(nonExistentBungaeId))
                .isInstanceOf(ApplicationException.class)
                .hasFieldOrPropertyWithValue(TestConstant.EXCEPTION_TYPE_FIELD, ApplicationExceptionType.BUNGAE_NOT_FOUND_BY_ID);
    }


}

