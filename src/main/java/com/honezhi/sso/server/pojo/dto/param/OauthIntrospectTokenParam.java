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
public class OauthIntrospectTokenParam extends OauthClientParam {

	@ApiModelProperty("token码")
	private String token;
	@ApiModelProperty("默认是access_token类型")
	private String tokenTypeHint;

}
