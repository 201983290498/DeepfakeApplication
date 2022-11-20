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
 *
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
     *
     * @param base64   文件的base64字符串
     * @param filename 解压的文件名
     * @param request  http的请求，获取项目在本地的地址
     * @return 返回压缩文件的地址
     */
    public static String Base64File(String base64, String filename, HttpServletRequest request) {
        filename = filename.substring(0, filename.lastIndexOf('.'));

        String contentPath = request.getSession().getServletContext().getRealPath("/");
        File dir = new File(contentPath + detectDir + UUID.randomUUID().toString().substring(0, 6));
        // 查看检测文件夹/context/detectFile是否存在
        if (!dir.getParentFile().exists()) {
            dir.getParentFile().mkdir();
        }
        // 创建检测文件夹/context/detectFile/path
        if (!dir.exists()) {
            dir.mkdir();
        }

        // 解压文件夹
        String filePath = dir.getAbsolutePath().concat("\\").concat(filename);
        dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            // 先将文件写出来在解压

            // 将base64去掉文件头
            base64 = base64.substring(base64.indexOf(',') + 1);
            byte[] bytes = Base64.getDecoder().decode(base64);
            ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
            ZipInputStream zipInput = new ZipInputStream(byteArray);
            ZipEntry entry = zipInput.getNextEntry();
            File fout = null;
            while (entry != null) {
                if (!entry.isDirectory()) {
                    // todo 这边需要断点，查看这边函数的具体意义, Zip可以发现文件夹下的文件，一般用户可能直接对文件夹压缩，导致解压第一个文件不是图片，所以需要将文件夹的前缀删除
                    log.info("文件名称： [{}]", entry.getName());
                    String fileName = entry.getName();
                    if (fileName.lastIndexOf("/") != -1) {
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    }
                    fout = new File(dir, fileName);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fout));
                    int offo = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((offo = zipInput.read(buffer)) != -1) {
                        bos.write(buffer, 0, offo);
                    }
                    bos.close();
                    // 获取下一个文件
                }
                entry = zipInput.getNextEntry();
            }
            zipInput.close();
            byteArray.close();
        } catch (Exception e) {
            throw new RuntimeException("解压流出现异常", e);
        }
        return filePath;
    }

    /**
     * todo 待检测 该函数还未检测
     *
     * @param arcFiles
     * @return
     * @throws RuntimeException
     */
    public static String ZipToBase64(List<File> arcFiles) throws RuntimeException {
        log.info("开始压缩文件 [{}]", arcFiles);
        // 获取压缩文件的时间
        long start = System.currentTimeMillis();
        // base64字符串
        String base64toZip = "";
        // zip输出流
        ZipOutputStream zos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 将字节流转换成Zip输出流
            zos = new ZipOutputStream(baos);
            for (File srcFile : arcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                log.info("压缩的文件名称 [{}]", srcFile.getName() + "压缩的文件大小    [{}]", srcFile.length());
                zos.putNextEntry(new ZipEntry(srcFile.getName())); // 创建一个zip项目
                int len;
                // 读取文件，并写入到zip文件夹中
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry(); // 关闭zip项目
                in.close();
                long end = System.currentTimeMillis();
                log.info("压缩完成，耗时: [{}] ms", (end - start));
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipToBase64", e);
        } finally {
            if (zos != null) {
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

    /**
     * 压缩成zip文件
     *
     * @param srcDir           压缩文件夹的路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void folderToZip(String srcDir, OutputStream out, boolean KeepDirStructure) throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 递归的方式压缩文件夹
     *
     * @param sourceFile       源文件
     * @param zos              输出流
     * @param name             压缩后的文件名称
     * @param KeepDirStructure 是否保留原来的目录结构,
     * @throws Exception 抛出异常
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure)
            throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // 把文件放到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 保留原来的文件结构,并对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
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
