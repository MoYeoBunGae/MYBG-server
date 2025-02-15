package com.midasdev.mybg.group.repository;

import com.midasdev.mybg.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupSpringDataRepository extends JpaRepository<Group, Long> {

}
