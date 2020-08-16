package com.honezhi.sso.server.constant;

/**
 * @author tsb
 * @date 2020/8/13 14:36
 * @description 用户账号信息描述
 */
public interface UserStatus {
	int DISABLED = 1;			// 账号被禁用
	int EXPIRED = 1;			// 账号过期,需要重新修改密码
}
