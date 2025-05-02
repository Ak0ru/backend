package com.example.demo.dto;

import java.util.List;

public class EmailRequest {
    private List<String> to;
    private String subject;
    private String body;

    // для attachment: список путей к файлам на сервере
    private List<String> attachments;

    public List<String> getTo() { return to; }
    public void setTo(List<String> to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
}
