package com.honezhi.sso.server.pojo.bo.cache;

import com.honezhi.sso.server.pojo.dto.OauthUserAttribute;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OauthAccessTokenToRedisBO extends OauthTokenToRedisBO implements Serializable {

	private static final long serialVersionUID = 4489154215852194179L;

	private OauthUserAttribute userAttribute;


}
