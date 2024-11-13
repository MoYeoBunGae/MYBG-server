package com.midasdev.mochat.config.security.jwt;

import lombok.Getter;

@Getter
public enum GrantType {
    BEARER("Bearer");

    private final String type;

    private GrantType(String type) {
        this.type = type;
    }
}
