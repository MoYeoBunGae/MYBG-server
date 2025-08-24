package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BungaeRecruitDateOptionRepository extends JpaRepository<BungaeRecruitDateOption, Long> {

    List<BungaeRecruitDateOption> findAllByBungae(Bungae bungae);

}
