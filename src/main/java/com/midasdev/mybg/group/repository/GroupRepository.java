package com.midasdev.mybg.group.repository;

import com.midasdev.mybg.group.domain.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long>, GroupRepositoryCustom {

    Optional<Group> findByIdAndDeletedIsFalse(Long id);

    Optional<Group> findByInvitationCode(String invitationCode);

    Boolean existsByInvitationCode(String invitationCode);

}
