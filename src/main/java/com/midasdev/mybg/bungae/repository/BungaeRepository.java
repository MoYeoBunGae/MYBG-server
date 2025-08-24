package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.Bungae;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BungaeRepository extends JpaRepository<Bungae, Long>, CustomBungaeRepository {
    Optional<Bungae> findByIdAndDeletedIsFalse(Long id);

}
