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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pumpkinonlinedisk.Config;
import com.example.pumpkinonlinedisk.R;
import com.example.pumpkinonlinedisk.aty.MainActivity;
import com.example.pumpkinonlinedisk.bean.DownAndUpLoadInfo;
import com.example.pumpkinonlinedisk.fragment.StorageFrag;
import com.example.pumpkinonlinedisk.fragment.TransmissionFrag;
import com.example.pumpkinonlinedisk.service.DownAndUploadService;
import com.example.pumpkinonlinedisk.util.ServerUtil;
import com.example.pumpkinonlinedisk.view.CircleProgressView;

 public class DownLoadAdapter extends BaseAdapter {
	//储存下载信息的list
	ArrayList<DownAndUpLoadInfo> list;
	//上下文
	Context context;
	//被按住的项的位置
	int posi;
	//显示文件图标
	ImageView fileicon;
	TextView tv  ;
	//TextView fileprogress ;
	TextView filesize ;
	//进度条
	//ProgressBar downloadprogressbar;
	
	boolean progressbarisshow = false;
	
	
	CircleProgressView jinduview;
	
	////是否显示进度条
	public boolean isProgressbarisshow() {
		return progressbarisshow;
	}

	//是否显示文件进度
	public void isshowprogress(boolean show) {
		if (show) {
		 	jinduview.setVisibility(View.INVISIBLE);
		} else {
		 	jinduview.setVisibility(View.VISIBLE);
		}
	}


	public void setProgressbarisshow(boolean progressbarisshow) {
		this.progressbarisshow = progressbarisshow;
	}



	public DownLoadAdapter(Context context, ArrayList<DownAndUpLoadInfo> list, int posi) {
		this.context = context;
		this.list = list;
		this.posi = posi;
		//判断当前文件夹是否存在，不存在则生成文件夹
		File file = new File(Config.DOWNLOAD_PATH);
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	
 
	
	public void setlist(ArrayList<DownAndUpLoadInfo> list) {
		this.list = list;
	}
	
	public int getPosi() {
		return posi;
	}

	public void setPosi(int posi) {
		this.posi = posi;
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
			//downloadprogressbar.setProgress(100);
		} else {
			//如果下载未完成，显示下载的进度
			//这里的total为0
			int baifenbi = finished * 100 / (list.get(id).getTotal() / 1000);
			list.get(id).setFinished(baifenbi);
			
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final DownAndUpLoadInfo info = list.get(position);
		
		View view = LayoutInflater.from(context).inflate(R.layout.counddiskfrag_listitem,
				null);
	 
	//	Button open =(Button) view.findViewById(R.id.btOpen);  
		 Button start = (Button) view.findViewById(R.id.btStart);
		Button stop = (Button) view.findViewById(R.id.btStop);
		// fileprogress = (TextView) view.findViewById(R.id.progress);
		 filesize = (TextView) view.findViewById(R.id.size);
		 tv= (TextView) view.findViewById(R.id.tvFileName);
		// downloadprogressbar = (ProgressBar) view.findViewById(R.id.downloadprogressbar);
		 jinduview = (CircleProgressView) view.findViewById(R.id.jindu);
/*		if (progressbarisshow) {
			downloadprogressbar.setVisibility(View.VISIBLE);
		} else {
			downloadprogressbar.setVisibility(View.GONE);
		}*/
		
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MainActivity.currentTag == 0) {
					TransmissionFrag.getTransinstanceActivity();
					//先判断transfrag中是否已经有这个info
					if (StorageFrag.isdownloadinfoexist(info)) {
						Toast.makeText(context, "此文件已在本地文件中", Toast.LENGTH_SHORT).show();
					} else if (TransmissionFrag.isdownloadinfoexist(info)) {
						Toast.makeText(context, "此文件已在下载列表中", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "请在下载列表中查看", Toast.LENGTH_SHORT).show();
						//调用transfrag的方法将要下载的info送过去
						TransmissionFrag.adddownloadlist(info);
					}
				} else if (MainActivity.currentTag == 2) {
					//downloadprogressbar.setVisibility(View.VISIBLE);
				//点击开始下载
				if (!list.get(position).isStart()) {

					
					Intent intent = new Intent(context, DownAndUploadService.class);
					intent.setAction(Config.ACTION_START);
					list.get(position).setId(position);
					list.get(position).setStart(true);
					intent.putExtra("value", list.get(position));
					context.startService(intent);
					jinduview.setVisibility(View.VISIBLE);
				}
				
				}
			}
		});

		//点击停止下载
		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (list.get(position).isStart()) {
					Intent intent = new Intent(context, DownAndUploadService.class);
					intent.setAction(Config.ACTION_STOP);
					list.get(position).setId(position);
					list.get(position).setStart(false);
					intent.putExtra("value", list.get(position));
					context.startService(intent);
				}
			}
		});
		
	 
		
		/*//open按钮的点击事件
			open.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//如果是文件
					if (info.getType() == 0) {
						Toast.makeText(context, "暂时没有写文件查看功能", Toast.LENGTH_SHORT).show();
						//如果是文件夹
					}else if (info.getType() == 1) {
						 //向服务器发送请求得到对应文件夹下的列表ArrayList<DownLoadInfo> list
					 	  new ServerUtil().getfolderList(list.get(position).getName(), context);
						 notifyDataSetChanged(); 
					}
				}
			});*/
		
 
		//显示文件名
		tv.setText(info.getName());
		//得到文件的大小
		
		long size = info.getFilesize()/1024;
		DecimalFormat f = new DecimalFormat("####0.0");
		String str = f.format(size)+"KB";
	//	fileprogress.setText(info.getFinished() + "%");
		float num = ((float)info.getFinished())*360/100;
		
		String text = String.valueOf(info.getFinished()) + "%";
		jinduview.setshuju(num, text);
		
		
		filesize.setText(String.valueOf(str));
		//在进度条里显示大小
	//	downloadprogressbar.setMax(100);
	//	downloadprogressbar.setProgress(info.getFinished());
