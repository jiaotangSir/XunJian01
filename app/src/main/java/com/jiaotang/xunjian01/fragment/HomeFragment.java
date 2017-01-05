package com.jiaotang.xunjian01.fragment;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiaotang.xunjian01.MainActivity_map;
import com.jiaotang.xunjian01.MainActivity_mission;
import com.jiaotang.xunjian01.MainActivity_record;
import com.jiaotang.xunjian01.R;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGABannerUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private BGABanner bgaBanner;
    private List<View> views = new ArrayList<>();

    //三个imageView
    private ImageView ivMission;
    private ImageView ivMap;
    private ImageView ivRecord;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bgaBanner=(BGABanner)view.findViewById(R.id.banner_content);
        ivMission = (ImageView) view.findViewById(R.id.home_mission);
        ivMap = (ImageView) view.findViewById(R.id.home_map);
        ivRecord = (ImageView) view.findViewById(R.id.home_record);

        //添加轮播图片
        views.add(BGABannerUtil.getItemImageView(getActivity(), R.drawable.login_xun));
        views.add(BGABannerUtil.getItemImageView(getActivity(), R.drawable.test1));
        views.add(BGABannerUtil.getItemImageView(getActivity(), R.drawable.test2));
        //添加轮播文案
        List<String> tips = new ArrayList<String>();
        tips.add("欢迎来到巡检中心");
        tips.add("XX小区发生水管泄露，请迅速处理");
        tips.add("XX小区发生水管泄露，请迅速处理");
        //实现显示
        bgaBanner.setData(views,null,tips);
        //设置轮转点击事件
        bgaBanner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {
                Log.d("data","点击了："+position);
            }
        });


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //点击图片跳转到任务界面
        ivMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity_mission.class);
                startActivity(intent);
            }
        });
        //点击图片跳转到地图界面
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity_map.class);
                startActivity(intent);
            }
        });
        //点击图片跳转到上报记录界面
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity_record.class);
                startActivity(intent);
            }
        });


    }

    //重写setMenuVisibility方法，不然会出现叠层的现象
    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }

}
