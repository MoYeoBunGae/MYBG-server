package com.midasdev.mochat.config.security.jwt.repository;

import com.midasdev.mochat.config.security.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, Long> {

}
