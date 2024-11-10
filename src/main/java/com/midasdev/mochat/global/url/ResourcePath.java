package com.midasdev.mochat.global.url;

import lombok.Getter;

@Getter
public enum ResourcePath {
    DEFAULT_PROFILE_IMG("default_profile_img");

    private final String path;

    ResourcePath(String path) {
        this.path = path;
    }
}
