package com.jiaotang.xunjian01.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jiaotang.xunjian01.R;
import com.jiaotang.xunjian01.ui.MissionCompletedDetail;
import com.jiaotang.xunjian01.model.MissionCondition;
import com.jiaotang.xunjian01.adapter.MissionConditionAdapter;
import com.jiaotang.xunjian01.util.MySqlHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissionCompletedFragment extends Fragment {

    private List<MissionCondition> missionConditionList = new ArrayList<MissionCondition>();
    public MissionCompletedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        初始化数据，以后是从数据库解析数据
        initMissionCondition();
//        加载布局layout_main_mission_completed
        View view = inflater.inflate(R.layout.fragment_mission_completed, container, false);
//        重写listView
        MissionConditionAdapter missionConditionAdapter = new MissionConditionAdapter(getContext(),
                R.layout.mission_completed_item,missionConditionList);
        ListView listView = (ListView) view.findViewById(R.id.listView_completed);
        listView.setAdapter(missionConditionAdapter);
//        点击listView进入已办任务详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),"ddddd",Toast.LENGTH_LONG).show();
//                这里传递数据给详情页面
                Intent intent = new Intent(getContext(),MissionCompletedDetail.class);
                startActivity(intent);
            }
        });
        return view;


    }

    //        初始化数据，以后是从数据库解析数据
    private void initMissionCondition(){

//        MissionCondition missionCondition1 =  new MissionCondition("20161101","已办","德州某地1");
//        missionConditionList.add(missionCondition1);
//        MissionCondition missionCondition2 =  new MissionCondition("20161102","已办","德州某地2");
//        missionConditionList.add(missionCondition2);

        MySqlHelper helper = new MySqlHelper(getContext(), "Mission", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Mission", null);
        if (cursor.moveToFirst()) {
            do {
                MissionCondition mc = new MissionCondition();
                int missionId = cursor.getInt(cursor.getColumnIndex("id")) + 20160000;
                mc.setMissionId(String.valueOf(missionId));
                mc.setMissionPlace(cursor.getString(cursor.getColumnIndex("missionPlace")));
                mc.setMissionStatus(cursor.getString(cursor.getColumnIndex("missionStatus")));
                missionConditionList.add(mc);
            } while (cursor.moveToNext());
        }cursor.close();

    }

}
