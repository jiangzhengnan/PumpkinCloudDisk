package com.example.pumpkinonlinedisk.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
  
import android.os.IBinder;
import android.util.Log;

import com.example.pumpkinonlinedisk.Config;
import com.example.pumpkinonlinedisk.adapter.DownLoadAdapter;
import com.example.pumpkinonlinedisk.bean.DownAndUpLoadInfo;
import com.example.pumpkinonlinedisk.bean.Flag;
import com.example.pumpkinonlinedisk.downloaddb.DownLoadDAO;
import com.example.pumpkinonlinedisk.util.Util;

public class DownAndUploadService extends Service {

	// 每个Flag保存下载或暂停标志
	private LinkedList<Flag> flist = new LinkedList<Flag>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// 开始service
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		// 获取传来的对象
		final DownAndUpLoadInfo info = (DownAndUpLoadInfo) intent
				.getSerializableExtra("value");

		// 中转变量
		Flag flagtemp;
		if ((flagtemp = Util.getStart(flist, info.getId())) == null) {
			// 生成新的flag对象并添加到链表
			/*
			 *isDownload 是否暂停
			 * id id号
			 * num 在链表中的第几个
			 */
			Flag flag = new Flag();
			flag.setNum(flist.size());
			flag.setId(info.getId());
			flag.setDownload(true);
			flagtemp = flag;
			flist.add(flag);
		}
		final Flag result = flagtemp;

		//如果是开始下载
		if (intent.getAction().equals(Config.ACTION_START)) {
			//开启线程
			Thread thread = new Thread(new Runnable() {
				File file = new File(Config.DOWNLOAD_PATH
						+ info.getName());
				@Override
				public void run() {
					DownLoadDAO dao = new DownLoadDAO(DownAndUploadService.this);
					//插入数据库，在dao里进行本地是否已有数据的判断
					dao.insert(info);

					if (!file.exists())
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					writeFile(file, info, result);
				}
			});
			thread.start();

		} else if (intent.getAction().equals(Config.ACTION_STOP)) {
			//设置暂停
			flist.get(flagtemp.getNum()).setDownload(false);
		} else if (intent.getAction().equals(Config.ACTION_DELETE)) {
		//如果是删除，要删除服务器上的该文件和数据库里保存的文件，这个写在长按操作里面了
			DownLoadDAO dao = new DownLoadDAO(DownAndUploadService.this);
			//插入数据库，在dao里删除这个数据
			dao.delete(info);
		} else if (intent.getAction().equals(Config.ACTION_START_UPLOAD)) {
		//如果是开始上传
			DownLoadDAO dao = new DownLoadDAO(DownAndUploadService.this);
			//插入数据库，在dao里删除这个数据
			dao.delete(info);
			//开启线程
			Thread thread = new Thread(new Runnable() {
				File file = new File(Config.DOWNLOAD_PATH
						+ info.getName());
				@Override
				public void run() {
					DownLoadDAO dao = new DownLoadDAO(DownAndUploadService.this);
					//插入数据库，在dao里进行本地是否已有数据的判断
					dao.insert(info);
					uploadFile(file, info, result);
				}
			});
			thread.start();
			
		}else if (intent.getAction().equals(Config.ACTION_STOP_UPLOAD)) {
		//如果是停止上传
			//设置暂停
			flist.get(flagtemp.getNum()).setDownload(false);
			}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 上传文件到服务器的方法
	 * @param file
	 * @param info
	 * @param flag
	 */
	public void uploadFile(final File file, final DownAndUpLoadInfo info,
			final Flag flag) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				//设置服务器的IP地址，以及端口
				Socket socket;
				try {
					socket = new Socket(Config.SEVER_PATH, Config.SEVER_PORT);
					InputStream in = socket.getInputStream();
					OutputStream out = socket.getOutputStream();
					DataOutputStream dataout = new DataOutputStream(out);
					//发送给服务器需要下载的文件和断点
					dataout.writeUTF("4" +info.getName() + " " + info.getFinished());
					
					
					//实现断点续传需要使用的File类
					RandomAccessFile raf = new RandomAccessFile(Config.DOWNLOAD_PATH + info.getName(),
							"rwd");
					//skipBytes方法可以尝试跳过输入的 n 个字节以丢弃跳过的字节，以达到断点续传的效果
					raf.skipBytes(info.getFinished());
					
					int length = 0;
					int total = info.getFinished();
					long time = System.currentTimeMillis();
					
					byte buff[] = new byte[8192];
					//根据Flag进行判断是否继续读出
					while (flist.get(flag.getNum()).isDownload()) {
						if (raf != null) {
							length = raf.read(buff);
						}
						if (length == -1) {
							break;
						}
						//把资源读出
						out.write(buff, 0, length);
						
						total += length;
						info.setFinished(total);
						
						//在相隔一定时间后用广播进行数据的传递，用广播跟新UI
						if ((System.currentTimeMillis() - time) > 1000) {
							Log.d("上传过程中的total", String.valueOf(info.getTotal()) );
							Log.d("上传过程中的finished", String.valueOf(info.getFinished()) );
							time = System.currentTimeMillis();
							Intent intent = new Intent(
									Config.ACTION_UPLOADUPDATE);
							intent.putExtra("id", info.getId());
							intent.putExtra("finished",
									info.getFinished() / 1000);
							sendBroadcast(intent);
						}
					}
					//
					out.flush();
					
					//当所有数据上传到服务器完毕
					if (info.getTotal() == info.getFinished()) {
						Log.d("上传完毕的total和finished",String.valueOf(info.getTotal()) +"分割~！~#"+ String.valueOf(info.getFinished()) );
						Intent intent = new Intent(
								Config.ACTION_UPLOAD_FINISH);
						intent.putExtra("id", info.getId());
						intent.putExtra("finished", info.getFinished() / 1000);
						sendBroadcast(intent);
					}
					
					//重新设置Flag
					flist.get(flag.getNum()).setDownload(true);
					
					
					//按下暂停或者下载完成将信息保存至数据库中
					DownLoadDAO dao = new DownLoadDAO(DownAndUploadService.this);
					dao.update(info);
					//关闭资源
					raf.close();
					Util.close(in, out, null, dataout, socket);
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		thread.start();
		
	}

