package org.example.work121.controller;

import org.example.work121.service.PdfGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
public class PdfController {
    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    private ResponseEntity<byte[]> asInlinePdf(byte[] bytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private ResponseEntity<byte[]> asAttachmentPdf(byte[] bytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping("/entrustment/preview")
    public ResponseEntity<byte[]> previewEntrustment(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateEntrustmentPdf(request);
        return asInlinePdf(pdf, "entrustment.pdf");
    }

    @PostMapping("/entrustment/generate")
    public ResponseEntity<byte[]> generateEntrustment(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateEntrustmentPdf(request);
        return asAttachmentPdf(pdf, "entrustment.pdf");
    }

    @PostMapping("/density_test_report/preview")
    public ResponseEntity<byte[]> previewDensityTestReport(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateDensityTestReportPdf(request);
        return asInlinePdf(pdf, "density_test_report.pdf");
    }

    @PostMapping("/density_test_report/generate")
    public ResponseEntity<byte[]> generateDensityTestReport(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateDensityTestReportPdf(request);
        return asAttachmentPdf(pdf, "density_test_report.pdf");
    }

    @PostMapping("/density_test_result/preview")
    public ResponseEntity<byte[]> previewDensityTestResult(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateDensityTestResultPdf(request);
        return asInlinePdf(pdf, "density_test_result.pdf");
    }

    @PostMapping("/density_test_result/generate")
    public ResponseEntity<byte[]> generateDensityTestResult(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateDensityTestResultPdf(request);
        return asAttachmentPdf(pdf, "density_test_result.pdf");
    }

    @PostMapping("/rebound_method_record/preview")
    public ResponseEntity<byte[]> previewReboundMethodRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateReboundMethodRecordPdf(request);
        return asInlinePdf(pdf, "rebound_method_record.pdf");
    }

    @PostMapping("/rebound_method_record/generate")
    public ResponseEntity<byte[]> generateReboundMethodRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateReboundMethodRecordPdf(request);
        return asAttachmentPdf(pdf, "rebound_method_record.pdf");
    }

    @PostMapping("/rebound_method_report/preview")
    public ResponseEntity<byte[]> previewReboundMethodReport(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateReboundMethodReportPdf(request);
        return asInlinePdf(pdf, "rebound_method_report.pdf");
    }

    @PostMapping("/rebound_method_report/generate")
    public ResponseEntity<byte[]> generateReboundMethodReport(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateReboundMethodReportPdf(request);
        return asAttachmentPdf(pdf, "rebound_method_report.pdf");
    }

