package com.coder.desgin.util;

import com.coder.desgin.entity.ValidationInfo;
import com.coder.desgin.exception.MailMessageException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 这是一个邮件管理类，主要的功能是发送文本文件和验证码
 * The management of verification code including sending, checking and valid Message queue
 * @Author coder
 * @Date 2021 /12/1 20:20
 * @Description
 */
@Data
@Component
@Scope("singleton") // 设置单例模式
@AllArgsConstructor
@NoArgsConstructor
@PropertySource("classpath:mySetting.properties") // 加载配置文件
public class JavaEmail {

    /**
     * 发送邮件的授权邮箱
     */
    @Value("${email.emailAddress}")
    private String sendEmail;

    /**
     * 发送邮箱的授权码
     */
    @Value("${email.emailPwd}")
    private String pwd;

    /**
     * 验证码的有效时间
     */
    @Value("${email.timeout}")
    private Long timeout;

    /**
     * HashMap底层时一个红黑树(二叉排序树,按照关键字的查找效率始终是logN)
     * 目前在队列中存在的有效验证码，主要用于邮箱和验证码之间的映射
     * key: email
     * value: validationinfo
     */
    private HashMap<String,String> messageMap = new HashMap<>();

    /**
     * 消息队列，主要看验证码是否过期。如果不使用该队列，每次都需要遍历HashMap删除过时信息，不合理。
     */
    private ArrayDeque<ValidationInfo> validationInfoQueue = new ArrayDeque<>();

    /**
     * 根据邮箱发送验证码
     * @param email the email
     * @return Boolean
     */
    public Boolean sendValidationInfo(String email){
        clearOutdatedInfo();
        // 获取验证码，先查看是否已经存在，不存在创建一个验证码
        String msg = messageMap.get(email);
        if(msg==null){
            ValidationInfo validationInfo = new ValidationInfo(email);
            validationInfoQueue.addLast(validationInfo);
            msg = validationInfo.getMessage();
        }
        messageMap.put(email, msg);
        sendHtmlMsg(email,generationValidationHtml(email, msg));
        return true;
    }

    /**
     * 对验证码进行验证
     *
     * @param email   the email
     * @param message the message
     * @return the boolean
     * @throws MailMessageException the message exception
     */
    public Boolean checkValidationInfo(String email, String message) throws MailMessageException {
        clearOutdatedInfo();
        String msg = messageMap.get(email);
        if(message!=null&&message.equals(msg)){
            return true;
        }else{
            throw new MailMessageException("验证码错误");
        }
    }

    /**
     * 以html文本作为邮件内容发送给用户
     * @param receiveEmail
     * @param html
     */
    private void sendHtmlMsg(String receiveEmail, String html){
        try{
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
//            设置邮箱主机，如果是qq邮箱就是smtp.qq.com，网易smtp.163.com
            sender.setHost("smtp.qq.com");
//            设置编码集合
            sender.setDefaultEncoding("utf-8");
            //建立邮箱消息,我们需要以html格式发送邮件
            MimeMessage mailMessage = sender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);

//            设置收件人，寄件人和邮件的主题
            messageHelper.setTo(receiveEmail);
            messageHelper.setFrom(sendEmail);
            messageHelper.setSubject("DeepFakeApplication验证系统");

            messageHelper.setText(html,true);

            //设置发送的账号和密码，状态码
            sender.setUsername(sendEmail);
            sender.setPassword(pwd);

            Properties prop = new Properties();
            /* 让服务器去认证用户名和密码 */
            prop.put("mail.smtp.auth", "true");
            /* 连接超时时间 */
            prop.put("mail.smtp.timeout", "4000");
            sender.setJavaMailProperties(prop);
            sender.send(mailMessage);
        } catch (MessagingException e) {
            System.out.println("接收人或者寄件人邮箱错误");
            e.printStackTrace();
        }
    }

    /**
     * 使用单纯的javax发送邮件(带附件)。
     */
    private Boolean sendMessageWithFile(String receiveEmail, String file) throws MessagingException, UnsupportedEncodingException {
        Properties prop = new Properties();
        /* 让服务器去认证用户名和密码 */
        prop.put("mail.smtp.auth", "true");
        /* 连接超时时间 */
        prop.put("mail.smtp.timeout", "4000");
        prop.put("mail.smtp.host", "smtp.qq.com");
        // 根据属性创建一个邮件会话
        Session session = Session.getInstance(prop);
        // 打印调试信息
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(sendEmail);
        message.setRecipients(Message.RecipientType.TO, receiveEmail);
        message.setSubject("DeepFake检测文本");
        // 设置发送时间
        message.setSentDate(new Date());

        // 发送纯文本
        // message.setText("DeepFakeApplicaiton已经检测完成, 检测文本以发送在附件中");
        // 发送html邮件，样式丰富。
        // message.setContent("232323232323", "text/html;charset=gbk");

        Multipart multipart = new MimeMultipart();
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setText("DeepFakeApplicaiton已经检测完成, 检测文本以发送在附件中");
        multipart.addBodyPart(contentPart);
        // 添加附件
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource dataSource = new FileDataSource(new File(file));
        // 添加附件内容
        messageBodyPart.setDataHandler(new DataHandler(dataSource));
        // 添加标题通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
        messageBodyPart.setFileName(MimeUtility.encodeText(dataSource.getName()));
        multipart.addBodyPart(messageBodyPart);
        // 发送带附件
        message.setContent(multipart);
        message.saveChanges();// 存储邮件信息

        // 发送邮件
        Transport transport = session.getTransport();
        // 发送邮箱和授权码
        transport.connect(sendEmail, pwd);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        return true;
    }

    /**
     * 删除消息队列中过时的验证信息
     */
    private void clearOutdatedInfo(){
        boolean flag=true;
        while(!validationInfoQueue.isEmpty()&&flag){
            ValidationInfo validationInfo = validationInfoQueue.getFirst();
            if(System.currentTimeMillis()-validationInfo.getCreateTime()>=timeout){
                //状态码失效
                messageMap.remove(validationInfo.getEmail());
                validationInfoQueue.removeFirst();
            }else{
                flag=false;
            }
        }
    }

    /**
     * 生成验证码的html文本页面
     * @param receiveEmail 接收邮箱
     * @param validationCode 验证码
     * @return 返回生成码页面
     */
    private String generationValidationHtml(String receiveEmail, String validationCode){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm::ss");

        return  "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body>"
                +"<p style='font-size:20px;font-weight:blod;'>尊敬的："+ receiveEmail +"用户,您好</p>"
                +"<p style='text-indent:2em;font-size:20px'>欢迎注册DeepFakeApplication，您本次的验证码是 <span style='font-size:30px; font-weight:blod; color:red;'>"+ validationCode +"</span>,10分钟之内有效，请尽快填写!</p>"+
                "<p style='text-align:right; padding-right:20px;'> <a href='https:www.coderSimple.com' style='font-size18px;'>DeepFakeApplication团队</a></p>"
                +"<span style='font-size:18px; float:right; margin-right:60px;'>"+ sdf.format(new Date()) +"</span></body></html>";


    }

    public JavaEmail(String sendEmail, String pwd, Long timeout) {
        this.sendEmail = sendEmail;
        this.pwd = pwd;
        this.timeout = timeout;
    }

}
