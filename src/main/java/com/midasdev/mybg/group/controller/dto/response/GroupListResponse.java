package com.midasdev.mybg.group.controller.dto.response;

import com.midasdev.mybg.group.domain.Group;
import java.util.List;
import java.util.stream.Collectors;

public record GroupListResponse(List<GroupResponse> groups) {

    public static GroupListResponse from(List<Group> groups) {
        return new GroupListResponse(groups.stream()
                                           .map(GroupResponse::from)
                                           .collect(Collectors.toList()));
    }

}
