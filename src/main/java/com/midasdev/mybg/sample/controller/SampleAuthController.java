package com.midasdev.mybg.sample.controller;

import static com.midasdev.mybg.config.swagger.SwaggerConfig.SECURITY_SCHEME_NAME;

import com.midasdev.mybg.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth-required")
@RequiredArgsConstructor
public class SampleAuthController {

    private final SampleSpringDataRepository sampleSpringDataRepository;

    @Operation(
            summary = "Sample API",
            description = "Sample API",
            security = @SecurityRequirement(name = SECURITY_SCHEME_NAME))
    @GetMapping
    public ResponseEntity<List<SampleEntity>> findSample(@AuthenticationPrincipal Member member) {
        log.info("member: {}", member.getId());
        List<SampleEntity> all = sampleSpringDataRepository.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }
}
