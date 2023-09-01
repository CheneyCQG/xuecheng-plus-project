package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.exception.CommonError;
import com.xuecheng.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

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

    @Override
    @Transactional
    public void teachplanDeleteById(String id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan.getGrade() == 1){
            Integer count = teachplanMapper.selectCount(new QueryWrapper<Teachplan>().eq("parentid", teachplan.getId()));
            if (count > 0){
                GlobalException.cast(CommonError.HAVINGG_CHILD_NOT_DELETE);
            }
        }
        int i = teachplanMapper.deleteById(id);
        int teachplanId = teachplanMediaMapper.delete(new QueryWrapper<TeachplanMedia>().eq("teachplan_id", id));
    }

    @Override
    @Transactional
    public void move(String moveType, String id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Teachplan teachplan1 = null;
        if (teachplan.getGrade() == 2){
            if (moveType.equals("movedown")){
                teachplan1 = teachplanMapper.selectOne(new QueryWrapper<Teachplan>().ne("id",teachplan.getCourseId()).eq("parentid",teachplan.getParentid()).gt("orderby", teachplan.getOrderby()).orderByAsc("orderby").last("LIMIT 1"));
                if (teachplan1 == null)
                    GlobalException.cast(CommonError.NOT_MOVEDOWN);
            }else{
                teachplan1 = teachplanMapper.selectOne(new QueryWrapper<Teachplan>().ne("id",teachplan.getCourseId()).eq("parentid",teachplan.getParentid()).lt("orderby",teachplan.getOrderby()).orderByDesc("orderby").last("LIMIT 1"));
                if (teachplan1 == null)
                    GlobalException.cast(CommonError.NOT_MOVEUP);
            }
        }else {
            if (moveType.equals("movedown")){
                teachplan1 = teachplanMapper.selectOne(new QueryWrapper<Teachplan>().ne("id",teachplan.getCourseId()).eq("parentid",0).eq("course_id",teachplan.getCourseId()).gt("orderby", teachplan.getOrderby()).orderByAsc("orderby").last("LIMIT 1"));
                if (teachplan1 == null)
                    GlobalException.cast(CommonError.NOT_MOVEDOWN);
            }else{
                teachplan1 = teachplanMapper.selectOne(new QueryWrapper<Teachplan>().ne("id",teachplan.getCourseId()).eq("parentid",0).eq("course_id",teachplan.getCourseId()).lt("orderby",teachplan.getOrderby()).orderByDesc("orderby").last("LIMIT 1"));
                if (teachplan1 == null)
                    GlobalException.cast(CommonError.NOT_MOVEUP);
            }
        }


        int temp = teachplan.getOrderby();
        teachplan.setOrderby(teachplan1.getOrderby());
        teachplan1.setOrderby(temp);
        int update = teachplanMapper.updateById(teachplan);
        int i = teachplanMapper.updateById(teachplan1);
    }

    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //1教学计划id判断数据库中是否存在
        Teachplan teachplan = teachplanMapper.selectById(bindTeachplanMediaDto.getTeachplanId());
        if (teachplan == null) {
            GlobalException.cast("教学计划不存在");
        }
        if (teachplan.getGrade() != 2) {
            GlobalException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //2先删除原先该教学计划绑定的媒资信息
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,bindTeachplanMediaDto.getTeachplanId()));
        //3添加教学计划和媒资信息的绑定
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setTeachplanId(bindTeachplanMediaDto.getTeachplanId());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMedia.setCreatePeople("jack");
        teachplanMedia.setChangePeople("rose");
        int insert = teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public void deleteAssociationMedia(String teachplanId, String mediaId) {
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId).eq(TeachplanMedia::getMediaId,mediaId));
    }
}
