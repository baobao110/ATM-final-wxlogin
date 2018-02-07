package com.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.BankCard.Card;

@Service
public class redisService {
	
	@Autowired
	private RedisTemplate<String, String> template;
	
	 @Resource(name="redisTemplate")
	private ValueOperations<String, List<Card>> card;//注意这里的Card要继承序列化,这里的redisTemplate就是配置文件中对应的id
	 
	 public List<Card>List(String username){
		 String key="card="+username;
		 return  card.get(key);
	 }
	 
	 public void Set(String username,List<Card> cad) {
		 String key="card="+username;
		 card.set(key, cad);
	 }
	 
	 
	 public void del(String username) {
		 String key="card="+username;
		template.delete(key);
	 }
}
//注意这里的redis的方法分别为保存缓存 添加缓存  删除缓存  这里尤其要注意删除缓存
