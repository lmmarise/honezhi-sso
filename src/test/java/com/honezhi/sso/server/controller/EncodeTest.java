package com.honezhi.sso.server.controller;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author tsb
 * @date 2020/8/13 13:49
 * @description
 */
public class EncodeTest {
	@Test
	public void test() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("123456"));
		System.out.println(encoder.encode("123"));
	}
}
