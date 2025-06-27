package com.midasdev.mybg.member.service;

import com.midasdev.mybg.auth.dto.TokenRequestUser;
import com.midasdev.mybg.global.application.DefaultProfileImageService;
import com.midasdev.mybg.global.application.DefaultProfileImageType;
import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.member.domain.Member;
import com.midasdev.mybg.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final DefaultProfileImageService defaultProfileImageService;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Optional<Member> findMemberByOauthAccount(TokenRequestUser tokenRequestUser) {
        return memberRepository.findMemberByOauthAccountAndDeletedIsFalse(tokenRequestUser.oauthAccount());
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new ApplicationException(
                ApplicationExceptionType.MEMBER_NOT_FOUND_BY_ID, memberId));
    }

    @Transactional
    public Member register(TokenRequestUser tokenRequestUser) {
        Member member = Member.builder()
                              .oauthAccount(tokenRequestUser.oauthAccount())
                              .name(tokenRequestUser.nickname())
                              .profileImageUrl(defaultProfileImageService.createRandomProfileImageUrl(DefaultProfileImageType.MEMBER))
                              .build();

        return memberRepository.save(member);
    }

}
