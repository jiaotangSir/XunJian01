package com.jiaotang.xunjian01.mission;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jiaotang.xunjian01.R;


public class MissionUncompletedDetail extends AppCompatActivity {

    private TextView textViewUncompletedDetailId;
    private TextView textViewUncompletedDetailPlace;
    private MissionCondition missionCondition;
    private Button btnBack;
    private RadioGroup radioGroupResult;

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
        missionCondition = (MissionCondition) intent.getSerializableExtra("missionCondition");
//        根据数据显示在页面上
        textViewUncompletedDetailId.setText(missionCondition.missionId);
        textViewUncompletedDetailPlace.setText(missionCondition.missionPlace);

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

    public void clickMissionUncompletedDetailSubmit(View v){
//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//
//        bundle.putSerializable("missionConditionUpdate",missionCondition);
        finish();


    }



}
