package com.example.pumpkinonlinedisk.adapter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pumpkinonlinedisk.Config;
import com.example.pumpkinonlinedisk.R;
import com.example.pumpkinonlinedisk.aty.AtyPhotoViewer;
import com.example.pumpkinonlinedisk.aty.MainActivity;
import com.example.pumpkinonlinedisk.bean.DownAndUpLoadInfo;
import com.example.pumpkinonlinedisk.fragment.CoundDiskFrag;
import com.example.pumpkinonlinedisk.fragment.TransmissionFrag;
import com.example.pumpkinonlinedisk.service.DownAndUploadService;

public class UploadAdapter extends BaseAdapter{
	//储存下载信息的list
	ArrayList<DownAndUpLoadInfo> list;
	//上下文
	Context context;
	//被按到的位置
	int posi;
	TextView sf_fileprogress;
	TextView sf_filesize;
	//ProgressBar uploadprogressbar;
	
	Button dakaiButton;
	boolean progressbarisshow = false;
	
	//是否显示进度条
	public boolean isProgressbarisshow() {
		return progressbarisshow;
	}
	
	//是否显示文件进度
	public void isshowprogress(boolean show) {
		if (show) {
			sf_fileprogress.setVisibility(View.INVISIBLE);
		} else {
			sf_fileprogress.setVisibility(View.VISIBLE);
		}
	}




	public void setProgressbarisshow(boolean progressbarisshow) {
		this.progressbarisshow = progressbarisshow;
	}
	
	public void setlist(ArrayList<DownAndUpLoadInfo> list) {
		this.list = list;
	}
	
	private ImageView localfileicon;
	
	public int getPosi() {
		return posi;
	}

	public void setPosi(int posi) {
		this.posi = posi;
	}

	public UploadAdapter(Context context,	ArrayList<DownAndUpLoadInfo> list, int posi) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	//跟新UI操作
	public void update(int id, int finished) {
		if (finished == -1) {
			//如果下载完成
			list.get(id).setFinished(100);
		} else {
			//如果下载未完成，显示下载的进度
			list.get(id).setFinished(
					finished * 100 / (list.get(id).getTotal() / 1000));
	 
		}
		notifyDataSetChanged();
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.storagefrag_listitem,
				null);
		 Button start = (Button) view.findViewById(R.id.sf_btStart);
		Button stop = (Button) view.findViewById(R.id.sf_btStop);
		localfileicon = (ImageView) view.findViewById(R.id.localfileicon);
		sf_fileprogress = (TextView) view.findViewById(R.id.sf_fileprogress);
		dakaiButton = (Button) view.findViewById(R.id.dakai);
		 
		sf_filesize = (TextView) view.findViewById(R.id.sf_filesize);
		//uploadprogressbar = (ProgressBar) view.findViewById(R.id.uploadprogressbar);
		sf_fileprogress.setVisibility(View.INVISIBLE);
		if (progressbarisshow) {
		//	uploadprogressbar.setVisibility(View.VISIBLE);
		} else {
		//	uploadprogressbar.setVisibility(View.GONE);
		}
		final DownAndUpLoadInfo info = list.get(position);
		
			dakaiButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i  = new Intent(context, AtyPhotoViewer.class);
				i.putExtra(AtyPhotoViewer.EXTRA_PATH, info.getURL());
				context.startActivity(i);
			}
		});
		
		
		//点击开始上传
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final DownAndUpLoadInfo infoa = list.get(position);
				if (MainActivity.currentTag == 1) {
					TransmissionFrag.getTransinstanceActivity();
					//先判断transfrag中是否已经有这个info
					if (CoundDiskFrag.instance.isuploadinfoexist(infoa)) {
						Toast.makeText(context, "此文件已在网盘中", Toast.LENGTH_SHORT).show();
					} else if (TransmissionFrag.isuploadinfoexist(infoa)) {
						Toast.makeText(context, "此文件已在上传列表中", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "请在上传列表中查看", Toast.LENGTH_SHORT).show();
						//调用transfrag的方法将要下载的info送过去
						TransmissionFrag.adduploadlist(infoa);
					}
				} else if (MainActivity.currentTag == 2) { 
				//	uploadprogressbar.setVisibility(View.VISIBLE);
					
					Log.d("查看开始上传时的total", String.valueOf(infoa.getTotal()) );
					
					
				Intent intent = new Intent(context, DownAndUploadService.class);
				intent.setAction(Config.ACTION_START_UPLOAD);
				list.get(position).setId(position);
				list.get(position).setStart(true);
				intent.putExtra("value", list.get(position));
				context.startService(intent);
				}
			}
		});

		//点击停止上传
		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(context, DownAndUploadService.class);
				intent.setAction(Config.ACTION_STOP_UPLOAD);
				list.get(position).setId(position);
				list.get(position).setStart(true);
				intent.putExtra("value", list.get(position));
				context.startService(intent);
			}
		});
		 
		TextView tv = (TextView) view.findViewById(R.id.sf_tvFileName);
		//显示文件名
		tv.setText(info.getName());
		//得到文件的大小
		long size = info.getFilesize()/1024/1024;
		DecimalFormat f = new DecimalFormat("####0.0");
		String str = f.format(size)+"MB";
		sf_fileprogress.setText(String.valueOf(info.getFinished()) + "%");
		sf_filesize.setText(str);
	//	uploadprogressbar.setMax(100);
	//	uploadprogressbar.setProgress(info.getFinished());
		
	/*	if (MainActivity.currentTag == 1) {
			//显示大小
			
			tv1.setText(str);
		} else {
			//显示下载的百分比和已下载的大小
			tv1.setText(String.valueOf(info.getFinished()) + "%" + "     "
					+ str);
		}*/
		
		//设置颜色改变
		for (int i = 0; i < list.size(); i++) {
			if (position == posi) {
				start.setVisibility(View.VISIBLE);
				stop.setVisibility(View.VISIBLE);
				view.findViewById(R.id.storage_cell).setBackgroundColor(Color.parseColor("#F5DEB3"));
			} else {
				start.setVisibility(View.INVISIBLE);
				stop.setVisibility(View.INVISIBLE);
				view.findViewById(R.id.storage_cell).setBackgroundColor(Color.WHITE);
			}
		}
		
		if (MainActivity.currentTag == 1) {
			sf_fileprogress.setVisibility(View.INVISIBLE);
		//	uploadprogressbar.setVisibility(View.GONE);
		} else if (MainActivity.currentTag == 2) {
			sf_fileprogress.setVisibility(View.VISIBLE);
		//	uploadprogressbar.setVisibility(View.VISIBLE);
		}
		
		if (info.getURL() != null) {
			Log.d("啊啊啊啊啊啊啊啊啊啊", String.valueOf(info.getAa()) );
			Log.d("啊啊啊啊啊啊啊啊啊啊", Config.DOWNLOAD_PATH + info.getName());
			localfileicon.setImageURI(Uri.fromFile(new File( Config.DOWNLOAD_PATH + info.getName())));
		}
		
		
		
		return view;
		
		
	}
}