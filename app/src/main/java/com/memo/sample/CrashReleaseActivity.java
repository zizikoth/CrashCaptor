package com.memo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.memo.crashhunter.CrashHunter;
import com.memo.crashhunter.CrashInfo;


/**
 * title:
 * describe:
 *
 * @author zhou
 * @date 2019-02-13 11:59
 */
public class CrashReleaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_release);
        CrashInfo crashInfo = CrashHunter.getCrashInfo(getIntent());
        if (crashInfo != null) {
            ((TextView) findViewById(R.id.tv_info)).setText(crashInfo.toString());
        }
    }
}
