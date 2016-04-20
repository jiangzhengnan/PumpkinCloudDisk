package com.example.pumpkinonlinedisk.aty;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pumpkinonlinedisk.Config;
import com.example.pumpkinonlinedisk.R;
import com.example.pumpkinonlinedisk.adapter.ViewPagerAdapter;
import com.example.pumpkinonlinedisk.fragment.CoundDiskFrag;
import com.example.pumpkinonlinedisk.fragment.StorageFrag;
import com.example.pumpkinonlinedisk.fragment.TransmissionFrag;
import com.example.pumpkinonlinedisk.fragment.UserFrag;

public class MainActivity extends FragmentActivity implements OnPageChangeListener {
	public static MainActivity instanceActivity = null;
	
	// 主界面适配器
	FragmentPagerAdapter fPagerAdapter;
	// 碎片每个碎片为一个布局
	private ArrayList<Fragment> fragments;
	// 所有的Tab
	private List<View> views;
	// 导航式Tab
	private ViewPager vp;
	// 导航Tab适配器
	private ViewPagerAdapter vpAdapter;

	// imageview
	private ImageView[] ivViews;
	
	//TextView
	private TextView[] tvViews;
	
	
	private LinearLayout mian_caozuolan;
	// 当前标签
	public static int currentTag = 0;

	//顶部状态栏
	FrameLayout zhuangtaiLayout;
	
	/**
	 * 编辑按钮状态标签
	 * 0为编辑
	 * 1为返回
	 */
	static int editTag=0;
	// 编辑，保存状态标签
	private boolean edit_save = true;

	private int[] ivIds = { R.id.ivMain1, R.id.ivMain2,
			R.id.ivMain3, R.id.ivMain4 };
	
	private int[] tvIds = { R.id.tvMain1, R.id.tvMain2,
			R.id.tvMain3, R.id.tvMain4 };
	//四个tab按钮
	private static int[] ivviews = {R.drawable.ivviews0, R.drawable.ivviews1,
			R.drawable.ivviews2,R.drawable.ivviews3};
 
	//查看文件时改变的返回按钮和显示的文字
	private ImageButton main_top_leftreturn;
	private TextView main_top_filename;
	//查看文件时改变的控件
	private LinearLayout mian_top_gaibainqian;
	private LinearLayout mian_top_gaibainhou;
	
