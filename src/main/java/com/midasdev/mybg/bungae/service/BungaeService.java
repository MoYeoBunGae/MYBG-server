package com.midasdev.mybg.bungae.service;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.controller.dto.response.BungaeDateVoteResponse;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.domain.BungaeAttendee;
import com.midasdev.mybg.bungae.domain.BungaeDateTime;
import com.midasdev.mybg.bungae.domain.BungaeDateVote;
import com.midasdev.mybg.bungae.domain.BungaeRecruitDateOption;
import com.midasdev.mybg.bungae.domain.BungaeStatus;
import com.midasdev.mybg.bungae.repository.BungaeAttendeeRepository;
import com.midasdev.mybg.bungae.repository.BungaeDateVoteRepository;
import com.midasdev.mybg.bungae.repository.BungaeRecruitDateOptionRepository;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.bungae.repository.dto.BungaeDateVoteInfoDto;
import com.midasdev.mybg.bungae.repository.dto.BungaeDto;
import com.midasdev.mybg.bungae.service.event.BungaeVoteCreatedEvent;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.lock.NamedLockManager;
import com.midasdev.mybg.global.util.cursor_page.CursorPage;
import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.service.GroupFinder;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.service.GroupMemberFinder;
import com.midasdev.mybg.member.domain.Member;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BungaeService {

    private final BungaeRepository bungaeRepository;
    private final BungaeRecruitDateOptionRepository bungaeRecruitDateOptionRepository;
    private final BungaeAttendeeRepository bungaeAttendeeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BungaeFinder bungaeFinder;
    private final GroupFinder groupFinder;
    private final GroupMemberFinder groupMemberFinder;
    private final BungaeDateVoteRepository bungaeDateVoteRepository;
    private final NamedLockManager namedLockManager;

    @Transactional
    public Bungae createBungae(Member member, BungaeCreateRequest request) {
        // 1. 그룹 존재 여부 검증
        Group group = groupFinder.findById(request.groupId());

        // 2. 로그인한 멤버가 해당 그룹에 속하는 GroupMember 조회
        GroupMember hostGroupMember = groupMemberFinder.findByMemberAndGroup(member, group);

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

        if (!request.hasSingleDateCandidate()) {
            // 5. 날짜 후보가 여러개라면 날짜 후보 저장 (BungaeRecruitDateOption)
            List<BungaeRecruitDateOption> savedDateOptions = new ArrayList<>();
            request.dateCandidates().forEach(date -> {
                BungaeRecruitDateOption option = BungaeRecruitDateOption.builder()
                                                                        .dateOption(date)
                                                                        .bungae(savedBungae)
                                                                        .build();
                savedDateOptions.add(bungaeRecruitDateOptionRepository.save(option));
            });

            // 6. 날짜 투표가 필요한 경우, 날짜 후보에 대해 번개 생성자의 투표를 저장
            List<BungaeDateVote> hostVotes = savedDateOptions.stream()
                    .map(dateOption -> BungaeDateVote.builder()
                            .voter(hostGroupMember)
                            .dateOption(dateOption)
                            .build())
                    .toList();
            bungaeDateVoteRepository.saveAll(hostVotes);

            eventPublisher.publishEvent(new BungaeVoteCreatedEvent(savedBungae.getId()));
            // TODO: 투표 생성 이벤트 처리 (필요한가? - 처리할 리스트 정리부터)
        } else {
            // 7. 날짜가 확정된 경우, Bungae 참여자에 host GroupMember 추가
            BungaeAttendee attendee = BungaeAttendee.builder()
                                                    .bungae(savedBungae)
                                                    .groupMember(hostGroupMember)
                                                    .deleted(false)
                                                    .build();
            bungaeAttendeeRepository.save(attendee);
        }

        // TODO: 알림 전송 (번개 생성 알림)

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
        Group group = groupFinder.findById(groupId);

        // 2. 멤버가 그룹에 속하는지 검증
        groupMemberFinder.findByMemberAndGroup(member, group);

        // 3. 해당 그룹에 속하고 statuses에 포함된 번개모임 조회
        return bungaeRepository.findByGroupIdAndStatusIn(groupId, statuses, pageable);

    }

    public List<LocalDate> getBungaeDateVoteOptions(Member member, Long bungaeId) {

        // 1. 번개 조회 - BungaeFinder로 위임
        Bungae bungae = bungaeFinder.findById(bungaeId);

        // 2. 번개 상태가 DATE_VOTING인지 검증
        if (!bungae.isVotableStatus()) {
            throw new ApplicationException(ApplicationExceptionType.BUNGAE_VOTE_UNAVAILABLE, bungaeId);
        }

        // 3. 멤버가 번개가 속한 그룹에 속하는지 검증 - GroupMemberFinder로 위임
        groupMemberFinder.findByMemberAndGroup(member, bungae.getGroup());

        // 4. 날짜 후보 조회
        List<BungaeRecruitDateOption> dateOptions = bungaeRecruitDateOptionRepository.findAllByBungae(bungae);

        return dateOptions.stream()
                          .map(BungaeRecruitDateOption::getDateOption)
                          .toList();
    }

    /**
     * 번개 날짜 투표 서비스 (다중 날짜)
     *
     * @param member   로그인 멤버
     * @param bungaeId 번개 ID
     * @param voteDates 투표 날짜 리스트
     * @return BungaeDateVoteResponse
     */
    @Transactional
    public BungaeDateVoteResponse voteBungaeDates(Member member, Long bungaeId, List<LocalDate> voteDates) {
        // 0. 동시성 제어: 번개 ID 기반 락 획득
        acquireLockByBungae(bungaeId);

        //  1. 번개 조회 및 검증
        Bungae bungae = bungaeFinder.findById(bungaeId);

        //  2. 그룹 멤버 검증
        GroupMember groupMember = groupMemberFinder.findByMemberAndGroup(member, bungae.getGroup());

        boolean isVotableStatus = bungae.isVotableStatus();
        List<LocalDate> failedVoteDates = new ArrayList<>();

        if (isVotableStatus) {
            // 3. 한 번에 모든 날짜에 대한 투표 정보 조회
            List<BungaeDateVoteInfoDto> voteInfoList = bungaeRecruitDateOptionRepository.findVoteInfoByDates(
                    bungaeId, voteDates, groupMember.getId());

            // 요청한 날짜와 실제 존재하는 날짜 옵션 비교
            Set<LocalDate> validDates = voteInfoList.stream()
                                                    .map(dto -> dto.dateOption().getDateOption())
                                                    .collect(Collectors.toSet());

            // 존재하지 않는 날짜는 실패 목록에 추가
            for (LocalDate voteDate : voteDates) {
                if (!validDates.contains(voteDate)) {
                    failedVoteDates.add(voteDate);
                }
            }

            // 4. 각 날짜별로 투표 처리 및 최소 인원 도달 확인
            BungaeRecruitDateOption tempConfirmedDateOption = null;
            List<BungaeDateVote> votesToSave = new ArrayList<>();

            for (BungaeDateVoteInfoDto voteInfoDto : voteInfoList) {
                LocalDate voteDate = voteInfoDto.dateOption().getDateOption();

                // 이미 투표한 날짜는 실패 목록에 추가하고 스킵
                if (Boolean.TRUE.equals(voteInfoDto.voted())) {
                    failedVoteDates.add(voteDate);
                    continue;
                }

                // 투표 생성
                BungaeDateVote dateVote = BungaeDateVote.builder()
                        .voter(groupMember)
                        .dateOption(voteInfoDto.dateOption())
                        .build();
                votesToSave.add(dateVote);

                // 최소 인원 달성 여부 확인
                if (bungae.isOneLeftToMinAttendees(voteInfoDto.voteCount())) {
                    // 여러 날짜가 동시에 최소 인원에 도달할 수 있는 경우, 가장 빠른 날짜 선택
                    if (tempConfirmedDateOption == null || voteDate.isBefore(tempConfirmedDateOption.getDateOption())) {
                        tempConfirmedDateOption = voteInfoDto.dateOption();
                    }
                }
            }

            // 모든 투표 저장
            if (!votesToSave.isEmpty()) {
                bungaeDateVoteRepository.saveAll(votesToSave);
            }

            // 날짜 확정 처리 (가장 빠른 날짜로)
            final BungaeRecruitDateOption confirmedDateOption = tempConfirmedDateOption;
            if (confirmedDateOption != null) {
                bungae.confirmDate(confirmedDateOption.getDateOption());

                // 해당 날짜에 투표한 인원들은 번개 참여자로 변경
                List<BungaeDateVote> voters = bungaeDateVoteRepository.findBungaeDateVotesByDateOption(confirmedDateOption);

                bungaeAttendeeRepository.saveAll(voters.stream()
                        .map(BungaeDateVote::getVoter)
                        .map(voter -> BungaeAttendee.builder()
                                .bungae(bungae)
                                .groupMember(voter)
                                .deleted(false)
                                .build())
                        .toList());

                // TODO: 채팅방 생성 및 푸쉬 알림 전송 (알림 데이터: 투표한 날짜, 정해진 날짜)
            }
        }

        // 5. 응답 생성 (현재 번개의 상태를 반영)
        return BungaeDateVoteResponse.builder()
                .wasVotableBungae(isVotableStatus)
                .isDateFixed(bungae.isDateFixed())
                .isJoinable(bungae.isDateFixed() ? bungae.canJoin() : null)
                .fixedDate(bungae.getBungaeDate())
                .bungaeStatus(bungae.getStatus())
                .failedVoteDates(isVotableStatus ? failedVoteDates : null)
                .build();
    }

    private void acquireLockByBungae(Long bungaeId) {
        String lockKey = "bungae:" + bungaeId;
        boolean lockAcquired = false;

        for (int i = 0; i < 3; i++) { // 최대 3회 재시도
            lockAcquired = namedLockManager.tryAcquire(lockKey, 1);
            if (lockAcquired) {
                break;
            }

            if (i < 2) { // 마지막 시도가 아니면 50ms 대기 후 재시도
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ApplicationException(ApplicationExceptionType.BUNGAE_VOTE_CONCURRENCY_LOCK_FAILED, bungaeId);
                }
            }
        }

        if (!lockAcquired) {
            throw new ApplicationException(ApplicationExceptionType.BUNGAE_VOTE_CONCURRENCY_LOCK_FAILED, bungaeId);
        }
    }

}


