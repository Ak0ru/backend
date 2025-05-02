package com.example.demo.controller;

import com.example.demo.dto.EmailRequest;
import com.example.demo.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-simple-email")
    public ResponseEntity<String> sendSimple(@RequestBody EmailRequest req) {
        emailService.sendSimpleEmail(req);
        return ResponseEntity.ok("Simple email sent");
    }

    @PostMapping("/send-html-email")
    public ResponseEntity<String> sendHtml(@RequestBody EmailRequest req) throws MessagingException {
        emailService.sendHtmlEmail(req);
        return ResponseEntity.ok("HTML email sent");
    }

    @PostMapping("/send-email-with-attachment")
    public ResponseEntity<String> sendWithAttach(@RequestBody EmailRequest req) throws MessagingException {
        emailService.sendEmailWithAttachment(req);
        return ResponseEntity.ok("Email with attachments sent");
    }
}
