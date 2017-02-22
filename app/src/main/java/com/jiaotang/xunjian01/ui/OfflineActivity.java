package com.jiaotang.xunjian01.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.jiaotang.xunjian01.R;
import com.jiaotang.xunjian01.fragment.CityListFragment;
import com.jiaotang.xunjian01.fragment.LocalFragment;

import java.util.ArrayList;

public class OfflineActivity extends AppCompatActivity implements MKOfflineMapListener {

    private ArrayList<Fragment> mFragmentList=new ArrayList<>();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager offlineVP;
    private Button clButton;
    private Button localButton;

    //下载管理fragment
    private LocalFragment localFragment;

    //离线地图变量
    private MKOfflineMap mOffline = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        clButton = (Button) findViewById(R.id.clButton);
        localButton = (Button) findViewById(R.id.localButton);
        offlineVP = (ViewPager) findViewById(R.id.offlineVP);

        //初始化离线地图
        mOffline = new MKOfflineMap();
        mOffline.init(this);

        mFragmentList.add(new CityListFragment(mOffline));
//        localFragment = new LocalFragment(mOffline);
        localFragment = new LocalFragment();
        mFragmentList.add(localFragment);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        offlineVP.setAdapter(mSectionsPagerAdapter);

        //点击跳转到城市列表
        clButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offlineVP.setCurrentItem(0,true);
            }
        });
        //点击跳转到下载管理
        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offlineVP.setCurrentItem(1,true);
                Log.d("data","点击了第二个");
            }
        });
    }

    /**调用下载管理localFragment的方法*/
    public void toLocalFragment(int cityId){
        //跳转到LocalFragment
        offlineVP.setCurrentItem(1,true);
        localFragment.getCityRecord(cityId);
    }


    @Override
    public void onGetOfflineMapState(int type, int state) {


        switch (type) {

            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
                    localFragment.updateView();
                    Log.d("data","正在下载"+update.cityName + update.cityID);
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlineMap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        /**退出时销毁地图模块*/
        mOffline.destroy();
        super.onDestroy();

    }

    /**fragment适配器*/
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);

            int currentItem = offlineVP.getCurrentItem();
            if (currentItem == 0){
                clButton.setTextColor(0xff4196fd);
                localButton.setTextColor(0xffaaaaaa);
            }else {
                localButton.setTextColor(0xff4196fd);
                clButton.setTextColor(0xffaaaaaa);
            }
        }

    }
}