    @PostMapping("/nuclear_density_record/preview")
    public ResponseEntity<byte[]> previewNuclearDensityRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateNuclearDensityRecordPdf(request);
        return asInlinePdf(pdf, "nuclear_density_record.pdf");
    }

    @PostMapping("/nuclear_density_record/generate")
    public ResponseEntity<byte[]> generateNuclearDensityRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateNuclearDensityRecordPdf(request);
        return asAttachmentPdf(pdf, "nuclear_density_record.pdf");
    }

    @PostMapping("/sand_replacement_record/preview")
    public ResponseEntity<byte[]> previewSandReplacementRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateSandReplacementRecordPdf(request);
        return asInlinePdf(pdf, "sand_replacement_record.pdf");
    }

    @PostMapping("/sand_replacement_record/generate")
    public ResponseEntity<byte[]> generateSandReplacementRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateSandReplacementRecordPdf(request);
        return asAttachmentPdf(pdf, "sand_replacement_record.pdf");
    }

    @PostMapping("/water_replacement_record/preview")
    public ResponseEntity<byte[]> previewWaterReplacementRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateWaterReplacementRecordPdf(request);
        return asInlinePdf(pdf, "water_replacement_record.pdf");
    }

    @PostMapping("/water_replacement_record/generate")
    public ResponseEntity<byte[]> generateWaterReplacementRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateWaterReplacementRecordPdf(request);
        return asAttachmentPdf(pdf, "water_replacement_record.pdf");
    }

    @PostMapping("/cutting_ring_record/preview")
    public ResponseEntity<byte[]> previewCuttingRingRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateCuttingRingRecordPdf(request);
        return asInlinePdf(pdf, "cutting_ring_record.pdf");
    }

    @PostMapping("/cutting_ring_record/generate")
    public ResponseEntity<byte[]> generateCuttingRingRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateCuttingRingRecordPdf(request);
        return asAttachmentPdf(pdf, "cutting_ring_record.pdf");
    }

    @PostMapping("/beckman_beam_record/preview")
    public ResponseEntity<byte[]> previewBeckmanBeamRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateBeckmanBeamRecordPdf(request);
        return asInlinePdf(pdf, "beckman_beam_record.pdf");
    }

    @PostMapping("/beckman_beam_record/generate")
    public ResponseEntity<byte[]> generateBeckmanBeamRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateBeckmanBeamRecordPdf(request);
        return asAttachmentPdf(pdf, "beckman_beam_record.pdf");
    }

    @PostMapping("/beckman_beam_report/preview")
    public ResponseEntity<byte[]> previewBeckmanBeamReport(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateBeckmanBeamReportPdf(request);
        return asInlinePdf(pdf, "beckman_beam_report.pdf");
    }

    @PostMapping("/beckman_beam_report/generate")
    public ResponseEntity<byte[]> generateBeckmanBeamReport(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateBeckmanBeamReportPdf(request);
        return asAttachmentPdf(pdf, "beckman_beam_report.pdf");
    }

    @PostMapping("/beckman_beam_result/preview")
    public ResponseEntity<byte[]> previewBeckmanBeamResult(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateBeckmanBeamResultPdf(request);
        return asInlinePdf(pdf, "beckman_beam_result.pdf");
    }

    @PostMapping("/beckman_beam_result/generate")
    public ResponseEntity<byte[]> generateBeckmanBeamResult(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateBeckmanBeamResultPdf(request);
        return asAttachmentPdf(pdf, "beckman_beam_result.pdf");
    }

    @PostMapping("/light_dynamic_penetration/preview")
    public ResponseEntity<byte[]> previewLightDynamicPenetrationReport(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateLightDynamicPenetrationPdf(request);
        return asInlinePdf(pdf, "light_dynamic_penetration_report.pdf");
    }

    @PostMapping("/light_dynamic_penetration/generate")
    public ResponseEntity<byte[]> generateLightDynamicPenetrationReport(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateLightDynamicPenetrationPdf(request);
        return asAttachmentPdf(pdf, "light_dynamic_penetration_report.pdf");
    }

    @PostMapping("/light_dynamic_penetration_result/preview")
    public ResponseEntity<byte[]> previewLightDynamicPenetrationResult(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateLightDynamicPenetrationResultPdf(request);
        return asInlinePdf(pdf, "light_dynamic_penetration_result.pdf");
    }

    @PostMapping("/light_dynamic_penetration_result/generate")
    public ResponseEntity<byte[]> generateLightDynamicPenetrationResult(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateLightDynamicPenetrationResultPdf(request);
        return asAttachmentPdf(pdf, "light_dynamic_penetration_result.pdf");
    }

    @PostMapping("/light_dynamic_penetration_record/preview")
    public ResponseEntity<byte[]> previewLightDynamicPenetrationRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateLightDynamicPenetrationRecordPdf(request);
        return asInlinePdf(pdf, "light_dynamic_penetration_record.pdf");
    }

    @PostMapping("/light_dynamic_penetration_record/generate")
    public ResponseEntity<byte[]> generateLightDynamicPenetrationRecord(HttpServletRequest request) {
        byte[] pdf = pdfGeneratorService.generateLightDynamicPenetrationRecordPdf(request);
        return asAttachmentPdf(pdf, "light_dynamic_penetration_record.pdf");
    }
}
