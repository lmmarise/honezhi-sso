package com.cdk8s.tkey.server.controller;

import com.cdk8s.tkey.server.util.CaptchaUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tsb
 * @date 2020/8/13 17:22
 * @description
 */
@Controller
public class CaptchaController {

	/**
	 * 验证码接口, 验证码将被保存在session
	 */
	@GetMapping("/captcha")
	public void happyCaptcha(HttpServletRequest request, HttpServletResponse response) {
		CaptchaUtil.crateCaptcha(request, response);
	}

}
