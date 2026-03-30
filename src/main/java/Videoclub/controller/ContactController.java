package Videoclub.controller;

import Videoclub.dto.ContactRequest;
import Videoclub.service.EmailService;
import Videoclub.service.RecaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;
    private final RecaptchaService recaptchaService;

    @PostMapping("/contacto/send")
    public ResponseEntity<String> sendContact(@Valid @RequestBody ContactRequest request) {
        if (!recaptchaService.verify(request.getRecaptchaToken())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verificación reCAPTCHA fallida.");
        }
        emailService.sendContactEmail(request.getName(), request.getEmail(), request.getMessage());
        return ResponseEntity.ok().build();
    }
}
