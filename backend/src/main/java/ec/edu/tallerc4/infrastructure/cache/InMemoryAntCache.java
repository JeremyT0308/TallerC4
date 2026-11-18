package ec.edu.tallerc4.infrastructure.cache;

import ec.edu.tallerc4.application.AntCache;
import ec.edu.tallerc4.application.CachedAntPoints;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryAntCache implements AntCache {
    private final ConcurrentMap<String, CachedAntPoints> entries = new ConcurrentHashMap<>();

    @Override public Optional<CachedAntPoints> find(String identification) { return Optional.ofNullable(entries.get(identification)); }
    @Override public void saveSuccessful(CachedAntPoints value) { if (value.points() != null) entries.put(value.identification(), value); }
}
