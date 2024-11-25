package com.midasdev.mochat.sample.controller;

import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth-free")
@RequiredArgsConstructor
public class SampleController {

    private final SampleSpringDataRepository sampleSpringDataRepository;

    @PostMapping
    public String createSampleData() {
        sampleSpringDataRepository.save(SampleEntity.getSample());
        return "Success!";
    }

    @GetMapping("/error")
    public String errorTest() {
        log.info("createSampleData");
        throw new ApplicationException(ApplicationExceptionType.UNDEFINED_EXCEPTION, "test");
    }
}
