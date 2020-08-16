package com.honezhi.sso.server.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@ApiModel(description= "token信息")
public class OauthToken implements Serializable {

	private static final long serialVersionUID = 7975415790497139511L;

	@ApiModelProperty(value = "用户名")
	private String accessToken;
	@ApiModelProperty(value = "token类型")
	private String tokenType;
	@ApiModelProperty(value = "过期时间")
	private Integer expiresIn;
	@ApiModelProperty(value = "刷新token")
	private String refreshToken;

}
