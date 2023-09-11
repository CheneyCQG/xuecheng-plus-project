package com.xuecheng.content.feignclients;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MediaClientFallbackFactory implements FallbackFactory<MediaClient> {
    @Override
    public MediaClient create(Throwable throwable) {
        return new MediaClient() {
            @Override
            public void coursefile(MultipartFile multipartFile, String objectName) {
                System.out.println("走降级逻辑"+throwable.toString());
            }
        };
    }
}
