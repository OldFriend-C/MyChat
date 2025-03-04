package com.example.chatui.basic;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObjectSummary;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class OSSUtils {

    private static String AccessKeyID="your_access_key";
    private static String AccessKeySecret="your_access_key_secret";
    private static String endpoint="your_endpoint";
    private static String bucketName="your_bucket_name";
    private static String emojiprefix="emojiPictures/";


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
}
