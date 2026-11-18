package ec.edu.tallerc4.application;

import ec.edu.tallerc4.domain.VehicleDetails;

public interface VehicleProvider {
    VehicleDetails find(String plate);
}
