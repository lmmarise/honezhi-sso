package com.honezhi.sso.server.controller;


import com.honezhi.sso.server.constant.GlobalVariable;
import com.honezhi.sso.server.enums.ResponseProduceTypeEnum;
import com.honezhi.sso.server.exception.OauthApiException;
import com.honezhi.sso.server.pojo.bo.cache.*;
import com.honezhi.sso.server.pojo.bo.handle.OauthTokenStrategyHandleBO;
import com.honezhi.sso.server.pojo.dto.OauthIntrospect;
import com.honezhi.sso.server.pojo.dto.OauthToken;
import com.honezhi.sso.server.pojo.dto.OauthUserAttribute;
import com.honezhi.sso.server.pojo.dto.OauthUserProfile;
import com.honezhi.sso.server.pojo.dto.param.*;
import com.honezhi.sso.server.properties.OauthProperties;
import com.honezhi.sso.server.retry.RetryService;
import com.honezhi.sso.server.service.OauthCheckParamService;
import com.honezhi.sso.server.service.OauthGenerateService;
import com.honezhi.sso.server.service.OauthSaveService;
import com.honezhi.sso.server.strategy.OauthTokenStrategyContext;
import com.honezhi.sso.server.util.*;
import com.honezhi.sso.server.util.redis.StringRedisService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/oauth")
@Api(value = "OauthController | 登录接口")
public class OauthController {

    @Autowired
    private StringRedisService<String, OauthTgcToRedisBO> tgcRedisService;

    @Autowired
    private StringRedisService<String, OauthUserInfoToRedisBO> userInfoRedisService;

    @Autowired
    private StringRedisService<String, OauthAccessTokenToRedisBO> accessTokenRedisService;

    @Autowired
    private StringRedisService<String, OauthRefreshTokenToRedisBO> refreshTokenRedisService;

    @Autowired
    private OauthCheckParamService oauthCheckParamService;

    @Autowired
    private OauthGenerateService oauthGenerateService;

    @Autowired
    private OauthSaveService oauthSaveService;

    @Autowired
    private RetryService retryService;

    @Autowired
    private OauthTokenStrategyContext oauthTokenStrategyContext;

    @Autowired
    private OauthProperties oauthProperties;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    //=====================================业务处理 start=====================================

