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
import org.example.work121.entity.DensityTest;
import org.example.work121.entity.DensityTestReport;
import org.example.work121.mapper.DensityTestMapper;
import org.example.work121.mapper.DensityTestReportMapper;
import org.example.work121.service.DensityTestService;
import org.example.work121.service.TableGenerationService;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class DensityTestServiceImpl implements DensityTestService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DensityTestMapper mapper;

    @Autowired
    private DensityTestReportMapper reportMapper;

    @Autowired
    private TableGenerationService tableGenerationService;

    @Override
    public java.util.List<DensityTest> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void save(DensityTest densityTest) {
        if (densityTest.getId() != null && mapper.selectById(densityTest.getId()) != null) {
            mapper.updateById(densityTest);
        } else {
            if (densityTest.getId() == null) {
                densityTest.setId(UUID.randomUUID().toString());
            }
            mapper.insert(densityTest);
        }
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public DensityTestReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveReport(DensityTestReport report) {
        DensityTestReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
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
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        // 委托 + 记录都“已通过”且属于密度类时，才真正生成报告/结果
        // 具体条件和数据填充逻辑集中在 TableGenerationServiceImpl.generateDensityTestReportAndResult 中处理
        tableGenerationService.generateReportAndResult("DENSITY_TEST", entrustmentId);
    }

    @Override
    public String exportReportToExcel(Map<String, Object> payload) {
        if (payload == null) throw new RuntimeException("导出失败：缺少数据");

        String templateBaseName = stringValue(payload.get("templateBaseName"));
        if (templateBaseName.isEmpty()) templateBaseName = stringValue(payload.get("baseName"));
        if (templateBaseName.isEmpty()) templateBaseName = "原位密度报告表";

        String format = stringValue(payload.get("format")).toLowerCase(Locale.ROOT).trim();
        if (format.isEmpty()) format = "xlsx";

        Path templateDir = Paths.get(System.getProperty("user.dir"), "表");
        Path templatePath = templateDir.resolve(templateBaseName + "." + format);
        if (!Files.exists(templatePath)) {
            throw new RuntimeException("模板不存在，请先在“表”文件夹中导入模板: " + templatePath.toString());
        }

        List<Map<String, Object>> sheetData = extractSheetData(payload);
        Map<String, Object> commonData;
        if (sheetData != null && !sheetData.isEmpty() && sheetData.get(0) != null && !sheetData.get(0).isEmpty()) {
            commonData = sheetData.get(0);
        } else {
            commonData = extractExcelData(payload);
        }

        int pageNo = safeInt(payload.get("pageNo"), 1);
        int totalPages = safeInt(payload.get("totalPages"), 1);
        if (pageNo <= 0) pageNo = 1;
        if (totalPages <= 0) totalPages = 1;

        if (commonData != null) {
            commonData.put("pageNo", pageNo);
            commonData.put("totalPages", totalPages);
        }
        if (sheetData != null) {
            for (Map<String, Object> m : sheetData) {
                if (m == null) continue;
                m.put("pageNo", pageNo);
                m.put("totalPages", totalPages);
            }
        }

        Path outputPath = buildOutputPath(templatePath, commonData, payload, format, templateBaseName);

        try {
            byte[] bytes = Files.readAllBytes(templatePath);
            if ("xlsx".equals(format) || "xls".equals(format)) {
                try (Workbook workbook = "xls".equals(format)
                        ? new HSSFWorkbook(new ByteArrayInputStream(bytes))
                        : new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
                    fillWorkbook(workbook, commonData, sheetData, pageNo, totalPages);
                    try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                        workbook.write(out);
                    }
                }
            } else if ("docx".equals(format)) {
                try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
                    fillDocxDocument(doc, commonData);
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

    private List<Map<String, Object>> extractSheetData(Map<String, Object> payload) {
        Object v = payload.get("sheetData");
        if (!(v instanceof List)) return null;
        return (List<Map<String, Object>>) v;
    }

    private Map<String, Object> extractExcelData(Map<String, Object> payload) {
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

    private Path buildOutputPath(Path templatePath, Map<String, Object> data, Map<String, Object> payload, String ext, String templateBaseName) {
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

    private void fillWorkbook(Workbook workbook, Map<String, Object> commonData, List<Map<String, Object>> sheetData, int pageNo, int totalPages) {
        if (workbook == null) return;
        for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
            Sheet sheet = workbook.getSheetAt(s);
            if (sheet == null) continue;

            Map<String, Object> data = (sheetData != null && s < sheetData.size() && sheetData.get(s) != null)
                    ? sheetData.get(s)
                    : commonData;

            fillByLabel(sheet, "委托单位", firstNonEmpty(stringValue(data.get("clientUnit")), stringValue(data.get("entrustingUnit"))));
            fillByLabel(sheet, "统一编号", stringValue(data.get("unifiedNumber")));
            fillByLabel(sheet, "工程名称", stringValue(data.get("projectName")));
            fillByLabel(sheet, "委托日期", stringValue(data.get("commissionDate")));
            fillByLabel(sheet, "施工部位", stringValue(data.get("constructionPart")));
            fillByLabel(sheet, "检测日期", stringValue(data.get("testDate")));
            fillByLabel(sheet, "样品名称及状态", stringValue(data.get("sampleNameStatus")));
            fillByLabel(sheet, "报告日期", stringValue(data.get("reportDate")));
            fillByLabel(sheet, "仪器设备", stringValue(data.get("equipment")));
            fillByLabel(sheet, "检测方法", stringValue(data.get("testMethod")));
            fillByLabel(sheet, "检测依据", stringValue(data.get("testBasis")));
            fillByLabel(sheet, "检测类别", stringValue(data.get("testCategory")));
            fillByLabel(sheet, "最大干密度", stringValue(data.get("maxDryDensity")));
            fillByLabel(sheet, "最优含水率", stringValue(data.get("optimumMoisture")));
            fillByLabel(sheet, "最小干密度", stringValue(data.get("minDryDensity")));
            fillByLabel(sheet, "设计指标", stringValue(data.get("designIndex")));
            fillByLabel(sheet, "检测结果", stringValue(data.get("testResult")));
            fillByLabel(sheet, "依据标准", firstNonEmpty(stringValue(data.get("testBasis")), stringValue(data.get("standard"))));

            fillTable(sheet, data);
            fillPageText(sheet, pageNo, totalPages);

            replacePlaceholders(sheet, data);
        }
    }

    private void fillPageText(Sheet sheet, int pageNo, int totalPages) {
        if (sheet == null) return;
        if (pageNo <= 0) pageNo = 1;
        if (totalPages <= 0) totalPages = 1;
        String target = "第 " + pageNo + " 页，共 " + totalPages + " 页";
        for (Row row : sheet) {
            if (row == null) continue;
            for (Cell cell : row) {
                if (cell == null) continue;
                if (cell.getCellType() != CellType.STRING) continue;
                String text = cell.getStringCellValue();
                if (text == null || text.isEmpty()) continue;
                if (!"第页共页".equals(normalize(text))) continue;
                cell.setCellValue(target);
                return;
            }
        }
    }

    private void fillTable(Sheet sheet, Map<String, Object> data) {
        if (sheet == null || data == null) return;
        int headerRowIdx = findRowIndexContains(sheet, "样品编号");
        if (headerRowIdx < 0) return;

        Row headerRow = sheet.getRow(headerRowIdx);
        if (headerRow == null) return;

        Integer colSampleId = findColIndexInRowFlexible(headerRow, "样品编号");
        Integer colLocation = firstNonNull(findColIndexInRowFlexible(headerRow, "检测部位"), findColIndexInRowFlexible(headerRow, "检测地点"));
        Integer colDate = findColIndexInRowFlexible(headerRow, "检测日期");
        Integer colWet = findColIndexInRowFlexible(headerRow, "湿密度");
        Integer colDry = findColIndexInRowFlexible(headerRow, "干密度");
        Integer colMoisture = findColIndexInRowFlexible(headerRow, "含水率");
        Integer colAvgDry = firstNonNull(findColIndexInRowFlexible(headerRow, "平均干密度"), findColIndexInRowFlexible(headerRow, "平均实测干密度"));
        Integer colCompaction = findColIndexInRowFlexible(headerRow, "压实度");
        Integer colRemarks = findColIndexInRowFlexible(headerRow, "备注");

        int startRow = headerRowIdx + 1;
        int rowStep = 1;
        if (colSampleId != null) {
            CellRangeAddress region = findMergedRegion(sheet, startRow, colSampleId);
            if (region != null && region.getFirstRow() <= startRow) {
                int height = region.getLastRow() - region.getFirstRow() + 1;
                if (height > 0) rowStep = height;
            }
        }
        if (rowStep <= 0) rowStep = 1;

        for (int i = 0; i < 20; i++) {
            int baseRowIdx = startRow + i * rowStep;
            if (baseRowIdx > sheet.getLastRowNum()) break;
            if (rowStep > 1 && colSampleId != null) {
                CellRangeAddress sampleRegion = findMergedRegion(sheet, baseRowIdx, colSampleId);
                if (sampleRegion == null || sampleRegion.getFirstRow() != baseRowIdx) break;
            }

            Row baseRow = sheet.getRow(baseRowIdx);
            if (baseRow == null) baseRow = sheet.createRow(baseRowIdx);

            if (colSampleId != null) setCellString(sheet, baseRow, colSampleId, stringValue(data.get("sampleId_" + i)));
            if (colLocation != null) setCellString(sheet, baseRow, colLocation, stringValue(data.get("location_" + i)));
            if (colDate != null) setCellString(sheet, baseRow, colDate, stringValue(data.get("date_" + i)));
            if (colAvgDry != null) setCellString(sheet, baseRow, colAvgDry, stringValue(data.get("avgDryDensity_" + i)));
            if (colCompaction != null) setCellString(sheet, baseRow, colCompaction, stringValue(data.get("compaction_" + i)));
            if (colRemarks != null) setCellString(sheet, baseRow, colRemarks, stringValue(data.get("remarks_" + i)));

            fillDoubleRowCell(sheet, baseRowIdx, rowStep, colWet, stringValue(data.get("wetDensity_" + i)), stringValue(data.get("wetDensity2_" + i)));
            fillDoubleRowCell(sheet, baseRowIdx, rowStep, colDry, stringValue(data.get("dryDensity_" + i)), stringValue(data.get("dryDensity2_" + i)));
            fillDoubleRowCell(sheet, baseRowIdx, rowStep, colMoisture, stringValue(data.get("moisture_" + i)), stringValue(data.get("moisture2_" + i)));
        }
    }

    private void fillDoubleRowCell(Sheet sheet, int baseRowIdx, int rowStep, Integer col, String v1, String v2) {
        if (sheet == null) return;
        if (col == null) return;
        Row row1 = sheet.getRow(baseRowIdx);
        if (row1 == null) row1 = sheet.createRow(baseRowIdx);
        setCellString(sheet, row1, col, v1);

        if (v2 == null || v2.trim().isEmpty()) return;
        if (rowStep <= 1) return;

        CellRangeAddress r1 = findMergedRegion(sheet, baseRowIdx, col);
        int nextRow = (r1 != null ? (r1.getLastRow() + 1) : (baseRowIdx + 1));
        int blockEnd = baseRowIdx + rowStep - 1;
        if (nextRow > blockEnd) return;

        Row row2 = sheet.getRow(nextRow);
        if (row2 == null) row2 = sheet.createRow(nextRow);
        setCellString(sheet, row2, col, v2);
    }

    private int findRowIndexContains(Sheet sheet, String needle) {
        if (sheet == null) return -1;
        String n = normalize(needle);
        if (n.isEmpty()) return -1;
        for (Row row : sheet) {
            if (row == null) continue;
            for (Cell cell : row) {
                if (cell == null) continue;
                String text = readCellString(cell);
                if (text == null) continue;
                if (normalize(text).contains(n)) {
                    return row.getRowNum();
                }
            }
        }
        return -1;
    }

    private Integer findColIndexInRow(Row row, String headerText) {
        if (row == null) return null;
        String h = normalize(headerText);
        if (h.isEmpty()) return null;
        for (Cell cell : row) {
            if (cell == null) continue;
            String text = readCellString(cell);
            if (text == null) continue;
            if (normalize(text).equals(h)) {
                return cell.getColumnIndex();
            }
        }
        return null;
    }

    private Integer findColIndexInRowFlexible(Row row, String headerText) {
        Integer exact = findColIndexInRow(row, headerText);
        if (exact != null) return exact;
        if (row == null) return null;
        String h = normalize(headerText);
        if (h.isEmpty()) return null;
        for (Cell cell : row) {
            if (cell == null) continue;
            String text = readCellString(cell);
            if (text == null) continue;
            if (normalize(text).contains(h)) {
                return cell.getColumnIndex();
            }
        }
        return null;
    }

    private Integer firstNonNull(Integer a, Integer b) {
        return a != null ? a : b;
    }

    private int safeInt(Object v, int defaultValue) {
        if (v == null) return defaultValue;
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void fillByLabel(Sheet sheet, String label, String value) {
        if (sheet == null) return;
        if (value == null) value = "";
        String normalizedLabel = normalize(label);
        if (normalizedLabel.isEmpty()) return;

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell == null) continue;
                String text = readCellString(cell);
                if (text == null) continue;
                String normalizedText = normalize(text);
                if (!normalizedText.equals(normalizedLabel) && !normalizedText.contains(normalizedLabel)) continue;

                int rowNum = row.getRowNum();
                int labelCol = cell.getColumnIndex();
                int[] labelAnchor = mergedRegionAnchor(sheet, rowNum, labelCol);
                CellRangeAddress labelRegion = findMergedRegion(sheet, rowNum, labelCol);

                int targetCol = labelCol + 1;
                if (labelRegion != null && targetCol <= labelRegion.getLastColumn()) {
                    targetCol = labelRegion.getLastColumn() + 1;
                }

                int attempts = 0;
                while (attempts < 12) {
                    int[] targetAnchor = mergedRegionAnchor(sheet, rowNum, targetCol);
                    if (labelAnchor[0] != targetAnchor[0] || labelAnchor[1] != targetAnchor[1]) {
                        setCellString(sheet, row, targetCol, value);
                        return;
                    }
                    targetCol++;
                    attempts++;
                }

                Row nextRow = sheet.getRow(rowNum + 1);
                if (nextRow == null) nextRow = sheet.createRow(rowNum + 1);
                setCellString(sheet, nextRow, labelCol, value);
                return;
            }
        }
    }

    private CellRangeAddress findMergedRegion(Sheet sheet, int row, int col) {
        if (sheet == null) return null;
        for (CellRangeAddress region : sheet.getMergedRegions()) {
            if (region.isInRange(row, col)) {
                return region;
            }
        }
        return null;
    }

    private void replacePlaceholders(Sheet sheet, Map<String, Object> data) {
        if (sheet == null || data == null || data.isEmpty()) return;
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
        if (sheet == null || row == null) return;
        int[] anchor = mergedRegionAnchor(sheet, row.getRowNum(), col);
        Row anchorRow = sheet.getRow(anchor[0]);
        if (anchorRow == null) anchorRow = sheet.createRow(anchor[0]);
        Cell targetCell = anchorRow.getCell(anchor[1]);
        if (targetCell == null) targetCell = anchorRow.createCell(anchor[1], CellType.STRING);
        targetCell.setCellType(CellType.STRING);
        targetCell.setCellValue(value == null ? "" : value);
    }

    private int[] mergedRegionAnchor(Sheet sheet, int row, int col) {
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

    private String normalize(String s) {
        if (s == null) return "";
        String t = s.replace("\u00A0", " ").trim();
        t = t.replaceAll("[\\s:：()（）]", "");
        return t.toLowerCase(Locale.ROOT);
    }

    private String sanitizeFileName(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.isEmpty()) return "";
        return t.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String stringValue(Object v) {
        if (v == null) return "";
        return String.valueOf(v);
    }

    private String firstNonEmpty(String a, String b) {
        if (a != null && !a.trim().isEmpty()) return a;
        if (b != null && !b.trim().isEmpty()) return b;
        return "";
    }
}
