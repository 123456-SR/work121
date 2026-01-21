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

    @GetMapping("/density_test_report")
    public String densityTestReport() {
        return "density_test_report";
    }

    @GetMapping("/density_test_result")
    public String densityTestResult() {
        return "density_test_result";
    }

    @GetMapping("/sand_replacement_record")
    public String sandReplacementRecord() {
        return "sand_replacement_record";
    }

    @GetMapping("/water_replacement_record")
    public String waterReplacementRecord() {
        return "water_replacement_record";
    }

    @GetMapping("/cutting_ring_record")
    public String cuttingRingRecord() {
        return "cutting_ring_record";
    }

    @GetMapping("/rebound_method_record")
    public String reboundMethodRecord() {
        return "rebound_method_record";
    }

    @GetMapping("/rebound_method_report")
    public String reboundMethodReport() {
        return "rebound_method_report";
    }

    @GetMapping("/beckman_beam_record")
    public String beckmanBeamRecord() {
        return "beckman_beam_record";
    }

    @GetMapping("/beckman_beam_report")
    public String beckmanBeamReport() {
        return "beckman_beam_report";
    }

    @GetMapping("/beckman_beam_result")
    public String beckmanBeamResult() {
        return "beckman_beam_result";
    }
}
