package com.midasdev.mybg.bungae.controller.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BungaeDateVoteRequest {

    @NotNull
    @Size(min = 1)
    private List<@NotNull @Valid LocalDate> voteDates;
}
