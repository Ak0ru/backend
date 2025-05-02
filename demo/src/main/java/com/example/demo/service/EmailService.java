package com.example.demo.service;

import com.example.demo.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Отправка простого текста
    public void sendSimpleEmail(EmailRequest req) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(req.getTo().toArray(new String[0]));
        msg.setSubject(req.getSubject());
        msg.setText(req.getBody());
        mailSender.send(msg);
    }

    // Отправка HTML
    public void sendHtmlEmail(EmailRequest req) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
        helper.setTo(req.getTo().toArray(new String[0]));
        helper.setSubject(req.getSubject());
        helper.setText(req.getBody(), true);
        mailSender.send(mime);
    }

    // Отправка с вложениями
    public void sendEmailWithAttachment(EmailRequest req) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setTo(req.getTo().toArray(new String[0]));
        helper.setSubject(req.getSubject());
        helper.setText(req.getBody(), false);
        if (req.getAttachments() != null) {
            for (String path : req.getAttachments()) {
                FileSystemResource file = new FileSystemResource(new File(path));
                helper.addAttachment(file.getFilename(), file);
            }
        }
        mailSender.send(mime);
    }
}
