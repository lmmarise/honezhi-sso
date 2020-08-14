package com.cdk8s.tkey.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author tsb
 * @date 2020/8/13 12:21
 * @description 自定义密码校验逻辑
 */
@Component
public class UserPasswordEncoder implements PasswordEncoder {

	/**
	 * 如果数据库密码使用 BCrypt 进行加密存储
	 */
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public String encode(CharSequence rawPassword) {
		return bCryptPasswordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
	}

}
