package com.example.InvoiceMailer.service;

import com.example.InvoiceMailer.model.Order;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfGeneratorService {

    final static private String NOW = ZonedDateTime.now()
                                                   .toOffsetDateTime()
                                                   .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    private static final String FONT_PATH = "src/main/resources/fonts/arial.ttf";
    private static final String LOGO_PATH = "src/main/resources/images/Logo.png";

    final static private String SELLER_FIRMA_NAME = "Viggo-Programer";
    final static private String SELLER_NAME = "Michał Rydzanicz";
    final static private String SELLER_Address = "Popowicka 68/17, 54-237 Wrocław";
    final static private String SELLER_NIP = "6574654654654";
    final static private DecimalFormat df = new DecimalFormat("#.00");

    public ByteArrayOutputStream generateInvoicePdf(final String invoiceNumber,
                                                    final String buyerName,
                                                    final String buyerAddress,
                                                    final String buyerAddressEmail,
                                                    final String buyerNip,
                                                    final List<Order> orders) throws IOException {
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("Products cannot be null or empty");
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PdfWriter writer = new PdfWriter(out);
        final PdfDocument pdfDoc = new PdfDocument(writer);
        final Document document = new Document(pdfDoc);

        final PdfFont font = PdfFontFactory.createFont(FONT_PATH, "Identity-H", pdfDoc);

        document.setFont(font);
        document.setFontSize(9);
        document.setMargins(50, 50, 50, 50);

        document.showTextAligned(new Paragraph("Faktura VAT nr: " + invoiceNumber), 50, 800, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph(
                                         "_______________________________________________________________________________________________"),
                                 50,
                                 790,
                                 TextAlignment.LEFT);

        document.showTextAligned(new Paragraph("Faktura VAT nr: " + invoiceNumber), 320, 760, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph("Data wystawienia: " + NOW), 320, 740, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph("Data sprzedaży: " + NOW), 320, 720, TextAlignment.LEFT);

        document.showTextAligned(new Paragraph("________________________________________"), 50, 700, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph("________________________________________"), 50, 690, TextAlignment.LEFT);

        if (!new File(LOGO_PATH).exists()) {
            System.err.println("Logo file not found, skipping logo placement.");
        } else {
            Image logo = new Image(ImageDataFactory.create(LOGO_PATH));
            logo.setFixedPosition(36, 700);
            logo.scaleToFit(100, 80);
            document.add(logo);
        }

        document.showTextAligned(new Paragraph("SPRZEDAWCA"), 50, 690, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph(SELLER_FIRMA_NAME), 50, 670, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph(SELLER_Address), 50, 650, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph("NIP " + SELLER_NIP), 50, 630, TextAlignment.LEFT);

        document.showTextAligned(new Paragraph("________________________________________"), 320, 700, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph("________________________________________"), 320, 690, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph("NABYWCA"), 320, 690, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph(buyerName), 320, 670, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph(buyerAddress), 320, 650, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph(buyerAddressEmail), 320, 630, TextAlignment.LEFT);

        if (!buyerNip.isEmpty()) {
            document.showTextAligned(new Paragraph("NIP " + buyerNip), 320, 610, TextAlignment.LEFT);
        }
        document.add(new Paragraph("\n").setMarginTop(200));

        final float[] columnWidths = {1, 3, 3, 1, 2, 1, 1, 2};
        final Table invoiceItemsTable = new Table(columnWidths).useAllAvailableWidth();

        invoiceItemsTable.addCell("Lp.");
        invoiceItemsTable.addCell("Nazwa towaru/usługi");
        invoiceItemsTable.addCell("Opis");
        invoiceItemsTable.addCell("Ilość");
        invoiceItemsTable.addCell("Cena netto");
        invoiceItemsTable.addCell("VAT");
        invoiceItemsTable.addCell("Wartość netto");
        invoiceItemsTable.addCell("Wartość brutto");

        if (orders.isEmpty()) {
            throw new IllegalArgumentException("Lista produktów nie może być pusta.");
        }

        for (int i = 0; i < orders.size(); i++) {
            invoiceItemsTable.addCell(String.valueOf(i + 1));
            invoiceItemsTable.addCell(orders.get(i)
                                            .getName());
            invoiceItemsTable.addCell(orders.get(i)
                                            .getDescription());
            invoiceItemsTable.addCell(String.valueOf(orders.get(i)
                                                           .getQuantity()));
            invoiceItemsTable.addCell(df.format(orders.get(i)
                                                      .getPrice()));
            invoiceItemsTable.addCell("23%");
            invoiceItemsTable.addCell(df.format(orders.get(i)
                                                      .getPrice() * orders.get(i)
                                                                          .getQuantity()));
            invoiceItemsTable.addCell(df.format(orders.get(i)
                                                      .getPriceWithVAT() * orders.get(i)
                                                                                 .getQuantity()));
        }
        document.add(invoiceItemsTable.setMarginBottom(20));

        final Table summaryTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

        final double priceSum = orders.stream()
                                      .mapToDouble(x -> x.getPrice() * x.getQuantity())
                                      .sum();

        final double priceVatSum = orders.stream()
                                         .mapToDouble(x -> x.getPriceWithVAT() * x.getQuantity())
                                         .sum();

        summaryTable.addCell("Suma netto:")
                    .setTextAlignment(TextAlignment.RIGHT);
        summaryTable.addCell(df.format(priceSum))
                    .setTextAlignment(TextAlignment.RIGHT);
        summaryTable.addCell("Suma VAT:")
                    .setTextAlignment(TextAlignment.RIGHT);
        summaryTable.addCell(df.format(priceVatSum - priceSum))
                    .setTextAlignment(TextAlignment.RIGHT);
        summaryTable.addCell("Suma brutto:")
                    .setTextAlignment(TextAlignment.RIGHT);
        summaryTable.addCell(df.format(priceVatSum))
                    .setTextAlignment(TextAlignment.RIGHT);
        document.add(summaryTable);

        document.showTextAligned(new Paragraph("________________________________________"), 50, 80, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph("Osoba upoważniona do odbioru faktury\n"), 50, 50, TextAlignment.LEFT);

        document.showTextAligned(new Paragraph("________________________________________"), 320, 80, TextAlignment.LEFT);
        document.showTextAligned(new Paragraph("Osoba upoważniona do wystawienia faktury\n" + SELLER_NAME), 320, 50, TextAlignment.LEFT);

        document.close();

        return out;
    }
}