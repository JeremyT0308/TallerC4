package ec.edu.tallerc4.interfaces.rest;

import java.time.Instant;

public record ApiError(String code, String message, int status, Instant timestamp, String path) { }
