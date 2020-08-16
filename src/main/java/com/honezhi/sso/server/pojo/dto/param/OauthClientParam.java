package com.honezhi.sso.server.pojo.dto.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@ApiModel(description= "client的凭证")
public class OauthClientParam {

	@ApiModelProperty(value = "client的id")
	private String clientId;
	@ApiModelProperty(value = "client的密钥")
	private String clientSecret;

}
