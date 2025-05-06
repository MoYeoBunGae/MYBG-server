package com.midasdev.mybg.group.repository;

import com.midasdev.mybg.group.domain.Group;
import java.util.List;
import java.util.Optional;

public interface GroupRepositoryCustom {

    Optional<Group> findWithStatisticsById(Long groupId);

    List<Group> findGroupsWithStatisticsByMemberId(Long memberId);

    Optional<Group> findByInvitationCode(String invitationCode);
}
