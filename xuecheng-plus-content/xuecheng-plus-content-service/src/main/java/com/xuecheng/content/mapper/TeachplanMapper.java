package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author Cheney
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    List<TeachplanDto> teachplan(String courseId);

    Integer selectMaxOrderby(@Param("courseid") Long courseId, @Param("grade") Integer grade, @Param("parentid") Long parentid);
}
