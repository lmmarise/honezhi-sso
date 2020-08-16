package com.honezhi.sso.server.service;


import com.honezhi.sso.server.constant.UserStatus;
import com.honezhi.sso.server.exception.OauthApiException;
import com.honezhi.sso.server.jpa.dao.UserDao;
import com.honezhi.sso.server.jpa.entity.User;
import com.honezhi.sso.server.pojo.dto.OauthUserAttribute;
import com.honezhi.sso.server.util.JsonUtil;
import com.honezhi.sso.server.util.okhttp.OkHttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OauthThirdPartyApiService {

	@Autowired
	private OkHttpService okHttpService;

	@Qualifier("userPasswordEncoder")
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDao userDao;

	//=====================================业务处理 start=====================================


	/**
	 * 根据用户名和密码获取用户信息
	 */
	public OauthUserAttribute getOauthUserAttributeDTO(String username, String password) {
		log.debug("正在请求 UPMS 接口");
		log.debug("username=<{}>", username);
		log.debug("password=<{}>", password);

		// 演示模式, 静态登录
//		if (StringUtil.notEqualsIgnoreCase(username, "admin")) {
//			throw new OauthApiException("演示模式下，用户名是 admin");
//		}
//
//		if (StringUtil.notEqualsIgnoreCase(password, "123456")) {
//			throw new OauthApiException("演示模式下，密码是 123456");
//		}

		// 下面是真实场景下的 REST 调用方式。如果可以直连数据库的话，这里可以改为 Mapper 查询
		// OkHttpResponse okHttpResponse = okHttpService.get("https://www.baidu.com/");
		// log.debug("调用第三方接口返回=<{}>", okHttpResponse.toString());
		// if (okHttpResponse.getStatus() != HttpStatus.OK.value()) {
		// 	throw new OauthApiException("调用 UPMS 接口获取用户信息失败");
		// }

		User user = checkUser(username, password);

		return getUserInfoApi(user);
	}


	//=====================================业务处理  end=====================================

	//=====================================私有方法 start=====================================

	/**
	 * 根据用户名和密码校验正在登录的用户
	 * 1.用户名和密码是否正确
	 * 2.用户是否被禁用
	 * 3.用户账号密码是否需要修改
	 */
	private User checkUser(String username, String password) {
		User user = userDao.findUserByUsername(username);
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new OauthApiException("用户名或密码不正确");
		}

		if (user.getDisabled() == UserStatus.DISABLED) {
			throw new OauthApiException("该账号被禁用");
		}

		return user;
	}

	/**
	 * po 数据转为 vo 数据
	 * 由于 BeanUtils.copyProperties() 不能复制不同类型额属性, 使用 json 来进行属性拷贝
	 * @return	json格式的用户信息
	 */
	private OauthUserAttribute getUserInfoApi(User user) {
		// 不同类型的 Bean 之间的属性复制
		String userInfoJson = "{\n" +
				"  \"email\": \"" + user.getUserEmail() + "\",\n" +
				"  \"userId\": \"" + user.getId() + "\",\n" +
				"  \"username\": \"" + user.getUsername() + "\"\n" +
				"}";

		return JsonUtil.toObject(userInfoJson, OauthUserAttribute.class);
	}

	//=====================================私有方法  end=====================================

}
