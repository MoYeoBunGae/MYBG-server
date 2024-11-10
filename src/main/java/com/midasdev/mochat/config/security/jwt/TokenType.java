package com.midasdev.mochat.config.security.jwt;

public enum TokenType {
    AUTH, ACCESS, REFRESH;

    public boolean match(String type) {
        return this.toString().equals(type);
    }
}
