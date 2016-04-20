package com.example.pumpkinonlinedisk.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pumpkinonlinedisk.Config;
import com.example.pumpkinonlinedisk.adapter.DownLoadAdapter;
import com.example.pumpkinonlinedisk.aty.MainActivity;
import com.example.pumpkinonlinedisk.bean.DownAndUpLoadInfo;
import com.example.pumpkinonlinedisk.fragment.CoundDiskFrag;


public class ServerUtil {
	 ArrayList<DownAndUpLoadInfo> list;
	 
	// 打开app时运行此函数用来获取所有下载信息，需要开启新线程防止主线程卡住。
	/*
	 * ArrayList<DownLoadInfo> list
	 */
		public void getfolderList(final String foldername , final Context context) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Socket socket = new Socket(Config.SEVER_PATH, 8081);
						//本地局域网是用10.0.2.2
						//得到输入输出流
						OutputStream out = socket.getOutputStream();
						InputStream in = socket.getInputStream();
						/*
						 * 包装流
						 */
						DataOutputStream dataout = new DataOutputStream(out);
						DataInputStream datain = new DataInputStream(in);
						
						Config.SERVER_FILEPATH += "//" + foldername; 
						// 向服务器请求所有下载信息
						dataout.writeUTF("3" + Config.SERVER_FILEPATH);

						//获得服务器的判断信息判断是否为空
						int empty = datain.readInt();
						if (empty == 1) {
							// 返回到主线程，更新UI
							((Activity) context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(context, "该文件夹为空，不能打开", Toast.LENGTH_SHORT).show();
									Config.SERVER_FILEPATH = "F://pumpkin";
									}
							});
							
							
						} else {
							// 返回到主线程，更新UI
							((Activity) context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									//改变mainactivity显示
									MainActivity.instanceActivity.changgetoplayout(foldername);
									}
							});
							 
						// 获取服务器返回的下载信息
						String result = datain.readUTF();
						if (result == null) {
							Config.SERVER_FILEPATH = "F://pumpkin";
							// 返回到主线程，更新UI
							((Activity) context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(context, "服务器报错，打开失败", Toast.LENGTH_SHORT).show();
									}
							});
						}
						//关闭资源
						close(in, out, dataout, datain, socket);
						// 用来处理返回的函数，返回信息类似于 xx.apk 1234578 xxx.apk 234566
						// 将 一个 xx.apk 1234578包装成一个DownloadInfo
						// 返回一个ArrayList<DownLoadInfo>的链表
						
						Log.d("ServerUtil得到的result", result);
						//000001 file0 00001 file0 Android设计题目及要求2015.doc file0 
						list = TextUtil.getList(result);
						Message msg = new Message();
						msg.what=1;
						msg.obj = list;
						CoundDiskFrag.instance.hander.sendMessage(msg);
					
						Log.d("ServerUtil得到的list", String.valueOf(list.size()) );
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			 
			});
			thread.start();
			 
			//为什么这里得到的adapter是空的？待解决
		}
		
		

		// 自定义关闭流和关闭socket方法
		public static void close(InputStream in, OutputStream out,
				DataOutputStream dataout, DataInputStream datain, Socket socket) {
			try {
				in.close();
				out.close();
				dataout.close();
				datain.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}
