package com.memo.captor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


/**
 * title:崩溃展示界面
 * describe:
 *
 * @author zhou
 * @date 2019-02-12 19:53
 */
public class CrashActivity extends Activity {


    private CrashInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        info = CrashCaptor.getCrashInfo(getIntent());
        if (info == null) {
            return;
        }
        TextView mTvMessage = findViewById(R.id.textMessage);
        TextView mTvFileName = findViewById(R.id.tv_fileName);
        TextView mTvClassName = findViewById(R.id.tv_className);
        TextView mTvMethodName = findViewById(R.id.tv_methodName);
        TextView mTvLineNumber = findViewById(R.id.tv_lineNumber);
        TextView mTvExceptionType = findViewById(R.id.tv_exceptionType);
        TextView mTvFullException = findViewById(R.id.tv_fullException);
        TextView mTvTime = findViewById(R.id.tv_time);
        TextView mTvModel = findViewById(R.id.tv_model);
        TextView mTvBrand = findViewById(R.id.tv_brand);
        TextView mTvVersion = findViewById(R.id.tv_version);

        mTvMessage.setText(info.getExceptionMsg());
        mTvFileName.setText(info.getFileName());
        mTvClassName.setText(info.getClassName());
        mTvMethodName.setText(info.getMethodName());
        mTvLineNumber.setText(String.valueOf(info.getLineNumber()));
        mTvExceptionType.setText(info.getExceptionType());
        mTvFullException.setText(info.getFullException());
        mTvTime.setText(info.getTime());

        mTvModel.setText(info.getModel());
        mTvBrand.setText(info.getBrand());
        mTvVersion.setText(info.getVersion());

        findViewById(R.id.iv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        if (BuildConfig.DEBUG) {
            Log.e("CrashHunter", info.toString());
        }
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "崩溃信息：");
        intent.putExtra(Intent.EXTRA_TEXT, info.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享到"));
    }
}