package com.example.pumpkinonlinedisk.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DownAndUpLoadInfo implements Serializable{
	/*
	 * id id号
	 * name 文件名称
	 * finished 当前上传或下载长度
	 * isStart 当点击开始后设置不能点击，
	 */
	
	private int id;
	//文件名字
	private String name;
	//文件大小
	private int total;
	
	private long filesize;
 
	private static String URL;

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public DownAndUpLoadInfo() {
		super();
	}

	public DownAndUpLoadInfo(String name, long filesize ,int total,String URL,int a) {
		super();
		this.name = name;
		this.filesize = filesize;
		this.total = total;
		this.URL = URL;
		this.aa = a;
	}
	private int aa; //1本地 2网络

	public int getAa() {
		return aa;
	}

	public void setAa(int aa) {
		this.aa = aa;
	}
	private int finished;
	private boolean isStart=false;
	//判断是否在本地已经有了，有的话就不能再下载
	private boolean localhad = false;
	
	public boolean isLocalhad() {
		return localhad;
	}

	public void setLocalhad(boolean localhad) {
		this.localhad = localhad;
	}

	//判断是文件还是文件夹
	/**
	 * 0为文件，1为文件夹
	 * 默认为文件
	 */
	private int type = 0;
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	
	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
 
}
