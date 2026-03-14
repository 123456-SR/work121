package org.example.work121.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.work121.entity.JcCoreWtInfo;
import org.example.work121.entity.NuclearDensity;
import org.example.work121.entity.SimpleDirectory;
import org.example.work121.mapper.NuclearDensityMapper;
import org.example.work121.mapper.SimpleDirectoryMapper;
import org.example.work121.service.JcCoreWtInfoService;
import org.example.work121.service.NuclearDensityService;
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

@Service
public class NuclearDensityServiceImpl implements NuclearDensityService {

    @Autowired
    private NuclearDensityMapper mapper;

    @Autowired
    private org.example.work121.mapper.NuclearDensityReportMapper reportMapper;

    @Autowired
    private SimpleDirectoryMapper simpleDirectoryMapper;

    @Autowired
    private JcCoreWtInfoService jcCoreWtInfoService;

    @Autowired
    private TableGenerationService tableGenerationService;

    @Override
    public List<NuclearDensity> getByEntrustmentId(String entrustmentId) {
        return mapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    @Transactional
    public void save(NuclearDensity nuclearDensity) {
        java.util.Date now = new java.util.Date();
        if (nuclearDensity.getCreateTime() == null) {
            nuclearDensity.setCreateTime(now);
        }
        nuclearDensity.setUpdateTime(now);

        populateRolesFromDirectory(nuclearDensity);

        String id = nuclearDensity.getId();
        boolean hasId = id != null && !id.trim().isEmpty();

        if (hasId && mapper.selectById(id) != null) {
            NuclearDensity existing = mapper.selectById(id);
            if (nuclearDensity.getTester() == null) {
                nuclearDensity.setTester(existing.getTester());
            }
            if (nuclearDensity.getReviewer() == null) {
                nuclearDensity.setReviewer(existing.getReviewer());
            }
            if (nuclearDensity.getApprover() == null) {
                nuclearDensity.setApprover(existing.getApprover());
            }
            if (nuclearDensity.getFiller() == null) {
                nuclearDensity.setFiller(existing.getFiller());
            }
            if (nuclearDensity.getRecordTester() == null) {
                nuclearDensity.setRecordTester(existing.getRecordTester());
            }
            if (nuclearDensity.getRecordReviewer() == null) {
                nuclearDensity.setRecordReviewer(existing.getRecordReviewer());
            }
            if (nuclearDensity.getRecordReviewSign() == null) {
                nuclearDensity.setRecordReviewSign(existing.getRecordReviewSign());
            }
            if (nuclearDensity.getStatus() == null) {
                nuclearDensity.setStatus(existing.getStatus());
            }
            if (nuclearDensity.getRejectReason() == null) {
                nuclearDensity.setRejectReason(existing.getRejectReason());
            }
            if (nuclearDensity.getNextHandler() == null) {
                nuclearDensity.setNextHandler(existing.getNextHandler());
            }
            if (nuclearDensity.getCreateBy() == null) {
                nuclearDensity.setCreateBy(existing.getCreateBy());
            }
            if (nuclearDensity.getCreateTime() == null) {
                nuclearDensity.setCreateTime(existing.getCreateTime());
            }
            mapper.updateById(nuclearDensity);
        } else {
            if (!hasId) {
                nuclearDensity.setId(java.util.UUID.randomUUID().toString());
            }
            mapper.insert(nuclearDensity);
        }
    }

    private void populateRolesFromDirectory(NuclearDensity nuclearDensity) {
        if (nuclearDensity == null) return;
        String entrustmentId = nuclearDensity.getEntrustmentId();
        if (entrustmentId == null || entrustmentId.trim().isEmpty()) return;

        SimpleDirectory directory = simpleDirectoryMapper.selectByDirName(entrustmentId);
        if (directory != null) {
            if (nuclearDensity.getFiller() == null || nuclearDensity.getFiller().isEmpty()) {
                nuclearDensity.setFiller(directory.getJcTester());
            }
            if (nuclearDensity.getRecordTester() == null || nuclearDensity.getRecordTester().isEmpty()) {
                nuclearDensity.setRecordTester(directory.getJcTester());
            }
            if (nuclearDensity.getRecordReviewer() == null || nuclearDensity.getRecordReviewer().isEmpty()) {
                nuclearDensity.setRecordReviewer(directory.getJcReviewer());
            }
            if (nuclearDensity.getTester() == null || nuclearDensity.getTester().isEmpty()) {
                nuclearDensity.setTester(directory.getJcTester());
            }
            if (nuclearDensity.getReviewer() == null || nuclearDensity.getReviewer().isEmpty()) {
                nuclearDensity.setReviewer(directory.getJcReviewer());
            }
            if (nuclearDensity.getApprover() == null || nuclearDensity.getApprover().isEmpty()) {
                nuclearDensity.setApprover(directory.getBgApprover());
            }
        }

        JcCoreWtInfo entrustment = jcCoreWtInfoService.getByWtNum(entrustmentId);
        if (entrustment == null) return;

        if (nuclearDensity.getFiller() == null || nuclearDensity.getFiller().isEmpty()) {
            nuclearDensity.setFiller(entrustment.getFiller());
        }
        if (nuclearDensity.getRecordTester() == null || nuclearDensity.getRecordTester().isEmpty()) {
            nuclearDensity.setRecordTester(entrustment.getRecordTester());
        }
        if (nuclearDensity.getRecordReviewer() == null || nuclearDensity.getRecordReviewer().isEmpty()) {
            nuclearDensity.setRecordReviewer(entrustment.getRecordReviewer());
        }
        if (nuclearDensity.getTester() == null || nuclearDensity.getTester().isEmpty()) {
            nuclearDensity.setTester(entrustment.getTester());
        }
        if (nuclearDensity.getReviewer() == null || nuclearDensity.getReviewer().isEmpty()) {
            nuclearDensity.setReviewer(entrustment.getReviewer());
        }
        if (nuclearDensity.getApprover() == null || nuclearDensity.getApprover().isEmpty()) {
            nuclearDensity.setApprover(entrustment.getApprover());
        }
    }

    @Override
    public org.example.work121.entity.NuclearDensityReport getReportByEntrustmentId(String entrustmentId) {
        return reportMapper.selectByEntrustmentId(entrustmentId);
    }

    @Override
    @Transactional
    public void saveReport(org.example.work121.entity.NuclearDensityReport report) {
        org.example.work121.entity.NuclearDensityReport existing = reportMapper.selectByEntrustmentId(report.getEntrustmentId());
        if (existing != null) {
            reportMapper.update(report);
        } else {
            if (report.getId() == null) {
                report.setId(java.util.UUID.randomUUID().toString());
            }
            reportMapper.insert(report);
        }
    }

    @Override
    @Transactional
    public void generateReportAndResult(String entrustmentId) {
        // 与其它试验方法保持一致：先确保存在记录，然后交由 TableGenerationService 统一生成
        java.util.List<NuclearDensity> records = mapper.selectByEntrustmentId(entrustmentId);
        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Cannot generate NuclearDensity report/result: Record not found for entrustmentId " + entrustmentId);
        }

        tableGenerationService.generateReportAndResult("NUCLEAR_DENSITY", entrustmentId);
    }

