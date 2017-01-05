package com.jiaotang.xunjian01.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/19.
 */

public class MySqlHelper extends SQLiteOpenHelper {

    //创建“异常上传”表
    public static final String CREATE_RECORD = " create table Record("
            + "id integer primary key autoincrement,"
            + "title text,"
            + "status text,"
            + "detail text,"
            + "level text,"
            + "recordLng real,"
            + "recordLat real,"
            + "recordDate text)";
    //创建“任务信息”表
    public static final String CREATE_MISSION = " create table Mission("
            + "missionId integer primary key autoincrement,"
            + "missionPlace text,"
            + "missionStatus text,"
            + "missionDetail text,"
            + "missionReason text,"
            + "missionLat real,"
            + "missionLng real,"
            + "createDate text,"
            + "updateDate text)";



    public MySqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD);
        db.execSQL(CREATE_MISSION);
        Log.d("data","成功创建表");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
