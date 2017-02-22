package com.jiaotang.xunjian01.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jiaotang.xunjian01.MainActivity_mission;
import com.jiaotang.xunjian01.model.MissionCondition;
import com.jiaotang.xunjian01.adapter.MissionConditionAdapter;
import com.jiaotang.xunjian01.ui.MissionUncompletedDetail;
import com.jiaotang.xunjian01.util.DataMessage;
import com.jiaotang.xunjian01.R;
import com.jiaotang.xunjian01.util.MySqlHelper;
import com.jiaotang.xunjian01.util.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissionUncompletedFragment extends Fragment {

    private MissionConditionAdapter missionConditionAdapter;
    private DataMessage dataMessage;
    //定义待办任务数组unMissionConditionList
    private List<MissionCondition> unMissionConditionList = new ArrayList<>();
    private String[] data = {"1212","33333"};


    public MissionUncompletedFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mission_uncompleted, container, false);
        //取出dataMessage中的未完成任务的数据
        dataMessage = DataMessage.getSingleMessage();
        unMissionConditionList = dataMessage.unMissionConditionList;


//        重写listView
        missionConditionAdapter = new MissionConditionAdapter(
                getContext(),
                R.layout.mission_completed_item,
                unMissionConditionList);
        Log.d("data","数组大小："+unMissionConditionList.size());
        ListView listView = (ListView) view.findViewById(R.id.listView_uncompleted);
        listView.setAdapter(missionConditionAdapter);

        //listView点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //这里弹出详细界面，并传入本行任务的详细数据,数据待写

                //获取对应行的数据，以后会进行数据的传递
                MissionCondition missionCondition = unMissionConditionList.get(position);

                //进入待办任务详细页面
                Intent intent = new Intent(getContext(),MissionUncompletedDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("missionCondition", missionCondition);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                startActivityForResult(intent,10);

            }
        });


        /**设置数据上拉加载，下拉刷新*/
        // 获取RefreshLayout实例
        final RefreshLayout myRefreshListView = (RefreshLayout)
                view.findViewById(R.id.swipe_layout);

        // 设置下拉刷新时的颜色值,颜色值需要定义在xml中
//            myRefreshListView
//                    .setColorScheme(R.color.user_icon_1,
//                            R.color.user_icon_1, R.color.user_icon_1,
//                            R.color.user_icon_1);

        myRefreshListView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        myRefreshListView.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        // 设置下拉刷新监听器
        myRefreshListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                Log.d("data","刷新");

                myRefreshListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // 更新数据,耗时操作在子线程中进行
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

//                                getUnmissionData();
//                                MissionCondition mc = new MissionCondition("201622222","通过","北京丰台刷新");
//                                unMissionConditionList.add(mc);
                            }
                        }).start();

                        missionConditionAdapter.notifyDataSetChanged();
                        // 更新完后调用该方法结束刷新
                        myRefreshListView.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        // 加载监听器
        myRefreshListView.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {

                Log.d("data","加载");

                myRefreshListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
//                        getUnmissionData();
//                        MissionCondition mc = new MissionCondition("201622222","通过","北京丰台加载");
//                        unMissionConditionList.add(mc);
                        missionConditionAdapter.notifyDataSetChanged();
                        // 加载完后调用该方法
                        myRefreshListView.setLoading(false);
                    }
                }, 1500);

            }
        });


        return view;

    }


    public void getUnmissionData() {

        for (int i = 0; i <= 5; i++) {
            MissionCondition mc = new MissionCondition(String.valueOf(20161001 + i), "待办", "德州某地" + (i + 1));
            unMissionConditionList.add(mc);
        }
//        MySqlHelper helper = new MySqlHelper(getContext(), "Mission", null, 1);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        for (MissionCondition mc : unMissionConditionList) {
//            values.put("missionPlace", mc.getMissionPlace());
//            values.put("missionStatus", mc.getMissionStatus());
//            values.put("missionDetail", mc.getMissionDetail());
//            values.put("missionReason", mc.getMissionReason());
//            values.put("missionLat", mc.getMissionLat());
//            values.put("missionLng", mc.getMissionLng());
//            values.put("createDate", mc.getCreateDate());
//            values.put("updateDate", mc.getUpdateDate());
//            db.insert("Mission", null, values);
//            values.clear();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position", 10000);
                    if (position != 10000) {
                        unMissionConditionList.remove(position);
                        missionConditionAdapter.notifyDataSetChanged();
                        Log.d("data","数组大小："+unMissionConditionList.size());
                        Log.d("data", "移除mc"+position);

                        //刷新已办任务列表
                        MainActivity_mission mainActivity_mission = (MainActivity_mission) getActivity();
                        mainActivity_mission.refreshCompletedData();
                    }

                }
                break;
            default:
                break;
        }
    }
}
