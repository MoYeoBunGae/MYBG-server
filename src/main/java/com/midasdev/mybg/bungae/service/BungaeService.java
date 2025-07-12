package com.midasdev.mybg.bungae.service;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeDateTime;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.bungae.service.event.BungaeVoteCreatedEvent;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BungaeService {

    private final GroupMemberRepository groupMemberRepository;
    private final BungaeRepository bungaeRepository;
    private final BungaeRecruitDateOptionRepository bungaeRecruitDateOptionRepository;
    private final BungaeAttendeeRepository bungaeAttendeeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Bungae createBungae(BungaeCreateRequest request) {
        // 1. groupId와 groupMemberId로 삭제되지 않은 groupMember 조회 (그룹에 속하는 그룹멤버가 없다면 예외)
        GroupMember hostGroupMember = groupMemberRepository.findByIdAndGroupIdAndDeletedFalse(request.hostGroupMemberId(), request.groupId())
                                                           .orElseThrow(() -> new ApplicationException(
                                                                   ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID,
                                                                   request.hostGroupMemberId(), request.groupId()
                                                           ));

        // 2. Bungae 엔티티 생성 - 날짜 후보에 따라 Status 및 bungaeDateTime 설정
        BungaeStatus status = request.hasSingleDateCandidate()
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
                                                                        .voteCount(1) // 주최자가 1표를 가진 것으로 초기화
                                                                        .bungae(savedBungae)
                                                                        .build();
                bungaeRecruitDateOptionRepository.save(option);
            });
        }

        // 5. Bungae 참여자에 host GroupMember 추가
        BungaeAttendee attendee = BungaeAttendee.builder()
                                                .bungae(savedBungae)
                                                .groupMember(hostGroupMember)
                                                .deleted(false)
                                                .build();
        bungaeAttendeeRepository.save(attendee);

        // 6. 투표 생성 이벤트 발행
        eventPublisher.publishEvent(new BungaeVoteCreatedEvent(savedBungae.getId()));

        // 필요하다면 반환
        return savedBungae;
    }

    public CursorPage<Bungae> findBungaesByMemberIdAndStatuses(
            Member member,
            List<BungaeStatus> statuses,
            CursorPageable cursorPageable
    ) {
        return bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(member.getId(), statuses, cursorPageable);
    }

}