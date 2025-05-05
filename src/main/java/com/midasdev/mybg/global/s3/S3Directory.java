package com.midasdev.mybg.global.s3;

import lombok.Getter;

@Getter
public enum S3Directory {
    GROUP_PROFILE_IMAGES("group_profile_images"),
    GROUP_MEMBER_PROFILE_IMAGES("group_member_profile_images");

    private final String directory;

    S3Directory(String directory) {
        this.directory = directory;
    }
}
