package com.midasdev.mochat.config.security.jwt.constant;

import lombok.Getter;

@Getter
public enum JwtComponent {
    HEADER(0, "HEADER"), BODY(1, "BODY");

    int index;
    String name;

    JwtComponent(int index, String name) {
        this.index = index;
        this.name = name;
    }
}
