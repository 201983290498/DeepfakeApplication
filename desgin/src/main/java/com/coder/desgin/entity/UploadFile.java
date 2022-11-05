package com.coder.desgin.entity;

import lombok.Data;

/**
 * @Author coder
 * @Date 2022/10/30 14:05
 * @Description 图像类
 */
@Data
public class UploadFile {

    private String type;
    private String name;
    private Integer size;
    private String base64;


}
