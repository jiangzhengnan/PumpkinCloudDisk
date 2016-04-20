package com.example.pumpkinonlinedisk.fragment;

import com.example.pumpkinonlinedisk.R;
import com.example.pumpkinonlinedisk.aty.Initial_Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class UserFrag extends android.support.v4.app.Fragment implements OnClickListener{

	//定义按钮组件
	Button quit;
	Button shezhi;
	Context context;
	
	public UserFrag(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.main_activity_tab4, container, false);
			initView(v);
				return v;
	}
	
	private void initView(View v) {
		quit = (Button) v.findViewById(R.id.userfrag_quit);
		shezhi = (Button) v.findViewById(R.id.userfrag_shezhi);
		shezhi.setOnClickListener(this);
		quit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userfrag_quit:
			Intent i = new Intent(context, Initial_Activity.class);
			startActivity(i);
			break;
		case R.id.userfrag_shezhi:
			Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
