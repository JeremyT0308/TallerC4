package ec.edu.tallerc4.infrastructure.sri;

import ec.edu.tallerc4.application.ServiceUnavailableException;

public class SriPayloadException extends ServiceUnavailableException {
    public SriPayloadException(String message, Throwable cause) { super(message, cause); }
    public SriPayloadException(String message) { super(message); }
}
