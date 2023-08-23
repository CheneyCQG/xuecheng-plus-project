package com.xuecheng.controller;

import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 课程计划 前端控制器
 * </p>
 *
 * @author Cheney
 */
@Slf4j
@RestController
public class TeachplanController {

    @Autowired
    private TeachplanService  teachplanService;

    @RequestMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> teachplan(@PathVariable("courseId")String courseId){
        return teachplanService.teachplan(courseId);
    }
}