    @Override
    public String exportRecordToExcel(Map<String, Object> payload) {
        String templatePath = "D:\\IDEA\\IntelliJ IDEA 2024.1\\workplaces\\work\\表\\核子法记录表.xlsx";
        Path templateXlsxPath = Paths.get(templatePath);
        if (!Files.exists(templateXlsxPath)) {
            throw new RuntimeException("Excel模板不存在: " + templatePath);
        }

        Map<String, Object> data = extractData(payload);
        Path outputXlsxPath = buildOutputPath(templateXlsxPath, data, payload);
        try {
            byte[] bytes = Files.readAllBytes(templateXlsxPath);
            try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
                fillWorkbook(workbook, data);
                try (OutputStream out = Files.newOutputStream(outputXlsxPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                    workbook.write(out);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("写入Excel失败: " + e.getMessage(), e);
        }
        return outputXlsxPath.toString();
    }

    private Path buildOutputPath(Path templatePath, Map<String, Object> data, Map<String, Object> payload) {
        Path dir = templatePath.getParent();
        if (dir == null) {
            throw new RuntimeException("Excel模板路径无效: " + templatePath);
        }

        String projectName = sanitizeFileName(stringValue(data.get("projectName")));
        String baseName = (projectName.isEmpty() ? "" : (projectName + "_")) + "核子法记录表";
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

    private Map<String, Object> extractData(Map<String, Object> payload) {
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
                return new ObjectMapper().readValue(String.valueOf(dataJsonObj), new TypeReference<Map<String, Object>>() {});
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

            fillByLabel(sheet, "委托单位", stringValue(data.get("entrustingUnit")));
            fillByLabel(sheet, "统一编号", stringValue(data.get("unifiedNumber")));
            fillByLabel(sheet, "工程名称", stringValue(data.get("projectName")));
            fillByLabel(sheet, "委托日期", stringValue(data.get("commissionDate")));
            fillByLabel(sheet, "施工部位", stringValue(data.get("constructionPart")));
            fillByLabel(sheet, "仪器设备", stringValue(data.get("equipment")));
            fillByLabel(sheet, "检测方法", stringValue(data.get("testMethod")));
            fillByLabel(sheet, "样品名称及状态", stringValue(data.get("sampleNameStatus")));
            fillByLabel(sheet, "依据标准", stringValue(data.get("standard")));
            fillByLabel(sheet, "设计压实度", stringValue(data.get("designCompaction")));
            fillByLabel(sheet, "最大干密度", stringValue(data.get("maxDryDensity")));
            fillByLabel(sheet, "最优含水率", stringValue(data.get("optimumMoisture")));
            fillByLabel(sheet, "最小干密度", stringValue(data.get("minDryDensity")));

            fillTable(sheet, data);
            replacePlaceholders(sheet, data);
        }
    }

    private void fillTable(Sheet sheet, Map<String, Object> data) {
        int headerRowIdx = findRowIndexContains(sheet, "样品编号");
        if (headerRowIdx < 0) return;

        Row headerRow = sheet.getRow(headerRowIdx);
        if (headerRow == null) return;

        Integer colSampleId = findColIndexInRow(headerRow, "样品编号");
        Integer colLocation = findColIndexInRow(headerRow, "检测部位");
        Integer colDate = findColIndexInRow(headerRow, "检测日期");
        Integer colWet = findColIndexInRow(headerRow, "湿密度");
        Integer colDry = findColIndexInRow(headerRow, "干密度");
        Integer colMoisture = findColIndexInRow(headerRow, "含水率");
        Integer colCompaction = findColIndexInRow(headerRow, "压实度");
        Integer colRemarks = findColIndexInRow(headerRow, "备注");

        for (int i = 0; i < 20; i++) {
            int rowIdx = headerRowIdx + 1 + i;
            Row row = sheet.getRow(rowIdx);
            if (row == null) row = sheet.createRow(rowIdx);

            if (colSampleId != null) setCellString(sheet, row, colSampleId, stringValue(data.get("sampleId_" + i)));
            if (colLocation != null) setCellString(sheet, row, colLocation, stringValue(data.get("location_" + i)));
            if (colDate != null) setCellString(sheet, row, colDate, stringValue(data.get("date_" + i)));
            if (colWet != null) setCellString(sheet, row, colWet, stringValue(data.get("wetDensity_" + i)));
            if (colDry != null) setCellString(sheet, row, colDry, stringValue(data.get("dryDensity_" + i)));
            if (colMoisture != null) setCellString(sheet, row, colMoisture, stringValue(data.get("moisture_" + i)));
            if (colCompaction != null) setCellString(sheet, row, colCompaction, stringValue(data.get("compaction_" + i)));
            if (colRemarks != null) setCellString(sheet, row, colRemarks, stringValue(data.get("remarks_" + i)));
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

    private Integer findColIndexInRow(Row row, String headerLike) {
        if (row == null) return null;
        String target = normalize(headerLike);
        if (target.isEmpty()) return null;
        short lastCell = row.getLastCellNum();
        for (int c = 0; c < lastCell; c++) {
            Cell cell = row.getCell(c);
            String text = readCellString(cell);
            if (text == null || text.isEmpty()) continue;
            String norm = normalize(text);
            if (norm.contains(target)) return c;
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
}
