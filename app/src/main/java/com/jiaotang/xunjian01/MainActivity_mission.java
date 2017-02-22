package com.jiaotang.xunjian01;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import com.jiaotang.xunjian01.fragment.MissionCompletedFragment;
import com.jiaotang.xunjian01.fragment.MissionUncompletedFragment;

import java.util.ArrayList;



public class MainActivity_mission extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> mFragmentList=new ArrayList<>();
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private MissionCompletedFragment missionCompletedFragment = new MissionCompletedFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mission);

        /**这里是加入viewPager*/
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager_mission);
//
        mFragmentList.add(new MissionUncompletedFragment());
        mFragmentList.add(missionCompletedFragment);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }
    public void clickBack(View v){
        finish();
    }

    //调用MissionCompletedFragment的方法，刷新已办任务列表数据
    public void refreshCompletedData(){
        missionCompletedFragment.refreshCompletedData();
    }



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
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "待办任务";
                case 1:
                    return "已办任务";

            }
            return null;
        }
    }


}
