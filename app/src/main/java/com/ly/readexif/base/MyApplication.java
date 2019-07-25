package com.ly.readexif.base;

import android.app.Application;
import android.content.Context;



/**
 * @创建者 liuyang
 * @创建时间 2018/6/25 19:26
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取context
        mContext = getApplicationContext();
    }

    //创建一个静态的方法，以便获取context对象
    public static Context getContext(){
        return mContext;
    }

}