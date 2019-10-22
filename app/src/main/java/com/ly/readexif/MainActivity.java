package com.ly.readexif;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
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
import com.ly.readexif.utils.AndroidBmpUtil;
import com.ly.readexif.utils.getPhotoFromPhotoAlbum;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private int REQUEST_CODE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (OpenCVLoader.initDebug()) {
            System.loadLibrary("opencv_java3");
        }
        getPermission();
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    @OnClick({R.id.btn_goPhotoAlbum, R.id.iv_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_goPhotoAlbum:
                goPhotoAlbum();
//                Intent intent = new Intent();
//                ComponentName component = new ComponentName(
//                        "ai.moqi.fingerprint.camera_lib",
//                        "ai.moqi.fingerprint.lib.CameraActivity");
//                intent.setComponent(component);
//                startActivityForResult(intent, REQUEST_CODE_CAPTURE);
                break;
            case R.id.iv_photo:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == REQUEST_CODE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            // 8-bit grayscale bytes (for uploading)
//            byte[] imageBytes = data.getByteArrayExtra("image_bytes");
//
//            // convert to bitmap
//            Mat mat = new Mat(640, 640, CvType.CV_8UC1);
//            mat.put(0, 0, imageBytes);
//            Bitmap bitmap = Bitmap.createBitmap(640, 640, Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(mat, bitmap);

//            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            // display bitmap
//            ivPhoto.setImageBitmap(bitmap);
//            saveBmp(bitmap);
//            BmpUtils.getBmpWith8(imageBytes,"/sdcard/test2.bmp",bitmap.getWidth(),bitmap.getHeight());
//        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            //相册
            photoPath = getPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            bitmap = imageScale(bitmap,512,512);
            ivPhoto.setImageBitmap(bitmap);
            Log.e(TAG,"返回路径:" + photoPath);
//            readExif(photoPath);
            String sdcardBmpPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test456.bmp";
            AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
            Bitmap result = BitmapFactory.decodeFile(photoPath);
            boolean isSaveResult = bmpUtil.save(bitmap, sdcardBmpPath);
        }
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 2 && resultCode == RESULT_OK) {
//            //相册
//            photoPath = getPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
//            ivPhoto.setImageBitmap(BitmapFactory.decodeFile(photoPath));
//            Log.e(TAG,"返回路径:" + photoPath);
////            readExif(photoPath);
//
//            bitmap2Gray(photoPath);
//
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    try{
////                        File file = new File(photoPath);
////                        FileInputStream fileInputStream = new FileInputStream(file);
////                        byte[] bytes = new byte[(int)file.length()];
////                        while ((fileInputStream.read(bytes)) != -1) {
////
////                        }
////                        fileInputStream.close();
////                        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
////                        BmpUtils.getBmpWith8(bytes,"/sdcard/bmp.bmp",bitmap.getWidth(),bitmap.getHeight());
////                    }catch (Exception e) {
////
////                    }
////                }
////            }).start();
//
//            saveBmp(BitmapFactory.decodeFile(photoPath));
//
//        }
    }

    /**
     * 调整图片大小
     *
     * @param bitmap
     *            源
     * @param dst_w
     *            输出宽度
     * @param dst_h
     *            输出高度
     * @return
     */
    public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }

    private void bitmap2Gray(String filePath){

//       File file = new File(SAVEDIR, filename + "_1.jpg");
        Bitmap bitmapSrc =null;
        Bitmap grayBitmap = null;
        try {
            bitmapSrc = BitmapFactory.decodeFile(filePath);
//            grayBitmap = Bitmap.createBitmap(bitmapSrc.getWidth(), bitmapSrc.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(grayBitmap);
//            Paint paint = new Paint();
//            ColorMatrix colorMatrix = new ColorMatrix();
//            colorMatrix.setSaturation(0);
//            ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
//            paint.setColorFilter(colorMatrixFilter);
//            canvas.drawBitmap(bitmapSrc, 0, 0, paint);
//            canvas.save();
//            OutputStream outStream = null;
//            outStream = new FileOutputStream(new File(filePath));
//            grayBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

//            BmpUtils.getBmpWith8(bmpFile,"/sdcard/test.bmp",bitmapSrc.getWidth(), bitmapSrc.getHeight());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if (bitmapSrc!=null && !bitmapSrc.isRecycled()){
                bitmapSrc.recycle();
            }

            if (grayBitmap!=null && !grayBitmap.isRecycled()){
                grayBitmap.recycle();
            }

        }


    }

    /**
     * 将Bitmap存为 .bmp格式图片
     * @param bitmap
     */
    private void saveBmp(Bitmap bitmap) {
        if (bitmap == null)
            return;
        // 位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        try {
            byte[] bytes = addBMP8ImageInfosHeaderTable();
            // 存储文件名
            String filename = "/sdcard/test1.bmp";
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileos = new FileOutputStream(filename);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bytes.length + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40 + bytes.length;
            // 保存bmp文件头
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 8;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头
            writeDword(fileos, biSize);
            writeLong(fileos, biWidth);
            writeLong(fileos, biHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            fileos.write(bytes);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol) {
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                }
            }

            fileos.write(bmpData);
            fileos.flush();
            fileos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 8位位图的颜色调板
     * @return
     */
    private static byte[] addBMP8ImageInfosHeaderTable() {
        byte[] buffer = new byte[256 * 4];

        //生成颜色表
        for (int i = 0; i < 256; i++) {
            buffer[0 + 4 * i] = (byte) i;   //Blue
            buffer[1 + 4 * i] = (byte) i;   //Green
            buffer[2 + 4 * i] = (byte) i;   //Red
            buffer[3 + 4 * i] = (byte) 0x00;   //保留值
        }

        return buffer;
    }

    protected void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    protected void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    protected void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
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

//            StringBuilder sb = new StringBuilder();
//            sb.append(longitude)
//                    .append(latitude);

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
                tvExifInfo.setText("该照片没有经纬度信息");
//                ToastUtils.showLong("该照片没有经纬度信息");
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
            tvExifInfo.setText("获取地址信息错误：" + i);
//            ToastUtils.showLong("获取地址信息错误：" + i);
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
