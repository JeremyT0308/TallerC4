package ec.edu.tallerc4.application;

public class BadGatewayException extends ServiceUnavailableException {
    public BadGatewayException(String message) { super(message); }
    public BadGatewayException(String message, Throwable cause) { super(message, cause); }
}
