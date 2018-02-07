package com.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

import com.socket.webSocket;

public class ThreadRun {
	
	@Autowired
	private webSocket socket;
	
	private static final ExecutorService service=Executors.newSingleThreadScheduledExecutor();
	
	public void send(int userId,String message) {
		service.execute(new Runnable() {

			@Override
			public void run() {
				socket.sendMessage(userId, message);
				
			}
			
		});
	}
}
