package com.midasdev.mybg.bungae.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.midasdev.mybg.bungae.domain.QBungaeDateVote;
import com.midasdev.mybg.bungae.domain.QBungaeRecruitDateOption;
import com.midasdev.mybg.bungae.repository.dto.BungaeDateVoteInfoDto;
import com.midasdev.mybg.bungae.repository.dto.QBungaeDateVoteInfoDto;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class CustomBungaeRecruitDateOptionRepositoryImpl implements CustomBungaeRecruitDateOptionRepository {

    private final JPAQueryFactory queryFactory;

    public CustomBungaeRecruitDateOptionRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<BungaeDateVoteInfoDto> findVoteInfoByDates(Long bungaeId, List<LocalDate> dateOptions, Long groupMemberId) {
        QBungaeRecruitDateOption bungaeRecruitDateOption = QBungaeRecruitDateOption.bungaeRecruitDateOption;
        QBungaeDateVote bungaeDateVote = QBungaeDateVote.bungaeDateVote;

        // 총 득표수 서브쿼리
        SubQueryExpression<Integer> totalVotesSubquery = JPAExpressions
                .select(bungaeDateVote.count().intValue())
                .from(bungaeDateVote)
                .where(
                        bungaeDateVote.dateOption.id.eq(bungaeRecruitDateOption.id)
                );

        BooleanExpression userVotedCondition = JPAExpressions
                .selectOne()
                .from(bungaeDateVote)
                .where(
                        bungaeDateVote.dateOption.id.eq(bungaeRecruitDateOption.id),
                        bungaeDateVote.voter.id.eq(groupMemberId)
                )
                .exists();

        return queryFactory
                .select(new QBungaeDateVoteInfoDto(
                        bungaeRecruitDateOption,
                        totalVotesSubquery,
                        userVotedCondition
                ))
                .from(bungaeRecruitDateOption)
                .where(
                        bungaeRecruitDateOption.bungae.id.eq(bungaeId),
                        bungaeRecruitDateOption.dateOption.in(dateOptions)
                )
                .fetch();
    }
}