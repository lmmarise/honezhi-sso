package com.honezhi.sso.server.pojo.dto.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@ApiModel(value = "授权码模式表单登录需要的参数")
public class OauthTokenParam extends OauthClientParam {
	@ApiModelProperty(value = "token类型")
	private String grantType;
	@ApiModelProperty(value = "code码")
	private String code;
	@ApiModelProperty(value = "刷新的token")
	private String refreshToken;
	@ApiModelProperty(value = "重定向地址")
	private String redirectUri;

	@ApiModelProperty(value = "用户名")
	private String username;
	@ApiModelProperty(value = "密码")
	private String password;
}
