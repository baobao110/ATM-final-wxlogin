package com.record;

import java.util.Date;

public class history {
	
	private int id;
	
	private int InNumber;
	
	private int OutNumber;
	
	private int status;
	
	private Date createtime;
	
	private double money;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getInNumber() {
		return InNumber;
	}

	public void setInNumber(int inNumber) {
		InNumber = inNumber;
	}

	public int getOutNumber() {
		return OutNumber;
	}

	public void setOutNumber(int outNumber) {
		OutNumber = outNumber;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
	
	

}
