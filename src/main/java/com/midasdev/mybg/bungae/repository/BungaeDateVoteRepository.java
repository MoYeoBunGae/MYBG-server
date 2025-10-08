package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.BungaeDateVote;
import com.midasdev.mybg.bungae.domain.BungaeDateVoteId;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BungaeDateVoteRepository extends JpaRepository<BungaeDateVote, BungaeDateVoteId> {

    List<BungaeDateVote> findBungaeDateVotesByDateOption(BungaeRecruitDateOption dateOption);

}
