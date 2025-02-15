package com.midasdev.mybg.global.application;

import lombok.Getter;

@Getter
public enum DefaultProfileImageType {
    MEMBER(10, "default_profile_img", "/profile"),
    GROUP(4, "default_group_profile_img", "/group_profile_img");

    int totalImage;
    String directory;
    String filePrefix;

    DefaultProfileImageType(int totalImage, String directory, String filePrefix) {
        this.totalImage = totalImage;
        this.directory = directory;
        this.filePrefix = filePrefix;
    }
}
