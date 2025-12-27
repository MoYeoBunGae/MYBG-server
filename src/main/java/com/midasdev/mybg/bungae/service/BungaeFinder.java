package com.midasdev.mybg.bungae.service;

import com.midasdev.mybg.bungae.domain.Bungae;
import com.midasdev.mybg.bungae.repository.BungaeRepository;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BungaeFinder {

    private final BungaeRepository bungaeRepository;

    /**
     * ID로 번개를 조회하고 존재 여부를 검증합니다.
     *
     * @param bungaeId 번개 ID
     * @return 조회된 번개
     * @throws ApplicationException 번개가 존재하지 않는 경우
     */
    public Bungae findById(Long bungaeId) {
        return bungaeRepository
                .findByIdAndDeletedIsFalse(bungaeId)
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionType.BUNGAE_NOT_FOUND_BY_ID, bungaeId));
    }
}
