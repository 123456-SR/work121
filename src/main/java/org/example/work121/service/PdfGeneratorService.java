package org.example.work121.service;

import com.lowagie.text.*;
        import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

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
}
