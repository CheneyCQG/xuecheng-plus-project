package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {
    @Select("select * from media_process t where t.id % #{total} = #{index} and (t.status = '1' or t.status = '3') and t.fail_count < 3 limit 2")
    List<MediaProcess> findShardingMediaProcessList(@Param("index") int index, @Param("total") int total);
    @Update("update media_process m set m.status='4' where (m.status='1' or m.status='3') and m.fail_count<3 and m.id = #{id}")
    int getLock(Long id);
}
