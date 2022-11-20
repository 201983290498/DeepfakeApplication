package com.coder.desgin.service;

import com.alibaba.fastjson.JSONObject;
import com.coder.desgin.entity.ImgDetectorResult;
import com.coder.desgin.entity.NormalDetectionFile;
import com.coder.desgin.entity.UploadFile;
import com.coder.desgin.util.HttpUtil;
import com.coder.desgin.util.ImageUtil;
import com.coder.desgin.util.ZipUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author Pengfei Yue
 * @ClassName NormalDetectionService
 * @date 2022/11/8
 * @description 普通检测
 */
@Service
@Data
@NoArgsConstructor
@Slf4j
@PropertySource(value = "classpath:mySetting.properties")
public class NormalDetectionService {
    @Autowired
    private HttpUtil httpUtil;
    @Value("${flask.copymove.url}")
    private String copymoveDetectUrl;
    @Value("${flask.splicing.url}")
    private String splicingDetectUrl;

    public String detectZip(NormalDetectionFile file, HttpServletRequest request) {
        // zipPath 解压文件夹的路径
        String zipPath = ZipUtil.Base64File(file.getBase64(), file.getName(), request);
        // 打包参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        zipPath = zipPath.replace("\\", "/");
        params.add("path", zipPath);
        String detectionType = file.getDetectType();
        String url = null;
        if (detectionType.equals("copymove")) {
            url = getCopymoveDetectUrl();
        } else if (detectionType.equals("splicing")) {
            url = getSplicingDetectUrl();
        } else if (detectionType.equals("general")) {

        }
        // url 使用默认参数
        JSONObject jsonObject = httpUtil.sendPost(url, params);
        log.info(jsonObject.toString());

        // 返回存储检测定位图片的文件夹
        String result_detected_path = (String) jsonObject.get("result");
        result_detected_path = result_detected_path.replace("/", "\\");
        // 将该文件夹的压缩路径
        String result_zip_path = result_detected_path + ".zip";
        try {
            FileOutputStream fos1 = null;
            fos1 = new FileOutputStream(new File(result_zip_path));
            // 压缩文件夹
            ZipUtil.folderToZip(result_detected_path, fos1, true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result_zip_path;
    }

    public String detectImg(NormalDetectionFile file, HttpServletRequest request) {
        // filePath 解压的文件地址
        String filePath = ImageUtil.generateImage(file.getName(), file.getBase64(), request);
        log.info("解压文件地址为:".concat(filePath));
        // 打包参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        filePath = filePath.replace("\\", "/");
        params.add("path", filePath);
        String detectionType = file.getDetectType();
        String url = null;
        if (detectionType.equals("copymove")) {
            url = getCopymoveDetectUrl();
        } else if (detectionType.equals("splicing")) {
            url = getSplicingDetectUrl();
        } else if (detectionType.equals("general")) {

        }
        // url使用默认参数
        JSONObject jsonObject = httpUtil.sendPost(url, params);
        log.info(jsonObject.toString());

        String result_img_path = (String) jsonObject.get("result");
        result_img_path = result_img_path.replace("/", "\\");

        return ImageUtil.GenImageStr(result_img_path);
    }
}
