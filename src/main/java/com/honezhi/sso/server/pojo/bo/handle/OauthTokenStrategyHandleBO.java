package com.honezhi.sso.server.pojo.bo.handle;

import com.honezhi.sso.server.pojo.dto.OauthUserAttribute;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OauthTokenStrategyHandleBO {
	private String userInfoRedisKey;
	private OauthUserAttribute userAttribute;
}
