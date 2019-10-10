package com.memo.sample;

import android.app.Application;

import com.memo.captor.CrashCaptor;
import com.memo.captor.CrashModeEnum;

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
        CrashCaptor.init(this)
                .setCrashMode(CrashModeEnum.MODE_CRASH_SHOW_DEBUG_AND_RELEASE)
                .setReleaseCrashActivity(CrashReleaseActivity.class)
                .isDebug(BuildConfig.DEBUG);
    }

}
