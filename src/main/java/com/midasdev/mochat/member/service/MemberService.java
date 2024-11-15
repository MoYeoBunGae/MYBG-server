package com.midasdev.mochat.member.service;

import com.midasdev.mochat.auth.dto.TokenRequestUser;
import com.midasdev.mochat.config.security.jwt.RefreshToken;
import com.midasdev.mochat.config.security.jwt.repository.RefreshTokenRedisRepository;
import com.midasdev.mochat.global.exception.ApplicationException;
import com.midasdev.mochat.global.exception.ApplicationExceptionType;
import com.midasdev.mochat.global.util.assertion.Assertion;
import com.midasdev.mochat.member.domain.Member;
import com.midasdev.mochat.member.repository.MemberSpringDataRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final DefaultProfileImageService defaultProfileImageService;
    private final MemberSpringDataRepository memberSpringDataRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Transactional(readOnly = true)
    public Optional<Member> findMemberByOauthAccount(TokenRequestUser tokenRequestUser) {
        return memberSpringDataRepository.findMemberByOauthAccountAndDeletedIsFalse(tokenRequestUser.oauthAccount());
    }

    public Member findMemberById(Long memberId) {
        return memberSpringDataRepository.findById(memberId).orElseThrow(() -> new ApplicationException(
                ApplicationExceptionType.MEMBER_NOT_FOUND_BY_ID, memberId));
    }

    @Transactional
    public Member register(TokenRequestUser tokenRequestUser) {
        Member member = Member.builder()
                              .oauthAccount(tokenRequestUser.oauthAccount())
                              .name(tokenRequestUser.nickname())
                              .profileImageUrl(defaultProfileImageService.createRandomProfileImageUrl())
                              .build();

        return memberSpringDataRepository.save(member);
    }

    public void verifyRefreshToken(Long memberId, String refreshToken) {
        // TEST: 저장된 refreshToken 여부에 대한 테스트
        // TEST: refreshToken 일치 여부에 대한 테스트
        // REFACTOR: AuthService로 이동
        RefreshToken refreshTokenFromRedis = refreshTokenRedisRepository.findById(memberId)
                                                                        .orElseThrow(() -> new ApplicationException(
                                                                                ApplicationExceptionType.REFRESH_TOKEN_NOT_FOUND, memberId));
        Assertion.with(refreshToken)
                 .setValidation(refreshTokenFromRedis::isSameToken)
                 .validateOrThrow(() -> new ApplicationException(ApplicationExceptionType.REFRESH_TOKEN_MISMATCH, memberId));
    }

}
