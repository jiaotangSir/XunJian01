package com.jiaotang.xunjian01.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jiaotang.xunjian01.model.MissionCondition;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/11/22.
 */

public class DataMessage {
    private static DataMessage missionMessage = null;

    public static DataMessage getSingleMessage(){

        if (missionMessage == null){
            missionMessage = new DataMessage();
        }
        return missionMessage;
    }


    public List<MissionCondition> unMissionConditionList = new ArrayList<>();

    public void getUnmissionData(){


        for (int i=0; i<=4; i++){
            MissionCondition mc = new MissionCondition(String.valueOf(20161001+i),"待办","德州某地"+(i+1));
            unMissionConditionList.add(mc);
        }
//        MySqlHelper helper = new MySqlHelper(context, "Mission", null, 1);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        for (MissionCondition mc : unMissionConditionList) {
//            values.put("missionPlace",mc.getMissionPlace());
//            values.put("missionStatus",mc.getMissionStatus());
//            values.put("missionDetail",mc.getMissionDetail());
//            values.put("missionReason",mc.getMissionReason());
//            values.put("missionLat",mc.getMissionLat());
//            values.put("missionLng",mc.getMissionLng());
//            values.put("createDate",mc.getCreateDate());
//            values.put("updateDate",mc.getUpdateDate());
//            db.insert("Mission", null, values);
//            values.clear();
//        }





////查询数组missionList中missionStatus为“待办”的数据，并添加到unMissionConditionList数组中
//            BmobQuery<MissionCondition> query = new BmobQuery<>();
////查询数据类MissionCondition中的字段missionStatus叫“待办”的数据
//            query.addWhereEqualTo("missionStatus", "待办");
////返回20条数据，如果不加上这条语句，默认返回10条数据
//            query.setLimit(20);
////        query.setSkip(10);
//        query.order("createAt,missionPlace");
////执行查询方法
//            query.findObjects(new FindListener<MissionCondition>() {
//                @Override
//                public void done(List<MissionCondition> object, BmobException e) {
//                    if(e==null){
//
//                        unMissionConditionList = object;
//                        Log.d("data",object.get(0).getMissionPlace());
//                    }else{
////                        Toast.makeText(context,e.getMessage()+"失败",Toast.LENGTH_LONG).show();
//                        Log.d("data","获取数据失败");
//                    }
//                }
//            });

    }
}
