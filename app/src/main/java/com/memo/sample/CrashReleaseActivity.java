package com.memo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


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
        findViewById(R.id.tv_crash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
