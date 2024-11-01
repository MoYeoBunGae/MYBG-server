package com.midasdev.mochat.member.service;

import com.midasdev.mochat.auth.dto.TokenRequestUser;
import com.midasdev.mochat.member.domain.Member;
import com.midasdev.mochat.member.repository.MemberSpringDataRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberSpringDataRepository memberSpringDataRepository;
    public Optional<Member> findMemberByOauthAccount(TokenRequestUser tokenRequestUser) {
        return memberSpringDataRepository.findMemberByOauthAccountAndDeletedIsFalse(tokenRequestUser.toOauthAccount());
    }

}
