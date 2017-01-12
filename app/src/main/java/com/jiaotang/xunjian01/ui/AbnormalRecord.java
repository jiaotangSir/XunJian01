package com.jiaotang.xunjian01.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jiaotang.xunjian01.R;
import com.jiaotang.xunjian01.model.RecordCondition;
import com.jiaotang.xunjian01.util.MySqlHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbnormalRecord extends AppCompatActivity {

    private Button button_back;
    private Spinner spinner;
    private String[] abnormalLevel = {"请选择隐患等级","一级隐患","二级隐患","三级隐患"};
    private EditText place;
    private EditText title;
    private EditText detail;

    private RecordCondition record = new RecordCondition();

    //图片上传
    private static final int TAKE_PHOTO = 1;
    private static final int CROP_PHOTO = 2;
    private ImageView ivControl;
    private ImageView iv1;
    private ImageView expandImage;
    private Uri imageUri;
    private File imageFile;
    private Bitmap newBitmap;

    //实例化MySqlHelper
    private MySqlHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormal_record);

        button_back = (Button)findViewById(R.id.button_abnormalBack);
        spinner = (Spinner) findViewById(R.id.spinner_abnormal_level);
        ivControl = (ImageView) findViewById(R.id.imageView_abnormal);
        iv1 = (ImageView) findViewById(R.id.iv_describe1);
        expandImage = (ImageView) findViewById(R.id.expandImage);
        place = (EditText) findViewById(R.id.editText_abnormal_place);
        title = (EditText) findViewById(R.id.editText_abnormal_title);
        detail = (EditText) findViewById(R.id.editText_abnormal_detail);

        //创建xunImages文件夹来保存要上传的图片
        String s = Environment.getExternalStorageDirectory().getPath();
        imageFile = new File(s+File.separator+"xunImages");
        imageFile.mkdirs();


        //设置editView不可编辑
        place.setFocusable(false);
        place.setFocusableInTouchMode(false);
        //提取上个页面传过来的经纬度数据
        Intent intent = getIntent();
        double lat1 = intent.getDoubleExtra("lat",36.0);
        double lng1 = intent.getDoubleExtra("lng",116.0);
        //将经纬度放入recordCondition类中
        record.setRecordLat(lat1);
        record.setRecordLng(lng1);
        Log.d("data", "cheng gong");

        //给显示经纬度的editText赋值
        place.setText(String.valueOf(lat1)+" , "+String.valueOf(lng1));

        //初始化spinner下拉列表
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,abnormalLevel);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //实现spinner事件监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取spinner中的值,并放入了record类中
                String currentSpinner = parent.getItemAtPosition(position).toString();
                record.setLevel(currentSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //返回按钮点击事件
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //上传图片点击事件
        ivControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取当前时间，使图片的命名不重复
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String dateStr = format.format(new Date());

                //创建output_image.jpg文件，environment.getextrnalStorageDirectory()表示获取当前sd卡根目录，
                // 由于要对sd卡进行读写操作，需要在配置文件中
                // 声明权限:<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
                File outputImage = new File(imageFile, "output_image"+dateStr+".jpg");
                try{
                    outputImage.createNewFile();

                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(AbnormalRecord.this,"创建文件失败，请重试。。。",Toast.LENGTH_SHORT).show();

                }
                //imageUri标识着这个文件的唯一地址
                imageUri = Uri.fromFile(outputImage);
                //运用隐式intent，调用摄像头
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //拍摄完成后可返回结果，进入onActivityResult方法进行处理
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });

        //点击图片放大
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandImage.setImageBitmap(newBitmap);
                expandImage.setVisibility(View.VISIBLE);
            }
        });
        //点击大图变小
        expandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandImage.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**接收调用摄像头或裁剪程序的结果*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //调用裁剪工具
                    Intent intent = new Intent("com.android.camera.action.CROP");

                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    // 裁剪框的比例，4:5
                    intent.putExtra("aspectX", 4);
                    intent.putExtra("aspectY", 5);
                    // 裁剪后输入图片的尺寸大小
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 250);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent,CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        newBitmap = compressImage(bitmap);
                        iv1.setImageBitmap(newBitmap);
                        Log.d("data","裁剪保存almost成功");

                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                        Log.d("data","裁剪保存失败");
                    }

                }
                break;
            default:
                break;
        }

    }

    /**确认提交按钮*/
    public void clickCommit(View view) {

        record.setTitle(title.getText().toString());
        record.setDetail(detail.getText().toString());
        record.setStatus("待处理");

        Log.d("data","record的所有属性："+record.getTitle()+"**"+record.getLevel());

        if (record.getLevel().equals("请选择隐患等级")){
            Toast.makeText(AbnormalRecord.this,"请在选择隐患等级后提交",Toast.LENGTH_LONG).show();
        }else{
            //将数据写入本地数据库
            dbHelper = new MySqlHelper(this,"XunJian.db",null,1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            //设置日期格式
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //组装数据
            values.put("title",record.getTitle());
            values.put("status","待处理");
            values.put("detail",record.getDetail());
            values.put("level",record.getLevel());
            values.put("recordLat",record.getRecordLat());
            values.put("recordLng",record.getRecordLng());
            values.put("recordDate",df.format(new java.util.Date()));
            //执行插入数据
            long l = db.insert("Record",null,values);
            if (l == -1){
                Toast.makeText(AbnormalRecord.this,"插入数据失败，请重新提交",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(AbnormalRecord.this,"上报异常成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
//        db.execSQL("inset into Record(title,)");

    }




    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while ( baos.toByteArray().length / 1024>100) {
            //重置baos即清空baos
            baos.reset();
            //这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
        }
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }
}
