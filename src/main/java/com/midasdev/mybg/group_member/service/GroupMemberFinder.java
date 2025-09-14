package com.midasdev.mybg.group_member.service;

import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.repository.GroupMemberRepository;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupMemberFinder {

    private final GroupMemberRepository groupMemberRepository;

    /**
     * 멤버가 특정 그룹에 속하는 GroupMember를 조회하고 권한을 검증합니다.
     * @param member 검증할 멤버
     * @param group 검증할 그룹
     * @return 조회된 GroupMember
     * @throws ApplicationException 멤버가 그룹에 속하지 않는 경우
     */
    public GroupMember findByMemberAndGroup(Member member, Group group) {
        return groupMemberRepository.findByMemberAndGroup(member, group)
                .orElseThrow(() -> new ApplicationException(
                        ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_GROUP_ID,
                        member.getId(), group.getId()
                ));
    }

    private GroupMember findById(Long groupMemberId) {
        return groupMemberRepository.findEntityById(groupMemberId)
                                    .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_NOT_FOUND_BY_ID, groupMemberId));
    }

    public GroupMember findByIdAndMember(Long groupMemberId, Member member) {
        GroupMember groupMember = findById(groupMemberId);
        if (!groupMember.belongsTo(member.getId())) {
            throw new ApplicationException(ApplicationExceptionType.GROUP_MEMBER_DOES_NOT_BELONG_TO_MEMBER, groupMemberId, member.getId());
        }
        return groupMember;
    }
}

