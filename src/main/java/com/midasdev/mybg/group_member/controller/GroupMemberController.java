package com.midasdev.mybg.group_member.controller;

import static com.midasdev.mybg.config.swagger.SwaggerConfig.SECURITY_SCHEME_NAME;

import com.midasdev.mybg.global.response.MessageResponse;
import com.midasdev.mybg.group_member.controller.dto.request.GroupJoinRequest;
import com.midasdev.mybg.group_member.controller.dto.request.GroupMemberProfileUpdateRequest;
import com.midasdev.mybg.group_member.controller.dto.response.ActiveGroupMemberResponse;
import com.midasdev.mybg.group_member.controller.dto.response.TOBE_ActiveGroupMemberResponse;
import com.midasdev.mybg.group_member.domain.GroupMember;
import com.midasdev.mybg.group_member.service.GroupMemberService;
import com.midasdev.mybg.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Group Member APIs")
@RestController
@RequestMapping("/api/v1/group-member")
@RequiredArgsConstructor
@Validated
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    @Operation(summary = "그룹 참여 API", security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @PostMapping
    public ResponseEntity<ActiveGroupMemberResponse> joinGroup(@AuthenticationPrincipal Member member,
                                                               @Valid @RequestBody GroupJoinRequest groupJoinRequest) {
        GroupMember groupMember = groupMemberService.joinGroup(member, groupJoinRequest);
        return ResponseEntity.ok(ActiveGroupMemberResponse.from(groupMember));
    }

    @Operation(
            summary = "그룹 멤버 프로필 수정 API",
            description = "그룹에 참여 중인 사용자의 닉네임과 프로필 이미지를 수정합니다.",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    @PatchMapping(value = "/{groupMemberId}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TOBE_ActiveGroupMemberResponse> updateGroupMemberProfile(
            @PathVariable Long groupMemberId,
            @AuthenticationPrincipal Member member,
            @Valid @ModelAttribute GroupMemberProfileUpdateRequest request
    ) {
        GroupMember updatedMember = groupMemberService.updateProfile(groupMemberId, member, request);
        return ResponseEntity.ok(TOBE_ActiveGroupMemberResponse.from(updatedMember));
    }

    @Operation(
            summary = "그룹 나가기 API",
            description = """
                사용자가 참여한 그룹에서 나갑니다.
                - 세부사항:
                    1. 그룹의 소유자는 나갈 수 없습니다.
                """,
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    @DeleteMapping("/{groupMemberId}")
    public ResponseEntity<MessageResponse> leaveGroup(
            @PathVariable Long groupMemberId,
            @AuthenticationPrincipal Member loginMember
    ) {
        groupMemberService.leaveGroup(groupMemberId, loginMember);
        return ResponseEntity.ok(new MessageResponse("Leave group succeeded"));

    }

}
