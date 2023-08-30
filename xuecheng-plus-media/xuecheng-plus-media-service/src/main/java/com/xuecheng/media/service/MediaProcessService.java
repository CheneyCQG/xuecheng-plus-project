package com.xuecheng.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author itcast
 * @since 2023-08-30
 */
public interface MediaProcessService extends IService<MediaProcess> {
    //g根据执行器的分片序号和总数查询出待处理任务集合
    public List<MediaProcess> findShardingMediaProcessList(int index,int total);

    //加锁，基于数据库的乐观锁，使用status作为版本号
    public boolean getLock(Long id);
}
