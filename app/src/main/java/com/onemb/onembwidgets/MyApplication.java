package com.onemb.onembwidgets;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.onemb.onembwidgets.repository.ScreenTimeoutSettingsRepository;

public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        ScreenTimeoutSettingsRepository.INSTANCE.init(MyApplication.context);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
