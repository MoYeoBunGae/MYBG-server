package com.midasdev.mochat.member.repository;

import com.midasdev.mochat.config.security.Oauth.OauthAccount;
import com.midasdev.mochat.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberSpringDataRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByOauthAccountAndDeletedIsFalse(OauthAccount oauthAccount);
}
