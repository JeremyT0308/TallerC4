package ec.edu.tallerc4.interfaces.rest;

import ec.edu.tallerc4.application.AntPointsResponse;
import ec.edu.tallerc4.application.AntPointsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ant")
public class AntController {
    private final AntPointsService service;

    public AntController(AntPointsService service) { this.service = service; }

    @GetMapping("/points")
    public AntPointsResponse points(@RequestParam String identification,
                                    @RequestParam(required = false) String scenario) {
        return scenario == null ? service.query(identification) : service.query(identification, scenario);
    }
}
