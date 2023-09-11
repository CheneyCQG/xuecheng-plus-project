package com.xuecheng.auth.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;

/**
 * 统一认证接口
 */
public interface AuthService {
    XcUser vertify(AuthParamsDto authParamsDto);
}
