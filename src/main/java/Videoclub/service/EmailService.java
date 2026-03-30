package Videoclub.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EmailService {
    void sendWelcomeEmail(String toEmail, String name, String surname,
                          String username, String role, LocalDate createdAt);

    void sendDeactivationEmail(String toEmail, String name);

    void sendReactivationEmail(String toEmail, String name);

    void sendRentalEmail(String toEmail, String name, String gameTitle,
                         LocalDate startAt, LocalDate dueAt, BigDecimal price);

    void sendReturnEmail(String toEmail, String name, String gameTitle,
                         LocalDate returnedAt, BigDecimal lateFee);

    void sendContactEmail(String fromName, String fromEmail, String message);
}
