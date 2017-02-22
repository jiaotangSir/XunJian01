package com.jiaotang.xunjian01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.jiaotang.xunjian01.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/15.
 */

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context myContext;//上下文
    private ArrayList<MKOLSearchRecord> records;//全国离线地图记录

    public MyExpandableListAdapter(Context context,ArrayList<MKOLSearchRecord> records) {
        myContext = context;
        this.records = records;
    }



    @Override/**父项size*/
    public int getGroupCount() {
        return records.size();
    }

    @Override/**下标为i的父项下的子项数组size*/
    public int getChildrenCount(int i) {
        if (records.get(i).cityType==1){
            MKOLSearchRecord record = records.get(i);//下标为i的父项
            return record.childCities.size();
        }else {
            return 0;
        }

    }

    @Override/**下标为i的父项*/
    public Object getGroup(int i) {
        return records.get(i);
    }

    @Override/**下标为i的父项下的下标为i1的子项*/
    public Object getChild(int i, int i1) {

        if (records.get(i).cityType==1){
            MKOLSearchRecord record = records.get(i);//下标为i的父项
            return record.childCities.get(i1);
        }else {
            return null;
        }

    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }



    /**此函数表示设置父项view，
     * 其中的参数 int i  表示某个父项的id
     *           boolean b 表示是否点击了父项（即可判断箭头是向上还是向下）*/
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.parent_item,null);
        }

        view.setTag(R.layout.parent_item,i);
        view.setTag(R.layout.child_item,-1);
        TextView textView = (TextView) view.findViewById(R.id.tvParent);
        ImageView ivArrow = (ImageView) view.findViewById(R.id.ivArrow);

        //获取下标为i的城市record，得到城市名和安装包大小
        String parentName = "";
        String parentSize = "";
        MKOLSearchRecord record = records.get(i);
        if (record!=null){
            //获取城市名称
            parentName = record.cityName;
            //获取安装包大小
            if (record.size < (1024 * 1024)) {
                parentSize = String.format("(%dK)", record.size / 1024);
            } else {
                parentSize = String.format("(%.1fM)", record.size / (1024 * 1024.0));
            }

        }


        /**判断是否有子城市列表*/
        if (records.get(i).cityType!=1){//若无子城市列表,显示箭头为向右，并且显示城市安装包大小
            ivArrow.setImageResource(R.drawable.arrow_right);
            /**显示父项城市名字和安装包大小
             * */
            textView.setText(parentName+""+parentSize);
            textView.setTextSize(18);

        }else {//若有子城市列表
            if (b) {
                ivArrow.setImageResource(R.drawable.arrow_down);
            } else {
                ivArrow.setImageResource(R.drawable.arrow_up);
            }
            /**显示父项城市名字*/
            textView.setText(parentName);
            textView.setTextSize(21);
        }

        return view;
    }


    /**此函数表示设置子项view*/
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_item, null);
        }
        view.setTag(R.layout.parent_item,i);
        view.setTag(R.layout.child_item,i1);
        TextView childName = (TextView) view.findViewById(R.id.tvChildName);

        /**实验
         * 根据records获得子项城市名字、大小和id
         * cityType==1这里表示各个省
         * 注意：有子城市*/
        if (records.get(i).cityType==1){
            //获得子城市列表
            ArrayList<MKOLSearchRecord> records2 = records.get(i).childCities;
            //获得该列表对应子城市名称
            MKOLSearchRecord r = records2.get(i1);
            //获得子项城市名字、大小和id
            String name = r.cityName;
            String size;
            if (r.size < (1024 * 1024)) {
                size = String.format("(%dK)", r.size / 1024);
            } else {
                size = String.format("(%.1fM)", r.size / (1024 * 1024.0));
            }

            //设置左侧为城市名称加安装包大小
            childName.setText(name+""+size);
        }




        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
