package com.jiaotang.xunjian01.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiaotang.xunjian01.R;
import com.jiaotang.xunjian01.ui.OfflineActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    private ImageView ivToOfflineMap;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ivToOfflineMap = (ImageView) view.findViewById(R.id.toOfflineMap);
        /**箭头点击事件*/
        ivToOfflineMap.setOnClickListener(new MyListener());


        return view;
    }

    /**
     * 箭头点击事件
     */
    public class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                /**进入离线地图点击事件*/
                case R.id.toOfflineMap:
                    Intent intent = new Intent(getContext(), OfflineActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }


    /**重写setMenuVisibility方法，不然两个fragment会出现叠层的现象*/
    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }
}
