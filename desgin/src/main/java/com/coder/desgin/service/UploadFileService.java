package com.coder.desgin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coder.desgin.entity.DetectorRect;
import com.coder.desgin.entity.ImgDetectorResult;
import com.coder.desgin.entity.UploadFile;
import com.coder.desgin.util.HttpUtil;
import com.coder.desgin.util.ImageUtil;
import com.coder.desgin.util.ZipUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @Author coder
 * @Date 2022/11/3 15:28
 * @Description
 */
@Service
@Data
@NoArgsConstructor
@Slf4j
public class UploadFileService {

    @Autowired
    private HttpUtil httpUtil;

    /**
     * 检测压缩文件zip
     * @param file 压缩文件
     * @param request request请求，获取项目地址
     * @return 返回检测文档的地址
     */
    public String detectZip(UploadFile file, HttpServletRequest request) {
        // zipPath 解压文件夹的路径
        String zipPath = ZipUtil.Base64File(file.getBase64(), file.getName(), request);
        // 打包参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("path", zipPath);
        // url 使用默认参数
        JSONObject jsonObject = httpUtil.sendPost(null, params);
        log.info(jsonObject.toString());
        List<ImgDetectorResult> analysisResult = getAnalysisResult(jsonObject);
        log.info("Analysis result: path".concat(zipPath).concat(";  detections:").concat(analysisResult.toString()));
        return mkResultText(analysisResult, zipPath.substring(0, zipPath.lastIndexOf('\\')));
    }

    /**
     * 检测图片文件
     * @param file 压缩文件
     * @param request request请求，获取项目地址
     * @return 返回检测框的结果
     */
    public ImgDetectorResult detectImg(UploadFile file, HttpServletRequest request) {
        // filePath 解压的文件地址
        String filePath = ImageUtil.generateImage(file.getName(), file.getBase64(), request);
        log.info("解压文件地址为:".concat(filePath));
        // 打包参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("path", filePath);
        // url使用默认参数
        JSONObject jsonObject = httpUtil.sendPost(null, params);
        log.info(jsonObject.toString());
        List<ImgDetectorResult> analysisResult = getAnalysisResult(jsonObject);
        log.info("Analysis result: path".concat(filePath).concat(";  detections:").concat(analysisResult.toString()));
        return analysisResult.get(0);
    }

    /**
     * 负责解析flask传来的字符串
     * @param jsonMap
     * @return
     */
    public List<ImgDetectorResult> getAnalysisResult(JSONObject jsonMap){
        List<ImgDetectorResult> results = new ArrayList<ImgDetectorResult>();
        JSONObject imgs= (JSONObject) jsonMap.get("anchors");
        for(Map.Entry<String, Object>entry: imgs.entrySet()){
            ImgDetectorResult result = new ImgDetectorResult();
            result.setImageName(entry.getKey());
            Map<String, JSONArray> detections = (Map<String, JSONArray>) entry.getValue();
            for(Map.Entry<String, JSONArray> detection: detections.entrySet()){
                JSONArray array = detection.getValue();
                DetectorRect rect = new DetectorRect();
                rect.setType(array.getString(4));
                rect.setConfidence(array.getDouble(5));
                rect.setX1(array.getDouble(0));
                rect.setY1(array.getDouble(1));
                rect.setX2(array.getDouble(2));
                rect.setY2(array.getDouble(3));
                result.getRects().add(rect);
            }
            results.add(result);
        }
        return results;
    }

    /**
     * todo 目前只实现检测结果的文本 将检测结果转换成检测文件 or 返回图片的存储位置
     * @param imgs image的检测结果
     * @return
     */
    public String mkResultText(List<ImgDetectorResult> imgs, String dirPath){
        String savePath = dirPath + "\\detect.txt";
        Writer writer;
        LinkedList<String> results = new LinkedList<>();
        try {
            writer = new FileWriter(savePath);
            for(ImgDetectorResult img: imgs){
                for(DetectorRect rect: img.getRects()){
                    results.add(img.getImageName() + ":    " + rect.toString() + "\n");
                }
            }
            results.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            for(String line: results){
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savePath;
    }
}
