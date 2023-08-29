package com.xuecheng;

import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VedioTest {
    @Test
    public void chunk() throws Exception {
        //1将大文件分开
        RandomAccessFile sourceFile = new RandomAccessFile("C:\\Users\\22091\\Videos\\1.mp4","r");
        //块的大小
        int size = 1024*1024*5;
        //块的数量
        int blocks = (int)Math.ceil(sourceFile.length()*1.0/size);
        //临时的字节数组，用于读取文件用的
        byte[] bytes = new byte[1024 * 1024];
        //实际读取的字节数
        int len = 0;
        for (int i = 0; i < blocks; i++) {
            RandomAccessFile file = new RandomAccessFile("C:\\Users\\22091\\Videos\\data\\"+i, "rw");
            while ((len=sourceFile.read(bytes))!=-1){
                file.write(bytes,0,len);
                if (file.length() == size)
                    break;
            }
            file.close();
        }
        sourceFile.close();
    }
    @Test
    public void merge() throws Exception {
        //1要合并的视频文件夹
        File fileDir = new File("C:\\Users\\22091\\Videos\\data");
        String[] files = fileDir.list();
        List<String> fileList = Arrays.asList(files);
        Collections.sort(fileList,(o1,o2)->{return Integer.parseInt(o1)-Integer.parseInt(o2);});


        RandomAccessFile objectFile = new RandomAccessFile("C:\\Users\\22091\\Videos\\merge.mp4", "rw");
        byte[] bytes = new byte[1024];
        int len = 0;
        for (String file : fileList) {
            RandomAccessFile rFile = new RandomAccessFile("C:\\Users\\22091\\Videos\\data\\" + file, "r");
            while ((len = rFile.read(bytes))  != -1){
                objectFile.write(bytes,0,len);
            }
            rFile.close();
        }
        objectFile.close();
        System.out.println(DigestUtils.md5Hex(new FileInputStream(new File("C:\\Users\\22091\\Videos\\1.mp4"))));
        System.out.println(DigestUtils.md5Hex(new FileInputStream(new File("C:\\Users\\22091\\Videos\\merge.mp4"))));
    }
    @Test
    public void mergeChunk() throws Exception {
        MinioClient minioClient = MinioClient.builder().endpoint("http://192.168.101.65:9000")
                .credentials("minioadmin","minioadmin").build();
        String fileMD5 = DigestUtils.md5Hex(new FileInputStream("C:\\Users\\22091\\Videos\\1.mp4"));
        //块路径
        String chunkPath = fileMD5.charAt(0)+"/"+fileMD5.charAt(1)+"/"+fileMD5+"/chunk/";
        //上传所有的块
        File fileDir = new File("C:\\Users\\22091\\Videos\\data");
        String[] files = fileDir.list();
        for (String file : files) {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("test")
                            .object(chunkPath+file)
                            .filename("C:\\Users\\22091\\Videos\\data\\"+file)
                            .build());
            System.out.println("上传"+file+"成功");
        }

    }
    @Test
    public void mergerTrunk()throws Exception{
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://192.168.101.65:9000")
                        .credentials("minioadmin","minioadmin")
                        .build();
        String fileMD5 = DigestUtils.md5Hex(new FileInputStream("C:\\Users\\22091\\Videos\\1.mp4"));
        //快路径   {0}/{1}/{fileMD5}/chunk/
        String chunkPath = fileMD5.charAt(0)+"/"+fileMD5.charAt(1)+"/"+fileMD5+"/chunk/";
        String objPath = fileMD5.charAt(0)+"/"+fileMD5.charAt(1)+"/"+fileMD5+"/";

        //所有合并源的集合
        ArrayList<ComposeSource> composeSourceArrayList = new ArrayList<>();
        //添加每个块的源
        for (int i = 0; i < 31; i++) {
            ComposeSource composeSource = ComposeSource.builder().bucket("test").object(chunkPath + i).build();
            composeSourceArrayList.add(composeSource);
        }
        //合并对象参数
        ComposeObjectArgs composeObjectArgs =
                ComposeObjectArgs.builder()
                        .bucket("test")
                        .sources(composeSourceArrayList)
                        .object(objPath+fileMD5+".mp4")
                        .build();
        //调用minio的合并功能
        minioClient.composeObject(composeObjectArgs);
        System.out.println("OK");
    }
}