	/*
	 * 从服务器下载资源的方法
	 * 开始读取网络数据，并保存至本地
	 */
	
	public void writeFile(final File file, final DownAndUpLoadInfo info,
			final Flag flag) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					//设置服务器的IP地址，以及端口
					Socket socket = new Socket(Config.SEVER_PATH, Config.SEVER_PORT);
					InputStream in = socket.getInputStream();
					OutputStream out = socket.getOutputStream();
					DataOutputStream dataout = new DataOutputStream(out);
					//发送给服务器文件名字和下载的断点
					dataout.writeUTF("0" +info.getName() + " " + info.getFinished());
					
					//实现断点续传需要使用的File类
					RandomAccessFile raf = new RandomAccessFile(file, "rwd");
					//直接跳到断点的地方开始下载
					raf.skipBytes(info.getFinished());
					
					byte buff[] = new byte[8192];
					int length = 0;
					int total = info.getFinished();
					long time = System.currentTimeMillis();
					
					//根据Flag进行判断是否继续下载
					while (flist.get(flag.getNum()).isDownload()) {
						/**
						 * length记录已下载的长度，如果长度=-1，则跳出下载
						 * 否则，一直执行raf的write方法写入数据
						 */
						length = in.read(buff);
						if (length == -1)
							break;
						raf.write(buff, 0, length);
						
						
						total += length;
						info.setFinished(total);
						
						//在相隔一定时间后用广播进行数据的传递，用广播跟新UI
						if ((System.currentTimeMillis() - time) > 100) {
							time = System.currentTimeMillis();
							Intent intent = new Intent(
									Config.ACTION_UPDATE);
							intent.putExtra("id", info.getId());
							intent.putExtra("finished",
									info.getFinished() / 1000);
							sendBroadcast(intent);
							Log.d("下载过程中的total", String.valueOf(info.getTotal()) );
							Log.d("下载过程中的finished", String.valueOf(info.getFinished()) );
						}

					}
					
					//当所有数据下载完毕
					if (info.getTotal() == info.getFinished()) {
						Log.d("下载完毕的total和finished",String.valueOf(info.getTotal()) +"分割~！~#"+ String.valueOf(info.getFinished()) );
						Intent intent = new Intent(
								Config.ACTION_FINISH);
						intent.putExtra("id", info.getId());
						intent.putExtra("finished", info.getFinished() / 1000);
						sendBroadcast(intent);
					}

					//重新设置Flag
					flist.get(flag.getNum()).setDownload(true);
					
					//按下暂停或者下载完成将信息保存至数据库中
					DownLoadDAO dao = new DownLoadDAO(DownAndUploadService.this);
					dao.update(info);
					//关闭资源
					raf.close();
					Util.close(in, out, null, dataout, socket);
				} catch (Exception e) {
				}
			}
		});
		thread.start();
	}
}
