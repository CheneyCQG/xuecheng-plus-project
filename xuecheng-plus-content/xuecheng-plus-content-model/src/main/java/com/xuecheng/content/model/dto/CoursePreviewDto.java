package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseTeacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
/***
 * 课程预览模型对象,包含基本信息对象,营销信息对象,课程计划对象,师资信息对象
 */
public class CoursePreviewDto {

    //private CourseBase courseBase;
    //private CourseMarket courseMarket;
    //基本信息对象,营销信息对象,我们已经封装到CourseBaseInfoDto中
    private CourseBaseInfoDto courseBase;
    private List<TeachplanDto> teachplans;
    private CourseTeacher courseTeacher;//师资信息暂时不加...
}