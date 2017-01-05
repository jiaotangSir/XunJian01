package com.jiaotang.xunjian01.mission;

import android.widget.Toast;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/11/2.
 */

public class MissionCondition extends BmobObject implements Serializable{


    String missionId;
    String missionStatus;
    String missionPlace;
    String missionDetail;
    String missionReason;
    double missionLat;
    double missionLng;
    String createDate;
    String updateDate;


    public MissionCondition(){

    }
    public MissionCondition(String missionId,String missionCompletionStatus,String missionPlace){
        this.missionId = missionId;
        this.missionStatus = missionCompletionStatus;
        this.missionPlace = missionPlace;


    }
    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public void setMissionStatus(String missionCompletionStatus) {
        this.missionStatus = missionCompletionStatus;
    }

    public void setMissionPlace(String missionPlace) {
        this.missionPlace = missionPlace;
    }

    public String getMissionId() {
        return missionId;
    }

    public String getMissionStatus() {
        return missionStatus;
    }

    public String getMissionPlace() {
        return missionPlace;
    }

    public String getMissionDetail() {
        return missionDetail;
    }

    public void setMissionDetail(String missionDetail) {
        this.missionDetail = missionDetail;
    }

    public String getMissionReason() {
        return missionReason;
    }

    public void setMissionReason(String missionReason) {
        this.missionReason = missionReason;
    }

    public double getMissionLat() {
        return missionLat;
    }

    public void setMissionLat(double missionLat) {
        this.missionLat = missionLat;
    }

    public double getMissionLng() {
        return missionLng;
    }

    public void setMissionLng(double missionLng) {
        this.missionLng = missionLng;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
