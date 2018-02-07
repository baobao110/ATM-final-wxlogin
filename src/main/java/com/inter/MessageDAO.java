package com.inter;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

import com.AccountFlow.Account;
import com.information.Message;

public interface MessageDAO {
	public int add(Message msg);
	
	public ArrayList<Message> detail(@Param("userId")int userId);
	
	public int totalNumber(@Param("userId")int userId);
	
	public ArrayList<Message> List(@Param("userId")int userId,@Param("currentNumber")int currentNumber,@Param("move")int move);
	
	public int update(int userId);
}
