package com.service;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.BankCard.Card;
import com.control.cardControl;
import com.fenye.Fenye;
import com.inter.CardDAO;



@Component	//这里加注解是为了方便Control中的变量调用
@Transactional
public class CardService {
	
	@Autowired
	private CardDAO cardDao;
	
	@Autowired
	private  redisService redis;
	
	private  Logger log=LoggerFactory.getLogger(CardService.class);
	
	public Fenye List(int number,int currentPage) {
		int totalNumber=cardDao.totalNumber(number);
		Fenye fenye=new Fenye(totalNumber, currentPage);
		 ArrayList<Card>list=cardDao.List(number, fenye.getcurrentNumber(), fenye.move);
		 fenye.setObject(list);//将获取的记录保存
		 log.info(">>>>>>>>"+fenye);
		 return fenye;
	}
	
	public Fenye list(String username,int currentPage) {
		int totalNumber=cardDao.total(username);
		System.out.println(totalNumber);
		Fenye fenye=new Fenye(totalNumber, currentPage);
		 ArrayList<Card>list=cardDao.list(username, fenye.getcurrentNumber(), fenye.move);
		 fenye.setObject(list);//将获取的记录保存
		 return fenye;
	}
	
	public  ArrayList<Card> flow(String username) {
		 ArrayList<Card>list=(ArrayList<Card>) redis.List(username);
		 if(null==list||list.isEmpty()) {
			list=cardDao.flow(username);
			 redis.Set(username, list);
		 }
		 return list;
	}
	//注意这里的用redis缓存数据这里用缓存的方式保存数据如果不存在就添加缓存,保存银行卡,但是这样做也有一定的弊端,具体见开户的Control方法
	
	
}
