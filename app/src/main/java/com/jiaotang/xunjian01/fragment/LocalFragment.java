package com.jiaotang.xunjian01.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.jiaotang.xunjian01.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalFragment extends Fragment {


    private MKOfflineMap mOffline;
    private ArrayList<MKOLUpdateElement> downloadList = null;

    private DownloadingAdapter downloadingAdapter = null;

    public LocalFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local, container, false);

        mOffline = new MKOfflineMap();
        mOffline.init((MKOfflineMapListener) getActivity());

        //正在下载列表
        ListView loadingListView = (ListView) view.findViewById(R.id.loadingList);
        //获得所有下载城市信息
        downloadList = mOffline.getAllUpdateInfo();
        if (downloadList == null) {
            downloadList = new ArrayList<MKOLUpdateElement>();
        }
        downloadingAdapter = new DownloadingAdapter();
        loadingListView.setAdapter(downloadingAdapter);


        return view;
    }

    /**
     * 更新状态显示
     */
    public void updateView() {
        downloadList = mOffline.getAllUpdateInfo();
        if (downloadList == null) {
            downloadList = new ArrayList<MKOLUpdateElement>();
        }
        downloadingAdapter.notifyDataSetChanged();
    }

    //将传过来的城市信息进行处理
    public void getCityRecord(int cityID){
        mOffline.start(cityID);
        updateView();

    }


    /**正在下载列表适配器*/
    public class DownloadingAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return downloadList.size();
        }

        @Override
        public Object getItem(int index) {
            return downloadList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
            view = View.inflate(getContext(),
                    R.layout.downloading_map_item, null);
            initViewItem(view, e);
            return view;
        }

        void initViewItem(View view, final MKOLUpdateElement e) {
            ImageButton stopDownload = (ImageButton) view.findViewById(R.id.stopDownload);
            TextView cityInfo = (TextView) view.findViewById(R.id.cityName);
            TextView downloadProgress = (TextView) view.findViewById(R.id.downloadProgress);
            Button removeMap = (Button) view.findViewById(R.id.removeOfflineMap);


            if (e.ratio == 100){
                downloadProgress.setText(" ");
            }else {
                downloadProgress.setText(""+e.ratio + "%");
            }

            cityInfo.setText(e.cityName);

            //判断当前是在下载还是暂停
            if (e.status == MKOLUpdateElement.SUSPENDED || e.status == MKOLUpdateElement.WAITING) {//如果当前在暂停，就显示开始图标
                stopDownload.setImageResource(R.drawable.icon_start);

            }else if (e.status == MKOLUpdateElement.DOWNLOADING){//如果当前在下载，就显示暂停图片
                stopDownload.setImageResource(R.drawable.icon_stop);
            }


            //停止或开始下载
            stopDownload.setOnClickListener(new MyMapListener(e));

            //删除地图
            removeMap.setOnClickListener(new MyMapListener(e));
        }
    }

    //地图下载的开始、暂停、删除按钮响应事件
    class MyMapListener implements View.OnClickListener{

        private MKOLUpdateElement e;
        public MyMapListener(){

        }
        public MyMapListener(MKOLUpdateElement e){
            this.e = e;
        }
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                //删除地图
                case R.id.removeOfflineMap:
                    view.setBackgroundResource(R.drawable.icon_st);
                    mOffline.remove(e.cityID);
                    updateView();
                    break;
//                停止或开始下载
                case R.id.stopDownload:
                    Log.d("data","查看该城市是否在下载"+e.status);
                    if (e.status == MKOLUpdateElement.DOWNLOADING) {
                        mOffline.pause(e.cityID);
                        updateView();

                        Log.d("data","下载暂停");
                    }else if (e.status == MKOLUpdateElement.SUSPENDED){
                        mOffline.start(e.cityID);
                        updateView();
                        Log.d("data","重新开始下载");
                    }
                    break;
            }

        }
    }

}
