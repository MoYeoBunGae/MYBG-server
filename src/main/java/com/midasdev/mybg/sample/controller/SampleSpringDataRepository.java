package com.midasdev.mybg.sample.controller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleSpringDataRepository extends JpaRepository<SampleEntity, Long> {
}
