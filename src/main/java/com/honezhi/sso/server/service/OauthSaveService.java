package com.honezhi.sso.server.service;


import com.honezhi.sso.server.constant.GlobalVariable;
import com.honezhi.sso.server.pojo.bo.cache.*;
import com.honezhi.sso.server.pojo.dto.OauthUserAttribute;
import com.honezhi.sso.server.properties.OauthProperties;
import com.honezhi.sso.server.util.DatetimeUtil;
import com.honezhi.sso.server.util.UserAgentUtil;
import com.honezhi.sso.server.util.redis.StringRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class OauthSaveService {

	@Autowired
	private StringRedisService<String, OauthCodeToRedisBO> codeRedisService;

	@Autowired
	private StringRedisService<String, OauthTgcToRedisBO> tgcRedisService;

	@Autowired
	private StringRedisService<String, OauthUserInfoToRedisBO> userInfoRedisService;

	@Autowired
	private StringRedisService<String, OauthAccessTokenToRedisBO> accessTokenRedisService;

	@Autowired
	private StringRedisService<String, OauthRefreshTokenToRedisBO> refreshTokenRedisService;

	@Autowired
	private OauthGenerateService oauthGenerateService;

	@Autowired
	private OauthProperties oauthProperties;

	//=====================================业务处理 start=====================================

	@Async
	public void saveUserInfoKeyToRedis(String userInfoRedisKey, OauthUserAttribute oauthUserAttribute) {
		OauthUserInfoToRedisBO oauthUserInfoToRedisBO = new OauthUserInfoToRedisBO();
		oauthUserInfoToRedisBO.setUserAttribute(oauthUserAttribute);
		oauthUserInfoToRedisBO.setIat(DatetimeUtil.currentEpochSecond());

		userInfoRedisService.set(userInfoRedisKey, oauthUserInfoToRedisBO, oauthProperties.getRefreshTokenMaxTimeToLiveInSeconds(), TimeUnit.SECONDS);
	}

	@Async
	public void saveCodeToRedis(String code, String tgc, String userInfoRedisKey, String clientId) {
		OauthCodeToRedisBO oauthCodeToRedisBO = new OauthCodeToRedisBO();
		oauthCodeToRedisBO.setTgc(tgc);
		oauthCodeToRedisBO.setUserInfoRedisKey(userInfoRedisKey);
		oauthCodeToRedisBO.setClientId(clientId);
		oauthCodeToRedisBO.setIat(DatetimeUtil.currentEpochSecond());

		codeRedisService.set(GlobalVariable.REDIS_OAUTH_CODE_PREFIX_KEY_PREFIX + code, oauthCodeToRedisBO, oauthProperties.getCodeMaxTimeToLiveInSeconds(), TimeUnit.SECONDS);
	}

	@Async
	public void saveTgcToRedisAndCookie(String tgc, Integer maxTimeToLiveInSeconds, String userInfoRedisKey, String userAgent, String requestIp, boolean isRememberMe) {
		OauthTgcToRedisBO oauthTgcToRedisBO = new OauthTgcToRedisBO();
		oauthTgcToRedisBO.setIat(DatetimeUtil.currentEpochSecond());
		oauthTgcToRedisBO.setUserAgent(userAgent);
		oauthTgcToRedisBO.setRequestIp(requestIp);
		oauthTgcToRedisBO.setBoolIsRememberMe(isRememberMe);
		oauthTgcToRedisBO.setBoolIsMobile(UserAgentUtil.isMobile(userAgent));
		oauthTgcToRedisBO.setUserInfoRedisKey(userInfoRedisKey);

		oauthTgcToRedisBO.setIat(DatetimeUtil.currentEpochSecond());
		tgcRedisService.set(GlobalVariable.REDIS_TGC_KEY_PREFIX + tgc, oauthTgcToRedisBO, maxTimeToLiveInSeconds, TimeUnit.SECONDS);
	}

	@Async
	public void updateTgcAndUserInfoRedisKeyExpire(String tgc, String userInfoRedisKey) {
		tgcRedisService.expire(GlobalVariable.REDIS_TGC_KEY_PREFIX + tgc, oauthProperties.getTgcAndUserInfoMaxTimeToLiveInSeconds(), TimeUnit.SECONDS);
		userInfoRedisService.expire(userInfoRedisKey, oauthProperties.getTgcAndUserInfoMaxTimeToLiveInSeconds(), TimeUnit.SECONDS);
	}

	@Async
	public void saveAccessToken(String accessToken, OauthUserAttribute oauthUserAttribute, String clientId, String grantType) {
		long currentEpochSecond = DatetimeUtil.currentEpochSecond();
		OauthAccessTokenToRedisBO oauthAccessTokenToRedisBO = new OauthAccessTokenToRedisBO();
		if (null != oauthUserAttribute) {
			//客户端模式情况下是没有用户信息的
			oauthAccessTokenToRedisBO.setUserAttribute(oauthUserAttribute);
		}
		oauthAccessTokenToRedisBO.setGrantType(grantType);
		oauthAccessTokenToRedisBO.setClientId(clientId);
		oauthAccessTokenToRedisBO.setIat(currentEpochSecond);
		saveAccessTokenToRedis(accessToken, oauthAccessTokenToRedisBO);
	}

	@Async
	public void saveRefreshToken(String refreshToken, OauthUserAttribute oauthUserAttribute, String clientId, String grantType) {
		long currentEpochSecond = DatetimeUtil.currentEpochSecond();
		OauthRefreshTokenToRedisBO oauthRefreshTokenToRedisBO = new OauthRefreshTokenToRedisBO();
		if (null != oauthUserAttribute) {
			//客户端模式情况下是没有用户信息的
			String userId = oauthUserAttribute.getUserId();
			String userInfoRedisKey = oauthGenerateService.generateUserInfoRedisKey(userId);
			oauthRefreshTokenToRedisBO.setUserInfoRedisKey(userInfoRedisKey);
		}
		oauthRefreshTokenToRedisBO.setGrantType(grantType);
		oauthRefreshTokenToRedisBO.setClientId(clientId);
		oauthRefreshTokenToRedisBO.setIat(currentEpochSecond);
		saveRefreshTokenToRedis(refreshToken, oauthRefreshTokenToRedisBO);
	}

	//=====================================业务处理  end=====================================

	//=====================================私有方法 start=====================================

	private void saveAccessTokenToRedis(String accessToken, OauthAccessTokenToRedisBO oauthAccessTokenToRedisBO) {
		accessTokenRedisService.set(GlobalVariable.REDIS_OAUTH_ACCESS_TOKEN_KEY_PREFIX + accessToken, oauthAccessTokenToRedisBO, oauthProperties.getAccessTokenMaxTimeToLiveInSeconds(), TimeUnit.SECONDS);
	}

	private void saveRefreshTokenToRedis(String refreshToken, OauthRefreshTokenToRedisBO oauthRefreshTokenToRedisBO) {
		refreshTokenRedisService.set(GlobalVariable.REDIS_OAUTH_REFRESH_TOKEN_KEY_PREFIX + refreshToken, oauthRefreshTokenToRedisBO, oauthProperties.getRefreshTokenMaxTimeToLiveInSeconds(), TimeUnit.SECONDS);
	}

	//=====================================私有方法  end=====================================

}
