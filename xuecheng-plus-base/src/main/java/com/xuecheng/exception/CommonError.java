package com.xuecheng.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum CommonError {
    COMMON_ERROR("服务器出错！"),
    INVALID_ERROR("无效参数"),
    BALANCE_NOT_NAVIGATE_ERROR("金额不能为负数");
    String errMessage;
}
