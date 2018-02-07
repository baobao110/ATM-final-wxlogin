package com.inter;

import java.util.ArrayList;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.record.history;

public interface HistoryDAO {
	
	public int add(history a);
	
	public ArrayList<history> record (@Param("status") int status, @Param("createtime")Date createtime);
	
	public ArrayList<history> recordback  (@Param("createtime")Date createtime);
	
	public history pick(int id);
	
	public int set(@Param("status") int status,@Param("id") int id);
}
