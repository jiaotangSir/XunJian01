package com.jiaotang.xunjian01.adapter;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.jiaotang.xunjian01.R;
import com.jiaotang.xunjian01.model.MissionCondition;

import java.util.List;
/**
 * Created by Administrator on 2016/11/2.
 */

public class MissionConditionAdapter extends ArrayAdapter<MissionCondition> {

    private int resourceId;
    public MissionConditionAdapter(Context context,int textViewResourceId,List<MissionCondition> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;

    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MissionCondition missionCondition = getItem(position);
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }else {
            view = convertView;
        }

        TextView missionId = (TextView) view.findViewById(R.id.textView_missionId);
        TextView missionCompletionStatus = (TextView) view.findViewById(R.id.textView_missionCompletionStatus);
        TextView missionPlace = (TextView) view.findViewById(R.id.textView_missionPlace);

        if (missionCondition != null) {
            missionId.setText(missionCondition.getMissionId());
            missionPlace.setText(missionCondition.getMissionPlace());
            missionCompletionStatus.setText(missionCondition.getMissionStatus());
        }

        return view;
    }


}
