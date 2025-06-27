package com.midasdev.mybg.member.repository;

import com.midasdev.mybg.config.security.Oauth.OauthAccount;
import com.midasdev.mybg.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByOauthAccountAndDeletedIsFalse(OauthAccount oauthAccount);
}
