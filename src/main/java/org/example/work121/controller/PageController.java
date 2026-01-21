package org.example.work121.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/entrustment")
    public String entrustment() {
        return "entrustment";
    }

    @GetMapping("/signature")
    public String signature() {
        return "signature";
    }

    @GetMapping("/light_dynamic_penetration")
    public String lightDynamicPenetration() {
        return "light_dynamic_penetration";
    }

    @GetMapping("/light_dynamic_penetration_record")
    public String lightDynamicPenetrationRecord() {
        return "light_dynamic_penetration_record";
    }

    @GetMapping("/nuclear_density_record")
    public String nuclearDensityRecord() {
        return "nuclear_density_record";
    }
}
