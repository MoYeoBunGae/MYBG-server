package com.midasdev.mybg.config.security.jwt;

public enum TokenType {
    AUTH,
    ACCESS,
    REFRESH;

    public boolean match(String type) {
        return this.toString().equals(type);
    }
}
