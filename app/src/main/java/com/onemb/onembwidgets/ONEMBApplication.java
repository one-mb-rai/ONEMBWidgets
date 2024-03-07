package com.onemb.onembwidgets;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.onemb.onembwidgets.repository.WifiAdbRepository;

import com.onemb.onembwidgets.repository.ScreenTimeoutSettingsRepository;

public class ONEMBApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate() {
        super.onCreate();

        ONEMBApplication.context = getApplicationContext();
        ScreenTimeoutSettingsRepository.INSTANCE.init(ONEMBApplication.context);
        WifiAdbRepository.INSTANCE.init(ONEMBApplication.context);
    }

    public static Context getAppContext() {
        return ONEMBApplication.context;
    }

}