	private TextView yonghuming;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.main_activity_layout);
			// 创建碎片集合
			fragments = new ArrayList<Fragment>();
			initView();
			instanceActivity = this;
	}
	
	private void initView() {
		//查看文件时改变的控件
		main_top_leftreturn = (ImageButton) findViewById(R.id.main_top_leftreturn);
		main_top_leftreturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Config.SERVER_FILEPATH = "F://pumpkin";
				//改变顶端布局回去
				changgetoplayoutback(null);
				//刷新一次回到根目录
				CoundDiskFrag.coundDiskFrag.refresh();
			}
		});
		main_top_filename = (TextView) findViewById(R.id.main_top_filename);
		mian_top_gaibainqian = (LinearLayout) findViewById(R.id.mian_top_gaibainqian);
		yonghuming = (TextView) findViewById(R.id.yonghuming);
		yonghuming.setText(Config.username);
		mian_top_gaibainhou = (LinearLayout) findViewById(R.id.mian_top_gaibainhou);
		//布局组件
		zhuangtaiLayout = (FrameLayout) findViewById(R.id.ll1);
		mian_caozuolan = (LinearLayout) findViewById(R.id.mian_caozuolana);
		//绑定图片
		ivViews = new ImageView[ivIds.length];
		for (int i = 0; i < ivIds.length; i++) {//
			final int j = i;
			ivViews[i] = (ImageView) this.findViewById(ivIds[i]);
			ivViews[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					changeTagView(j);       
				}
			});
		}
		//绑定文字
		
		tvViews = new TextView[tvIds.length];
		for (int i = 0; i < tvIds.length; i++) {
			final int j = i;
			tvViews[i] = (TextView) this.findViewById(tvIds[i]);
			tvViews[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					changeTagView(j);
				}
			});
		}
		
		LayoutInflater inflater = LayoutInflater.from(this);

		// 添加滑动
		views = new ArrayList<View>();
		views.add(inflater.inflate(R.layout.main_activity_tab1, null));
		views.add(inflater.inflate(R.layout.main_activity_tab2, null));
		views.add(inflater.inflate(R.layout.main_activity_tab3, null));
		views.add(inflater.inflate(R.layout.main_activity_tab4, null));
		vpAdapter = new ViewPagerAdapter(views, this);
		vp = (ViewPager) findViewById(R.id.main_viewPager);
		
		fPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				// TODO 自动生成的方法存根
				return fragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				// TODO 自动生成的方法存根
				return fragments.get(arg0);
			}
		};
		
		// 声明各个Tab的实例
		CoundDiskFrag coundDiskFrag = new CoundDiskFrag(this);
		StorageFrag storageFrag = new StorageFrag(this);
		TransmissionFrag transmissionFrag = new TransmissionFrag(this);
		UserFrag userFrag = new UserFrag(this);
		// 将tabs加入到碎片集中
		fragments.add(coundDiskFrag);
		fragments.add(storageFrag);
		fragments.add(transmissionFrag);
		fragments.add(userFrag);
		vp.setAdapter(fPagerAdapter);

		vp.setOnPageChangeListener(this);
		
		//注意，设置Page 即缓存页面的个数，数过小时会出现fragment重复加载的问题
		vp.setOffscreenPageLimit(4);
		ivViews[0].setBackgroundResource(R.drawable.ivviews0);
		tvViews[0].setTextColor(Color.parseColor("#EE4000"));
	}
	
	// 更换标签
	private void changeTagView(int change) {
		vp.setCurrentItem(change, false);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		currentTag = arg0;
		switch (arg0) { 
		case 0:
			tvViews[0].setTextColor(Color.parseColor("#1A94E6"));
			tvViews[1].setTextColor(Color.parseColor("#000000"));
			tvViews[2].setTextColor(Color.parseColor("#000000"));
			tvViews[3].setTextColor(Color.parseColor("#000000"));
			 
			//图片
			ivViews[0].setBackgroundResource(R.drawable.ivviews0);
			ivViews[1].setBackgroundResource(R.drawable.cunchu);
			ivViews[2].setBackgroundResource(R.drawable.trans);
			ivViews[3].setBackgroundResource(R.drawable.user);
			
			break;
		case 1:
			//文字
			tvViews[0].setTextColor(Color.parseColor("#000000"));
			tvViews[1].setTextColor(Color.parseColor("#1A94E6"));
			tvViews[2].setTextColor(Color.parseColor("#000000"));
			tvViews[3].setTextColor(Color.parseColor("#000000"));
			 
			//图片
			ivViews[0].setBackgroundResource(R.drawable.clouddisk);
			ivViews[1].setBackgroundResource(R.drawable.ivviews1);
			ivViews[2].setBackgroundResource(R.drawable.trans);
			ivViews[3].setBackgroundResource(R.drawable.user);
			break;
		case 2:
			//文字
			tvViews[0].setTextColor(Color.parseColor("#000000"));
			tvViews[1].setTextColor(Color.parseColor("#000000"));
			tvViews[2].setTextColor(Color.parseColor("#1A94E6"));
			tvViews[3].setTextColor(Color.parseColor("#000000"));
			 
			//图片
			ivViews[0].setBackgroundResource(R.drawable.clouddisk);
			ivViews[1].setBackgroundResource(R.drawable.cunchu);
			ivViews[2].setBackgroundResource(R.drawable.ivviews2);
			ivViews[3].setBackgroundResource(R.drawable.user);
			break;
		case 3:
			//文字
			tvViews[0].setTextColor(Color.parseColor("#000000"));
			tvViews[1].setTextColor(Color.parseColor("#000000"));
			tvViews[2].setTextColor(Color.parseColor("#000000"));
			tvViews[3].setTextColor(Color.parseColor("#1A94E6"));
			 
			//图片
			ivViews[0].setBackgroundResource(R.drawable.clouddisk);
			ivViews[1].setBackgroundResource(R.drawable.cunchu);
			ivViews[2].setBackgroundResource(R.drawable.trans);
			ivViews[3].setBackgroundResource(R.drawable.ivviews3);
			
			break;
		}
	}

	//查看文件 改变顶端布局
	public void changgetoplayout(String filename) {
		mian_top_gaibainqian.setVisibility(View.INVISIBLE);
		mian_top_gaibainhou.setVisibility(View.VISIBLE);
		main_top_filename.setText(filename);
		
	}

	//查看文件 改回来顶端布局
	public void changgetoplayoutback(String filename) {
		mian_top_gaibainhou.setVisibility(View.INVISIBLE);
		mian_top_gaibainqian.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onBackPressed() {
		Config.SERVER_FILEPATH = "F://pumpkin";
		if (mian_top_gaibainqian.getVisibility() == View.VISIBLE) {
			if (currentTag != 0) {
				changeTagView(0);
				return;
			}
			super.onBackPressed();
		} else if (mian_top_gaibainqian.getVisibility() == View.INVISIBLE) {
			changgetoplayoutback(null);
			CoundDiskFrag.coundDiskFrag.refresh();
		}
	}
}
