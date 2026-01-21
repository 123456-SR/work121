package org.example.work121.controller;

import org.example.work121.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateEntrustmentPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "entrustment.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/preview")
    public ResponseEntity<byte[]> previewPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateEntrustmentPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/light_dynamic_penetration/generate")
    public ResponseEntity<byte[]> generateLightDynamicPenetrationPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateLightDynamicPenetrationPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "light_dynamic_penetration.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/light_dynamic_penetration/preview")
    public ResponseEntity<byte[]> previewLightDynamicPenetrationPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateLightDynamicPenetrationPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/light_dynamic_penetration_record/generate")
    public ResponseEntity<byte[]> generateLightDynamicPenetrationRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateLightDynamicPenetrationRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "light_dynamic_penetration_record.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/light_dynamic_penetration_record/preview")
    public ResponseEntity<byte[]> previewLightDynamicPenetrationRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateLightDynamicPenetrationRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/nuclear_density_record/generate")
    public ResponseEntity<byte[]> generateNuclearDensityRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateNuclearDensityRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "nuclear_density_record.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/nuclear_density_record/preview")
    public ResponseEntity<byte[]> previewNuclearDensityRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateNuclearDensityRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/cutting_ring_record/generate")
    public ResponseEntity<byte[]> generateCuttingRingRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateCuttingRingRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cutting_ring_record.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/cutting_ring_record/preview")
    public ResponseEntity<byte[]> previewCuttingRingRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateCuttingRingRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/density_test_report/generate")
    public ResponseEntity<byte[]> generateDensityTestReportPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateDensityTestReportPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "density_test_report.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/density_test_report/preview")
    public ResponseEntity<byte[]> previewDensityTestReportPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateDensityTestReportPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/density_test_result/generate")
    public ResponseEntity<byte[]> generateDensityTestResultPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateDensityTestResultPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "density_test_result.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/density_test_result/preview")
    public ResponseEntity<byte[]> previewDensityTestResultPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateDensityTestResultPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/rebound_method_record/generate")
    public ResponseEntity<byte[]> generateReboundMethodRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateReboundMethodRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "rebound_method_record.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/rebound_method_record/preview")
    public ResponseEntity<byte[]> previewReboundMethodRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateReboundMethodRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/sand_replacement_record/generate")
    public ResponseEntity<byte[]> generateSandReplacementRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateSandReplacementRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "sand_replacement_record.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/sand_replacement_record/preview")
    public ResponseEntity<byte[]> previewSandReplacementRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateSandReplacementRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/water_replacement_record/generate")
    public ResponseEntity<byte[]> generateWaterReplacementRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateWaterReplacementRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "water_replacement_record.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/water_replacement_record/preview")
    public ResponseEntity<byte[]> previewWaterReplacementRecordPdf(HttpServletRequest request) {
        byte[] pdfBytes = pdfGeneratorService.generateWaterReplacementRecordPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
