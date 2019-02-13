package com.memo.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * title:崩溃捕捉者
 * describe:
 *
 * @author zhou
 * @date 2019-02-12 20:07
 */
public class CrashHunter implements Thread.UncaughtExceptionHandler {

    /**
     * 当前实例
     */
    @SuppressLint("StaticFieldLeak") private static CrashHunter INSTANCE = new CrashHunter();

    /**
     * ApplicationContext
     */
    @SuppressLint("StaticFieldLeak") private static Context mContext;
    /**
     * 传递的Intent的Key
     */
    private static String CRASH = "Crash";
    /**
     * 崩溃展示类型
     *
     * @see CrashModeEnum#MODE_CRASH_SHOW_DEBUG_AND_RELEASE
     * 测试展示CrashActivity 线上展示自己写的Activity
     * <p>
     * @see CrashModeEnum#MODE_CRASH_SHOW_DEBUG
     * 测试展示CrashActivity 线上不展示
     */
    private CrashModeEnum crashModeEnum = CrashModeEnum.MODE_CRASH_SHOW_DEBUG;
    /**
     * 自己写的线上展示的页面
     */
    private Class<? extends Activity> releaseActivity;
    /**
     * 是否是测试模式
     */
    private boolean isDebug;

    /**
     * 私有化实例
     */
    private CrashHunter() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 初始化
     *
     * @param application Application
     * @return 当前实例 INSTANCE
     */
    public static CrashHunter init(Application application) {
        mContext = application.getApplicationContext();
        return INSTANCE;
    }

    /**
     * 获取传递的细腻
     *
     * @param intent Intent
     * @return 崩溃信息 CrashInfo
     */
    @Nullable
    public static CrashInfo getCrashInfo(Intent intent) {
        try {
            return (CrashInfo) intent.getParcelableExtra(CRASH);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置是否为测试模式
     *
     * @param isDebug 是否为测试模式
     * @return 当前实例 INSTANCE
     */
    public CrashHunter isDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    /**
     * 设置崩溃展示模式
     *
     * @param crashModeEnum 崩溃展示模式
     * @return 当前实例 INSTANCE
     * <p>
     * @see CrashModeEnum#MODE_CRASH_SHOW_DEBUG_AND_RELEASE
     * 测试展示CrashActivity 线上展示自己写的Activity
     * <p>
     * @see CrashModeEnum#MODE_CRASH_SHOW_DEBUG
     * 测试展示CrashActivity 线上不展示
     */
    public CrashHunter setCrashMode(CrashModeEnum crashModeEnum) {
        this.crashModeEnum = crashModeEnum;
        return this;
    }

    /**
     * 设置线上展示的Activity
     *
     * @param releaseActivity 线上展示的Activity
     *                        可以配合CrashCaptor.getCrashInfo(getIntent())来返回CrashInfo
     *                        注意判空
     * @return 当前实例 INSTANCE
     */
    public CrashHunter setReleaseActivity(Class<? extends Activity> releaseActivity) {
        this.releaseActivity = releaseActivity;
        return this;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        CrashInfo info = parseCrash(throwable);
        Class<? extends Activity> clazz = CrashActivity.class;
        switch (crashModeEnum) {
            case MODE_CRASH_SHOW_DEBUG_AND_RELEASE:
                if (!isDebug && releaseActivity != null) {
                    clazz = releaseActivity;
                }
                navigation(clazz, info);
                break;
            case MODE_CRASH_SHOW_DEBUG:
            default:
                if (isDebug) {
                    navigation(clazz, info);
                }
                break;
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 从异常中解析所有需要的信息
     *
     * @param exception 错误异常
     * @return 崩溃信息 CrashInfo
     */
    private CrashInfo parseCrash(Throwable exception) {
        CrashInfo crashInfo = new CrashInfo();
        try {
            if (exception.getCause() != null) {
                exception = exception.getCause();
            }
            crashInfo.setExceptionMsg(exception.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            pw.flush();
            String exceptionType = exception.getClass().getName();

            if (exception.getStackTrace() != null && exception.getStackTrace().length > 0) {
                StackTraceElement element = exception.getStackTrace()[0];
                crashInfo.setLineNumber(element.getLineNumber());
                crashInfo.setFileName(element.getFileName());
                crashInfo.setClassName(element.getClassName());
                crashInfo.setMethodName(element.getMethodName());
                crashInfo.setExceptionType(exceptionType);
            }

            crashInfo.setFullException(sw.toString());
        } catch (Exception e) {
            return crashInfo;
        }
        return crashInfo;
    }

    /**
     * 跳转页面
     *
     * @param clazz 跳转的页面
     * @param info  崩溃信息
     */
    private void navigation(Class<? extends Activity> clazz, CrashInfo info) {
        Intent intent = new Intent(mContext, clazz);
        intent.putExtra(CrashHunter.CRASH, info);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

}