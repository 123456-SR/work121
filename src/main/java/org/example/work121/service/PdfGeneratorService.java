package org.example.work121.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    private void addBackgroundAndFooter(Document document, PdfWriter writer, String version, String date, String currentPage, String totalPages) {
        try {
            // Add Footer Text
            // Position: Below the bottom line. Assuming bottom line is at ~100pt from bottom.
            // We'll place text at ~30pt from bottom.
            PdfContentByte canvas = writer.getDirectContentUnder();
            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font footerFont = new Font(chineseFont, 10, Font.NORMAL);

            float yPos = document.bottom() - 10;
            float xLeft = document.left();
            float xCenter = (document.left() + document.right()) / 2;
            float xRight = document.right();

            // Version (Left)
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, 
                new Phrase("版次：" + (version != null ? version : ""), footerFont), 
                xLeft, yPos, 0);

            // Date (Center)
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, 
                new Phrase(date != null ? date : "", footerFont), 
                xCenter, yPos, 0);

            // Page Number (Right)
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, 
                new Phrase("第 " + (currentPage != null ? currentPage : "1") + " 页，共 " + (totalPages != null ? totalPages : "1") + " 页", footerFont), 
                xRight, yPos, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] generateEntrustmentPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.NORMAL);
            Font labelFont = new Font(chineseFont, 12, Font.NORMAL);
            Font valueFont = new Font(chineseFont, 12, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("检测 (验) 委托单 (代合同)", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10);
            headerTable.setSpacingAfter(10);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("样品编号：" + getParameter(request, "sampleNumber"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(4);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(10);
            mainTable.setSpacingAfter(10);
            mainTable.setWidths(new float[]{1, 2, 1, 2});

            addRow4Col(mainTable, "委托单位：", getParameter(request, "clientUnit"), "委托日期：", getParameter(request, "clientDate"), labelFont, valueFont);

            addRow4Col(mainTable, "施工单位：", getParameter(request, "constructionUnit"), "建设单位：", getParameter(request, "buildingUnit"), labelFont, valueFont);

            PdfPCell cell3 = new PdfPCell(new Paragraph("工程名称：", labelFont));
            cell3.setBorder(Rectangle.BOX);
            cell3.setPadding(8);
            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Paragraph(getParameter(request, "projectName"), valueFont));
            cell4.setColspan(2);
            cell4.setBorder(Rectangle.BOX);
            cell4.setPadding(8);
            cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell4);

            PdfPCell cell5 = new PdfPCell();
            cell5.setBorder(Rectangle.BOX);
            cell5.setPadding(8);
            Paragraph p5 = new Paragraph();
            p5.add(new Chunk("施工 (使用) 部位：", labelFont));
            p5.add(new Chunk("\n" + getParameter(request, "constructionPart"), valueFont));
            cell5.addElement(p5);
            mainTable.addCell(cell5);

            PdfPTable nestedTable1 = new PdfPTable(6);
            nestedTable1.setWidths(new float[]{1, 2, 1, 2, 1, 2});
            nestedTable1.setWidthPercentage(100);

            addCellToNested(nestedTable1, "样品名称：", labelFont, Element.ALIGN_LEFT);
            addCellToNested(nestedTable1, getParameter(request, "sampleName"), valueFont, Element.ALIGN_LEFT);
            addCellToNested(nestedTable1, "规格/型号：", labelFont, Element.ALIGN_LEFT);
            addCellToNested(nestedTable1, getParameter(request, "spec"), valueFont, Element.ALIGN_LEFT);
            addCellToNested(nestedTable1, "生产厂家或产地：", labelFont, Element.ALIGN_LEFT);
            addCellToNested(nestedTable1, getParameter(request, "manufacturer"), valueFont, Element.ALIGN_LEFT);

            PdfPCell nestedCell1 = new PdfPCell(nestedTable1);
            nestedCell1.setColspan(4);
            nestedCell1.setBorder(Rectangle.BOX);
            nestedCell1.setPadding(0);
            mainTable.addCell(nestedCell1);

            PdfPTable nestedTable2 = new PdfPTable(4);
            nestedTable2.setWidths(new float[]{1, 1, 1, 1});
            nestedTable2.setWidthPercentage(100);

            addCellToNestedCenter(nestedTable2, "样品数量：", labelFont);
            addCellToNestedCenter(nestedTable2, getParameter(request, "sampleQuantity"), valueFont);
            addCellToNestedCenter(nestedTable2, "代表批量：", labelFont);
            addCellToNestedCenter(nestedTable2, getParameter(request, "representativeBatch"), valueFont);

            addCellToNestedCenter(nestedTable2, "批号：", labelFont);
            addCellToNestedCenter(nestedTable2, getParameter(request, "batchNumber"), valueFont);
            addCellToNestedCenter(nestedTable2, "检测(验)类别：", labelFont);
            addCellToNestedCenter(nestedTable2, getParameter(request, "testCategory"), valueFont);

            PdfPCell nestedCell2 = new PdfPCell(nestedTable2);
            nestedCell2.setColspan(4);
            nestedCell2.setBorder(Rectangle.BOX);
            nestedCell2.setPadding(0);
            mainTable.addCell(nestedCell2);

            PdfPCell cell6 = new PdfPCell();
            cell6.setColspan(2);
            cell6.setBorder(Rectangle.BOX);
            cell6.setPadding(8);
            Paragraph p6 = new Paragraph();
            p6.add(new Chunk("样品历史及概况：", labelFont));
            p6.add(new Chunk("\n" + getParameter(request, "sampleHistory"), valueFont));
            cell6.addElement(p6);
            mainTable.addCell(cell6);

            PdfPCell cell7 = new PdfPCell();
            cell7.setColspan(2);
            cell7.setBorder(Rectangle.BOX);
            cell7.setPadding(8);
            Paragraph p7 = new Paragraph();
            p7.add(new Chunk("样品状态：", labelFont));
            p7.add(new Chunk("\n" + getParameter(request, "sampleStatus"), valueFont));
            cell7.addElement(p7);
            mainTable.addCell(cell7);

            PdfPCell cell8 = new PdfPCell();
            cell8.setColspan(4);
            cell8.setBorder(Rectangle.BOX);
            cell8.setPadding(8);
            Paragraph p8 = new Paragraph();
            p8.add(new Chunk("检测(验)项目及依据：", labelFont));
            p8.add(new Chunk("\n" + getParameter(request, "testItems"), valueFont));
            cell8.addElement(p8);
            mainTable.addCell(cell8);

            PdfPCell cell9 = new PdfPCell(new Paragraph("委托单位地址及电话(传真)：", labelFont));
            cell9.setBorder(Rectangle.BOX);
            cell9.setPadding(8);
            cell9.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell9);

            PdfPCell cell10 = new PdfPCell(new Paragraph(getParameter(request, "clientAddressPhone"), valueFont));
            cell10.setColspan(3);
            cell10.setBorder(Rectangle.BOX);
            cell10.setPadding(8);
            cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell10);

            addRow4Col(mainTable, "报告发送：", getReportSendText(request), "样品处置：", getSampleDisposalText(request), labelFont, valueFont);

            addRow4Col(mainTable, "见证人：", getParameter(request, "witness"), "见证单位：", getParameter(request, "witnessUnit"), labelFont, valueFont);

            PdfPCell cell11 = new PdfPCell(new Paragraph("检测报告交付日期：", labelFont));
            cell11.setBorder(Rectangle.BOX);
            cell11.setPadding(8);
            cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell11);

            PdfPCell cell12 = new PdfPCell(new Paragraph("可选择日期选项", valueFont));
            cell12.setBorder(Rectangle.BOX);
            cell12.setPadding(8);
            cell12.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell12);

            PdfPCell cell13 = new PdfPCell(new Paragraph("应缴检测(验)费(元)：", labelFont));
            cell13.setBorder(Rectangle.BOX);
            cell13.setPadding(8);
            cell13.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell13);

            PdfPCell cell14 = new PdfPCell(new Paragraph(getParameter(request, "fee"), valueFont));
            cell14.setBorder(Rectangle.BOX);
            cell14.setPadding(8);
            cell14.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell14);

            PdfPCell cell15 = new PdfPCell(new Paragraph("备注：", labelFont));
            cell15.setBorder(Rectangle.BOX);
            cell15.setPadding(8);
            cell15.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell15);

            PdfPCell cell16 = new PdfPCell(new Paragraph(getParameter(request, "remarks"), valueFont));
            cell16.setColspan(3);
            cell16.setBorder(Rectangle.BOX);
            cell16.setPadding(8);
            cell16.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell16);

            PdfPCell cell17 = new PdfPCell(new Paragraph("说明：", labelFont));
            cell17.setBorder(Rectangle.BOX);
            cell17.setPadding(8);
            cell17.setVerticalAlignment(Element.ALIGN_TOP);
            cell17.setHorizontalAlignment(Element.ALIGN_LEFT);
            mainTable.addCell(cell17);

            PdfPCell cell18 = new PdfPCell();
            cell18.setColspan(3);
            cell18.setBorder(Rectangle.BOX);
            cell18.setPadding(8);
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
            footerTable.setSpacingBefore(20);

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("委托(送样)人：________________________", valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("承接(收样)人：________________________", valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            footerTable.addCell(footerCell2);

            document.add(footerTable);

            PdfPTable pageFooterTable = new PdfPTable(3);
            pageFooterTable.setWidthPercentage(100);
            pageFooterTable.setSpacingBefore(10);

            PdfPCell pageCell1 = new PdfPCell(new Paragraph("版次：________", valueFont));
            pageCell1.setBorder(Rectangle.NO_BORDER);
            pageCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageFooterTable.addCell(pageCell1);

            PdfPCell pageCell2 = new PdfPCell(new Paragraph("____年____月____日", valueFont));
            pageCell2.setBorder(Rectangle.NO_BORDER);
            pageCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageFooterTable.addCell(pageCell2);

            PdfPCell pageCell3 = new PdfPCell(new Paragraph("第 ____ 页，共 ____ 页", valueFont));
            pageCell3.setBorder(Rectangle.NO_BORDER);
            pageCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageFooterTable.addCell(pageCell3);

            document.add(pageFooterTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }







    private String getParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        return value != null ? value : "";
    }

    private String getReportSendText(HttpServletRequest request) {
        String[] values = request.getParameterValues("reportSend");
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

    private String getSampleDisposalText(HttpServletRequest request) {
        String[] values = request.getParameterValues("sampleDisposal");
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
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.NORMAL);
            Font labelFont = new Font(chineseFont, 12, Font.NORMAL);
            Font valueFont = new Font(chineseFont, 12, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("轻型动力触探检测报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10);
            headerTable.setSpacingAfter(10);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "entrustingUnit"), labelFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), labelFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(10);
            mainTable.setSpacingAfter(10);
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "projectName"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "entrustDate"), valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "constructionPart"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "testDate"), valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "岩土性状", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "soilProperty"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "报告日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "reportDate"), valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "见证单位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "witnessUnit"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "见证人", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "witness"), valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "设计\n承载力\n(kPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "designCapacity"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "锤重量\n(kg)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "hammerWeight"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "落距\n(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "dropDistance"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "testCategory"), valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入\n深度\n(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测\n锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均\n锤击数\nN10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力\n特征值\n(kPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "测点位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入\n深度\n(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测\n锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均\n锤击数\nN10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力\n特征值\n(kPa)", labelFont, Element.ALIGN_CENTER, 1);

            for (int block = 0; block < 4; block++) {
                for (int sub = 0; sub < 2; sub++) {
                    int globalRowIndex = block * 2 + sub;

                    // 左栏：测点位置（rowspan=2）
                    if (sub == 0) {
                        PdfPCell posCell = new PdfPCell(new Paragraph(getParameter(request, "pos_L_" + block), valueFont));
                        posCell.setBorder(Rectangle.BOX);
                        posCell.setPadding(5);
                        posCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        posCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        posCell.setRowspan(2);
                        mainTable.addCell(posCell);
                    }

                    // 左栏：贯入深度和实测锤击数（每行都有）
                    addCell(mainTable, getParameter(request, "depth_L_" + globalRowIndex), valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, getParameter(request, "actual_L_" + globalRowIndex), valueFont, Element.ALIGN_CENTER, 1);

                    // 左栏：平均锤击数和承载力特征值（rowspan=2）
                    if (sub == 0) {
                        PdfPCell avgCell = new PdfPCell(new Paragraph(getParameter(request, "avg_L_" + block), valueFont));
                        avgCell.setBorder(Rectangle.BOX);
                        avgCell.setPadding(5);
                        avgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        avgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        avgCell.setRowspan(2);
                        mainTable.addCell(avgCell);

                        PdfPCell capacityCell = new PdfPCell(new Paragraph(getParameter(request, "capacity_L_" + block), valueFont));
                        capacityCell.setBorder(Rectangle.BOX);
                        capacityCell.setPadding(5);
                        capacityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        capacityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        capacityCell.setRowspan(2);
                        mainTable.addCell(capacityCell);
                    }

                    // 右栏：测点位置（rowspan=2）
                    if (sub == 0) {
                        PdfPCell posCellR = new PdfPCell(new Paragraph(getParameter(request, "pos_R_" + block), valueFont));
                        posCellR.setBorder(Rectangle.BOX);
                        posCellR.setPadding(5);
                        posCellR.setHorizontalAlignment(Element.ALIGN_CENTER);
                        posCellR.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        posCellR.setRowspan(2);
                        mainTable.addCell(posCellR);
                    }

                    // 右栏：贯入深度和实测锤击数（每行都有）
                    addCell(mainTable, getParameter(request, "depth_R_" + globalRowIndex), valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, getParameter(request, "actual_R_" + globalRowIndex), valueFont, Element.ALIGN_CENTER, 1);

                    // 右栏：平均锤击数和承载力特征值（rowspan=2）
                    if (sub == 0) {
                        PdfPCell avgCellR = new PdfPCell(new Paragraph(getParameter(request, "avg_R_" + block), valueFont));
                        avgCellR.setBorder(Rectangle.BOX);
                        avgCellR.setPadding(5);
                        avgCellR.setHorizontalAlignment(Element.ALIGN_CENTER);
                        avgCellR.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        avgCellR.setRowspan(2);
                        mainTable.addCell(avgCellR);

                        PdfPCell capacityCellR = new PdfPCell(new Paragraph(getParameter(request, "capacity_R_" + block), valueFont));
                        capacityCellR.setBorder(Rectangle.BOX);
                        capacityCellR.setPadding(5);
                        capacityCellR.setHorizontalAlignment(Element.ALIGN_CENTER);
                        capacityCellR.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        capacityCellR.setRowspan(2);
                        mainTable.addCell(capacityCellR);
                    }
                }
            }

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "testBasis"), valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "equipment"), valueFont, Element.ALIGN_LEFT, 9);

            PdfPCell conclusionCell = new PdfPCell(new Paragraph("检测结论：\n" + getParameter(request, "conclusion"), valueFont));
            conclusionCell.setColspan(10);
            conclusionCell.setBorder(Rectangle.BOX);
            conclusionCell.setPadding(8);
            conclusionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell.setFixedHeight(80);
            mainTable.addCell(conclusionCell);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "remarks"), valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("批准：" + getParameter(request, "approve"), valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("审核：" + getParameter(request, "review"), valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检验：" + getParameter(request, "inspect"), valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            PdfPTable statementTable = new PdfPTable(1);
            statementTable.setWidthPercentage(100);
            statementTable.setSpacingBefore(15);

            PdfPCell statementCell = new PdfPCell(new Paragraph("声明：\n" +
                    "1. 对本检测报告的复印件未加盖公司检验检测专用章无效。\n" +
                    "2. 对检测结果如有异议，应在收到报告之日起十五日之内向本公司提出。\n" +
                    "公司名称：河北金涛建设工程质量检测有限公司。\n" +
                    "公司地址：石家庄高新区方亿科技工业园A区第2号楼。 电话：0311—86107634 0311—67300616", smallFont));
            statementCell.setBorder(Rectangle.NO_BORDER);
            statementCell.setPadding(5);
            statementTable.addCell(statementCell);

            document.add(statementTable);

            PdfPTable pageFooterTable = new PdfPTable(3);
            pageFooterTable.setWidthPercentage(100);
            pageFooterTable.setSpacingBefore(10);

            PdfPCell pageCell1 = new PdfPCell(new Paragraph("版次：" + getParameter(request, "version"), valueFont));
            pageCell1.setBorder(Rectangle.NO_BORDER);
            pageCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageFooterTable.addCell(pageCell1);

            PdfPCell pageCell2 = new PdfPCell(new Paragraph(getParameter(request, "year") + "年" + getParameter(request, "month") + "月" + getParameter(request, "day") + "日", valueFont));
            pageCell2.setBorder(Rectangle.NO_BORDER);
            pageCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageFooterTable.addCell(pageCell2);

            PdfPCell pageCell3 = new PdfPCell(new Paragraph("第 " + getParameter(request, "page") + " 页，共 " + getParameter(request, "totalPages") + " 页", valueFont));
            pageCell3.setBorder(Rectangle.NO_BORDER);
            pageCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageFooterTable.addCell(pageCell3);

            document.add(pageFooterTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateLightDynamicPenetrationRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.NORMAL);
            Font labelFont = new Font(chineseFont, 12, Font.NORMAL);
            Font valueFont = new Font(chineseFont, 12, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("轻型动力触探检测记录表", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10);
            headerTable.setSpacingAfter(10);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "entrustingUnit"), labelFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), labelFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(10);
            mainTable.setSpacingAfter(10);
            mainTable.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "projectName"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "commissionDate"), valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "constructionPart"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "testDate"), valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "岩土性状", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "soilProperties"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "testCategory"), valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "设计\n承载力\n(kPa)", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "锤重量\n(kg)", labelFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, "落距\n(cm)", labelFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, getParameter(request, "designCapacity"), valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, getParameter(request, "hammerWeight"), valueFont, Element.ALIGN_CENTER, 3);
            addCell(mainTable, getParameter(request, "dropDistance"), valueFont, Element.ALIGN_CENTER, 4);

            addCell(mainTable, "测点\n位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入\n深度\n(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测\n锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均\n锤击数\nN10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力\n特征值\n(kPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "测点\n位置", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "贯入\n深度\n(cm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "实测\n锤击数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "平均\n锤击数\nN10", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "承载力\n特征值\n(kPa)", labelFont, Element.ALIGN_CENTER, 1);

            for (int block = 0; block < 2; block++) {
                for (int sub = 0; sub < 5; sub++) {
                    int globalRowIndex = block * 5 + sub;

                    // 左栏：测点位置（rowspan=5）
                    if (sub == 0) {
                        PdfPCell posCell = new PdfPCell(new Paragraph(getParameter(request, "pos_L_" + block), valueFont));
                        posCell.setBorder(Rectangle.BOX);
                        posCell.setPadding(5);
                        posCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        posCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        posCell.setRowspan(5);
                        mainTable.addCell(posCell);
                    }

                    // 左栏：贯入深度和实测锤击数（每行都有）
                    addCell(mainTable, getParameter(request, "depth_L_" + globalRowIndex), valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, getParameter(request, "actual_L_" + globalRowIndex), valueFont, Element.ALIGN_CENTER, 1);

                    // 左栏：平均锤击数和承载力特征值（rowspan=5）
                    if (sub == 0) {
                        PdfPCell avgCell = new PdfPCell(new Paragraph(getParameter(request, "avg_L_" + block), valueFont));
                        avgCell.setBorder(Rectangle.BOX);
                        avgCell.setPadding(5);
                        avgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        avgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        avgCell.setRowspan(5);
                        mainTable.addCell(avgCell);

                        PdfPCell capacityCell = new PdfPCell(new Paragraph(getParameter(request, "capacity_L_" + block), valueFont));
                        capacityCell.setBorder(Rectangle.BOX);
                        capacityCell.setPadding(5);
                        capacityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        capacityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        capacityCell.setRowspan(5);
                        mainTable.addCell(capacityCell);
                    }

                    // 右栏：测点位置（rowspan=5）
                    if (sub == 0) {
                        PdfPCell posCellR = new PdfPCell(new Paragraph(getParameter(request, "pos_R_" + block), valueFont));
                        posCellR.setBorder(Rectangle.BOX);
                        posCellR.setPadding(5);
                        posCellR.setHorizontalAlignment(Element.ALIGN_CENTER);
                        posCellR.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        posCellR.setRowspan(5);
                        mainTable.addCell(posCellR);
                    }

                    // 右栏：贯入深度和实测锤击数（每行都有）
                    addCell(mainTable, getParameter(request, "depth_R_" + globalRowIndex), valueFont, Element.ALIGN_CENTER, 1);
                    addCell(mainTable, getParameter(request, "actual_R_" + globalRowIndex), valueFont, Element.ALIGN_CENTER, 1);

                    // 右栏：平均锤击数和承载力特征值（rowspan=5）
                    if (sub == 0) {
                        PdfPCell avgCellR = new PdfPCell(new Paragraph(getParameter(request, "avg_R_" + block), valueFont));
                        avgCellR.setBorder(Rectangle.BOX);
                        avgCellR.setPadding(5);
                        avgCellR.setHorizontalAlignment(Element.ALIGN_CENTER);
                        avgCellR.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        avgCellR.setRowspan(5);
                        mainTable.addCell(avgCellR);

                        PdfPCell capacityCellR = new PdfPCell(new Paragraph(getParameter(request, "capacity_R_" + block), valueFont));
                        capacityCellR.setBorder(Rectangle.BOX);
                        capacityCellR.setPadding(5);
                        capacityCellR.setHorizontalAlignment(Element.ALIGN_CENTER);
                        capacityCellR.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        capacityCellR.setRowspan(5);
                        mainTable.addCell(capacityCellR);
                    }
                }
            }

            addCell(mainTable, "检测依据", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "testBasis"), valueFont, Element.ALIGN_LEFT, 9);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "equipment"), valueFont, Element.ALIGN_LEFT, 9);

            PdfPCell conclusionCell = new PdfPCell(new Paragraph("检测结论：\n" + getParameter(request, "conclusion"), valueFont));
            conclusionCell.setColspan(10);
            conclusionCell.setBorder(Rectangle.BOX);
            conclusionCell.setPadding(8);
            conclusionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionCell.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionCell.setFixedHeight(60);
            mainTable.addCell(conclusionCell);

            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "remarks"), valueFont, Element.ALIGN_LEFT, 9);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);
            footerTable.setWidths(new float[]{1, 1, 1});

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("计算：" + getParameter(request, "calculator"), valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            PdfPCell footerCell3 = new PdfPCell(new Paragraph("检测：" + getParameter(request, "tester"), valueFont));
            footerCell3.setBorder(Rectangle.NO_BORDER);
            footerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell3);

            document.add(footerTable);

            PdfPTable pageFooterTable = new PdfPTable(3);
            pageFooterTable.setWidthPercentage(100);
            pageFooterTable.setSpacingBefore(10);

            PdfPCell pageCell1 = new PdfPCell(new Paragraph("版次：" + getParameter(request, "version"), valueFont));
            pageCell1.setBorder(Rectangle.NO_BORDER);
            pageCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageFooterTable.addCell(pageCell1);

            PdfPCell pageCell2 = new PdfPCell(new Paragraph(getParameter(request, "year") + "年" + getParameter(request, "month") + "月" + getParameter(request, "day") + "日", valueFont));
            pageCell2.setBorder(Rectangle.NO_BORDER);
            pageCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageFooterTable.addCell(pageCell2);

            PdfPCell pageCell3 = new PdfPCell(new Paragraph("第 " + getParameter(request, "page") + " 页，共 " + getParameter(request, "totalPages") + " 页", valueFont));
            pageCell3.setBorder(Rectangle.NO_BORDER);
            pageCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageFooterTable.addCell(pageCell3);

            document.add(pageFooterTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateNuclearDensityRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.NORMAL);
            Font labelFont = new Font(chineseFont, 12, Font.NORMAL);
            Font valueFont = new Font(chineseFont, 12, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("原位密度检测记录表（核子法）", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10);
            headerTable.setSpacingAfter(10);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "entrustingUnit"), labelFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), labelFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(8);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(10);
            mainTable.setSpacingAfter(10);
            mainTable.setWidths(new float[]{1, 4, 1, 1, 1, 1, 1, 1});

            addCell(mainTable, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "projectName"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "commissionDate"), valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "constructionPart"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "testCategory"), valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "仪器设备", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "equipment"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "检测方法", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "testMethod"), valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "样品名称及\n状态", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "sampleNameStatus"), valueFont, Element.ALIGN_CENTER, 4);
            addCell(mainTable, "依据标准", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "standard"), valueFont, Element.ALIGN_CENTER, 2);

            addCell(mainTable, "最大干密度\n(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "maxDryDensity"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "最优含水率 %", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "optimumMoisture"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(mainTable, "最小干密度\n(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, getParameter(request, "minDryDensity"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(mainTable, "样品编号", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "检测部位\n(桩号、高程)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "湿密度\n(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "干密度\n(g/cm³)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "含水率\n%", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "压实度%", labelFont, Element.ALIGN_CENTER, 1);
            addCell(mainTable, "备注", labelFont, Element.ALIGN_CENTER, 1);

            for (int i = 0; i < 15; i++) {
                addCell(mainTable, getParameter(request, "sampleId_" + i), valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, getParameter(request, "location_" + i), valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, getParameter(request, "date_" + i), valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, getParameter(request, "wetDensity_" + i), valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, getParameter(request, "dryDensity_" + i), valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, getParameter(request, "moisture_" + i), valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, getParameter(request, "compaction_" + i), valueFont, Element.ALIGN_CENTER, 1);
                addCell(mainTable, getParameter(request, "remarks_" + i), valueFont, Element.ALIGN_CENTER, 1);
            }

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(20);
            footerTable.setWidths(new float[]{1, 1});

            PdfPCell footerCell1 = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            footerCell1.setBorder(Rectangle.NO_BORDER);
            footerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell1);

            PdfPCell footerCell2 = new PdfPCell(new Paragraph("检测：" + getParameter(request, "tester"), valueFont));
            footerCell2.setBorder(Rectangle.NO_BORDER);
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell2);

            document.add(footerTable);

            PdfPTable pageFooterTable = new PdfPTable(3);
            pageFooterTable.setWidthPercentage(100);
            pageFooterTable.setSpacingBefore(10);

            PdfPCell pageCell1 = new PdfPCell(new Paragraph("版次：" + getParameter(request, "version"), valueFont));
            pageCell1.setBorder(Rectangle.NO_BORDER);
            pageCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageFooterTable.addCell(pageCell1);

            PdfPCell pageCell2 = new PdfPCell(new Paragraph(getParameter(request, "year") + "年" + getParameter(request, "month") + "月" + getParameter(request, "day") + "日", valueFont));
            pageCell2.setBorder(Rectangle.NO_BORDER);
            pageCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageFooterTable.addCell(pageCell2);

            PdfPCell pageCell3 = new PdfPCell(new Paragraph("第 " + getParameter(request, "page") + " 页，共 " + getParameter(request, "totalPages") + " 页", valueFont));
            pageCell3.setBorder(Rectangle.NO_BORDER);
            pageCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageFooterTable.addCell(pageCell3);

            document.add(pageFooterTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(8);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addCellEmpty(PdfPTable table, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(""));
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(8);
        table.addCell(cell);
    }

    public byte[] generateCuttingRingRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.NORMAL);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);

            Paragraph title = new Paragraph("原位密度检测记录表（环刀法）", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(4);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10);
            headerTable.setWidths(new float[]{2, 1, 1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("施工部位：" + getParameter(request, "constructionLocation"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("最大干密度 (g/cm³)：" + getParameter(request, "maxDryDensity"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("最优含水率 (%)：" + getParameter(request, "optimumMoisture"), valueFont));
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Paragraph("检测类别：" + getParameter(request, "testCategory"), valueFont));
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell4);

            document.add(headerTable);

            PdfPTable headerTable2 = new PdfPTable(3);
            headerTable2.setWidthPercentage(100);
            headerTable2.setSpacingBefore(5);
            headerTable2.setWidths(new float[]{2, 1, 1});

            PdfPCell cell5 = new PdfPCell(new Paragraph("依据标准：" + getParameter(request, "standard"), valueFont));
            cell5.setBorder(Rectangle.NO_BORDER);
            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell5);

            PdfPCell cell6 = new PdfPCell(new Paragraph("设计压实度：" + getParameter(request, "designCompaction"), valueFont));
            cell6.setBorder(Rectangle.NO_BORDER);
            cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell6);

            PdfPCell cell7 = new PdfPCell(new Paragraph("检测日期：" + getParameter(request, "testDate"), valueFont));
            cell7.setBorder(Rectangle.NO_BORDER);
            cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell7);

            document.add(headerTable2);

            PdfPTable mainTable = new PdfPTable(17);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(10);
            mainTable.setWidths(new float[]{1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});

            PdfPCell sampleNoCell = new PdfPCell(new Paragraph("样品\n编号", labelFont));
            sampleNoCell.setBorder(Rectangle.BOX);
            sampleNoCell.setPadding(5);
            sampleNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sampleNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            sampleNoCell.setRowspan(2);
            mainTable.addCell(sampleNoCell);

            PdfPCell locationCell = new PdfPCell(new Paragraph("检测部位\n(桩号、\n高程)", labelFont));
            locationCell.setBorder(Rectangle.BOX);
            locationCell.setPadding(5);
            locationCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            locationCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            locationCell.setRowspan(2);
            mainTable.addCell(locationCell);

            PdfPCell statusCell = new PdfPCell(new Paragraph("样品\n状态", labelFont));
            statusCell.setBorder(Rectangle.BOX);
            statusCell.setPadding(5);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            statusCell.setRowspan(2);
            mainTable.addCell(statusCell);

            PdfPCell ringNoCell = new PdfPCell(new Paragraph("环刀\n号", labelFont));
            ringNoCell.setBorder(Rectangle.BOX);
            ringNoCell.setPadding(5);
            ringNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            ringNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            ringNoCell.setRowspan(2);
            mainTable.addCell(ringNoCell);

            PdfPCell ringMassCell = new PdfPCell(new Paragraph("环刀\n质量g", labelFont));
            ringMassCell.setBorder(Rectangle.BOX);
            ringMassCell.setPadding(5);
            ringMassCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            ringMassCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            ringMassCell.setRowspan(2);
            mainTable.addCell(ringMassCell);

            PdfPCell ringWetMassCell = new PdfPCell(new Paragraph("环刀+湿\n土质量g", labelFont));
            ringWetMassCell.setBorder(Rectangle.BOX);
            ringWetMassCell.setPadding(5);
            ringWetMassCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            ringWetMassCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            ringWetMassCell.setRowspan(2);
            mainTable.addCell(ringWetMassCell);

            PdfPCell ringVolumeCell = new PdfPCell(new Paragraph("环刀体\n积cm³", labelFont));
            ringVolumeCell.setBorder(Rectangle.BOX);
            ringVolumeCell.setPadding(5);
            ringVolumeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            ringVolumeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            ringVolumeCell.setRowspan(2);
            mainTable.addCell(ringVolumeCell);

            PdfPCell moistureHeaderCell = new PdfPCell(new Paragraph("含水率测定", labelFont));
            moistureHeaderCell.setBorder(Rectangle.BOX);
            moistureHeaderCell.setPadding(5);
            moistureHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            moistureHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            moistureHeaderCell.setColspan(5);
            mainTable.addCell(moistureHeaderCell);

            PdfPCell avgMoistureCell = new PdfPCell(new Paragraph("平均\n含水\n率%", labelFont));
            avgMoistureCell.setBorder(Rectangle.BOX);
            avgMoistureCell.setPadding(5);
            avgMoistureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            avgMoistureCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            avgMoistureCell.setRowspan(2);
            mainTable.addCell(avgMoistureCell);

            PdfPCell wetDensityCell = new PdfPCell(new Paragraph("湿密度\ng/cm³", labelFont));
            wetDensityCell.setBorder(Rectangle.BOX);
            wetDensityCell.setPadding(5);
            wetDensityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            wetDensityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            wetDensityCell.setRowspan(2);
            mainTable.addCell(wetDensityCell);

            PdfPCell dryDensityCell = new PdfPCell(new Paragraph("干密度\ng/cm³", labelFont));
            dryDensityCell.setBorder(Rectangle.BOX);
            dryDensityCell.setPadding(5);
            dryDensityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            dryDensityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            dryDensityCell.setRowspan(2);
            mainTable.addCell(dryDensityCell);

            PdfPCell avgDryDensityCell = new PdfPCell(new Paragraph("平均干\n密度\ng/cm³", labelFont));
            avgDryDensityCell.setBorder(Rectangle.BOX);
            avgDryDensityCell.setPadding(5);
            avgDryDensityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            avgDryDensityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            avgDryDensityCell.setRowspan(2);
            mainTable.addCell(avgDryDensityCell);

            PdfPCell compactionCell = new PdfPCell(new Paragraph("压实\n度%", labelFont));
            compactionCell.setBorder(Rectangle.BOX);
            compactionCell.setPadding(5);
            compactionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            compactionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            compactionCell.setRowspan(2);
            mainTable.addCell(compactionCell);

            PdfPCell boxNoCell = new PdfPCell(new Paragraph("盒号", labelFont));
            boxNoCell.setBorder(Rectangle.BOX);
            boxNoCell.setPadding(5);
            boxNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxNoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxNoCell);

            PdfPCell boxMassCell = new PdfPCell(new Paragraph("盒质量\ng", labelFont));
            boxMassCell.setBorder(Rectangle.BOX);
            boxMassCell.setPadding(5);
            boxMassCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxMassCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxMassCell);

            PdfPCell boxWetMassCell = new PdfPCell(new Paragraph("盒+湿土\n质量g", labelFont));
            boxWetMassCell.setBorder(Rectangle.BOX);
            boxWetMassCell.setPadding(5);
            boxWetMassCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxWetMassCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxWetMassCell);

            PdfPCell boxDryMassCell = new PdfPCell(new Paragraph("盒+干土\n质量g", labelFont));
            boxDryMassCell.setBorder(Rectangle.BOX);
            boxDryMassCell.setPadding(5);
            boxDryMassCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxDryMassCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxDryMassCell);

            PdfPCell moistureCell = new PdfPCell(new Paragraph("含水率\n%", labelFont));
            moistureCell.setBorder(Rectangle.BOX);
            moistureCell.setPadding(5);
            moistureCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            moistureCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(moistureCell);

            for (int i = 0; i < 5; i++) {
                PdfPCell sampleNoValueCell = new PdfPCell(new Paragraph(getParameter(request, "sampleNo_" + i), valueFont));
                sampleNoValueCell.setBorder(Rectangle.BOX);
                sampleNoValueCell.setPadding(5);
                sampleNoValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                sampleNoValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sampleNoValueCell.setRowspan(2);
                mainTable.addCell(sampleNoValueCell);

                PdfPCell locationValueCell = new PdfPCell(new Paragraph(getParameter(request, "location_" + i), valueFont));
                locationValueCell.setBorder(Rectangle.BOX);
                locationValueCell.setPadding(5);
                locationValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                locationValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                locationValueCell.setRowspan(2);
                mainTable.addCell(locationValueCell);

                PdfPCell statusValueCell = new PdfPCell(new Paragraph(getParameter(request, "status_" + i), valueFont));
                statusValueCell.setBorder(Rectangle.BOX);
                statusValueCell.setPadding(5);
                statusValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                statusValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                statusValueCell.setRowspan(2);
                mainTable.addCell(statusValueCell);

                PdfPCell ringNoValueCell = new PdfPCell(new Paragraph(getParameter(request, "ringNo_" + i), valueFont));
                ringNoValueCell.setBorder(Rectangle.BOX);
                ringNoValueCell.setPadding(5);
                ringNoValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                ringNoValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                ringNoValueCell.setRowspan(2);
                mainTable.addCell(ringNoValueCell);

                PdfPCell ringMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "ringMass_" + i), valueFont));
                ringMassValueCell.setBorder(Rectangle.BOX);
                ringMassValueCell.setPadding(5);
                ringMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                ringMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                ringMassValueCell.setRowspan(2);
                mainTable.addCell(ringMassValueCell);

                PdfPCell ringWetMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "ringWetMass_" + i), valueFont));
                ringWetMassValueCell.setBorder(Rectangle.BOX);
                ringWetMassValueCell.setPadding(5);
                ringWetMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                ringWetMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                ringWetMassValueCell.setRowspan(2);
                mainTable.addCell(ringWetMassValueCell);

                PdfPCell ringVolumeValueCell = new PdfPCell(new Paragraph(getParameter(request, "ringVolume_" + i), valueFont));
                ringVolumeValueCell.setBorder(Rectangle.BOX);
                ringVolumeValueCell.setPadding(5);
                ringVolumeValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                ringVolumeValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                ringVolumeValueCell.setRowspan(2);
                mainTable.addCell(ringVolumeValueCell);

                PdfPCell boxNo1ValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxNo1_" + i), valueFont));
                boxNo1ValueCell.setBorder(Rectangle.BOX);
                boxNo1ValueCell.setPadding(5);
                boxNo1ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxNo1ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(boxNo1ValueCell);

                PdfPCell boxMass1ValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxMass1_" + i), valueFont));
                boxMass1ValueCell.setBorder(Rectangle.BOX);
                boxMass1ValueCell.setPadding(5);
                boxMass1ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxMass1ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(boxMass1ValueCell);

                PdfPCell boxWetMass1ValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxWetMass1_" + i), valueFont));
                boxWetMass1ValueCell.setBorder(Rectangle.BOX);
                boxWetMass1ValueCell.setPadding(5);
                boxWetMass1ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxWetMass1ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(boxWetMass1ValueCell);

                PdfPCell boxDryMass1ValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxDryMass1_" + i), valueFont));
                boxDryMass1ValueCell.setBorder(Rectangle.BOX);
                boxDryMass1ValueCell.setPadding(5);
                boxDryMass1ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxDryMass1ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(boxDryMass1ValueCell);

                PdfPCell moisture1ValueCell = new PdfPCell(new Paragraph(getParameter(request, "moisture1_" + i), valueFont));
                moisture1ValueCell.setBorder(Rectangle.BOX);
                moisture1ValueCell.setPadding(5);
                moisture1ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                moisture1ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(moisture1ValueCell);

                PdfPCell avgMoistureValueCell = new PdfPCell(new Paragraph(getParameter(request, "avgMoisture_" + i), valueFont));
                avgMoistureValueCell.setBorder(Rectangle.BOX);
                avgMoistureValueCell.setPadding(5);
                avgMoistureValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgMoistureValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                avgMoistureValueCell.setRowspan(2);
                mainTable.addCell(avgMoistureValueCell);

                PdfPCell wetDensityValueCell = new PdfPCell(new Paragraph(getParameter(request, "wetDensity_" + i), valueFont));
                wetDensityValueCell.setBorder(Rectangle.BOX);
                wetDensityValueCell.setPadding(5);
                wetDensityValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                wetDensityValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                wetDensityValueCell.setRowspan(2);
                mainTable.addCell(wetDensityValueCell);

                PdfPCell dryDensityValueCell = new PdfPCell(new Paragraph(getParameter(request, "dryDensity_" + i), valueFont));
                dryDensityValueCell.setBorder(Rectangle.BOX);
                dryDensityValueCell.setPadding(5);
                dryDensityValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                dryDensityValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                dryDensityValueCell.setRowspan(2);
                mainTable.addCell(dryDensityValueCell);

                PdfPCell avgDryDensityValueCell = new PdfPCell(new Paragraph(getParameter(request, "avgDryDensity_" + i), valueFont));
                avgDryDensityValueCell.setBorder(Rectangle.BOX);
                avgDryDensityValueCell.setPadding(5);
                avgDryDensityValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgDryDensityValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                avgDryDensityValueCell.setRowspan(2);
                mainTable.addCell(avgDryDensityValueCell);

                PdfPCell compactionValueCell = new PdfPCell(new Paragraph(getParameter(request, "compaction_" + i), valueFont));
                compactionValueCell.setBorder(Rectangle.BOX);
                compactionValueCell.setPadding(5);
                compactionValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                compactionValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                compactionValueCell.setRowspan(2);
                mainTable.addCell(compactionValueCell);

                PdfPCell boxNo2ValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxNo2_" + i), valueFont));
                boxNo2ValueCell.setBorder(Rectangle.BOX);
                boxNo2ValueCell.setPadding(5);
                boxNo2ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxNo2ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(boxNo2ValueCell);

                PdfPCell boxMass2ValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxMass2_" + i), valueFont));
                boxMass2ValueCell.setBorder(Rectangle.BOX);
                boxMass2ValueCell.setPadding(5);
                boxMass2ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxMass2ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(boxMass2ValueCell);

                PdfPCell boxWetMass2ValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxWetMass2_" + i), valueFont));
                boxWetMass2ValueCell.setBorder(Rectangle.BOX);
                boxWetMass2ValueCell.setPadding(5);
                boxWetMass2ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxWetMass2ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(boxWetMass2ValueCell);

                PdfPCell boxDryMass2ValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxDryMass2_" + i), valueFont));
                boxDryMass2ValueCell.setBorder(Rectangle.BOX);
                boxDryMass2ValueCell.setPadding(5);
                boxDryMass2ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxDryMass2ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(boxDryMass2ValueCell);

                PdfPCell moisture2ValueCell = new PdfPCell(new Paragraph(getParameter(request, "moisture2_" + i), valueFont));
                moisture2ValueCell.setBorder(Rectangle.BOX);
                moisture2ValueCell.setPadding(5);
                moisture2ValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                moisture2ValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(moisture2ValueCell);
            }

            PdfPCell remarksLabelCell = new PdfPCell(new Paragraph("备注", labelFont));
            remarksLabelCell.setBorder(Rectangle.BOX);
            remarksLabelCell.setPadding(5);
            remarksLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            remarksLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            remarksLabelCell.setRowspan(1);
            mainTable.addCell(remarksLabelCell);

            PdfPCell remarksValueCell = new PdfPCell(new Paragraph(getParameter(request, "remarks"), valueFont));
            remarksValueCell.setBorder(Rectangle.BOX);
            remarksValueCell.setPadding(5);
            remarksValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            remarksValueCell.setVerticalAlignment(Element.ALIGN_TOP);
            remarksValueCell.setColspan(16);
            mainTable.addCell(remarksValueCell);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(15);
            footerTable.setWidths(new float[]{1, 1});

            PdfPCell reviewerCell = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            reviewerCell.setBorder(Rectangle.NO_BORDER);
            reviewerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(reviewerCell);

            PdfPCell testerCell = new PdfPCell(new Paragraph("试验：" + getParameter(request, "tester"), valueFont));
            testerCell.setBorder(Rectangle.NO_BORDER);
            testerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(testerCell);

            document.add(footerTable);

            PdfPTable pageFooterTable = new PdfPTable(3);
            pageFooterTable.setWidthPercentage(100);
            pageFooterTable.setSpacingBefore(10);

            PdfPCell versionCell = new PdfPCell(new Paragraph("版次：" + getParameter(request, "version"), valueFont));
            versionCell.setBorder(Rectangle.NO_BORDER);
            versionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageFooterTable.addCell(versionCell);

            PdfPCell dateCell = new PdfPCell(new Paragraph(getParameter(request, "year") + "年" + getParameter(request, "month") + "月" + getParameter(request, "day") + "日", valueFont));
            dateCell.setBorder(Rectangle.NO_BORDER);
            dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageFooterTable.addCell(dateCell);

            PdfPCell pageCell = new PdfPCell(new Paragraph("第 " + getParameter(request, "page") + " 页，共 " + getParameter(request, "totalPages") + " 页", valueFont));
            pageCell.setBorder(Rectangle.NO_BORDER);
            pageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageFooterTable.addCell(pageCell);

            document.add(pageFooterTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateDensityTestReportPdf(HttpServletRequest request) {
        // Margins: Left 20, Right 20, Top 30, Bottom 30 to fit between lines
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add Background and Footer
            String footerDate = getParameter(request, "footerDate");
            if (footerDate.isEmpty()) {
                footerDate = getParameter(request, "reportDate");
            }
            addBackgroundAndFooter(document, writer, getParameter(request, "version"), footerDate, getParameter(request, "page"), getParameter(request, "totalPages"));

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            
            // Optimized fonts for table
            Font tableLabelFont = new Font(chineseFont, 9, Font.BOLD);
            Font tableValueFont = new Font(chineseFont, 9, Font.NORMAL);
            float cellPadding = 1.5f;

            Paragraph title = new Paragraph("原位密度检测报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(4);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(2);
            headerTable.setWidths(new float[]{1, 1, 1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "client"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("工程名称：" + getParameter(request, "projectName"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("检测类别：" + getParameter(request, "testCategory"), valueFont));
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Paragraph("报告编号：" + getParameter(request, "reportNo"), valueFont));
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell4);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(11);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(0);
            // Widths roughly matching JSP percentages: 10, 20(3cols), 14(2cols), 14, 14, 14, 14(2cols)
            // Normalized to 11 columns: 
            // Sample: 10
            // Loc: 20/3 = 6.66
            // Date: 14/2 = 7
            // Wet: 14
            // Dry: 14
            // Mois: 14
            // Comp: 14/2 = 7
            mainTable.setWidths(new float[]{10f, 6.66f, 6.66f, 6.66f, 7f, 7f, 14f, 14f, 14f, 7f, 7f});

            // Helper for adding cells with reduced code
            addCellWithFont(mainTable, "工程名称", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "projectName"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 6);
            addCellWithFont(mainTable, "委托日期", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "commissionDate"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 3);

            addCellWithFont(mainTable, "施工部位", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "constructionPart"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 6);
            addCellWithFont(mainTable, "检测日期", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "testDate"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 3);

            addCellWithFont(mainTable, "样品名称\n及状态", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "sampleNameStatus"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 6);
            addCellWithFont(mainTable, "报告日期", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "reportDate"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 3);

            addCellWithFont(mainTable, "仪器设备", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "equipment"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 6);
            addCellWithFont(mainTable, "检测方法", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "testMethod"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 3);

            addCellWithFont(mainTable, "检测依据", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "testBasis"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 6);
            addCellWithFont(mainTable, "检测类别", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "testCategory"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 3);

            addCellWithFont(mainTable, "最大干密度\n(g/cm³)", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "maxDryDensity"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 3);
            addCellWithFont(mainTable, "最优含水率 %", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "optimumMoisture"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 2);
            addCellWithFont(mainTable, "最小干密度\n(g/cm³)", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, getParameter(request, "minDryDensity"), tableValueFont, cellPadding, Element.ALIGN_CENTER, 3);

            PdfPCell cellDesignLabel = new PdfPCell(new Paragraph("设计指标", tableLabelFont));
            cellDesignLabel.setBorder(Rectangle.BOX);
            cellDesignLabel.setPadding(cellPadding);
            cellDesignLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellDesignLabel.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(cellDesignLabel);

            PdfPCell cellDesignValue = new PdfPCell(new Paragraph(getParameter(request, "designIndex"), tableValueFont));
            cellDesignValue.setBorder(Rectangle.BOX);
            cellDesignValue.setPadding(cellPadding);
            cellDesignValue.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellDesignValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellDesignValue.setColspan(10);
            mainTable.addCell(cellDesignValue);

            PdfPCell cellResultLabel = new PdfPCell(new Paragraph("检测结果", tableLabelFont));
            cellResultLabel.setBorder(Rectangle.BOX);
            cellResultLabel.setPadding(cellPadding);
            cellResultLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellResultLabel.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(cellResultLabel);

            PdfPCell cellResultValue = new PdfPCell(new Paragraph(getParameter(request, "testResult"), tableValueFont));
            cellResultValue.setBorder(Rectangle.BOX);
            cellResultValue.setPadding(cellPadding);
            cellResultValue.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellResultValue.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellResultValue.setColspan(10);
            mainTable.addCell(cellResultValue);

            // Header Rows
            addCellWithFont(mainTable, "样品编号", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, "检测部位\n(桩号、高程)", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 3);
            addCellWithFont(mainTable, "检测日期", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 2);
            addCellWithFont(mainTable, "湿密度\n(g/cm³)", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, "干密度\n(g/cm³)", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, "含水率\n%", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, "压实度%", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 2);

            // Data Rows (8 rows)
            for (int i = 0; i < 8; i++) {
                // Row 1
                // Sample ID
                PdfPCell cSample = new PdfPCell(new Paragraph(getParameter(request, "sampleId_" + i), tableValueFont));
                cSample.setBorder(Rectangle.BOX);
                cSample.setPadding(cellPadding);
                cSample.setHorizontalAlignment(Element.ALIGN_CENTER);
                cSample.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cSample.setRowspan(2);
                mainTable.addCell(cSample);

                // Location
                PdfPCell cLocation = new PdfPCell(new Paragraph(getParameter(request, "location_" + i), tableValueFont));
                cLocation.setBorder(Rectangle.BOX);
                cLocation.setPadding(cellPadding);
                cLocation.setHorizontalAlignment(Element.ALIGN_CENTER);
                cLocation.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cLocation.setColspan(3);
                cLocation.setRowspan(2);
                mainTable.addCell(cLocation);

                // Date
                PdfPCell cDate = new PdfPCell(new Paragraph(getParameter(request, "date_" + i), tableValueFont));
                cDate.setBorder(Rectangle.BOX);
                cDate.setPadding(cellPadding);
                cDate.setHorizontalAlignment(Element.ALIGN_CENTER);
                cDate.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cDate.setColspan(2);
                cDate.setRowspan(2);
                mainTable.addCell(cDate);

                // Wet1
                addCellWithFont(mainTable, getParameter(request, "wetDensity_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
                // Dry1
                addCellWithFont(mainTable, getParameter(request, "dryDensity_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
                // Moisture1
                addCellWithFont(mainTable, getParameter(request, "moisture_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);

                // Compaction
                PdfPCell cCompaction = new PdfPCell(new Paragraph(getParameter(request, "compaction_" + i), tableValueFont));
                cCompaction.setBorder(Rectangle.BOX);
                cCompaction.setPadding(cellPadding);
                cCompaction.setHorizontalAlignment(Element.ALIGN_CENTER);
                cCompaction.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cCompaction.setColspan(2);
                cCompaction.setRowspan(2);
                mainTable.addCell(cCompaction);

                // Row 2
                // Wet2
                addCellWithFont(mainTable, getParameter(request, "wetDensity2_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
                // Dry2
                addCellWithFont(mainTable, getParameter(request, "dryDensity2_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
                // Moisture2
                addCellWithFont(mainTable, getParameter(request, "moisture2_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
            }

            PdfPCell remarksLabelCell = new PdfPCell(new Paragraph("备 注", tableLabelFont));
            remarksLabelCell.setBorder(Rectangle.BOX);
            remarksLabelCell.setPadding(cellPadding);
            remarksLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            remarksLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            mainTable.addCell(remarksLabelCell);

            PdfPCell remarksValueCell = new PdfPCell(new Paragraph(getParameter(request, "remarks"), tableValueFont));
            remarksValueCell.setBorder(Rectangle.BOX);
            remarksValueCell.setPadding(cellPadding);
            remarksValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            remarksValueCell.setVerticalAlignment(Element.ALIGN_TOP);
            remarksValueCell.setColspan(10);
            mainTable.addCell(remarksValueCell);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(5);
            footerTable.setWidths(new float[]{1, 1, 1});

            PdfPCell approverCell = new PdfPCell(new Paragraph("批准：" + getParameter(request, "approver"), valueFont));
            approverCell.setBorder(Rectangle.NO_BORDER);
            approverCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(approverCell);

            PdfPCell reviewerCell = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            reviewerCell.setBorder(Rectangle.NO_BORDER);
            reviewerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(reviewerCell);

            PdfPCell testerCell = new PdfPCell(new Paragraph("检测：" + getParameter(request, "tester"), valueFont));
            testerCell.setBorder(Rectangle.NO_BORDER);
            testerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(testerCell);

            document.add(footerTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private void addCellWithFont(PdfPTable table, String text, Font font, float padding, int alignment, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(padding);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setColspan(colspan);
        table.addCell(cell);
    }

    public byte[] generateDensityTestResultPdf(HttpServletRequest request) {
        // Margins: Left 20, Right 20, Top 30, Bottom 30
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add Background and Footer
            addBackgroundAndFooter(document, writer, getParameter(request, "version"), 
                getParameter(request, "year") + "年" + getParameter(request, "month") + "月" + getParameter(request, "day") + "日",
                getParameter(request, "page"), getParameter(request, "totalPages"));

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            // Use smaller font and padding for table to fit in one page
            Font tableLabelFont = new Font(chineseFont, 9, Font.BOLD);
            Font tableValueFont = new Font(chineseFont, 9, Font.NORMAL);
            float cellPadding = 1.5f;

            Paragraph title = new Paragraph("原位密度检测结果", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(2);

            PdfPCell headerCell = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(headerCell);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(10);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(0);
            // Widths matching JSP percentages: 10, 25(2cols), 15(2cols), 12.5, 12.5, 12.5, 12.5(2cols)
            mainTable.setWidths(new float[]{10f, 12.5f, 12.5f, 7.5f, 7.5f, 12.5f, 12.5f, 12.5f, 6.25f, 6.25f});

            PdfPCell cell1 = new PdfPCell(new Paragraph("施工部位", tableLabelFont));
            cell1.setBorder(Rectangle.BOX);
            cell1.setPadding(cellPadding);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph(getParameter(request, "constructionPart"), tableValueFont));
            cell2.setBorder(Rectangle.BOX);
            cell2.setPadding(cellPadding);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setColspan(9);
            mainTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("最大干密度(g/cm³)", tableLabelFont));
            cell3.setBorder(Rectangle.BOX);
            cell3.setPadding(cellPadding);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell3.setColspan(2); // Was 2 in old table (col 2,3?), now spans col 1,2 of whatever? No, previous table had 11 cols.
            // Let's stick to simple colspan logic relative to 10 cols.
            // Old table: Label(1) Value(10). Now 1 + 9 = 10. OK.
            // Next row: MaxDry(2) Value(1) OptMois(2) Value(2) MinDry(2) Value(2). Total 11.
            // New table: 10 cols.
            // JSP: MaxDry(colspan 2), Value(colspan 2), OptMois(1), Value(2), MinDry(1), Value(3). 
            // Wait, JSP structure for top rows:
            // <td class="label">最大干密度...</td> <td colspan="2">...</td>
            // <td class="label">最优含水率 %</td> <td colspan="2">...</td>
            // <td class="label">最小干密度...</td> <td colspan="3">...</td>
            // Total: 1 + 2 + 1 + 2 + 1 + 3 = 10 columns.
            // My 10-col grid works perfectly for this row too.
            mainTable.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Paragraph(getParameter(request, "maxDryDensity"), tableValueFont));
            cell4.setBorder(Rectangle.BOX);
            cell4.setPadding(cellPadding);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell4.setColspan(2);
            mainTable.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Paragraph("最优含水率 %", tableLabelFont));
            cell5.setBorder(Rectangle.BOX);
            cell5.setPadding(cellPadding);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            // cell5.setColspan(1); // Default
            mainTable.addCell(cell5);

            PdfPCell cell6 = new PdfPCell(new Paragraph(getParameter(request, "optimumMoisture"), tableValueFont));
            cell6.setBorder(Rectangle.BOX);
            cell6.setPadding(cellPadding);
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell6.setColspan(2);
            mainTable.addCell(cell6);

            PdfPCell cell7 = new PdfPCell(new Paragraph("最小干密度(g/cm³)", tableLabelFont));
            cell7.setBorder(Rectangle.BOX);
            cell7.setPadding(cellPadding);
            cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            // cell7.setColspan(1); // Default
            mainTable.addCell(cell7);

            PdfPCell cell8 = new PdfPCell(new Paragraph(getParameter(request, "minDryDensity"), tableValueFont));
            cell8.setBorder(Rectangle.BOX);
            cell8.setPadding(cellPadding);
            cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell8.setColspan(3);
            mainTable.addCell(cell8);

            // Headers
            addCellWithFont(mainTable, "样品编号", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, "检测部位\n(桩号、高程)", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 2);
            addCellWithFont(mainTable, "检测日期", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 2);
            addCellWithFont(mainTable, "湿密度\n(g/cm³)", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, "干密度\n(g/cm³)", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, "含水率\n%", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 1);
            addCellWithFont(mainTable, "压实度%", tableLabelFont, cellPadding, Element.ALIGN_CENTER, 2);

            for (int i = 0; i < 20; i++) {
                // Row 1
                PdfPCell cSample = new PdfPCell(new Paragraph(getParameter(request, "sampleId_" + i), tableValueFont));
                cSample.setBorder(Rectangle.BOX);
                cSample.setPadding(cellPadding);
                cSample.setHorizontalAlignment(Element.ALIGN_CENTER);
                cSample.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cSample.setRowspan(2);
                mainTable.addCell(cSample);

                PdfPCell cLocation = new PdfPCell(new Paragraph(getParameter(request, "location_" + i), tableValueFont));
                cLocation.setBorder(Rectangle.BOX);
                cLocation.setPadding(cellPadding);
                cLocation.setHorizontalAlignment(Element.ALIGN_CENTER);
                cLocation.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cLocation.setColspan(2);
                cLocation.setRowspan(2);
                mainTable.addCell(cLocation);

                PdfPCell cDate = new PdfPCell(new Paragraph(getParameter(request, "date_" + i), tableValueFont));
                cDate.setBorder(Rectangle.BOX);
                cDate.setPadding(cellPadding);
                cDate.setHorizontalAlignment(Element.ALIGN_CENTER);
                cDate.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cDate.setColspan(2);
                cDate.setRowspan(2);
                mainTable.addCell(cDate);

                addCellWithFont(mainTable, getParameter(request, "wetDensity_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
                addCellWithFont(mainTable, getParameter(request, "dryDensity_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
                addCellWithFont(mainTable, getParameter(request, "moisture_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);

                PdfPCell cCompaction = new PdfPCell(new Paragraph(getParameter(request, "compaction_" + i), tableValueFont));
                cCompaction.setBorder(Rectangle.BOX);
                cCompaction.setPadding(cellPadding);
                cCompaction.setHorizontalAlignment(Element.ALIGN_CENTER);
                cCompaction.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cCompaction.setColspan(2);
                cCompaction.setRowspan(2);
                mainTable.addCell(cCompaction);

                // Row 2
                addCellWithFont(mainTable, getParameter(request, "wetDensity2_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
                addCellWithFont(mainTable, getParameter(request, "dryDensity2_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
                addCellWithFont(mainTable, getParameter(request, "moisture2_" + i), tableValueFont, cellPadding, Element.ALIGN_CENTER, 1);
            }

            document.add(mainTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateReboundMethodRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);
            Font smallFont = new Font(chineseFont, 8, Font.NORMAL);

            Paragraph title = new Paragraph("回弹法检测混凝土抗压强度记录表", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // Header Info Table
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell entrustingUnitCell = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "entrustingUnit"), valueFont));
            entrustingUnitCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(entrustingUnitCell);

            PdfPCell unifiedNumberCell = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            unifiedNumberCell.setBorder(Rectangle.NO_BORDER);
            unifiedNumberCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(unifiedNumberCell);

            PdfPCell sampleNoCell = new PdfPCell(new Paragraph("样品编号：" + getParameter(request, "sampleNo"), valueFont));
            sampleNoCell.setBorder(Rectangle.NO_BORDER);
            sampleNoCell.setColspan(2);
            headerTable.addCell(sampleNoCell);

            document.add(headerTable);

            PdfPTable mainTable = new PdfPTable(21);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(5);
            
            float[] widths = new float[21];
            widths[0] = 1.5f;
            for (int i = 1; i <= 16; i++) {
                widths[i] = 1f;
            }
            widths[17] = 1.5f;
            widths[18] = 1.5f;
            widths[19] = 1.5f;
            widths[20] = 1.5f;
            mainTable.setWidths(widths);

            PdfPCell cell1 = new PdfPCell(new Paragraph("工程名称", labelFont));
            cell1.setBorder(Rectangle.BOX);
            cell1.setPadding(5);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell1.setColspan(2);
            mainTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph(getParameter(request, "projectName"), valueFont));
            cell2.setBorder(Rectangle.BOX);
            cell2.setPadding(5);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setColspan(13);
            mainTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("委托日期", labelFont));
            cell3.setBorder(Rectangle.BOX);
            cell3.setPadding(5);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell3.setColspan(3);
            mainTable.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Paragraph(getParameter(request, "commissionDate"), valueFont));
            cell4.setBorder(Rectangle.BOX);
            cell4.setPadding(5);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell4.setColspan(3);
            mainTable.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Paragraph("结构部位", labelFont));
            cell5.setBorder(Rectangle.BOX);
            cell5.setPadding(5);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell5.setColspan(2);
            mainTable.addCell(cell5);

            PdfPCell cell6 = new PdfPCell(new Paragraph(getParameter(request, "structurePart"), valueFont));
            cell6.setBorder(Rectangle.BOX);
            cell6.setPadding(5);
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell6.setColspan(5);
            mainTable.addCell(cell6);

            PdfPCell cell7 = new PdfPCell(new Paragraph("检测类别", labelFont));
            cell7.setBorder(Rectangle.BOX);
            cell7.setPadding(5);
            cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell7.setColspan(3);
            mainTable.addCell(cell7);

            PdfPCell cell8 = new PdfPCell(new Paragraph(getParameter(request, "testCategory"), valueFont));
            cell8.setBorder(Rectangle.BOX);
            cell8.setPadding(5);
            cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell8.setColspan(4);
            mainTable.addCell(cell8);

            PdfPCell cell9 = new PdfPCell(new Paragraph("浇筑日期", labelFont));
            cell9.setBorder(Rectangle.BOX);
            cell9.setPadding(5);
            cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell9.setColspan(3);
            mainTable.addCell(cell9);

            PdfPCell cell10 = new PdfPCell(new Paragraph(getParameter(request, "pourDate"), valueFont));
            cell10.setBorder(Rectangle.BOX);
            cell10.setPadding(5);
            cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell10.setColspan(4);
            mainTable.addCell(cell10);

            PdfPCell cell11 = new PdfPCell(new Paragraph("样品状态", labelFont));
            cell11.setBorder(Rectangle.BOX);
            cell11.setPadding(5);
            cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell11.setColspan(2);
            mainTable.addCell(cell11);

            PdfPCell cell12 = new PdfPCell(new Paragraph(getParameter(request, "sampleStatus"), valueFont));
            cell12.setBorder(Rectangle.BOX);
            cell12.setPadding(5);
            cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell12.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell12.setColspan(5);
            mainTable.addCell(cell12);

            PdfPCell cell13 = new PdfPCell(new Paragraph("测试角度", labelFont));
            cell13.setBorder(Rectangle.BOX);
            cell13.setPadding(5);
            cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell13.setColspan(3);
            mainTable.addCell(cell13);

            PdfPCell cell14 = new PdfPCell(new Paragraph(getParameter(request, "testAngle"), valueFont));
            cell14.setBorder(Rectangle.BOX);
            cell14.setPadding(5);
            cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell14.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell14.setColspan(4);
            mainTable.addCell(cell14);

            PdfPCell cell15 = new PdfPCell(new Paragraph("检测日期", labelFont));
            cell15.setBorder(Rectangle.BOX);
            cell15.setPadding(5);
            cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell15.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell15.setColspan(3);
            mainTable.addCell(cell15);

            PdfPCell cell16 = new PdfPCell(new Paragraph(getParameter(request, "testDate"), valueFont));
            cell16.setBorder(Rectangle.BOX);
            cell16.setPadding(5);
            cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell16.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell16.setColspan(4);
            mainTable.addCell(cell16);

            PdfPCell cell17 = new PdfPCell(new Paragraph("设计指标", labelFont));
            cell17.setBorder(Rectangle.BOX);
            cell17.setPadding(5);
            cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell17.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell17.setColspan(2);
            mainTable.addCell(cell17);

            PdfPCell cell18 = new PdfPCell(new Paragraph(getParameter(request, "designIndex"), valueFont));
            cell18.setBorder(Rectangle.BOX);
            cell18.setPadding(5);
            cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell18.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell18.setColspan(5);
            mainTable.addCell(cell18);

            PdfPCell cell19 = new PdfPCell(new Paragraph("构件厚度或骨料最大粒径", labelFont));
            cell19.setBorder(Rectangle.BOX);
            cell19.setPadding(5);
            cell19.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell19.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell19.setColspan(7);
            mainTable.addCell(cell19);

            PdfPCell cell20 = new PdfPCell(new Paragraph(getParameter(request, "aggregateSize"), valueFont));
            cell20.setBorder(Rectangle.BOX);
            cell20.setPadding(5);
            cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell20.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell20.setColspan(7);
            mainTable.addCell(cell20);

            PdfPCell areaCell = new PdfPCell(new Paragraph("测\n区", labelFont));
            areaCell.setBorder(Rectangle.BOX);
            areaCell.setPadding(5);
            areaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            areaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            areaCell.setRowspan(2);
            mainTable.addCell(areaCell);

            PdfPCell reboundHeaderCell = new PdfPCell(new Paragraph("回弹值", labelFont));
            reboundHeaderCell.setBorder(Rectangle.BOX);
            reboundHeaderCell.setPadding(5);
            reboundHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            reboundHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            reboundHeaderCell.setColspan(16);
            mainTable.addCell(reboundHeaderCell);

            PdfPCell avgReboundCell = new PdfPCell(new Paragraph("平均\n回弹\n值", labelFont));
            avgReboundCell.setBorder(Rectangle.BOX);
            avgReboundCell.setPadding(5);
            avgReboundCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            avgReboundCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            avgReboundCell.setRowspan(2);
            mainTable.addCell(avgReboundCell);

            PdfPCell carbonCell = new PdfPCell(new Paragraph("碳化\n深度\nmm", labelFont));
            carbonCell.setBorder(Rectangle.BOX);
            carbonCell.setPadding(5);
            carbonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            carbonCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            carbonCell.setRowspan(2);
            mainTable.addCell(carbonCell);

            PdfPCell estimatedCell = new PdfPCell(new Paragraph("推定\n强度\n值MPa", labelFont));
            estimatedCell.setBorder(Rectangle.BOX);
            estimatedCell.setPadding(5);
            estimatedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            estimatedCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            estimatedCell.setRowspan(2);
            mainTable.addCell(estimatedCell);

            PdfPCell correctedCell = new PdfPCell(new Paragraph("碳化修\n正强度\n值MPa", labelFont));
            correctedCell.setBorder(Rectangle.BOX);
            correctedCell.setPadding(5);
            correctedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            correctedCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            correctedCell.setRowspan(2);
            mainTable.addCell(correctedCell);

            for (int i = 1; i <= 16; i++) {
                PdfPCell numCell = new PdfPCell(new Paragraph(String.valueOf(i), labelFont));
                numCell.setBorder(Rectangle.BOX);
                numCell.setPadding(5);
                numCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                numCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(numCell);
            }

            for (int i = 1; i <= 10; i++) {
                PdfPCell areaNumCell = new PdfPCell(new Paragraph(String.valueOf(i), valueFont));
                areaNumCell.setBorder(Rectangle.BOX);
                areaNumCell.setPadding(5);
                areaNumCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                areaNumCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(areaNumCell);

                for (int j = 1; j <= 16; j++) {
                    PdfPCell reboundValueCell = new PdfPCell(new Paragraph(getParameter(request, "reboundValue_" + i + "_" + j), valueFont));
                    reboundValueCell.setBorder(Rectangle.BOX);
                    reboundValueCell.setPadding(3);
                    reboundValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    reboundValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    mainTable.addCell(reboundValueCell);
                }

                PdfPCell avgReboundValueCell = new PdfPCell(new Paragraph(getParameter(request, "avgRebound_" + i), valueFont));
                avgReboundValueCell.setBorder(Rectangle.BOX);
                avgReboundValueCell.setPadding(5);
                avgReboundValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgReboundValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(avgReboundValueCell);

                PdfPCell carbonValueCell = new PdfPCell(new Paragraph(getParameter(request, "carbonDepth_" + i), valueFont));
                carbonValueCell.setBorder(Rectangle.BOX);
                carbonValueCell.setPadding(5);
                carbonValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                carbonValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(carbonValueCell);

                PdfPCell estimatedValueCell = new PdfPCell(new Paragraph(getParameter(request, "estimatedStrength_" + i), valueFont));
                estimatedValueCell.setBorder(Rectangle.BOX);
                estimatedValueCell.setPadding(5);
                estimatedValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                estimatedValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(estimatedValueCell);

                PdfPCell correctedValueCell = new PdfPCell(new Paragraph(getParameter(request, "correctedStrength_" + i), valueFont));
                correctedValueCell.setBorder(Rectangle.BOX);
                correctedValueCell.setPadding(5);
                correctedValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                correctedValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                mainTable.addCell(correctedValueCell);
            }

            PdfPCell avgStrengthLabelCell = new PdfPCell(new Paragraph("平均强度值\nMPa", labelFont));
            avgStrengthLabelCell.setBorder(Rectangle.BOX);
            avgStrengthLabelCell.setPadding(5);
            avgStrengthLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            avgStrengthLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            avgStrengthLabelCell.setColspan(3);
            mainTable.addCell(avgStrengthLabelCell);

            PdfPCell avgStrengthValueCell = new PdfPCell(new Paragraph(getParameter(request, "avgStrength"), valueFont));
            avgStrengthValueCell.setBorder(Rectangle.BOX);
            avgStrengthValueCell.setPadding(5);
            avgStrengthValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            avgStrengthValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            avgStrengthValueCell.setColspan(2);
            mainTable.addCell(avgStrengthValueCell);

            PdfPCell stdDevLabelCell = new PdfPCell(new Paragraph("标准差\nMPa", labelFont));
            stdDevLabelCell.setBorder(Rectangle.BOX);
            stdDevLabelCell.setPadding(5);
            stdDevLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            stdDevLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            stdDevLabelCell.setColspan(3);
            mainTable.addCell(stdDevLabelCell);

            PdfPCell stdDevValueCell = new PdfPCell(new Paragraph(getParameter(request, "stdDev"), valueFont));
            stdDevValueCell.setBorder(Rectangle.BOX);
            stdDevValueCell.setPadding(5);
            stdDevValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            stdDevValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            stdDevValueCell.setColspan(2);
            mainTable.addCell(stdDevValueCell);

            PdfPCell coefLabelCell = new PdfPCell(new Paragraph("变异系\n数%", labelFont));
            coefLabelCell.setBorder(Rectangle.BOX);
            coefLabelCell.setPadding(5);
            coefLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            coefLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            coefLabelCell.setColspan(3);
            mainTable.addCell(coefLabelCell);

            PdfPCell coefValueCell = new PdfPCell(new Paragraph(getParameter(request, "coefVariation"), valueFont));
            coefValueCell.setBorder(Rectangle.BOX);
            coefValueCell.setPadding(5);
            coefValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            coefValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            coefValueCell.setColspan(2);
            mainTable.addCell(coefValueCell);

            PdfPCell compEstLabelCell = new PdfPCell(new Paragraph("构件强度推\n定值 MPa", labelFont));
            compEstLabelCell.setBorder(Rectangle.BOX);
            compEstLabelCell.setPadding(5);
            compEstLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            compEstLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            compEstLabelCell.setColspan(3);
            mainTable.addCell(compEstLabelCell);

            PdfPCell compEstValueCell = new PdfPCell(new Paragraph(getParameter(request, "compEstimatedStrength"), valueFont));
            compEstValueCell.setBorder(Rectangle.BOX);
            compEstValueCell.setPadding(5);
            compEstValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            compEstValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            compEstValueCell.setColspan(3);
            mainTable.addCell(compEstValueCell);

            PdfPCell equipmentLabelCell = new PdfPCell(new Paragraph("仪器设备", labelFont));
            equipmentLabelCell.setBorder(Rectangle.BOX);
            equipmentLabelCell.setPadding(5);
            equipmentLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            equipmentLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            equipmentLabelCell.setColspan(2);
            mainTable.addCell(equipmentLabelCell);

            PdfPCell equipmentValueCell = new PdfPCell(new Paragraph(getParameter(request, "equipment"), valueFont));
            equipmentValueCell.setBorder(Rectangle.BOX);
            equipmentValueCell.setPadding(5);
            equipmentValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            equipmentValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            equipmentValueCell.setColspan(11);
            mainTable.addCell(equipmentValueCell);

            PdfPCell carbonAvgLabelCell = new PdfPCell(new Paragraph("碳化平均值\nmm", labelFont));
            carbonAvgLabelCell.setBorder(Rectangle.BOX);
            carbonAvgLabelCell.setPadding(5);
            carbonAvgLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            carbonAvgLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            carbonAvgLabelCell.setColspan(4);
            mainTable.addCell(carbonAvgLabelCell);

            PdfPCell carbonAvgValueCell = new PdfPCell(new Paragraph(getParameter(request, "avgCarbonation"), valueFont));
            carbonAvgValueCell.setBorder(Rectangle.BOX);
            carbonAvgValueCell.setPadding(5);
            carbonAvgValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            carbonAvgValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            carbonAvgValueCell.setColspan(4);
            mainTable.addCell(carbonAvgValueCell);

            PdfPCell standardLabelCell = new PdfPCell(new Paragraph("依据标准", labelFont));
            standardLabelCell.setBorder(Rectangle.BOX);
            standardLabelCell.setPadding(5);
            standardLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            standardLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            standardLabelCell.setColspan(2);
            mainTable.addCell(standardLabelCell);

            PdfPCell standardValueCell = new PdfPCell(new Paragraph(getParameter(request, "standard"), valueFont));
            standardValueCell.setBorder(Rectangle.BOX);
            standardValueCell.setPadding(5);
            standardValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            standardValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            standardValueCell.setColspan(11);
            mainTable.addCell(standardValueCell);

            PdfPCell calibrationLabelCell = new PdfPCell(new Paragraph("率定值", labelFont));
            calibrationLabelCell.setBorder(Rectangle.BOX);
            calibrationLabelCell.setPadding(5);
            calibrationLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            calibrationLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            calibrationLabelCell.setColspan(4);
            mainTable.addCell(calibrationLabelCell);

            Paragraph calibrationPara = new Paragraph();
            calibrationPara.add(new Chunk("检测前：" + getParameter(request, "calibrationBefore"), valueFont));
            calibrationPara.add(new Chunk("\n检测后：" + getParameter(request, "calibrationAfter"), valueFont));
            PdfPCell calibrationValueCell = new PdfPCell(calibrationPara);
            calibrationValueCell.setBorder(Rectangle.BOX);
            calibrationValueCell.setPadding(5);
            calibrationValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            calibrationValueCell.setVerticalAlignment(Element.ALIGN_TOP);
            calibrationValueCell.setColspan(4);
            mainTable.addCell(calibrationValueCell);

            PdfPCell conclusionLabelCell = new PdfPCell(new Paragraph("结论", labelFont));
            conclusionLabelCell.setBorder(Rectangle.BOX);
            conclusionLabelCell.setPadding(5);
            conclusionLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            conclusionLabelCell.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionLabelCell.setColspan(2);
            mainTable.addCell(conclusionLabelCell);

            PdfPCell conclusionValueCell = new PdfPCell(new Paragraph(getParameter(request, "conclusion"), valueFont));
            conclusionValueCell.setBorder(Rectangle.BOX);
            conclusionValueCell.setPadding(5);
            conclusionValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            conclusionValueCell.setVerticalAlignment(Element.ALIGN_TOP);
            conclusionValueCell.setColspan(19);
            mainTable.addCell(conclusionValueCell);

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(15);
            footerTable.setWidths(new float[]{1, 1, 1});

            PdfPCell approverCell = new PdfPCell(new Paragraph("批准：" + getParameter(request, "approver"), valueFont));
            approverCell.setBorder(Rectangle.NO_BORDER);
            approverCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(approverCell);

            PdfPCell reviewerCell = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            reviewerCell.setBorder(Rectangle.NO_BORDER);
            reviewerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(reviewerCell);

            PdfPCell testerCell = new PdfPCell(new Paragraph("检测：" + getParameter(request, "tester"), valueFont));
            testerCell.setBorder(Rectangle.NO_BORDER);
            testerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(testerCell);

            document.add(footerTable);

            PdfPTable pageFooterTable = new PdfPTable(3);
            pageFooterTable.setWidthPercentage(100);
            pageFooterTable.setSpacingBefore(10);

            PdfPCell versionCell = new PdfPCell(new Paragraph("版次：" + getParameter(request, "version"), valueFont));
            versionCell.setBorder(Rectangle.NO_BORDER);
            versionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageFooterTable.addCell(versionCell);

            PdfPCell dateFooterCell = new PdfPCell(new Paragraph(getParameter(request, "year") + "年" + getParameter(request, "month") + "月" + getParameter(request, "day") + "日", valueFont));
            dateFooterCell.setBorder(Rectangle.NO_BORDER);
            dateFooterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageFooterTable.addCell(dateFooterCell);

            PdfPCell pageCell = new PdfPCell(new Paragraph("第 " + getParameter(request, "page") + " 页，共 " + getParameter(request, "totalPages") + " 页", valueFont));
            pageCell.setBorder(Rectangle.NO_BORDER);
            pageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageFooterTable.addCell(pageCell);

            document.add(pageFooterTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateSandReplacementRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.NORMAL);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("原位密度检测记录表（灌砂法）", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10);
            headerTable.setWidths(new float[]{2, 1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("单元工程名称：" + getParameter(request, "projectName"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("试验日期：" + getParameter(request, "testDate"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell3);

            document.add(headerTable);

            PdfPTable headerTable2 = new PdfPTable(4);
            headerTable2.setWidthPercentage(100);
            headerTable2.setSpacingBefore(5);
            headerTable2.setWidths(new float[]{1, 1, 1, 1});

            PdfPCell cell4 = new PdfPCell(new Paragraph("依据标准：" + getParameter(request, "standard"), valueFont));
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Paragraph("最大干密度 (g/cm³)：" + getParameter(request, "maxDryDensity"), valueFont));
            cell5.setBorder(Rectangle.NO_BORDER);
            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell5);

            PdfPCell cell6 = new PdfPCell(new Paragraph("最优含水率 (%)：" + getParameter(request, "optMoisture"), valueFont));
            cell6.setBorder(Rectangle.NO_BORDER);
            cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell6);

            PdfPCell cell7 = new PdfPCell(new Paragraph("最小干密度 (g/cm³)：" + getParameter(request, "minDryDensity"), valueFont));
            cell7.setBorder(Rectangle.NO_BORDER);
            cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell7);

            document.add(headerTable2);

            PdfPTable headerTable3 = new PdfPTable(4);
            headerTable3.setWidthPercentage(100);
            headerTable3.setSpacingBefore(5);
            headerTable3.setWidths(new float[]{1, 1, 1, 1});

            PdfPCell cell8 = new PdfPCell(new Paragraph("量砂密度：" + getParameter(request, "sandDensity") + " g/cm³", valueFont));
            cell8.setBorder(Rectangle.NO_BORDER);
            cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cell8);

            PdfPCell cell9 = new PdfPCell(new Paragraph("仪器设备：" + getParameter(request, "equipment"), valueFont));
            cell9.setBorder(Rectangle.NO_BORDER);
            cell9.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cell9);

            PdfPCell cell10 = new PdfPCell(new Paragraph("检测类别：" + getParameter(request, "testCategory"), valueFont));
            cell10.setBorder(Rectangle.NO_BORDER);
            cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cell10);

            PdfPCell cell11 = new PdfPCell(new Paragraph("设计压实度：" + getParameter(request, "designCompaction"), valueFont));
            cell11.setBorder(Rectangle.NO_BORDER);
            cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cell11);

            document.add(headerTable3);

            PdfPTable mainTable = new PdfPTable(34);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(10);
            
            float[] widths = new float[34];
            widths[0] = 2.5f;
            widths[1] = 0.5f;
            for (int i = 2; i < 34; i++) {
                widths[i] = 1f;
            }
            mainTable.setWidths(widths);

            PdfPCell locationLabelCell = new PdfPCell(new Paragraph("检测部位\n(桩号、高程)", labelFont));
            locationLabelCell.setBorder(Rectangle.BOX);
            locationLabelCell.setPadding(5);
            locationLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            locationLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            locationLabelCell.setColspan(2);
            mainTable.addCell(locationLabelCell);

            for (int i = 0; i < 4; i++) {
                PdfPCell locationValueCell = new PdfPCell(new Paragraph(getParameter(request, "location_" + i), valueFont));
                locationValueCell.setBorder(Rectangle.BOX);
                locationValueCell.setPadding(5);
                locationValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                locationValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                locationValueCell.setColspan(8);
                mainTable.addCell(locationValueCell);
            }

            PdfPCell sandMassLabelCell1 = new PdfPCell(new Paragraph("量砂容器+原有砂质量", labelFont));
            sandMassLabelCell1.setBorder(Rectangle.BOX);
            sandMassLabelCell1.setPadding(5);
            sandMassLabelCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            sandMassLabelCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(sandMassLabelCell1);

            PdfPCell unitCell1 = new PdfPCell(new Paragraph("g", labelFont));
            unitCell1.setBorder(Rectangle.BOX);
            unitCell1.setPadding(5);
            unitCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell1);

            for (int i = 0; i < 8; i++) {
                PdfPCell sandMassValueCell1 = new PdfPCell(new Paragraph(getParameter(request, "totalSandMass_" + i), valueFont));
                sandMassValueCell1.setBorder(Rectangle.BOX);
                sandMassValueCell1.setPadding(5);
                sandMassValueCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                sandMassValueCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sandMassValueCell1.setColspan(4);
                mainTable.addCell(sandMassValueCell1);
            }

            PdfPCell sandMassLabelCell2 = new PdfPCell(new Paragraph("量砂容器+剩余砂质量", labelFont));
            sandMassLabelCell2.setBorder(Rectangle.BOX);
            sandMassLabelCell2.setPadding(5);
            sandMassLabelCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            sandMassLabelCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(sandMassLabelCell2);

            PdfPCell unitCell2 = new PdfPCell(new Paragraph("g", labelFont));
            unitCell2.setBorder(Rectangle.BOX);
            unitCell2.setPadding(5);
            unitCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell2);

            for (int i = 0; i < 8; i++) {
                PdfPCell sandMassValueCell2 = new PdfPCell(new Paragraph(getParameter(request, "remainingSandMass_" + i), valueFont));
                sandMassValueCell2.setBorder(Rectangle.BOX);
                sandMassValueCell2.setPadding(5);
                sandMassValueCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                sandMassValueCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sandMassValueCell2.setColspan(4);
                mainTable.addCell(sandMassValueCell2);
            }

            PdfPCell sandMassLabelCell3 = new PdfPCell(new Paragraph("锥体内砂质量", labelFont));
            sandMassLabelCell3.setBorder(Rectangle.BOX);
            sandMassLabelCell3.setPadding(5);
            sandMassLabelCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            sandMassLabelCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(sandMassLabelCell3);

            PdfPCell unitCell3 = new PdfPCell(new Paragraph("g", labelFont));
            unitCell3.setBorder(Rectangle.BOX);
            unitCell3.setPadding(5);
            unitCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell3);

            for (int i = 0; i < 8; i++) {
                PdfPCell sandMassValueCell3 = new PdfPCell(new Paragraph(getParameter(request, "coneSandMass_" + i), valueFont));
                sandMassValueCell3.setBorder(Rectangle.BOX);
                sandMassValueCell3.setPadding(5);
                sandMassValueCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                sandMassValueCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sandMassValueCell3.setColspan(4);
                mainTable.addCell(sandMassValueCell3);
            }

            PdfPCell sandMassLabelCell4 = new PdfPCell(new Paragraph("试坑耗砂质量", labelFont));
            sandMassLabelCell4.setBorder(Rectangle.BOX);
            sandMassLabelCell4.setPadding(5);
            sandMassLabelCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            sandMassLabelCell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(sandMassLabelCell4);

            PdfPCell unitCell4 = new PdfPCell(new Paragraph("g", labelFont));
            unitCell4.setBorder(Rectangle.BOX);
            unitCell4.setPadding(5);
            unitCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell4);

            for (int i = 0; i < 8; i++) {
                PdfPCell sandMassValueCell4 = new PdfPCell(new Paragraph(getParameter(request, "pitSandMass_" + i), valueFont));
                sandMassValueCell4.setBorder(Rectangle.BOX);
                sandMassValueCell4.setPadding(5);
                sandMassValueCell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                sandMassValueCell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sandMassValueCell4.setColspan(4);
                mainTable.addCell(sandMassValueCell4);
            }

            PdfPCell pitVolumeLabelCell = new PdfPCell(new Paragraph("试坑体积", labelFont));
            pitVolumeLabelCell.setBorder(Rectangle.BOX);
            pitVolumeLabelCell.setPadding(5);
            pitVolumeLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pitVolumeLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(pitVolumeLabelCell);

            PdfPCell unitCell5 = new PdfPCell(new Paragraph("cm³", labelFont));
            unitCell5.setBorder(Rectangle.BOX);
            unitCell5.setPadding(5);
            unitCell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell5);

            for (int i = 0; i < 8; i++) {
                PdfPCell pitVolumeValueCell = new PdfPCell(new Paragraph(getParameter(request, "pitVolume_" + i), valueFont));
                pitVolumeValueCell.setBorder(Rectangle.BOX);
                pitVolumeValueCell.setPadding(5);
                pitVolumeValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pitVolumeValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pitVolumeValueCell.setColspan(4);
                mainTable.addCell(pitVolumeValueCell);
            }

            PdfPCell sampleMassLabelCell = new PdfPCell(new Paragraph("试样质量", labelFont));
            sampleMassLabelCell.setBorder(Rectangle.BOX);
            sampleMassLabelCell.setPadding(5);
            sampleMassLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sampleMassLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(sampleMassLabelCell);

            PdfPCell unitCell6 = new PdfPCell(new Paragraph("g", labelFont));
            unitCell6.setBorder(Rectangle.BOX);
            unitCell6.setPadding(5);
            unitCell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell6);

            for (int i = 0; i < 8; i++) {
                PdfPCell sampleMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "sampleMass_" + i), valueFont));
                sampleMassValueCell.setBorder(Rectangle.BOX);
                sampleMassValueCell.setPadding(5);
                sampleMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                sampleMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sampleMassValueCell.setColspan(4);
                mainTable.addCell(sampleMassValueCell);
            }

            PdfPCell wetDensityLabelCell = new PdfPCell(new Paragraph("试样湿密度", labelFont));
            wetDensityLabelCell.setBorder(Rectangle.BOX);
            wetDensityLabelCell.setPadding(5);
            wetDensityLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            wetDensityLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(wetDensityLabelCell);

            PdfPCell unitCell7 = new PdfPCell(new Paragraph("g/cm³", labelFont));
            unitCell7.setBorder(Rectangle.BOX);
            unitCell7.setPadding(5);
            unitCell7.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell7);

            for (int i = 0; i < 8; i++) {
                PdfPCell wetDensityValueCell = new PdfPCell(new Paragraph(getParameter(request, "wetDensity_" + i), valueFont));
                wetDensityValueCell.setBorder(Rectangle.BOX);
                wetDensityValueCell.setPadding(5);
                wetDensityValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                wetDensityValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                wetDensityValueCell.setColspan(4);
                mainTable.addCell(wetDensityValueCell);
            }

            PdfPCell moistureLabelCell = new PdfPCell(new Paragraph("试样含水率", labelFont));
            moistureLabelCell.setBorder(Rectangle.BOX);
            moistureLabelCell.setPadding(5);
            moistureLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            moistureLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            moistureLabelCell.setRowspan(6);
            mainTable.addCell(moistureLabelCell);

            PdfPCell boxNoLabelCell = new PdfPCell(new Paragraph("盒号", labelFont));
            boxNoLabelCell.setBorder(Rectangle.BOX);
            boxNoLabelCell.setPadding(5);
            boxNoLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxNoLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxNoLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell boxNoValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxNo_" + i), valueFont));
                boxNoValueCell.setBorder(Rectangle.BOX);
                boxNoValueCell.setPadding(5);
                boxNoValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxNoValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                boxNoValueCell.setColspan(2);
                mainTable.addCell(boxNoValueCell);
            }

            PdfPCell boxMassLabelCell = new PdfPCell(new Paragraph("盒质量 g", labelFont));
            boxMassLabelCell.setBorder(Rectangle.BOX);
            boxMassLabelCell.setPadding(5);
            boxMassLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxMassLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxMassLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell boxMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxMass_" + i), valueFont));
                boxMassValueCell.setBorder(Rectangle.BOX);
                boxMassValueCell.setPadding(5);
                boxMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                boxMassValueCell.setColspan(2);
                mainTable.addCell(boxMassValueCell);
            }

            PdfPCell boxWetMassLabelCell = new PdfPCell(new Paragraph("盒+湿土质量 g", labelFont));
            boxWetMassLabelCell.setBorder(Rectangle.BOX);
            boxWetMassLabelCell.setPadding(5);
            boxWetMassLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxWetMassLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxWetMassLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell boxWetMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxWetMass_" + i), valueFont));
                boxWetMassValueCell.setBorder(Rectangle.BOX);
                boxWetMassValueCell.setPadding(5);
                boxWetMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxWetMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                boxWetMassValueCell.setColspan(2);
                mainTable.addCell(boxWetMassValueCell);
            }

            PdfPCell boxDryMassLabelCell = new PdfPCell(new Paragraph("盒+干土质量 g", labelFont));
            boxDryMassLabelCell.setBorder(Rectangle.BOX);
            boxDryMassLabelCell.setPadding(5);
            boxDryMassLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxDryMassLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxDryMassLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell boxDryMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxDryMass_" + i), valueFont));
                boxDryMassValueCell.setBorder(Rectangle.BOX);
                boxDryMassValueCell.setPadding(5);
                boxDryMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxDryMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                boxDryMassValueCell.setColspan(2);
                mainTable.addCell(boxDryMassValueCell);
            }

            PdfPCell moistureValueLabelCell = new PdfPCell(new Paragraph("含水率 %", labelFont));
            moistureValueLabelCell.setBorder(Rectangle.BOX);
            moistureValueLabelCell.setPadding(5);
            moistureValueLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            moistureValueLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(moistureValueLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell moistureValueCell = new PdfPCell(new Paragraph(getParameter(request, "moistureContent_" + i), valueFont));
                moistureValueCell.setBorder(Rectangle.BOX);
                moistureValueCell.setPadding(5);
                moistureValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                moistureValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                moistureValueCell.setColspan(2);
                mainTable.addCell(moistureValueCell);
            }

            PdfPCell avgMoistureLabelCell = new PdfPCell(new Paragraph("平均含水率 %", labelFont));
            avgMoistureLabelCell.setBorder(Rectangle.BOX);
            avgMoistureLabelCell.setPadding(5);
            avgMoistureLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            avgMoistureLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(avgMoistureLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell avgMoistureValueCell = new PdfPCell(new Paragraph(getParameter(request, "avgMoisture_" + i), valueFont));
                avgMoistureValueCell.setBorder(Rectangle.BOX);
                avgMoistureValueCell.setPadding(5);
                avgMoistureValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgMoistureValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                avgMoistureValueCell.setColspan(2);
                mainTable.addCell(avgMoistureValueCell);
            }

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(15);
            footerTable.setWidths(new float[]{1, 1, 1});

            PdfPCell approverCell = new PdfPCell(new Paragraph("批准：" + getParameter(request, "approver"), valueFont));
            approverCell.setBorder(Rectangle.NO_BORDER);
            approverCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(approverCell);

            PdfPCell reviewerCell = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            reviewerCell.setBorder(Rectangle.NO_BORDER);
            reviewerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(reviewerCell);

            PdfPCell testerCell = new PdfPCell(new Paragraph("检测：" + getParameter(request, "tester"), valueFont));
            testerCell.setBorder(Rectangle.NO_BORDER);
            testerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(testerCell);

            document.add(footerTable);

            PdfPTable pageFooterTable = new PdfPTable(3);
            pageFooterTable.setWidthPercentage(100);
            pageFooterTable.setSpacingBefore(10);

            PdfPCell versionCell = new PdfPCell(new Paragraph("版次：" + getParameter(request, "version"), valueFont));
            versionCell.setBorder(Rectangle.NO_BORDER);
            versionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageFooterTable.addCell(versionCell);

            PdfPCell dateFooterCell = new PdfPCell(new Paragraph(getParameter(request, "year") + "年" + getParameter(request, "month") + "月" + getParameter(request, "day") + "日", valueFont));
            dateFooterCell.setBorder(Rectangle.NO_BORDER);
            dateFooterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageFooterTable.addCell(dateFooterCell);

            PdfPCell pageCell = new PdfPCell(new Paragraph("第 " + getParameter(request, "page") + " 页，共 " + getParameter(request, "totalPages") + " 页", valueFont));
            pageCell.setBorder(Rectangle.NO_BORDER);
            pageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageFooterTable.addCell(pageCell);

            document.add(pageFooterTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateWaterReplacementRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.NORMAL);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("相对密度试验记录表（灌水法）", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(10);
            headerTable.setWidths(new float[]{2, 1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("单元工程名称：" + getParameter(request, "projectName"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("试验日期：" + getParameter(request, "testDate"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            cell3.setBorder(Rectangle.NO_BORDER);
            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell3);

            document.add(headerTable);

            PdfPTable headerTable2 = new PdfPTable(4);
            headerTable2.setWidthPercentage(100);
            headerTable2.setSpacingBefore(5);
            headerTable2.setWidths(new float[]{1, 1, 1, 1});

            PdfPCell cell4 = new PdfPCell(new Paragraph("依据标准：" + getParameter(request, "standard"), valueFont));
            cell4.setBorder(Rectangle.NO_BORDER);
            cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Paragraph("最大干密度 (g/cm³)：" + getParameter(request, "maxDryDensity"), valueFont));
            cell5.setBorder(Rectangle.NO_BORDER);
            cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell5);

            PdfPCell cell6 = new PdfPCell(new Paragraph("最优含水率 (%)：" + getParameter(request, "optMoisture"), valueFont));
            cell6.setBorder(Rectangle.NO_BORDER);
            cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell6);

            PdfPCell cell7 = new PdfPCell(new Paragraph("最小干密度 (g/cm³)：" + getParameter(request, "minDryDensity"), valueFont));
            cell7.setBorder(Rectangle.NO_BORDER);
            cell7.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable2.addCell(cell7);

            document.add(headerTable2);

            PdfPTable headerTable3 = new PdfPTable(5);
            headerTable3.setWidthPercentage(100);
            headerTable3.setSpacingBefore(5);
            headerTable3.setWidths(new float[]{1, 1, 1, 1, 1});

            PdfPCell cellWaterDensity = new PdfPCell(new Paragraph("水的密度：" + getParameter(request, "waterDensity") + " g/cm³", valueFont));
            cellWaterDensity.setBorder(Rectangle.NO_BORDER);
            cellWaterDensity.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cellWaterDensity);

            PdfPCell cell8 = new PdfPCell(new Paragraph("仪器设备：" + getParameter(request, "equipment"), valueFont));
            cell8.setBorder(Rectangle.NO_BORDER);
            cell8.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cell8);

            PdfPCell cell9 = new PdfPCell(new Paragraph("检测类别：" + getParameter(request, "testCategory"), valueFont));
            cell9.setBorder(Rectangle.NO_BORDER);
            cell9.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cell9);

            PdfPCell cell10 = new PdfPCell(new Paragraph("设计压实度：" + getParameter(request, "designCompaction"), valueFont));
            cell10.setBorder(Rectangle.NO_BORDER);
            cell10.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cell10);

            PdfPCell cell11 = new PdfPCell(new Paragraph("相对密度：" + getParameter(request, "relativeDensity"), valueFont));
            cell11.setBorder(Rectangle.NO_BORDER);
            cell11.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable3.addCell(cell11);

            document.add(headerTable3);

            PdfPTable mainTable = new PdfPTable(34);
            mainTable.setWidthPercentage(100);
            mainTable.setSpacingBefore(10);
            
            float[] widths = new float[34];
            widths[0] = 2.5f;
            widths[1] = 0.5f;
            for (int i = 2; i < 34; i++) {
                widths[i] = 1f;
            }
            mainTable.setWidths(widths);

            PdfPCell locationLabelCell = new PdfPCell(new Paragraph("检测部位\n(桩号、高程)", labelFont));
            locationLabelCell.setBorder(Rectangle.BOX);
            locationLabelCell.setPadding(5);
            locationLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            locationLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            locationLabelCell.setColspan(2);
            mainTable.addCell(locationLabelCell);

            for (int i = 0; i < 4; i++) {
                PdfPCell locationValueCell = new PdfPCell(new Paragraph(getParameter(request, "location_" + i), valueFont));
                locationValueCell.setBorder(Rectangle.BOX);
                locationValueCell.setPadding(5);
                locationValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                locationValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                locationValueCell.setColspan(8);
                mainTable.addCell(locationValueCell);
            }

            PdfPCell waterVolumeLabelCell1 = new PdfPCell(new Paragraph("试坑体积", labelFont));
            waterVolumeLabelCell1.setBorder(Rectangle.BOX);
            waterVolumeLabelCell1.setPadding(5);
            waterVolumeLabelCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            waterVolumeLabelCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(waterVolumeLabelCell1);

            PdfPCell unitCell1 = new PdfPCell(new Paragraph("cm³", labelFont));
            unitCell1.setBorder(Rectangle.BOX);
            unitCell1.setPadding(5);
            unitCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell1);

            for (int i = 0; i < 8; i++) {
                PdfPCell waterVolumeValueCell1 = new PdfPCell(new Paragraph(getParameter(request, "pitVolume_" + i), valueFont));
                waterVolumeValueCell1.setBorder(Rectangle.BOX);
                waterVolumeValueCell1.setPadding(5);
                waterVolumeValueCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                waterVolumeValueCell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                waterVolumeValueCell1.setColspan(4);
                mainTable.addCell(waterVolumeValueCell1);
            }

            PdfPCell sampleMassLabelCell = new PdfPCell(new Paragraph("试样质量", labelFont));
            sampleMassLabelCell.setBorder(Rectangle.BOX);
            sampleMassLabelCell.setPadding(5);
            sampleMassLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sampleMassLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(sampleMassLabelCell);

            PdfPCell unitCell2 = new PdfPCell(new Paragraph("g", labelFont));
            unitCell2.setBorder(Rectangle.BOX);
            unitCell2.setPadding(5);
            unitCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell2);

            for (int i = 0; i < 8; i++) {
                PdfPCell sampleMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "sampleMass_" + i), valueFont));
                sampleMassValueCell.setBorder(Rectangle.BOX);
                sampleMassValueCell.setPadding(5);
                sampleMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                sampleMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                sampleMassValueCell.setColspan(4);
                mainTable.addCell(sampleMassValueCell);
            }

            PdfPCell wetDensityLabelCell = new PdfPCell(new Paragraph("试样湿密度", labelFont));
            wetDensityLabelCell.setBorder(Rectangle.BOX);
            wetDensityLabelCell.setPadding(5);
            wetDensityLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            wetDensityLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(wetDensityLabelCell);

            PdfPCell unitCell3 = new PdfPCell(new Paragraph("g/cm³", labelFont));
            unitCell3.setBorder(Rectangle.BOX);
            unitCell3.setPadding(5);
            unitCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            unitCell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(unitCell3);

            for (int i = 0; i < 8; i++) {
                PdfPCell wetDensityValueCell = new PdfPCell(new Paragraph(getParameter(request, "wetDensity_" + i), valueFont));
                wetDensityValueCell.setBorder(Rectangle.BOX);
                wetDensityValueCell.setPadding(5);
                wetDensityValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                wetDensityValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                wetDensityValueCell.setColspan(4);
                mainTable.addCell(wetDensityValueCell);
            }

            PdfPCell moistureLabelCell = new PdfPCell(new Paragraph("试样含水率", labelFont));
            moistureLabelCell.setBorder(Rectangle.BOX);
            moistureLabelCell.setPadding(5);
            moistureLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            moistureLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            moistureLabelCell.setRowspan(6);
            mainTable.addCell(moistureLabelCell);

            PdfPCell boxNoLabelCell = new PdfPCell(new Paragraph("盒号", labelFont));
            boxNoLabelCell.setBorder(Rectangle.BOX);
            boxNoLabelCell.setPadding(5);
            boxNoLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxNoLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxNoLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell boxNoValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxNo_" + i), valueFont));
                boxNoValueCell.setBorder(Rectangle.BOX);
                boxNoValueCell.setPadding(5);
                boxNoValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxNoValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                boxNoValueCell.setColspan(2);
                mainTable.addCell(boxNoValueCell);
            }

            PdfPCell boxMassLabelCell = new PdfPCell(new Paragraph("盒质量 g", labelFont));
            boxMassLabelCell.setBorder(Rectangle.BOX);
            boxMassLabelCell.setPadding(5);
            boxMassLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxMassLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxMassLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell boxMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxMass_" + i), valueFont));
                boxMassValueCell.setBorder(Rectangle.BOX);
                boxMassValueCell.setPadding(5);
                boxMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                boxMassValueCell.setColspan(2);
                mainTable.addCell(boxMassValueCell);
            }

            PdfPCell boxWetMassLabelCell = new PdfPCell(new Paragraph("盒+湿土质量 g", labelFont));
            boxWetMassLabelCell.setBorder(Rectangle.BOX);
            boxWetMassLabelCell.setPadding(5);
            boxWetMassLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxWetMassLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxWetMassLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell boxWetMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxWetMass_" + i), valueFont));
                boxWetMassValueCell.setBorder(Rectangle.BOX);
                boxWetMassValueCell.setPadding(5);
                boxWetMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxWetMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                boxWetMassValueCell.setColspan(2);
                mainTable.addCell(boxWetMassValueCell);
            }

            PdfPCell boxDryMassLabelCell = new PdfPCell(new Paragraph("盒+干土质量 g", labelFont));
            boxDryMassLabelCell.setBorder(Rectangle.BOX);
            boxDryMassLabelCell.setPadding(5);
            boxDryMassLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            boxDryMassLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(boxDryMassLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell boxDryMassValueCell = new PdfPCell(new Paragraph(getParameter(request, "boxDryMass_" + i), valueFont));
                boxDryMassValueCell.setBorder(Rectangle.BOX);
                boxDryMassValueCell.setPadding(5);
                boxDryMassValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                boxDryMassValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                boxDryMassValueCell.setColspan(2);
                mainTable.addCell(boxDryMassValueCell);
            }

            PdfPCell moistureValueLabelCell = new PdfPCell(new Paragraph("含水率 %", labelFont));
            moistureValueLabelCell.setBorder(Rectangle.BOX);
            moistureValueLabelCell.setPadding(5);
            moistureValueLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            moistureValueLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(moistureValueLabelCell);

            for (int i = 0; i < 16; i++) {
                PdfPCell moistureValueCell = new PdfPCell(new Paragraph(getParameter(request, "moistureContent_" + i), valueFont));
                moistureValueCell.setBorder(Rectangle.BOX);
                moistureValueCell.setPadding(5);
                moistureValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                moistureValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                moistureValueCell.setColspan(2);
                mainTable.addCell(moistureValueCell);
            }

            PdfPCell avgMoistureLabelCell = new PdfPCell(new Paragraph("平均含水率 %", labelFont));
            avgMoistureLabelCell.setBorder(Rectangle.BOX);
            avgMoistureLabelCell.setPadding(5);
            avgMoistureLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            avgMoistureLabelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            mainTable.addCell(avgMoistureLabelCell);

            for (int i = 0; i < 8; i++) {
                PdfPCell avgMoistureValueCell = new PdfPCell(new Paragraph(getParameter(request, "avgMoisture_" + i), valueFont));
                avgMoistureValueCell.setBorder(Rectangle.BOX);
                avgMoistureValueCell.setPadding(5);
                avgMoistureValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                avgMoistureValueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                avgMoistureValueCell.setColspan(4);
                mainTable.addCell(avgMoistureValueCell);
            }

            document.add(mainTable);

            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(15);
            footerTable.setWidths(new float[]{1, 1, 1});

            PdfPCell approverCell = new PdfPCell(new Paragraph("批准：" + getParameter(request, "approver"), valueFont));
            approverCell.setBorder(Rectangle.NO_BORDER);
            approverCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(approverCell);

            PdfPCell reviewerCell = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            reviewerCell.setBorder(Rectangle.NO_BORDER);
            reviewerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(reviewerCell);

            PdfPCell testerCell = new PdfPCell(new Paragraph("检测：" + getParameter(request, "tester"), valueFont));
            testerCell.setBorder(Rectangle.NO_BORDER);
            testerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(testerCell);

            document.add(footerTable);

            PdfPTable pageFooterTable = new PdfPTable(3);
            pageFooterTable.setWidthPercentage(100);
            pageFooterTable.setSpacingBefore(10);

            PdfPCell versionCell = new PdfPCell(new Paragraph("版次：" + getParameter(request, "version"), valueFont));
            versionCell.setBorder(Rectangle.NO_BORDER);
            versionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pageFooterTable.addCell(versionCell);

            PdfPCell dateFooterCell = new PdfPCell(new Paragraph(getParameter(request, "year") + "年" + getParameter(request, "month") + "月" + getParameter(request, "day") + "日", valueFont));
            dateFooterCell.setBorder(Rectangle.NO_BORDER);
            dateFooterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pageFooterTable.addCell(dateFooterCell);

            PdfPCell pageCell = new PdfPCell(new Paragraph("第 " + getParameter(request, "page") + " 页，共 " + getParameter(request, "totalPages") + " 页", valueFont));
            pageCell.setBorder(Rectangle.NO_BORDER);
            pageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageFooterTable.addCell(pageCell);

            document.add(pageFooterTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateBeckmanBeamResultPdf(HttpServletRequest request) {
        // Margins: 36, 36, 50, 50
        Document document = new Document(PageSize.A4, 36, 36, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // Add Background and Footer
            addBackgroundAndFooter(document, writer, getParameter(request, "version"), 
                getParameter(request, "reportDate"),
                getParameter(request, "page"), getParameter(request, "totalPages"));

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("路基路面回弹弯沉(回弹模量) 检测结果", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Header info
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingAfter(5);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "entrustingUnit"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            // Main table
            PdfPTable mainTable = new PdfPTable(6);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{0.8f, 2.2f, 1.5f, 2f, 2f, 1.5f});

            // Table Header
            String[] headers = {"序号", "测点桩号（幅段）", "车道", "左侧回弹弯沉值\n(0.01mm)", "右侧回弹弯沉值\n(0.01mm)", "备注"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(h, labelFont));
                cell.setBorder(Rectangle.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                mainTable.addCell(cell);
            }

            // Table Body (25 rows)
            for (int i = 1; i <= 25; i++) {
                mainTable.addCell(createSimpleCell(getParameter(request, "seq_" + i), valueFont));
                mainTable.addCell(createSimpleCell(getParameter(request, "station_" + i), valueFont));
                mainTable.addCell(createSimpleCell(getParameter(request, "lane_" + i), valueFont));
                mainTable.addCell(createSimpleCell(getParameter(request, "left_val_" + i), valueFont));
                mainTable.addCell(createSimpleCell(getParameter(request, "right_val_" + i), valueFont));
                mainTable.addCell(createSimpleCell(getParameter(request, "remark_" + i), valueFont));
            }

            document.add(mainTable);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private PdfPCell createSimpleCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(content, font));
        cell.setBorder(Rectangle.BOX);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3);
        cell.setMinimumHeight(20f);
        return cell;
    }

    public byte[] generateBeckmanBeamRecordPdf(HttpServletRequest request) {
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("路基路面回弹弯沉试验检测记录表（贝克曼梁法）", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            // Header info
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "entrustingUnit"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            // Table 1
            PdfPTable table1 = new PdfPTable(4);
            table1.setWidthPercentage(100);
            table1.setSpacingBefore(5);
            table1.setWidths(new float[]{1, 2, 1, 2});

            addCell(table1, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "projectName"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "commissionDate"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table1, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "constructionPart"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "testDate"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table1, "仪器设备及编码", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "equipmentCode"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "testCategory"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table1, "样品名称及状态", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "sampleStatus"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "检测方法", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "贝克曼梁法", valueFont, Element.ALIGN_CENTER, 1);

            document.add(table1);

            // Table 2
            PdfPTable table2 = new PdfPTable(6);
            table2.setWidthPercentage(100);
            table2.setSpacingBefore(-1); // Merge borders
            table2.setWidths(new float[]{1, 1, 1, 1, 1, 1});

            addCell(table2, "依据标准", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "standard"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "设计弯沉值(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "designDeflection"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "温度修正系数K", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "tempCorrectionK"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table2, "测试车车型", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "vehicleModel"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "沥青面层平均温度", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "avgAsphaltTemp"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "轮胎传压面积(cm²)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "tireArea"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table2, "前5天平均气温(℃)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "avgTempPrev5Days"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "路面结构类型", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "pavementType"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "路面厚度(mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "pavementThickness"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table2, "后轴重(kN)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "rearAxleWeight"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "轮胎气压左(MPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "tirePressureLeft"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "轮胎气压右(MPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "tirePressureRight"), valueFont, Element.ALIGN_CENTER, 1);

            document.add(table2);

            // Table 3 (Data)
            PdfPTable table3 = new PdfPTable(10);
            table3.setWidthPercentage(100);
            table3.setSpacingBefore(-1);
            table3.setWidths(new float[]{0.5f, 1.1f, 0.7f, 1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.2f});

            // Header Rows
            PdfPCell h1 = new PdfPCell(new Paragraph("序号", labelFont));
            h1.setRowspan(2);
            h1.setHorizontalAlignment(Element.ALIGN_CENTER);
            h1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Paragraph("测点桩号\n(幅段)", labelFont));
            h2.setRowspan(2);
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            h2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Paragraph("车道", labelFont));
            h3.setRowspan(2);
            h3.setHorizontalAlignment(Element.ALIGN_CENTER);
            h3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(h3);

            PdfPCell h4 = new PdfPCell(new Paragraph("路表温度\n(℃)", labelFont));
            h4.setRowspan(2);
            h4.setHorizontalAlignment(Element.ALIGN_CENTER);
            h4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table3.addCell(h4);

            PdfPCell h5 = new PdfPCell(new Paragraph("左侧(0.01mm)", labelFont));
            h5.setColspan(3);
            h5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(h5);

            PdfPCell h6 = new PdfPCell(new Paragraph("右侧(0.01mm)", labelFont));
            h6.setColspan(3);
            h6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.addCell(h6);

            String[] subHeaders = {"初读数", "终读数", "回弹弯沉", "初读数", "终读数", "回弹弯沉"};
            for (String sh : subHeaders) {
                PdfPCell c = new PdfPCell(new Paragraph(sh, labelFont));
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                table3.addCell(c);
            }

            // Data Rows
            for (int i = 1; i <= 15; i++) {
                table3.addCell(createSimpleCell(String.valueOf(i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "station_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "lane_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "surfaceTemp_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "leftInitial_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "leftFinal_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "leftDeflection_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "rightInitial_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "rightFinal_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "rightDeflection_" + i), valueFont));
            }

            document.add(table3);

            // Table 4 (Stats 1)
            PdfPTable table4 = new PdfPTable(10);
            table4.setWidthPercentage(100);
            table4.setSpacingBefore(-1);
            table4.setWidths(new float[]{1, 0.5f, 0.5f, 1, 1, 1, 1, 1.5f, 0.5f, 1});

            String[] t4Headers = {"测试间距(m)", "车道数", "总点数", "测试公里(km)", "全部平均弯沉", "标准差", "弯沉代表值", "温度修正后平均", "特异点", "是否合格"};
            for (String h : t4Headers) {
                 PdfPCell c = new PdfPCell(new Paragraph(h, labelFont));
                 c.setHorizontalAlignment(Element.ALIGN_CENTER);
                 table4.addCell(c);
            }
            
            table4.addCell(createSimpleCell(getParameter(request, "testInterval"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "laneCount"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "totalPoints"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "testKm"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "totalAvgDeflection"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "stdDev"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "repDeflection"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "tempCorrectedAvg"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "outlierCount"), valueFont));
            table4.addCell(createSimpleCell(getParameter(request, "isQualified"), valueFont));

            document.add(table4);

            // Table 5 (Stats 2)
            PdfPTable table5 = new PdfPTable(6);
            table5.setWidthPercentage(100);
            table5.setSpacingBefore(-1);
            table5.setWidths(new float[]{1.5f, 1, 1.5f, 1.5f, 1.5f, 2});

            String[] t5Headers = {"特异值下限", "特异值上限", "去特异点平均", "去特异点标准差", "去特异点代表值", "去特异点修正平均"};
            for (String h : t5Headers) {
                 PdfPCell c = new PdfPCell(new Paragraph(h, labelFont));
                 c.setHorizontalAlignment(Element.ALIGN_CENTER);
                 table5.addCell(c);
            }

            table5.addCell(createSimpleCell(getParameter(request, "outlierLower"), valueFont));
            table5.addCell(createSimpleCell(getParameter(request, "outlierUpper"), valueFont));
            table5.addCell(createSimpleCell(getParameter(request, "cleanAvg"), valueFont));
            table5.addCell(createSimpleCell(getParameter(request, "cleanStdDev"), valueFont));
            table5.addCell(createSimpleCell(getParameter(request, "cleanRepDeflection"), valueFont));
            table5.addCell(createSimpleCell(getParameter(request, "cleanTempCorrectedAvg"), valueFont));

            document.add(table5);

            // Footer (Remarks)
            PdfPTable tableFooter = new PdfPTable(2);
            tableFooter.setWidthPercentage(100);
            tableFooter.setSpacingBefore(-1);
            tableFooter.setWidths(new float[]{1, 9});

            PdfPCell remarkLabel = new PdfPCell(new Paragraph("备注", labelFont));
            remarkLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            remarkLabel.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tableFooter.addCell(remarkLabel);

            PdfPCell remarkValue = new PdfPCell(new Paragraph(getParameter(request, "remarks"), valueFont));
            remarkValue.setPadding(5);
            tableFooter.addCell(remarkValue);

            document.add(tableFooter);

            // Signatures
            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setSpacingBefore(10);
            
            PdfPCell s1 = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            s1.setBorder(Rectangle.NO_BORDER);
            signTable.addCell(s1);

            PdfPCell s2 = new PdfPCell(new Paragraph("检测：" + getParameter(request, "tester"), valueFont));
            s2.setBorder(Rectangle.NO_BORDER);
            s2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            signTable.addCell(s2);

            document.add(signTable);

            // Page Info
            PdfPTable pageTable = new PdfPTable(2);
            pageTable.setWidthPercentage(100);
            pageTable.setSpacingBefore(5);

            PdfPCell p1 = new PdfPCell(new Paragraph("版次：" + getParameter(request, "version"), valueFont));
            p1.setBorder(Rectangle.NO_BORDER);
            pageTable.addCell(p1);

            PdfPCell p2 = new PdfPCell(new Paragraph("第 " + getParameter(request, "page") + " 页，共 " + getParameter(request, "totalPages") + " 页", valueFont));
            p2.setBorder(Rectangle.NO_BORDER);
            p2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageTable.addCell(p2);

            document.add(pageTable);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateBeckmanBeamReportPdf(HttpServletRequest request) {
        // Margins: 36, 36, 50, 50
        Document document = new Document(PageSize.A4, 36, 36, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // Add Background and Footer
            addBackgroundAndFooter(document, writer, getParameter(request, "version"), 
                getParameter(request, "reportDate"),
                getParameter(request, "page"), getParameter(request, "totalPages"));

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("路基路面回弹弯沉（回弹模量）检测报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // Header info
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "entrustingUnit"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            // Table 1
            PdfPTable table1 = new PdfPTable(4);
            table1.setWidthPercentage(100);
            table1.setSpacingBefore(5);
            table1.setWidths(new float[]{1.5f, 3.5f, 1.5f, 3.5f});

            addCell(table1, "工程名称", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "projectName"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "委托日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "commissionDate"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table1, "施工部位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "constructionPart"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "检测日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "testDate"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table1, "仪器设备及编码", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "equipmentCode"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "报告日期", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "reportDate"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table1, "样品名称及状态", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "sampleStatus"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "检测类别", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "testCategory"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table1, "依据标准", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "standard"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "检测方法", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "testMethod"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table1, "见证单位", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "witnessUnit"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table1, "见证人", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table1, getParameter(request, "witness"), valueFont, Element.ALIGN_CENTER, 1);

            document.add(table1);

            // Table 2
            PdfPTable table2 = new PdfPTable(6);
            table2.setWidthPercentage(100);
            table2.setSpacingBefore(-1);
            table2.setWidths(new float[]{1.5f, 2f, 1.5f, 1.5f, 2f, 1.5f});

            addCell(table2, "路面结构类型", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "pavementType"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "路面厚度(mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "pavementThickness"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "温度修正系数", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "tempCorrection"), valueFont, Element.ALIGN_CENTER, 1);

            addCell(table2, "设计弯沉值\n(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "designDeflection"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "沥青面层平均温度(℃)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "avgAsphaltTemp"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table2, "泊松比μ", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table2, getParameter(request, "poissonRatio"), valueFont, Element.ALIGN_CENTER, 1);

            document.add(table2);

            // Table 3
            PdfPTable table3 = new PdfPTable(6);
            table3.setWidthPercentage(100);
            table3.setSpacingBefore(-1);
            table3.setWidths(new float[]{1, 2.5f, 1.5f, 1.5f, 2, 1.5f});

            String[] t3Headers = {"序号", "测点桩号 (幅段)", "车道", "左侧回弹弯沉\n(0.01mm)", "右侧回弹弯沉\n(0.01mm)", "备注"};
            for(String h : t3Headers) {
                PdfPCell c = new PdfPCell(new Paragraph(h, labelFont));
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                table3.addCell(c);
            }

            for(int i=1; i<=5; i++) {
                table3.addCell(createSimpleCell(String.valueOf(i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "station_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "lane_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "leftDeflection_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "rightDeflection_" + i), valueFont));
                table3.addCell(createSimpleCell(getParameter(request, "remarks_" + i), valueFont));
            }

            document.add(table3);

            // Table 4
            PdfPTable table4 = new PdfPTable(8);
            table4.setWidthPercentage(100);
            table4.setSpacingBefore(-1);
            table4.setWidths(new float[]{1.5f, 1, 1.5f, 1, 1.5f, 1, 1.5f, 1});

            addCell(table4, "(温度修正后)\n平均弯沉值\n(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table4, getParameter(request, "avgDeflection"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table4, "标准差\n(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table4, getParameter(request, "stdDev"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table4, "弯沉代表值\n(0.01mm)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table4, getParameter(request, "repDeflection"), valueFont, Element.ALIGN_CENTER, 1);
            addCell(table4, "回弹模量\nE1(MPa)", labelFont, Element.ALIGN_CENTER, 1);
            addCell(table4, getParameter(request, "resilientModulus"), valueFont, Element.ALIGN_CENTER, 1);

            document.add(table4);

            // Table 5 (Conclusion)
            PdfPTable table5 = new PdfPTable(2);
            table5.setWidthPercentage(100);
            table5.setSpacingBefore(-1);
            table5.setWidths(new float[]{1.5f, 8.5f});

            PdfPCell c1 = new PdfPCell(new Paragraph("检测结论", labelFont));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            c1.setMinimumHeight(40);
            table5.addCell(c1);

            PdfPCell v1 = new PdfPCell(new Paragraph(getParameter(request, "testConclusion"), valueFont));
            v1.setPadding(5);
            table5.addCell(v1);

            PdfPCell c2 = new PdfPCell(new Paragraph("报告说明", labelFont));
            c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            c2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            c2.setMinimumHeight(40);
            table5.addCell(c2);

            PdfPCell v2 = new PdfPCell(new Paragraph(getParameter(request, "reportDesc"), valueFont));
            v2.setPadding(5);
            table5.addCell(v2);

            document.add(table5);

            // Footer
            PdfPTable footer = new PdfPTable(3);
            footer.setWidthPercentage(100);
            footer.setSpacingBefore(15);
            
            PdfPCell f1 = new PdfPCell(new Paragraph("批准：" + getParameter(request, "approver"), valueFont));
            f1.setBorder(Rectangle.NO_BORDER);
            footer.addCell(f1);

            PdfPCell f2 = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            f2.setBorder(Rectangle.NO_BORDER);
            footer.addCell(f2);

            PdfPCell f3 = new PdfPCell(new Paragraph("检测：" + getParameter(request, "tester"), valueFont));
            f3.setBorder(Rectangle.NO_BORDER);
            footer.addCell(f3);

            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] generateReboundMethodReportPdf(HttpServletRequest request) {
        // Margins: 36, 36, 50, 50
        Document document = new Document(PageSize.A4, 36, 36, 50, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add Background and Footer
            addBackgroundAndFooter(document, writer, getParameter(request, "version"), 
                getParameter(request, "reportDate"),
                getParameter(request, "page"), getParameter(request, "totalPages"));

            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 18, Font.BOLD);
            Font labelFont = new Font(chineseFont, 10, Font.BOLD);
            Font valueFont = new Font(chineseFont, 10, Font.NORMAL);

            Paragraph title = new Paragraph("回弹法检测混凝土抗压强度报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // Header info
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});

            PdfPCell cell1 = new PdfPCell(new Paragraph("委托单位：" + getParameter(request, "entrustingUnit"), valueFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Paragraph("统一编号：" + getParameter(request, "unifiedNumber"), valueFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(cell2);

            document.add(headerTable);

            PdfPTable headerTable2 = new PdfPTable(1);
            headerTable2.setWidthPercentage(100);
            PdfPCell cell3 = new PdfPCell(new Paragraph("样品编号：" + getParameter(request, "sampleNumber"), valueFont));
            cell3.setBorder(Rectangle.NO_BORDER);
            headerTable2.addCell(cell3);
            document.add(headerTable2);

            // Main Table (12 columns)
            PdfPTable table = new PdfPTable(12);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);
            
            // Row 1
            addCell(table, "工程名称", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "projectName"), valueFont, Element.ALIGN_CENTER, 6);
            addCell(table, "委托日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "commissionDate"), valueFont, Element.ALIGN_CENTER, 2);

            // Row 2
            addCell(table, "结构部位", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "structurePart"), valueFont, Element.ALIGN_CENTER, 6);
            addCell(table, "检测日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "testDate"), valueFont, Element.ALIGN_CENTER, 2);

            // Row 3
            addCell(table, "样品状态", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "sampleStatus"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(table, "测试角度", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "testAngle"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(table, "报告日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "reportDate"), valueFont, Element.ALIGN_CENTER, 2);

            // Row 4
            addCell(table, "见证单位", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "witnessUnit"), valueFont, Element.ALIGN_CENTER, 6);
            addCell(table, "见证人", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "witnessPerson"), valueFont, Element.ALIGN_CENTER, 2);

            // Row 5
            addCell(table, "设计指标", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "designIndex"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(table, "检测类别", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "testCategory"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(table, "浇筑日期", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "pourDate"), valueFont, Element.ALIGN_CENTER, 2);

            // Row 6
            addCell(table, "碳化深度 mm", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "carbonDepth"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(table, "构件厚度或骨料最大粒径", labelFont, Element.ALIGN_CENTER, 4);
            addCell(table, getParameter(request, "aggregateSize"), valueFont, Element.ALIGN_CENTER, 4);

            // Row 7 (Zone No)
            addCell(table, "测区号", labelFont, Element.ALIGN_CENTER, 2);
            for(int i=1; i<=10; i++) {
                addCell(table, String.valueOf(i), labelFont, Element.ALIGN_CENTER, 1);
            }

            // Row 8 (Corrected Strength)
            addCell(table, "碳化修正强度\nMPa", labelFont, Element.ALIGN_CENTER, 2);
            for(int i=1; i<=10; i++) {
                addCell(table, getParameter(request, "correctedStrength_" + i), valueFont, Element.ALIGN_CENTER, 1);
            }

            // Row 9 (Estimation)
            addCell(table, "构件强度推定\n值 MPa", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "compEstimatedStrength"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(table, "标准差 MPa", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "stdDev"), valueFont, Element.ALIGN_CENTER, 2);
            addCell(table, "变异系数%", labelFont, Element.ALIGN_CENTER, 2);
            addCell(table, getParameter(request, "coefVariation"), valueFont, Element.ALIGN_CENTER, 2);

            // Row 10 (Equipment)
            addCell(table, "仪器设备", labelFont, Element.ALIGN_CENTER, 2);
            PdfPCell eqCell = new PdfPCell(new Paragraph(getParameter(request, "equipment"), valueFont));
            eqCell.setColspan(10);
            eqCell.setBorder(Rectangle.BOX);
            eqCell.setPadding(5);
            table.addCell(eqCell);

            // Row 11 (Standard)
            addCell(table, "依据标准", labelFont, Element.ALIGN_CENTER, 2);
            PdfPCell stCell = new PdfPCell(new Paragraph(getParameter(request, "standard"), valueFont));
            stCell.setColspan(10);
            stCell.setBorder(Rectangle.BOX);
            stCell.setPadding(5);
            table.addCell(stCell);

            // Row 12 (Conclusion)
            addCell(table, "结论", labelFont, Element.ALIGN_CENTER, 2);
            PdfPCell concCell = new PdfPCell(new Paragraph(getParameter(request, "conclusion"), valueFont));
            concCell.setColspan(10);
            concCell.setBorder(Rectangle.BOX);
            concCell.setPadding(5);
            concCell.setMinimumHeight(60);
            table.addCell(concCell);

            // Row 13 (Diagram)
            PdfPCell diagCell = new PdfPCell(new Paragraph("测区示意图：", labelFont));
            diagCell.setColspan(12);
            diagCell.setBorder(Rectangle.BOX);
            diagCell.setPadding(5);
            diagCell.setMinimumHeight(150);
            table.addCell(diagCell);

            // Row 14 (Remarks)
            addCell(table, "备注：", labelFont, Element.ALIGN_CENTER, 1);
            PdfPCell remCell = new PdfPCell(new Paragraph(getParameter(request, "remarks"), valueFont));
            remCell.setColspan(11);
            remCell.setBorder(Rectangle.BOX);
            remCell.setPadding(5);
            remCell.setMinimumHeight(40);
            table.addCell(remCell);

            document.add(table);

            // Footer
            PdfPTable footer = new PdfPTable(3);
            footer.setWidthPercentage(100);
            footer.setSpacingBefore(10);
            
            PdfPCell f1 = new PdfPCell(new Paragraph("批准：" + getParameter(request, "approver"), valueFont));
            f1.setBorder(Rectangle.NO_BORDER);
            footer.addCell(f1);

            PdfPCell f2 = new PdfPCell(new Paragraph("审核：" + getParameter(request, "reviewer"), valueFont));
            f2.setBorder(Rectangle.NO_BORDER);
            footer.addCell(f2);

            PdfPCell f3 = new PdfPCell(new Paragraph("检验：" + getParameter(request, "tester"), valueFont));
            f3.setBorder(Rectangle.NO_BORDER);
            footer.addCell(f3);

            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
