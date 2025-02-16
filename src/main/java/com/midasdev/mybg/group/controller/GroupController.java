package com.midasdev.mybg.group.controller;

import com.midasdev.mybg.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mybg.group.controller.dto.response.GroupResponse;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.service.GroupService;
import com.midasdev.mybg.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Group APIs")
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
@Validated
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "그룹 생성 API", description = "특정 사용자가 그룹을 생성합니다.", security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@AuthenticationPrincipal Member member,
                                                     @Valid @RequestBody GroupCreateRequest groupCreateRequest) {
        Group group = groupService.createGroup(member, groupCreateRequest);
        return ResponseEntity.ok(GroupResponse.from(group));
    }

    @Operation(summary = "그룹 조회 By 초대코드", description = "초대 코드로 그룹을 조회합니다.", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/search")
    public ResponseEntity<GroupResponse> findGroupByInvitationCode(@RequestParam String invitationCode) {
        Group group = groupService.findGroupByInvitationCode(invitationCode);
        return ResponseEntity.ok(GroupResponse.from(group));
    }

}
