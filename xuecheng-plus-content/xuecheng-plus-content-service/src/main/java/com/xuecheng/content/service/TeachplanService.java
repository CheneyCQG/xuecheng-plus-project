package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
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

    List<TeachplanDto> teachplan(String courseId);
}
