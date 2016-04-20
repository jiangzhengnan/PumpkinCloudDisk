package com.example.pumpkinonlinedisk.aty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;

import com.example.pumpkinonlinedisk.R;

public class Initial_Activity extends Activity {
	//声明注册，登陆按钮
	private Button landbtn;
	private Button registerbtn;
	//private Button initial_kuaisuland;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.initial_layout);
        initView();
    }
    
    private void initView() {
    	landbtn = (Button) findViewById(R.id.initial_land);
    	registerbtn = (Button) findViewById(R.id.initial_register);
    	//initial_kuaisuland = (Button) findViewById(R.id.initial_kuaisuland);
    	//分别设置跳转的监听器
    	landbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Initial_Activity.this, Land.class);
				startActivity(i);
			}
		});
    	
    	landbtn.setLongClickable(true);
    	landbtn.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Intent i = new Intent(Initial_Activity.this,MainActivity.class);
				startActivity(i);
				return false;
			}
		});
    	
    	registerbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Initial_Activity.this, Register.class);
				startActivity(i);
			}
		});
    	
    /*	initial_kuaisuland.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Initial_Activity.this, MainActivity.class);
				startActivity(i);
			}
		});*/
    	
    }


}
