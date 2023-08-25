package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author Cheney
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> tolist(String courseid) {
        return courseTeacherMapper.selectList(new QueryWrapper<CourseTeacher>().eq("course_id",courseid));
    }

    @Override
    @Transactional
    public CourseTeacher addAndUpdate(CourseTeacher courseTeacher) {
        if (courseTeacher.getId() == null){
            int insert = courseTeacherMapper.insert(courseTeacher);
        }else {
            int i = courseTeacherMapper.updateById(courseTeacher);
        }
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }

    @Override
    public void delete(String courseid, String id) {
        courseTeacherMapper.delete(new QueryWrapper<CourseTeacher>().eq("id",id).eq("course_id",courseid));
    }

}
