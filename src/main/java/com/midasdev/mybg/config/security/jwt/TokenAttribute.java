package com.midasdev.mybg.config.security.jwt;

import lombok.Getter;

@Getter
public enum TokenAttribute {
    TYPE("type"),
    ID_TOKEN("idToken"),
    SUB("sub"),
    NICKNAME("nickname"),
    KID("kid");

    private final String attribute;

    TokenAttribute(String attribute) {
        this.attribute = attribute;
    }
}
