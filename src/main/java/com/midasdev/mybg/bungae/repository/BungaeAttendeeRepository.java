package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BungaeAttendeeRepository extends JpaRepository<BungaeAttendee, Long> {

    List<BungaeAttendee> findByBungaeIdAndDeletedFalse(Long bungaeId);
}
