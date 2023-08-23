package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author Cheney
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> tree() {
        List<CourseCategory> courseCategories = courseCategoryMapper.tree("1");
//        ArrayList<CourseCategoryTreeDto> tree = new ArrayList<>();
        Map<String,CourseCategoryTreeDto> treeDtoMap = new HashMap<>();
        //将集合封装成List<CourseCategoryTreeDto>
        for (CourseCategory courseCategory : courseCategories) {
            //先判断一级节点
            if (courseCategory.getParentid().equals("1")) {
                CourseCategoryTreeDto dto = new CourseCategoryTreeDto();
                BeanUtils.copyProperties(courseCategory,dto);
                dto.setChildrenTreeNodes(new ArrayList<>());
                treeDtoMap.put(dto.getId(),dto);
                continue;
            }
            //判断二级节点
            String parentid = courseCategory.getParentid();
            CourseCategoryTreeDto parentDto = treeDtoMap.get(parentid);
            if (parentDto != null) {
                CourseCategoryTreeDto childdto = new CourseCategoryTreeDto();
                BeanUtils.copyProperties(courseCategory, childdto);
                parentDto.getChildrenTreeNodes().add(childdto);
            }
        }
        List<CourseCategoryTreeDto> dtos = treeDtoMap.values().stream().collect(Collectors.toList());
        return dtos;
    }
}
