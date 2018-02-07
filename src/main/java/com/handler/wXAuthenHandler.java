package com.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.util.ApiConnector;
import com.wx.WXUserInfoDTO;
import com.wx.wxDTO;

@Component
public class wXAuthenHandler {
	
private static final Logger log = LoggerFactory.getLogger(wXAuthenHandler.class);
	
	@Value("${wx_appId}")
	private String appId;
	
	@Value("${wx_redirectUrl}")
	private String redirectUrl;
	
	@Value("${wx_secret}")
	private String secret;
	
	@Value("${wx_codeUrl}")
	private String codeUrl;
	
	@Value("${wx_accessTokenUrl}")
	private String accessTokenUrl;
	
	@Value("${wx_wxUserInfoUrl}")
	private String wxUserInfoUrl;//注意这里的注入详细见spring-config
	
	public String getCodeUrl() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(codeUrl).append("?").append("appid=").append(appId)
				.append("&").append("redirect_uri=").append(redirectUrl).append("&").append("response_type=code")
				.append("&").append("scope=snsapi_login");

		return builder.toString();
	}
	
	public wxDTO getAccessToken(String code) {
			
			StringBuilder builder = new StringBuilder();
			builder.append(accessTokenUrl).append("?").append("appid=").append(appId)
			.append("&").append("secret=").append(secret).append("&").append("code=").append(code)
			.append("&").append("grant_type=authorization_code");
			
			String result = ApiConnector.get(builder.toString(), null);
			log.info(">>>>>>>>>accessToken result:" + result);
			
			wxDTO authenDTO = JSON.parseObject(result,wxDTO.class);
			
			return authenDTO;
	
		}
	
	public WXUserInfoDTO getWxUserInfo(wxDTO authenDTO) {
		
		String result = ApiConnector.get(wxUserInfoUrl + "?access_token="
				+ authenDTO.getAccess_token() + "&openid=" + authenDTO.getOpenid(), null);
		log.info(">>>>>>>>>用户微信信息:" + result);

		WXUserInfoDTO wxInfo = JSON.parseObject(result, WXUserInfoDTO.class);
		log.info(">>>>>>>>>>>nickname=" + wxInfo.getNickname());
		
		return wxInfo;
		
	}

}
