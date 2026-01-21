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
}
