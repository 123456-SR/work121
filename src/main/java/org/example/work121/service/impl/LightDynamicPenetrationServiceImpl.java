package org.example.work121.service.impl;

import org.example.work121.entity.LightDynamicPenetration;
import org.example.work121.entity.LightDynamicPenetrationReport;
import org.example.work121.entity.LightDynamicPenetrationResult;
import org.example.work121.mapper.LightDynamicPenetrationMapper;
import org.example.work121.mapper.LightDynamicPenetrationResultMapper;
import org.example.work121.service.LightDynamicPenetrationService;
import org.example.work121.service.TableGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class LightDynamicPenetrationServiceImpl implements LightDynamicPenetrationService {

    @Autowired
    private LightDynamicPenetrationMapper mapper;

    @Autowired
    private org.example.work121.mapper.LightDynamicPenetrationReportMapper reportMapper;

    @Autowired
    private LightDynamicPenetrationResultMapper resultMapper;

    @Autowired
    private org.example.work121.mapper.JcCoreWtInfoMapper jcCoreWtInfoMapper;

    @Autowired
    private TableGenerationService tableGenerationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LightDynamicPenetration getById(String id) {
        LightDynamicPenetration entity = mapper.selectById(id);
        if (entity != null && entity.getDataJson() != null && !entity.getDataJson().isEmpty()) {
            try {
                Map<String, Object> jsonData = objectMapper.readValue(entity.getDataJson(), Map.class);
                if (jsonData != null) {
                    if (jsonData.get("clientUnit") != null) entity.setClientUnit((String) jsonData.get("clientUnit"));
                    if (jsonData.get("projectName") != null) entity.setProjectName((String) jsonData.get("projectName"));
                    if (jsonData.get("commissionDate") != null) {
                        Object val = jsonData.get("commissionDate");
                        if (val instanceof Long) entity.setCommissionDate(new java.util.Date((Long) val));
                    }
                    if (jsonData.get("constructionPart") != null) entity.setConstructionPart((String) jsonData.get("constructionPart"));
                    if (jsonData.get("testDate") != null) {
                        Object val = jsonData.get("testDate");
                        if (val instanceof Long) entity.setTestDate(new java.util.Date((Long) val));
                    }
                    if (jsonData.get("soilProperty") != null) {
                        entity.setSoilProperty((String) jsonData.get("soilProperty"));
                    } else if (jsonData.get("soilProperties") != null) {
                        entity.setSoilProperty((String) jsonData.get("soilProperties"));
                    }
                    if (jsonData.get("reportDate") != null) {
                        Object val = jsonData.get("reportDate");
                        if (val instanceof Long) entity.setReportDate(new java.util.Date((Long) val));
                    }
                    if (jsonData.get("witnessUnit") != null) entity.setWitnessUnit((String) jsonData.get("witnessUnit"));
                    if (jsonData.get("witness") != null) entity.setWitness((String) jsonData.get("witness"));
                    if (jsonData.get("designCapacity") != null) entity.setDesignCapacity((String) jsonData.get("designCapacity"));
                    if (jsonData.get("hammerWeight") != null) entity.setHammerWeight((String) jsonData.get("hammerWeight"));
                    if (jsonData.get("dropDistance") != null) entity.setDropDistance((String) jsonData.get("dropDistance"));
                    if (jsonData.get("testCategory") != null) entity.setTestCategory((String) jsonData.get("testCategory"));
                    if (jsonData.get("testBasis") != null) entity.setTestBasis((String) jsonData.get("testBasis"));
                    if (jsonData.get("equipment") != null) entity.setEquipment((String) jsonData.get("equipment"));
                    if (jsonData.get("remarks") != null) entity.setRemarks((String) jsonData.get("remarks"));
                    if (jsonData.get("conclusion") != null) entity.setConclusion((String) jsonData.get("conclusion"));
                    if (jsonData.get("constructionUnit") != null) entity.setConstructionUnit((String) jsonData.get("constructionUnit"));
                }
            } catch (Exception e) {
                System.err.println("Error parsing DATA_JSON: " + e.getMessage());
            }
        }
        return entity;
    }

    @Override
    public List<LightDynamicPenetration> getByEntrustmentId(String entrustmentId) {
        // 兼容前端传 WT_NUM 或 WT_ID：若传入的是 WT_NUM，则先解析出 WT_ID
        String key = entrustmentId;
        String resolvedId = resolveEntrustmentIdToWtId(key);
        if (resolvedId == null || resolvedId.trim().isEmpty()) {
            resolvedId = key;
        }
        List<LightDynamicPenetration> list = mapper.selectByEntrustmentId(resolvedId);
        if ((list == null || list.isEmpty()) && key != null && !key.trim().isEmpty() && resolvedId != null && !resolvedId.equals(key)) {
            list = mapper.selectByEntrustmentId(key);
        }
        if (list == null || list.isEmpty()) {
            LightDynamicPenetration entity = new LightDynamicPenetration();
            entity.setId(null);
            entity.setEntrustmentId(resolvedId);
            entity.setStatus("0");

            Map<String, Object> entrustmentData = tableGenerationService.getEntrustmentData(key);
            if ((entrustmentData == null || entrustmentData.isEmpty()) && resolvedId != null && key != null && !resolvedId.equals(key)) {
                entrustmentData = tableGenerationService.getEntrustmentData(resolvedId);
            }

            if (entrustmentData != null && !entrustmentData.isEmpty()) {
                entity.setWtNum((String) entrustmentData.get("wtNum"));
                entity.setClientUnit((String) entrustmentData.get("clientUnit"));
                entity.setProjectName((String) entrustmentData.get("projectName"));
                entity.setCommissionDate((java.util.Date) entrustmentData.get("commissionDate"));
                entity.setConstructionPart((String) entrustmentData.get("constructionPart"));
                entity.setTestCategory((String) entrustmentData.get("testCategory"));
                entity.setTester((String) entrustmentData.get("tester"));
                entity.setReviewer((String) entrustmentData.get("reviewer"));
                entity.setApprover((String) entrustmentData.get("approver"));
                entity.setSampleName((String) entrustmentData.get("sampleName"));
                entity.setTestBasis((String) entrustmentData.get("testBasis"));
                entity.setEquipment((String) entrustmentData.get("equipment"));
                entity.setRemarks((String) entrustmentData.get("remarks"));
                entity.setWitness((String) entrustmentData.get("witness"));
                entity.setWitnessUnit((String) entrustmentData.get("witnessUnit"));
            } else {
                entity.setWtNum(key);
            }

            return Collections.singletonList(entity);
        }
        return list;
    }

    @Override
    @Transactional
    public void save(LightDynamicPenetration entity) {
        try {
            if (entity != null && entity.getEntrustmentId() != null && !entity.getEntrustmentId().trim().isEmpty()) {
                entity.setEntrustmentId(resolveEntrustmentIdToWtId(entity.getEntrustmentId()));
            }

            // Prepare data map for DATA_JSON
            Map<String, Object> recordData = new HashMap<>();
            
            // Parse existing DATA_JSON if present
            if (entity.getDataJson() != null && !entity.getDataJson().isEmpty()) {
                try {
                    Map<String, Object> existingJson = objectMapper.readValue(entity.getDataJson(), Map.class);
                    if (existingJson != null) {
                        recordData.putAll(existingJson);
                        // Back-fill entity fields from JSON if missing
                        if (entity.getSoilProperty() == null && existingJson.get("soilProperties") != null) {
                            entity.setSoilProperty((String) existingJson.get("soilProperties"));
                        }
                        if (entity.getCommissionDate() == null && existingJson.get("entrustDate") != null) {
                            // Date conversion might be tricky here, skipping for now or handle string dates
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing record JSON: " + e.getMessage());
                }
            }
            
            // Add all business fields to DATA_JSON
            recordData.put("clientUnit", entity.getClientUnit());
            recordData.put("projectName", entity.getProjectName());
            recordData.put("commissionDate", entity.getCommissionDate());
            recordData.put("constructionPart", entity.getConstructionPart());
            recordData.put("testDate", entity.getTestDate());
            recordData.put("soilProperty", entity.getSoilProperty());
            recordData.put("reportDate", entity.getReportDate());
            recordData.put("witnessUnit", entity.getWitnessUnit());
            recordData.put("witness", entity.getWitness());
            recordData.put("designCapacity", entity.getDesignCapacity());
            recordData.put("hammerWeight", entity.getHammerWeight());
            recordData.put("dropDistance", entity.getDropDistance());
            recordData.put("testCategory", entity.getTestCategory());
            recordData.put("testBasis", entity.getTestBasis());
            recordData.put("equipment", entity.getEquipment());
            recordData.put("remarks", entity.getRemarks());
            recordData.put("conclusion", entity.getConclusion());
            recordData.put("constructionUnit", entity.getConstructionUnit());
            
            // Serialize to JSON
            String mergedJson = objectMapper.writeValueAsString(recordData);
            entity.setDataJson(mergedJson);
            
            if (entity.getId() == null || entity.getId().trim().isEmpty()) {
                entity.setId(UUID.randomUUID().toString());
                mapper.insert(entity);
            } else {
                if (mapper.countById(entity.getId()) > 0) {
                    mapper.updateById(entity);
                } else {
                    mapper.insert(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving LightDynamicPenetration: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public org.example.work121.entity.LightDynamicPenetrationReport getReportByEntrustmentId(String entrustmentId) {
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        List<org.example.work121.entity.LightDynamicPenetrationReport> list = reportMapper.selectByEntrustmentId(resolvedId);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    @Override
    @Transactional
    public void saveReport(org.example.work121.entity.LightDynamicPenetrationReport report) {
        if (report.getId() != null && !report.getId().trim().isEmpty()) {
            reportMapper.updateById(report);
        } else {
            List<org.example.work121.entity.LightDynamicPenetrationReport> existingList = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
            if (existingList != null && !existingList.isEmpty()) {
                // If exists, update the first one (or all? Here we assume update by ID is preferred if we knew it, but here we update by EntrustmentId using update())
                // Actually reportMapper.update() updates by entrustmentId.
                reportMapper.update(report);
            } else {
                if (report.getId() == null || report.getId().trim().isEmpty()) {
                    report.setId(UUID.randomUUID().toString());
                }
                reportMapper.insert(report);
            }
        }
    }

    @Override
    public LightDynamicPenetrationResult getResultByEntrustmentId(String entrustmentId) {
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        List<LightDynamicPenetrationResult> list = resultMapper.selectByEntrustmentId(resolvedId);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    @Override
    @Transactional
    public void saveResult(LightDynamicPenetrationResult result) {
        if (result.getId() != null && !result.getId().trim().isEmpty()) {
            resultMapper.updateById(result);
        } else {
            List<LightDynamicPenetrationResult> existingList = resultMapper.selectByEntrustmentId(result.getEntrustmentId());
            if (existingList != null && !existingList.isEmpty()) {
                resultMapper.update(result);
            } else {
                if (result.getId() == null || result.getId().trim().isEmpty()) {
                    result.setId(UUID.randomUUID().toString());
                }
                resultMapper.insert(result);
            }
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        String resolvedId = resolveEntrustmentIdToWtId(entrustmentId);
        // Delegate to TableGenerationService which has the complete logic including proper field mapping
        tableGenerationService.generateReportAndResult("LIGHT_DYNAMIC_PENETRATION", resolvedId);
    }

    @Override
    public String exportRecordToExcel(Map<String, Object> payload) {
        String templatePath = "D:\\IDEA\\IntelliJ IDEA 2024.1\\workplaces\\work\\表\\动力触探记录表.xlsx";
        Path templateXlsxPath = Paths.get(templatePath);
        if (!Files.exists(templateXlsxPath)) {
            throw new RuntimeException("Excel模板不存在: " + templatePath);
        }

        Map<String, Object> data = extractExcelData(payload);
        Path outputXlsxPath = buildExcelOutputPath(templateXlsxPath, data, payload);
        try {
            byte[] bytes = Files.readAllBytes(templateXlsxPath);
            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
                fillExcelWorkbook(workbook, data);
                try (OutputStream out = Files.newOutputStream(outputXlsxPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                    workbook.write(out);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("写入Excel失败: " + e.getMessage(), e);
        }
        return outputXlsxPath.toString();
    }

    private Path buildExcelOutputPath(Path templatePath, Map<String, Object> data, Map<String, Object> payload) {
        Path dir = templatePath.getParent();
        if (dir == null) {
            throw new RuntimeException("Excel模板路径无效: " + templatePath);
        }

        String projectName = sanitizeFileName(stringValue(data.get("projectName")));
        String baseName = (projectName.isEmpty() ? "" : (projectName + "_")) + "动力触探记录表";
        String unifiedNumber = stringValue(data.get("unifiedNumber"));
        if (unifiedNumber.isEmpty()) {
            unifiedNumber = stringValue(payload.get("entrustmentId"));
        }
        unifiedNumber = sanitizeFileName(unifiedNumber);

        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String fileName = baseName
                + (unifiedNumber.isEmpty() ? "" : ("_" + unifiedNumber))
                + "_" + timestamp
                + ".xlsx";
        return dir.resolve(fileName);
    }

    private String sanitizeFileName(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.isEmpty()) return "";
        return t.replaceAll("[\\\\/:*?\"<>|]", "_");
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

    private void fillExcelWorkbook(Workbook workbook, Map<String, Object> data) {
        if (workbook == null) return;
        for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
            Sheet sheet = workbook.getSheetAt(s);
            if (sheet == null) continue;

            fillByLabel(sheet, "委托单位", stringValue(data.get("entrustingUnit")));
            fillByLabel(sheet, "统一编号", stringValue(data.get("unifiedNumber")));
            fillByLabel(sheet, "工程名称", stringValue(data.get("projectName")));
            fillByLabel(sheet, "委托日期", stringValue(data.get("commissionDate")));
            fillByLabel(sheet, "施工部位", stringValue(data.get("constructionPart")));
            fillByLabel(sheet, "检测日期", stringValue(data.get("testDate")));
            fillByLabel(sheet, "岩土性状", stringValue(data.get("soilProperties")));
            fillByLabel(sheet, "检测类别", stringValue(data.get("testCategory")));
            fillByLabel(sheet, "设计承载力", stringValue(data.get("designCapacity")));
            fillByLabel(sheet, "锤重量", stringValue(data.get("hammerWeight")));
            fillByLabel(sheet, "落距", stringValue(data.get("dropDistance")));
            fillByLabel(sheet, "检测依据", stringValue(data.get("testBasis")));
            fillByLabel(sheet, "仪器设备", stringValue(data.get("equipment")));
            fillByLabel(sheet, "检测结论", stringValue(data.get("conclusion")));
            fillByLabel(sheet, "备注", stringValue(data.get("remarks")));

            fillPenetrationTable(sheet, data);
            replacePlaceholders(sheet, data);
        }
    }

    private void fillPenetrationTable(Sheet sheet, Map<String, Object> data) {
        int headerRowIdx = findRowIndexContains(sheet, "贯入深度");
        if (headerRowIdx < 0) headerRowIdx = findRowIndexContains(sheet, "锤击数");
        if (headerRowIdx < 0) return;

        Row headerRow = sheet.getRow(headerRowIdx);
        if (headerRow == null) return;

        Integer leftPosCol = findNthColIndexInRow(headerRow, "测点位置", 1);
        Integer leftDepthCol = findNthColIndexInRow(headerRow, "贯入深度", 1);
        Integer leftActualCol = findNthColIndexInRow(headerRow, "锤击数", 1);
        Integer leftAvgCol = findNthColIndexInRow(headerRow, "平均锤击数", 1);
        Integer leftCapacityCol = findNthColIndexInRow(headerRow, "承载力", 1);

        Integer rightPosCol = findNthColIndexInRow(headerRow, "测点位置", 2);
        Integer rightDepthCol = findNthColIndexInRow(headerRow, "贯入深度", 2);
        Integer rightActualCol = findNthColIndexInRow(headerRow, "锤击数", 2);
        Integer rightAvgCol = findNthColIndexInRow(headerRow, "平均锤击数", 2);
        Integer rightCapacityCol = findNthColIndexInRow(headerRow, "承载力", 2);

        int totalRows = countConsecutiveDataRows(sheet, headerRowIdx + 1);
        if (totalRows <= 0) return;

        int rowsPerBlock = 7;
        int blocks = totalRows / rowsPerBlock;
        if (blocks <= 0) blocks = 1;
        int maxDataRowIdx = headerRowIdx + totalRows;

        for (int b = 0; b < blocks; b++) {
            int baseRowIdx = headerRowIdx + 1 + b * rowsPerBlock;
            if (baseRowIdx > maxDataRowIdx) break;
            Row firstRow = sheet.getRow(baseRowIdx);
            if (firstRow == null) firstRow = sheet.createRow(baseRowIdx);

            if (leftPosCol != null) {
                setCellString(sheet, firstRow, leftPosCol, joinLines(
                        stringValue(data.get("pos_L_" + b)),
                        stringValue(data.get("pos_L2_" + b)),
                        stringValue(data.get("pos_L3_" + b))
                ));
            }
            if (leftAvgCol != null) setCellString(sheet, firstRow, leftAvgCol, stringValue(data.get("avg_L_" + b)));
            if (leftCapacityCol != null) setCellString(sheet, firstRow, leftCapacityCol, stringValue(data.get("capacity_L_" + b)));

            if (rightPosCol != null) {
                setCellString(sheet, firstRow, rightPosCol, joinLines(
                        stringValue(data.get("pos_R_" + b)),
                        stringValue(data.get("pos_R2_" + b)),
                        stringValue(data.get("pos_R3_" + b))
                ));
            }
            if (rightAvgCol != null) setCellString(sheet, firstRow, rightAvgCol, stringValue(data.get("avg_R_" + b)));
            if (rightCapacityCol != null) setCellString(sheet, firstRow, rightCapacityCol, stringValue(data.get("capacity_R_" + b)));

            for (int r = 0; r < rowsPerBlock; r++) {
                int rowIdx = baseRowIdx + r;
                if (rowIdx > maxDataRowIdx) break;
                Row row = sheet.getRow(rowIdx);
                if (row == null) row = sheet.createRow(rowIdx);

                int dataIdx = b * 7 + r;
                if (leftDepthCol != null) setCellString(sheet, row, leftDepthCol, stringValue(data.get("depth_L_" + dataIdx)));
                if (leftActualCol != null) setCellString(sheet, row, leftActualCol, stringValue(data.get("actual_L_" + dataIdx)));
                if (rightDepthCol != null) setCellString(sheet, row, rightDepthCol, stringValue(data.get("depth_R_" + dataIdx)));
                if (rightActualCol != null) setCellString(sheet, row, rightActualCol, stringValue(data.get("actual_R_" + dataIdx)));
            }
        }
    }

    private int countConsecutiveDataRows(Sheet sheet, int startRowIdx) {
        int count = 0;
        int last = sheet.getLastRowNum();
        for (int r = startRowIdx; r <= last; r++) {
            Row row = sheet.getRow(r);
            if (row == null) break;
            if (rowContainsAny(row, "检测依据", "仪器设备", "检测结论", "备注")) break;
            count++;
        }
        return count;
    }

    private boolean rowContainsAny(Row row, String... containsText) {
        if (row == null || containsText == null || containsText.length == 0) return false;
        for (Cell cell : row) {
            String text = readCellString(cell);
            if (text == null || text.isEmpty()) continue;
            String norm = normalize(text);
            for (String t : containsText) {
                if (normalize(t).isEmpty()) continue;
                if (norm.contains(normalize(t))) return true;
            }
        }
        return false;
    }

    private String joinLines(String... parts) {
        if (parts == null || parts.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p == null) continue;
            String t = p.trim();
            if (t.isEmpty()) continue;
            if (sb.length() > 0) sb.append("\n");
            sb.append(t);
        }
        return sb.toString();
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

    private Integer findNthColIndexInRow(Row row, String headerLike, int nth) {
        if (row == null) return null;
        String target = normalize(headerLike);
        if (target.isEmpty()) return null;
        if (nth <= 0) return null;
        short lastCell = row.getLastCellNum();
        int hit = 0;
        for (int c = 0; c < lastCell; c++) {
            Cell cell = row.getCell(c);
            String text = readCellString(cell);
            if (text == null || text.isEmpty()) continue;
            String norm = normalize(text);
            if (norm.contains(target)) {
                hit++;
                if (hit == nth) return c;
            }
        }
        return null;
    }

    private int findRowIndexContains(Sheet sheet, String containsText) {
        String target = normalize(containsText);
        if (target.isEmpty()) return -1;
        int last = sheet.getLastRowNum();
        for (int r = 0; r <= last; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            for (Cell cell : row) {
                String text = readCellString(cell);
                if (text == null || text.isEmpty()) continue;
                if (normalize(text).contains(target)) return r;
            }
        }
        return -1;
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

    /**
     * 解析“委托ID”入参：兼容传 WT_NUM 或 WT_ID，最终返回 WT_ID
     */
    private String resolveEntrustmentIdToWtId(String entrustmentIdOrWtNum) {
        if (entrustmentIdOrWtNum == null || entrustmentIdOrWtNum.trim().isEmpty()) {
            return entrustmentIdOrWtNum;
        }
        String key = entrustmentIdOrWtNum.trim();
        try {
            // 若传入的是 WT_NUM，则可通过 selectByWtNum 得到 WT_ID
            List<org.example.work121.entity.JcCoreWtInfo> list = jcCoreWtInfoMapper.selectByWtNum(key);
            if (list != null && !list.isEmpty() && list.get(0) != null && list.get(0).getId() != null && !list.get(0).getId().trim().isEmpty()) {
                return list.get(0).getId();
            }
        } catch (Exception e) {
            // ignore, fallback to original
        }
        return key;
    }
}
