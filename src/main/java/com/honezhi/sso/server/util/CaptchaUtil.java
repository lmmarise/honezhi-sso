package com.honezhi.sso.server.util;


import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.support.CaptchaStyle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tsb
 * @date 2020/8/13 16:21
 * @description 获取验证码接口
 */
public final class CaptchaUtil {

	/**
	 * 在session中创建一个验证码
	 */
	public static void crateCaptcha(HttpServletRequest request, HttpServletResponse response) {
		HappyCaptcha.require(request, response)
				.style(CaptchaStyle.ANIM)
				.length(4)
				.width(180)
				.height(60)
				.build().finish();
	}

	/**
	 * 验证用户填写的验证码是否正确
	 * @param code 验证码
	 * @param request 用户请求, 主要用来获取session中的验证码
	 * @return 是否匹配
	 */
	public static boolean verifyCaptcha(String code, HttpServletRequest request) {
		// 验证
		boolean flag = HappyCaptcha.verification(request, code, true);
		// 每次验证都把session中的验证码清除
		HappyCaptcha.remove(request);
		return flag;
	}

}
