package com.xuecheng;

import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

public class MinIOTest {
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
    @Test
    public void testFileUpload() throws Exception {
        ObjectWriteResponse response = minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket("test")
                        .object("2022/06/20/1.mp4")
                        .filename("D:\\微服务阶段视频\\微服务项目视频\\29 Minio分布式文件系统.mp4")
                        .build());
        System.out.println(response.etag());
    }

    @Test
    public void testFileDownload() throws Exception {
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket("test")
                        .object("2022/06/20/1.mp4")
                        .filename("D:\\微服务阶段视频\\微服务项目视频\\29 Minio分布式文件系统111.mp4")
                        .build());
    }


    @Test
    public void testFileMD5() throws Exception {
        FileInputStream fis = new FileInputStream("D:\\微服务阶段视频\\微服务项目视频\\29 Minio分布式文件系统111.mp4");
        String md5Hex = DigestUtils.md5Hex(fis);
        FileInputStream fis2 = new FileInputStream("D:\\微服务阶段视频\\微服务项目视频\\29 Minio分布式文件系统.mp4");
        String md5Hex2 = DigestUtils.md5Hex(fis2);
        System.out.println(md5Hex);
        System.out.println(md5Hex2);
    }

    @Test
    public void testFileDelete() throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket("test")
                        .object("2022/06/19/1.jpg")
                        .build()
        );
    }

    @Test
    public void testFileGet() throws Exception {
        GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("test")
//                        .object("2022/06/19/123.png")
                        .object("2022/06/20/1.mp4")
                        .build()
        );
        System.out.println(response.object());
    }

}
