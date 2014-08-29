package com.gocharm.lohaskh;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static final String DB_NAME = "LOHASKH";
	private final static int DB_VERSION = 1;
	
	private static final String TICKET_TABLE_NAME = "ticket";
	private static final String ART_TABLE_NAME = "art";
	private static final String VISTA_TABLE_NAME = "vista";
	
    public final static String FIELD_ID="_id"; 
    public final static String FIELD_TITLE="title";
    public final static String FIELD_IMAGEURL="imgURL";
    public final static String FIELD_SPID="spID";//twShow
    public final static String FIELD_NGID="ngID";//twVista
    public final static String FIELD_SIID="siID";//iticket
    public final static String FIELD_TEL="tel";
    public final static String FIELD_ADDR="addr";
    public final static String FIELD_SHARE="share";
    public final static String FIELD_LAT="lat";
    public final static String FIELD_LNG="lng";
    public final static String FIELD_OPEN="openTime";
    
    public final static int TABLE_ART = 3;
    public final static int TABLE_VISTA = 2;
    public final static int TABLE_TICKET = 1;
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//create ticket table
		String sql = "CREATE TABLE IF NOT EXISTS "+ TICKET_TABLE_NAME + "(" + 
					 FIELD_ID + " integer primary key autoincrement," +
					 FIELD_SIID + " text," +
					 FIELD_IMAGEURL + " text," +
					 FIELD_TITLE + " text, " +
					 FIELD_TEL + " text, " +
					 FIELD_ADDR + " text, " +
					 FIELD_LAT + " text, " +
					 FIELD_LNG + " text, " +
					 FIELD_SHARE + " text" +
					 ");";
		db.execSQL(sql);
		//create vista table
		sql = "CREATE TABLE IF NOT EXISTS "+ VISTA_TABLE_NAME + "(" + 
					FIELD_ID + " integer primary key autoincrement," +
					FIELD_NGID + " text," +
					FIELD_IMAGEURL + " text," + 
					FIELD_TITLE + " text, " +
					FIELD_TEL + " text, " +
					FIELD_ADDR + " text, " +
					FIELD_LAT + " text, " +
					FIELD_LNG + " text, " +
					FIELD_OPEN + " text, " +
					FIELD_SHARE + " text" +
					");";
		db.execSQL(sql);
		sql = "CREATE TABLE IF NOT EXISTS "+ ART_TABLE_NAME + "(" + 
				FIELD_ID + " integer primary key autoincrement," +
				FIELD_SPID + " text," +
				FIELD_IMAGEURL + " text," + 
				FIELD_TITLE + " text, " +
				FIELD_ADDR + " text, " +
				FIELD_SHARE + " text" +
				");";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	public Cursor select(int table)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = null;
        switch (table) {
	        case TABLE_ART:
	        	cursor=db.query(ART_TABLE_NAME, null, null, null, null, null,  " _id desc");
	        	break;
	        	
	        case TABLE_VISTA:
	        	cursor=db.query(VISTA_TABLE_NAME, null, null, null, null, null,  " _id desc");
	        	break;
	        	
	        case TABLE_TICKET:
	        	cursor=db.query(TICKET_TABLE_NAME, null, null, null, null, null,  " _id desc");
	        	break;
        }
        return cursor;
    }
    
	public boolean hasID(String id, int table) {
		SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        switch (table) {
	        case TABLE_ART:
	        	cursor = db.rawQuery("select * from " + ART_TABLE_NAME + " where " + FIELD_SPID + "=?;", new String[]{id});
	        	break;
	        	
	        case TABLE_VISTA:
	        	cursor = db.rawQuery("select * from " + VISTA_TABLE_NAME + " where " + FIELD_NGID + "=?;", new String[]{id});
	        	break;
	        	
	        case TABLE_TICKET:
	        	cursor = db.rawQuery("select * from " + TICKET_TABLE_NAME + " where " + FIELD_SIID + "=?;", new String[]{id});
	        	break;
	    }
        if(cursor.getCount() == 0) {
        	db.close();
        	cursor.close();
        	return false;
        }
        db.close();
        cursor.close();
        return true;
	}
	
    public long insert(Map<String, String> mapParam, int table) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        long row = 0;
        
        switch (table) {
	        case TABLE_ART:
	        	cv.put(FIELD_TITLE, mapParam.get("title"));
	        	cv.put(FIELD_SPID, mapParam.get("spID"));
	        	cv.put(FIELD_IMAGEURL, mapParam.get("imgURL"));
	        	cv.put(FIELD_ADDR, mapParam.get("addr"));
	        	cv.put(FIELD_SHARE, mapParam.get("share"));
	        	row=db.insert(ART_TABLE_NAME, null, cv);
	        	break;
	        	
	        case TABLE_VISTA:
	        	cv.put(FIELD_TITLE, mapParam.get("title"));
	        	cv.put(FIELD_NGID, mapParam.get("ngID"));
	        	cv.put(FIELD_IMAGEURL, mapParam.get("imgURL"));
	        	cv.put(FIELD_ADDR, mapParam.get("addr"));
	        	cv.put(FIELD_TEL, mapParam.get("tel"));
	        	cv.put(FIELD_SHARE, mapParam.get("share"));
	        	cv.put(FIELD_LAT, mapParam.get("lat"));
	        	cv.put(FIELD_LNG, mapParam.get("lng"));
	        	cv.put(FIELD_OPEN, mapParam.get("openTime"));
	        	row=db.insert(VISTA_TABLE_NAME, null, cv);
	        	break;
	        	
	        case TABLE_TICKET:
	        	cv.put(FIELD_TITLE, mapParam.get("title"));
	        	cv.put(FIELD_SIID, mapParam.get("siID"));
	        	cv.put(FIELD_IMAGEURL, mapParam.get("imgURL"));
	        	cv.put(FIELD_ADDR, mapParam.get("addr"));
	        	cv.put(FIELD_TEL, mapParam.get("tel"));
	        	cv.put(FIELD_SHARE, mapParam.get("share"));
	        	cv.put(FIELD_LAT, mapParam.get("lat"));
	        	cv.put(FIELD_LNG, mapParam.get("lng"));
	        	row=db.insert(TICKET_TABLE_NAME, null, cv);
	        	break;
	    }
        
        return row;
    }
    
    public void delete(String id, int table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "";
        String[] whereValue = new String[] {id};
        
        switch (table) {
	        case TABLE_ART:
	        	where = FIELD_SPID+"=?";
	            db.delete(ART_TABLE_NAME, where, whereValue);
	        	break;
	        	
	        case TABLE_VISTA:
	        	where = FIELD_NGID+"=?";
	            db.delete(VISTA_TABLE_NAME, where, whereValue);
	        	break;
	        	
	        case TABLE_TICKET:
	        	where = FIELD_SIID+"=?";
	            db.delete(TICKET_TABLE_NAME, where, whereValue);
	        	break;
	    }
    }
}
