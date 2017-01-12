package com.jiaotang.xunjian01.model;


/**
 * Created by Administrator on 2016/11/4.
 */

public class RecordCondition {
    private String title;//标题
    private String status;//处理情况：已处理，待处理
    private String recordDate;//上报日期
    private int id;//事件ID
    private String detail;//上报内容详情
    private double recordLat;//上报地点维度
    private double recordLng;//上报地点经度
    private String level;//上报时间隐患程度：一级隐患、二级隐患、三级隐患

    public RecordCondition(String recordTitle,String recordCompletionStatus,String recordDate){
        this.title = recordTitle;
        this.status = recordCompletionStatus;
        this.recordDate = recordDate;

    }
    public RecordCondition(){}


    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getRecordLng() {
        return recordLng;
    }

    public void setRecordLng(double recordLng) {
        this.recordLng = recordLng;
    }

    public double getRecordLat() {
        return recordLat;
    }

    public void setRecordLat(double recordLat) {
        this.recordLat = recordLat;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
