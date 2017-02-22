package com.jiaotang.xunjian01;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudRgcResult;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.jiaotang.xunjian01.ui.AbnormalRecord;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_map extends AppCompatActivity implements CloudListener,
        BaiduMap.OnMarkerClickListener,BaiduMap.OnMapLongClickListener,BaiduMap.OnMapClickListener {

    private LocationManager locationManager;
    private MapView mMapView;
    private BaiduMap baiduMap;
    private String provider;
    private boolean isFirstLocate = true;
    private Button btnFindMe;
    private Location location;
    private Button btnSearchAround;

    //长按地图获得的经纬度
    private double recordLat=0.0;
    private double recordLng=0.0;

    /**图层控制相关*/
    private View legendLayout;
    private boolean isShowLegend = false;//显示图例控制
    private List<Polyline> polylineList1 = new ArrayList<>();
    private List<Marker> markerList1 = new ArrayList<>();
    private List<Polyline> polylineList2 = new ArrayList<>();
    private List<Marker> markerList2 = new ArrayList<>();
    private List<Polyline> polylineList3 = new ArrayList<>();
    private List<Marker> markerList3 = new ArrayList<>();
    private boolean isShowRoute1 = false;//显示图例1的管线或路线
    private boolean isShowRoute2 = false;//显示图例2的管线或路线
    private boolean isShowRoute3 = false;//显示图例3的管线或路线

    /**覆盖物...*/
    private Marker abnormalMarker = null;//异常上报Marker
    private List<Marker> aroundMarkerList = new ArrayList<>();//周边设备MarkerList
    private Circle aroundCircle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        /**
         * 获取CloudManager的唯一实例，这句话必须写，否则获取数据是会崩溃
         * 在onDestroy()方法调用时要销毁实例*/
        CloudManager.getInstance().init(MainActivity_map.this);/**注意：必写*/
        mMapView = (MapView) findViewById(R.id.bmapView);
        btnFindMe = (Button) findViewById(R.id.findMe);
        btnSearchAround = (Button) findViewById(R.id.button_around);
        legendLayout = findViewById(R.id.legendLayout);
        legendLayout.setVisibility(View.INVISIBLE);

        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        //实现OnMarkerClickListener接口
        baiduMap.setOnMarkerClickListener(this);
        //实现长按地图响应接口
        baiduMap.setOnMapLongClickListener(this);
        //实现点击地图响应接口
        baiduMap.setOnMapClickListener(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            Log.d("data", "使用GPS定位");
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
            Log.d("data", "使用网络定位");
        } else {
            Log.d("data", "当前无法定位");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            navigateTo(location);
            Log.d("data","经纬度为："+location.getLatitude());
        }
        locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);

        //点击箭头回到定位
        btnFindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirstLocate = true;
                navigateTo(location);
            }
        });

        /**点击查询周边*/
        btnSearchAround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**查询前先清除以前的覆盖物*/
                if (aroundCircle != null){
                    aroundCircle.remove();
                    Log.d("data","清除周边Circle");
                }
                if (aroundMarkerList.size() != 0){
                    for (Marker m : aroundMarkerList) {
                        m.remove();

                    }
                    Log.d("data","清除周边marker");
                }
                baiduMap.hideInfoWindow();//隐藏信息框


                /**获得周边所有设备*/
                getAround();

                //对周边检索的范围进行绘制
                LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
                OverlayOptions ooCircle = new CircleOptions().fillColor( 0x66CCCC00 )
                        .center(center).stroke(new Stroke(5, 0xCCFF00FF ))
                        .radius(3000);
                aroundCircle = (Circle) baiduMap.addOverlay(ooCircle);

            }
        });


    }



    /**点击按钮“异常上报”跳转到异常上报页面*/
    public void clickAbnormal(View v){

        if ((recordLat != 0.0) & (recordLng != 0.0)){
            Intent intent = new Intent(MainActivity_map.this,AbnormalRecord.class);

            intent.putExtra("lat",recordLat);
            intent.putExtra("lng",recordLng);
            startActivity(intent);
        }else {
            Toast.makeText(MainActivity_map.this, "跳转失败，请先长按地图获得异常地点经纬度", Toast.LENGTH_LONG).show();
        }
    }

    /**获得周边所有设备*/
    private void getAround() {

        NearbySearchInfo info = new NearbySearchInfo();
        info.ak = "7uXuGG77barvr7lhL15KzTIUABmlmtef";
        info.geoTableId = 159344;
        info.radius = 3000;
//        String s = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());

        info.location = location.getLongitude()+","+location.getLatitude();
        CloudManager.getInstance().nearbySearch(info);



    }

    /**实现自身定位*/
    private void navigateTo(Location location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(14f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData = builder.build();
        baiduMap.setMyLocationData(locationData);
    }

    /**点击出现图例控制*/
    public void showLegend(View v) {

        if (!isShowLegend) {
            legendLayout.setVisibility(View.VISIBLE);
            isShowLegend = true;

        }else {
            legendLayout.setVisibility(View.INVISIBLE);
            isShowLegend = false;
        }
    }
    /**点击图例显示相关管线*/
    public void clickCheckBox(View v) {
        baiduMap.hideInfoWindow();//隐藏信息框
        switch ( v.getId()) {
            case R.id.legend1://显示第一个图例

                if (((CheckBox) v).isChecked()) {
                    if (isShowRoute1){
                        for (Polyline polyline : polylineList1) {
                            polyline.setVisible(true);
                        }
                        for (Marker marker : markerList1) {
                            marker.setVisible(true);
                        }
                    }else {
                        Log.d("data", ((CheckBox) v).getText().toString() + "被选中了");
                        //从网络获取管线数据或路径等位置信息，现在为模拟数据
                        List<LatLng> planPoints = new ArrayList<>();
                        LatLng p1 = new LatLng(39.816,116.166);
                        LatLng p2 = new LatLng(39.817,116.165);
                        LatLng p3 = new LatLng(39.819,116.167);
                        LatLng p4 = new LatLng(39.819,116.168);
                        planPoints.add(p1);
                        planPoints.add(p2);
                        planPoints.add(p3);
                        planPoints.add(p4);
                        //调用自定义的方法，显示折线和折点。
                        //画出折线
                        OverlayOptions polyline = new PolylineOptions().points(planPoints).width(8).color(0xAA00CED1);
                        Polyline myPolyline = (Polyline) baiduMap.addOverlay(polyline);
                        polylineList1.add(myPolyline);
                        //在每个折线点设置一个大头针
                        for (LatLng point : planPoints){
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.gcoding_purple);
                            OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);
                            Marker myMarker = (Marker) baiduMap.addOverlay(options);
                            myMarker.setTitle(point.latitude+","+point.longitude);
                            markerList1.add(myMarker);
                        }
                        isShowRoute1 = true;
                    }
                } else {
                    Log.d("data", ((CheckBox) v).getText().toString() + "取消选中");
                    for (Polyline polyline : polylineList1) {
                        polyline.setVisible(false);
                    }
                    for (Marker marker : markerList1) {
                        marker.setVisible(false);
                    }
                }
                break;
            case R.id.legend2://显示第二个图例
                if (((CheckBox) v).isChecked()) {
                    Log.d("data", ((CheckBox) v).getText().toString() + "被选中了");
                    if (isShowRoute2){
                        for (Polyline polyline : polylineList2) {
                            polyline.setVisible(true);
                        }
                        for (Marker marker : markerList2) {
                            marker.setVisible(true);
                        }
                    }else {
                        Log.d("data", ((CheckBox) v).getText().toString() + "被选中了");
                        //从网络获取管线数据或路径等位置信息，现在为模拟数据
                        List<LatLng> planPoints = new ArrayList<>();
                        LatLng p1 = new LatLng(39.806,116.166);
                        LatLng p2 = new LatLng(39.807,116.165);
                        LatLng p3 = new LatLng(39.809,116.167);
                        LatLng p4 = new LatLng(39.809,116.168);
                        planPoints.add(p1);
                        planPoints.add(p2);
                        planPoints.add(p3);
                        planPoints.add(p4);
                        //调用自定义的方法，显示折线和折点。
                        //画出折线
                        OverlayOptions polyline = new PolylineOptions().points(planPoints).width(8).color(0xAAFFD700);
                        Polyline myPolyline = (Polyline) baiduMap.addOverlay(polyline);
                        polylineList2.add(myPolyline);
                        //在每个折线点设置一个大头针
                        for (LatLng point : planPoints){
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.gcoding_purple);
                            OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);
                            Marker myMarker = (Marker) baiduMap.addOverlay(options);
                            markerList2.add(myMarker);
                        }
                        isShowRoute2 = true;
                    }
                } else {
                    Log.d("data", ((CheckBox) v).getText().toString() + "取消选中");
                    for (Polyline polyline : polylineList2) {
                        polyline.setVisible(false);
                    }
                    for (Marker marker : markerList2) {
                        marker.setVisible(false);
                    }
                }
                break;
            case R.id.legend3://显示第三个图例
                if (((CheckBox) v).isChecked()) {
                    Log.d("data", ((CheckBox) v).getText().toString() + "被选中了");
                    if (isShowRoute3){
                        for (Polyline polyline : polylineList3) {
                            polyline.setVisible(true);
                        }
                        for (Marker marker : markerList3) {
                            marker.setVisible(true);
                        }
                    }else {
                        Log.d("data", ((CheckBox) v).getText().toString() + "被选中了");
                        //从网络获取管线数据或路径等位置信息，现在为模拟数据
                        List<LatLng> planPoints = new ArrayList<>();
                        LatLng p1 = new LatLng(39.816,116.176);
                        LatLng p2 = new LatLng(39.817,116.175);
                        LatLng p3 = new LatLng(39.819,116.177);
                        LatLng p4 = new LatLng(39.819,116.178);
                        planPoints.add(p1);
                        planPoints.add(p2);
                        planPoints.add(p3);
                        planPoints.add(p4);
                        //调用自定义的方法，显示折线和折点。
                        //画出折线
                        OverlayOptions polyline = new PolylineOptions().points(planPoints).width(8).color(0xAAFF7F50);
                        Polyline myPolyline = (Polyline) baiduMap.addOverlay(polyline);
                        polylineList3.add(myPolyline);
                        //在每个折线点设置一个大头针
                        for (LatLng point : planPoints){
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.gcoding_purple);
                            OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);
                            Marker myMarker = (Marker) baiduMap.addOverlay(options);
                            markerList3.add(myMarker);
                        }
                        isShowRoute3 = false;
                    }
                } else {
                    Log.d("data", ((CheckBox) v).getText().toString() + "取消选中");
                    for (Polyline polyline : polylineList3) {
                        polyline.setVisible(false);
                    }
                    for (Marker marker : markerList3) {
                        marker.setVisible(false);
                    }
                }
                break;
            default:
                break;

        }
    }

    /**向地图添加折线表示管线位置或计划巡查路线*/
    private void planRoute(List<LatLng> points,int myColor){

        //画出折线
        OverlayOptions polyline = new PolylineOptions().points(points).width(8).color(myColor);
        baiduMap.addOverlay(polyline);
        //在每个折线点设置一个大头针
        for (LatLng point : points){
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.gcoding_purple);
            OverlayOptions options = new MarkerOptions().position(point).icon(bitmap);
            baiduMap.addOverlay(options);
        }

        Log.d("data", "画出折线和大头针");
    }


    /**接口locationListener，实现实时监听位置变化*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location1) {
            if (location1 != null) {
                location = location1;
                navigateTo(location1);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    /**activity生命周期*/
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
//        // 退出时销毁定位
//        mLocClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            locationManager.removeUpdates(locationListener);
        }

        CloudManager.getInstance().destroy();//销毁实例
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }




