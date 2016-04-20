package com.example.pumpkinonlinedisk.bean;

public class Flag {
	/*
	 * isDownload 是否暂停
	 * id id号
	 * num 在链表中的第几个
	 */
	private boolean isDownload=false;
	int id;
	int num;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public boolean isDownload() {
		return isDownload;
	}
	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
