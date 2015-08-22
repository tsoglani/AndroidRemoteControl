package com.tsoglani.mobilefinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "mobileFinderPrefDB";
	public static final String TABLE_PRODUCTS = "products";

	public static final String ID = "_id";
	public static final String IS_RUNNING_AT_START = "runs_at_start",    VIBRATE = "VIBRATE",    FLASH = "Flash";

	public DB(Context context, String name, CursorFactory factory, int version) {
		
			super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
		String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
				+ ID + " INTEGER PRIMARY KEY," + IS_RUNNING_AT_START + " TEXT,"+FLASH+" TEXT,"+VIBRATE+" TEXT)";
		db.execSQL(CREATE_PRODUCTS_TABLE);
		}catch(Exception e){
			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		onCreate(db);
	}

	public void addProduct(String runOnStart,String flash,String vibrate) {

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
		onCreate(db);
		ContentValues values = new ContentValues();
		values.put(IS_RUNNING_AT_START, runOnStart);////////////////////////
		values.put(FLASH, flash);
		values.put(VIBRATE, vibrate);
		db.insert(TABLE_PRODUCTS, null, values);
		db.close();
	}

	public String[] getData() {
		String query = "Select * FROM " + TABLE_PRODUCTS;
String []pinax= new String[3];
		SQLiteDatabase db = this.getWritableDatabase();
		String str = null;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			//Integer.parseInt(cursor.getString(0));
			pinax[0] = cursor.getString(1);
			pinax[1] = cursor.getString(2);
			pinax[2] = cursor.getString(3);
		
			cursor.close();
		}
		db.close();
		return pinax;
	}

}
