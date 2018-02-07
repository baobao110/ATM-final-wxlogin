package com.control;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ajax.ajaxDAO;
import com.alibaba.fastjson.JSON;
import com.exception.newException;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;
import com.handler.wXAuthenHandler;
import com.service.ThirdAuthorService;
import com.service.UserService;
import com.thirdauthor.ThirdAuthor;
import com.user.User;
import com.util.AESUtils;
import com.wx.wxDTO;

@Controller
@RequestMapping("/WX")
public class WXControl {
	
	@Autowired
	private ThirdAuthorService third;
	
	@Autowired
	private wXAuthenHandler wx;
	
	@Autowired
	private UserService user;
	
	private  Logger log=LoggerFactory.getLogger(WXControl.class);
	
	@RequestMapping("/toWXlogin")
	public String toWXlogin() {
		/*StringBuilder build=new StringBuilder();
		build.append("https://open.weixin.qq.com/connect/qrconnect?appid=");
		build.append("http://payhub.dayuanit.com/weixin/connect/qrconnect.do?")
		.append("appid=").append(APPID).append("&redirect_uri=")
		.append(REDIRECT_URI).append("&response_type=code&scope=snsapi_login");//这里注意scope
		return "redirect:"+build.toString();//注意这里重定向,如果用服务器内部跳转会跳转到微信
		return "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";*/
		String getCodeUrl = wx.getCodeUrl();
		return "redirect:" + getCodeUrl;//下一步根据微信的properties文件进入WXloginCallback方法
	}
	//这里注意builder的内容需要和注释的相比较 
	
	
	@RequestMapping("/WXloginCallback")
	public String WXloginCallback(String code,HttpServletRequest request,HttpSession session) {
		/*log.info("<<<<<<<<<<<<code="+code);
		StringBuilder build=new StringBuilder();
		build.append("http://payhub.dayuanit.com/weixin/sns/oauth2/access_token.do?appid=")
		.append(APPID)
		.append("&secret=05666884059118082634851249940491&code=")
		.append(code).append("&grant_type=authorization_code");
		HttpGet get=new HttpGet(build.toString());
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(get);
			String result=EntityUtils.toString(httpResponse.getEntity());
			log.info("<<<<<result"+result);
			wxDTO dto=JSON.parseObject(result, wxDTO.class);
			log.info("<<<<<dto"+dto.getAccess_token());
			ThirdAuthor author=third.save(dto);
			HttpGet userInfo=new HttpGet("http://payhub.dayuanit.com/weixin/sns/userinfo.do?access_token="+dto.getAccess_token()+"&openid="+dto.getOpenid());
			httpResponse=httpClient.execute(userInfo);
			result=EntityUtils.toString(httpResponse.getEntity());
			log.info("<<<<<result<<<<<<<"+result);
			 dto=JSON.parseObject(result, wxDTO.class);
			 Map<String,String> msg=JSON.parseObject(result, Map.class);
			session.setAttribute("nickname", msg.get("nickname"));//获取昵称
			session.setAttribute("headimgurl", msg.get("headimgurl"));//获取头像地址
			session.setAttribute("openid", com.util.AESUtils.encodeMsg(dto.getOpenid().getBytes()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		wxDTO authenDTO = wx.getAccessToken(code);
		ThirdAuthor author = third.save(authenDTO);//注意这里的save方法发生在扫二维码和后面的微信登录跳转到用户中心的方法联系就是下面的BindLogin方法联系
		if (null != author.getUserId()) {
			
			User u = user.getId(author.getUserId());
			if (null == u) {
				throw new newException("认证失败");
			}
			
			session.setAttribute("user", user);
			return "redirect:/user/toUsercenter.do";
		}


		com.wx.WXUserInfoDTO wxInfo = wx.getWxUserInfo(authenDTO);

		session.setAttribute("nickname", wxInfo.getNickname());
		session.setAttribute("headimgurl", wxInfo.getHeadimgurl());
		session.setAttribute("openid", AESUtils.encodeMsg(wxInfo.getOpenid().getBytes()));
		return "redirect:/WX/toblind.do";//这里不加会出现302错误,不加白名单还会出403错误
		
		/*return "bindLogin";*/
		/*return "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";*/
	}
	
	@RequestMapping("/toblind")
	public String toBlind() {
		return "bindLogin";
	}
	
	@RequestMapping("/wxLogin")
	@ResponseBody
	public ajaxDAO BindLogin(String username,String password,HttpSession session) {
		User user = third.bindUser(username, password, (String)session.getAttribute("openid"));
		/*
		 * 这里和上面的callback方法联系就知道在微信扫码时在数据库已经添加数据只是数据中缺少userId，
		 * 这里当填完用户名和密码后在用户表查找如何找到该用户信息将用户表中的userId放入到微信数据表中。
		 * 这样就将用户表和微信表联系起来通过userId
		 */
		
		session.setAttribute("user", user);
		return ajaxDAO.success();
	}
	
}
//这里需要注意拦截的问题所以必须在白名单中加入tologin和login两个url地址
