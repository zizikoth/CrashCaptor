package com.memo.sample;

import android.app.Application;

import com.memo.crashhunter.CrashHunter;
import com.memo.crashhunter.CrashModeEnum;

/**
 * title:Application
 * describe:
 *
 * @author zhou
 * @date 2019-02-12 20:02
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHunter.init(this)
                .setCrashMode(CrashModeEnum.MODE_CRASH_SHOW_DEBUG_AND_RELEASE)
                .setReleaseActivity(CrashReleaseActivity.class)
                .isDebug(BuildConfig.DEBUG);
    }

}
