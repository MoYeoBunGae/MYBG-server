package com.midasdev.mybg.global.application;

import com.midasdev.mybg.global.url.ResourceUrlGenerator;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultProfileImageService {

    private static final String PNG_EXT = "png";

    private final ResourceUrlGenerator resourceUrlGenerator;

    public String createRandomProfileImageUrl(DefaultProfileImageType defaultProfileImageType) {
        Random random = new Random();
        int randomProfileImageNumber = random.nextInt(defaultProfileImageType.getTotalImage()) + 1;
        return resourceUrlGenerator.generateS3Url(
                defaultProfileImageType.getDirectory(),
                getFilename(defaultProfileImageType.getFilePrefix(), randomProfileImageNumber));
    }

    private String getFilename(String filePrefix, int number) {
        return filePrefix + number + "." + PNG_EXT;
    }
}
