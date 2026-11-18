package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.ResourceNotFoundException;
import ec.edu.tallerc4.application.BadGatewayException;
import ec.edu.tallerc4.application.ServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> validation(IllegalArgumentException ex, HttpServletRequest request) {
        return response("VALIDATION_ERROR", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> missing(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return response("VALIDATION_ERROR", "Falta el parámetro requerido: " + ex.getParameterName() + ".", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> notFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return response("NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<ApiError> badGateway(BadGatewayException ex, HttpServletRequest request) {
        return response("BAD_GATEWAY", ex.getMessage(), HttpStatus.BAD_GATEWAY, request);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> unavailable(ServiceUnavailableException ex, HttpServletRequest request) {
        return response("SERVICE_UNAVAILABLE", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    private ResponseEntity<ApiError> response(String code, String message, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiError(code, message, status.value(), Instant.now(), request.getRequestURI()));
    }
}
