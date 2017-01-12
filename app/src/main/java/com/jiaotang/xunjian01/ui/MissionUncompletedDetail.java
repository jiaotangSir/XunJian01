package com.jiaotang.xunjian01.ui;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.jiaotang.xunjian01.R;
import com.jiaotang.xunjian01.model.MissionCondition;

import java.text.SimpleDateFormat;

import static java.text.DateFormat.getDateInstance;


public class MissionUncompletedDetail extends AppCompatActivity {

    private TextView textViewUncompletedDetailId;
    private TextView textViewUncompletedDetailPlace;
    private MissionCondition missionCondition;
    private Button btnBack;
    private RadioGroup radioGroupResult;
    private boolean isReload = false;

    //上一页面点击listView的position
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_uncompleted_detail);

        //编号及区域
        textViewUncompletedDetailId = (TextView) findViewById(R.id.textView_mission_uncompleted_detail_Id);
        textViewUncompletedDetailPlace = (TextView) findViewById(R.id.textView_mission_uncompleted_detail_place);
        btnBack = (Button) findViewById(R.id.button_mission_uncompleted_detail_back);
        radioGroupResult = (RadioGroup) findViewById(R.id.radioGroup_mission_uncompleted_detail_result);

        //取出上个页面发给的数据
        Intent intent = getIntent();
        position = intent.getIntExtra("position",10000);
        missionCondition = (MissionCondition) intent.getSerializableExtra("missionCondition");
//        根据数据显示在页面上
        textViewUncompletedDetailId.setText(missionCondition.getMissionId());
        textViewUncompletedDetailPlace.setText(missionCondition.getMissionPlace());

        //绑定radioGroup的一个匿名监听器
//        radioGroupResult.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (group.getCheckedRadioButtonId()){
//                    case R.id.radioButton_mission_uncompleted_detail_result1:
//                        missionCondition.setMissionCompletionStatus("已办");
//                        break;
//                    case R.id.radioButton_mission_uncompleted_detail_result2:
//                        missionCondition.setMissionCompletionStatus("待办");
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**开始巡检*/
    public void clickStart(View view) {

        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new java.util.Date());
        missionCondition.setCreateDate(date);

        Intent intent = new Intent(this, MissionMap.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("missionCondition", missionCondition);
        intent.putExtras(bundle);
        startActivityForResult(intent,11);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isReload) {
            Intent intent = new Intent();

            intent.putExtra("position",position);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 11:
                if (resultCode == RESULT_OK) {
                    isReload = data.getBooleanExtra("reload", false);
                }
        }
    }
}
