package com.example.docplatform.service;


import com.example.docplatform.model.Memo;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class MemoService {
    public byte[] generateMemoPdf(Memo memo) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph("Служебная записка"));
        document.add(new Paragraph("От: " + formatName(memo.getAuthor())));
        document.add(new Paragraph("Кому: " + formatName(memo.getApprover())));
        document.add(new Paragraph("Дата создания: " + memo.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
        document.add(new Paragraph("\n" + memo.getContent()));

        document.newPage();
        document.add(new Paragraph("Подписано:"));
        document.add(new Paragraph("Руководитель: " + formatName(memo.getApprover())));
        document.add(new Paragraph("Дата подписи: " + memo.getApprovedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));

        document.close();
        return out.toByteArray();
    }

    private String formatName(com.example.docplatform.model.User user) {
        return user.getLastName() + " " + user.getFirstName() + " " + user.getMiddleName();
    }
}
