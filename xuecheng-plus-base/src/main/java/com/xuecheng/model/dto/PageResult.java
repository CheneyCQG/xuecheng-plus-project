package com.xuecheng.model.dto;

import lombok.Data;

import java.util.List;
@Data
public class PageResult<T> {
    // 数据列表
    private List<T> items;
    //总记录数
    private long counts;
    //当前页码
    private long page;
    //每页记录数
    private long pageSize;
}
