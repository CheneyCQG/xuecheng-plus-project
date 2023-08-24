package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author Cheney
 */
@Slf4j
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {
    @Autowired
    private TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> teachplanTreeNodes(String courseId) {
        return teachplanMapper.teachplan(courseId);
    }

    @Override
    public void teachplan(SaveTeachplanDto saveTeachplanDto) {
        //1判断是否有id 新增或者修改
        if (saveTeachplanDto.getId() == null || saveTeachplanDto.getId().equals(0L)) {
            //2新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplan.setCreateDate(LocalDateTime.now());
            teachplan.setChangeDate(LocalDateTime.now());
            //设置章节或者小节的排序
            if (teachplan.getGrade() == 1){
                //章节
                //查询当前课程，并且grade==1的有几条
//                Integer count = teachplanMapper.selectCount(
//                        new QueryWrapper<Teachplan>()
//                                .eq("course_id", teachplan.getCourseId())
//                                .eq("grade", 1));
                Integer orderby = teachplanMapper.selectMaxOrderby(teachplan.getCourseId(),teachplan.getGrade(),teachplan.getParentid());
                if (orderby == null)
                    orderby = 0;
                //不能通过记录数+1来计算orderby，查询出最大的orderby
                teachplan.setOrderby(orderby+1);
            }else {
                //小节
                //查询当前课程，并且parentid是当前章节那些小节有几条
//                Integer count = teachplanMapper.selectCount(
//                        new QueryWrapper<Teachplan>()
//                                .eq("course_id", teachplan.getCourseId())
//                                .eq("parentid",teachplan.getParentid())
//                                .eq("grade", 2));
                Integer orderby = teachplanMapper.selectMaxOrderby(teachplan.getCourseId(),teachplan.getGrade(),teachplan.getParentid());
                if (orderby == null)
                    orderby = 0;
                teachplan.setOrderby(orderby+1);
            }

            int rows = teachplanMapper.insert(teachplan);
            if (rows != 1) {
                GlobalException.cast("章节或者小节插入失败");
            }
        }else {
            //修改
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplan.setChangeDate(LocalDateTime.now());

            teachplanMapper.updateById(teachplan);
        }
    }
}
