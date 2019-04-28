package com.memo.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * title:测试
 * describe:
 *
 * @author zhou
 * @date 2019-02-12 19:50
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_crash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (++App.OPEN_NUM > 1) {
                    throw new RuntimeException("Boom! sha ka la ka!");
                } else {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            }
        });
    }
}
