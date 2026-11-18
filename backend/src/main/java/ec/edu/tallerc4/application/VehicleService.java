package ec.edu.tallerc4.application;

import ec.edu.tallerc4.domain.InputValidator;
import ec.edu.tallerc4.domain.VehicleDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleService {
    private final Optional<VehicleProvider> provider;

    public VehicleService(Optional<VehicleProvider> provider) { this.provider = provider; }

    public VehicleDetails find(String plate) {
        String normalized = InputValidator.requirePlate(plate);
        return provider.orElseThrow(() ->
                new ServiceUnavailableException("El proveedor SRI de vehículos no está configurado.")).find(normalized);
    }
}
