package com.midasdev.mybg.bungae.controller.dto.request;


import com.midasdev.mybg.global.util.validator.IsPositiveNumber;
import com.midasdev.mybg.global.util.validator.WithinWeeks;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "번개 생성 요청")
public record BungaeCreateRequest(

        @Schema(
                title = "번개 이름",
                description = "최대 30자까지 입력 가능합니다.",
                requiredMode = RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(max = 30)
        String name,

        @Schema(
                title = "번개 설명",
                description = "최대 1000자까지 입력 가능합니다.",
                requiredMode = RequiredMode.REQUIRED
        )
        @Size(max = 1000)
        String description,

        @Schema(
                title = "최소 인원 수",
                description = "2 이상의 값을 입력해야 합니다.",
                requiredMode = RequiredMode.REQUIRED
        )
        @NotNull
        @Min(2)
        Integer minAttendees,

        @Schema(
                title = "최대 인원 수",
                description = "2 이상의 값을 입력해야 하며, 최소 인원 수 이상이어야 합니다.",
                requiredMode = RequiredMode.REQUIRED
        )
        @NotNull
        @Min(2) @Max(1000)
        Integer maxAttendees,

        @Schema(
                title = "온라인 여부",
                requiredMode = RequiredMode.REQUIRED
        )
        @NotNull
        Boolean isOnline,

        @Schema(
                title = "오프라인 장소",
                description = "최대 100자까지 입력 가능합니다.",
                requiredMode = RequiredMode.NOT_REQUIRED
        )
        @Size(max = 100)
        String location,

        @Schema(
                title = "번개 시간",
                requiredMode = RequiredMode.NOT_REQUIRED
        )
        LocalTime bungaeTime,

        @Schema(
                title = "날짜 후보 리스트",
                description = "모든 날짜는 오늘 이후여야 하며, 오늘로부터 4주 이내여야 합니다.",
                requiredMode = RequiredMode.REQUIRED
        )
        @NotEmpty
        List<@FutureOrPresent @WithinWeeks(4) LocalDate> dateCandidates,

        @Schema(
                title = "투표 마감 시각",
                description = "현재 시각 이후여야 합니다.",
                requiredMode = RequiredMode.REQUIRED
        )
        @NotNull
        @Future
        LocalDateTime dateVoteClosedAt,

        @Schema(
                title = "그룹 ID",
                requiredMode = RequiredMode.REQUIRED
        )
        @IsPositiveNumber
        @NotNull
        Long groupId

) {}





