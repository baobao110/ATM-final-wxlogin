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
				throw new newException("ע��ʧ��");
			}
		}
		else {
			author=new ThirdAuthor();
			author.setAcceccToken(dto.getAccess_token());
			author.setExpiresIn(dto.getExpires_in());
			author.setOpenid(dto.getOpenid());
			author.setRefreshToken(dto.getRefresh_token());
			author.setUserId(null);//�����Ȳ�����idΪʲô������builduser���÷�
			int row=third.add(author);
			if(1!=row) {
				throw new newException("��¼ʧ��");
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
			System.out.println(">>>>����֮��openid" + openid);
		} catch (Exception e) {
			throw new newException("����ʧ��");
		}
		
		ThirdAuthor author = third.getAuthor(openid);
		if (null == author) {
			throw new newException("��֤ʧ��");
		}
		
		User u = user.login(username, password);//��������û�����password��ȡuser��Ϣ
		
		author.setUserId(u.getId());
		
		int rows = third.update(author);//ע�������setId��update������ϵǰ���save()����
		if (1 != rows) {
			throw new newException("��֤ʧ��");
		}
		
		return u;
		
	}
	

}
