package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.VehicleService;
import ec.edu.tallerc4.domain.VehicleDetails;
import ec.edu.tallerc4.domain.InputValidator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
    private final VehicleService service;

    public VehicleController(VehicleService service) { this.service = service; }

    @GetMapping("/{plate}")
    public VehicleDetails get(@PathVariable String plate) { return service.find(InputValidator.requirePlate(plate)); }
}
