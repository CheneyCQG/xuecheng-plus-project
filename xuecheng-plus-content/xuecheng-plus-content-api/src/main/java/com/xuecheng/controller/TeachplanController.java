package com.xuecheng.controller;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 查询课程计划
     * @param courseId
     * @return
     */
    @RequestMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> treenodes(@PathVariable("courseId")String courseId){
        return teachplanService.teachplanTreeNodes(courseId);
    }

    /**
     * 课程计划新增和修改
     * @param saveTeachplanDto
     */
    @RequestMapping("/teachplan")
    public void teachplan(@RequestBody SaveTeachplanDto saveTeachplanDto){
        teachplanService.teachplan(saveTeachplanDto);
    }

    /**
     * 删除课程计划
     * @param id
     */
    @DeleteMapping("/teachplan/{id}")
    public void teachplanDeleteById(@PathVariable("id")String id){

    }
}
