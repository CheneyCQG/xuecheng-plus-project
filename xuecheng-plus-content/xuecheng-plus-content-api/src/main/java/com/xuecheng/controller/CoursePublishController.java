package com.xuecheng.controller;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class CoursePublishController {
    @Autowired
    private CoursePublishService coursePublishService;

    /**
     * 预览
     * @param courseId
     * @return
     */
    @RequestMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") String courseId){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("course_template");

        CoursePreviewDto coursePreviewDto = coursePublishService.findAllCourseInfoById(courseId);
        modelAndView.addObject("model",coursePreviewDto);
        return modelAndView;
    }

    /**
     * 提交审核接口
     * @param courseId
     */
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long id = 1232141425L;
        coursePublishService.commitAudit(id,courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long id = 1232141425L;
        coursePublishService.coursepublish(id,courseId);
    }
}