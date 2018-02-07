package com.socket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.information.Message;
import com.inter.MessageDAO;
import com.user.User;

@Component("socket")
public class webSocket extends TextWebSocketHandler  {
	
	private static final ConcurrentHashMap<Integer,WebSocketSession > map=new ConcurrentHashMap<>();//一个线程安全的支持高并发的HashMap集合类
	
	private Logger log=LoggerFactory.getLogger(TextWebSocketHandler.class);
	
	@Autowired
	private MessageDAO messageDAO;
	
	public void sendMessage(int userId,String message) {
		try {
			Message msg=new Message();
			msg.setUserId(userId);
			msg.setStatus(0);
			msg.setMessage(message);
			messageDAO.add(msg);
			WebSocketSession session=map.get(userId);
			if(null==session) {
				return;
			}
			session.sendMessage(new TextMessage(message));//注意这里的发送数据的方式
		} catch (IOException e) {
			// TODO Auto-generated catch block
			map.remove(userId);
		}
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// TODO Auto-generated method stub
		Object obj=session.getAttributes().get("user");
		log.info("----------------"+obj+"----------------");
		if(null==obj) {
			log.info("目前无连接");
			System.out.println("------"+obj);
			System.out.println("目前无连接");
			return;
		}
		
		User user=(User)obj;
		map.put(user.getId(), session);
		log.info("服务器已连接"+user.getUsername());
		System.out.println("服务器已连接"+user.getUsername());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println(message.getPayload());
		
		session.sendMessage(new TextMessage("world"));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
		Object obj=session.getAttributes().get("user");//注意这里的获取Session属性的方法
		if(null==obj) {
			log.info("目前无连接");
			System.out.println("目前无连接");
			return;
		}
		
		User user=(User)obj;
		map.remove(user.getId());
		log.info("服务器已断开"+user.getUsername());
		System.out.println("服务器已断开"+user.getUsername());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// TODO Auto-generated method stub
		Object obj=session.getAttributes().get("user");
		if(null==obj) {
			log.info("目前无连接");
			System.out.println("目前无连接");
			return;
		}
		
		User user=(User)obj;
		map.remove(user.getId());
		log.info("服务器错误"+user.getUsername());
		System.out.println("服务器已断开"+user.getUsername());
		}
	}
	
	
	
	
	
	


