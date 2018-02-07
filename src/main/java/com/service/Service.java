package com.service;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.AccountFlow.Account;
import com.BankCard.Card;
import com.base.DataBase;
import com.exception.newException;
import com.fenye.Fenye;
import com.flow.flow;
import com.information.Message;
import com.inter.AccountDAO;
import com.inter.CardDAO;
import com.inter.HistoryDAO;
import com.inter.MessageDAO;
import com.inter.UserDAO;
import com.pool.ThreadRun2;
import com.record.history;
import com.user.User;
import com.util.Dateformate;



@Component	//这里是Spring的注入运行时Spring会自动扫描该注入的作用域,这里是配合Control层使用的
@Transactional	//这里的注入是用于spring和mybaties结合时的事务管理,它等同于mybaties中的sqlSession的connection,这里不需要手动的开启和关闭事务
public class Service {
	
	@Autowired
	private redisService redis;
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private CardDAO cardDao;
	
	@Autowired
	private AccountDAO accountDao;
	
	@Autowired
	private HistoryDAO historyDao;
	
	@Autowired
	private MessageDAO messageDao;
	
	@Autowired
	private ThreadRun2 thread;

	private Logger log=LoggerFactory.getLogger(Service.class);
	
	@Transactional(rollbackFor=Exception.class)
	public Card open(String username,String password) {
		redis.del(username);
		Card card=new Card();
		card.setNumber(DataBase.CreateNumber());
		card.setPassword(DigestUtils.md5Hex(password));
		card.setMoney(0);
		card.setUsername(username);
		System.out.println(card);
		if(cardDao.open(card)==1) {//这里调用Card.xml类中的SQL语句
			return card;
			}
		else {
			throw new newException("开户失败");
		}
	}
	//注意这里的异常处理如果是Exception就回滚,如果是自定义的RuntimeException异常就抛到Control层,通过try catch捕获最后将错误信息以ajax方式在前端以页面显示
	//注意这里的del方法如果步删除缓存开户后不会显示开户的银行卡,这里是因为展示银行卡时先走redis缓存,如果缓存有就不会走数据库,但是缓存保存的是开户前的信息,所以不会出现新银行卡 
	@Transactional(rollbackFor=Exception.class)
	public void save(int number,String password,double money,String username)  {
			Card card=cardDao.GetCad(number);//这里可以和之前的使用mybaties的获取事务进行比较就可以发现用spring+mybaties的优点,这里自动创建对象
			if(null==card) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			if(money<0) {
				log.info("金额小于零");
				throw new newException("金额小于零");
			}
			double x=card.getMoney();
			x+=money;
			if(cardDao.modifyMoney(number, x)!=0) {
				log.info("存钱成功");	
			}
			Account account=new Account();
			account.setNumber(number);
			account.setMoney(money);
			account.setType(1);
			account.setDescription("存钱");
			account.setUsername(username);
			accountDao.add(account);
			log.info("流水账产生");
		}
		
	@Transactional(rollbackFor=Exception.class)
	public void draw(int number,String password,double money,String username) {
			Card card=cardDao.GetCad(number);
			if(null==card) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			if(money<0) {
				log.info("金额小于零");
				throw new newException("金额小于零");
			}
			double x=card.getMoney();
			if(money>x) {
				log.info("金额不足");
				throw new newException("金额不足");
			}
			x-=money;
			if(cardDao.modifyMoney(number, x)!=0) {
				log.info("取钱成功");
			}
			Account account=new Account();
			account.setNumber(number);
			account.setMoney(money);
			account.setType(1);
			account.setDescription("取钱");
			account.setUsername(username);
			accountDao.add(account);
			log.info("流水账产生");
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void transfer(int OutNumber,String password,double money,int InNumber,String username) {
			Card card1=cardDao.GetCad(OutNumber);
			if(null==card1) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
				
			}
			if(!DigestUtils.md5Hex(password).equals(card1.getPassword())) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			if(money<0) {
				log.info("金额小于零");
				throw new newException("金额小于零");
			}
			double x=card1.getMoney();
			if(money>x) {
				log.info("金额不足");
				throw new newException("金额不足");
			}
			x-=money;
			if(cardDao.modifyMoney(OutNumber, x)!=0) {
				log.info("取钱成功");
			}
			Account account1=new Account();
			account1.setNumber(OutNumber);
			account1.setMoney(money);
			account1.setType(1);
			account1.setDescription("取钱");
			account1.setUsername(username);
			accountDao.add(account1);
			log.info("流水账产生");
			history a=new history();
			a.setInNumber(InNumber);
			a.setMoney(money);
			a.setOutNumber(OutNumber);
			a.setStatus(0);
			historyDao.add(a);
			
		/*	
			Card card2=cardDao.GetCad(InNumber);
			if(null==card2) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
				
			}
			double y=card2.getMoney();
			y+=money;
			if(cardDao.modifyMoney(InNumber, y)!=0) {
				log.info("转入成功");	
			}
			Account account2=new Account();
			account2.setNumber(InNumber);
			account2.setMoney(money);
			account2.setType(1);
			account2.setDescription("存钱");
			account2.setUsername(username);
			accountDao.add(account2);
			log.info("流水账产生");*/
		}//这里注意cardDao的使用
	
	
	@Transactional(rollbackFor=Exception.class)
	public void Out(Date date,int id) {
		history record=historyDao.pick(id);
		int InNumber=record.getInNumber();
		Card card=cardDao.GetCad(InNumber);
		if(null==card) {
			log.info("账号或者密码不存在");
			historyDao.set(2, id);
		}
		else {
			log.info("账号存在");
			historyDao.set(1, id);
		}
	}
	
