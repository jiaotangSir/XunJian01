package com.jiaotang.xunjian01;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.jiaotang.xunjian01.record.RecordCondition;
import com.jiaotang.xunjian01.util.MySqlHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_record extends AppCompatActivity {

    private ArrayList<RecordCondition> mRecordCondition = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_record);

        initRecordCondition();
        ListView listView = (ListView) findViewById(R.id.listView_record);
        RecordAdapter recordAdapter = new RecordAdapter(this,R.layout.record_item,mRecordCondition);
        listView.setAdapter(recordAdapter);
    }
    public void clickBack(View v){
        finish();
    }

    //    初始化数据，后续将加入数据库
    private void initRecordCondition(){

        //从本地查询XunJian数据库的Record表信息，并显示出来
        //1、创建或读取XunJian数据库，获得SQLiteDatabase对象
        MySqlHelper dbHelper = new MySqlHelper(MainActivity_record.this, "XunJian.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //2、利用SQLiteDatabase对象查询XunJian数据库中Record表所有数据
        Cursor cursor = db.rawQuery("select * from Record ORDER BY recordDate",null);
        if (cursor.moveToFirst()) {
            do {
                RecordCondition recordCondition = new RecordCondition();
                recordCondition.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                recordCondition.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                recordCondition.setRecordDate(cursor.getString(cursor.getColumnIndex("recordDate")));
                recordCondition.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
                recordCondition.setLevel(cursor.getString(cursor.getColumnIndex("level")));
                recordCondition.setRecordLat(cursor.getDouble(cursor.getColumnIndex("recordLat")));
                recordCondition.setRecordLng(cursor.getDouble(cursor.getColumnIndex("recordLng")));

                mRecordCondition.add(recordCondition);
            }while (cursor.moveToNext());
        }
        cursor.close();

    }



//    自定义listView适配器
    public class RecordAdapter extends ArrayAdapter<RecordCondition>{

    private int resourceId;

    public RecordAdapter(Context context, int resource, List<RecordCondition> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecordCondition recordCondition = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView recordTitle = (TextView) view.findViewById(R.id.text_record_title);
        TextView recordCompletionStatus = (TextView) view.findViewById(R.id.text_record_completionStatus);
        TextView recordDate = (TextView) view.findViewById(R.id.text_record_date);
        recordTitle.setText(recordCondition.getTitle());
        recordCompletionStatus.setText(recordCondition.getStatus());
        recordDate.setText(recordCondition.getRecordDate());

        return view;
    }
}
}