/**接口CloudListener的三个方法，云检索功能*/
    @Override
    public void onGetSearchResult(CloudSearchResult result, int i) {

        if (result != null && result.poiList != null
                && result.poiList.size() > 0) {


            BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
            LatLng ll;
            for (CloudPoiInfo info : result.poiList) {
                ll = new LatLng(info.latitude, info.longitude);
                OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
                Marker marker = (Marker)baiduMap.addOverlay(oo);
                marker.setTitle(info.title);
                aroundMarkerList.add(marker);

            }

        }
    }

    @Override
    public void onGetDetailSearchResult(DetailSearchResult detailSearchResult, int i) {

    }
    @Override
    public void onGetCloudRgcResult(CloudRgcResult result, int error) {

    }




    /**接口OnMarkClickListener的一个方法，点击大头针时调用*/
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker == null) {
            return false;
        }

        TextView tv = new TextView(MainActivity_map.this);
        tv.setBackgroundResource(R.drawable.popup);
        tv.setPadding(20,10,20,10);
        tv.setTextColor(Color.WHITE);
        tv.setText(marker.getTitle());
        tv.setGravity(Gravity.CENTER);

        InfoWindow mInfoWindow = new InfoWindow(tv, marker.getPosition(), -100);
        baiduMap.showInfoWindow(mInfoWindow);
        Log.d("data","获取标题："+marker.getTitle());
        return true;
    }



    /**长按地图监听事件*/
    @Override
    public void onMapLongClick(LatLng latLng) {

        //先清除之前的异常点
        if (abnormalMarker != null) {
            abnormalMarker.remove();
        }


        recordLat = latLng.latitude;
        recordLng = latLng.longitude;

        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions oo = new MarkerOptions().icon(bd).position(latLng);
        abnormalMarker = (Marker)baiduMap.addOverlay(oo);
        abnormalMarker.setTitle(String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
        Log.d("data","长按地图title："+String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));

        isShowRoute1 = false;
        isShowRoute2 = false;
        isShowRoute3 = false;
    }


    /**点击地图空白处接口方法  OnMapClickListener*/
    @Override
    public void onMapClick(LatLng latLng) {
        baiduMap.hideInfoWindow();
        Log.d("data","点击空白处取消弹出窗口");
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }
}
