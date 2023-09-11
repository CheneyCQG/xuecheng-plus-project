package com.xuecheng.content.feignclients;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
@Component
public class MediaClientFallback implements MediaClient{
    @Override
    public void coursefile(MultipartFile multipartFile, String objectName) {
        System.out.println("走文件上传的降级逻辑");
    }
}