    /**
     * 登录页面入口
     * <p>
     * dataType="int" 代表请求参数类型为int类型，当然也可以是Map、User、String等；
     * paramType="body" 代表参数应该放在请求的什么地方：
     * header-->放在请求头。请求参数的获取：@RequestHeader(代码中接收注解)
     * query-->用于get请求的参数拼接。请求参数的获取：@RequestParam(代码中接收注解)
     * path（用于restful接口）-->请求参数的获取：@PathVariable(代码中接收注解)
     * body-->放在请求体。请求参数的获取：@RequestBody(代码中接收注解)
     * form（不常用）
     */
    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    @ApiOperation(value = "登录页面入口", notes = "返回登录的HTML页面")
    public String authorize(ModelMap model, OauthAuthorizeParam oAuthAuthorizeParam) {

        OauthClientToRedisBO oauthClientToRedisBO = oauthCheckParamService.checkOauthAuthorizeParam(oAuthAuthorizeParam);

        model.put(GlobalVariable.DEFAULT_LOGIN_PAGE_CLIENT_INFO_KEY, oauthClientToRedisBO);

        String tgcCookieValue = CookieUtil.getCookie(request, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
        if (StringUtil.isBlank(tgcCookieValue)) {
            return GlobalVariable.DEFAULT_LOGIN_PAGE_PATH;
        }

        String userInfoRedisKey = oauthCheckParamService.checkCookieTgc(request.getHeader(GlobalVariable.HTTP_HEADER_USER_AGENT), IPUtil.getIp(request), tgcCookieValue);

        String finalRedirectUrl;
        String redirectUri = oAuthAuthorizeParam.getRedirectUri();
        if (StringUtil.equalsIgnoreCase(oAuthAuthorizeParam.getResponseType(), GlobalVariable.OAUTH_TOKEN_RESPONSE_TYPE)) {
            // 简化模式
            OauthUserInfoToRedisBO oauthUserInfoToRedisBO = userInfoRedisService.get(userInfoRedisKey);

            OauthToken oauthTokenInfoByCodePO = oauthGenerateService.generateOauthTokenInfoBO(true);
            oauthSaveService.saveAccessToken(oauthTokenInfoByCodePO.getAccessToken(), oauthUserInfoToRedisBO.getUserAttribute(),
                    oAuthAuthorizeParam.getClientId(), GlobalVariable.OAUTH_TOKEN_GRANT_TYPE);
            oauthSaveService.saveRefreshToken(oauthTokenInfoByCodePO.getRefreshToken(), oauthUserInfoToRedisBO.getUserAttribute(),
                    oAuthAuthorizeParam.getClientId(), GlobalVariable.OAUTH_TOKEN_GRANT_TYPE);
            finalRedirectUrl = getRedirectUrlWithAccessToken(redirectUri, oauthTokenInfoByCodePO);
        } else {
            // 授权码模式
            String code = oauthGenerateService.generateCode();
            oauthSaveService.saveCodeToRedis(code, tgcCookieValue, userInfoRedisKey, oAuthAuthorizeParam.getClientId());
            finalRedirectUrl = getRedirectUrlWithCode(redirectUri, oAuthAuthorizeParam.getState(), code);
        }

        oauthSaveService.updateTgcAndUserInfoRedisKeyExpire(tgcCookieValue, userInfoRedisKey);
        return GlobalVariable.REDIRECT_URI_PREFIX + finalRedirectUrl;
    }

    /**
     * 表单登录接口：验证用户名和密码
     */
    @RequestMapping(value = "/authorize", method = RequestMethod.POST)
    @ApiOperation(value = "表单登录接口", notes = "验证用户名和密码")
    // 单独描述一个参数, 只需要在pojo属性上用@ApiModelProperty描述即可
    /*@ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "oauthFormLoginParam",
                    value = "登录参数:1.用户名 2.密码 3.是否记住我",
                    required = true, dataType = "OauthFormLoginParam"),
            @ApiImplicitParam(paramType = "body", name = "captcha", value = "验证码",
                    required = true, dataType = "String")
    })*/
    public String formLogin(ModelMap model, OauthFormLoginParam oauthFormLoginParam, String captcha) {

        OauthClientToRedisBO oauthClientToRedisBO;
        OauthUserAttribute oauthUserAttribute;
        String userAgent = request.getHeader(GlobalVariable.HTTP_HEADER_USER_AGENT);
        String requestIp = IPUtil.getIp(request);

        try {
            oauthClientToRedisBO = oauthCheckParamService.checkClientIdParam(oauthFormLoginParam.getClientId());
            oauthCheckParamService.checkUserAgentAndRequestIpParam(userAgent, requestIp);
            oauthCheckParamService.checkOauthFormLoginParam(oauthFormLoginParam);

            model.put(GlobalVariable.DEFAULT_LOGIN_PAGE_CLIENT_INFO_KEY, oauthClientToRedisBO);

            // 校验验证码
            checkCaptcha(request, captcha);
            // 校验用户名密码
            oauthUserAttribute = requestLoginApi(oauthFormLoginParam);
        } catch (Exception e) {
            model.put(GlobalVariable.DEFAULT_LOGIN_ERROR_KEY, e.getMessage());
            return GlobalVariable.DEFAULT_LOGIN_PAGE_PATH;
        }

        String userInfoRedisKey = oauthGenerateService.generateUserInfoRedisKey(oauthUserAttribute.getUserId());
        oauthSaveService.saveUserInfoKeyToRedis(userInfoRedisKey, oauthUserAttribute);

        boolean isRememberMe = oauthFormLoginParam.getBoolIsRememberMe();
        String tgc = oauthGenerateService.generateTgc();

        Integer maxTimeToLiveInSeconds = oauthProperties.getTgcAndUserInfoMaxTimeToLiveInSeconds();
        if (isRememberMe) {
            maxTimeToLiveInSeconds = oauthProperties.getRememberMeMaxTimeToLiveInSeconds();
        }
        CookieUtil.setCookie(response, GlobalVariable.OAUTH_SERVER_COOKIE_KEY, tgc, maxTimeToLiveInSeconds, true, oauthProperties.getTgcCookieSecure());

        oauthSaveService.saveTgcToRedisAndCookie(tgc, maxTimeToLiveInSeconds, userInfoRedisKey, userAgent, requestIp, isRememberMe);

        String finalRedirectUrl;
        String redirectUri = oauthFormLoginParam.getRedirectUri();
        if (StringUtil.equalsIgnoreCase(oauthFormLoginParam.getResponseType(), GlobalVariable.OAUTH_TOKEN_RESPONSE_TYPE)) {
            // 简化模式
            OauthToken oauthToken = oauthGenerateService.generateOauthTokenInfoBO(true);
            oauthSaveService.saveAccessToken(oauthToken.getAccessToken(), oauthUserAttribute, oauthFormLoginParam.getClientId(), GlobalVariable.OAUTH_TOKEN_GRANT_TYPE);
            oauthSaveService.saveRefreshToken(oauthToken.getRefreshToken(), oauthUserAttribute, oauthFormLoginParam.getClientId(), GlobalVariable.OAUTH_TOKEN_GRANT_TYPE);
            finalRedirectUrl = getRedirectUrlWithAccessToken(redirectUri, oauthToken);
        } else {
            // 授权码模式
            String code = oauthGenerateService.generateCode();
            oauthSaveService.saveCodeToRedis(code, tgc, userInfoRedisKey, oauthFormLoginParam.getClientId());
            finalRedirectUrl = getRedirectUrlWithCode(redirectUri, oauthFormLoginParam.getState(), code);
        }

        return GlobalVariable.REDIRECT_URI_PREFIX + finalRedirectUrl;

    }

    /**
     * 换取 token（授权码模式、客户端模式、密码模式、刷新模式）
     */
    @RequestMapping(value = "/token", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @ApiOperation(value = "换取 token", notes = "根据code, 返回新的token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "换取成功"),
            @ApiResponse(code = 400, message = "换取失败")
    })
    public ResponseEntity<?> token(OauthTokenParam oauthTokenParam) {
        // 1.获取并校验授权码类型参数
        String grantType = oauthTokenParam.getGrantType();
        // 1.1.校验授权码类型是否是服务器已知的
        oauthCheckParamService.checkGrantTypeParam(grantType);
        // 1.2.解析请求头中的 ClientSecret 和 ClientId
        resolveRequestHeader(request, oauthTokenParam);
        // 1.3.验证请求头中的 ClientSecret 和 ClientId
        OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO = new OauthTokenStrategyHandleBO();
        oauthTokenStrategyContext.checkParam(grantType, oauthTokenParam, oauthTokenStrategyHandleBO);
        // 2.根据4种授权模式的各自实现, 生成token授权码
        OauthToken oauthToken = oauthTokenStrategyContext.generateOauthTokenInfo(grantType, oauthTokenParam, oauthTokenStrategyHandleBO);
        // 3.以json格式相应给客户端
        return ResponseEntity.ok(oauthToken);
    }


    /**
     * 根据 AccessToken 获取用户信息
     */
    @RequestMapping(value = "/userinfo", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @ApiOperation(value = "根据 AccessToken 获取用户信息", notes = "REST ful风格的接口")
    public ResponseEntity<?> userinfo() {
        OauthAccessTokenToRedisBO oauthAccessTokenToRedisBO = oauthCheckParamService.checkAccessTokenParam(request);

        OauthUserProfile oauthUserProfile = new OauthUserProfile();
        buildOauthUserInfoByTokenDTO(oauthUserProfile, oauthAccessTokenToRedisBO);

        return ResponseEntity.ok(oauthUserProfile);
    }


    /**
     * 验证 AccessToken / RefreshToken 有效性和基础信息
     */
    @RequestMapping(value = "/introspect", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "校验 AccessToken", notes = "验证 AccessToken / RefreshToken 有效性和基础信息")
    public ResponseEntity<?> introspect(OauthIntrospectTokenParam oauthIntrospectTokenParam) {

        resolveRequestHeader(request, oauthIntrospectTokenParam);
        OauthIntrospect oauthIntrospect = oauthCheckParamService.checkOauthIntrospectTokenParam(oauthIntrospectTokenParam);

        Long iat = 0L;
        String grantType = "";

        String token = oauthIntrospectTokenParam.getToken();
        String tokenTypeHint = oauthIntrospectTokenParam.getTokenTypeHint();
        if (StringUtil.equalsIgnoreCase(tokenTypeHint, GlobalVariable.OAUTH_ACCESS_TOKEN_TYPE_HINT)) {
            // 验证 AccessToken
            OauthAccessTokenToRedisBO oauthTokenToRedisDTO = accessTokenRedisService.get(GlobalVariable.REDIS_OAUTH_ACCESS_TOKEN_KEY_PREFIX + token);
            if (null == oauthTokenToRedisDTO) {
                throw new OauthApiException("token 已失效");
            }
            grantType = oauthTokenToRedisDTO.getGrantType();
            iat = oauthTokenToRedisDTO.getIat();
            oauthIntrospect.setExp(iat + oauthProperties.getAccessTokenMaxTimeToLiveInSeconds());
        } else if (StringUtil.equalsIgnoreCase(tokenTypeHint, GlobalVariable.OAUTH_REFRESH_TOKEN_GRANT_TYPE)) {
            // 验证 RefreshToken
            OauthRefreshTokenToRedisBO oauthTokenToRedisDTO = refreshTokenRedisService.get(GlobalVariable.REDIS_OAUTH_REFRESH_TOKEN_KEY_PREFIX + token);
            if (null == oauthTokenToRedisDTO) {
                throw new OauthApiException("token 已失效");
            }
            grantType = oauthTokenToRedisDTO.getGrantType();
            iat = oauthTokenToRedisDTO.getIat();
            oauthIntrospect.setExp(iat + oauthProperties.getRefreshTokenMaxTimeToLiveInSeconds());
        }

        oauthIntrospect.setGrantType(grantType);
        oauthIntrospect.setIat(iat);

        return ResponseEntity.ok(oauthIntrospect);
    }

    /**
     * 登出
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiOperation(value = "退出登录", notes = "退出登录并重定向到默认地址, 或给定地址")
    public String logout(@RequestParam(value = "redirect_uri", required = false) String redirectUri) {

        // 1.删除tgc cookie
        String tgcCookieValue = CookieUtil.getCookie(request, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
        if (StringUtil.isNotBlank(tgcCookieValue)) {
            tgcRedisService.delete(GlobalVariable.REDIS_TGC_KEY_PREFIX + tgcCookieValue);
            CookieUtil.deleteCookie(request, response, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
        }

        // 2.1.客户端需要重定向, 则重定向
        if (StringUtil.isNotBlank(redirectUri)) {
            return GlobalVariable.REDIRECT_URI_PREFIX + redirectUri;
        }

        // 2.1.重定向回系统默认地址, 登出成功地址
        return GlobalVariable.DEFAULT_LOGOUT_PAGE_PATH;

    }

    //=====================================业务处理  end=====================================

    //=====================================私有方法 start=====================================

    /**
     * 根据session中的验证码校验用户给的验证码
     */
    private void checkCaptcha(HttpServletRequest request, String captcha) {
        if (captcha == null || "".equals(captcha)) {
            throw new OauthApiException("验证码不能为空");
        } else if (!CaptchaUtil.verifyCaptcha(captcha, request)) {
            throw new OauthApiException("验证码错误");
        }
    }

    private OauthUserAttribute requestLoginApi(OauthFormLoginParam oauthFormLoginParam) {
        // 为了防止 UPMS 接口抖动，这里做了 retry 机制
        OauthUserAttribute oauthUserAttribute = retryService.getOauthUserAttributeBO(oauthFormLoginParam.getUsername(), oauthFormLoginParam.getPassword());
        if (null == oauthUserAttribute || StringUtil.isBlank(oauthUserAttribute.getUserId())) {
            log.error("调用 UPMS 接口返回错误信息，用户名：<{}>", oauthFormLoginParam.getUsername());
            throw new OauthApiException("用户名或密码错误", ResponseProduceTypeEnum.HTML, GlobalVariable.DEFAULT_LOGIN_PAGE_PATH);
        }
        return oauthUserAttribute;
    }

    private void buildOauthUserInfoByTokenDTO(OauthUserProfile oauthUserProfile, OauthAccessTokenToRedisBO oauthAccessTokenToRedisBO) {
        OauthUserAttribute oauthUserAttribute = oauthAccessTokenToRedisBO.getUserAttribute();

        if (null != oauthUserAttribute) {
            oauthUserProfile.setUserAttribute(oauthUserAttribute);
            oauthUserProfile.setUsername(oauthUserAttribute.getUsername());
            oauthUserProfile.setName(oauthUserAttribute.getUsername());
            oauthUserProfile.setUserId(oauthUserAttribute.getUserId());
            oauthUserProfile.setId(oauthUserAttribute.getUserId());
        } else {
            // 客户端模式情况下是没有用户信息的
            oauthUserProfile.setUserAttribute(new OauthUserAttribute());
        }

        oauthUserProfile.setIat(oauthAccessTokenToRedisBO.getIat());
        oauthUserProfile.setExp(oauthAccessTokenToRedisBO.getIat() + oauthProperties.getAccessTokenMaxTimeToLiveInSeconds());
        oauthUserProfile.setClientId(oauthAccessTokenToRedisBO.getClientId());
        oauthUserProfile.setGrantType(oauthAccessTokenToRedisBO.getGrantType());
    }

    private String getRedirectUrlWithCode(String redirectUri, String state, String code) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(redirectUri);
        if (StringUtil.containsIgnoreCase(redirectUri, "?")) {
            stringBuilder.append("&");
        } else {
            stringBuilder.append("?");
        }
        stringBuilder.append(GlobalVariable.OAUTH_CODE_RESPONSE_TYPE);
        stringBuilder.append("=");
        stringBuilder.append(code);
        if (StringUtil.isNotBlank(state)) {
            stringBuilder.append("&");
            stringBuilder.append(GlobalVariable.OAUTH_STATE_KEY);
            stringBuilder.append("=");
            stringBuilder.append(state);
        }

        return stringBuilder.toString();
    }

    private String getRedirectUrlWithAccessToken(String redirectUri, OauthToken oauthToken) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(redirectUri);
        stringBuilder.append("#");
        stringBuilder.append(GlobalVariable.OAUTH_ACCESS_TOKEN_KEY);
        stringBuilder.append("=");
        stringBuilder.append(oauthToken.getAccessToken());
        stringBuilder.append("&");
        stringBuilder.append(GlobalVariable.OAUTH_TOKEN_TYPE_KEY);
        stringBuilder.append("=");
        stringBuilder.append(GlobalVariable.OAUTH_TOKEN_TYPE);
        stringBuilder.append("&");
        stringBuilder.append(GlobalVariable.OAUTH_EXPIRES_IN_KEY);
        stringBuilder.append("=");
        stringBuilder.append(oauthProperties.getAccessTokenMaxTimeToLiveInSeconds());
        stringBuilder.append("&");
        stringBuilder.append(GlobalVariable.OAUTH_REFRESH_TOKEN_KEY);
        stringBuilder.append("=");
        stringBuilder.append(oauthToken.getRefreshToken());
        return stringBuilder.toString();
    }

    private void resolveRequestHeader(HttpServletRequest request, OauthClientParam oauthClientParam) {
        String authorizationHeader = request.getHeader(GlobalVariable.HTTP_HEADER_AUTHORIZATION);
        if (StringUtil.isBlank(authorizationHeader)) {
            return;
        }

        if (StringUtil.containsIgnoreCase(authorizationHeader, GlobalVariable.BASIC_AUTH_UPPER_PREFIX)) {
            String replaceIgnoreCase = StringUtil.replaceIgnoreCase(authorizationHeader, GlobalVariable.BASIC_AUTH_UPPER_PREFIX, GlobalVariable.BASIC_AUTH_LOWER_PREFIX);
            authorizationHeader = StringUtil.substringAfter(replaceIgnoreCase, GlobalVariable.BASIC_AUTH_LOWER_PREFIX);
        }
        String basic = CodecUtil.base64DecodeBySafe(authorizationHeader);
        List<String> stringList = StringUtil.split(basic, ":");
        if (stringList.size() < 2) {
            return;
        }
        oauthClientParam.setClientId(stringList.get(0));
        oauthClientParam.setClientSecret(stringList.get(1));

    }
    //=====================================私有方法  end=====================================

}
