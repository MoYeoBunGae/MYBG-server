package com.midasdev.mybg.bungae.controller.dto.response;

import java.time.LocalDate;
import java.util.List;

public record BungaeDateVoteOptionResponse(
        List<LocalDate> dateOptions
) {}

