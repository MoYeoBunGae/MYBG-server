package com.midasdev.mochat.config.security.jwt;

public enum GrantType {
    BEARER("Bearer");

    private final String type;

    private GrantType(String type) {
        this.type = type;
    }
}
