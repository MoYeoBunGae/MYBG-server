package com.midasdev.mybg.group_member.controller;

import static com.midasdev.mybg.config.swagger.SwaggerConfig.SECURITY_SCHEME_NAME;

import com.midasdev.mybg.group_member.controller.dto.request.GroupJoinRequest;
import com.midasdev.mybg.group_member.controller.dto.request.GroupMemberProfileUpdateRequest;
import com.midasdev.mybg.group_member.controller.dto.response.ActiveGroupMemberResponse;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActiveGroupMemberResponse> updateGroupMemberProfile(
            @AuthenticationPrincipal Member member,
            @Valid @ModelAttribute GroupMemberProfileUpdateRequest request
    ) {
        GroupMember updatedMember = groupMemberService.updateProfile(member, request);
        return ResponseEntity.ok(ActiveGroupMemberResponse.from(updatedMember));
    }

}
