package com.example.pumpkinonlinedisk.fragment;

import java.io.File;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
 
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pumpkinonlinedisk.Config;
import com.example.pumpkinonlinedisk.R;
import com.example.pumpkinonlinedisk.adapter.UploadAdapter;
import com.example.pumpkinonlinedisk.bean.DownAndUpLoadInfo;
import com.example.pumpkinonlinedisk.service.DownAndUploadService;

public class StorageFrag extends android.support.v4.app.Fragment implements
		android.view.View.OnClickListener {
	public static StorageFrag storageFrag = null;

	//上下文
	Context context;
	//定义组件
	ListView listView;
	static //定义存储本地文件信息的数组
	ArrayList<DownAndUpLoadInfo> list= new ArrayList<DownAndUpLoadInfo>();;
	//定义删除用的存储本地文件信息的数组
	ArrayList<DownAndUpLoadInfo> aaaaaalist;
	//本地文件信息适配器
	UploadAdapter adapter;
	//操作栏刷新按钮
	ImageView frag2_refresh;
	
	public StorageFrag(Context context) {
		this.context = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.main_activity_tab2, container, false);
		initView(v);
/*		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Config.ACTION_DELETE);
		// 注册一个Receiver
		((Activity) context).registerReceiver(receiver, intentFilter);*/
		storageFrag = this;
		return v;
	}
	static int posi;
	/**
	 * 
	 * @param v
	 */
	private void initView(View v) {
		frag2_refresh = (ImageView) v.findViewById(R.id.frag2_refresh);
		frag2_refresh.setOnClickListener(this);
		listView = (ListView) v.findViewById(R.id.main_lv2);
		getlocalfile();
		//设置颜色改变和选项出现的监听器
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//设置被选择项的颜色
				
				posi = position;
				adapter.setPosi(position);
				adapter.notifyDataSetChanged();
			}
		});
		
		//设置长按删除
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				final int position, long id) {
			AlertDialog.Builder builder = new Builder(context);
			builder.setMessage("确认删掉这个文件吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//发送广播删除本地文件的SQLITE数据
					Intent intent = new Intent(context, DownAndUploadService.class);
					intent.setAction(Config.ACTION_DELETE);
					intent.putExtra("value", list.get(position));
					context.startService(intent);
					
					//删除本地文件deleteFile(list.get(posi).getName(), posi);
					// 获得目录
					list.remove(posi);
					File file = new File(Config.DOWNLOAD_PATH + aaaaaalist.get(position).getName());
					Log.d("选中要删除的文件名字", file.getName());
					Log.d("对应list的文件名字", aaaaaalist.get(position).getName() );
					//删除该文件
					file.delete();
					 getlocalfile();
					adapter.notifyDataSetChanged();
					refresh();
					listView.setAdapter(adapter);
						Toast.makeText(context, "删除本地文件成功", Toast.LENGTH_SHORT).show();
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
	
	//得到本地文件
	public   void getlocalfile() {
		// 获得目录
		File file = new File(Config.DOWNLOAD_PATH);
		// 获得文件名
		String[]   filename = file.list();
		File file2;
		aaaaaalist = new ArrayList<DownAndUpLoadInfo>();
		if (filename != null) {
		
 
		for (int i = 0; i < filename.length; i++) {
			Log.d("得到的文件名", filename[i]);
			//得到媒体长度
			  file2 = new File(Config.DOWNLOAD_PATH, filename[i]);
			  long a = file2.length();
			  String url = Config.DOWNLOAD_PATH + filename[i];
			  DownAndUpLoadInfo fileinfo = new DownAndUpLoadInfo(filename[i],file2.length(), (int)file2.length(), url ,1);
			  
			  Log.d("热认为二位二位二位", fileinfo.getURL());
			  
			list.add(fileinfo);
			aaaaaalist.add(fileinfo);
			Log.d("list中的文件与对应的位置", list.get(i).getName() + "第多少位：" + i);
			 
		}
		}
		adapter = new UploadAdapter(context, list, 0);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frag2_refresh:
			list.clear();
			 getlocalfile() ;
			 adapter.notifyDataSetChanged();
			 Toast.makeText(context, "刷新本地文件成功", Toast.LENGTH_SHORT).show();
			 
			RotateAnimation ra = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
			ra.setDuration(500);
			frag2_refresh.startAnimation(ra);
			break;

		default:
			break;
		}
		
	}
	
	//刷新
	public void refresh() {
		list.clear();
		 getlocalfile() ;
		 adapter.notifyDataSetChanged();
		 listView.setAdapter(adapter);
	}
	
/*	// 定义一个Receiver接受上传成功的广播
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 如果是ACTION_UPDATE则表示下载进度更新UI，ACTION_FINISH则表示下载完成，同时也更新UI
			if (Config.ACTION_UPLOAD_FINISH.equals(intent.getAction())) {
				int finished = intent.getIntExtra("finished", 0);
				int id = intent.getIntExtra("id", -1);
				Log.d("finished", String.valueOf(finished) + "  " + list.size());
				// 调用adapter更新UI操作
				adapter.update(id, finished);
				adapter.setProgressbarisshow(false);
				adapter.notifyDataSetChanged();
				Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
			} else {
			 
				int finished = intent.getIntExtra("finished", 0);
				int id = intent.getIntExtra("id", -1);
				// 这里以-1为下载完成
				// 调用adapter更新UI操作
				adapter.update(id, -1);
			}
		}
	};*/
	

	//判断送过来的downloadinfo是否已经存在的方法
	public static boolean isdownloadinfoexist(DownAndUpLoadInfo info) {
 
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
	
	// 注销Receiver
	public void onDestroy() {
	//	((Activity) context).unregisterReceiver(receiver);
		list.clear();
		super.onDestroy();
	};
	
	
}