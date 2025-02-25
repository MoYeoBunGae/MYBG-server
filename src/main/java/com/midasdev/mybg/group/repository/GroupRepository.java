package com.midasdev.mybg.group.repository;

import com.midasdev.mybg.group.domain.Group;
import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Optional<Group> findById(Long groupId);

    List<Group> findGroupsByMemberId(Long memberId);
}
