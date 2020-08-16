package com.honezhi.sso.server.strategy;


import com.honezhi.sso.server.constant.GlobalVariable;
import com.honezhi.sso.server.exception.OauthApiException;
import com.honezhi.sso.server.pojo.bo.cache.OauthRefreshTokenToRedisBO;
import com.honezhi.sso.server.pojo.bo.cache.OauthUserInfoToRedisBO;
import com.honezhi.sso.server.pojo.bo.handle.OauthTokenStrategyHandleBO;
import com.honezhi.sso.server.pojo.dto.OauthToken;
import com.honezhi.sso.server.pojo.dto.param.OauthTokenParam;
import com.honezhi.sso.server.service.OauthCheckParamService;
import com.honezhi.sso.server.service.OauthGenerateService;
import com.honezhi.sso.server.service.OauthSaveService;
import com.honezhi.sso.server.util.StringUtil;
import com.honezhi.sso.server.util.redis.StringRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(GlobalVariable.OAUTH_REFRESH_TOKEN_GRANT_TYPE)
public class OauthRefreshTokenToTokenStrategy implements OauthTokenStrategyInterface {

	@Autowired
	private StringRedisService<String, OauthUserInfoToRedisBO> userInfoRedisService;

	@Autowired
	private StringRedisService<String, OauthRefreshTokenToRedisBO> refreshTokenRedisService;

	@Autowired
	private OauthCheckParamService oauthCheckParamService;

	@Autowired
	private OauthSaveService oauthSaveService;

	@Autowired
	private OauthGenerateService oauthGenerateService;

	//=====================================业务处理 start=====================================

	@Override
	public void checkParam(OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO) {
		oauthCheckParamService.checkOauthRefreshTokenParam(oauthTokenParam);

		OauthRefreshTokenToRedisBO oauthRefreshTokenToRedisBO =
				refreshTokenRedisService.get(
						GlobalVariable.REDIS_OAUTH_REFRESH_TOKEN_KEY_PREFIX + oauthTokenParam.getRefreshToken()
				);
		if (null == oauthRefreshTokenToRedisBO) {
			throw new OauthApiException("refresh_token 已失效");
		}

		if (StringUtil.notEqualsIgnoreCase(oauthRefreshTokenToRedisBO.getClientId(), oauthTokenParam.getClientId())) {
			throw new OauthApiException("该 refresh_token 与当前请求的 client_id 参数不匹配");
		}

		String userInfoRedisKey = oauthRefreshTokenToRedisBO.getUserInfoRedisKey();
		if (null != userInfoRedisKey) {
			OauthUserInfoToRedisBO oauthUserInfoToRedisBO = userInfoRedisService.get(userInfoRedisKey);
			if (null == oauthUserInfoToRedisBO) {
				throw new OauthApiException("未找到该用户信息");
			}

			oauthTokenStrategyHandleBO.setUserAttribute(oauthUserInfoToRedisBO.getUserAttribute());
		}
	}

	@Override
	public OauthToken handle(OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO) {

		OauthToken oauthTokenInfoByRefreshTokenBO = oauthGenerateService.generateOauthTokenInfoBO(false);

		if (null != oauthTokenStrategyHandleBO.getUserAttribute()) {
			oauthSaveService.saveAccessToken(
					oauthTokenInfoByRefreshTokenBO.getAccessToken(),
					oauthTokenStrategyHandleBO.getUserAttribute(),
					oauthTokenParam.getClientId(),
					GlobalVariable.OAUTH_REFRESH_TOKEN_GRANT_TYPE);
		} else {
			oauthSaveService.saveAccessToken(oauthTokenInfoByRefreshTokenBO.getAccessToken(),
					null,
					oauthTokenParam.getClientId(),
					GlobalVariable.OAUTH_REFRESH_TOKEN_GRANT_TYPE);
		}

		return oauthTokenInfoByRefreshTokenBO;
	}

	//=====================================业务处理 end=====================================

}
