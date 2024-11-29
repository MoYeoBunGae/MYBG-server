package com.midasdev.mochat.group.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GroupCreateRequest(@NotBlank String name, String profileImageUrl) {

}
