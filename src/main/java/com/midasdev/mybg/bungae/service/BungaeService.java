package com.midasdev.mybg.bungae.service;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeDateTime;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.bungae.service.event.BungaeVoteCreatedEvent;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.member.domain.Member;
import java.time.LocalDate;
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
    private final GroupRepository groupRepository;

    @Transactional
    public Bungae createBungae(Member member, BungaeCreateRequest request) {
        // 1. 그룹 존재 여부 검증
        Group group = groupRepository.findByIdAndDeletedIsFalse(request.groupId())
                                    .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_ID, request.groupId()));

        // 2. 로그인한 멤버가 해당 그룹에 속하는 GroupMember 조회
        GroupMember hostGroupMember = groupMemberRepository.findByMemberAndGroup(member, group)
                                                           .orElseThrow(() -> new ApplicationException(
                                                                   ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID,
                                                                   member.getId(), request.groupId()
                                                           ));

        // 3. Bungae 엔티티 생성 - 날짜 후보에 따라 Status 및 bungaeDateTime 설정
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
                              .group(group)
                              .host(hostGroupMember)
                              .build();

        // 4. Bungae 엔티티 저장
        Bungae savedBungae = bungaeRepository.save(bungae);

        // 5. 날짜 후보가 여러개라면 날짜 후보 저장 (BungaeRecruitDateOption)
        if (!request.hasSingleDateCandidate()) {
            request.dateCandidates().forEach(date -> {
                BungaeRecruitDateOption option = BungaeRecruitDateOption.builder()
                                                                        .dateOption(date)
                                                                        .bungae(savedBungae)
                                                                        .build();
                bungaeRecruitDateOptionRepository.save(option);
            });
        }

        // 6. Bungae 참여자에 host GroupMember 추가
        BungaeAttendee attendee = BungaeAttendee.builder()
                                                .bungae(savedBungae)
                                                .groupMember(hostGroupMember)
                                                .deleted(false)
                                                .build();
        bungaeAttendeeRepository.save(attendee);

        eventPublisher.publishEvent(new BungaeVoteCreatedEvent(savedBungae.getId()));

        // 필요하다면 반환
        return savedBungae;
    }

    public CursorPage<BungaeDto> findBungaesByMemberIdAndStatuses(
            Member member,
            List<BungaeStatus> statuses,
            CursorPageable cursorPageable
    ) {
        return bungaeRepository.findAllByAttendeeMemberIdAndStatusIn(member.getId(), statuses, cursorPageable);
    }

    public CursorPage<BungaeDto> findBungaesByGroupIdAndStatuses(Member member, Long groupId, List<BungaeStatus> statuses, CursorPageable pageable) {
        // 1. 그룹 존재 여부 검증
        Group group = groupRepository.findByIdAndDeletedIsFalse(groupId)
                                    .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_ID, groupId));

        // 2. 멤버가 그룹에 속하는지 검증
        groupMemberRepository.findByMemberAndGroup(member, group)
                             .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID, member.getId(), groupId));

        // 3. 해당 그룹에 속하고 statuses에 포함된 번개모임 조회
        return bungaeRepository.findByGroupIdAndStatusIn(groupId, statuses, pageable);

    }

    public List<LocalDate> getBungaeDateVoteOptions(Member member, Long bungaeId) {
        // 1. 번개 조회
        Bungae bungae = bungaeRepository.findByIdAndDeletedIsFalse(bungaeId)
                .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.BUNGAE_NOT_FOUND_BY_ID, bungaeId));

        // 2. 번개 상태가 DATE_VOTING인지 검증
        if (!bungae.canVote()) {
            throw new ApplicationException(ApplicationExceptionType.BUNGAE_VOTE_UNAVAILABLE, bungaeId);
        }

        // 3. 멤버가 번개가 속한 그룹에 속하는지 검증
        groupMemberRepository.findByMemberAndGroup(member, bungae.getGroup())
                .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID, member.getId(), bungae.getGroup().getId()));

        // 4. 날짜 후보 조회
        List<BungaeRecruitDateOption> dateOptions = bungaeRecruitDateOptionRepository.findAllByBungae(bungae);

        return dateOptions.stream()
                .map(BungaeRecruitDateOption::getDateOption)
                .toList();
    }

}