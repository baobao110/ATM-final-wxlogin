package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exception.newException;
import com.inter.ThirdDAO;
import com.thirdauthor.ThirdAuthor;
import com.user.User;
import com.util.AESUtils;
import com.wx.wxDTO;

@Service
public class ThirdAuthorService {
	
	@Autowired
	private ThirdDAO third;
	
	@Autowired
	private UserService user;
	
	
	public ThirdAuthor save(wxDTO dto) {
		
		ThirdAuthor author = third.getAuthor(dto.getOpenid());
		if(null!=author) {
			author.setAcceccToken(dto.getAccess_token());
			author.setExpiresIn(dto.getExpires_in());
			author.setOpenid(dto.getOpenid());
			author.setRefreshToken(dto.getRefresh_token());
			author.setUserId(null);
			int row=third.update(author);
			if(1!=row) {
				throw new newException("注册失败");
			}
		}
		else {
			author=new ThirdAuthor();
			author.setAcceccToken(dto.getAccess_token());
			author.setExpiresIn(dto.getExpires_in());
			author.setOpenid(dto.getOpenid());
			author.setRefreshToken(dto.getRefresh_token());
			author.setUserId(null);//这里先不设置id为什么见后面builduser的用法
			int row=third.add(author);
			if(1!=row) {
				throw new newException("登录失败");
			}
		}
		return author;
		
	}
	
	public ThirdAuthor queryAuthor(String openid) {
		return third.getAuthor(openid);
	}
	
	public User bindUser(String username, String password, String openid) {
		
		try {
			openid = AESUtils.decodeMsg(openid.getBytes());
			System.out.println(">>>>解密之后openid" + openid);
		} catch (Exception e) {
			throw new newException("解密失败");
		}
		
		ThirdAuthor author = third.getAuthor(openid);
		if (null == author) {
			throw new newException("认证失败");
		}
		
		User u = user.login(username, password);//这里根据用户名和password获取user信息
		
		author.setUserId(u.getId());
		
		int rows = third.update(author);//注意这里的setId和update方法联系前面的save()方法
		if (1 != rows) {
			throw new newException("认证失败");
		}
		
		return u;
		
	}
	

}
