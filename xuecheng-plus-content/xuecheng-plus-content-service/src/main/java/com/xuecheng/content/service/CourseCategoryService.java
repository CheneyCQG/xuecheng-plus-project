package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 *
 * @author Cheney
 * @since 2023-08-21
 */
public interface CourseCategoryService extends IService<CourseCategory> {

    List<CourseCategoryTreeDto> tree();
}
