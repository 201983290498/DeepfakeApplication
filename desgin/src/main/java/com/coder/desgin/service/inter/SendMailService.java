package com.coder.desgin.service.inter;

import javax.mail.MessagingException;

/**
 * @author coder
 * @date 2022/11/13 11:31
 * @Description 通过Springboot实现的java类
 */
public interface SendMailService {

    /**
     * 发送文本类型的邮件
     * @param to 目的email
     * @param title 主题
     * @param text 发送的文本内容
     * @throws MessagingException 可能的报错
     */
    void sendMail(String to,String title, String text) throws MessagingException;

    /**
     * 发送html类型的邮件
     * @param to 目的email
     * @param title 主题
     * @param text 发送的文本内容
     * @param http 是否采用http格式
     * @throws MessagingException 可能的报错
     */
    void sendMail(String to,String title,String text, Boolean http) throws MessagingException;

    /**
     * 发送html类型的邮件
     * @param to 目的email
     * @param title 主题
     * @param text 发送的文本内容
     * @param http 是否采用http格式
     * @param attachments 传输的附件
     * @throws MessagingException 可能的报错
     */
    void sendMail(String to,String title,String text, Boolean http, String attachments) throws MessagingException;
}
