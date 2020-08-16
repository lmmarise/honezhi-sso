package com.honezhi.sso.server.strategy;


import com.honezhi.sso.server.constant.GlobalVariable;
import com.honezhi.sso.server.exception.OauthApiException;
import com.honezhi.sso.server.pojo.bo.cache.OauthCodeToRedisBO;
import com.honezhi.sso.server.pojo.bo.cache.OauthUserInfoToRedisBO;
import com.honezhi.sso.server.pojo.bo.handle.OauthTokenStrategyHandleBO;
import com.honezhi.sso.server.pojo.dto.OauthToken;
import com.honezhi.sso.server.pojo.dto.OauthUserAttribute;
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
@Service(GlobalVariable.OAUTH_CODE_GRANT_TYPE)
public class OauthCodeToTokenStrategy implements OauthTokenStrategyInterface {

	@Autowired
	private StringRedisService<String, OauthCodeToRedisBO> codeRedisService;

	@Autowired
	private StringRedisService<String, OauthUserInfoToRedisBO> userInfoRedisService;

	@Autowired
	private OauthCheckParamService oauthCheckParamService;

	@Autowired
	private OauthGenerateService oauthGenerateService;

	@Autowired
	private OauthSaveService oauthSaveService;

	//=====================================业务处理 start=====================================

	@Override
	public void checkParam(OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO) {
		oauthCheckParamService.checkOauthTokenParam(oauthTokenParam);

		OauthCodeToRedisBO oauthCodeToRedisBO = codeRedisService.get(GlobalVariable.REDIS_OAUTH_CODE_PREFIX_KEY_PREFIX + oauthTokenParam.getCode());
		if (null == oauthCodeToRedisBO) {
			throw new OauthApiException("code 无效");
		}

		if (StringUtil.notEqualsIgnoreCase(oauthCodeToRedisBO.getClientId(), oauthTokenParam.getClientId())) {
			throw new OauthApiException("该 code 与当前请求的 client_id 参数不匹配");
		}

		oauthTokenStrategyHandleBO.setUserInfoRedisKey(oauthCodeToRedisBO.getUserInfoRedisKey());
	}

	@Override
	public OauthToken handle(OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO) {
		String userInfoRedisKey = oauthTokenStrategyHandleBO.getUserInfoRedisKey();
		OauthUserInfoToRedisBO oauthUserInfoToRedisBO = userInfoRedisService.get(userInfoRedisKey);

		OauthToken oauthToken = oauthGenerateService.generateOauthTokenInfoBO(true);

		OauthUserAttribute userAttribute = oauthUserInfoToRedisBO.getUserAttribute();
		oauthSaveService.saveAccessToken(oauthToken.getAccessToken(), userAttribute, oauthTokenParam.getClientId(), GlobalVariable.OAUTH_CODE_GRANT_TYPE);
		oauthSaveService.saveRefreshToken(oauthToken.getRefreshToken(), userAttribute, oauthTokenParam.getClientId(), GlobalVariable.OAUTH_CODE_GRANT_TYPE);

		// code 只能被用一次，这里用完会立马被删除
		codeRedisService.delete(GlobalVariable.REDIS_OAUTH_CODE_PREFIX_KEY_PREFIX + oauthTokenParam.getCode());
		return oauthToken;
	}

	//=====================================业务处理 end=====================================

}
