package com.example.docplatform.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

public class PdfGenerator {

    public byte[] generateSimplePdf(String content) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();
        document.add(new Paragraph("Служебная записка"));
        document.add(new Paragraph("Содержание:"));
        document.add(new Paragraph(content));
        document.close();

        return out.toByteArray();
    }
}
