package com.jiaotang.xunjian01;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jiaotang.xunjian01.fragment.HomeFragment;
import com.jiaotang.xunjian01.fragment.MeFragment;
import com.jiaotang.xunjian01.util.DataMessage;


public class MainActivity extends AppCompatActivity{


    private FrameLayout mHomeContent;
    private RadioGroup mHomeRadioGroup;
    static final int NUM_ITEMS = 2;//一共三个fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        //初始化数据
        DataMessage dataMessage = DataMessage.getSingleMessage();
        dataMessage.getUnmissionData();

        initView();
    }

    protected void initView() {
        mHomeContent = (FrameLayout) findViewById(R.id.content_frame); //tab上方的区域
        mHomeRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);  //底部的四个tab

        //监听事件：为底部的RadioGroup绑定状态改变的监听事件
        mHomeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = 0;
                switch (checkedId) {
                    case R.id.radio_home:
                        index = 0;
                        break;
                    case R.id.radio_me:
                        index = 1;
                        break;
                }
                //通过fragments这个adapter还有index来替换帧布局中的内容
                Fragment fragment = (Fragment) fragments.instantiateItem(mHomeContent, index);
                //一开始将帧布局中 的内容设置为第一个
                fragments.setPrimaryItem(mHomeContent, 0, fragment);
                fragments.finishUpdate(mHomeContent);

            }
        });
    }

    //第一次启动时，我们让mHomeHomeRb这个radiobutton处于选中状态。
    // 当然了，在这之前，先要在布局文件中设置其他的某一个radiobutton（只要不是mHomeHomeRb就行）
    // 的属性为android:checked="true"，才会出发下面的这个check方法切换到mHomeHomeRb
    @Override
    protected void onStart() {
        super.onStart();
        mHomeRadioGroup.check(R.id.radio_home);
    }

    //用adapter来管理三个Fragment界面的变化。注意，我这里用的Fragment都是v4包里面的
    FragmentStatePagerAdapter fragments = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public int getCount() {
            return NUM_ITEMS;//一共有四个Fragment
        }

        //进行Fragment的初始化
        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case 0://首页
                    fragment = new HomeFragment();
                    break;
                case 1://我
                    fragment = new MeFragment();
                    //  fragment = new YSHomeFagment();
                    break;
                default:
                    new HomeFragment();
                    break;
            }

            return fragment;
        }
    };



}
