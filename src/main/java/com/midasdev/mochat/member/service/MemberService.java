package com.midasdev.mochat.member.service;

import com.midasdev.mochat.auth.dto.TokenRequestUser;
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

    @Transactional(readOnly = true)
    public Optional<Member> findMemberByOauthAccount(TokenRequestUser tokenRequestUser) {
        return memberSpringDataRepository.findMemberByOauthAccountAndDeletedIsFalse(tokenRequestUser.oauthAccount());
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

}
