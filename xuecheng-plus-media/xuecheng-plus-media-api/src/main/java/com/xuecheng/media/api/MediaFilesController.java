package com.xuecheng.media.api;


import com.xuecheng.exception.GlobalException;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.model.dto.PageParams;
import com.xuecheng.model.dto.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
 @RestController
public class MediaFilesController {


  @Autowired
  MediaFileService mediaFileService;


 @ApiOperation("媒资列表查询接口")
 @PostMapping("/files")
 public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
  Long companyId = 1232141425L;
  return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);

 }

@ApiOperation(value = "文件上传")
 @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public UploadFileResultDto coursefile(@RequestPart("filedata")MultipartFile multipartFile){
     //1保存上传的文件
//    long size = multipartFile.getSize();
//    String originalFilename = multipartFile.getOriginalFilename();//1.jpg
    File tempFile = null;
    try {
        tempFile = File.createTempFile("minio", "temp");//minio.temp
        multipartFile.transferTo(tempFile);
    } catch (IOException e) {
        e.printStackTrace();
        GlobalException.cast(e.getMessage());
    }
    //2封装service需要的信息
    UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
    uploadFileParamsDto.setUsername("jack");
    uploadFileParamsDto.setFilename(multipartFile.getOriginalFilename());
    uploadFileParamsDto.setFileSize(multipartFile.getSize());
    uploadFileParamsDto.setTags("图片");
    uploadFileParamsDto.setRemark("备注");
    uploadFileParamsDto.setFileType("001001");//代表类型为图片
    //调用service层，正在上传文件到minio
    return mediaFileService.uploadFile(uploadFileParamsDto,tempFile);
 }

}
