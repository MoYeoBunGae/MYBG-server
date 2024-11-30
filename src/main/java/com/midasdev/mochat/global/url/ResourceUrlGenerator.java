package com.midasdev.mochat.global.url;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class ResourceUrlGenerator {

    @Value("${cloudfront.url}")
    private String cloudFrontUrl;

    public String generateS3Url(ResourcePath path, String file) {
        return UriComponentsBuilder.fromHttpUrl(cloudFrontUrl)
                                   .path(path.getPath())
                                   .path(file)
                                   .toUriString();
    }

    public String generateS3Url(String path, String file) {
        return UriComponentsBuilder.fromHttpUrl(cloudFrontUrl)
                                   .path(path)
                                   .path(file)
                                   .toUriString();
    }

}
