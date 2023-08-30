package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class MediaProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaProcessService {
    @Autowired
    private MediaProcessMapper mediaProcessMapper;
    @Override
    public List<MediaProcess> findShardingMediaProcessList(int index, int total) {
        return mediaProcessMapper.findShardingMediaProcessList(index,total);
    }

    /**
     * 用于获取分布式锁
     * @param id
     * @return
     */
    @Override
    public boolean getLock(Long id) {
        int rows = mediaProcessMapper.getLock(id);
        return rows > 0;
    }
}
