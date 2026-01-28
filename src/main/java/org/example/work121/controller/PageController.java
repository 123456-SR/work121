package org.example.work121.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PageController {

    @GetMapping("/")
    public RedirectView index() {
        return new RedirectView("index.html");
    }

    @GetMapping("/entrustment")
    public RedirectView entrustment() {
        return new RedirectView("entrustment-vue.html");
    }

    @GetMapping("/signature")
    public RedirectView signature() {
        return new RedirectView("signature-vue.html");
    }

    @GetMapping("/light_dynamic_penetration")
    public RedirectView lightDynamicPenetration() {
        return new RedirectView("light_dynamic_penetration-vue.html");
    }

    @GetMapping("/light_dynamic_penetration_record")
    public RedirectView lightDynamicPenetrationRecord() {
        return new RedirectView("light_dynamic_penetration_record-vue.html");
    }

    @GetMapping("/nuclear_density_record")
    public RedirectView nuclearDensityRecord() {
        return new RedirectView("nuclear_density_record-vue.html");
    }

    @GetMapping("/density_test_report")
    public RedirectView densityTestReport() {
        return new RedirectView("density_test_report-vue.html");
    }

    @GetMapping("/density_test_result")
    public RedirectView densityTestResult() {
        return new RedirectView("density_test_result-vue.html");
    }

    @GetMapping("/sand_replacement_record")
    public RedirectView sandReplacementRecord() {
        return new RedirectView("sand_replacement_record-vue.html");
    }

    @GetMapping("/water_replacement_record")
    public RedirectView waterReplacementRecord() {
        return new RedirectView("water_replacement_record-vue.html");
    }

    @GetMapping("/cutting_ring_record")
    public RedirectView cuttingRingRecord() {
        return new RedirectView("cutting_ring_record-vue.html");
    }

    @GetMapping("/rebound_method_record")
    public RedirectView reboundMethodRecord() {
        return new RedirectView("rebound_method_record-vue.html");
    }

    @GetMapping("/rebound_method_report")
    public RedirectView reboundMethodReport() {
        return new RedirectView("rebound_method_report-vue.html");
    }

    @GetMapping("/beckman_beam_record")
    public RedirectView beckmanBeamRecord() {
        return new RedirectView("beckman_beam_record-vue.html");
    }

    @GetMapping("/beckman_beam_report")
    public RedirectView beckmanBeamReport() {
        return new RedirectView("beckman_beam_report-vue.html");
    }

    @GetMapping("/beckman_beam_result")
    public RedirectView beckmanBeamResult() {
        return new RedirectView("beckman_beam_result-vue.html");
    }
}
