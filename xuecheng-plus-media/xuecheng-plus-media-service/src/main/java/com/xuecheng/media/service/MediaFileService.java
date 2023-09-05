package com.xuecheng.media.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.model.dto.PageParams;
import com.xuecheng.model.dto.PageResult;
import com.xuecheng.model.dto.RestResponse;

import java.io.File;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService extends IService<MediaFiles> {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传图片
     * @param uploadFileParamsDto 图片信息
     * @param tempFile 图片文件
     * @return 插入到媒体库后的记录信息
     */
    UploadFileResultDto uploadFile(UploadFileParamsDto uploadFileParamsDto, File tempFile,String objectName);

    /**
     * 向数据库插入文件信息
     * @param mediaFiles
     * @param uploadFileParamsDto
     * @param
     */
    boolean addFileToDB(MediaFiles mediaFiles, UploadFileParamsDto uploadFileParamsDto, String fileMD5, String objectName,String fileBucket);

    /**
     * 检查文件是否存在
     * @param fileMd5
     * @return
     */
    RestResponse<Boolean> checkfile(String fileMd5);

    /**
     * 检查分块是否存在
     * @param fileMd5
     * @param chunk
     * @return
     */
    RestResponse<Boolean> checkchunk(String fileMd5, int chunk);

   /**
    * 上传分块
    * @param chunkFile
    * @param chunk
    * @param fileMd5
    * @return
    */
    RestResponse uploadchunkFile(File chunkFile, int chunk, String fileMd5);

    RestResponse mergechinks(String fileMd5, String fileName1, int chunkTotal);

    void addMediaFilesAndMediaProcess(File downloadFile, String fileName, String fileMD5, String objPath, String subfix);
}
