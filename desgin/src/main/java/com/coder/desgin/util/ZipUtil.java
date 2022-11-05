package com.coder.desgin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * todo 需要在项目创建对应的文件夹。
 * @Author coder
 * @Date 2022/11/2 21:53
 * @Description 压缩文件上传，解压
 */
public class ZipUtil {

    /**
     * 读写的缓冲池的大小
     */
    private static final int BUFFER_SIZE = 1024 * 2;

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * detector文件夹
     */
    private static String detectDir = "detectFile/";

    /**
     * 将zip文件的base64转换成相应的文件
     * @param base64 文件的base64字符串
     * @param filename 解压的文件名
     * @param request http的请求，获取项目在本地的地址
     * @return 返回压缩文件的地址
     */
    public static String Base64File(String base64, String filename, HttpServletRequest request){
        filename = filename.substring(0, filename.lastIndexOf('.'));

        String contentPath = request.getSession().getServletContext().getRealPath("/");
        File dir = new File(contentPath + detectDir + UUID.randomUUID().toString().substring(0, 6));
        // 查看检测文件夹/context/detectFile是否存在
        if(!dir.getParentFile().exists()){
            dir.getParentFile().mkdir();
        }
        // 创建检测文件夹/context/detectFile/path
        if(!dir.exists()){
            dir.mkdir();
        }

        // 解压文件夹
        String filePath = dir.getAbsolutePath().concat("\\").concat(filename);
        dir = new File(filePath);
        if(!dir.exists()){
            dir.mkdir();
        }

        // 将base64去掉文件头
        base64 = base64.substring(base64.indexOf(',')+1);
        try{
            // todo 这个类有啥用
            byte[] bytes = Base64.getDecoder().decode(base64);
            ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
            ZipInputStream zipInput = new ZipInputStream(byteArray);
            ZipEntry entry = zipInput.getNextEntry();
            File fout = null;
            while(entry != null && !entry.isDirectory()){
                // todo 这边需要断点，查看这边函数的具体意义
                log.info("文件名称： [{}]", entry.getName());
                fout = new File(dir, entry.getName());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fout));
                int offo = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while((offo=zipInput.read(buffer)) != -1){
                    bos.write(buffer, 0, offo);
                }
                bos.close();
                // 获取下一个文件
                entry = zipInput.getNextEntry();
            }
            zipInput.close();
            byteArray.close();
        }catch(Exception e){
            throw new RuntimeException("解压流出现异常", e);
        }
        return filePath;
    }

    /**
     * todo 待检测 该函数还未检测
     * @param arcFiles
     * @return
     * @throws RuntimeException
     */
    public static String ZipToBase64(List<File> arcFiles) throws RuntimeException{
        log.info("开始压缩文件 [{}]", arcFiles);
        // 获取压缩文件的时间
        long  start = System.currentTimeMillis();
        // base64字符串
        String base64toZip = "";
        // zip输出流
        ZipOutputStream zos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            // 将字节流转换成Zip输出流
            zos = new ZipOutputStream(baos);
            for(File srcFile : arcFiles){
                byte[] buf = new byte[BUFFER_SIZE];
                log.info("压缩的文件名称 [{}]", srcFile.getName() + "压缩的文件大小    [{}]", srcFile.length());
                zos.putNextEntry(new ZipEntry(srcFile.getName())); // 创建一个zip项目
                int len;
                // 读取文件，并写入到zip文件夹中
                FileInputStream in = new FileInputStream(srcFile);
                while((len=in.read(buf))!=-1){
                   zos.write(buf, 0, len);
                }
                zos.closeEntry(); // 关闭zip项目
                in.close();
                long end = System.currentTimeMillis();
                log.info("压缩完成，耗时: [{}] ms",(end-start));
            }
        }catch(Exception e){
            throw new RuntimeException("zip error from ZipToBase64", e);
        }finally{
            if (zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        byte[] refereFileBase64Bytes = Base64.getEncoder().encode(baos.toByteArray());
        try {
            base64toZip = new String(refereFileBase64Bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("压缩流出现异常", e);
        }
        return "data:application/zip;base64," + base64toZip;
    }

    public static void main(String[] args){
        // 文件压缩
        List<File> fileList = new ArrayList<File>();
        String zipPath = "新文件夹";
        String[] filePath = new String[3];
        fileList.add(new File(filePath[0]));
        fileList.add(new File(filePath[1]));
        fileList.add(new File(filePath[2]));
        String base64 = ZipToBase64(fileList);
        log.info("文件Base64加密为" + base64);
        Base64File(base64, zipPath, null);
    }

}
