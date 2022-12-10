package com.coder.desgin.service;

import com.coder.desgin.service.inter.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;

/**
 * @author coder
 * @date 2022/11/13 11:31
 * @Description
 */
@Service
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Override
    public void sendMail(String to,String title, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(to);
        helper.setFrom(Objects.requireNonNull(javaMailSender.getUsername()));
        helper.setSubject(title);
        helper.setText(text);
        javaMailSender.send(message);
    }

    @Override
    public void sendMail(String to, String title, String text, Boolean http) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(to);
        helper.setFrom(Objects.requireNonNull(javaMailSender.getUsername()));
        helper.setSubject(title);
        helper.setText(text,true);
        javaMailSender.send(message);
    }

    @Override
    public void sendMail(String to, String title, String text, Boolean http, String attachments) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(to);
        helper.setFrom(Objects.requireNonNull(javaMailSender.getUsername()));
        helper.setSubject(title);
        helper.setText(text,true);
        helper.addAttachment(attachments.substring(attachments.lastIndexOf("/")+1),new File(attachments));
        javaMailSender.send(message);
    }

}
