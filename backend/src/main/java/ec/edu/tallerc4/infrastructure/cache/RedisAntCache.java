package ec.edu.tallerc4.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.tallerc4.application.AntCache;
import ec.edu.tallerc4.application.CacheUnavailableException;
import ec.edu.tallerc4.application.CachedAntPoints;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;

public class RedisAntCache implements AntCache {
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final Duration maxStale;

    public RedisAntCache(StringRedisTemplate redis, ObjectMapper mapper, Duration maxStale) {
        this.redis = redis;
        this.mapper = mapper.findAndRegisterModules();
        this.maxStale = maxStale;
    }

    @Override
    public Optional<CachedAntPoints> find(String identification) {
        try {
            String json = redis.opsForValue().get(key(identification));
            if (json == null) return Optional.empty();
            return Optional.of(mapper.readValue(json, CachedAntPoints.class));
        } catch (DataAccessException | JsonProcessingException ex) {
            throw new CacheUnavailableException("Redis no está disponible para leer cache ANT.", ex);
        }
    }

    @Override
    public void saveSuccessful(CachedAntPoints value) {
        if (value.points() == null) return;
        try {
            redis.opsForValue().set(key(value.identification()), mapper.writeValueAsString(value), maxStale);
        } catch (DataAccessException | JsonProcessingException ex) {
            throw new CacheUnavailableException("Redis no está disponible para guardar cache ANT.", ex);
        }
    }

    private String key(String identification) { return "tallerc4:ant:points:" + identification; }
}
