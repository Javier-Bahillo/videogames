package Videoclub.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class PdfService {

    private static final Color COLOR_RED      = new Color(229, 9, 20);
    private static final Color COLOR_BG       = new Color(15, 15, 15);
    private static final Color COLOR_SURFACE  = new Color(26, 26, 26);
    private static final Color COLOR_SURFACE2 = new Color(36, 36, 36);
    private static final Color COLOR_BORDER   = new Color(46, 46, 46);
    private static final Color COLOR_TEXT     = new Color(240, 240, 240);
    private static final Color COLOR_MUTED    = new Color(136, 136, 136);
    private static final Color COLOR_GREEN    = new Color(74, 222, 128);

    public byte[] generateWelcomePdf(String name, String surname, String username,
                                     String email, String role, LocalDate date) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 0, 0, 0, 0);

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // HEADER
            PdfPTable header = wideTable(1);
            PdfPCell hCell = colorCell(COLOR_RED, 40);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hCell.addElement(centeredPara("VIDEOCLUB", hv(30, Font.BOLD, Color.WHITE)));
            hCell.addElement(spacedPara("Tu destino gamer definitivo", hv(11, Font.ITALIC, new Color(255, 200, 200)), 4));
            header.addCell(hCell);
            doc.add(header);

            // BIENVENIDA
            PdfPTable welcome = wideTable(1);
            PdfPCell wCell = colorCell(COLOR_BG, 36);
            wCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            wCell.addElement(centeredPara("¡Bienvenido, " + name + " " + surname + "!", hv(20, Font.BOLD, COLOR_TEXT)));
            wCell.addElement(spacedPara("Tu cuenta ha sido creada exitosamente en el Videoclub.",
                    hv(11, Font.NORMAL, COLOR_MUTED), 8));
            welcome.addCell(wCell);
            doc.add(welcome);

            // DIVIDER
            doc.add(redLine());

            // SECCIÓN DATOS
            PdfPTable sec = wideTable(1);
            PdfPCell secCell = colorCell(COLOR_SURFACE, 0);
            secCell.setPaddingTop(20);
            secCell.setPaddingBottom(8);
            secCell.setPaddingLeft(48);
            secCell.setPaddingRight(48);
            secCell.addElement(new Paragraph("DATOS DE TU CUENTA", hv(9, Font.BOLD, COLOR_MUTED)));
            sec.addCell(secCell);
            doc.add(sec);

            // FILAS
            addRow(doc, "Usuario",         "@" + username,                                       COLOR_SURFACE);
            addRow(doc, "Nombre completo", name + " " + surname,                                 COLOR_SURFACE2);
            addRow(doc, "Email",           email,                                                COLOR_SURFACE);
            addRow(doc, "Rol",             "USER".equals(role) ? "Usuario" : "Administrador",   COLOR_SURFACE2);
            addRow(doc, "Miembro desde",   date.toString(),                                      COLOR_SURFACE);

            // DIVIDER
            doc.add(redLine());

            // MENSAJE GAMING
            PdfPTable msg = wideTable(1);
            PdfPCell mCell = colorCell(COLOR_BG, 40);
            mCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mCell.addElement(centeredPara("¡Tu aventura comienza ahora!", hv(16, Font.BOLD, COLOR_GREEN)));
            mCell.addElement(spacedPara(
                    "Explora nuestro catálogo, alquila tus juegos favoritos\ny vive la experiencia definitiva del videoclub.",
                    hv(11, Font.NORMAL, new Color(180, 180, 180)), 10));
            msg.addCell(mCell);
            doc.add(msg);

            // BANDA DECORATIVA
            PdfPTable deco = wideTable(3);
            String[] labels = {"RPG", "ACCIÓN", "AVENTURA"};
            Color[]  colors = {COLOR_RED, new Color(180, 0, 10), new Color(140, 0, 5)};
            for (int i = 0; i < 3; i++) {
                PdfPCell dc = colorCell(colors[i], 0);
                dc.setPadding(14);
                dc.setHorizontalAlignment(Element.ALIGN_CENTER);
                dc.addElement(centeredPara(labels[i], hv(10, Font.BOLD, Color.WHITE)));
                deco.addCell(dc);
            }
            doc.add(deco);

            // FOOTER
            PdfPTable footer = wideTable(1);
            PdfPCell fCell = colorCell(new Color(10, 10, 10), 18);
            fCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            fCell.addElement(centeredPara(
                    "© 2026 Videoclub  ·  Todos los derechos reservados  ·  Disfruta del juego",
                    hv(9, Font.NORMAL, COLOR_MUTED)));
            footer.addCell(fCell);
            doc.add(footer);

            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF", e);
        }

        return baos.toByteArray();
    }

    public byte[] generateContactPdf(String name, String email, String message, LocalDate date) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 0, 0, 0, 0);

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // HEADER
            PdfPTable header = wideTable(1);
            PdfPCell hCell = colorCell(COLOR_RED, 40);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hCell.addElement(centeredPara("VIDEOCLUB", hv(30, Font.BOLD, Color.WHITE)));
            hCell.addElement(spacedPara("Confirmación de mensaje enviado", hv(11, Font.ITALIC, new Color(255, 200, 200)), 4));
            header.addCell(hCell);
            doc.add(header);

            // CONFIRMACIÓN
            PdfPTable confirm = wideTable(1);
            PdfPCell cCell = colorCell(COLOR_BG, 36);
            cCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cCell.addElement(centeredPara("¡Mensaje recibido, " + name + "!", hv(20, Font.BOLD, COLOR_TEXT)));
            cCell.addElement(spacedPara("Hemos recibido tu mensaje correctamente. Te responderemos lo antes posible.",
                    hv(11, Font.NORMAL, COLOR_MUTED), 8));
            confirm.addCell(cCell);
            doc.add(confirm);

            // DIVIDER
            doc.add(redLine());

            // SECCIÓN DATOS
            PdfPTable sec = wideTable(1);
            PdfPCell secCell = colorCell(COLOR_SURFACE, 0);
            secCell.setPaddingTop(20);
            secCell.setPaddingBottom(8);
            secCell.setPaddingLeft(48);
            secCell.setPaddingRight(48);
            secCell.addElement(new Paragraph("DATOS DE TU SOLICITUD", hv(9, Font.BOLD, COLOR_MUTED)));
            sec.addCell(secCell);
            doc.add(sec);

            addRow(doc, "Nombre",     name,           COLOR_SURFACE);
            addRow(doc, "Email",      email,          COLOR_SURFACE2);
            addRow(doc, "Fecha",      date.toString(), COLOR_SURFACE);

            // MENSAJE
            PdfPTable msgTable = wideTable(1);
            PdfPCell msgLabelCell = colorCell(COLOR_SURFACE2, 0);
            msgLabelCell.setPaddingTop(14);
            msgLabelCell.setPaddingBottom(6);
            msgLabelCell.setPaddingLeft(48);
            msgLabelCell.setPaddingRight(48);
            msgLabelCell.addElement(new Paragraph("MENSAJE", hv(9, Font.BOLD, COLOR_MUTED)));
            msgTable.addCell(msgLabelCell);
            doc.add(msgTable);

            PdfPTable msgBody = wideTable(1);
            PdfPCell msgBodyCell = colorCell(COLOR_SURFACE2, 0);
            msgBodyCell.setPaddingTop(6);
            msgBodyCell.setPaddingBottom(20);
            msgBodyCell.setPaddingLeft(48);
            msgBodyCell.setPaddingRight(48);
            msgBodyCell.addElement(new Paragraph(message, hv(12, Font.NORMAL, COLOR_TEXT)));
            msgBody.addCell(msgBodyCell);
            doc.add(msgBody);

            // DIVIDER
            doc.add(redLine());

            // MENSAJE CIERRE
            PdfPTable close = wideTable(1);
            PdfPCell closeCell = colorCell(COLOR_BG, 40);
            closeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            closeCell.addElement(centeredPara("¡Gracias por contactar con nosotros!", hv(16, Font.BOLD, COLOR_GREEN)));
            closeCell.addElement(spacedPara(
                    "Conserva este documento como justificante de tu solicitud.\nNos pondremos en contacto contigo a la brevedad.",
                    hv(11, Font.NORMAL, new Color(180, 180, 180)), 10));
            close.addCell(closeCell);
            doc.add(close);

            // FOOTER
            PdfPTable footer = wideTable(1);
            PdfPCell fCell = colorCell(new Color(10, 10, 10), 18);
            fCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            fCell.addElement(centeredPara(
                    "© 2026 Videoclub  ·  Todos los derechos reservados",
                    hv(9, Font.NORMAL, COLOR_MUTED)));
            footer.addCell(fCell);
            doc.add(footer);

            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF de contacto", e);
        }

        return baos.toByteArray();
    }

    // Helpers

    /** Fuente Helvetica con tamaño, estilo y color. */
    private Font hv(float size, int style, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, size, style, color);
    }

    /** Tabla que ocupa el 100% del ancho. */
    private PdfPTable wideTable(int cols) throws DocumentException {
        PdfPTable t = new PdfPTable(cols);
        t.setWidthPercentage(100);
        t.setSpacingBefore(0);
        t.setSpacingAfter(0);
        return t;
    }

    /** Celda con fondo de color, sin borde y padding uniforme. */
    private PdfPCell colorCell(Color bg, float padding) {
        PdfPCell c = new PdfPCell();
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        if (padding > 0) c.setPadding(padding);
        return c;
    }

    /** Línea roja de 4 puntos de alto. */
    private PdfPTable redLine() throws DocumentException {
        PdfPTable t = wideTable(1);
        PdfPCell c = new PdfPCell();
        c.setBackgroundColor(COLOR_RED);
        c.setBorder(Rectangle.NO_BORDER);
        c.setMinimumHeight(4);
        t.addCell(c);
        return t;
    }

    /** Párrafo centrado. */
    private Paragraph centeredPara(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    /** Párrafo centrado con espaciado superior. */
    private Paragraph spacedPara(String text, Font font, float spacingBefore) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingBefore(spacingBefore);
        return p;
    }

    /** Fila de datos: etiqueta a la izquierda, valor a la derecha. */
    private void addRow(Document doc, String label, String value, Color bg) throws DocumentException {
        PdfPTable row = wideTable(2);
        row.setWidths(new float[]{1f, 2.2f});

        PdfPCell lc = colorCell(bg, 0);
        lc.setPaddingTop(14);
        lc.setPaddingBottom(14);
        lc.setPaddingLeft(48);
        lc.setPaddingRight(12);
        lc.addElement(new Paragraph(label.toUpperCase(), hv(9, Font.BOLD, COLOR_MUTED)));
        row.addCell(lc);

        PdfPCell vc = colorCell(bg, 0);
        vc.setPaddingTop(14);
        vc.setPaddingBottom(14);
        vc.setPaddingLeft(12);
        vc.setPaddingRight(48);
        vc.addElement(new Paragraph(value, hv(12, Font.BOLD, COLOR_TEXT)));
        row.addCell(vc);

        doc.add(row);

        // Separador fino entre filas
        PdfPTable sep = wideTable(1);
        PdfPCell sc = colorCell(COLOR_BORDER, 0);
        sc.setMinimumHeight(1);
        sep.addCell(sc);
        doc.add(sep);
    }
}
