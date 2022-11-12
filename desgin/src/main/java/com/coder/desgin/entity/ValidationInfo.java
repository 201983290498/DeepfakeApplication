package com.coder.desgin.entity;

import lombok.Data;

import java.util.UUID;

/**
 * 获取验证码
 * @Author coder
 * @Date 2022/11/12 14:31
 * @Description
 */
@Data
public class ValidationInfo {

    /**
     * 验证码信息
     */
    private String message;

    /**
     * 创建的时间
     */
    private Long createTime;

    /**
     * 发送的邮箱
     */
    private String email;

    public ValidationInfo(String email) {
        this.email = email;
        this.message = UUID.randomUUID().toString().substring(0, 6);
        this.createTime = System.currentTimeMillis();
    }
}
