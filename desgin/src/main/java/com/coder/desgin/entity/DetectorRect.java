package com.coder.desgin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author coder
 * @Date 2022/11/3 14:12
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectorRect {

    /**
     * 检测框
     */
    private Double x1, y1, x2, y2;

    /**
     * 检测自信度
     */
    private Double confidence;

    /**
     * 检测框内物品的类型 或者 真假
     */
    private String type;

    @Override
    public String toString() {
        return "检测框信息:{" +
                "type='" + type + '\'' +
                ", confidence=" + confidence +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }
}
