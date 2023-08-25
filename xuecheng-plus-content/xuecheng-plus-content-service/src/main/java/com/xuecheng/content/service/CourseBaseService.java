package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.AddCourseParamsDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.model.dto.PageParams;
import com.xuecheng.model.dto.PageResult;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author Cheney
 * @since 2023-08-21
 */
public interface CourseBaseService extends IService<CourseBase> {
    /**
     * 显示课程列表
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    PageResult<CourseBase> listPage(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 添加课程
     * @param addCourseParamsDto
     * @return
     */
    CourseBaseInfoDto course(AddCourseParamsDto addCourseParamsDto);

    /**
     * 更新课程的回显
     * @param id
     * @return
     */
    CourseBaseInfoDto course(String id);

    /**
     * 删除课程
     * @param id
     */
    void deleteCourseById(String id);
}
