	package com.example.pumpkinonlinedisk.fragment;
	
	import java.util.ArrayList;
	
	import com.example.pumpkinonlinedisk.Config;
	import com.example.pumpkinonlinedisk.R;
	import com.example.pumpkinonlinedisk.adapter.DownLoadAdapter;
	import com.example.pumpkinonlinedisk.adapter.UploadAdapter;
	import com.example.pumpkinonlinedisk.aty.MainActivity;
	import com.example.pumpkinonlinedisk.bean.DownAndUpLoadInfo;
	
	import android.app.Activity;
	import android.content.BroadcastReceiver;
	import android.content.Context;
	import android.content.Intent;
	import android.content.IntentFilter;
	import android.os.Bundle;
	import android.support.annotation.Nullable;
	import android.util.Log;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.View.OnClickListener;
	import android.view.ViewGroup;
	import android.widget.AdapterView;
	import android.widget.Button;
	import android.widget.ListView;
	import android.widget.Toast;
	import android.widget.AdapterView.OnItemClickListener;
	
	public class TransmissionFrag extends android.support.v4.app.Fragment{
		
		static Context context;
		private static TransmissionFrag transinstanceActivity = null;
		//定义组件
		static ListView  trans_lv;
		
		// 用来保存所有的下载信息
		private static ArrayList<DownAndUpLoadInfo> downloadlist = new ArrayList<DownAndUpLoadInfo>();
		
		private static DownLoadAdapter downloadAdapter  = new DownLoadAdapter(context, downloadlist, 0);
			
		// 用来保存所有的上傳信息
		private static ArrayList<DownAndUpLoadInfo> uploadlist= new ArrayList<DownAndUpLoadInfo>();
		
		private static UploadAdapter uploadAdapter  = new UploadAdapter(context, uploadlist, 0);
		
		//定义按钮组件
		Button trans_downloadbtn;
		Button trans_uploadbtn;
		Button trans_clean;
			
		public TransmissionFrag(Context context) {
			this.context = context;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			View v = inflater
					.inflate(R.layout.main_activity_tab3, container, false);
			trans_lv = (ListView) v.findViewById(R.id.trans_lv);
			setTransinstanceActivity(this);
			initView(v);
	
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Config.ACTION_UPDATE);
			intentFilter.addAction(Config.ACTION_FINISH);
			intentFilter.addAction(Config.ACTION_UPLOAD_FINISH);
			intentFilter.addAction(Config.ACTION_UPLOADUPDATE);
			// 注册一个Receiver
			((Activity) context).registerReceiver(receiver, intentFilter);
					return v;
		}
		
		private void initView(View v ) {
			trans_downloadbtn = (Button) v.findViewById(R.id.trans_downloadbtn);
			trans_downloadbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					downloadAdapter.notifyDataSetChanged();
					trans_lv.setAdapter(downloadAdapter);
				}
			});
			trans_uploadbtn = (Button) v.findViewById(R.id.trans_uploadbtn);
			trans_uploadbtn.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					uploadAdapter.notifyDataSetChanged();
					trans_lv.setAdapter(uploadAdapter);
			 
				}
			});
			
			// 设置颜色改变和选项出现的监听器
			trans_lv.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// 设置被选择项的颜色
					downloadAdapter.setPosi(position);
					downloadAdapter.notifyDataSetChanged();
					
					 
					uploadAdapter.setPosi(position);
					uploadAdapter.notifyDataSetChanged();
				}
			});
			
			trans_clean = (Button) v.findViewById(R.id.trans_clean);
			trans_clean.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					downloadlist.clear();
					uploadlist.clear();
					downloadAdapter.setlist(downloadlist);
					downloadAdapter.notifyDataSetChanged();
					uploadAdapter.setlist(uploadlist);
					uploadAdapter.notifyDataSetChanged();
					Toast.makeText(context, "清空列表成功", Toast.LENGTH_SHORT).show();
					
				}
			});
		}
		
		//往downloadlist中添加数据的方法
		public static void adddownloadlist(DownAndUpLoadInfo info) {
			 
			downloadlist.add(info);
			downloadAdapter  = new DownLoadAdapter(context, downloadlist, 0);
			downloadAdapter.notifyDataSetChanged();
			trans_lv.setAdapter(downloadAdapter);
		}
		
		//往uploadlist中添加数据的方法
		public static void adduploadlist(DownAndUpLoadInfo info) {
			uploadlist.add(info);
			uploadAdapter  = new UploadAdapter(context, uploadlist, 0);
			uploadAdapter.notifyDataSetChanged();
			trans_lv.setAdapter(uploadAdapter);
		}
		
		//判断送过来的downloadinfo是否已经存在的方法
		public static boolean isdownloadinfoexist(DownAndUpLoadInfo info) {
			boolean result = false;
			if (downloadlist.size() != 0) {
			for (int i = 0; i < downloadlist.size(); i++) {
				if (info == downloadlist.get(i)) {
					result = true;
				}
			}
			}
			return result;
		}
		
		//判断送过来的uploadinfo是否已经存在的方法
		public static boolean isuploadinfoexist(DownAndUpLoadInfo info) {
			boolean result = false;
			if (uploadlist.size() != 0) {
			for (int i = 0; i < uploadlist.size(); i++) {
				if (info == uploadlist.get(i)) {
					result = true;
				}
			}
			}
			return result;
		}
	
		// 定义一个Receiver接受下载成功的广播
		BroadcastReceiver receiver = new BroadcastReceiver() {
	
			@Override
			public void onReceive(Context context, Intent intent) {
				// 如果是ACTION_UPDATE则表示下载进度更新UI，ACTION_FINISH则表示下载完成，同时也更新UI
				if (Config.ACTION_UPDATE.equals(intent.getAction())) {
					int finished = intent.getIntExtra("finished", 0);
					int id = intent.getIntExtra("id", -1);
					// 调用adapter更新UI操作
					downloadAdapter.update(id, finished);
					downloadAdapter.notifyDataSetChanged();
				} else if (Config.ACTION_UPLOADUPDATE.equals(intent.getAction())) {
					int finished = intent.getIntExtra("finished", 0);
					int id = intent.getIntExtra("id", -1);
					// 调用adapter更新UI操作
					uploadAdapter.update(id, finished);
					uploadAdapter.notifyDataSetChanged();
				} else if (Config.ACTION_UPLOAD_FINISH.equals(intent.getAction())) {
					int id = intent.getIntExtra("id", -1);
					// 这里以-1为下载完成
					// 调用adapter更新UI操作
					uploadAdapter.update(id, -1);
					uploadAdapter.notifyDataSetChanged();
					CoundDiskFrag.instance.coundrefresh();
					Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
				} else if (Config.ACTION_FINISH.equals(intent.getAction())){
					int id = intent.getIntExtra("id", -1);
					// 这里以-1为下载完成
					// 调用adapter更新UI操作
					downloadAdapter.update(id, -1);
					downloadAdapter.notifyDataSetChanged();
					StorageFrag.storageFrag.refresh();
					Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		//自动生成的方法
		public static TransmissionFrag getTransinstanceActivity() {
			return transinstanceActivity;
		}
		//自动生成的方法
		public static void setTransinstanceActivity(TransmissionFrag transinstanceActivity) {
			TransmissionFrag.transinstanceActivity = transinstanceActivity;
		}
		
		@Override
		public void onDestroy() {
			((Activity) context).unregisterReceiver(receiver);
			super.onDestroy();
		}
		
		//设置显示文件进度的方法
		public  static void changgeprogress() {
			downloadAdapter.isshowprogress(true);
			downloadAdapter.notifyDataSetChanged();
		}
		
	
	
	}
