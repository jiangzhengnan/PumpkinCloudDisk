package com.example.pumpkinonlinedisk.downloaddb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DLDbHelper extends SQLiteOpenHelper {
	private static final String CREAT_DOWNLOAD = "create table download_info ( _id integer primary key autoincrement,"
			+ "name text,finished integer, total integer)";
	private static final String DROP_DOWNLOAD = "drop table if exits download_info";
	private static final String DB_NAME = "downloadtask.db";
	private static final int VERSION = 1;
	private static DLDbHelper helper;

	private DLDbHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DB_NAME, null, VERSION);
	}

	public static DLDbHelper getInstance(Context context) {
		if (helper == null) {
			helper = new DLDbHelper(context, DB_NAME, null, VERSION);
		}
		return helper;
	}

	
	//µ¥ÀýÄ£Ê½
	private DLDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREAT_DOWNLOAD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_DOWNLOAD);
		db.execSQL(CREAT_DOWNLOAD);
	}

}
