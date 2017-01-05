package com.jiaotang.xunjian01;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class MainActivity_map extends AppCompatActivity implements CloudListener,BaiduMap.OnMarkerClickListener,BaiduMap.OnMapLongClickListener {

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

        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        //实现OnMarkerClickListener接口
        baiduMap.setOnMarkerClickListener(this);
        //实现长按地图响应接口
        baiduMap.setOnMapLongClickListener(this);



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

        //点击查询周边
        btnSearchAround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baiduMap.clear();

                getAround();

                //对周边检索的范围进行绘制

                LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
                OverlayOptions ooCircle = new CircleOptions().fillColor( 0xCCCCCC00 )
                        .center(center).stroke(new Stroke(5, 0xFFFF00FF ))
                        .radius(3000);
                baiduMap.addOverlay(ooCircle);


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
        info.radius = 30000;
//        String s = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());

        info.location = "116.167727,39.815400";
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

        baiduMap.clear();
        recordLat = latLng.latitude;
        recordLng = latLng.longitude;

        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions oo = new MarkerOptions().icon(bd).position(latLng);
        Marker marker = (Marker)baiduMap.addOverlay(oo);
        marker.setTitle(String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
        Log.d("data","长按地图title："+String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));

    }
}
