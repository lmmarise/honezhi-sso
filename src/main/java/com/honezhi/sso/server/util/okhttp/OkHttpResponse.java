package com.honezhi.sso.server.util.okhttp;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OkHttpResponse {

	private int status;
	private String response;
}
