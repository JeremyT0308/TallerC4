package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.TaxpayerService;
import ec.edu.tallerc4.domain.TaxpayerDetails;
import ec.edu.tallerc4.domain.InputValidator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/taxpayers")
public class TaxpayerController {
    private final TaxpayerService service;

    public TaxpayerController(TaxpayerService service) { this.service = service; }

    @GetMapping("/{ruc}")
    public TaxpayerDetails get(@PathVariable String ruc, @RequestParam String email) {
        return service.find(InputValidator.requireRuc(ruc), InputValidator.requireEmail(email));
    }
}
