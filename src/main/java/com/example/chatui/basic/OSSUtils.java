package com.example.chatui.basic;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.chatui.aboutMessage.FileInfo;
import javafx.scene.image.Image;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class OSSUtils {

    private static final String AccessKeyID="your_access_key";
    private static final String AccessKeySecret="your_access_key_secret";
    private static final String endpoint="your_endpoint";
    private static final String bucketName="your_bucketName";
    private static final String emojiprefix="emojiPictures/";

    private static final Date expiration = new Date(System.currentTimeMillis() + 300 * 1000);  //过期时间为5分钟


    public static Map<String,Image> getEmojis(){
        Map<String, Image> emojisUrls=new HashMap<>();
        OSS ossClient = new OSSClientBuilder().build(endpoint, AccessKeyID, AccessKeySecret);
        // 列取文件
        List<OSSObjectSummary> objectSummaries = ossClient.listObjects(bucketName,emojiprefix).getObjectSummaries();
        // 构造文件URL
        for (OSSObjectSummary objectSummary : objectSummaries) {
            String key=objectSummary.getKey();
            String fileUrl = "https://" + bucketName + "." +  endpoint + "/" + key;
            emojisUrls.put(key,new Image(fileUrl));
        }
        ossClient.shutdown();

        return emojisUrls;
    }

    public static String getEmojiUrl(String emojiId){
//        String url= "https://" + bucketName + "." +  endpoint + "/" +emojiprefix+ emojiId+".png";
        String url="file:emojiPictures/"+emojiId+".png";
        return url;
    }

    public static String GenerateSignedURL(String bucketName,String objectName){
        OSS ossClient = new OSSClientBuilder().build(endpoint, AccessKeyID, AccessKeySecret);
        return ossClient.generatePresignedUrl(bucketName, objectName, expiration).toString();
    }

    public static FileInfo getFileInfo(String objectName){
        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, AccessKeyID, AccessKeySecret);
        ObjectMetadata metadata = null;
        try {
            // 获取文件元数据
             metadata = ossClient.getObjectMetadata(bucketName, objectName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭OSS客户端
            ossClient.shutdown();
        }
        // 获取原始文件名
        String originalFileName = metadata.getUserMetadata().get("originalFileName");
        // 获取文件大小
        long fileSize = metadata.getContentLength();
        return new FileInfo(originalFileName,fileSize);
    }


}
