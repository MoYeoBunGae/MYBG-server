package com.midasdev.mybg.bungae.repository;

import com.midasdev.mybg.bungae.domain.BungaeDateVote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 테스트 전용 리포지토리
 * 테스트에서 데이터 검증을 위한 쿼리 메서드를 제공합니다.
 */
public interface BungaeDateVoteTestRepository extends JpaRepository<BungaeDateVote, Long> {

    @Query("SELECT bdv FROM BungaeDateVote bdv " +
           "JOIN FETCH bdv.dateOption do " +
           "WHERE do.bungae.id = :bungaeId AND bdv.voter.id = :voterId")
    List<BungaeDateVote> findByBungaeIdAndVoterId(@Param("bungaeId") Long bungaeId, @Param("voterId") Long voterId);

}

