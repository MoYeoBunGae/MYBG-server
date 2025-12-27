package com.midasdev.mybg.group.controller.dto.request;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.util.validator.FileMaxSize;
import com.midasdev.mybg.global.util.validator.NotBlankIfPresent;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Schema(
        description =
                """
                그룹 정보 수정 Request
                - 프로필 이미지는 jpg, jpeg, png, gif 형식만 허용되며, 최대 3MB까지 업로드할 수 있습니다.
                - 각 필드는 선택 사항이며, null인 경우 해당 항목은 변경되지 않습니다.
                        - 수정하지 않을 시 반드시 null 입력
                """)
public record GroupUpdateRequest(
        @Schema(
                        description = "변경할 그룹 이름 (최대 25자)",
                        example = "우리 가족방",
                        requiredMode = RequiredMode.NOT_REQUIRED)
                @NotBlankIfPresent
                @Size(max = 25, message = "그룹 이름은 최대 25자까지 가능합니다.")
                String name,
        @Schema(
                        description =
                                "변경할 그룹 프로필 이미지 (jpg, jpeg, png, gif), 변경하지 않을 시 null, 최대 3MB",
                        format = "binary",
                        requiredMode = RequiredMode.NOT_REQUIRED)
                @FileMaxSize(value = 3 * 1024 * 1024, message = "파일 크기가 3MB를 초과할 수 없습니다.")
                MultipartFile profileImage,
        @Schema(
                        description = "최대 인원 수 (최대 1000명)",
                        example = "10",
                        requiredMode = RequiredMode.NOT_REQUIRED)
                @Max(value = 1000, message = "최대 인원 수는 1000명을 초과할 수 없습니다.")
                Integer maxMemberCount) {

    /**
     * 최소 1개 필드가 null이 아닌지 검증합니다.
     *
     * @throws ApplicationException 최소 1개 필드가 null이 아닐 경우
     */
    public void validateAtLeastOneFieldPresent() {
        if (name == null && profileImage == null && maxMemberCount == null) {
            throw new ApplicationException(
                    ApplicationExceptionType.GLOBAL_NO_UPDATE_FIELD_PROVIDED);
        }
    }
}
