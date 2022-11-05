package com.coder.desgin.entity;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author coder
 * @Date 2022/11/3 15:36
 * @Description
 */
@Data
public class ImgDetectorResult {

    /**
     * 文件的名称
     */
    private String imageName;

    /**
     * 图片的检测结果
     */
    private List<DetectorRect> rects = new LinkedList<>();

}
