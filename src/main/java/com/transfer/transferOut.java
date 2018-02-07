package com.transfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter.HistoryDAO;
import com.record.history;
import com.service.Service;

@Component("Out")
public class transferOut {
	
	@Autowired
	private HistoryDAO historyDao;
	
	@Autowired
	private Service service;
	
	private Logger log=LoggerFactory.getLogger(transferOut.class);
	
	public void work() {
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -1);//时间操作这里用Calendar的方法这里需要注意add()的两个参数
		
		Date date= cal.getTime();

		ArrayList<history> record=historyDao.record(0,date);
		
		if(null==record||record.isEmpty()) {
			log.info("近期无转账");
		}
		
		else {
			for(history i:record) {
				service.Out(date,i.getId());
			}
		}
	}
	
	/*
	 * 注意这里的集合的查询,这里需要注意是在进行集合的判别时,
	 * 特别要注意集合的空值问题,这里的集合判断不是用null而是用isEmpty()方法,
	 * 这一点和之前的文件下载出现同样的问题如果文件下载时不用isEmpty()就会一直的下载
	 * 这一点是之前就遇到的以后判断时遇到集合判断为空时需要用isEmpty()的方法
	 */
}
