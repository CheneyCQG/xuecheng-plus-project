package com.xuecheng.media.service.jobhandler;

import com.xuecheng.exception.GlobalException;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.service.MediaProcessHistoryService;
import com.xuecheng.media.service.MediaProcessService;
import com.xuecheng.utils.Mp4VideoUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class HandlerVideoJob {
    @Autowired
    private MediaProcessService mediaProcessService;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MediaFileService mediaFileService;
    @Autowired
    private MediaProcessHistoryService mediaProcessHistoryService;
    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //1获取需要处理的视频任务
        List<MediaProcess> processList = mediaProcessService.findShardingMediaProcessList(shardIndex,shardTotal);
        if (processList == null || processList.size() == 0){
//            Log.debug("所有视频处理完毕");
            log.debug("所有视频处理完毕");
            System.out.println("所有视频处理完毕");
            return;
        }
        //2遍历任务，枷锁一个处理一个
        int i = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(i);
        for (MediaProcess mediaProcess : processList) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    //1获取分布式锁
                    boolean lock = mediaProcessService.getLock(mediaProcess.getId());
                    if (!lock)
                        return;
                    //2处理
                    //ffmpeg的路径
                    String ffmpeg_path = "D:\\ffmpeg\\ffmpeg.exe";//ffmpeg的安装位置
                    //源avi视频的路径
                    File tempFile = null;
                    try {
                        tempFile = File.createTempFile("video", "tranfer");
                        tempFile = new File(tempFile.getParent()+"/"+UUID.randomUUID().toString());
                        minioClient.downloadObject(
                                DownloadObjectArgs.builder()
                                        .bucket(mediaProcess.getBucket())
                                        .object(mediaProcess.getFilePath())
                                        .filename(tempFile.getAbsolutePath())
                                        .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                        handlerVideoError(mediaProcess,"视频下载失败");
                        GlobalException.cast("视频下载失败");
                    }
                    String video_path = tempFile.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = mediaProcess.getFileId()+".mp4";
                    //转换后mp4文件的路径
                    String mp4_path = tempFile.getParent()+"\\"+mp4_name;
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4_path);
                    //开始视频转换，成功将返回success
                    String s = videoUtil.generateMp4();
                    if (!s.equals("success")){
                        handlerVideoError(mediaProcess,"视频转码失败");

                        GlobalException.cast("视频转码失败");
                    }
                    //上传视频
                    String filePath = mediaProcess.getFilePath();
                    filePath = filePath.substring(0,filePath.lastIndexOf("."))+".mp4";
                    try {
                        ObjectWriteResponse response = minioClient.uploadObject(
                                UploadObjectArgs.builder()
                                        .bucket(mediaProcess.getBucket())
                                        .object(filePath)
                                        .filename(mp4_path)
                                        .contentType("video/mp4")
                                        .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                        handlerVideoError(mediaProcess,"视频上传失败");

                        GlobalException.cast("视频上传失败");
                    }
                    //3修改媒资表url
                    MediaFiles mediaFiles = mediaFileService.getById(mediaProcess.getFileId());
                    if (mediaFiles == null) {
                        handlerVideoError(mediaProcess,"找不到目标视频");
                        GlobalException.cast("找不到目标视频");
                        return;
                    }
                    mediaFiles.setUrl("/"+mediaProcess.getBucket()+"/"+filePath);
                    mediaFiles.setFilePath(filePath);
                    mediaFiles.setFileSize(new File(mp4_path).length());
                    mediaFileService.updateById(mediaFiles);
                    //4修改待处理任务表
                    mediaProcess.setStatus("2");
                    mediaProcess.setFilePath(filePath);
                    mediaProcess.setFinishDate(LocalDateTime.now());
                    mediaProcess.setUrl("/"+mediaProcess.getBucket()+"/"+filePath);
                    mediaProcessService.updateById(mediaProcess);
                    //5插入到处理任务历史表，并删除待处理表
                    MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
                    BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
                    mediaProcessHistoryService.save(mediaProcessHistory);

                    mediaProcessService.removeById(mediaProcess.getId());
                }
            });
        }

    }
    public void handlerVideoError(MediaProcess mediaProcess,String errmsg){
        mediaProcess.setStatus("3");
        mediaProcess.setFailCount(mediaProcess.getFailCount()+1);
        mediaProcess.setErrormsg(errmsg);
        mediaProcessService.updateById(mediaProcess);
    }
}
