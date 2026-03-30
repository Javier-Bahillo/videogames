package Videoclub.service.impl;

import Videoclub.service.EmailService;
import Videoclub.service.PdfService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    private static final String SENDER_NAME = "Mecaboll";

    @Value("${spring.mail.username}")
    private String fromEmail;

    private InternetAddress senderAddress() throws MessagingException {
        try {
            return new InternetAddress(fromEmail, SENDER_NAME, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Encoding error building sender address", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String name, String surname,
                                 String username, String role, LocalDate createdAt) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderAddress());
            helper.setTo(toEmail);
            helper.setSubject("🎮 ¡Bienvenido al Videoclub, " + name + "!");
            helper.setText(buildHtml(name, surname, username, toEmail, role, createdAt), true);

            byte[] pdf = pdfService.generateWelcomePdf(name, surname, username, toEmail, role, createdAt);
            helper.addAttachment("bienvenida_videoclub.pdf",
                    new ByteArrayResource(pdf), "application/pdf");

            mailSender.send(message);
            log.info("Email de bienvenida enviado a {}", toEmail);

        } catch (MessagingException e) {
            log.error("Error enviando email a {}: {}", toEmail, e.getMessage());
            // No propagamos el error — el registro no debe fallar por el email
        }
    }

    @Override
    public void sendDeactivationEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderAddress());
            helper.setTo(toEmail);
            helper.setSubject("🎮 Videoclub — Tu cuenta ha sido desactivada");
            helper.setText(buildSimpleHtml(
                "Cuenta desactivada",
                "Hasta pronto, " + name + ".",
                "Tu cuenta ha sido desactivada correctamente.<br>Si esto fue un error o deseas volver en cualquier momento, contacta con nosotros.",
                "#e50914"
            ), true);
            mailSender.send(message);
            log.info("Email de baja enviado a {}", toEmail);
        } catch (MessagingException e) {
            log.error("Error enviando email de baja a {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendReactivationEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderAddress());
            helper.setTo(toEmail);
            helper.setSubject("🎮 Videoclub — ¡Tu cuenta ha sido reactivada!");
            helper.setText(buildSimpleHtml(
                "¡Cuenta reactivada!",
                "¡Bienvenido de vuelta, " + name + "!",
                "Tu cuenta ha sido reactivada y ya puedes acceder de nuevo al catálogo.<br>¡Esperamos que disfrutes de nuestros juegos!",
                "#4ade80"
            ), true);
            mailSender.send(message);
            log.info("Email de reactivación enviado a {}", toEmail);
        } catch (MessagingException e) {
            log.error("Error enviando email de reactivación a {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendRentalEmail(String toEmail, String name, String gameTitle,
                                LocalDate startAt, LocalDate dueAt, BigDecimal price) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderAddress());
            helper.setTo(toEmail);
            helper.setSubject("🎮 Videoclub — Alquiler confirmado: " + gameTitle);
            String body = "<html><body style='margin:0;padding:0;background:#0f0f0f;font-family:Segoe UI,sans-serif;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:40px 20px;'>" +
                "<table width='560' cellpadding='0' cellspacing='0' style='border-radius:12px;overflow:hidden;border:1px solid #2e2e2e;'>" +
                "<tr><td style='background:#e50914;padding:28px;text-align:center;'>" +
                "<h1 style='color:white;margin:0;font-size:24px;letter-spacing:3px;'>VIDEOCLUB</h1></td></tr>" +
                "<tr><td style='background:#1a1a1a;padding:32px;text-align:center;'>" +
                "<h2 style='color:#f0f0f0;margin:0 0 8px;'>¡Alquiler confirmado!</h2>" +
                "<p style='color:#888;margin:0;font-size:14px;'>Hola <strong style='color:#f0f0f0;'>" + name + "</strong>, aquí tienes los detalles de tu alquiler.</p></td></tr>" +
                "<tr><td style='background:#e50914;height:3px;'></td></tr>" +
                "<tr><td style='background:#1a1a1a;padding:28px 36px;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0'>" +
                dataRow("Juego", gameTitle) +
                dataRow("Fecha de inicio", startAt.toString()) +
                dataRow("Fecha de devolución", dueAt.toString()) +
                dataRowLast("Precio", "€" + price) +
                "</table></td></tr>" +
                "<tr><td style='background:#141414;padding:24px;text-align:center;'>" +
                "<p style='color:#aaa;font-size:13px;margin:0;'>Recuerda devolver el juego antes del <strong style='color:#f0f0f0;'>" + dueAt + "</strong> para evitar recargos.</p></td></tr>" +
                "<tr><td style='background:#e50914;padding:14px;text-align:center;'>" +
                "<p style='color:rgba(255,255,255,0.85);font-size:11px;margin:0;'>© 2026 Videoclub · Todos los derechos reservados</p></td></tr>" +
                "</table></td></tr></table></body></html>";
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Email de alquiler enviado a {}", toEmail);
        } catch (MessagingException e) {
            log.error("Error enviando email de alquiler a {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendReturnEmail(String toEmail, String name, String gameTitle,
                                LocalDate returnedAt, BigDecimal lateFee) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderAddress());
            helper.setTo(toEmail);
            helper.setSubject("🎮 Videoclub — Devolución registrada: " + gameTitle);
            boolean hasLateFee = lateFee != null && lateFee.compareTo(BigDecimal.ZERO) > 0;
            String feeRow = hasLateFee
                ? dataRowLast("Recargo por retraso", "€" + lateFee)
                : dataRowLast("Recargo", "Sin recargo ✓");
            String feeNote = hasLateFee
                ? "<p style='color:#f87171;font-size:13px;margin:0;'>Se ha aplicado un recargo de <strong>€" + lateFee + "</strong> por devolución tardía.</p>"
                : "<p style='color:#4ade80;font-size:13px;margin:0;'>¡Devuelto a tiempo! Sin recargos adicionales.</p>";
            String body = "<html><body style='margin:0;padding:0;background:#0f0f0f;font-family:Segoe UI,sans-serif;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:40px 20px;'>" +
                "<table width='560' cellpadding='0' cellspacing='0' style='border-radius:12px;overflow:hidden;border:1px solid #2e2e2e;'>" +
                "<tr><td style='background:#e50914;padding:28px;text-align:center;'>" +
                "<h1 style='color:white;margin:0;font-size:24px;letter-spacing:3px;'>VIDEOCLUB</h1></td></tr>" +
                "<tr><td style='background:#1a1a1a;padding:32px;text-align:center;'>" +
                "<h2 style='color:#f0f0f0;margin:0 0 8px;'>Devolución registrada</h2>" +
                "<p style='color:#888;margin:0;font-size:14px;'>Hola <strong style='color:#f0f0f0;'>" + name + "</strong>, hemos registrado la devolución de tu juego.</p></td></tr>" +
                "<tr><td style='background:#e50914;height:3px;'></td></tr>" +
                "<tr><td style='background:#1a1a1a;padding:28px 36px;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0'>" +
                dataRow("Juego devuelto", gameTitle) +
                dataRow("Fecha de devolución", returnedAt.toString()) +
                feeRow +
                "</table></td></tr>" +
                "<tr><td style='background:#141414;padding:24px;text-align:center;'>" + feeNote + "</td></tr>" +
                "<tr><td style='background:#e50914;padding:14px;text-align:center;'>" +
                "<p style='color:rgba(255,255,255,0.85);font-size:11px;margin:0;'>© 2026 Videoclub · Todos los derechos reservados</p></td></tr>" +
                "</table></td></tr></table></body></html>";
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Email de devolución enviado a {}", toEmail);
        } catch (MessagingException e) {
            log.error("Error enviando email de devolución a {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildSimpleHtml(String title, String heading, String body, String accentColor) {
        return "<html><body style='margin:0;padding:0;background:#0f0f0f;font-family:Segoe UI,sans-serif;'>" +
               "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:40px 20px;'>" +
               "<table width='560' cellpadding='0' cellspacing='0' style='border-radius:12px;overflow:hidden;border:1px solid #2e2e2e;'>" +
               "<tr><td style='background:#e50914;padding:28px;text-align:center;'>" +
               "<h1 style='color:white;margin:0;font-size:24px;letter-spacing:3px;'>VIDEOCLUB</h1></td></tr>" +
               "<tr><td style='background:#1a1a1a;padding:36px;text-align:center;'>" +
               "<h2 style='color:" + accentColor + ";margin:0 0 12px;font-size:22px;'>" + heading + "</h2>" +
               "<p style='color:#aaa;font-size:14px;line-height:1.7;margin:0;'>" + body + "</p></td></tr>" +
               "<tr><td style='background:#e50914;padding:14px;text-align:center;'>" +
               "<p style='color:rgba(255,255,255,0.85);font-size:11px;margin:0;'>© 2026 Videoclub · Todos los derechos reservados</p></td></tr>" +
               "</table></td></tr></table></body></html>";
    }

    private String buildHtml(String name, String surname, String username,
                              String email, String role, LocalDate date) {
        String rolLabel = "USER".equals(role) ? "Usuario estándar" : "Administrador";
        return "<html><body style='margin:0;padding:0;background:#0f0f0f;" +
               "font-family:Segoe UI,sans-serif;'>" +
               "<table width='100%' cellpadding='0' cellspacing='0'>" +
               "<tr><td align='center' style='padding:40px 20px;'>" +
               "<table width='560' cellpadding='0' cellspacing='0' " +
               "style='border-radius:12px;overflow:hidden;border:1px solid #2e2e2e;'>" +

               // Header
               "<tr><td style='background:#e50914;padding:32px;text-align:center;'>" +
               "<h1 style='color:white;margin:0;font-size:28px;letter-spacing:3px;'>VIDEOCLUB</h1>" +
               "<p style='color:rgba(255,255,255,0.8);margin:6px 0 0;font-size:12px;" +
               "font-style:italic;'>Tu destino gamer definitivo</p></td></tr>" +

               // Bienvenida
               "<tr><td style='background:#1a1a1a;padding:36px;text-align:center;'>" +
               "<h2 style='color:#f0f0f0;margin:0 0 10px;font-size:22px;'>" +
               "¡Bienvenido, " + name + " " + surname + "!</h2>" +
               "<p style='color:#888;margin:0;font-size:14px;'>" +
               "Tu cuenta ha sido creada exitosamente.</p></td></tr>" +


               "<tr><td style='background:#e50914;height:3px;'></td></tr>" +

               // Datos
               "<tr><td style='background:#1a1a1a;padding:28px 36px;'>" +
               "<p style='color:#555;font-size:10px;text-transform:uppercase;" +
               "letter-spacing:1.5px;margin:0 0 18px;'>Datos de tu cuenta</p>" +
               "<table width='100%' cellpadding='0' cellspacing='0'>" +
               dataRow("Usuario", "@" + username) +
               dataRow("Nombre completo", name + " " + surname) +
               dataRow("Email", email) +
               dataRow("Rol", rolLabel) +
               dataRowLast("Miembro desde", date.toString()) +
               "</table></td></tr>" +


               "<tr><td style='background:#e50914;height:3px;'></td></tr>" +

               // Mensaje gaming
               "<tr><td style='background:#141414;padding:32px;text-align:center;'>" +
               "<p style='color:#4ade80;font-size:18px;font-weight:700;margin:0 0 12px;'>" +
               "¡Tu aventura comienza ahora!</p>" +
               "<p style='color:#aaa;font-size:13px;line-height:1.7;margin:0;'>" +
               "Explora nuestro catálogo, alquila tus juegos favoritos<br>" +
               "y vive la experiencia definitiva del videoclub.</p></td></tr>" +

               // Footer
               "<tr><td style='background:#e50914;padding:16px;text-align:center;'>" +
               "<p style='color:rgba(255,255,255,0.85);font-size:11px;margin:0;'>" +
               "© 2026 Videoclub · Todos los derechos reservados</p></td></tr>" +

               "</table></td></tr></table></body></html>";
    }

    @Override
    public void sendContactEmail(String fromName, String fromEmail, String message) {
        java.time.LocalDate today = java.time.LocalDate.now();

        // 1. Notificación interna al admin (enviada a la cuenta del videoclub)
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(senderAddress());
            helper.setTo(fromEmail);
            helper.setReplyTo(fromEmail);
            helper.setSubject("📬 Contacto Videoclub — " + fromName);
            String body = "<html><body style='margin:0;padding:0;background:#0f0f0f;font-family:Segoe UI,sans-serif;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0'><tr><td align='center' style='padding:40px 20px;'>" +
                "<table width='560' cellpadding='0' cellspacing='0' style='border-radius:12px;overflow:hidden;border:1px solid #2e2e2e;'>" +
                "<tr><td style='background:#e50914;padding:24px;text-align:center;'>" +
                "<h1 style='color:white;margin:0;font-size:22px;letter-spacing:3px;'>VIDEOCLUB</h1></td></tr>" +
                "<tr><td style='background:#1a1a1a;padding:28px 36px;'>" +
                "<h2 style='color:#f0f0f0;margin:0 0 20px;font-size:18px;'>Nuevo mensaje de contacto</h2>" +
                "<table width='100%' cellpadding='0' cellspacing='0'>" +
                dataRow("Nombre", fromName) +
                dataRow("Email", fromEmail) +
                dataRowLast("Mensaje", message.replace("\n", "<br>")) +
                "</table></td></tr>" +
                "<tr><td style='background:#e50914;padding:14px;text-align:center;'>" +
                "<p style='color:rgba(255,255,255,0.85);font-size:11px;margin:0;'>© 2026 Videoclub · Todos los derechos reservados</p></td></tr>" +
                "</table></td></tr></table></body></html>";
            helper.setText(body, true);
            mailSender.send(msg);
            log.info("Email de contacto (admin) enviado desde {}", fromEmail);
        } catch (MessagingException e) {
            log.error("Error enviando notificación de contacto al admin: {}", e.getMessage());
        }

        // 2. Confirmación al remitente con PDF adjunto
        try {
            MimeMessage confirm = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(confirm, true, "UTF-8");
            helper.setFrom(senderAddress());
            helper.setTo(fromEmail);
            helper.setSubject("🎮 Videoclub — Hemos recibido tu mensaje");
            String body = buildSimpleHtml(
                "Mensaje enviado",
                "¡Gracias, " + fromName + "!",
                "Hemos recibido tu mensaje correctamente y te responderemos lo antes posible.<br>" +
                "Adjuntamos un PDF como justificante de tu solicitud.",
                "#4ade80"
            );
            helper.setText(body, true);

            byte[] pdf = pdfService.generateContactPdf(fromName, fromEmail, message, today);
            helper.addAttachment("confirmacion_contacto_videoclub.pdf",
                    new ByteArrayResource(pdf), "application/pdf");

            mailSender.send(confirm);
            log.info("Confirmación de contacto con PDF enviada a {}", fromEmail);
        } catch (MessagingException e) {
            log.error("Error enviando confirmación de contacto a {}: {}", fromEmail, e.getMessage());
        }
    }

    private String dataRow(String label, String value) {
        return "<tr><td style='padding:11px 0;border-bottom:1px solid #2e2e2e;'>" +
               "<span style='color:#666;font-size:11px;'>" + label + "</span><br>" +
               "<strong style='color:#f0f0f0;font-size:15px;'>" + value + "</strong>" +
               "</td></tr>";
    }

    private String dataRowLast(String label, String value) {
        return "<tr><td style='padding:11px 0;'>" +
               "<span style='color:#666;font-size:11px;'>" + label + "</span><br>" +
               "<strong style='color:#f0f0f0;font-size:15px;'>" + value + "</strong>" +
               "</td></tr>";
    }
}
