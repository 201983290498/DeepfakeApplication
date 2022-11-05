package com.coder.desgin.util;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.UUID;


/**
 * @Author coder
 * @Date 2022/11/2 21:17
 * @Description
 */
@Data
public class ImageUtil {

    /**
     * 检测文件夹
     */
    private static String detectDir = "detectFile/";

    /**
     * @Description: 将base64编码字符串转换成图片
     * @param file base64编码字符串
     * @return 返回文件具体写入的路径
     */
    public static String generateImage(String filename, String file, HttpServletRequest request) {
        try {
            // 获取项目的绝对路径 todo 获取 tomcat的路径，需要输出检查  结果如下：C:\Users\coder\AppData\Local\Temp\tomcat-docbase.8088.4091451682675346135\
            String savePath = request.getSession().getServletContext().getRealPath("/");
            File dir = new File(savePath + detectDir + UUID.randomUUID().toString().substring(0, 6));
            // 查看检测文件夹/context/detectFile是否存在
            if(!dir.getParentFile().exists()){
                dir.getParentFile().mkdir();
            }
            // 创建检测文件夹/context/detectFile/path
            if(!dir.exists()){
                dir.mkdir();
            }
            String filePath = dir.getAbsolutePath().concat("\\").concat(filename);
            Decoder decoder = Base64.getDecoder();
            // 删除Base64开头的文件类型
            file = file.substring(file.indexOf(",", 1) + 1);
            byte[] bytes = decoder.decode(file);
            // 处理数据
            for(int i = 0; i< bytes.length; i++){
                if(bytes[i] < 0){
                    bytes[i] += 256;
                }
            }
            //保存图片
            OutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.flush();
            out.close();
            return filePath;
        }catch(Exception ignored){
            return null;
        }
    }

    /**
     * @Description 将图像转换成Base64字符串
     * @param imgPath 输入的文件路径
     * @return 返回base64字符串
     */
    public static String GenImageStr(String imgPath){
        InputStream in = null;
        byte[] data = null;
        String encode = "";
        Encoder encoder = Base64.getEncoder();
        try{
            in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            encode = "data:image/jpg;base64," + new String(encoder.encode(data));
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return encode;
    }
}