	public User get(HttpSession session,HttpServletRequest req) {
		session=req.getSession();
		User user=(User)session.getAttribute("user");
		return user;
	}
	@Transactional(rollbackFor=Exception.class)
	public void In(Date date,int id) {
		history record=historyDao.pick(id);
		int i=record.getStatus();
		if(i==2) {
			log.info("账号或者密码不存在");
			historyDao.set(3, id);
			int OutNumber=record.getOutNumber();
			Card card=cardDao.GetCad(OutNumber);
			double money=card.getMoney();
			double account=record.getMoney();
			money+=account;
			cardDao.modifyMoney(OutNumber, money);
			Account account2=new Account();
			account2.setNumber(OutNumber);
			account2.setMoney(account);
			account2.setType(3);
			account2.setDescription("取消");
			accountDao.add(account2);
			log.info("取消");
		}
		if(i==1) {
			int InNumber=record.getInNumber();
			Card card=cardDao.GetCad(InNumber);
			double money=card.getMoney();
			double account=record.getMoney();
			money+=account;
			cardDao.modifyMoney(InNumber, money);
			record.setStatus(4);
			Account account2=new Account();
			account2.setNumber(InNumber);
			account2.setMoney(account);
			account2.setType(2);
			account2.setDescription("转入");
			accountDao.add(account2);
			historyDao.set(4, id);
			Card cad=cardDao.GetCad(InNumber);
			String username=cad.getUsername();
			User user=userDao.getUser(username);
			Message msg=new Message();
			msg.setStatus(0);
			msg.setUserId(user.getId());
			msg.setMessage("转入"+record.getMoney()+"元");
			messageDao.add(msg);
			thread.send(user.getId(),"转入"+record.getMoney()+"元" );
			log.info("成功");
		}
	}//这里又一个坑就是无论失败还是成功都要修改状态数,尤其是成功时如果不修改,它会一直执行转出操作
	
	@Transactional(rollbackFor=Exception.class)
	public Card ChangePassword(int number,String oldPassword,String newPassword) {
			Card a=cardDao.GetCad(number);
			if(null==a) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			if(!DigestUtils.md5Hex(oldPassword).equals(a.getPassword())) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			if(cardDao.modifyPassword(number, DigestUtils.md5Hex(newPassword))!=0) {
				a=cardDao.GetCad(number);
				return a;
			}
			else {
				 throw new newException("账号或者密码不存在");
			}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Fenye List(int number, String password, int currentPage) {
		Card card=cardDao.GetCad(number);
		if(null==card) {
			log.info("账号或者密码不存在");
			throw new newException("账号或者密码不存在");
		}
		if(!DigestUtils.md5Hex(password).equals(card.getPassword())) {
			log.info("账号或者密码不存在");
			throw new newException("账号或者密码不存在");
		}
		int totalNumber=accountDao.totalNumber(number);//调用totalNumber方法获取总纪录数目
		Fenye fenye=new Fenye(totalNumber, currentPage);//初始化，参数为总记录和当前页数
		 ArrayList<Account>list=accountDao.List(number, fenye.getcurrentNumber(), fenye.move);//调用List()方法获取该number的该页内容
		 ArrayList<flow> account=new ArrayList<>(list.size());
			for(Account i:list) {
				flow a=new flow();
				a.setId(i.getId());
				a.setNumber(i.getNumber());
				a.setMoney(i.getMoney());
				a.setType(i.getType());
				a.setCreatetime(Dateformate.formate(i.getCreatetime()));
				a.setDescription(i.getDescription());
				account.add(a);
			}
		 fenye.setObject(account);//将获取的记录保存
		 if(null==fenye) {
			 throw new newException("数据为空");
		 }
		 else {
			 return fenye;
		 }
	}
	
	@Transactional(rollbackFor=Exception.class)
	public int total(int number) {
		return accountDao.totalNumber(number);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Card GetCard(int number) {
			Card a=cardDao.GetCad(number);
			if(null==a) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			else {
				return a;
			}
		}
	
	@Transactional(rollbackFor=Exception.class)
	public Card Get(int number,String password) {
			Card a=cardDao.GetCard(number,DigestUtils.md5Hex(password));
			if(null==a) {
				log.info("账号或者密码不存在");
				throw new newException("账号或者密码不存在");
			}
			else {
				return a;
			}
		}
	
	@Transactional(rollbackFor=Exception.class)//作用处理检查型异常
	public void delete(int number) {
			int i=cardDao.delete(number);
			System.out.println(i);
			if(i==0) {
				log.info("账号或者密码不存在");
			}
	}
	
	public ArrayList<flow> ten(String username){
		System.out.println("server username"+username);
		ArrayList<Account> account=accountDao.ten(username);
		ArrayList<flow> list=new ArrayList<>(account.size());
		for(Account i:account) {
			flow a=new flow();
			a.setId(i.getId());
			a.setNumber(i.getNumber());
			a.setMoney(i.getMoney());
			a.setType(i.getType());
			a.setCreatetime(Dateformate.formate(i.getCreatetime()));
			a.setDescription(i.getDescription());
			list.add(a);
		}
		return list;
	}
}
/*
 * 什么是检查型异常,什么是非检查型异常,这里的区分方法是:
 * 1 检查型异常继承于Exception 非检查型异常继承于RuntimException或者Error
 * 2 检查型异常必须捕获,但是非检查型异常可以不捕获
 * /
 */