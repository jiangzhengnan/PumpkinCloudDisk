package com.example.pumpkinonlinedisk.bean;

public class UpLoadInfo {
	private String filename;
	private long filesize;
	public UpLoadInfo(String filename, long filesize) {
		super();
		this.filename = filename;
		this.filesize = filesize;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
}
