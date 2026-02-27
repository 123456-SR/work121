package org.example.work121.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

@Service
public class PdfGeneratorService {

    private void addBackgroundAndFooter(Document document, PdfWriter writer, String version, String date, String currentPage, String totalPages) {
        // Footer removed as per requirement
    }

    public byte[] generateEntrustmentPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

            addEntrustmentContent(document, request, chineseFont);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private void addEntrustmentContent(Document document, HttpServletRequest request, BaseFont chineseFont) throws DocumentException, IOException {
        Font titleFont = new Font(chineseFont, 18, Font.BOLD);
        Font labelFont = new Font(chineseFont, 10, Font.NORMAL);
        Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
        Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

        Paragraph title = new Paragraph("检测 (验) 委托单 (代合同)", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingBefore(5);
        headerTable.setSpacingAfter(5);
        headerTable.setWidths(new float[]{1, 1});

        String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";
        String sampleNumber = request.getParameter("sampleNumber") != null ? request.getParameter("sampleNumber") : "";

        PdfPCell cell1 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerTable.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Paragraph("样品编号：" + sampleNumber, valueFont));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTable.addCell(cell2);

        document.add(headerTable);

        PdfPTable mainTable = new PdfPTable(4);
        mainTable.setWidthPercentage(100);
        mainTable.setSpacingBefore(5);
        mainTable.setSpacingAfter(5);
        mainTable.setWidths(new float[]{1, 2, 1, 2});

        String clientUnit = request.getParameter("clientUnit") != null ? request.getParameter("clientUnit") : "";
        String clientDate = request.getParameter("clientDate") != null ? request.getParameter("clientDate") : "";
        String constructionUnit = request.getParameter("constructionUnit") != null ? request.getParameter("constructionUnit") : "";
        String buildingUnit = request.getParameter("buildingUnit") != null ? request.getParameter("buildingUnit") : "";

        addRow4Col(mainTable, "委托单位：", clientUnit, "委托日期：", clientDate, labelFont, valueFont);
        addRow4Col(mainTable, "施工单位：", constructionUnit, "建设单位：", buildingUnit, labelFont, valueFont);

        String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
        String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";

        PdfPCell cell3 = new PdfPCell(new Paragraph("工程名称：", labelFont));
        cell3.setBorder(Rectangle.BOX);
        cell3.setPadding(4);
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell3);

