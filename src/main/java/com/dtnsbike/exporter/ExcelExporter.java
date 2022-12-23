package com.dtnsbike.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dtnsbike.entity.Categories;

public class ExcelExporter {

	public static ByteArrayInputStream contactListToExcelFile(List<Categories> customers) {
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Category");

			XSSFRow row = null;
			Cell cell = null;

			CellStyle headerCellStyle2 = workbook.createCellStyle();
			headerCellStyle2.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);

			row = sheet.createRow(0);
			sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 1));
			row.setHeight((short) 500);
			cell = row.createCell(0, CellType.STRING);
			cell.setCellValue("DANH SÁCH DANH MỤC SẢN PHẨM");
			cell.setCellStyle(headerCellStyle2);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
			headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
			headerCellStyle.setBorderRight(BorderStyle.MEDIUM);
			headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);

			// Creating header
			row = sheet.createRow((short) 2);
			row.setHeight((short) 500);
			cell = row.createCell(0, CellType.STRING);
			cell = row.createCell(0);
			cell.setCellValue("Mã");
			cell.setCellStyle(headerCellStyle);

			cell = row.createCell(1);
			cell.setCellValue("Tên danh mục");
			cell.setCellStyle(headerCellStyle);

			// Creating data rows for each customer
			for (int i = 0; i < customers.size(); i++) {
				Row dataRow = sheet.createRow((short) 3 + i);
				dataRow.setHeight((short) 400);

				Cell cell3 = dataRow.createCell(0);
				CellStyle style = workbook.createCellStyle();
				style.setWrapText(true);
				style.setBorderTop(BorderStyle.MEDIUM);
				style.setBorderLeft(BorderStyle.MEDIUM);
				style.setBorderRight(BorderStyle.MEDIUM);
				style.setBorderBottom(BorderStyle.MEDIUM);

				cell3.setCellValue(customers.get(i).getId());
				cell3.setCellStyle(style);

				cell3 = dataRow.createCell(1);
				cell3.setCellValue(customers.get(i).getName());
				cell3.setCellStyle(style);

			}

			// Making size of column auto resize to fit with data
			sheet.autoSizeColumn(0);
			sheet.setColumnWidth(1, 25 * 500);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