/*		if (MainActivity.currentTag == 0) {
			//显示大小
			tv1.setText(str);
		} else {
			//显示下载的百分比和已下载的大小
			tv1.setText(String.valueOf(info.getFinished()) + "%" + "     "
					+ str);
		}*/
	 
		if (MainActivity.currentTag == 0) {
		//	fileprogress.setVisibility(View.INVISIBLE);
			//downloadprogressbar.setVisibility(View.GONE);
			jinduview.setVisibility(View.INVISIBLE);
		} else if (MainActivity.currentTag == 2) {
	//		fileprogress.setVisibility(View.VISIBLE);
			//downloadprogressbar.setVisibility(View.VISIBLE);
			jinduview.setVisibility(View.VISIBLE);
		}
		
		//显示文件或文件夹图标
	 
		fileicon = (ImageView) view.findViewById(R.id.cound_fileicon);
		if (MainActivity.currentTag == 1) {
			if (info.getURL() != null) {
				Log.d("啊啊啊啊啊啊啊啊啊啊", info.getURL());
				fileicon.setImageURI(Uri.fromFile(new File(info.getURL())));
			}
		} else {
	 
		 
	 	if (info.getType() == 0) {
			//MP4MP3和图片格式文件显示的图标
			if (info.getName().endsWith(".mp3")) {
				fileicon.setBackgroundResource(R.drawable.mp3filepicture);
				
				//fileicon.setImageURI(Uri.fromFile(new File(info.getURL())));
			//	Log.d("热认为二位二位二位", info.getURL());
			} else if(info.getName().endsWith(".mp4")) {
				fileicon.setBackgroundResource(R.drawable.mp4filepicture);
			} else if((info.getName().endsWith(".jpg"))||(info.getName().endsWith(".png"))) {
				//Log.d("热认为二位二位二位", info.getURL());
				fileicon.setBackgroundResource(R.drawable.picturefilepicture);
				//fileicon.setImageURI(Uri.fromFile(new File(info.getURL())));
			}
			else {
				//如果格式未知则设置为一般文件图标
			fileicon.setBackgroundResource(R.drawable.file);
			}
		}else if (info.getType() == 1) {
			//fileprogress.setText("");
			fileicon.setBackgroundResource(R.drawable.folder);
		} 
		
		}
		
		//设置颜色改变
		for (int i = 0; i < list.size(); i++) {
			if (position == posi) {
			//	open.setVisibility(View.VISIBLE);
				start.setVisibility(View.VISIBLE);
				stop.setVisibility(View.VISIBLE);
				view.findViewById(R.id.counddisk_cell).setBackgroundColor(Color.parseColor("#F5DEB3"));
			} else {
			//	open.setVisibility(View.INVISIBLE);
				start.setVisibility(View.INVISIBLE);
				stop.setVisibility(View.INVISIBLE);
				view.findViewById(R.id.counddisk_cell).setBackgroundColor(Color.WHITE);
			}
		}
		
	 
		
		return view;
	}
	
 
}
