package com.midasdev.mybg.group_member.service;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.domain.GroupStatistics;
import com.midasdev.mybg.group.repository.GroupRepository;
import com.midasdev.mybg.group.repository.GroupStatisticsRepository;
import com.midasdev.mybg.group_member.controller.dto.request.GroupJoinRequest;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberSpringDataRepository;
import com.midasdev.mybg.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupRepository groupRepository;
    private final GroupMemberSpringDataRepository groupMemberSpringDataRepository;
    private final GroupStatisticsRepository groupStatisticsRepository;

    @Transactional
    public GroupMember joinGroup(Member member, GroupJoinRequest groupJoinRequest) {
        // group 검증
        Long requestedGroupId = groupJoinRequest.groupId();
        Group group = groupRepository.findWithStatisticsById(requestedGroupId)
                                     .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_ID,
                                                                                 requestedGroupId));
        // group에 가입 가능한지 검증
        if (group.isFull()) {
            throw new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_CAPACITY_REACHED, group.getId());
        }

        // owner 인지 검증
        if (isAlreadyGroupMember(member, group)) {
            throw new ApplicationException(ApplicationExceptionType.ALREADY_JOINED_GROUP, member.getId(), group.getId());
        }

        // nickname 이 null이면 member의 name을 사용
        String nickname = groupJoinRequest.nickname() == null ? member.getName() : groupJoinRequest.nickname();

        GroupMember groupMember = GroupMember.builder()
                                             .nickname(nickname)
                                             .member(member)
                                             .group(group)
                                             .build();
        return saveGroupMember(groupMember, group);
    }

    private GroupMember saveGroupMember(GroupMember groupMember, Group group) {
        GroupMember savedGroupMember = groupMemberSpringDataRepository.save(groupMember);
        GroupStatistics groupStatistics = groupStatisticsRepository.findById(group.getId()).orElseThrow(
                () -> new ApplicationException(ApplicationExceptionType.GROUP_STATISTICS_NOT_FOUND_BY_ID, group.getId()));

        groupStatistics.increaseTotalMemberCount();
        return savedGroupMember;
    }


    private boolean isAlreadyGroupMember(Member member, Group group) {
        return group.isOwner(member) || groupMemberSpringDataRepository.findByMemberAndGroup(member, group).isPresent();
    }

}
