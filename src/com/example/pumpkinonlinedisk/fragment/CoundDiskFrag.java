package com.example.pumpkinonlinedisk.fragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pumpkinonlinedisk.Config;
import com.example.pumpkinonlinedisk.R;
import com.example.pumpkinonlinedisk.adapter.DownLoadAdapter;
import com.example.pumpkinonlinedisk.aty.MainActivity;
import com.example.pumpkinonlinedisk.bean.DownAndUpLoadInfo;
import com.example.pumpkinonlinedisk.util.TextUtil;

public class CoundDiskFrag extends android.support.v4.app.Fragment implements
		android.view.View.OnClickListener {
	
	
	public static CoundDiskFrag coundDiskFrag = null;
	// 上下文
	Context context;
 

	// 从构造方法传入context
	public CoundDiskFrag(Context context) {
		this.context = context;
		instance =this;
	}

	// 用来保存所有的下载信息
	private static ArrayList<DownAndUpLoadInfo> list;
	// 显示的listview
	ListView listView;
	// 网盘适配器
	static DownLoadAdapter adapter;
	// 四个操作栏按钮
	ImageButton frag1_sort;
	ImageButton frag1_newfolder;
	ImageButton frag1_cloud_upload;
	ImageView frag1_refresh;

	// 图片
	ImageView img;
	// 新建文件夹操作的文件夹名字
	EditText inputfoldername;

	// 定义操作栏按钮的点击监听器

	public static CoundDiskFrag instance;

	// 定义接受信息的hander
	public  Handler hander = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
//				adapter. msg.obj
				adapter.setlist((ArrayList<DownAndUpLoadInfo>) msg.obj);
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);
				Toast.makeText(context, "文件夹打开成功", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.main_activity_tab1, container, false);

		// 得到服务端提供的文件列表
		getFileList();

		// 初始化界面
		initView(v);
/* 
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Config.ACTION_UPDATE);
		intentFilter.addAction(Config.ACTION_FINISH);
		// 注册一个Receiver
		((Activity) context).registerReceiver(receiver, intentFilter); */
		coundDiskFrag = this;
		return v;
	}

	private void initView(View v) {
		// 图片
		img = new ImageView(context);
		// 操作栏
		frag1_sort = (ImageButton) v.findViewById(R.id.frag1_sort);
		frag1_newfolder = (ImageButton) v.findViewById(R.id.frag1_newfolder);
		frag1_cloud_upload = (ImageButton) v
				.findViewById(R.id.frag1_cloud_upload);
		frag1_refresh = (ImageView) v.findViewById(R.id.frag1_refresh);
		// 设置监听器
		frag1_sort.setOnClickListener(this);
		frag1_newfolder.setOnClickListener(this);
		frag1_cloud_upload.setOnClickListener(this);
		frag1_refresh.setOnClickListener(this);
		// listview
		listView = (ListView) v.findViewById(R.id.main_lv1);
		// 设置颜色改变和选项出现的监听器
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 设置被选择项的颜色
				adapter.setPosi(position);
				adapter.notifyDataSetChanged();
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int posi = position;
				AlertDialog.Builder builder = new Builder(context);
				builder.setMessage("确认删掉这个文件吗？");
				builder.setTitle("提示");
				builder.setPositiveButton("确认", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						deleteFile(list.get(posi).getName(), posi);
					}
				});

				builder.setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				return true;
			}
		});
	}

	// 得到服务器文件列表
	// 打开app时运行此函数用来获取所有下载信息，需要开启新线程防止主线程卡住。
	public void getFileList() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Socket socket = new Socket(Config.SEVER_PATH, Config.SEVER_PORT);
					// 本地局域网是用10.0.2.2
					// 得到输入输出流
					OutputStream out = socket.getOutputStream();
					InputStream in = socket.getInputStream();
					/*
					 * 包装流
					 */
					DataOutputStream dataout = new DataOutputStream(out);
					DataInputStream datain = new DataInputStream(in);

					// 向服务器请求所有下载信息
					dataout.writeUTF("file");

					// 获取服务器返回的下载信息
					String result = datain.readUTF();
					
					// 关闭资源
					close(in, out, dataout, datain, socket);
					// 用来处理返回的函数，返回信息类似于 xx.apk 1234578 xxx.apk 234566
					/**
					 * xx.apk file123456
					 */
					// 将 一个 xx.apk 1234578包装成一个DownloadInfo
					// 返回一个ArrayList<DownLoadInfo>的链表
					Log.d("请求访问文件列表得到的result", result);
					if (result == null) {
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(context, "获取失败", Toast.LENGTH_SHORT);
							}
						});
					}
					list = TextUtil.getList(result);
					adapter = new DownLoadAdapter(context, list, 0);
					// 返回到主线程，更新UI
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							listView.setAdapter(adapter);
						}
					});
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	// 删除服务器某文件
	public void deleteFile(final String filename, final int position) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Socket socket = new Socket(Config.SEVER_PATH, Config.SEVER_PORT);
					// 本地局域网是用10.0.2.2
					// 得到输入输出流
					OutputStream out = socket.getOutputStream();
					InputStream in = socket.getInputStream();
					/*
					 * 包装流
					 */
					DataOutputStream dataout = new DataOutputStream(out);
					DataInputStream datain = new DataInputStream(in);

					// 向服务器发送删除信息
					dataout.writeUTF("1" + filename);

					// 获取服务器返回的result
					final int result = datain.readInt();
					// 关闭资源
					close(in, out, dataout, datain, socket);
					/*
					 * 得到服务器返回的result 如果为1则删除成功，为0则删除失败
					 */

					// 返回到主线程，更新UI
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (result == 1) {
								Toast.makeText(context, "删除成功",
										Toast.LENGTH_SHORT).show();
								list.remove(position);
								adapter.notifyDataSetChanged();
								listView.setAdapter(adapter);
							} else {
								Toast.makeText(context, "删除失败",
										Toast.LENGTH_SHORT).show();
							}
						}
					});
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	// 在服务器上新建文件夹
	public void newfloder(final String flodername) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Socket socket = new Socket(Config.SEVER_PATH, Config.SEVER_PORT);
					// 本地局域网是用10.0.2.2
					// 得到输入输出流
					OutputStream out = socket.getOutputStream();
					InputStream in = socket.getInputStream();
					/*
					 * 包装流
					 */
					DataOutputStream dataout = new DataOutputStream(out);
					DataInputStream datain = new DataInputStream(in);

					// 向服务器发送删除信息
					dataout.writeUTF("2" + flodername);

					// 获取服务器返回的result
					final int result = datain.readInt();
					// 关闭资源
					close(in, out, dataout, datain, socket);
					/*
					 * 得到服务器返回的result 如果为1则新建成功，为0则新建失败
					 */

					// 返回到主线程，更新UI
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (result == 1) {
								Toast.makeText(context, "新建成功",
										Toast.LENGTH_SHORT).show();
								adapter.notifyDataSetChanged();
								listView.setAdapter(adapter);
							} else {
								Toast.makeText(context, "新建失败",
										Toast.LENGTH_SHORT).show();
							}
						}
					});
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

