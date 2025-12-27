package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.group_member.domain.GroupMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BungaeAttendeeRepository extends JpaRepository<BungaeAttendee, Long> {

    List<BungaeAttendee> findByBungaeIdAndDeletedFalse(Long bungaeId);

    Optional<BungaeAttendee> findByBungaeAndGroupMemberAndDeletedFalse(
            Bungae bungae, GroupMember groupMember);
}
