package com.jiaotang.xunjian01.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.jiaotang.xunjian01.R;
import com.jiaotang.xunjian01.adapter.MyExpandableListAdapter;
import com.jiaotang.xunjian01.ui.OfflineActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CityListFragment extends Fragment{

    //离线地图变量
    private MKOfflineMap mOffline = null;
    //扩展列表
    private ExpandableListView cityList;//全国普通城市列表
    private ListView hotCityList;//热门城市列表
    //热门城市数组
    private ArrayList<MKOLSearchRecord> records1;
    //全国城市数组
    private ArrayList<MKOLSearchRecord> records2;
    //获取该fragment所在Activity的实例：TestOfflineDemo
    private OfflineActivity offlineActivity;


    /**通过构造方法得到activity的离线地图类MKOfflineMap*/
    public CityListFragment(MKOfflineMap offline) {
        // Required empty public constructor
        mOffline = offline;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_city_list, container, false);

        hotCityList = (ListView) view.findViewById(R.id.hotcitylist);
        cityList = (ExpandableListView) view.findViewById(R.id.myExpandable);

        //获取该fragment所在Activity的实例：TestOfflineDemo
        offlineActivity = (OfflineActivity) getActivity();

        //初始化热门城市列表和城市列表
        initHotList();
        initCityList();
//
//
        return view;
    }

    /**初始化热门城市列表*/
    private void initHotList(){
        // 获取热门城市列表
        records1 = mOffline.getHotCityList();
        MyHotAdapter hotAdapter = new MyHotAdapter(getActivity(),R.layout.child_item,records1);

        hotCityList.setAdapter(hotAdapter);

        /**热门城市点击事件*/
        hotCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MKOLSearchRecord record = records1.get(i);

                //调用所在Activity的方法，将信息record传到下载管理fragment中
                offlineActivity.toLocalFragment(record.cityID);
                Log.d("data","点击了"+record.cityName+record.cityID);

            }
        });

    }
    /**初始化全国城市列表*/
    private void initCityList(){
        // 获取全国城市列表
        records2 = mOffline.getOfflineCityList();
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(getContext(), records2);
        cityList.setAdapter(adapter);
        cityList.setGroupIndicator(null);

        /**子项点击事件*/
        cityList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                //点击的子项城市record为
                MKOLSearchRecord record = records2.get(i).childCities.get(i1);

                //调用所在Activity的方法，将信息record传到下载管理fragment中
                offlineActivity.toLocalFragment(record.cityID);
                return true;
            }
        });
        /**父项点击事件*/
        cityList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                /**如何4<i<32,则表示有子项，不响应点击事件，返回false*/
                if (i>4&&i<32){
                    return false;
                }
                /**反之,则表示没有子项，就需要响应点击事件，返回true*/
                MKOLSearchRecord record = records2.get(i);

                //调用所在Activity的方法，将信息record传到下载管理fragment中
                offlineActivity.toLocalFragment(record.cityID);

                return true;

            }
        });


    }


    /**热门城市listView适配器*/
    public class MyHotAdapter extends ArrayAdapter<MKOLSearchRecord> {

        private int resourceId;
        public MyHotAdapter(Context context, int resource, List<MKOLSearchRecord> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MKOLSearchRecord record = getItem(position);
            View view;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            }else {
                view = convertView;
            }

            TextView childName = (TextView) view.findViewById(R.id.tvChildName);

            if (record!=null){
                //获取城市名称
                String name = record.cityName;
                //获取安装包大小
                String size;
                if (record.size < (1024 * 1024)) {
                    size = String.format("(%dK)", record.size / 1024);
                } else {
                    size = String.format("(%.1fM)", record.size / (1024 * 1024.0));
                }

                //设置左侧为城市名称加安装包大小
                childName.setText(name + "" + size);

                Log.d("data","城市名："+name+",id:"+record.cityID);

            }

            return view;
        }


    }


}
