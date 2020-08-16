package com.honezhi.sso.server.pojo.dto.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@ApiModel(description= "授权码模式表单登录需要的参数")
public class OauthFormLoginParam extends OauthAuthorizeParam {
	@ApiModelProperty(value = "用户名")
	private String username;
	@ApiModelProperty(value = "密码")
	private String password;
	@ApiModelProperty(value = "记住我")
	private Boolean boolIsRememberMe;
}
