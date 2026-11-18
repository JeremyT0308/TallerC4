package ec.edu.tallerc4.infrastructure.cache;

import ec.edu.tallerc4.application.AntCachePolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

@Configuration
@EnableConfigurationProperties(RedisCacheProperties.class)
public class AntCacheConfiguration {
    @Bean
    AntCachePolicy antCachePolicy(RedisCacheProperties properties) {
        return new AntCachePolicy(Duration.ofMinutes(properties.getFreshMinutes()), Duration.ofDays(properties.getMaxStaleDays()));
    }

    @Bean
    Supplier<Instant> antClock() { return Instant::now; }

    @Bean
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
    RedisAntCache redisAntCache(StringRedisTemplate redis, ObjectMapper mapper, RedisCacheProperties properties) {
        return new RedisAntCache(redis, mapper, Duration.ofDays(properties.getMaxStaleDays()));
    }

    @Bean
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "false", matchIfMissing = true)
    InMemoryAntCache inMemoryAntCache() { return new InMemoryAntCache(); }
}
