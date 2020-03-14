package com.medas.rewamp.reportservice.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * Purpose : For generating cells for pdf reports.
 * @author indrajith.gireesan
 * @since 01/05/2019
 */
public class ItextPdfCellFactory {
	
	public static PdfPCell getCell(String text, com.itextpdf.text.Font font, int horizontalAlign, BaseColor bordorColor, int colspan) {
		return getCell(text, font, horizontalAlign, bordorColor, colspan, 10);
	}
	public static PdfPCell getCellWithUnderlinedText(String text, com.itextpdf.text.Font font, int horizontalAlign,
			BaseColor bordorColor, int colspan) {
		return getCellWithUnderlinedText(text, font, horizontalAlign, bordorColor, colspan, 10);
	}
	public static PdfPCell getCell(String text, com.itextpdf.text.Font font, int horizontalAlign, BaseColor bordorColor, int colspan, float fixedLeading) {
		PdfPCell cell = getCellForReport(getPhrase(text, font), fixedLeading);
		cell.setBorderColor(bordorColor);
		cell.setHorizontalAlignment(horizontalAlign);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setColspan(colspan);
		return cell;
	}
	public static PdfPCell getCell(String text, com.itextpdf.text.Font font, int horizontalAlign, BaseColor bordorColor, int colspan, float fixedLeading, float paddingBottom) {
		PdfPCell cell = getCellForReport(getPhrase(text, font), fixedLeading);
		cell.setBorderColor(bordorColor);
		cell.setHorizontalAlignment(horizontalAlign);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setColspan(colspan);
		cell.setPaddingBottom(paddingBottom);
		return cell;
	}
	public static PdfPCell getCellWithUnderlinedText(String text, com.itextpdf.text.Font font, int horizontalAlign,
			BaseColor bordorColor, int colspan, float fixedLeading) {
		PdfPCell cell = null;
		com.itextpdf.text.Chunk ck = new com.itextpdf.text.Chunk(text, font);
		ck.setUnderline(1f, -1f);
		com.itextpdf.text.Paragraph ph = new com.itextpdf.text.Paragraph();
		ph.add(ck);
		cell = getCellForReport(ph, fixedLeading);
		cell.setBorderColor(bordorColor);
		cell.setHorizontalAlignment(horizontalAlign);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setColspan(colspan);
		return cell;
	}
	public static PdfPCell getCellWithUnderlinedText(String text, com.itextpdf.text.Font font, int horizontalAlign,
			BaseColor bordorColor, int colspan, float fixedLeading, float paddingBottom) {
		PdfPCell cell = null;
		com.itextpdf.text.Chunk ck = new com.itextpdf.text.Chunk(text, font);
		ck.setUnderline(1f, -1f);
		com.itextpdf.text.Paragraph ph = new com.itextpdf.text.Paragraph();
		ph.add(ck);
		cell = getCellForReport(ph, fixedLeading);
		cell.setBorderColor(bordorColor);
		cell.setHorizontalAlignment(horizontalAlign);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setColspan(colspan);
		cell.setPaddingBottom(paddingBottom);
		return cell;
	}
	/********************************************************************************************************************************/
	
	/**
	 * Itext 5 related changes
	 * 
	 * @author Jegatheesh
	 */
	public PdfPCell getCellForReport(com.itextpdf.text.Paragraph para) {
		PdfPCell cell = new PdfPCell(para);
		cell.setLeading(1, 0);
		return cell;
	}
	public static PdfPCell getCellForReport(Phrase phrase) {
		PdfPCell cell = new PdfPCell(phrase);
		return cell;
	}
	public static PdfPCell getCellForReport(com.itextpdf.text.Paragraph para, float fixedLeading) {
		PdfPCell cell = new PdfPCell(para);
		cell.setLeading(fixedLeading, 0);
		return cell;
	}
	public static PdfPCell getCellForReport(Phrase phrase, float fixedLeading) {
		PdfPCell cell = new PdfPCell(phrase);
		cell.setLeading(fixedLeading, 0);
		return cell;
	}
	public PdfPCell getCellForReport(com.itextpdf.text.Paragraph para, float fixedLeading, float multipliedLeading) {
		PdfPCell cell = new PdfPCell(para);
		cell.setLeading(fixedLeading, multipliedLeading);
		return cell;
	}
	public com.itextpdf.text.Paragraph getParagraph(String content, com.itextpdf.text.Font font) {
		com.itextpdf.text.Paragraph para = new com.itextpdf.text.Paragraph(content);
		para.setFont(font);
		return para;
	}
	public static Phrase getPhrase(String content, com.itextpdf.text.Font font) {
		return new Phrase(content, font);
	}
	private static String breakStr() {
		return "____________________________________________________________________________________";
	}
	/**
	 * return line break with default leading 1
	 * @return cell
	 */
	public static PdfPCell lineBreak() {
		return getCellForReport(new com.itextpdf.text.Paragraph(breakStr()), 1);
	}
	public static PdfPCell lineBreak(float fixedLeading) {
		return getCellForReport(new com.itextpdf.text.Paragraph(breakStr()), fixedLeading);
	}
	public PdfPCell lineBreak(com.itextpdf.text.Font font) {
		return getCellForReport(getPhrase(breakStr(), font));
	}
	public static PdfPCell setBorderWidth(PdfPCell cell, Integer[] border) {
		if (border[0] == 0) {
			cell.setBorderWidthTop(0f);
		}
		if (border[1] == 0) {
			cell.setBorderWidthRight(0f);
		}
		if (border[2] == 0) {
			cell.setBorderWidthBottom(0f);
		}
		if (border[3] == 0) {
			cell.setBorderWidthLeft(0f);
		}
		return cell;
	}
	public static PdfPCell getNoBorderCell() {
		PdfPCell cell = new PdfPCell();
		cell.setLeading(0, 0);
		cell.setBorder(0);
		return cell;
	}
	public static PdfPCell getNoBorderCell(Phrase phrase, float fixedLeading) {
		PdfPCell cell = getCellForReport(phrase, fixedLeading);
		cell.setBorder(0);
		return cell;
	}
	public static PdfPCell getNoBorderCell(Element element) {
		PdfPCell cell = new PdfPCell();
		cell.setLeading(0, 0);
		cell.setBorder(0);
		cell.addElement(element);
		return cell;
	}
	public static PdfPCell getNoBorderCell(Element element, float fixedLeading) {
		PdfPCell cell = new PdfPCell();
		cell.setLeading(fixedLeading, 0);
		cell.setBorder(0);
		cell.addElement(element);
		return cell;
	}
	public static PdfPCell getNoBorderCell(String content, com.itextpdf.text.Font font) {
		PdfPCell cell = new PdfPCell();
		cell.setLeading(0, 0);
		cell.setBorder(0);
		cell.addElement(getPhrase(content, font));
		return cell;
	}
}
