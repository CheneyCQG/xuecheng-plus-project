package com.xuecheng.controller;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class CoursePublishController {
    @Autowired
    private CoursePublishService coursePublishService;

    @RequestMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") String courseId){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("course_template");

        CoursePreviewDto coursePreviewDto = coursePublishService.findAllCourseInfoById(courseId);
        modelAndView.addObject("model",coursePreviewDto);
        return modelAndView;
    }
}