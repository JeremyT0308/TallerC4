package ec.edu.tallerc4.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.tallerc4.application.CacheUnavailableException;
import ec.edu.tallerc4.application.CachedAntPoints;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RedisAntCacheTest {
    @Test
    void serializesSuccessfulResultWithMaximumStaleTtl() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        RedisAntCache cache = new RedisAntCache(redis, new ObjectMapper(), Duration.ofDays(7));
        CachedAntPoints item = new CachedAntPoints("0912576287", 30.0, "ANT", false,
                Instant.parse("2026-11-17T20:00:00Z"), "B", null);

        cache.saveSuccessful(item);

        verify(values).set(eq("tallerc4:ant:points:0912576287"), contains("0912576287"), eq(Duration.ofDays(7)));
    }

    @Test
    void wrapsRedisFailureAsCacheUnavailable() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        when(redis.opsForValue()).thenThrow(new DataAccessResourceFailureException("down"));

        assertThrows(CacheUnavailableException.class,
                () -> new RedisAntCache(redis, new ObjectMapper(), Duration.ofDays(7)).find("0912576287"));
    }
}
