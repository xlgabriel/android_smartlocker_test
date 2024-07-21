package com.aituolink.smartbox;

import android.app.Application;
import android.content.Context;

public class AppContext extends Application {

    //公用实例
    private static AppContext instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = (AppContext)getApplicationContext();
    }
    public static Context getContext()
    {
        return instance;
    }
}
