package com.coder.desgin.entity;

import lombok.Data;

/**
 * @author Pengfei Yue
 * @ClassName NormalDecFile
 * @date 2022/11/8
 * @description 普通检测上传文件Bean
 */
@Data
public class NormalDetectionFile {
    private String type;
    private String name;
    private Integer size;
    private String base64;
    /**
     * 普通检测类型：copymove、splicing、general
     */
    private String detectType;
}
