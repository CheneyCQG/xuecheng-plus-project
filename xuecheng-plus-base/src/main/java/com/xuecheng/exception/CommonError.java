package com.xuecheng.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum CommonError {
    COMMON_ERROR("服务器出错！"),
    INVALID_ERROR("无效参数"),
    BALANCE_NOT_NAVIGATE_ERROR("金额不能为负数"),
    HAVINGG_CHILD_NOT_DELETE("课程计划信息还有子级信息，无法操作"),
    NOT_MOVEDOWN("已经在最下方，不可以再下移了"),
    NOT_MOVEUP("已经在最上方，不可以再上移了");
    String errMessage;
}