/*	//定义一个Receiver接受下载成功的广播
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 如果是ACTION_UPDATE则表示下载进度更新UI，ACTION_FINISH则表示下载完成，同时也更新UI
			if (Config.ACTION_UPDATE.equals(intent.getAction())) {
				int finished = intent.getIntExtra("finished", 0);
				int id = intent.getIntExtra("id", -1);
				Log.d("finished", String.valueOf(finished) + "  " + list.size());
				// 调用adapter更新UI操作
				adapter.update(id, finished);
			} else {
				//int finished = intent.getIntExtra("finished", 0);
				int id = intent.getIntExtra("id", -1);
				// 这里以-1为下载完成
				// 调用adapter更新UI操作
				adapter.update(id, -1);
				adapter.setProgressbarisshow(false);
				adapter.notifyDataSetChanged();
				Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
			}
		}
	};*/

	// 注销Receiver
	public void onDestroy() {
	//	((Activity) context).unregisterReceiver(receiver);
		super.onDestroy();
	};

	// 自定义关闭流和关闭socket方法
	public void close(InputStream in, OutputStream out,
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frag1_sort:
			Toast.makeText(context, "此功能暂未开放", Toast.LENGTH_SHORT).show();
			break;
		case R.id.frag1_newfolder:
			// 弹出新建文件夹的对话框
			AlertDialog.Builder builder = new Builder(context);
			builder.setTitle("新建文件夹");
			builder.setIcon(com.example.pumpkinonlinedisk.R.drawable.folder);
			inputfoldername = new EditText(context);
			builder.setView(inputfoldername);
			builder.setPositiveButton("确认", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					String getfoldername = inputfoldername.getText().toString();
					newfloder(getfoldername);
					getFileList();
					// 刷新listview
					adapter.notifyDataSetChanged();
					listView.setAdapter(adapter);
					Toast.makeText(context, "新建成功", Toast.LENGTH_SHORT).show();
				}
			});

			builder.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();

			break;
		case R.id.frag1_cloud_upload:
			Toast.makeText(context, "请到上传页面进行上传操作", Toast.LENGTH_SHORT).show();
			break;
		case R.id.frag1_refresh:
			getFileList();
			// 刷新listview
			adapter.notifyDataSetChanged();
			listView.setAdapter(adapter);
			Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
			RotateAnimation ra = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
			ra.setDuration(500);
			frag1_refresh.startAnimation(ra);
			break;
		}
	}
	
	//刷新
	public void coundrefresh() {
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
	}
	
	
	//设置不显示文件进度的方法
	public  static void changgeprogress() {
		adapter.isshowprogress(false);
		adapter.notifyDataSetChanged();
	}
	
	//判断送过来的downloadinfo是否已经存在的方法
	public static boolean isuploadinfoexist(DownAndUpLoadInfo info) {
 
		boolean result = false;
		if (list.size() != 0) {
		for (int i = 0; i < list.size(); i++) {
			if (info.getName().equals(list.get(i).getName())) {
				result = true;
			}
		}
		}
		return result;
	}
	
	//刷新数据的方法
	public void refresh() {
		Config.SERVER_FILEPATH = "F://pumpkin";
		getFileList();
		// 刷新listview
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
	}

}
