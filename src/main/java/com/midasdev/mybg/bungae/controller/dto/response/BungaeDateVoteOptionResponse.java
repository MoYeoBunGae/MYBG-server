package com.midasdev.mybg.bungae.controller.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
public record BungaeDateVoteOptionResponse(
        List<LocalDate> dateOptions
) {}

