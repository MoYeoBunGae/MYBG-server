package com.midasdev.mochat.group.repository;

import com.midasdev.mochat.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupSpringDataRepository extends JpaRepository<Group, Long> {

}
