package com.pool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.socket.webSocket;

@Component
public class ThreadRun2 {
	
	@Autowired
	private webSocket socket;
	
	private Boolean flag=true;
	
	private  LinkedBlockingQueue<Information> queue=new LinkedBlockingQueue<>();
	
	
	public void send(int userId,String message) {
		queue.offer(new Information(userId, message));
		while(flag) {
			synchronized (this) {
				 sendMessage();
			}
		}
	}
	
	public void sendMessage() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						Information a=queue.take();
						socket.sendMessage(a.getUserId(),a.getMessage());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		}).start();
	}
}

class Information{
	
	private int userId;
	
	private String message;

	public Information(int userId, String message) {
		this.userId = userId;
		this.message = message;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
