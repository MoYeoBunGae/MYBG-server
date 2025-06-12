package com.midasdev.mybg.bungae.service;

import com.midasdev.mybg.bungae.controller.dto.request.BungaeCreateRequest;
import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BungaeService {

    private final GroupMemberRepository groupMemberRepository;

    public Bungae createBungae(BungaeCreateRequest request) {
        // 1. groupId와 groupMemberId로 삭제되지 않은 groupMember 조회 (그룹에 속하는 그룹멤버가 없다면 예외)
        GroupMember hostGroupMember = groupMemberRepository.findByIdAndGroupIdAndDeletedFalse(request.hostGroupMemberId(), request.groupId())
                .orElseThrow(() -> new ApplicationException(
                        ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID,
                        request.hostGroupMemberId(), request.groupId()
                ));

        // 2. Bungae 엔티티 생성 - 날짜 후보에 따라 Status 설정

        // 3. Bungae 엔티티 저장

        // 4. 날짜 후보가 여러개라면 날짜 후보 저장 (BungaeRecruitDateOption)

        // 5. Bungae 참여자에 host GroupMember 추가

        // 6. 투표 생성 이벤트 발행

    }

}