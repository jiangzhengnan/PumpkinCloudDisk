package com.example.pumpkinonlinedisk.aty;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class AtyPhotoViewer extends Activity {
	private ImageView iv;
	
	public static final String EXTRA_PATH="path";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		iv = new ImageView(this);
		setContentView(iv);
		
		String path = getIntent().getStringExtra(EXTRA_PATH);
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		/*Bitmap bitmap = createScaledBitmap(Bitmap src, int dstWidth, 
	            int dstHeight,boolean filter)*/
		if (path!=null) {
			iv.setImageBitmap( ThumbnailUtils.extractThumbnail(bitmap, 100, 100));
		//	iv.setImageURI(Uri.fromFile(new File(path)));
			 
		}else{
			finish();
		}
		
	}
}
