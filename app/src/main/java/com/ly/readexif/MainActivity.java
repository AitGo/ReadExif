package com.ly.readexif;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.ly.readexif.utils.ToastUtils;
import com.ly.readexif.utils.getPhotoFromPhotoAlbum;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,GeocodeSearch.OnGeocodeSearchListener {

    @BindView(R.id.btn_goPhotoAlbum)
    Button btnGoPhotoAlbum;
    @BindView(R.id.tv_exif_info)
    TextView tvExifInfo;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    /**
     * 需要进行检测的权限数组
     */
    private String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET
    };
    private static final String TAG = "exif";
    private String photoPath;
    private GeocodeSearch geocoderSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getPermission();
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    @OnClick({R.id.btn_goPhotoAlbum, R.id.iv_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_goPhotoAlbum:
                goPhotoAlbum();
                break;
            case R.id.iv_photo:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            //相册
            photoPath = getPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
            ivPhoto.setImageBitmap(BitmapFactory.decodeFile(photoPath));
            Log.e(TAG,"返回路径:" + photoPath);

            readExif(photoPath);
        }
    }

    private void readExif(String photoPath) {
        //android读取图片EXIF信息
        try {
            ExifInterface exifInterface=new ExifInterface(photoPath);

//         执行保存
            exifInterface.saveAttributes();
            //获取图片的方向
            String orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            //获取图片的时间
            String dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            String make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
            String model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
            String flash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
            String height = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String width = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

            StringBuilder sb = new StringBuilder();
            sb.append(longitude)
                    .append(latitude);

            Log.e("TAG", "## orientation=" + orientation);
            Log.e("TAG", "## dateTime=" + dateTime);
            Log.e("TAG", "## make=" + make);
            Log.e("TAG", "## model=" + model);
            Log.e("TAG", "## flash=" + flash);
            Log.e("TAG", "## imageLength=" + height);
            Log.e("TAG", "## imageWidth=" + width);
            Log.e("TAG", "## latitude=" + latitude);
            Log.e("TAG", "## longitude=" + longitude);

            if(latitude != null && longitude != null) {
                Float lat = String2GPSFloat(latitude);
                Float lon = String2GPSFloat(longitude);
    //            LatLng latLng = new LatLng(lat,lon);
    //            LatLng amapLatLng = GPS2Amap(latLng);
                LatLonPoint latLonPoint = new LatLonPoint(lat,lon);
                // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.GPS);
                geocoderSearch.getFromLocationAsyn(query);
            }else {
                ToastUtils.showLong("该照片没有经纬度信息");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private LatLng GPS2Amap(LatLng latLng) {
        CoordinateConverter converter  = new CoordinateConverter(this);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 LatLng类型
        converter.coord(latLng);
        // 执行转换操作
        return converter.convert();
    }

    /**
     * 将exif中的gps数据转换成十进制
     * @param s
     * @return
     */
    private Float String2GPSFloat(String s) {
        float temp = 0;
        if(s != null) {
            String[] split = s.split(",");
            for(int i = split.length -1; i >=0 ; i --) {
                String[] split1 = split[i].split("/");
                float v = 0;
                if(i != 0) {
                    v = (Float.valueOf(split1[0]) + temp) / 6;
                    temp = v/(Float.valueOf(split1[1]) * 10);
                }else {
                    v = Float.valueOf(split1[0]) + temp;
                    temp = v/Float.valueOf(split1[1]);
                }
            }
            Log.e("TAG", "## gps lon=" + temp);
        }
        return temp;
    }


    //激活相册操作
    private void goPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int i) {
        Log.e(TAG,i + "..." + result.getRegeocodeAddress().getCity());
        if(i == 1000) {
            tvExifInfo.setText(result.getRegeocodeAddress().getFormatAddress());
        }else {
            ToastUtils.showLong("获取地址信息错误：" + i);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * 检查权限
     *
     * @param
     * @since 2.5.0
     */
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, needPermissions)) {
            //已经打开权限
//            Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的存储、定位、相机权限", 1, needPermissions);
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}
