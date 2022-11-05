package com.coder.desgin.util;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ui.ModelMap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The type resp message utils.统一响应信息处理类
 * @Author coder
 * @Date 2022/11/3 14:02
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespMessageUtils {

    /**
     * 状态
     */
    private Boolean result;

    /**
     * 详细的错误信息
     */
    private String msg;

    /**
     * 需要传输的数据
     */
    private Object data;

    /**
     * 状态码
     */
    private Integer statusCode;


    private int size;

    /**
     * 只返回校验结果
     *
     * @param result the result
     */
    public RespMessageUtils(Boolean result) {
        this.result = result;
    }

    /**
     * 返回校验结果和数据
     *
     * @param result the result
     * @param data   the data
     */
    public RespMessageUtils(Boolean result, Object data) {
        this.result = result;
        this.data = data;
    }

    /**
     * 返回校验结果和相关的提示信息
     *
     * @param result the result
     * @param msg    the msg
     */
    public RespMessageUtils(Boolean result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    /**
     * 返回成功的信息
     *
     * @return string
     */
    public static String SUCCESS(){
        return JSON.toJSONString(new RespMessageUtils(true));
    }

    /**
     * 返回成功的信息和数据
     *
     * @param data :数据
     * @return string
     */
    public static String SUCCESS(Object data){
        return JSON.toJSONString(new RespMessageUtils(true,data));
    }

    public static RespMessageUtils SUCCESSOBJ(Object data){
        return new RespMessageUtils(true,data);
    }

    /**
     * 返回错误
     *
     * @return string
     */
    public static String ERROR(){
        return JSON.toJSONString(new RespMessageUtils(false,null));
    }

    /**
     * 返回错误的信息
     *
     * @param msg the msg
     * @return string
     */
    public static String ERROR(String msg){
        return JSON.toJSONString(new RespMessageUtils(false,msg));
    }

    /**
     * 可能产生的错误比较多,因此在响应体中返回ERRORS作为所有错误的总称
     *
     * @param map  the map
     * @param args the args
     */
    public static void generateErrorInfo(ModelMap map, String[] args){
        // TODO errors待定义为一个固定的常量
        Object error = map.getAttribute("errors");
        List<String> errors  = new LinkedList<>();
        if(error == null) {
            errors.addAll(Arrays.asList(args));
        }else{
            errors = (List<String>) error;
            errors.add("用户名和密码错误");
        }
        //响应的信息，应该是放到response响应体的话可以在前端直接获取到
        // TODO errors待定义为一个固定的常量
        map.addAttribute("errors",errors);
    }

    public static RespMessageUtils generateErrorInfoOBJ(ModelMap map, String[] args){
        // TODO errors待定义为一个固定的常量
        Object error = map.getAttribute("errors");
        List<String> errors  = new LinkedList<>();
        if(error == null) {
            errors.addAll(Arrays.asList(args));
        }else{
            errors = (List<String>) error;
            errors.add("用户名和密码错误");
        }
        //响应的信息，应该是放到response响应体的话可以在前端直接获取到
        // TODO errors待定义为一个固定的常量
        map.addAttribute("errors",errors);
        return new RespMessageUtils(false,map);
    }
}
