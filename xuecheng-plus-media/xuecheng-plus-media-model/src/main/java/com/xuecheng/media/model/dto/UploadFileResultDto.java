package com.xuecheng.media.model.dto;

import com.xuecheng.media.model.po.MediaFiles;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;


@Data
@NoArgsConstructor
public class UploadFileResultDto extends MediaFiles {
    public UploadFileResultDto(MediaFiles mediaFiles1) {
        BeanUtils.copyProperties(mediaFiles1,this);
    }
}
