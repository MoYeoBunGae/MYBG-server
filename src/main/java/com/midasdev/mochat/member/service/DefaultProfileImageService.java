package com.midasdev.mochat.member.service;

import com.midasdev.mochat.global.url.ResourcePath;
import com.midasdev.mochat.global.url.ResourceUrlGenerator;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultProfileImageService {

    private static final int TOTAL_DEFAULT_PROFILE_IMG = 10;
    private static final String FILE_PREFIX = "/profile";
    private static final String PNG_EXT = "png";

    private final ResourceUrlGenerator resourceUrlGenerator;

    public String createRandomProfileImageUrl() {
        Random random = new Random();
        int randomProfileImageNumber = random.nextInt(TOTAL_DEFAULT_PROFILE_IMG) + 1;
        return resourceUrlGenerator.generateS3Url(ResourcePath.DEFAULT_PROFILE_IMG, getFilename(randomProfileImageNumber));
    }

    public String createRandomProfileImageUrl(DefaultProfileImageType defaultProfileImageType) {
        Random random = new Random();
        int randomProfileImageNumber = random.nextInt(defaultProfileImageType.getTotalImage()) + 1;
        return resourceUrlGenerator.generateS3Url(defaultProfileImageType.getDirectory(),
                                                  getFilename(defaultProfileImageType.getFilePrefix(), randomProfileImageNumber));
    }

    private String getFilename(int number) {
        return FILE_PREFIX + number + "." + PNG_EXT;
    }

    private String getFilename(String filePrefix, int number) {
        return filePrefix + number + "." + PNG_EXT;
    }

}
