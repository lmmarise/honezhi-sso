package com.honezhi.sso.server.pojo.dto.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@ApiModel(description= "授权码模式需要的参数")
public class OauthAuthorizeParam {
	@ApiModelProperty(value = "响应类型")
	private String responseType;
	@ApiModelProperty(value = "client端Id")
	private String clientId;
	@ApiModelProperty(value = "重定向地址")
	private String redirectUri;
	@ApiModelProperty(value = "状态")
	private String state;
}
