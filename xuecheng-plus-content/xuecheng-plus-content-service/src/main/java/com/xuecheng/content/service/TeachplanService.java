package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author Cheney
 * @since 2023-08-21
 */
public interface TeachplanService extends IService<Teachplan> {
    /**
     * 查询课程计划
     * @param courseId
     * @return
     */
    List<TeachplanDto> teachplanTreeNodes(String courseId);

    /**
     * 新增或者修改课程计划
     * @param saveTeachplanDto
     * @return
     */
    void teachplan(SaveTeachplanDto saveTeachplanDto);

    void teachplanDeleteById(String id);

    void move(String moveType, String id);
}
