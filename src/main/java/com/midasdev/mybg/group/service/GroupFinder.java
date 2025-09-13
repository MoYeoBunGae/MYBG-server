package com.midasdev.mybg.group.service;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupFinder {

    private final GroupRepository groupRepository;

    public Group findGroupById(Long groupId) {
        return groupRepository.findByIdAndDeletedIsFalse(groupId)
                              .orElseThrow(() -> new ApplicationException(ApplicationExceptionType.GROUP_NOT_FOUND_BY_ID, groupId));
    }

}
