package com.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BankCard.Card;
import com.fenye.Fenye;
import com.information.Message;
import com.information.Msg;
import com.information.notice;
import com.inter.MessageDAO;
import com.message.messageDTO;

@Service
public class messageService {

	@Autowired
	private MessageDAO msg;
	
	public messageDTO unRead(int userId){
		ArrayList<Message> message=msg.detail(userId);
		ArrayList<notice>msg=new ArrayList<>(message.size());
		for(Message i:message) {
			notice a=new notice();
			a.setCreatetime(i.getCreatetime().getTime());
			a.setMessage(i.getMessage());
			a.setStatus(i.getStatus());
			a.setUserId(i.getUserId());
			msg.add(a);
		}
		return messageDTO.success(msg.size(),msg);
	}
	
	public Fenye List(int userId,int currentPage) {
		int totalNumber=msg.totalNumber(userId);
		Fenye fenye=new Fenye(totalNumber, currentPage);
		 ArrayList<Message>list=msg.List(userId, fenye.getcurrentNumber(), fenye.move);
		 ArrayList<Msg>flow=new ArrayList<>(list.size());
		 for(Message i:list) {
			 Msg a=new Msg();
			 a.setId(i.getId());
			 a.setMessage(i.getMessage());
			 a.setStatus(i.getStatus());
			 a.setUserId(i.getUserId());
			 SimpleDateFormat form=new SimpleDateFormat("yyyy-MM-dd HH:MM;ss");
			 String str=form.format(i.getCreatetime());
			 a.setCreatetime(str);
			 flow.add(a);
		 }
		 msg.update(userId);
		 fenye.setObject(flow);
		 return fenye;
	}

}