        PdfPCell cell4 = new PdfPCell(new Paragraph(projectName, valueFont));
        cell4.setColspan(2);
        cell4.setBorder(Rectangle.BOX);
        cell4.setPadding(4);
        cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell4);

        PdfPCell cell5 = new PdfPCell();
        cell5.setBorder(Rectangle.BOX);
        cell5.setPadding(4);
        Paragraph p5 = new Paragraph();
        p5.add(new Chunk("施工 (使用) 部位：", labelFont));
        p5.add(new Chunk("\n" + constructionPart, valueFont));
        cell5.addElement(p5);
        mainTable.addCell(cell5);

        String sampleName = request.getParameter("sampleName") != null ? request.getParameter("sampleName") : "";
        String spec = request.getParameter("spec") != null ? request.getParameter("spec") : "";
        String manufacturer = request.getParameter("manufacturer") != null ? request.getParameter("manufacturer") : "";
        String sampleQuantity = request.getParameter("sampleQuantity") != null ? request.getParameter("sampleQuantity") : "";
        String representativeBatch = request.getParameter("representativeBatch") != null ? request.getParameter("representativeBatch") : "";
        String batchNumber = request.getParameter("batchNumber") != null ? request.getParameter("batchNumber") : "";
        String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";

        PdfPTable nestedTable1 = new PdfPTable(6);
        nestedTable1.setWidths(new float[]{1, 2, 1, 2, 1, 2});
        nestedTable1.setWidthPercentage(100);

        addCellToNested(nestedTable1, "样品名称：", labelFont, Element.ALIGN_LEFT);
        addCellToNested(nestedTable1, sampleName, valueFont, Element.ALIGN_LEFT);
        addCellToNested(nestedTable1, "规格/型号：", labelFont, Element.ALIGN_LEFT);
        addCellToNested(nestedTable1, spec, valueFont, Element.ALIGN_LEFT);
        addCellToNested(nestedTable1, "生产厂家或产地：", labelFont, Element.ALIGN_LEFT);
        addCellToNested(nestedTable1, manufacturer, valueFont, Element.ALIGN_LEFT);

        PdfPCell nestedCell1 = new PdfPCell(nestedTable1);
        nestedCell1.setColspan(4);
        nestedCell1.setBorder(Rectangle.BOX);
        nestedCell1.setPadding(0);
        mainTable.addCell(nestedCell1);

        PdfPTable nestedTable2 = new PdfPTable(4);
        nestedTable2.setWidths(new float[]{1, 1, 1, 1});
        nestedTable2.setWidthPercentage(100);

        addCellToNestedCenter(nestedTable2, "样品数量：", labelFont);
        addCellToNestedCenter(nestedTable2, sampleQuantity, valueFont);
        addCellToNestedCenter(nestedTable2, "代表批量：", labelFont);
        addCellToNestedCenter(nestedTable2, representativeBatch, valueFont);

        addCellToNestedCenter(nestedTable2, "批号：", labelFont);
        addCellToNestedCenter(nestedTable2, batchNumber, valueFont);
        addCellToNestedCenter(nestedTable2, "检测(验)类别：", labelFont);
        addCellToNestedCenter(nestedTable2, testCategory, valueFont);

        PdfPCell nestedCell2 = new PdfPCell(nestedTable2);
        nestedCell2.setColspan(4);
        nestedCell2.setBorder(Rectangle.BOX);
        nestedCell2.setPadding(0);
        mainTable.addCell(nestedCell2);

        String sampleHistory = request.getParameter("sampleHistory") != null ? request.getParameter("sampleHistory") : "";
        String sampleStatus = request.getParameter("sampleStatus") != null ? request.getParameter("sampleStatus") : "";
        String testItems = request.getParameter("testItems") != null ? request.getParameter("testItems") : "";
        String clientAddressPhone = request.getParameter("clientAddressPhone") != null ? request.getParameter("clientAddressPhone") : "";

        PdfPCell cell6 = new PdfPCell();
        cell6.setColspan(2);
        cell6.setBorder(Rectangle.BOX);
        cell6.setPadding(4);
        Paragraph p6 = new Paragraph();
        p6.add(new Chunk("样品历史及概况：", labelFont));
        p6.add(new Chunk("\n" + sampleHistory, valueFont));
        cell6.addElement(p6);
        mainTable.addCell(cell6);

        PdfPCell cell7 = new PdfPCell();
        cell7.setColspan(2);
        cell7.setBorder(Rectangle.BOX);
        cell7.setPadding(4);
        Paragraph p7 = new Paragraph();
        p7.add(new Chunk("样品状态：", labelFont));
        p7.add(new Chunk("\n" + sampleStatus, valueFont));
        cell7.addElement(p7);
        mainTable.addCell(cell7);

        PdfPCell cell8 = new PdfPCell();
        cell8.setColspan(4);
        cell8.setBorder(Rectangle.BOX);
        cell8.setPadding(4);
        Paragraph p8 = new Paragraph();
        p8.add(new Chunk("检测(验)项目及依据：", labelFont));
        p8.add(new Chunk("\n" + testItems, valueFont));
        cell8.addElement(p8);
        mainTable.addCell(cell8);

        PdfPCell cell9 = new PdfPCell(new Paragraph("委托单位地址及电话(传真)：", labelFont));
        cell9.setBorder(Rectangle.BOX);
        cell9.setPadding(4);
        cell9.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell9);

        PdfPCell cell10 = new PdfPCell(new Paragraph(clientAddressPhone, valueFont));
        cell10.setColspan(3);
        cell10.setBorder(Rectangle.BOX);
        cell10.setPadding(4);
        cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell10);

        String[] reportSendValues = request.getParameterValues("reportSend");
        String reportSendText = getReportSendText(reportSendValues);
        String[] sampleDisposalValues = request.getParameterValues("sampleDisposal");
        String sampleDisposalText = getSampleDisposalText(sampleDisposalValues);

        addRow4Col(mainTable, "报告发送：", reportSendText, "样品处置：", sampleDisposalText, labelFont, valueFont);

        String witness = request.getParameter("witness") != null ? request.getParameter("witness") : "";
        String witnessUnit = request.getParameter("witnessUnit") != null ? request.getParameter("witnessUnit") : "";

        addRow4Col(mainTable, "见证人：", witness, "见证单位：", witnessUnit, labelFont, valueFont);

        PdfPCell cell11 = new PdfPCell(new Paragraph("检测报告交付日期：", labelFont));
        cell11.setBorder(Rectangle.BOX);
        cell11.setPadding(4);
        cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell11);

        PdfPCell cell12 = new PdfPCell();
        cell12.setBorder(Rectangle.BOX);
        cell12.setPadding(4);
        cell12.setHorizontalAlignment(Element.ALIGN_LEFT);

        String mode = request.getParameter("deliveryMode") != null ? request.getParameter("deliveryMode") : "3";

        String check1 = "1".equals(mode) ? "☑" : "□";
        String check2 = "2".equals(mode) ? "☑" : "□";
        String check3 = "3".equals(mode) ? "☑" : "□";

        Paragraph pDates = new Paragraph();
        pDates.setLeading(14f);

        // Option 1
        pDates.add(new Chunk(check1 + " 可以为：    ", valueFont));
        String y1 = request.getParameter("deliveryDate1_y") != null ? request.getParameter("deliveryDate1_y") : "";
        String m1 = request.getParameter("deliveryDate1_m") != null ? request.getParameter("deliveryDate1_m") : "";
        String d1 = request.getParameter("deliveryDate1_d") != null ? request.getParameter("deliveryDate1_d") : "";
        pDates.add(new Chunk((y1.isEmpty() ? "    " : y1) + "  年  ", valueFont));
        pDates.add(new Chunk((m1.isEmpty() ? "  " : m1) + "  月  ", valueFont));
        pDates.add(new Chunk((d1.isEmpty() ? "  " : d1) + "  日\n", valueFont));

        // Option 2
        pDates.add(new Chunk(check2 + " 严格限定为：", valueFont));
        String y2 = request.getParameter("deliveryDate2_y") != null ? request.getParameter("deliveryDate2_y") : "";
        String m2 = request.getParameter("deliveryDate2_m") != null ? request.getParameter("deliveryDate2_m") : "";
        String d2 = request.getParameter("deliveryDate2_d") != null ? request.getParameter("deliveryDate2_d") : "";
        pDates.add(new Chunk((y2.isEmpty() ? "    " : y2) + "  年  ", valueFont));
        pDates.add(new Chunk((m2.isEmpty() ? "  " : m2) + "  月  ", valueFont));
        pDates.add(new Chunk((d2.isEmpty() ? "  " : d2) + "  日\n", valueFont));

        // Option 3
        pDates.add(new Chunk(check3 + " 不要求", valueFont));

        cell12.addElement(pDates);
        mainTable.addCell(cell12);

        String fee = request.getParameter("fee") != null ? request.getParameter("fee") : "";
        String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

        PdfPCell cell13 = new PdfPCell(new Paragraph("应缴检测(验)费(元)：", labelFont));
        cell13.setBorder(Rectangle.BOX);
        cell13.setPadding(4);
        cell13.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell13);

        PdfPCell cell14 = new PdfPCell(new Paragraph(fee, valueFont));
        cell14.setBorder(Rectangle.BOX);
        cell14.setPadding(4);
        cell14.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell14);

        PdfPCell cell15 = new PdfPCell(new Paragraph("备注：", labelFont));
        cell15.setBorder(Rectangle.BOX);
        cell15.setPadding(4);
        cell15.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell15);

        PdfPCell cell16 = new PdfPCell(new Paragraph(remarks, valueFont));
        cell16.setColspan(3);
        cell16.setBorder(Rectangle.BOX);
        cell16.setPadding(4);
        cell16.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell16);

        PdfPCell cell17 = new PdfPCell(new Paragraph("说明：", labelFont));
        cell17.setBorder(Rectangle.BOX);
        cell17.setPadding(4);
        cell17.setVerticalAlignment(Element.ALIGN_TOP);
        cell17.setHorizontalAlignment(Element.ALIGN_LEFT);
        mainTable.addCell(cell17);

        PdfPCell cell18 = new PdfPCell();
        cell18.setColspan(3);
        cell18.setBorder(Rectangle.BOX);
        cell18.setPadding(4);
        Paragraph p18 = new Paragraph("1、委托人(送样人)和见证人应对样品的代表性负责。\n" +
                "2、检测(验)单位对检测结果负责技术责任。\n" +
                "3、本委托单一式三份，委托方一份，检测方两份，自送样人收样人签字起生效。\n" +
                "4、检测(验)委托单由委托方技术人员或送样人填写，要求内容齐全清晰。\n" +
                "5、见证检验时，本委托单无见证人签字无效。\n" +
                "6、本单如有变动，双方应及时沟通，凭此委托单领取报告。\n" +
                "7、客户可在报告发出之日起30日内取回样品，不合格样品一律不予返还。", smallFont);
        cell18.addElement(p18);
        mainTable.addCell(cell18);

        document.add(mainTable);

        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(10);

        String clientPerson = request.getParameter("clientPerson") != null ? request.getParameter("clientPerson") : "";
        String receiverPerson = request.getParameter("receiverPerson") != null ? request.getParameter("receiverPerson") : "";

        PdfPCell footerCell1 = new PdfPCell(new Paragraph("委托(送样)人：" + clientPerson, valueFont));
        footerCell1.setBorder(Rectangle.NO_BORDER);
        footerCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        footerTable.addCell(footerCell1);

        PdfPCell footerCell2 = new PdfPCell(new Paragraph("承接(收样)人：" + receiverPerson, valueFont));
        footerCell2.setBorder(Rectangle.NO_BORDER);
        footerCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(footerCell2);

        document.add(footerTable);
    }

    private String getReportSendText(String[] values) {
        if (values == null || values.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            switch (v) {
                case "1": sb.append("自取 "); break;
                case "2": sb.append("邮寄 "); break;
                case "3": sb.append("其它 "); break;
            }
        }
        return sb.toString().trim();
    }

    private String getSampleDisposalText(String[] values) {
        if (values == null || values.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            switch (v) {
                case "1": sb.append("取回 "); break;
                case "2": sb.append("不取回 "); break;
            }
        }
        return sb.toString().trim();
    }

    private void addRow4Col(PdfPTable table, String label1, String value1, String label2, String value2, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Paragraph(label1, labelFont));
        cell1.setBorder(Rectangle.BOX);
        cell1.setPadding(8);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Paragraph(value1, valueFont));
        cell2.setBorder(Rectangle.BOX);
        cell2.setPadding(8);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Paragraph(label2, labelFont));
        cell3.setBorder(Rectangle.BOX);
        cell3.setPadding(8);
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell3);

        PdfPCell cell4 = new PdfPCell(new Paragraph(value2, valueFont));
        cell4.setBorder(Rectangle.BOX);
        cell4.setPadding(8);
        cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell4);
    }

    private void addCellToNested(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addCellToNestedCenter(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    public byte[] generateLightDynamicPenetrationPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("轻型动力触探检测报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            // Add Header Info (Unified Number etc.) into mainTable to allow repetition
            PdfPTable topHeader = new PdfPTable(2);
            topHeader.setWidthPercentage(100);
            topHeader.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell topCell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            topCell1.setBorder(Rectangle.NO_BORDER);
            topCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            topHeader.addCell(topCell1);

            PdfPCell topCell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            topCell2.setBorder(Rectangle.NO_BORDER);
            topCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            topHeader.addCell(topCell2);

            PdfPCell topRowCell = new PdfPCell(topHeader);
            topRowCell.setColspan(10);
            topRowCell.setBorder(Rectangle.NO_BORDER);
            topRowCell.setPadding(0);
            topRowCell.setPaddingBottom(5);
            mainTable.addCell(topRowCell);

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String entrustDate = request.getParameter("entrustDate") != null ? request.getParameter("entrustDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testMethod = request.getParameter("testMethod") != null ? request.getParameter("testMethod") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String soilProperty = request.getParameter("soilProperty") != null ? request.getParameter("soilProperty") : "";
            String reportDate = request.getParameter("reportDate") != null ? request.getParameter("reportDate") : "";
            String witnessUnit = request.getParameter("witnessUnit") != null ? request.getParameter("witnessUnit") : "";
            String witness = request.getParameter("witness") != null ? request.getParameter("witness") : "";
            String designCapacity = request.getParameter("designCapacity") != null ? request.getParameter("designCapacity") : "";
            String hammerWeight = request.getParameter("hammerWeight") != null ? request.getParameter("hammerWeight") : "";
            String dropDistance = request.getParameter("dropDistance") != null ? request.getParameter("dropDistance") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, entrustDate, valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "岩土性状", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, soilProperty, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "报告日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, reportDate, valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "见证单位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, witnessUnit, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "见证人", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, witness, valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "设计\n承载力\n(kPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, designCapacity, valueFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "锤重量\n(kg)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, hammerWeight, valueFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "落距\n(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, dropDistance, valueFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 2);

            // 明细表头：与前端 LightDynamicPenetration.vue 完全一致（左右各一组）
            addCell(mainTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入深度(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均锤击数N10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力特征值(kPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入深度(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均锤击数N10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力特征值(kPa)", labelFont, Element.ALIGN_CENTER, 1);

            // 设置需要重复的表头行数：上方 4 行信息 + 1 行设计参数 + 1 行明细表头 = 6
            mainTable.setHeaderRows(6);

            // 明细数据行：与 Vue 中 dataBlocks 结构对应（2 组，每组 6 行，左右各一组）
            for (int b = 0; b < 2; b++) {
                String pos_L = getSafeParam(request, "pos_L_" + b);
                String avg_L = getSafeParam(request, "avg_L_" + b);
                String capacity_L = getSafeParam(request, "capacity_L_" + b);

                String pos_R = getSafeParam(request, "pos_R_" + b);
                String avg_R = getSafeParam(request, "avg_R_" + b);
                String capacity_R = getSafeParam(request, "capacity_R_" + b);

                // 每个 block 有 6 个子行：索引 0..5
                for (int s = 0; s < 6; s++) {
                    int idx = b * 6 + s;

                    String depth_L = getSafeParam(request, "depth_L_" + idx);
                    String actual_L = getSafeParam(request, "actual_L_" + idx);
                    String depth_R = getSafeParam(request, "depth_R_" + idx);
                    String actual_R = getSafeParam(request, "actual_R_" + idx);

                    PdfPCell posLCell = null;
                    PdfPCell avgLCell = null;
                    PdfPCell capLCell = null;
                    PdfPCell posRCell = null;
                    PdfPCell avgRCell = null;
                    PdfPCell capRCell = null;

                    // 第一行：写入测点位置 / 平均锤击数 / 承载力单元格，并用 rowspan 覆盖 6 行
                    if (s == 0) {
                        posLCell = new PdfPCell(new Paragraph(pos_L, valueFont));
                        posLCell.setBorder(Rectangle.BOX);
                        posLCell.setPadding(4);
                        posLCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        posLCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        posLCell.setRowspan(6);
                        mainTable.addCell(posLCell);
                    }

                    // 左侧贯入深度与实测锤击数
                    addCell(mainTable, depth_L, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, actual_L, valueFont, Element.ALIGN_CENTER, 1);

                    if (s == 0) {
                        avgLCell = new PdfPCell(new Paragraph(avg_L, valueFont));
                        avgLCell.setBorder(Rectangle.BOX);
                        avgLCell.setPadding(4);
                        avgLCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        avgLCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        avgLCell.setRowspan(6);
                        mainTable.addCell(avgLCell);

                        capLCell = new PdfPCell(new Paragraph(capacity_L, valueFont));
                        capLCell.setBorder(Rectangle.BOX);
                        capLCell.setPadding(4);
                        capLCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        capLCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        capLCell.setRowspan(6);
                        mainTable.addCell(capLCell);

                        posRCell = new PdfPCell(new Paragraph(pos_R, valueFont));
                        posRCell.setBorder(Rectangle.BOX);
                        posRCell.setPadding(4);
                        posRCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        posRCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        posRCell.setRowspan(6);
                        mainTable.addCell(posRCell);
                    }

                    // 右侧贯入深度与实测锤击数
                    addCell(mainTable, depth_R, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, actual_R, valueFont, Element.ALIGN_CENTER, 1);

                    if (s == 0) {
                        avgRCell = new PdfPCell(new Paragraph(avg_R, valueFont));
                        avgRCell.setBorder(Rectangle.BOX);
                        avgRCell.setPadding(4);
                        avgRCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        avgRCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        avgRCell.setRowspan(6);
                        mainTable.addCell(avgRCell);

                        capRCell = new PdfPCell(new Paragraph(capacity_R, valueFont));
                        capRCell.setBorder(Rectangle.BOX);
                        capRCell.setPadding(4);
                        capRCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        capRCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        capRCell.setRowspan(6);
                        mainTable.addCell(capRCell);
                    }

                    mainTable.completeRow();
                }
            }

            // 直接在 mainTable 末尾追加“检测结论 / 备注”两行，保持与上表格连成一个整体
            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarksBottom = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            PdfPCell conclusionLabelCell = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionLabelCell.setBorder(Rectangle.BOX);
            conclusionLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            conclusionLabelCell.setColspan(1);
            mainTable.addCell(conclusionLabelCell);

            PdfPCell conclusionValueCell = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionValueCell.setBorder(Rectangle.BOX);
            conclusionValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionValueCell.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionValueCell.setColspan(9);
            conclusionValueCell.setMinimumHeight(60f);
            mainTable.addCell(conclusionValueCell);

            PdfPCell remarksLabelCell = new PdfPCell(new Paragraph("备注", labelFont));
            remarksLabelCell.setBorder(Rectangle.BOX);
            remarksLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            remarksLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            remarksLabelCell.setColspan(1);
            mainTable.addCell(remarksLabelCell);

            PdfPCell remarksValueCell = new PdfPCell(new Paragraph(remarksBottom, valueFont));
            remarksValueCell.setBorder(Rectangle.BOX);
            remarksValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            remarksValueCell.setVerticalAlignment(Element.ALIGN_TOP);
            remarksValueCell.setColspan(9);
            remarksValueCell.setMinimumHeight(40f);
            mainTable.addCell(remarksValueCell);

            document.add(mainTable);

            // 下方签字区 + 声明 + 公司信息：布局与页面保持一致（批准/审核/检验 + 声明 + 公司名称/地址/电话）
            PdfPTable footerTable = new PdfPTable(3); // 3列用于批准、审核、检验
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);
            footerTable.setWidths(new float[]{1, 1, 1});

            String approval = request.getParameter("approval") != null ? request.getParameter("approval") : "";
            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("批准：" + approval, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检验：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            // 声明 + 公司信息区
            String companyName = request.getParameter("companyName") != null ? request.getParameter("companyName") : "";
            String companyPhone = request.getParameter("companyPhone") != null ? request.getParameter("companyPhone") : "";
            String companyAddress = request.getParameter("companyAddress") != null ? request.getParameter("companyAddress") : "";

            // 顶部粗横线（在“声明”之上）
            PdfPTable topLineTable = new PdfPTable(1);
            topLineTable.setWidthPercentage(100);
            topLineTable.setSpacingBefore(10);
            PdfPCell topLineCell = new PdfPCell(new Paragraph(" ", valueFont));
            topLineCell.setBorder(Rectangle.BOTTOM);
            topLineCell.setBorderWidthBottom(1.2f); // 略粗的横线
            topLineCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            topLineTable.addCell(topLineCell);
            document.add(topLineTable);

            // 声明行（无边框）
            PdfPTable statementTable = new PdfPTable(1);
            statementTable.setWidthPercentage(100);
            statementTable.setSpacingBefore(5);

            PdfPCell statementCell = new PdfPCell(new Paragraph("声明：1. 对本检测报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。", smallFont));
            statementCell.setBorder(Rectangle.NO_BORDER);
            statementCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            statementTable.addCell(statementCell);

            document.add(statementTable);

            // 公司名称 / 电话、公司地址，使用 4 列表格，只保留下划线效果
            PdfPTable companyTable = new PdfPTable(4);
            companyTable.setWidthPercentage(100);
            companyTable.setSpacingBefore(5);
            companyTable.setWidths(new float[]{1.2f, 3.8f, 0.8f, 2.2f});

            PdfPCell nameLabelCell = new PdfPCell(new Paragraph("公司名称：", valueFont));
            nameLabelCell.setBorder(Rectangle.NO_BORDER);
            nameLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(nameLabelCell);

            PdfPCell nameValueCell = new PdfPCell(new Paragraph(companyName, valueFont));
            nameValueCell.setBorder(Rectangle.NO_BORDER);
            nameValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(nameValueCell);

            PdfPCell phoneLabelCell = new PdfPCell(new Paragraph("电话：", valueFont));
            phoneLabelCell.setBorder(Rectangle.NO_BORDER);
            phoneLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(phoneLabelCell);

            PdfPCell phoneValueCell = new PdfPCell(new Paragraph(companyPhone, valueFont));
            phoneValueCell.setBorder(Rectangle.NO_BORDER);
            phoneValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(phoneValueCell);

            PdfPCell addrLabelCell = new PdfPCell(new Paragraph("公司地址：", valueFont));
            addrLabelCell.setBorder(Rectangle.NO_BORDER);
            addrLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            addrLabelCell.setColspan(1);
            companyTable.addCell(addrLabelCell);

            PdfPCell addrValueCell = new PdfPCell(new Paragraph(companyAddress, valueFont));
            addrValueCell.setBorder(Rectangle.NO_BORDER);
            addrValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            addrValueCell.setColspan(3);
            companyTable.addCell(addrValueCell);

            // 公司地址下方粗横线
            PdfPCell bottomLineCell = new PdfPCell(new Paragraph(" ", valueFont));
            bottomLineCell.setBorder(Rectangle.BOTTOM);
            bottomLineCell.setBorderWidthBottom(1.2f);
            bottomLineCell.setColspan(4);
            bottomLineCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(bottomLineCell);

            document.add(companyTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(4);
        cell.setHorizontalAlignment(alignment);
        cell.setColspan(colspan);
        table.addCell(cell);
    }

    public byte[] generateLightDynamicPenetrationResultPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("轻型动力触探检测结果", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            // 与前端表单对齐：委托单位字段名为 clientUnit（兼容旧的 entrustingUnit）
            String entrustingUnitParam = request.getParameter("clientUnit") != null
                    ? request.getParameter("clientUnit")
                    : request.getParameter("entrustingUnit");
            String entrustingUnit = entrustingUnitParam != null ? entrustingUnitParam : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            // 10 列等宽，使上半部分与“设计参数/检测类别”行的列线完全对齐
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String soilProperties = request.getParameter("soilProperties") != null ? request.getParameter("soilProperties") : "";
            String reportDate = request.getParameter("reportDate") != null ? request.getParameter("reportDate") : "";
            String witnessUnit = request.getParameter("witnessUnit") != null ? request.getParameter("witnessUnit") : "";
            String witness = request.getParameter("witness") != null ? request.getParameter("witness") : "";
            String designCapacity = request.getParameter("designCapacity") != null ? request.getParameter("designCapacity") : "";
            String hammerWeight = request.getParameter("hammerWeight") != null ? request.getParameter("hammerWeight") : "";
            String dropDistance = request.getParameter("dropDistance") != null ? request.getParameter("dropDistance") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";

            // 头部信息区：1 + 4 + 1 + 4 = 10，右半部分与下方“检测类别”所在列对齐
            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "岩土性状", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, soilProperties, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "报告日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, reportDate, valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "见证单位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, witnessUnit, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "见证人", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, witness, valueFont, Element.ALIGN_CENTER, 4);

            // 设计参数行：与前端表格一致：1+2+1+1+1+1+1+2 = 10
            addCell(mainTable, "设计承载力(kPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, designCapacity, valueFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "锤重量(kg)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, hammerWeight, valueFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "落距(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, dropDistance, valueFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入深度(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均锤击数N10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力特征值(kPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入深度(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均锤击数N10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力特征值(kPa)", labelFont, Element.ALIGN_CENTER, 1);

            for (int blockIndex = 1; blockIndex <= 4; blockIndex++) {
                String pos_L = request.getParameter("pos_L_" + blockIndex) != null ? request.getParameter("pos_L_" + blockIndex) : "";
                String depth_L_1 = request.getParameter("depth_L_1_" + blockIndex) != null ? request.getParameter("depth_L_1_" + blockIndex) : "";
                String actual_L_1 = request.getParameter("actual_L_1_" + blockIndex) != null ? request.getParameter("actual_L_1_" + blockIndex) : "";
                String avg_L = request.getParameter("avg_L_" + blockIndex) != null ? request.getParameter("avg_L_" + blockIndex) : "";
                String capacity_L = request.getParameter("capacity_L_" + blockIndex) != null ? request.getParameter("capacity_L_" + blockIndex) : "";
                String pos_R = request.getParameter("pos_R_" + blockIndex) != null ? request.getParameter("pos_R_" + blockIndex) : "";
                String depth_R_1 = request.getParameter("depth_R_1_" + blockIndex) != null ? request.getParameter("depth_R_1_" + blockIndex) : "";
                String actual_R_1 = request.getParameter("actual_R_1_" + blockIndex) != null ? request.getParameter("actual_R_1_" + blockIndex) : "";
                String avg_R = request.getParameter("avg_R_" + blockIndex) != null ? request.getParameter("avg_R_" + blockIndex) : "";
                String capacity_R = request.getParameter("capacity_R_" + blockIndex) != null ? request.getParameter("capacity_R_" + blockIndex) : "";
                String depth_L_2 = request.getParameter("depth_L_2_" + blockIndex) != null ? request.getParameter("depth_L_2_" + blockIndex) : "";
                String actual_L_2 = request.getParameter("actual_L_2_" + blockIndex) != null ? request.getParameter("actual_L_2_" + blockIndex) : "";
                String depth_R_2 = request.getParameter("depth_R_2_" + blockIndex) != null ? request.getParameter("depth_R_2_" + blockIndex) : "";
                String actual_R_2 = request.getParameter("actual_R_2_" + blockIndex) != null ? request.getParameter("actual_R_2_" + blockIndex) : "";

                PdfPCell posCell1 = new PdfPCell(new Paragraph(pos_L, valueFont));
                posCell1.setBorder(Rectangle.BOX);
                posCell1.setPadding(4);
                posCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                posCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                posCell1.setRowspan(2);
                mainTable.addCell(posCell1);

                addCell(mainTable, depth_L_1, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, actual_L_1, valueFont, Element.ALIGN_CENTER, 1);

                PdfPCell avgCell1 = new PdfPCell(new Paragraph(avg_L, valueFont));
                avgCell1.setBorder(Rectangle.BOX);
                avgCell1.setPadding(4);
                avgCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                avgCell1.setRowspan(2);
                mainTable.addCell(avgCell1);

                PdfPCell capacityCell1 = new PdfPCell(new Paragraph(capacity_L, valueFont));
                capacityCell1.setBorder(Rectangle.BOX);
                capacityCell1.setPadding(4);
                capacityCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                capacityCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                capacityCell1.setRowspan(2);
                mainTable.addCell(capacityCell1);

                PdfPCell posCell2 = new PdfPCell(new Paragraph(pos_R, valueFont));
                posCell2.setBorder(Rectangle.BOX);
                posCell2.setPadding(4);
                posCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                posCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                posCell2.setRowspan(2);
                mainTable.addCell(posCell2);

                addCell(mainTable, depth_R_1, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, actual_R_1, valueFont, Element.ALIGN_CENTER, 1);

                PdfPCell avgCell2 = new PdfPCell(new Paragraph(avg_R, valueFont));
                avgCell2.setBorder(Rectangle.BOX);
                avgCell2.setPadding(4);
                avgCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                avgCell2.setRowspan(2);
                mainTable.addCell(avgCell2);

                PdfPCell capacityCell2 = new PdfPCell(new Paragraph(capacity_R, valueFont));
                capacityCell2.setBorder(Rectangle.BOX);
                capacityCell2.setPadding(4);
                capacityCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                capacityCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                capacityCell2.setRowspan(2);
                mainTable.addCell(capacityCell2);

                mainTable.completeRow();

                // 第二行：只需要添加深度和实际锤击数，其他单元格由rowspan覆盖
                addCell(mainTable, depth_L_2, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, actual_L_2, valueFont, Element.ALIGN_CENTER, 1);
                // 跳过avgCell1和capacityCell1的位置（由rowspan覆盖）
                addCell(mainTable, depth_R_2, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, actual_R_2, valueFont, Element.ALIGN_CENTER, 1);
                // 跳过avgCell2和capacityCell2的位置（由rowspan覆盖）
                mainTable.completeRow();
            }

            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_LEFT, 9);

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(3);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(3);
            conclusionCell2.setColspan(9);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, remarks, valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);
            footerTable.setWidths(new float[]{1, 1, 1});

            String approval = request.getParameter("approval") != null ? request.getParameter("approval") : "";
            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";
            String reportDateFooter = request.getParameter("reportDate") != null ? request.getParameter("reportDate") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("批准：" + approval, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检验：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            PdfPCell footerCell4 = new PdfPCell();
            footerCell4.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell4);

            PdfPCell footerCell5 = new PdfPCell();
            footerCell5.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell5);

            PdfPCell footerCell6 = new PdfPCell(new Paragraph("日期：" + reportDateFooter, valueFont));
            footerCell6.setBorder(Rectangle.NO_BORDER);
            footerCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell6);

            document.add(footerTable);

            String companyName = request.getParameter("companyName") != null ? request.getParameter("companyName") : "";
            String companyAddress = request.getParameter("companyAddress") != null ? request.getParameter("companyAddress") : "";
            String companyPhone = request.getParameter("companyPhone") != null ? request.getParameter("companyPhone") : "";
            Paragraph statement = new Paragraph("声明：\n1. 对本检测报告的复印件未加盖公司检验检测专用章无效。\n2. 对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。\n公司名称：" + companyName + "。\n公司地址：" + companyAddress + "    电话：" + companyPhone, smallFont);
            statement.setSpacingBefore(20);
            statement.setLeading(14f);
            document.add(statement);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    /**
     * 安全地从 HttpServletRequest 中获取参数值，避免出现 null。
     */
    private String getSafeParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value != null ? value : "";
    }

    public byte[] generateLightDynamicPenetrationRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("轻型动力触探检测记录", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String soilProperties = request.getParameter("soilProperties") != null ? request.getParameter("soilProperties") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";
            String designCapacity = request.getParameter("designCapacity") != null ? request.getParameter("designCapacity") : "";
            String hammerWeight = request.getParameter("hammerWeight") != null ? request.getParameter("hammerWeight") : "";
            String dropDistance = request.getParameter("dropDistance") != null ? request.getParameter("dropDistance") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "岩土性状", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, soilProperties, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "设计承载力(kPa)", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "锤重量(kg)", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "落距(cm)", labelFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, designCapacity, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, hammerWeight, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, dropDistance, valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入深度(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均锤击数N10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力特征值(kPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入深度(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均锤击数N10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力特征值(kPa)", labelFont, Element.ALIGN_CENTER, 1);

            for (int blockIndex = 1; blockIndex <= 2; blockIndex++) {
                String leftPosition = request.getParameter("leftPosition_" + blockIndex) != null ? request.getParameter("leftPosition_" + blockIndex) : "";
                String leftAvg = request.getParameter("leftAvg_" + blockIndex) != null ? request.getParameter("leftAvg_" + blockIndex) : "";
                String leftCapacity = request.getParameter("leftCapacity_" + blockIndex) != null ? request.getParameter("leftCapacity_" + blockIndex) : "";
                String rightPosition = request.getParameter("rightPosition_" + blockIndex) != null ? request.getParameter("rightPosition_" + blockIndex) : "";
                String rightAvg = request.getParameter("rightAvg_" + blockIndex) != null ? request.getParameter("rightAvg_" + blockIndex) : "";
                String rightCapacity = request.getParameter("rightCapacity_" + blockIndex) != null ? request.getParameter("rightCapacity_" + blockIndex) : "";

                for (int rowIndex = 1; rowIndex <= 5; rowIndex++) {
                    PdfPCell positionCell = null;
                    PdfPCell avgCell = null;
                    PdfPCell capacityCell = null;
                    PdfPCell rightPositionCell = null;
                    PdfPCell rightAvgCell = null;
                    PdfPCell rightCapacityCell = null;

                    if (rowIndex == 1) {
                        positionCell = new PdfPCell(new Paragraph(leftPosition, valueFont));
                        positionCell.setBorder(Rectangle.BOX);
                        positionCell.setPadding(4);
                        positionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        positionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        positionCell.setRowspan(5);
                        mainTable.addCell(positionCell);
                    }

                    String leftDepth = request.getParameter("leftDepth_" + blockIndex + "_" + rowIndex) != null ? request.getParameter("leftDepth_" + blockIndex + "_" + rowIndex) : "";
                    String leftActual = request.getParameter("leftActual_" + blockIndex + "_" + rowIndex) != null ? request.getParameter("leftActual_" + blockIndex + "_" + rowIndex) : "";
                    addCell(mainTable, leftDepth, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, leftActual, valueFont, Element.ALIGN_CENTER, 1);

                    if (rowIndex == 1) {
                        avgCell = new PdfPCell(new Paragraph(leftAvg, valueFont));
                        avgCell.setBorder(Rectangle.BOX);
                        avgCell.setPadding(4);
                        avgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        avgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        avgCell.setRowspan(5);
                        mainTable.addCell(avgCell);

                        capacityCell = new PdfPCell(new Paragraph(leftCapacity, valueFont));
                        capacityCell.setBorder(Rectangle.BOX);
                        capacityCell.setPadding(4);
                        capacityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        capacityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        capacityCell.setRowspan(5);
                        mainTable.addCell(capacityCell);

                        rightPositionCell = new PdfPCell(new Paragraph(rightPosition, valueFont));
                        rightPositionCell.setBorder(Rectangle.BOX);
                        rightPositionCell.setPadding(4);
                        rightPositionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        rightPositionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        rightPositionCell.setRowspan(5);
                        mainTable.addCell(rightPositionCell);
                    }

                    String rightDepth = request.getParameter("rightDepth_" + blockIndex + "_" + rowIndex) != null ? request.getParameter("rightDepth_" + blockIndex + "_" + rowIndex) : "";
                    String rightActual = request.getParameter("rightActual_" + blockIndex + "_" + rowIndex) != null ? request.getParameter("rightActual_" + blockIndex + "_" + rowIndex) : "";
                    addCell(mainTable, rightDepth, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, rightActual, valueFont, Element.ALIGN_CENTER, 1);

                    if (rowIndex == 1) {
                        rightAvgCell = new PdfPCell(new Paragraph(rightAvg, valueFont));
                        rightAvgCell.setBorder(Rectangle.BOX);
                        rightAvgCell.setPadding(4);
                        rightAvgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        rightAvgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        rightAvgCell.setRowspan(5);
                        mainTable.addCell(rightAvgCell);

                        rightCapacityCell = new PdfPCell(new Paragraph(rightCapacity, valueFont));
                        rightCapacityCell.setBorder(Rectangle.BOX);
                        rightCapacityCell.setPadding(4);
                        rightCapacityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        rightCapacityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        rightCapacityCell.setRowspan(5);
                        mainTable.addCell(rightCapacityCell);
                    }
                    // 每行只需要补充左/右贯入深度与实测锤击数四个单元格，其余由第一行的 rowspan 覆盖
                    mainTable.completeRow();
                }
            }

            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_LEFT, 9);

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionCell2.setColspan(9);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, remarks, valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);
            footerTable.setWidths(new float[]{1, 1, 1});

            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String calculator = request.getParameter("calculator") != null ? request.getParameter("calculator") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";
            String year = request.getParameter("year") != null ? request.getParameter("year") : "";
            String month = request.getParameter("month") != null ? request.getParameter("month") : "";
            String day = request.getParameter("day") != null ? request.getParameter("day") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("计算：" + calculator, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            PdfPCell footerCell4 = new PdfPCell();
            footerCell4.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell4);

            PdfPCell footerCell5 = new PdfPCell();
            footerCell5.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell5);

            PdfPCell footerCell6 = new PdfPCell(new Paragraph("日期：" + year + "年" + month + "月" + day + "日", valueFont));
            footerCell6.setBorder(Rectangle.NO_BORDER);
            footerCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell6);

            document.add(footerTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateNuclearDensityRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("原位密度检测记录表（核子法）", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            mainTable.setWidths(new float[]{1, 2, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String nuclearModel = request.getParameter("nuclearModel") != null ? request.getParameter("nuclearModel") : "";
            String testDepth = request.getParameter("testDepth") != null ? request.getParameter("testDepth") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "核子仪型号", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, nuclearModel, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "检测深度(cm)", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testDepth, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "样品编号", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "检测部位(桩号、高程)", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "湿密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "含水率%", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 2);

            for (int i = 1; i <= 15; i++) {
                String sampleId = request.getParameter("sampleId_" + i) != null ? request.getParameter("sampleId_" + i) : "";
                String location = request.getParameter("location_" + i) != null ? request.getParameter("location_" + i) : "";
                String date = request.getParameter("date_" + i) != null ? request.getParameter("date_" + i) : "";
                String wetDensity = request.getParameter("wetDensity_" + i) != null ? request.getParameter("wetDensity_" + i) : "";
                String dryDensity = request.getParameter("dryDensity_" + i) != null ? request.getParameter("dryDensity_" + i) : "";
                String moisture = request.getParameter("moisture_" + i) != null ? request.getParameter("moisture_" + i) : "";
                String remarks = request.getParameter("remarks_" + i) != null ? request.getParameter("remarks_" + i) : "";

                addCell(mainTable, sampleId, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, location, valueFont, Element.ALIGN_CENTER, 2);
                addCell(mainTable, date, valueFont, Element.ALIGN_CENTER, 2);
                addCell(mainTable, wetDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, dryDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, moisture, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, remarks, valueFont, Element.ALIGN_CENTER, 2);
            }

            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String overallRemarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionCell2.setColspan(9);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, overallRemarks, valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);
            footerTable.setWidths(new float[]{1, 1, 1});

            String calculator = request.getParameter("calculator") != null ? request.getParameter("calculator") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";
            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String year = request.getParameter("year") != null ? request.getParameter("year") : "";
            String month = request.getParameter("month") != null ? request.getParameter("month") : "";
            String day = request.getParameter("day") != null ? request.getParameter("day") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("计算：" + calculator, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("复核：" + reviewer, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.setSpacingBefore(10);
            dateTable.setWidths(new float[]{1, 1});

            PdfPCell dateCell1 = new PdfPCell(new Paragraph(year + "年" + month + "月" + day + "日", valueFont));
            dateCell1.setBorder(Rectangle.NO_BORDER);
            dateCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell1);

            PdfPCell dateCell2 = new PdfPCell(new Paragraph("第 1 页，共 1 页", valueFont));
            dateCell2.setBorder(Rectangle.NO_BORDER);
            dateCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell2);

            document.add(dateTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateCuttingRingRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("环刀法检测记录", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String soilType = request.getParameter("soilType") != null ? request.getParameter("soilType") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "土的种类", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, soilType, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "测点", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "环刀体积(cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "试样湿重(g)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "试样干重(g)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "含水率(%)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "湿密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "最大干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "压实系数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "合格率(%)", labelFont, Element.ALIGN_CENTER, 1);

            for (int i = 1; i <= 10; i++) {
                String point = request.getParameter("point_" + i) != null ? request.getParameter("point_" + i) : "";
                String ringVolume = request.getParameter("ringVolume_" + i) != null ? request.getParameter("ringVolume_" + i) : "";
                String wetWeight = request.getParameter("wetWeight_" + i) != null ? request.getParameter("wetWeight_" + i) : "";
                String dryWeight = request.getParameter("dryWeight_" + i) != null ? request.getParameter("dryWeight_" + i) : "";
                String moistureContent = request.getParameter("moistureContent_" + i) != null ? request.getParameter("moistureContent_" + i) : "";
                String wetDensity = request.getParameter("wetDensity_" + i) != null ? request.getParameter("wetDensity_" + i) : "";
                String dryDensity = request.getParameter("dryDensity_" + i) != null ? request.getParameter("dryDensity_" + i) : "";
                String maxDryDensity = request.getParameter("maxDryDensity_" + i) != null ? request.getParameter("maxDryDensity_" + i) : "";
                String compactionCoefficient = request.getParameter("compactionCoefficient_" + i) != null ? request.getParameter("compactionCoefficient_" + i) : "";
                String qualifiedRate = request.getParameter("qualifiedRate_" + i) != null ? request.getParameter("qualifiedRate_" + i) : "";

                addCell(mainTable, point, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, ringVolume, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, wetWeight, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, dryWeight, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, moistureContent, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, wetDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, dryDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, maxDryDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, compactionCoefficient, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, qualifiedRate, valueFont, Element.ALIGN_CENTER, 1);
            }

            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_LEFT, 9);

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionCell2.setColspan(9);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, remarks, valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);
            footerTable.setWidths(new float[]{1, 1, 1});

            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String calculator = request.getParameter("calculator") != null ? request.getParameter("calculator") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";
            String year = request.getParameter("year") != null ? request.getParameter("year") : "";
            String month = request.getParameter("month") != null ? request.getParameter("month") : "";
            String day = request.getParameter("day") != null ? request.getParameter("day") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("计算：" + calculator, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.setSpacingBefore(10);
            dateTable.setWidths(new float[]{1, 1});

            PdfPCell dateCell1 = new PdfPCell(new Paragraph(year + "年" + month + "月" + day + "日", valueFont));
            dateCell1.setBorder(Rectangle.NO_BORDER);
            dateCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell1);

            PdfPCell dateCell2 = new PdfPCell(new Paragraph("第 1 页，共 1 页", valueFont));
            dateCell2.setBorder(Rectangle.NO_BORDER);
            dateCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell2);

            document.add(dateTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateDensityTestReportPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("原位密度检测报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(11);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            // 与前端 HTML 一致：11 列等宽，让“1 列 label + 6 列值 + 1 列 label + 3 列值”整体对齐
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String sampleNameStatus = request.getParameter("sampleNameStatus") != null ? request.getParameter("sampleNameStatus") : "";
            String reportDate = request.getParameter("reportDate") != null ? request.getParameter("reportDate") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String testMethod = request.getParameter("testMethod") != null ? request.getParameter("testMethod") : "";
            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";
            String maxDryDensity = request.getParameter("maxDryDensity") != null ? request.getParameter("maxDryDensity") : "";
            String optimumMoisture = request.getParameter("optimumMoisture") != null ? request.getParameter("optimumMoisture") : "";
            String minDryDensity = request.getParameter("minDryDensity") != null ? request.getParameter("minDryDensity") : "";

            boolean isNuclearMethod = testMethod != null && testMethod.contains("核子");
            String designIndex = request.getParameter("designIndex") != null ? request.getParameter("designIndex") : "";
            String testResult = request.getParameter("testResult") != null ? request.getParameter("testResult") : "";

            // 每一行的 colspan 总和控制为 11，严格按前端表格结构排版
            // 工程名称 + 委托日期：1 + 6 + 1 + 3 = 11
            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 3);

            // 施工部位 + 检测日期：1 + 6 + 1 + 3 = 11
            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 3);

            // 样品名称及状态 + 报告日期：1 + 6 + 1 + 3 = 11
            addCell(mainTable, "样品名称及状态", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, sampleNameStatus, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "报告日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, reportDate, valueFont, Element.ALIGN_CENTER, 3);

            // 仪器设备 + 检测方法：1 + 6 + 1 + 3 = 11
            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "检测方法", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testMethod, valueFont, Element.ALIGN_CENTER, 3);

            // 检测依据 + 检测类别：1 + 6 + 1 + 3 = 11
            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 3);

            // 最大/最小干密度 & 最优含水率：1+3+1+3+1+2 = 11
            addCell(mainTable, "最大干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, maxDryDensity, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "最优含水率 %", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, optimumMoisture, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "最小干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, minDryDensity, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "设计指标", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, designIndex, valueFont, Element.ALIGN_LEFT, 10);

            addCell(mainTable, "检测结果", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testResult, valueFont, Element.ALIGN_LEFT, 10);

            addCell(mainTable, "样品编号", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "检测部位(桩号、高程)", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "湿密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "含水率%", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "压实度%", labelFont, Element.ALIGN_CENTER, 2);

            // 与前端保持一致：表单字段索引为 0..7
            for (int i = 0; i < 8; i++) {
                String idx = String.valueOf(i);
                String sampleId = request.getParameter("sampleId_" + idx) != null ? request.getParameter("sampleId_" + idx) : "";
                String location = request.getParameter("location_" + idx) != null ? request.getParameter("location_" + idx) : "";
                String date = request.getParameter("date_" + idx) != null ? request.getParameter("date_" + idx) : "";
                String wetDensity = request.getParameter("wetDensity_" + idx) != null ? request.getParameter("wetDensity_" + idx) : "";
                String dryDensity = request.getParameter("dryDensity_" + idx) != null ? request.getParameter("dryDensity_" + idx) : "";
                String moisture = request.getParameter("moisture_" + idx) != null ? request.getParameter("moisture_" + idx) : "";
                String wetDensity2 = request.getParameter("wetDensity2_" + idx) != null ? request.getParameter("wetDensity2_" + idx) : "";
                String dryDensity2 = request.getParameter("dryDensity2_" + idx) != null ? request.getParameter("dryDensity2_" + idx) : "";
                String moisture2 = request.getParameter("moisture2_" + idx) != null ? request.getParameter("moisture2_" + idx) : "";
                String compaction = request.getParameter("compaction_" + idx) != null ? request.getParameter("compaction_" + idx) : "";

                if (isNuclearMethod) {
                    // 核子法：一行结构（不需要第二行）
                    PdfPCell sampleIdCell = new PdfPCell(new Paragraph(sampleId, valueFont));
                    sampleIdCell.setBorder(Rectangle.BOX);
                    sampleIdCell.setPadding(4);
                    sampleIdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    sampleIdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    mainTable.addCell(sampleIdCell);

                    PdfPCell locationCell = new PdfPCell(new Paragraph(location, valueFont));
                    locationCell.setBorder(Rectangle.BOX);
                    locationCell.setPadding(4);
                    locationCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    locationCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    locationCell.setColspan(3);
                    mainTable.addCell(locationCell);

                    PdfPCell dateCell = new PdfPCell(new Paragraph(date, valueFont));
                    dateCell.setBorder(Rectangle.BOX);
                    dateCell.setPadding(4);
                    dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dateCell.setColspan(2);
                    mainTable.addCell(dateCell);

                    addCell(mainTable, wetDensity, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, dryDensity, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, moisture, valueFont, Element.ALIGN_CENTER, 1);

                    PdfPCell compactionCell = new PdfPCell(new Paragraph(compaction, valueFont));
                    compactionCell.setBorder(Rectangle.BOX);
                    compactionCell.setPadding(4);
                    compactionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    compactionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    compactionCell.setColspan(2);
                    mainTable.addCell(compactionCell);
                } else {
                    // 非核子法：两行结构，与页面一致
                    PdfPCell sampleIdCell = new PdfPCell(new Paragraph(sampleId, valueFont));
                    sampleIdCell.setBorder(Rectangle.BOX);
                    sampleIdCell.setPadding(4);
                    sampleIdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    sampleIdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    sampleIdCell.setRowspan(2);
                    mainTable.addCell(sampleIdCell);

                    PdfPCell locationCell = new PdfPCell(new Paragraph(location, valueFont));
                    locationCell.setBorder(Rectangle.BOX);
                    locationCell.setPadding(4);
                    locationCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    locationCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    locationCell.setRowspan(2);
                    locationCell.setColspan(3);
                    mainTable.addCell(locationCell);

                    PdfPCell dateCell = new PdfPCell(new Paragraph(date, valueFont));
                    dateCell.setBorder(Rectangle.BOX);
                    dateCell.setPadding(4);
                    dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dateCell.setRowspan(2);
                    dateCell.setColspan(2);
                    mainTable.addCell(dateCell);

                    addCell(mainTable, wetDensity, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, dryDensity, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, moisture, valueFont, Element.ALIGN_CENTER, 1);

                    PdfPCell compactionCell = new PdfPCell(new Paragraph(compaction, valueFont));
                    compactionCell.setBorder(Rectangle.BOX);
                    compactionCell.setPadding(4);
                    compactionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    compactionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    compactionCell.setRowspan(2);
                    compactionCell.setColspan(2);
                    mainTable.addCell(compactionCell);

                    addCell(mainTable, wetDensity2, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, dryDensity2, valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, moisture2, valueFont, Element.ALIGN_CENTER, 1);
                }
            }

            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            PdfPCell remarksCell1 = new PdfPCell(new Paragraph("备注", labelFont));
            remarksCell1.setBorder(Rectangle.BOX);
            remarksCell1.setPadding(4);
            remarksCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            remarksCell1.setVerticalAlignment(Element.ALIGN_TOP);
            remarksCell1.setRowspan(3);
            mainTable.addCell(remarksCell1);

            PdfPCell remarksCell2 = new PdfPCell(new Paragraph(remarks, valueFont));
            remarksCell2.setBorder(Rectangle.BOX);
            remarksCell2.setPadding(4);
            remarksCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            remarksCell2.setVerticalAlignment(Element.ALIGN_TOP);
            remarksCell2.setRowspan(3);
            remarksCell2.setColspan(10);
            mainTable.addCell(remarksCell2);

            document.add(mainTable);

            // 底部签字区：三列，文字居中，避免乱跑
            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);
            footerTable.setWidths(new float[]{1, 1, 1});

            // 与前端字段名对齐：使用 approver/reviewer/tester
            String approvalParam = request.getParameter("approver") != null
                    ? request.getParameter("approver")
                    : request.getParameter("approval");
            String approval = approvalParam != null ? approvalParam : "";
            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("批准：        " + approval, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("审核：        " + reviewer, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：        " + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            // 声明 + 公司信息区，使用带边框的表格，结构与页面相似，避免杂乱
            String companyName = request.getParameter("companyName") != null ? request.getParameter("companyName") : "";
            String companyPhone = request.getParameter("companyPhone") != null ? request.getParameter("companyPhone") : "";
            String companyAddress = request.getParameter("companyAddress") != null ? request.getParameter("companyAddress") : "";

            // 顶部粗横线（在“声明”之上）
            PdfPTable topLineTable = new PdfPTable(1);
            topLineTable.setWidthPercentage(100);
            topLineTable.setSpacingBefore(10);
            PdfPCell topLineCell = new PdfPCell(new Paragraph(" ", valueFont));
            topLineCell.setBorder(Rectangle.BOTTOM);
            topLineCell.setBorderWidthBottom(1.2f); // 略粗的横线
            topLineCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            topLineTable.addCell(topLineCell);
            document.add(topLineTable);

            // 声明行（无边框）
            PdfPTable statementTable = new PdfPTable(1);
            statementTable.setWidthPercentage(100);
            statementTable.setSpacingBefore(5);

            PdfPCell statementCell = new PdfPCell(new Paragraph("声明：1. 对本检测报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。", smallFont));
            statementCell.setBorder(Rectangle.NO_BORDER);
            statementCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            statementTable.addCell(statementCell);

            document.add(statementTable);

            // 公司名称 / 电话、公司地址、页码，使用 4 列表格，只保留下划线效果（BOTTOM 边），视觉上是“横线”而不是方框
            PdfPTable companyTable = new PdfPTable(4);
            companyTable.setWidthPercentage(100);
            companyTable.setSpacingBefore(5);
            companyTable.setWidths(new float[]{1.2f, 3.8f, 0.8f, 2.2f});

            PdfPCell nameLabelCell = new PdfPCell(new Paragraph("公司名称：", valueFont));
            nameLabelCell.setBorder(Rectangle.NO_BORDER);
            nameLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(nameLabelCell);

            PdfPCell nameValueCell = new PdfPCell(new Paragraph(companyName, valueFont));
            nameValueCell.setBorder(Rectangle.NO_BORDER);
            nameValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(nameValueCell);

            PdfPCell phoneLabelCell = new PdfPCell(new Paragraph("电话：", valueFont));
            phoneLabelCell.setBorder(Rectangle.NO_BORDER);
            phoneLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(phoneLabelCell);

            PdfPCell phoneValueCell = new PdfPCell(new Paragraph(companyPhone, valueFont));
            phoneValueCell.setBorder(Rectangle.NO_BORDER);
            phoneValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(phoneValueCell);

            PdfPCell addrLabelCell = new PdfPCell(new Paragraph("公司地址：", valueFont));
            addrLabelCell.setBorder(Rectangle.NO_BORDER);
            addrLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            addrLabelCell.setColspan(1);
            companyTable.addCell(addrLabelCell);

            PdfPCell addrValueCell = new PdfPCell(new Paragraph(companyAddress, valueFont));
            addrValueCell.setBorder(Rectangle.NO_BORDER);
            addrValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            addrValueCell.setColspan(3);
            companyTable.addCell(addrValueCell);

            // 公司地址下方粗横线
            PdfPCell bottomLineCell = new PdfPCell(new Paragraph(" ", valueFont));
            bottomLineCell.setBorder(Rectangle.BOTTOM);
            bottomLineCell.setBorderWidthBottom(1.2f);
            bottomLineCell.setColspan(4);
            bottomLineCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(bottomLineCell);

            PdfPCell pageCell = new PdfPCell(new Paragraph("第 1 页，共 1 页", valueFont));
            pageCell.setBorder(Rectangle.NO_BORDER);
            pageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageCell.setColspan(4);
            companyTable.addCell(pageCell);

            document.add(companyTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateDensityTestResultPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(10);

            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell headerCell = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(headerCell);

            document.add(headerTable);

            Paragraph title = new Paragraph("原位密度检测结果", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            // 10 列等宽，使列线与前端 HTML 对齐
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String maxDryDensity = request.getParameter("maxDryDensity") != null ? request.getParameter("maxDryDensity") : "";
            String optimumMoisture = request.getParameter("optimumMoisture") != null ? request.getParameter("optimumMoisture") : "";
            String minDryDensity = request.getParameter("minDryDensity") != null ? request.getParameter("minDryDensity") : "";
            String testMethod = request.getParameter("testMethod") != null ? request.getParameter("testMethod") : "";
            boolean isNuclearMethod = testMethod != null && testMethod.contains("核子");

            // 施工部位行：1 + 9 = 10
            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_LEFT, 9);

            // 最大/最小干密度 & 最优含水率：
            // 与前端表格结构保持一致：1(最大干密度label) + 2(值) + 1(最优含水率label) + 2(值) + 1(最小干密度label) + 3(值) = 10
            addCell(mainTable, "最大干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, maxDryDensity, valueFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "最优含水率 %", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, optimumMoisture, valueFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "最小干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, minDryDensity, valueFont, Element.ALIGN_CENTER, 3);

            // 表头：1 + 2 + 2 + 1 + 1 + 1 + 2 = 10
            addCell(mainTable, "样品编号", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "检测部位(桩号、高程)", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "湿密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "含水率%", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "压实度%", labelFont, Element.ALIGN_CENTER, 2);

            // 与前端保持一致：索引 0..19，对应 20 组；
            // 核子法：一行结构；其他方法：两行一组
            float detailRowHeight = 22f; // 明细行统一行高，避免空行“缩成一团”
            for (int i = 0; i < 20; i++) {
                String idx = String.valueOf(i);
                String sampleId = request.getParameter("sampleId_" + idx) != null ? request.getParameter("sampleId_" + idx) : "";
                String location = request.getParameter("location_" + idx) != null ? request.getParameter("location_" + idx) : "";
                String date = request.getParameter("date_" + idx) != null ? request.getParameter("date_" + idx) : "";
                String wetDensity = request.getParameter("wetDensity_" + idx) != null ? request.getParameter("wetDensity_" + idx) : "";
                String dryDensity = request.getParameter("dryDensity_" + idx) != null ? request.getParameter("dryDensity_" + idx) : "";
                String moisture = request.getParameter("moisture_" + idx) != null ? request.getParameter("moisture_" + idx) : "";
                String wetDensity2 = request.getParameter("wetDensity2_" + idx) != null ? request.getParameter("wetDensity2_" + idx) : "";
                String dryDensity2 = request.getParameter("dryDensity2_" + idx) != null ? request.getParameter("dryDensity2_" + idx) : "";
                String moisture2 = request.getParameter("moisture2_" + idx) != null ? request.getParameter("moisture2_" + idx) : "";
                String compaction = request.getParameter("compaction_" + idx) != null ? request.getParameter("compaction_" + idx) : "";

                if (isNuclearMethod) {
                    // 核子法：一行结构
                    PdfPCell sampleIdCell = new PdfPCell(new Paragraph(sampleId, valueFont));
                    sampleIdCell.setBorder(Rectangle.BOX);
                    sampleIdCell.setPadding(4);
                    sampleIdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    sampleIdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    sampleIdCell.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(sampleIdCell);

                    PdfPCell locationCell = new PdfPCell(new Paragraph(location, valueFont));
                    locationCell.setBorder(Rectangle.BOX);
                    locationCell.setPadding(4);
                    locationCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    locationCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    locationCell.setColspan(2);
                    locationCell.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(locationCell);

                    PdfPCell dateCell = new PdfPCell(new Paragraph(date, valueFont));
                    dateCell.setBorder(Rectangle.BOX);
                    dateCell.setPadding(4);
                    dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dateCell.setColspan(2);
                    dateCell.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(dateCell);

                    PdfPCell wetCell = new PdfPCell(new Paragraph(wetDensity, valueFont));
                    wetCell.setBorder(Rectangle.BOX);
                    wetCell.setPadding(4);
                    wetCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    wetCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    wetCell.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(wetCell);

                    PdfPCell dryCell = new PdfPCell(new Paragraph(dryDensity, valueFont));
                    dryCell.setBorder(Rectangle.BOX);
                    dryCell.setPadding(4);
                    dryCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dryCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dryCell.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(dryCell);

                    PdfPCell moistureCell = new PdfPCell(new Paragraph(moisture, valueFont));
                    moistureCell.setBorder(Rectangle.BOX);
                    moistureCell.setPadding(4);
                    moistureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    moistureCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    moistureCell.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(moistureCell);

                    PdfPCell compactionCell = new PdfPCell(new Paragraph(compaction, valueFont));
                    compactionCell.setBorder(Rectangle.BOX);
                    compactionCell.setPadding(4);
                    compactionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    compactionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    compactionCell.setColspan(2);
                    compactionCell.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(compactionCell);
                } else {
                    // 其他方法：两行结构
                    PdfPCell sampleIdCell = new PdfPCell(new Paragraph(sampleId, valueFont));
                    sampleIdCell.setBorder(Rectangle.BOX);
                    sampleIdCell.setPadding(4);
                    sampleIdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    sampleIdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    sampleIdCell.setRowspan(2);
                    sampleIdCell.setMinimumHeight(detailRowHeight * 2);
                    mainTable.addCell(sampleIdCell);

                    PdfPCell locationCell = new PdfPCell(new Paragraph(location, valueFont));
                    locationCell.setBorder(Rectangle.BOX);
                    locationCell.setPadding(4);
                    locationCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    locationCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    locationCell.setRowspan(2);
                    locationCell.setColspan(2);
                    locationCell.setMinimumHeight(detailRowHeight * 2);
                    mainTable.addCell(locationCell);

                    PdfPCell dateCell = new PdfPCell(new Paragraph(date, valueFont));
                    dateCell.setBorder(Rectangle.BOX);
                    dateCell.setPadding(4);
                    dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dateCell.setRowspan(2);
                    dateCell.setColspan(2);
                    dateCell.setMinimumHeight(detailRowHeight * 2);
                    mainTable.addCell(dateCell);

                    PdfPCell wetCell1 = new PdfPCell(new Paragraph(wetDensity, valueFont));
                    wetCell1.setBorder(Rectangle.BOX);
                    wetCell1.setPadding(4);
                    wetCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    wetCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    wetCell1.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(wetCell1);

                    PdfPCell dryCell1 = new PdfPCell(new Paragraph(dryDensity, valueFont));
                    dryCell1.setBorder(Rectangle.BOX);
                    dryCell1.setPadding(4);
                    dryCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dryCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dryCell1.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(dryCell1);

                    PdfPCell moistureCell1 = new PdfPCell(new Paragraph(moisture, valueFont));
                    moistureCell1.setBorder(Rectangle.BOX);
                    moistureCell1.setPadding(4);
                    moistureCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    moistureCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    moistureCell1.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(moistureCell1);

                    PdfPCell compactionCell = new PdfPCell(new Paragraph(compaction, valueFont));
                    compactionCell.setBorder(Rectangle.BOX);
                    compactionCell.setPadding(4);
                    compactionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    compactionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    compactionCell.setRowspan(2);
                    compactionCell.setColspan(2);
                    compactionCell.setMinimumHeight(detailRowHeight * 2);
                    mainTable.addCell(compactionCell);

                    PdfPCell wetCell2 = new PdfPCell(new Paragraph(wetDensity2, valueFont));
                    wetCell2.setBorder(Rectangle.BOX);
                    wetCell2.setPadding(4);
                    wetCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    wetCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    wetCell2.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(wetCell2);

                    PdfPCell dryCell2 = new PdfPCell(new Paragraph(dryDensity2, valueFont));
                    dryCell2.setBorder(Rectangle.BOX);
                    dryCell2.setPadding(4);
                    dryCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    dryCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    dryCell2.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(dryCell2);

                    PdfPCell moistureCell2 = new PdfPCell(new Paragraph(moisture2, valueFont));
                    moistureCell2.setBorder(Rectangle.BOX);
                    moistureCell2.setPadding(4);
                    moistureCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    moistureCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    moistureCell2.setMinimumHeight(detailRowHeight);
                    mainTable.addCell(moistureCell2);
                }
            }

            document.add(mainTable);

            // 底部仅保留页码信息，避免与“报告详情页”的声明区混淆
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);

            String page = request.getParameter("page") != null ? request.getParameter("page") : "1";
            String totalPages = request.getParameter("totalPages") != null ? request.getParameter("totalPages") : "1";

            PdfPCell pageCell = new PdfPCell(new Paragraph("第 " + page + " 页，共 " + totalPages + " 页", valueFont));
            pageCell.setBorder(Rectangle.NO_BORDER);
            pageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            footerTable.addCell(pageCell);

            document.add(footerTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateReboundMethodRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 8, Font.NORMAL);

            Paragraph title = new Paragraph("回弹法检测混凝土抗压强度记录表", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";
            String sampleNo = request.getParameter("sampleNo") != null ? request.getParameter("sampleNo") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setColspan(1);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setColspan(1);
            headerTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("样品编号：" + sampleNo, valueFont));
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setColspan(1);
            headerTable.addCell(cell3);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(21);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            float[] widths = new float[21];
            for (int i = 0; i < 21; i++) {
                widths[i] = 1;
            }
            mainTable.setWidths(widths);

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String structurePart = request.getParameter("structurePart") != null ? request.getParameter("structurePart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String reportDate = request.getParameter("reportDate") != null ? request.getParameter("reportDate") : "";
            String concreteGrade = request.getParameter("concreteGrade") != null ? request.getParameter("concreteGrade") : "";
            String moldingDate = request.getParameter("moldingDate") != null ? request.getParameter("moldingDate") : "";
            String age = request.getParameter("age") != null ? request.getParameter("age") : "";
            String instrumentModel = request.getParameter("instrumentModel") != null ? request.getParameter("instrumentModel") : "";
            String calibrationNo = request.getParameter("calibrationNo") != null ? request.getParameter("calibrationNo") : "";
            String zoneCount = request.getParameter("zoneCount") != null ? request.getParameter("zoneCount") : "";
            String pouringDirection = request.getParameter("pouringDirection") != null ? request.getParameter("pouringDirection") : "";
            String testAngle = request.getParameter("testAngle") != null ? request.getParameter("testAngle") : "";
            String pumpingMethod = request.getParameter("pumpingMethod") != null ? request.getParameter("pumpingMethod") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_LEFT, 13);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "结构部位", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, structurePart, valueFont, Element.ALIGN_CENTER, 5);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "报告日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, reportDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "混凝土强度等级", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, concreteGrade, valueFont, Element.ALIGN_CENTER, 5);
            addCell(mainTable, "成型日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, moldingDate, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "检测龄期(d)", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, age, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "回弹仪型号", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, instrumentModel, valueFont, Element.ALIGN_CENTER, 5);
            addCell(mainTable, "检定证号", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, calibrationNo, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "测区数", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, zoneCount, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "浇筑方向", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, pouringDirection, valueFont, Element.ALIGN_CENTER, 5);
            addCell(mainTable, "测试角度", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testAngle, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "泵送方式", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, pumpingMethod, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "测区", labelFont, Element.ALIGN_CENTER, 1);
            for (int i = 0; i < 16; i++) {
                addCell(mainTable, String.valueOf(i), labelFont, Element.ALIGN_CENTER, 1);
            }
            addCell(mainTable, "平均", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "碳化深度(mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "换算强度(MPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "修正强度(MPa)", labelFont, Element.ALIGN_CENTER, 1);

            for (int i = 1; i <= 10; i++) {
                String zone = request.getParameter("zone_" + i) != null ? request.getParameter("zone_" + i) : "";
                addCell(mainTable, zone, valueFont, Element.ALIGN_CENTER, 1);

                for (int j = 1; j <= 16; j++) {
                    String value = request.getParameter("value_" + i + "_" + j) != null ? request.getParameter("value_" + i + "_" + j) : "";
                    addCell(mainTable, value, smallFont, Element.ALIGN_CENTER, 1);
                }

                String average = request.getParameter("average_" + i) != null ? request.getParameter("average_" + i) : "";
                String carbonationDepth = request.getParameter("carbonationDepth_" + i) != null ? request.getParameter("carbonationDepth_" + i) : "";
                String conversionStrength = request.getParameter("conversionStrength_" + i) != null ? request.getParameter("conversionStrength_" + i) : "";
                String correctedStrength = request.getParameter("correctedStrength_" + i) != null ? request.getParameter("correctedStrength_" + i) : "";

                addCell(mainTable, average, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, carbonationDepth, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, conversionStrength, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, correctedStrength, valueFont, Element.ALIGN_CENTER, 1);
            }

            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_LEFT, 20);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_LEFT, 20);

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionCell2.setColspan(20);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, remarks, valueFont, Element.ALIGN_LEFT, 20);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);
            footerTable.setWidths(new float[]{1, 1, 1});

            String approver = request.getParameter("approver") != null ? request.getParameter("approver") : "";
            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("批准：" + approver, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.setSpacingBefore(10);
            dateTable.setWidths(new float[]{1, 1});

            String year = request.getParameter("year") != null ? request.getParameter("year") : "";
            String month = request.getParameter("month") != null ? request.getParameter("month") : "";
            String day = request.getParameter("day") != null ? request.getParameter("day") : "";

            PdfPCell dateCell1 = new PdfPCell(new Paragraph(year + "年" + month + "月" + day + "日", valueFont));
            dateCell1.setBorder(Rectangle.NO_BORDER);
            dateCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell1);

            PdfPCell dateCell2 = new PdfPCell(new Paragraph("第 1 页，共 1 页", valueFont));
            dateCell2.setBorder(Rectangle.NO_BORDER);
            dateCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell2);

            document.add(dateTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateSandReplacementRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("灌砂法检测记录表", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String soilType = request.getParameter("soilType") != null ? request.getParameter("soilType") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "土的种类", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, soilType, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "测点", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "试样湿重(g)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "试样干重(g)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "含水率(%)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "湿密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "最大干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "最小干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "压实系数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "合格率(%)", labelFont, Element.ALIGN_CENTER, 1);

            for (int i = 1; i <= 10; i++) {
                String point = request.getParameter("point_" + i) != null ? request.getParameter("point_" + i) : "";
                String wetWeight = request.getParameter("wetWeight_" + i) != null ? request.getParameter("wetWeight_" + i) : "";
                String dryWeight = request.getParameter("dryWeight_" + i) != null ? request.getParameter("dryWeight_" + i) : "";
                String moistureContent = request.getParameter("moistureContent_" + i) != null ? request.getParameter("moistureContent_" + i) : "";
                String wetDensity = request.getParameter("wetDensity_" + i) != null ? request.getParameter("wetDensity_" + i) : "";
                String dryDensity = request.getParameter("dryDensity_" + i) != null ? request.getParameter("dryDensity_" + i) : "";
                String maxDryDensity = request.getParameter("maxDryDensity_" + i) != null ? request.getParameter("maxDryDensity_" + i) : "";
                String minDryDensity = request.getParameter("minDryDensity_" + i) != null ? request.getParameter("minDryDensity_" + i) : "";
                String compactionCoefficient = request.getParameter("compactionCoefficient_" + i) != null ? request.getParameter("compactionCoefficient_" + i) : "";
                String qualifiedRate = request.getParameter("qualifiedRate_" + i) != null ? request.getParameter("qualifiedRate_" + i) : "";

                addCell(mainTable, point, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, wetWeight, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, dryWeight, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, moistureContent, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, wetDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, dryDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, maxDryDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, minDryDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, compactionCoefficient, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, qualifiedRate, valueFont, Element.ALIGN_CENTER, 1);
            }

            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_LEFT, 9);

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionCell2.setColspan(9);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, remarks, valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);
            footerTable.setWidths(new float[]{1, 1, 1});

            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String calculator = request.getParameter("calculator") != null ? request.getParameter("calculator") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("计算：" + calculator, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.setSpacingBefore(10);
            dateTable.setWidths(new float[]{1, 1});

            String year = request.getParameter("year") != null ? request.getParameter("year") : "";
            String month = request.getParameter("month") != null ? request.getParameter("month") : "";
            String day = request.getParameter("day") != null ? request.getParameter("day") : "";
            String page = request.getParameter("page") != null ? request.getParameter("page") : "1";
            String totalPages = request.getParameter("totalPages") != null ? request.getParameter("totalPages") : "1";

            PdfPCell dateCell1 = new PdfPCell(new Paragraph(year + "年" + month + "月" + day + "日", valueFont));
            dateCell1.setBorder(Rectangle.NO_BORDER);
            dateCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell1);

            PdfPCell dateCell2 = new PdfPCell(new Paragraph("第 " + page + " 页，共 " + totalPages + " 页", valueFont));
            dateCell2.setBorder(Rectangle.NO_BORDER);
            dateCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell2);

            document.add(dateTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateWaterReplacementRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("灌水法检测记录表", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String soilType = request.getParameter("soilType") != null ? request.getParameter("soilType") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "土的种类", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, soilType, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "测点", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "试坑体积(cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "试样湿重(g)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "试样干重(g)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "含水率(%)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "湿密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "最大干密度(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "压实系数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "合格率(%)", labelFont, Element.ALIGN_CENTER, 1);

            for (int i = 1; i <= 10; i++) {
                String point = request.getParameter("point_" + i) != null ? request.getParameter("point_" + i) : "";
                String pitVolume = request.getParameter("pitVolume_" + i) != null ? request.getParameter("pitVolume_" + i) : "";
                String wetWeight = request.getParameter("wetWeight_" + i) != null ? request.getParameter("wetWeight_" + i) : "";
                String dryWeight = request.getParameter("dryWeight_" + i) != null ? request.getParameter("dryWeight_" + i) : "";
                String moistureContent = request.getParameter("moistureContent_" + i) != null ? request.getParameter("moistureContent_" + i) : "";
                String wetDensity = request.getParameter("wetDensity_" + i) != null ? request.getParameter("wetDensity_" + i) : "";
                String dryDensity = request.getParameter("dryDensity_" + i) != null ? request.getParameter("dryDensity_" + i) : "";
                String maxDryDensity = request.getParameter("maxDryDensity_" + i) != null ? request.getParameter("maxDryDensity_" + i) : "";
                String compactionCoefficient = request.getParameter("compactionCoefficient_" + i) != null ? request.getParameter("compactionCoefficient_" + i) : "";
                String qualifiedRate = request.getParameter("qualifiedRate_" + i) != null ? request.getParameter("qualifiedRate_" + i) : "";

                addCell(mainTable, point, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, pitVolume, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, wetWeight, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, dryWeight, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, moistureContent, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, wetDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, dryDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, maxDryDensity, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, compactionCoefficient, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, qualifiedRate, valueFont, Element.ALIGN_CENTER, 1);
            }

            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_LEFT, 9);

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionCell2.setColspan(9);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, remarks, valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);
            footerTable.setWidths(new float[]{1, 1, 1});

            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String calculator = request.getParameter("calculator") != null ? request.getParameter("calculator") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("计算：" + calculator, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.setSpacingBefore(10);
            dateTable.setWidths(new float[]{1, 1});

            String year = request.getParameter("year") != null ? request.getParameter("year") : "";
            String month = request.getParameter("month") != null ? request.getParameter("month") : "";
            String day = request.getParameter("day") != null ? request.getParameter("day") : "";
            String page = request.getParameter("page") != null ? request.getParameter("page") : "1";
            String totalPages = request.getParameter("totalPages") != null ? request.getParameter("totalPages") : "1";

            PdfPCell dateCell1 = new PdfPCell(new Paragraph(year + "年" + month + "月" + day + "日", valueFont));
            dateCell1.setBorder(Rectangle.NO_BORDER);
            dateCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell1);

            PdfPCell dateCell2 = new PdfPCell(new Paragraph("第 " + page + " 页，共 " + totalPages + " 页", valueFont));
            dateCell2.setBorder(Rectangle.NO_BORDER);
            dateCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell2);

            document.add(dateTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateBeckmanBeamResultPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("路基路面回弹弯沉（回弹模量）检测结果", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(10);

            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell headerCell = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(headerCell);

            document.add(headerTable);

            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(5);
            infoTable.setSpacingAfter(10);
            infoTable.setWidths(new float[]{1, 2, 1, 2});

            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String deflectometerType = request.getParameter("deflectometerType") != null ? request.getParameter("deflectometerType") : "";
            String axleWeight = request.getParameter("axleWeight") != null ? request.getParameter("axleWeight") : "";
            String tirePressure = request.getParameter("tirePressure") != null ? request.getParameter("tirePressure") : "";

            addCell(infoTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, constructionPart, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "弯沉仪类型", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, deflectometerType, valueFont, Element.ALIGN_CENTER, 1);

            addCell(infoTable, "后轴重(kN)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, axleWeight, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "轮胎接地压强(MPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, tirePressure, valueFont, Element.ALIGN_CENTER, 1);

            document.add(infoTable);

            PdfPTable dataTable = new PdfPTable(7);
            dataTable.setWidthPercentage(100);
            dataTable.setSpacingBefore(10);
            dataTable.setSpacingAfter(10);
            dataTable.setWidths(new float[]{1.2f, 1.8f, 1.3f, 1.3f, 1.5f, 1.5f, 0.9f});

            addCell(dataTable, "测点桩号", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "初读数(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "终读数(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "回弹弯沉值(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "代表弯沉值(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "备注", labelFont, Element.ALIGN_CENTER, 1);

            for (int i = 1; i <= 25; i++) {
                String station = request.getParameter("station_" + i) != null ? request.getParameter("station_" + i) : "";
                String location = request.getParameter("lane_" + i) != null ? request.getParameter("lane_" + i) : "";
                String initialReading = request.getParameter("initialReading_" + i) != null ? request.getParameter("initialReading_" + i) : "";
                String finalReading = request.getParameter("finalReading_" + i) != null ? request.getParameter("finalReading_" + i) : "";
                String reboundDeflection = request.getParameter("left_val_" + i) != null ? request.getParameter("left_val_" + i) : "";
                String representativeDeflection = request.getParameter("right_val_" + i) != null ? request.getParameter("right_val_" + i) : "";
                String remarks = request.getParameter("remark_" + i) != null ? request.getParameter("remark_" + i) : "";

                addCell(dataTable, station, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, location, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, initialReading, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, finalReading, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, reboundDeflection, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, representativeDeflection, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, remarks, valueFont, Element.ALIGN_CENTER, 1);
            }

            document.add(dataTable);

            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);
            footerTable.setWidths(new float[]{1, 1});

            String year = request.getParameter("year") != null ? request.getParameter("year") : "";
            String month = request.getParameter("month") != null ? request.getParameter("month") : "";
            String day = request.getParameter("day") != null ? request.getParameter("day") : "";
            String page = request.getParameter("page") != null ? request.getParameter("page") : "1";
            String totalPages = request.getParameter("totalPages") != null ? request.getParameter("totalPages") : "1";

            PdfPCell dateCell = new PdfPCell(new Paragraph(year + "年" + month + "月" + day + "日", valueFont));
            dateCell.setBorder(Rectangle.NO_BORDER);
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(dateCell);

            PdfPCell pageCell = new PdfPCell(new Paragraph("第 " + page + " 页，共 " + totalPages + " 页", valueFont));
            pageCell.setBorder(Rectangle.NO_BORDER);
            pageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(pageCell);

            document.add(footerTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateBeckmanBeamRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("贝克曼梁检测记录", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String subgradeType = request.getParameter("subgradeType") != null ? request.getParameter("subgradeType") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "路基类型", labelFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, subgradeType, valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "测点", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "回弹弯沉值(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "测试车轴载(kg)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "温度修正系数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "湿度修正系数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "季节影响系数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "计算弯沉值(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "设计弯沉值(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "是否合格", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "合格率(%)", labelFont, Element.ALIGN_CENTER, 1);

            for (int i = 1; i <= 10; i++) {
                String point = request.getParameter("point_" + i) != null ? request.getParameter("point_" + i) : "";
                String deflectionValue = request.getParameter("deflectionValue_" + i) != null ? request.getParameter("deflectionValue_" + i) : "";
                String axleLoad = request.getParameter("axleLoad_" + i) != null ? request.getParameter("axleLoad_" + i) : "";
                String tempCorrection = request.getParameter("tempCorrection_" + i) != null ? request.getParameter("tempCorrection_" + i) : "";
                String humidityCorrection = request.getParameter("humidityCorrection_" + i) != null ? request.getParameter("humidityCorrection_" + i) : "";
                String seasonCoefficient = request.getParameter("seasonCoefficient_" + i) != null ? request.getParameter("seasonCoefficient_" + i) : "";
                String calculatedDeflection = request.getParameter("calculatedDeflection_" + i) != null ? request.getParameter("calculatedDeflection_" + i) : "";
                String designDeflection = request.getParameter("designDeflection_" + i) != null ? request.getParameter("designDeflection_" + i) : "";
                String isQualified = request.getParameter("isQualified_" + i) != null ? request.getParameter("isQualified_" + i) : "";
                String qualifiedRate = request.getParameter("qualifiedRate_" + i) != null ? request.getParameter("qualifiedRate_" + i) : "";

                addCell(mainTable, point, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, deflectionValue, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, axleLoad, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, tempCorrection, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, humidityCorrection, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, seasonCoefficient, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, calculatedDeflection, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, designDeflection, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, isQualified, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, qualifiedRate, valueFont, Element.ALIGN_CENTER, 1);
            }

            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_LEFT, 9);

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionCell2.setColspan(9);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, remarks, valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(10);
            footerTable.setWidths(new float[]{1, 1, 1});

            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String calculator = request.getParameter("calculator") != null ? request.getParameter("calculator") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";
            String year = request.getParameter("year") != null ? request.getParameter("year") : "";
            String month = request.getParameter("month") != null ? request.getParameter("month") : "";
            String day = request.getParameter("day") != null ? request.getParameter("day") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("计算：" + calculator, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            PdfPCell footerCell4 = new PdfPCell();
            footerCell4.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell4);

            PdfPCell footerCell5 = new PdfPCell();
            footerCell5.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell5);

            PdfPCell footerCell6 = new PdfPCell(new Paragraph("日期：" + year + "年" + month + "月" + day + "日", valueFont));
            footerCell6.setBorder(Rectangle.NO_BORDER);
            footerCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell6);

            document.add(footerTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateBeckmanBeamReportPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("路基路面回弹弯沉（回弹模量）检测报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(5);
            infoTable.setSpacingAfter(5);
            infoTable.setWidths(new float[]{1, 2, 1, 2});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String equipmentCode = request.getParameter("equipmentCode") != null ? request.getParameter("equipmentCode") : "";
            String reportDate = request.getParameter("reportDate") != null ? request.getParameter("reportDate") : "";
            String sampleStatus = request.getParameter("sampleStatus") != null ? request.getParameter("sampleStatus") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";
            String standard = request.getParameter("standard") != null ? request.getParameter("standard") : "";
            String testMethod = request.getParameter("testMethod") != null ? request.getParameter("testMethod") : "";
            String witnessUnit = request.getParameter("witnessUnit") != null ? request.getParameter("witnessUnit") : "";
            String witness = request.getParameter("witness") != null ? request.getParameter("witness") : "";
            String deflectometerType = request.getParameter("deflectometerType") != null ? request.getParameter("deflectometerType") : "";
            String axleWeight = request.getParameter("axleWeight") != null ? request.getParameter("axleWeight") : "";
            String tirePressure = request.getParameter("tirePressure") != null ? request.getParameter("tirePressure") : "";
            String testLength = request.getParameter("testLength") != null ? request.getParameter("testLength") : "";

            addCell(infoTable, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, projectName, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, commissionDate, valueFont, Element.ALIGN_CENTER, 1);

            addCell(infoTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, constructionPart, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, testDate, valueFont, Element.ALIGN_CENTER, 1);

            addCell(infoTable, "仪器设备及编码", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, equipmentCode, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "报告日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, reportDate, valueFont, Element.ALIGN_CENTER, 1);

            addCell(infoTable, "样品名称及状态", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, sampleStatus, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, testCategory, valueFont, Element.ALIGN_CENTER, 1);

            addCell(infoTable, "依据标准", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, standard, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "检测方法", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, testMethod, valueFont, Element.ALIGN_CENTER, 1);

            addCell(infoTable, "见证单位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, witnessUnit, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "见证人", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, witness, valueFont, Element.ALIGN_CENTER, 1);

            addCell(infoTable, "弯沉仪类型", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, deflectometerType, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "后轴重(kN)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, axleWeight, valueFont, Element.ALIGN_CENTER, 1);

            addCell(infoTable, "轮胎接地压强(MPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, tirePressure, valueFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, "检测段长度(m)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(infoTable, testLength, valueFont, Element.ALIGN_CENTER, 1);

            document.add(infoTable);

            PdfPTable dataTable = new PdfPTable(7);
            dataTable.setWidthPercentage(100);
            dataTable.setSpacingBefore(10);
            dataTable.setSpacingAfter(10);
            dataTable.setWidths(new float[]{1.2f, 1.8f, 1.3f, 1.3f, 1.5f, 1.5f, 0.9f});

            addCell(dataTable, "测点桩号", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "初读数(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "终读数(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "回弹弯沉值(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "代表弯沉值(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(dataTable, "备注", labelFont, Element.ALIGN_CENTER, 1);

            for (int i = 1; i <= 15; i++) {
                String station = request.getParameter("station_" + i) != null ? request.getParameter("station_" + i) : "";
                String location = request.getParameter("location_" + i) != null ? request.getParameter("location_" + i) : "";
                String initialReading = request.getParameter("initialReading_" + i) != null ? request.getParameter("initialReading_" + i) : "";
                String finalReading = request.getParameter("finalReading_" + i) != null ? request.getParameter("finalReading_" + i) : "";
                String reboundDeflection = request.getParameter("reboundDeflection_" + i) != null ? request.getParameter("reboundDeflection_" + i) : "";
                String representativeDeflection = request.getParameter("representativeDeflection_" + i) != null ? request.getParameter("representativeDeflection_" + i) : "";
                String rowRemarks = request.getParameter("remarks_" + i) != null ? request.getParameter("remarks_" + i) : "";

                addCell(dataTable, station, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, location, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, initialReading, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, finalReading, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, reboundDeflection, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, representativeDeflection, valueFont, Element.ALIGN_CENTER, 1);
                addCell(dataTable, rowRemarks, valueFont, Element.ALIGN_CENTER, 1);
            }

            document.add(dataTable);

            PdfPTable conclusionTable = new PdfPTable(2);
            conclusionTable.setWidthPercentage(100);
            conclusionTable.setSpacingBefore(5);
            conclusionTable.setSpacingAfter(5);
            conclusionTable.setWidths(new float[]{0.8f, 3.2f});

            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String remarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            conclusionTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionTable.addCell(conclusionCell2);

            PdfPCell remarksCell1 = new PdfPCell(new Paragraph("备注", labelFont));
            remarksCell1.setBorder(Rectangle.BOX);
            remarksCell1.setPadding(4);
            remarksCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            remarksCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionTable.addCell(remarksCell1);

            PdfPCell remarksCell2 = new PdfPCell(new Paragraph(remarks, valueFont));
            remarksCell2.setBorder(Rectangle.BOX);
            remarksCell2.setPadding(4);
            remarksCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            remarksCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionTable.addCell(remarksCell2);

            document.add(conclusionTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);
            footerTable.setWidths(new float[]{1, 1, 1});

            String approval = request.getParameter("approval") != null ? request.getParameter("approval") : "";
            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("批准：" + approval, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            PdfPTable companyTable = new PdfPTable(1);
            companyTable.setWidthPercentage(100);
            companyTable.setSpacingBefore(20);
            companyTable.setSpacingAfter(10);

            String companyName = request.getParameter("companyName") != null ? request.getParameter("companyName") : "";
            String companyPhone = request.getParameter("companyPhone") != null ? request.getParameter("companyPhone") : "";
            String companyAddress = request.getParameter("companyAddress") != null ? request.getParameter("companyAddress") : "";
            String footerDate = request.getParameter("footerDate") != null ? request.getParameter("footerDate") : "";

            PdfPCell companyCell1 = new PdfPCell(new Paragraph("声明：1. 对本检测报告的复印件未加盖公司检验检测专用章无效。2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。", smallFont));
            companyCell1.setBorder(Rectangle.NO_BORDER);
            companyCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(companyCell1);

            PdfPTable companyInfoTable = new PdfPTable(2);
            companyInfoTable.setWidthPercentage(100);
            companyInfoTable.setSpacingBefore(10);
            companyInfoTable.setWidths(new float[]{1, 1});

            PdfPCell companyInfoCell1 = new PdfPCell(new Paragraph("公司名称：" + companyName, smallFont));
            companyInfoCell1.setBorder(Rectangle.NO_BORDER);
            companyInfoCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyInfoTable.addCell(companyInfoCell1);

            PdfPCell companyInfoCell2 = new PdfPCell(new Paragraph("电话：" + companyPhone, smallFont));
            companyInfoCell2.setBorder(Rectangle.NO_BORDER);
            companyInfoCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            companyInfoTable.addCell(companyInfoCell2);

            companyTable.addCell(companyInfoTable);

            PdfPCell companyCell2 = new PdfPCell(new Paragraph("公司地址：" + companyAddress, smallFont));
            companyCell2.setBorder(Rectangle.NO_BORDER);
            companyCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(companyCell2);

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.setSpacingBefore(10);
            dateTable.setWidths(new float[]{1, 1});

            PdfPCell dateCell1 = new PdfPCell(new Paragraph(footerDate, smallFont));
            dateCell1.setBorder(Rectangle.NO_BORDER);
            dateCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell1);

            PdfPCell dateCell2 = new PdfPCell(new Paragraph("第 1 页，共 1 页", smallFont));
            dateCell2.setBorder(Rectangle.NO_BORDER);
            dateCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell2);

            companyTable.addCell(dateTable);

            document.add(companyTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateReboundMethodReportPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 9, Font.NORMAL);

            Paragraph title = new Paragraph("回弹法检测混凝土抗压强度报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(5);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            String entrustingUnit = request.getParameter("entrustingUnit") != null ? request.getParameter("entrustingUnit") : "";
            String unifiedNumber = request.getParameter("unifiedNumber") != null ? request.getParameter("unifiedNumber") : "";

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + entrustingUnit, valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + unifiedNumber, valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            mainTable.setSpacingAfter(5);
            mainTable.setWidths(new float[]{1, 2, 1, 1, 1, 1, 1, 1, 1, 1});

            String projectName = request.getParameter("projectName") != null ? request.getParameter("projectName") : "";
            String commissionDate = request.getParameter("commissionDate") != null ? request.getParameter("commissionDate") : "";
            String constructionPart = request.getParameter("constructionPart") != null ? request.getParameter("constructionPart") : "";
            String testDate = request.getParameter("testDate") != null ? request.getParameter("testDate") : "";
            String sampleNameStatus = request.getParameter("sampleNameStatus") != null ? request.getParameter("sampleNameStatus") : "";
            String reportDate = request.getParameter("reportDate") != null ? request.getParameter("reportDate") : "";
            String equipment = request.getParameter("equipment") != null ? request.getParameter("equipment") : "";
            String testMethod = request.getParameter("testMethod") != null ? request.getParameter("testMethod") : "";
            String testBasis = request.getParameter("testBasis") != null ? request.getParameter("testBasis") : "";
            String testCategory = request.getParameter("testCategory") != null ? request.getParameter("testCategory") : "";
            String designStrength = request.getParameter("designStrength") != null ? request.getParameter("designStrength") : "";
            String carbonationDepth = request.getParameter("carbonationDepth") != null ? request.getParameter("carbonationDepth") : "";
            String pumping = request.getParameter("pumping") != null ? request.getParameter("pumping") : "";
            String testResult = request.getParameter("testResult") != null ? request.getParameter("testResult") : "";

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, projectName, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, commissionDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, constructionPart, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "样品名称及状态", labelFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, sampleNameStatus, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "报告日期", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, reportDate, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, equipment, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "检测方法", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testMethod, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, testBasis, valueFont, Element.ALIGN_CENTER, 6);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, testCategory, valueFont, Element.ALIGN_CENTER, 3);

            addCell(mainTable, "设计强度等级", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, designStrength, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "碳化深度(mm)", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, carbonationDepth, valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "泵送情况", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, pumping, valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "检测结果", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, testResult, valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "构件名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "构件部位", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "回弹值平均值", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "强度换算值(MPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "强度推定值(MPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 2);

            for (int i = 1; i <= 12; i++) {
                String componentName = request.getParameter("componentName_" + i) != null ? request.getParameter("componentName_" + i) : "";
                String componentPart = request.getParameter("componentPart_" + i) != null ? request.getParameter("componentPart_" + i) : "";
                String date = request.getParameter("date_" + i) != null ? request.getParameter("date_" + i) : "";
                String reboundAvg = request.getParameter("reboundAvg_" + i) != null ? request.getParameter("reboundAvg_" + i) : "";
                String strengthConversion = request.getParameter("strengthConversion_" + i) != null ? request.getParameter("strengthConversion_" + i) : "";
                String strengthEstimation = request.getParameter("strengthEstimation_" + i) != null ? request.getParameter("strengthEstimation_" + i) : "";
                String remarks = request.getParameter("remarks_" + i) != null ? request.getParameter("remarks_" + i) : "";

                addCell(mainTable, componentName, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, componentPart, valueFont, Element.ALIGN_CENTER, 2);
                addCell(mainTable, date, valueFont, Element.ALIGN_CENTER, 2);
                addCell(mainTable, reboundAvg, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, strengthConversion, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, strengthEstimation, valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, remarks, valueFont, Element.ALIGN_CENTER, 2);
            }

            String conclusion = request.getParameter("conclusion") != null ? request.getParameter("conclusion") : "";
            String overallRemarks = request.getParameter("remarks") != null ? request.getParameter("remarks") : "";

            PdfPCell conclusionCell1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            conclusionCell1.setBorder(Rectangle.BOX);
            conclusionCell1.setPadding(4);
            conclusionCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionCell1.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell1.setRowspan(2);
            mainTable.addCell(conclusionCell1);

            PdfPCell conclusionCell2 = new PdfPCell(new Paragraph(conclusion, valueFont));
            conclusionCell2.setBorder(Rectangle.BOX);
            conclusionCell2.setPadding(4);
            conclusionCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell2.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell2.setRowspan(2);
            conclusionCell2.setColspan(9);
            mainTable.addCell(conclusionCell2);

            addCell(mainTable, "备 注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, overallRemarks, valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);
            footerTable.setWidths(new float[]{1, 1, 1});

            String approval = request.getParameter("approval") != null ? request.getParameter("approval") : "";
            String reviewer = request.getParameter("reviewer") != null ? request.getParameter("reviewer") : "";
            String tester = request.getParameter("tester") != null ? request.getParameter("tester") : "";

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("批准：" + approval, valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("审核：" + reviewer, valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + tester, valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            Paragraph statement = new Paragraph("声明：\n1. 对本检测报告的复印件未加盖公司检验检测专用章无效。 2. 对检验结果如有异议，应在收到报告之日起十五日之内向本公司提出。", smallFont);
            statement.setAlignment(Element.ALIGN_LEFT);
            statement.setSpacingBefore(15);
            statement.setSpacingAfter(15);
            document.add(statement);

            PdfPTable companyTable = new PdfPTable(2);
            companyTable.setWidthPercentage(100);
            companyTable.setSpacingBefore(5);
            companyTable.setSpacingAfter(5);
            companyTable.setWidths(new float[]{1, 1});

            String companyName = request.getParameter("companyName") != null ? request.getParameter("companyName") : "";
            String companyPhone = request.getParameter("companyPhone") != null ? request.getParameter("companyPhone") : "";
            String companyAddress = request.getParameter("companyAddress") != null ? request.getParameter("companyAddress") : "";

            PdfPCell companyCell1 = new PdfPCell(new Paragraph("公司名称：" + companyName, valueFont));
            companyCell1.setBorder(Rectangle.NO_BORDER);
            companyCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyTable.addCell(companyCell1);

            PdfPCell companyCell2 = new PdfPCell(new Paragraph("电话：" + companyPhone, valueFont));
            companyCell2.setBorder(Rectangle.NO_BORDER);
            companyCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            companyTable.addCell(companyCell2);

            document.add(companyTable);

            PdfPTable addressTable = new PdfPTable(1);
            addressTable.setWidthPercentage(100);
            addressTable.setSpacingBefore(5);
            addressTable.setSpacingAfter(5);

            PdfPCell addressCell = new PdfPCell(new Paragraph("公司地址：" + companyAddress, valueFont));
            addressCell.setBorder(Rectangle.NO_BORDER);
            addressCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            addressTable.addCell(addressCell);

            document.add(addressTable);

            PdfPTable dateTable = new PdfPTable(2);
            dateTable.setWidthPercentage(100);
            dateTable.setSpacingBefore(15);
            dateTable.setWidths(new float[]{1, 1});

            String footerDate = request.getParameter("footerDate") != null ? request.getParameter("footerDate") : "";
            String page = request.getParameter("page") != null ? request.getParameter("page") : "1";
            String totalPages = request.getParameter("totalPages") != null ? request.getParameter("totalPages") : "1";

            PdfPCell dateCell1 = new PdfPCell(new Paragraph(footerDate, valueFont));
            dateCell1.setBorder(Rectangle.NO_BORDER);
            dateCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell1);

            PdfPCell dateCell2 = new PdfPCell(new Paragraph("第 " + page + " 页，共 " + totalPages + " 页", valueFont));
            dateCell2.setBorder(Rectangle.NO_BORDER);
            dateCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            dateTable.addCell(dateCell2);

            document.add(dateTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private byte[] generateGenericPdf(HttpServletRequest request, String title) {
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(20);
            document.add(titlePara);

            Paragraph content = new Paragraph("报告内容将在此显示", valueFont);
            content.setAlignment(Element.ALIGN_CENTER);
            document.add(content);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
