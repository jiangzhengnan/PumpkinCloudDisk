package com.example.pumpkinonlinedisk.aty;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.pumpkinonlinedisk.Config;
import com.example.pumpkinonlinedisk.R;
import com.example.pumpkinonlinedisk.util.ServerUtil;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Register extends Activity {
	//定义组件
	EditText register_username;
	EditText register_userpassword;
	Button register_register;
	ImageButton land_return;
	
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_layout);
		context = this;
		initView();
	}
	
	private void initView() {
		land_return = (ImageButton) findViewById(R.id.land_return);
		land_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		register_username = (EditText) findViewById(R.id.register_username);
		register_userpassword = (EditText) findViewById(R.id.register_userpassword);
		register_register = (Button) findViewById(R.id.register_register);
		register_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//调用注册方法
				register(register_username.getText().toString(), register_userpassword.getText().toString());
				
			}
		});
	}
	
	// 向服务器发送注册信息
	public void register(final String username, final String userpassword) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Socket socket = new Socket(Config.SEVER_PATH, 8081);
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
					dataout.writeUTF("5" + " " + username + " " + userpassword);

					// 获取服务器返回的result
					final int result = datain.readInt();
					 
					// 关闭资源
					ServerUtil.close(in, out, dataout, datain, socket);
					/*
					 * 得到服务器返回的result 如果为1则新建成功，为0则新建失败
					 */

					// 返回到主线程，更新UI
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							/*
							 * 如果result = 1则表明注册成功
							 * 如果 = 0则表明注册失败
							 */
							Log.d("申请注册返回的结果", String.valueOf(result));
							if (result == 1) {
								Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
								finish();
							} else if (result == 2 ) {
								Toast.makeText(context, "用户名已经存在", Toast.LENGTH_SHORT).show();
								register_userpassword.setText("");
							} else {
								Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
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
}
