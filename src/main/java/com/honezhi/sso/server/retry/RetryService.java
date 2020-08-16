package com.honezhi.sso.server.retry;


import com.honezhi.sso.server.pojo.dto.OauthUserAttribute;
import com.honezhi.sso.server.service.OauthThirdPartyApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RetryService {

	@Autowired
	private OauthThirdPartyApiService oauthThirdPartyApiService;

	//=====================================调用验证用户名密码的 retry 逻辑 start=====================================

	// delay: 指定延迟后重试
	// multiplier: 指定延迟的倍数, 例如: multiplier=2, 即, 第一次1s, 第二次2s, 第三次4s
	@Retryable(value = {Exception.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000L, multiplier = 1))
	public OauthUserAttribute getOauthUserAttributeBO(String username, String password) {
		return oauthThirdPartyApiService.getOauthUserAttributeDTO(username, password);
	}

	// 重试到达指定次数时，被注解的方法将被回调
	@Recover
	public OauthUserAttribute getOauthUserAttributeBORecover(Exception e) {
		log.error("多次重试调用验证用户名密码接口失败=<{}>", e.getMessage());
		return new OauthUserAttribute();
	}

	//=====================================调用验证用户名密码的  end=====================================


	//=====================================私有方法 start=====================================

	//=====================================私有方法  end=====================================

}
