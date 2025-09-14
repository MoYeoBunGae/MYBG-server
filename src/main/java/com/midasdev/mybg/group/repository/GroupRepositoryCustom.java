package com.midasdev.mybg.group.repository;

import com.midasdev.mybg.group.domain.Group;
import java.util.List;
import java.util.Optional;

public interface GroupRepositoryCustom {

    List<Group> findGroupsByMemberId(Long memberId);

    Optional<Group> findByInvitationCode(String invitationCode);


}
