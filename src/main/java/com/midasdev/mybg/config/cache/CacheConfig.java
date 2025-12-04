package com.midasdev.mybg.config.cache;

import com.midasdev.mybg.global.cache.Cache;
import java.time.Duration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager ehCacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        CacheConfiguration<String, String> configuration = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String.class,
                String.class,
                ResourcePoolsBuilder.heap(10))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(7)))
                .build();
        if (cacheManager.getCache(Cache.OIDC_PUBLIC_KEYS) == null) {
            cacheManager.createCache(Cache.OIDC_PUBLIC_KEYS,
                    Eh107Configuration.fromEhcacheCacheConfiguration(configuration));
        }
        return cacheManager;
    }

}
