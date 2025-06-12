package com.midasdev.mybg.bungae.service;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeDateTime;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BungaeService {

    private final GroupMemberRepository groupMemberRepository;
    private final BungaeRepository bungaeRepository;
    private final BungaeRecruitDateOptionRepository bungaeRecruitDateOptionRepository;

    @Transactional
    public Bungae createBungae(BungaeCreateRequest request) {
        // 1. groupId와 groupMemberId로 삭제되지 않은 groupMember 조회 (그룹에 속하는 그룹멤버가 없다면 예외)
        GroupMember hostGroupMember = groupMemberRepository.findByIdAndGroupIdAndDeletedFalse(request.hostGroupMemberId(), request.groupId())
                .orElseThrow(() -> new ApplicationException(
                        ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID,
                        request.hostGroupMemberId(), request.groupId()
                ));

        // 2. Bungae 엔티티 생성 - 날짜 후보에 따라 Status 및 bungaeDateTime 설정
        BungaeStatus status = request.dateCandidates().size() == 1
                ? BungaeStatus.RECRUITING
                : BungaeStatus.DATE_VOTING;

        BungaeDateTime bungaeDateTime;
        if (request.hasSingleDateCandidate()) {
            // 날짜 후보가 1개면 날짜+시간 모두 포함
            bungaeDateTime = new BungaeDateTime(
                request.dateCandidates().get(0),
                request.bungaeTime()
            );
        } else {
            // 여러개면 시간만 포함
            bungaeDateTime = new BungaeDateTime(request.bungaeTime());
        }

        Bungae bungae = Bungae.builder()
                .name(request.name())
                .description(request.description())
                .minAttendees(request.minAttendees())
                .maxAttendees(request.maxAttendees())
                .isOnline(request.isOnline())
                .location(request.location())
                .bungaeDateTime(bungaeDateTime)
                .dateVoteClosedAt(request.dateVoteClosedAt())
                .status(status)
                .deleted(false)
                .group(hostGroupMember.getGroup())
                .host(hostGroupMember)
                .build();

        // 3. Bungae 엔티티 저장
        Bungae savedBungae = bungaeRepository.save(bungae);

        // 4. 날짜 후보가 여러개라면 날짜 후보 저장 (BungaeRecruitDateOption)
        if (!request.hasSingleDateCandidate()) {
            request.dateCandidates().forEach(date -> {
                BungaeRecruitDateOption option = BungaeRecruitDateOption.builder()
                        .dateOption(date)
                        .voteCount(0)
                        .bungae(savedBungae)
                        .build();
                bungaeRecruitDateOptionRepository.save(option);
            });
        }

        // 5. Bungae 참여자에 host GroupMember 추가

        // 6. 투표 생성 이벤트 발행

    }

}