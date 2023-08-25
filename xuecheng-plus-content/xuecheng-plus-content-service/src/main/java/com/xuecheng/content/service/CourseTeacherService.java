package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author Cheney
 * @since 2023-08-21
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    List<CourseTeacher> tolist(String courseid);

    CourseTeacher addAndUpdate(CourseTeacher courseTeacher);

    void delete(String courseid, String id);
}
