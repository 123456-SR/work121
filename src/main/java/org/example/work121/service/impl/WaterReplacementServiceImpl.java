package org.example.work121.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.example.work121.entity.WaterReplacement;
import org.example.work121.entity.WaterReplacementReport;
import org.example.work121.entity.WaterReplacementResult;
import org.example.work121.mapper.WaterReplacementMapper;
import org.example.work121.mapper.WaterReplacementReportMapper;
import org.example.work121.mapper.WaterReplacementResultMapper;
import org.example.work121.service.TableGenerationService;
import org.example.work121.service.WaterReplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class WaterReplacementServiceImpl implements WaterReplacementService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WaterReplacementMapper mapper;

    @Autowired
    private WaterReplacementReportMapper reportMapper;

    @Autowired
    private WaterReplacementResultMapper resultMapper;

    @Autowired
    private TableGenerationService tableGenerationService;

    @Override
    public java.util.List<WaterReplacement> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    @Transactional
    public void save(WaterReplacement waterReplacement) {
        // 兼容前端传入空字符串 ID（Oracle 中空字符串会当作 NULL 处理，导致 ORA-01400）
        String id = waterReplacement.getId();
        if (id != null && id.trim().isEmpty()) {
            id = null;
            waterReplacement.setId(null);
        }

        // 如果传了有效的 ID 且数据库中已存在，则执行更新
        if (id != null) {
            WaterReplacement existing = mapper.selectById(id);
            if (existing != null) {
                // 防止前端未传 dataJson / entrustmentId 时把已有值覆盖为 null
                if (waterReplacement.getDataJson() == null) {
                    waterReplacement.setDataJson(existing.getDataJson());
                }
                if (waterReplacement.getEntrustmentId() == null) {
                    waterReplacement.setEntrustmentId(existing.getEntrustmentId());
                }
            mapper.updateById(waterReplacement);
                return;
            }
        } else {
            // 否则视为新增，补充生成主键 ID
            if (waterReplacement.getId() == null || waterReplacement.getId().trim().isEmpty()) {
                waterReplacement.setId(UUID.randomUUID().toString());
            }
            mapper.insert(waterReplacement);
        }
    }

    @Override
    public WaterReplacementReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveReport(WaterReplacementReport report) {
        WaterReplacementReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
        if (existing != null) {
            reportMapper.update(report);
        } else {
            if (report.getId() == null) {
                report.setId(UUID.randomUUID().toString());
            }
            reportMapper.insert(report);
        }
    }

    @Override
    public WaterReplacementResult getResultByEntrustmentId(String entrustmentId) {
        return resultMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveResult(WaterReplacementResult result) {
        WaterReplacementResult existing = resultMapper.selectByEntrustmentId(result.getEntrustmentId());
        if (existing != null) {
            resultMapper.update(result);
        } else {
            if (result.getId() == null) {
                result.setId(UUID.randomUUID().toString());
            }
            resultMapper.insert(result);
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        java.util.List<WaterReplacement> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Cannot generate WaterReplacement report/result: Record not found for entrustmentId " + entrustmentId);
        }
        
        // 委托 + 灌水法记录表状态校验以及具体字段映射，集中在 TableGenerationServiceImpl.generateWaterReplacementReportAndResult 中处理
        // 这里仅负责触发统一的报表生成服务
        tableGenerationService.generateReportAndResult("WATER_REPLACEMENT", entrustmentId);
    }

    @Override
    public String exportRecordToExcel(Map<String, Object> payload) {
        if (payload == null) throw new RuntimeException("导出失败：缺少数据");

        String templateBaseName = stringValue(payload.get("templateBaseName"));
        if (templateBaseName.isEmpty()) templateBaseName = stringValue(payload.get("baseName"));
        if (templateBaseName.isEmpty()) templateBaseName = "灌水法记录表";

        String format = stringValue(payload.get("format")).toLowerCase(Locale.ROOT).trim();
        if (format.isEmpty()) format = "xls";

        Path templateDir = Paths.get(System.getProperty("user.dir"), "表");
        Path templatePath = templateDir.resolve(templateBaseName + "." + format);
        if (!Files.exists(templatePath)) {
            throw new RuntimeException("模板不存在，请先在“表”文件夹中导入模板: " + templatePath.toString());
        }

        Map<String, Object> data = extractExcelData(payload);
        Path outputPath = buildExcelOutputPath(templatePath, data, payload, format, templateBaseName);
        try {
            byte[] bytes = Files.readAllBytes(templatePath);
            if ("xlsx".equals(format) || "xls".equals(format)) {
                try (Workbook workbook = "xls".equals(format)
                        ? new HSSFWorkbook(new ByteArrayInputStream(bytes))
                        : new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
                    fillWorkbook(workbook, data);
                    try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                        workbook.write(out);
                    }
                }
            } else if ("docx".equals(format)) {
                try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
                    fillDocxDocument(doc, data);
                    try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                        doc.write(out);
                    }
                }
            } else {
                throw new RuntimeException("不支持的导出格式: " + format);
            }
        } catch (IOException e) {
            throw new RuntimeException("写入导出文件失败: " + e.getMessage(), e);
        }
        return outputPath.toString();
    }

    private Path buildExcelOutputPath(Path templatePath, Map<String, Object> data, Map<String, Object> payload, String ext, String templateBaseName) {
        Path dir = templatePath.getParent();
        if (dir == null) {
            throw new RuntimeException("Excel模板路径无效: " + templatePath);
        }

        String projectName = sanitizeFileName(stringValue(data.get("projectName")));
        String projectPrefix = projectName;
        if (projectPrefix.length() > 3) projectPrefix = projectPrefix.substring(0, 3);

        int pageNo = 0;
        int totalPages = 0;
        try {
            pageNo = Integer.parseInt(stringValue(payload.get("pageNo")));
        } catch (Exception ignored) {
        }
        if (pageNo <= 0) {
            try {
                pageNo = Integer.parseInt(stringValue(data.get("pageNo")));
            } catch (Exception ignored) {
            }
        }
        try {
            totalPages = Integer.parseInt(stringValue(payload.get("totalPages")));
        } catch (Exception ignored) {
        }
        if (totalPages <= 0) {
            try {
                totalPages = Integer.parseInt(stringValue(data.get("totalPages")));
            } catch (Exception ignored) {
            }
        }
        if (pageNo <= 0) pageNo = 1;
        if (totalPages <= 0) totalPages = 1;

        String baseName = (projectPrefix.isEmpty() ? "" : (projectPrefix + "_")) + templateBaseName;
        String fileName = baseName + "_P" + pageNo + "of" + totalPages + "." + ext;
        return dir.resolve(fileName);
    }

    private Map<String, Object> extractExcelData(Map<String, Object> payload) {
        if (payload == null) {
            throw new RuntimeException("导出失败：缺少数据");
        }
        Object dataObj = payload.get("data");
        if (dataObj instanceof Map) {
            return (Map<String, Object>) dataObj;
        }
        Object dataJsonObj = payload.get("dataJson");
        if (dataJsonObj != null) {
            try {
                return objectMapper.readValue(String.valueOf(dataJsonObj), new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                throw new RuntimeException("导出失败：dataJson解析失败");
            }
        }
        return payload;
    }

    private void fillWorkbook(Workbook workbook, Map<String, Object> data) {
        if (workbook == null) return;
        for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
            Sheet sheet = workbook.getSheetAt(s);
            if (sheet == null) continue;

            fillByLabel(sheet, "工程部位", stringValue(data.get("constructionPart")));
            fillByLabel(sheet, "试验日期", stringValue(data.get("testDate")));
            fillByLabel(sheet, "依据标准", stringValue(data.get("standard")));
            fillByLabel(sheet, "最大干密度", stringValue(data.get("maxDryDensity")));
            fillByLabel(sheet, "最小干密度", stringValue(data.get("minDryDensity")));
            fillByLabel(sheet, "设计相对密度", stringValue(data.get("relativeDensity")));
            fillByLabel(sheet, "统一编号", stringValue(data.get("unifiedNumber")));
            fillByLabel(sheet, "水的密度", stringValue(data.get("waterDensity")));
            fillByLabel(sheet, "仪器设备", stringValue(data.get("equipment")));
            fillByLabel(sheet, "备注", stringValue(data.get("remarks")));

            replacePlaceholders(sheet, data);
        }
    }

    private void fillByLabel(Sheet sheet, String label, String value) {
        if (value == null) value = "";
        String normalizedLabel = normalize(label);
        if (normalizedLabel.isEmpty()) return;

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell == null) continue;
                String text = readCellString(cell);
                if (text == null) continue;
                if (!normalize(text).equals(normalizedLabel)) continue;

                int targetCol = cell.getColumnIndex() + 1;
                setCellString(sheet, row, targetCol, value);
                return;
            }
        }
    }

    private void replacePlaceholders(Sheet sheet, Map<String, Object> data) {
        if (data == null || data.isEmpty()) return;
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell == null) continue;
                if (cell.getCellType() != CellType.STRING) continue;
                String text = cell.getStringCellValue();
                if (text == null || text.isEmpty()) continue;
                String replaced = text;
                for (Map.Entry<String, Object> e : data.entrySet()) {
                    String key = e.getKey();
                    if (key == null || key.isEmpty()) continue;
                    String val = stringValue(e.getValue());
                    replaced = replaced.replace("{{" + key + "}}", val);
                    replaced = replaced.replace("${" + key + "}", val);
                }
                if (!replaced.equals(text)) {
                    cell.setCellValue(replaced);
                }
            }
        }
    }

    private void setCellString(Sheet sheet, Row row, int col, String value) {
        if (row == null) return;
        int[] anchor = mergedRegionAnchor(sheet, row.getRowNum(), col);
        Row anchorRow = sheet.getRow(anchor[0]);
        if (anchorRow == null) anchorRow = sheet.createRow(anchor[0]);
        Cell targetCell = anchorRow.getCell(anchor[1]);
        if (targetCell == null) targetCell = anchorRow.createCell(anchor[1], CellType.STRING);
        targetCell.setCellType(CellType.STRING);
        targetCell.setCellValue(value == null ? "" : value);
    }

    private int[] mergedRegionAnchor(Sheet sheet, int row, int col) {
        if (sheet == null) return new int[]{row, col};
        for (CellRangeAddress region : sheet.getMergedRegions()) {
            if (region.isInRange(row, col)) {
                return new int[]{region.getFirstRow(), region.getFirstColumn()};
            }
        }
        return new int[]{row, col};
    }

    private String readCellString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf(cell.getNumericCellValue());
        if (cell.getCellType() == CellType.BOOLEAN) return String.valueOf(cell.getBooleanCellValue());
        if (cell.getCellType() == CellType.FORMULA) {
            try {
                return cell.getStringCellValue();
            } catch (Exception ignored) {
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception ignored2) {
                    return null;
                }
            }
        }
        return null;
    }

    private void fillDocxDocument(XWPFDocument doc, Map<String, Object> data) {
        if (doc == null || data == null || data.isEmpty()) return;

        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceDocxParagraphText(p, data);
        }

        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                if (row == null) continue;
                for (XWPFTableCell cell : row.getTableCells()) {
                    if (cell == null) continue;
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replaceDocxParagraphText(p, data);
                    }
                }
            }
        }
    }

    private void replaceDocxParagraphText(XWPFParagraph paragraph, Map<String, Object> data) {
        if (paragraph == null) return;
        String text = paragraph.getText();
        if (text == null || text.isEmpty()) return;

        String replaced = text;
        for (Map.Entry<String, Object> e : data.entrySet()) {
            String key = e.getKey();
            if (key == null || key.isEmpty()) continue;
            String val = stringValue(e.getValue());
            replaced = replaced.replace("{{" + key + "}}", val);
            replaced = replaced.replace("${" + key + "}", val);
        }
        if (replaced.equals(text)) return;

        int runCount = paragraph.getRuns() == null ? 0 : paragraph.getRuns().size();
        for (int i = runCount - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
        paragraph.createRun().setText(replaced, 0);
    }

    private String sanitizeFileName(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.isEmpty()) return "";
        return t.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String normalize(String s) {
        if (s == null) return "";
        String t = s.replace("\u00A0", " ").trim();
        t = t.replaceAll("[\\s:：()（）]", "");
        return t.toLowerCase(Locale.ROOT);
    }

    private String stringValue(Object v) {
        if (v == null) return "";
        return String.valueOf(v);
    }
}
