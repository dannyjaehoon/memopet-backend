package com.memopet.memopet.global.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Configuration
@EnableCaching
public class CachingConfig {

  public static final String CONFIG_CACHE = "config";
  public static final String LICENSE_CACHE = "license";

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    return defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(300))
        .disableCachingNullValues()
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
  }

  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    return (builder) -> builder
        .withCacheConfiguration(CONFIG_CACHE, defaultCacheConfig().entryTtl(Duration.ofSeconds(10)))
        .withCacheConfiguration(LICENSE_CACHE, defaultCacheConfig().entryTtl(Duration.ofSeconds(200)));
  }
}