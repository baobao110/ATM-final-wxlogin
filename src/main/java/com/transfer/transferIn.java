package com.transfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter.HistoryDAO;
import com.record.history;
import com.service.Service;
import com.user.User;

@Component("In")	
public class transferIn {
	
	@Autowired
	private HistoryDAO historyDao;
	
	@Autowired
	private Service service;
	
	private Logger log=LoggerFactory.getLogger(transferIn.class);
	
	public void work() {
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -1);//时间操作这里用Calendar的方法这里需要注意add()的两个参数
		
		Date date= cal.getTime();

		ArrayList<history> record=historyDao.recordback(date);
		
		if(null==record||record.isEmpty()) {
			log.info("近期无转账");
		}
		
		else {
			ArrayList<history> back=new ArrayList<>();
			for(history i:record) {
				if(i.getStatus()==1||i.getStatus()==2) {
					log.info("=====================================加载===============================================");
					back.add(i);
				}
			}
			log.info("查记录");
			for(history i:back) {
				service.In(date,i.getId());
			}
		}
	}
}

/*
 * 这里需要注意的就是Component的注解不然spring无法找到,
 * 注解和spring-task中的配置一样,还有就是事务不能开在这里不然会出现只要一笔不成功所有流水都回滚
 */
	