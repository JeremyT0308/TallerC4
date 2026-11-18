package ec.edu.tallerc4.application;

import ec.edu.tallerc4.domain.TaxpayerDetails;

public interface TaxpayerProvider {
    boolean exists(String ruc);
    TaxpayerDetails find(String ruc);
}
