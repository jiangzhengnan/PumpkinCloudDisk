package com.example.pumpkinonlinedisk;

import android.os.Environment;

public class Config {

	
	public static String username ;
	// 开始下载
	public static final String ACTION_START = "ACTION_START";
	// 停止下载
	public static final String ACTION_STOP = "ACTION_STOP";
	// 开始上传
	public static final String ACTION_START_UPLOAD = "ACTION_START_UPLOAD";
	// 停止上传
	public static final String ACTION_STOP_UPLOAD = "ACTION_STOP_UPLOAD";
	// 下载更新UI
	public static final String ACTION_UPDATE = "ACTION_UPDATE";
	//上传更新UI
	public static final String ACTION_UPLOADUPDATE = "ACTION_UPLOAD_UPDATE";
	// 下载完成
	public static final String ACTION_FINISH = "ACTION_FINISH";
	// 上传完成
	public static final String ACTION_UPLOAD_FINISH = "ACTION_UPLOAD_FINISH";
	// 删除文件
	public static final String ACTION_DELETE = "ACTION_DELETE";
	
	//下载之后存储到手机上的地址
	public static final String DOWNLOAD_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/downloadtask/";
	//服务器的IP地址
	public static final String SEVER_PATH = "192.168.191.1";
	//172.16.51.57    
	//服务器使用的端口
	public static final int SEVER_PORT = 8081;
	
	//当前顶端栏显示的名称
	public static String SHOW_FILE_OR_FOLDER_NAME= "根目录";
	
	//当前服务器文件路径 
	public static String SERVER_FILEPATH = "F://pumpkin";
}
