package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

/**
 * <p>
 * 课程发布 服务类
 * </p>
 *
 * @author Cheney
 * @since 2023-08-21
 */
public interface CoursePublishService extends IService<CoursePublish> {
    /**
     * 根据课程id获取所有课程信息
     * @param courseId
     * @return
     */
    CoursePreviewDto findAllCourseInfoById(String courseId);
}
