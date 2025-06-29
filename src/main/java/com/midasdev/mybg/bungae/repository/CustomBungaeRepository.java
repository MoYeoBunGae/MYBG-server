package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomBungaeRepository {
    Page<Bungae> findAllByAttendeeMemberIdAndStatusIn(Long memberId, List<BungaeStatus> statuses, Pageable pageable);
}
