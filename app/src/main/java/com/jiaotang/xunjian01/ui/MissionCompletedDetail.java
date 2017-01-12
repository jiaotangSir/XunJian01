package com.jiaotang.xunjian01.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jiaotang.xunjian01.R;

public class MissionCompletedDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_completed_detail);



    }

    public void clickMissionCompletedDetailBack(View v){
        finish();
    }
}
