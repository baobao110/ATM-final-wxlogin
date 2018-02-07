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
		.append(REDIRECT_URI).append("&response_type=code&scope=snsapi_login");//����ע��scope
		return "redirect:"+build.toString();//ע�������ض���,����÷������ڲ���ת����ת��΢��
		return "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";*/
		String getCodeUrl = wx.getCodeUrl();
		return "redirect:" + getCodeUrl;//��һ������΢�ŵ�properties�ļ�����WXloginCallback����
	}
	//����ע��builder��������Ҫ��ע�͵���Ƚ� 
	
	
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
			session.setAttribute("nickname", msg.get("nickname"));//��ȡ�ǳ�
			session.setAttribute("headimgurl", msg.get("headimgurl"));//��ȡͷ���ַ
			session.setAttribute("openid", com.util.AESUtils.encodeMsg(dto.getOpenid().getBytes()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		wxDTO authenDTO = wx.getAccessToken(code);
		ThirdAuthor author = third.save(authenDTO);//ע�������save����������ɨ��ά��ͺ����΢�ŵ�¼��ת���û����ĵķ�����ϵ���������BindLogin������ϵ
		if (null != author.getUserId()) {
			
			User u = user.getId(author.getUserId());
			if (null == u) {
				throw new newException("��֤ʧ��");
			}
			
			session.setAttribute("user", user);
			return "redirect:/user/toUsercenter.do";
		}


		com.wx.WXUserInfoDTO wxInfo = wx.getWxUserInfo(authenDTO);

		session.setAttribute("nickname", wxInfo.getNickname());
		session.setAttribute("headimgurl", wxInfo.getHeadimgurl());
		session.setAttribute("openid", AESUtils.encodeMsg(wxInfo.getOpenid().getBytes()));
		return "redirect:/WX/toblind.do";//���ﲻ�ӻ����302����,���Ӱ����������403����
		
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
		 * ����������callback������ϵ��֪����΢��ɨ��ʱ�����ݿ��Ѿ��������ֻ��������ȱ��userId��
		 * ���ﵱ�����û�������������û����������ҵ����û���Ϣ���û����е�userId���뵽΢�����ݱ��С�
		 * �����ͽ��û����΢�ű���ϵ����ͨ��userId
		 */
		
		session.setAttribute("user", user);
		return ajaxDAO.success();
	}
	
}
//������Ҫע�����ص��������Ա����ڰ������м���tologin��login����url��ַ
