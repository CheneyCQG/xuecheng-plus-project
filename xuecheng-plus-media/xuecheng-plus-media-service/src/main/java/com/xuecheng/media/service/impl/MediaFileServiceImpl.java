package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.media.service.MediaProcessService;
import com.xuecheng.model.dto.PageParams;
import com.xuecheng.model.dto.PageResult;
import com.xuecheng.model.dto.RestResponse;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
public class MediaFileServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MediaFileService mediaFileService;
    @Autowired
    private MediaProcessService mediaProcessService;

    @Value("${minio.bucket.files}")
    private String fileBucket;

    @Value("${minio.bucket.videofiles}")
    private String videoBucket;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(UploadFileParamsDto uploadFileParamsDto, File tempFile,String objectName) {

        String fileMD5 = getFileMD5(tempFile); //7f8as79fds9f89dsg9d9f7g87fds
        //0.查询数据库有没有该文件
        MediaFiles existFile = mediaFilesMapper.selectById(fileMD5);
        if (existFile != null) {
            //接续优化，如果数据库有该文件，但是minio没有该文件，上传到minio
            return new UploadFileResultDto(existFile);
        }

        String extendName = uploadFileParamsDto.getFilename().substring(uploadFileParamsDto.getFilename().lastIndexOf("."));//.png
        ContentInfo contentInfo  = ContentInfoUtil.findExtensionMatch(extendName); //.png -> image/png
        //1.把文件上传到MINIO
        if (StringUtils.isEmpty(objectName)) {
            String filePath = getFilePath(); //年/月/日/
            objectName = filePath+fileMD5+extendName; //
        }

        addFileToMinIO(fileBucket,objectName,tempFile.getAbsolutePath(),contentInfo.getMimeType());
        //2.把文件信息保持到mediaFiles数据表中
        MediaFiles mediaFiles = new MediaFiles();
        //this.addFileToDB(mediaFiles,uploadFileParamsDto,fileMD5,objectName);
        mediaFileService.addFileToDB(mediaFiles,uploadFileParamsDto,fileMD5,objectName,fileBucket);

        //3.返回该文件的详细信息
        return new UploadFileResultDto(mediaFiles);
    }

    /**
     * 插入数据到mediafiles表
     *
     * @param mediaFiles
     * @param uploadFileParamsDto
     * @param
     * @return
     */
    @Transactional
    public boolean addFileToDB(MediaFiles mediaFiles, UploadFileParamsDto uploadFileParamsDto, String fileMD5, String objectName,String fileBucket){
        BeanUtils.copyProperties(uploadFileParamsDto,mediaFiles);
        mediaFiles.setAuditStatus("002002");
        mediaFiles.setBucket(fileBucket);
        mediaFiles.setChangeDate(LocalDateTime.now());
        mediaFiles.setCompanyId(123456L);
        mediaFiles.setCompanyName("西方瑞通");
        mediaFiles.setCreateDate(LocalDateTime.now());
        mediaFiles.setId(fileMD5);
        mediaFiles.setFileId(fileMD5);
        mediaFiles.setFilePath(objectName);
        mediaFiles.setStatus("1");
        mediaFiles.setUrl("/"+fileBucket+"/"+objectName);
        int rows = mediaFilesMapper.insert(mediaFiles);

        if (rows <= 0) {
//       deleteFileFromMinio();
            return false;
        }else{
            return true;
        }

        //插入待处理视频表
    }


    /**
     * 获取文件md5
     */
    public String getFileMD5(File file) {
        try {
            String md5 = DigestUtils.md5Hex(new FileInputStream(file));
            return md5;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前年月日
     */
    public String getFilePath() {
        return new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
    }

    /**
     * 上传文件到MINIO
     */
    private boolean addFileToMinIO(String bucket, String object, String filepath, String contentType) {
        try {
            UploadObjectArgs uploadObjectArgs =
                    UploadObjectArgs
                            .builder()
                            .bucket(bucket)
                            .object(object)
                            .filename(filepath)
                            .contentType(contentType)
                            .build();
            ObjectWriteResponse response = minioClient.uploadObject(uploadObjectArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public RestResponse<Boolean> checkfile(String fileMd5) {
        //1从数据库查
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            RestResponse.success(false);
        }
        //2从minio查
        //实际业务逻辑应该更复杂
        //1应该两个都存在我们才告诉前端，文件已经存在
        //2如果有一个不存在， mediafiles表存在，单minio不存在，告诉前端不存在并且删除存在的一个数据
        //                 mediafiles表不存在，单minio存在，告诉前端不存在并且在表中新增一条记录
//        minioClient.getObject()
        return RestResponse.success(true);
    }

    public String getObjectPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/chunk/";
    }

    @Override
    public RestResponse<Boolean> checkchunk(String fileMd5, int chunk) {
        String objectPath = getObjectPath(fileMd5);
        //只能从Minio中检查
        InputStream in = null;
        try {
            in = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(videoBucket)
                            .object(objectPath + chunk)
                            .build());
            if (in != null) {
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RestResponse.success(false);
    }

    @Override
    public RestResponse uploadchunkFile(File chunkFile, int chunk, String fileMd5) {
        String objectPath = getObjectPath(fileMd5);
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(videoBucket)
                            .object(objectPath + chunk)
                            .filename(chunkFile.getAbsolutePath())
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.validfail("分块" + chunk + "失败");
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergechinks(String fileMD5, String fileName, int chunkTotal) {

        //快路径   {0}/{1}/{fileMD5}/chunk/
        String chunkPath = fileMD5.charAt(0)+"/"+fileMD5.charAt(1)+"/"+fileMD5+"/chunk/";
        String objPath = fileMD5.charAt(0)+"/"+fileMD5.charAt(1)+"/"+fileMD5+"/";
        String subfix = fileName.substring(fileName.lastIndexOf("."));

        //所有合并源的集合
        ArrayList<ComposeSource> composeSourceArrayList = new ArrayList<>();
        //添加每个块的源
        for (int i = 0; i < chunkTotal; i++) {
            ComposeSource composeSource = ComposeSource.builder().bucket(videoBucket).object(chunkPath + i).build();
            composeSourceArrayList.add(composeSource);
        }
        //合并对象参数
        ComposeObjectArgs composeObjectArgs =
                ComposeObjectArgs.builder()
                        .bucket(videoBucket)
                        .sources(composeSourceArrayList)
                        .object(objPath+fileMD5+subfix)
                        .build();
        //调用minio的合并功能
        try {
            minioClient.composeObject(composeObjectArgs);
            //对比md5
            File download = File.createTempFile("download"+ UUID.randomUUID().toString(), subfix);
            File downloadFile = new File(download.getParent(),"download"+System.currentTimeMillis());
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(videoBucket)
                            .object(objPath+fileMD5+subfix)
                            .filename(downloadFile.getAbsolutePath())
                            .build());
            //删除分块
//            ArrayList<DeleteObject> objects = new ArrayList<DeleteObject>();
//            for (int i = 0; i < chunkTotal; i++) {
//                DeleteObject deleteObject = new DeleteObject(chunkPath+i);
//                objects.add(deleteObject);
//            }
//            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
//                    RemoveObjectsArgs
//                            .builder()
//                            .bucket(videoBucket)
//                            .objects(objects)
//                            .build());

            //插入mediafiles表和待处理任务表
            mediaFileService.addMediaFilesAndMediaProcess(downloadFile,fileName,fileMD5,objPath,subfix);

            return RestResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.validfail(e.getMessage());
        }



    }

    /**
     * 插入mediafiles表和mediaprocess表
     * @param downloadFile
     * @param fileName
     * @param fileMD5
     * @param objPath
     * @param subfix
     */
    @Transactional
    public void addMediaFilesAndMediaProcess(File downloadFile, String fileName, String fileMD5, String objPath, String subfix) {
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setUsername("jack");
        uploadFileParamsDto.setFileSize(downloadFile.length());
        uploadFileParamsDto.setTags("标签");
        uploadFileParamsDto.setRemark("备注");
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setFileType("001002");
        MediaFiles mediaFiles = new MediaFiles();
        addFileToDB(mediaFiles, uploadFileParamsDto, fileMD5, objPath + fileMD5 + subfix, videoBucket);


        //插入待处理任务表
        String mimeType = ContentInfoUtil.findExtensionMatch(fileName).getMimeType();
        if (mimeType.equals("video/x-msvideo")) {
            //是avi文件
            //插入表
            addFileToMediaProcessDB(mediaFiles);
        }
    }

    /**
     * 插入待处理任务表
     */
    public void addFileToMediaProcessDB(MediaFiles mediaFiles) {
        MediaProcess mediaProcess = new MediaProcess();
        BeanUtils.copyProperties(mediaFiles,mediaProcess);
        mediaProcess.setFailCount(0);//刚插入的处理任务，失败次数为0
        mediaProcess.setStatus("1");//1未处理2处理成功3处理失败

        mediaProcessService.save(mediaProcess);
    }
}
