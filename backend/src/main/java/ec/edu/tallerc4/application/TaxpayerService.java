package ec.edu.tallerc4.application;

import ec.edu.tallerc4.domain.InputValidator;
import ec.edu.tallerc4.domain.TaxpayerDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaxpayerService {
    private final Optional<TaxpayerProvider> provider;

    public TaxpayerService(Optional<TaxpayerProvider> provider) {
        this.provider = provider;
    }

    public TaxpayerDetails find(String ruc, String email) {
        InputValidator.requireRuc(ruc);
        InputValidator.requireEmail(email);
        TaxpayerProvider active = provider.orElseThrow(() ->
                new ServiceUnavailableException("El proveedor SRI no está configurado."));
        if (!active.exists(ruc)) throw new ResourceNotFoundException("No se encontró un contribuyente para el RUC indicado.");
        return active.find(ruc);
    }
}
