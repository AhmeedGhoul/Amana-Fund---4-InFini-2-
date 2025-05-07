package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.EmailTemplateName;
import com.ghoul.AmanaFund.entity.FraudCases;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
public class EmailService {
private final JavaMailSender mailSender;
private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
public void sendEmail(String to, String username, EmailTemplateName emailTemplate, String confirmationUrl, String activationCode, String subject) throws MessagingException {
String templateName;
if(emailTemplate==null){
    templateName = "confirm-email";
}
else {
    templateName = emailTemplate.name();
}
MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper messageHelper = new MimeMessageHelper(message,MULTIPART_MODE_MIXED,UTF_8.name());
Map<String, Object> properties = new HashMap<>();
properties.put("username", username);
properties.put("confirmationUrl", confirmationUrl);
properties.put("activationCode", activationCode);
Context context = new Context();
context.setVariables(properties);
messageHelper.setFrom("ahmed.ghoul@esprit.tn");
messageHelper.setTo(to);
messageHelper.setSubject(subject);
messageHelper.setText(templateEngine.process(templateName, context), true);

mailSender.send(message);
}
    @Async
    public void sendEmail(String to, String username, EmailTemplateName emailTemplate, List<FraudCases> fraudCases, String subject) throws MessagingException {
        String templateName = emailTemplate != null ? emailTemplate.name() : "fraud-case-notification";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED, UTF_8.name());
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("fraudCases", fraudCases);
        Context context = new Context();
        context.setVariables(properties);
        messageHelper.setFrom("ahmed.ghoul@esprit.tn");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(templateEngine.process(templateName, context), true);
        mailSender.send(message);
    }
    @Async
    public void sendEmail(String to, EmailTemplateName emailTemplate, String confirmationUrl, String activationCode, String subject) throws MessagingException {
        String templateName;
        if(emailTemplate==null){
            templateName = "confirm-email";
        }
        else {
            templateName = emailTemplate.name();
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message,MULTIPART_MODE_MIXED,UTF_8.name());
        Map<String, Object> properties = new HashMap<>();
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activationCode", activationCode);
        Context context = new Context();
        context.setVariables(properties);
        messageHelper.setFrom("ahmed.ghoul@esprit.tn");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(templateEngine.process(templateName, context), true);

        mailSender.send(message);
    }


}
