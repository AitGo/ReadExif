package com.ly.readexif.utils;

import android.widget.Toast;

import com.ly.readexif.base.MyApplication;

/**
 * @创建者 ly
 * @创建时间 2019/7/9
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class ToastUtils {

    public static void showLong(String msg){
        Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_LONG).show();
    }
}
