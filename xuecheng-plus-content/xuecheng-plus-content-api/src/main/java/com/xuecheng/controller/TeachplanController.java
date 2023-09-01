package com.xuecheng.controller;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.ApiOperation;
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
        teachplanService.teachplanDeleteById(id);
    }

    /**
     * 课程计划的排序
     * 上移或者下移
     * @param moveType
     * @param id
     */
    @PostMapping("/teachplan/{moveType}/{id}")
    public void move(@PathVariable("moveType")String moveType,@PathVariable("id")String id){
        teachplanService.move(moveType,id);
    }

    /**
     * 课程计划和媒资信息绑定
     * @param bindTeachplanMediaDto
     */
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    /**
     * 删除媒资绑定
     * @param teachplanId
     * @param mediaId
     */
    @ApiOperation(value = "删除媒资绑定")
    @DeleteMapping("/teachplan/association/media/{teachplanId}/{mediaId}")
    public void deleteAssociationMedia(@PathVariable("teachplanId")String teachplanId,@PathVariable("mediaId")String mediaId){
        teachplanService.deleteAssociationMedia(teachplanId,mediaId);
    }
}
