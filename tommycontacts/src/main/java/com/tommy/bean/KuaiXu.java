package com.tommy.bean;

import com.lidroid.xutils.db.annotation.Id;

public class KuaiXu {
	@Id
	private int id;		
	private int number;
	private String phone;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
