package com.midasdev.mybg.group.controller;

import static com.midasdev.mybg.config.swagger.SwaggerConfig.SECURITY_SCHEME_NAME;

import com.midasdev.mybg.global.util.validator.IsPositiveNumber;
import com.midasdev.mybg.group.controller.dto.request.GroupCreateRequest;
import com.midasdev.mybg.group.controller.dto.response.GroupListResponse;
import com.midasdev.mybg.group.controller.dto.response.GroupMemberCountResponse;
import com.midasdev.mybg.group.controller.dto.response.GroupResponse;
import com.midasdev.mybg.group.domain.Group;
import com.midasdev.mybg.group.service.GroupService;
import com.midasdev.mybg.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Group APIs")
@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Validated
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "그룹 생성 API", description = "특정 사용자가 그룹을 생성합니다.", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@AuthenticationPrincipal Member member,
                                                     @Valid @RequestBody GroupCreateRequest groupCreateRequest) {
        Group savedGroup = groupService.createGroup(member, groupCreateRequest);
        return ResponseEntity.ok(GroupResponse.from(
                groupService.findGroupWithStatisticsById(savedGroup.getId())
        ));
    }

    @Operation(summary = "그룹 조회 By 초대코드", description = "초대 코드로 그룹을 조회합니다.", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @GetMapping("/search")
    public ResponseEntity<GroupResponse> findGroupByInvitationCode(@RequestParam String invitationCode) {
        Group group = groupService.findGroupByInvitationCode(invitationCode);
        return ResponseEntity.ok(GroupResponse.from(group));
    }

    @Operation(summary = "참여 중인 그룹 조회 API", description = "사용자가 참여 중인 그룹을 조회합니다.", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @GetMapping("/participating")
    public ResponseEntity<GroupListResponse> findGroupsByMemberId(@AuthenticationPrincipal Member member) {
        List<Group> groups = groupService.findGroupsByMember(member);
        return ResponseEntity.ok(GroupListResponse.from(groups));
    }

    @Operation(summary = "그룹 인원 수 조회 API", description = "그룹의 인원 수를 조회합니다.", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @GetMapping("/{groupId}/participants/count")
    public ResponseEntity<GroupMemberCountResponse> countGroupMembers(
            @Parameter(description = "그룹 ID", required = true)
            @PathVariable @IsPositiveNumber Long groupId) {
        int groupMemberCount = groupService.countGroupMembers(groupId);
        return ResponseEntity.ok(new GroupMemberCountResponse(groupId, groupMemberCount));
    }



}
