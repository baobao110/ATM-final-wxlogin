package com.control;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ajax.ajaxDAO;
import com.fenye.Fenye;
import com.information.Message;
import com.inter.MessageDAO;
import com.message.messageDTO;
import com.service.messageService;
import com.user.User;

@Controller
@RequestMapping("/message")
public class messageControl {
	
	@Autowired
	private messageService msg;
	
	
	private  Logger log=LoggerFactory.getLogger(messageControl.class);
	
	@RequestMapping("/Information")
	@ResponseBody
	public messageDTO message(HttpSession session,HttpServletRequest req) {
		log.info("------------------------------------------");
		session=req.getSession();
		Object obj=session.getAttribute("user");
		if(null==obj) {
			return null;
		}
		User user=(User)obj;
		int id=user.getId();
		log.info("----------------------------------------------------"+id+"-------------------------------------");
		return msg.unRead(id);
	}
	
	@RequestMapping("/toMessageList")
	public String toMessageList() {
		return "message";
	}
	
	@RequestMapping("/list")
	@ResponseBody
	public ajaxDAO list(HttpSession session,@RequestParam("currentPage")String currentPage,HttpServletRequest req) throws ServletException, IOException {
				session=req.getSession();
				Object obj=session.getAttribute("user");
				currentPage=currentPage ==null ? "1" : currentPage;
				User user=(User)obj;
				int id=user.getId();
				try {
					Fenye list = msg.List(id, Integer.parseInt(currentPage));
					log.info("totalPage"+list.getTotalPage());
					return ajaxDAO.success(list);
				}
				catch(Exception e) {
					return ajaxDAO.failure(e.getMessage());
				}
		}

}