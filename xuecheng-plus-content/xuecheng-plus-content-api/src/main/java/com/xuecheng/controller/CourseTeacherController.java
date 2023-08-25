package com.xuecheng.controller;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 前端控制器
 * </p>
 *
 * @author Cheney
 */
@Slf4j
@RestController
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService  courseTeacherService;
    @GetMapping("/courseTeacher/list/{courseid}")
    public List<CourseTeacher> list(@PathVariable("courseid")String courseid){
        return courseTeacherService.tolist(courseid);
    }
    @PostMapping("/courseTeacher")
    public CourseTeacher update(@RequestBody CourseTeacher courseTeacher){
        return courseTeacherService.addAndUpdate(courseTeacher);
    }
    @DeleteMapping("/courseTeacher/course/{courseid}/{id}")
    public void delete(@PathVariable("courseid")String courseid,@PathVariable("id")String id){
        courseTeacherService.delete(courseid,id);
    }
}
