package com.memo.captor;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Stack;

/**
 * title:崩溃捕捉者
 * describe:
 *
 * @author zhou
 * @date 2019-02-12 20:07
 */
public class CrashCaptor implements Thread.UncaughtExceptionHandler {

    /**
     * 当前实例
     */
    private static CrashCaptor INSTANCE;

    /**
     * Application
     */
    private static Application mApplication;

    /**
     * 存放Activity的栈
     */
    private static Stack<Activity> mActivityStack;

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
    private Class<? extends Activity> releaseCrashActivity;
    /**
     * 是否是测试模式
     */
    private boolean isDebug;

    /**
     * 私有化实例
     */
    private CrashCaptor(Application application) {
        mApplication = application;
        mActivityStack = new Stack<>();
        Thread.setDefaultUncaughtExceptionHandler(this);
        //通过注册生命周期让所有的页面退出 除了自定义的CrashActivity 和线上展示的崩溃界面
        mApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (activity.getClass() != CrashActivity.class ||
                        activity.getClass() != releaseCrashActivity) {
                    mActivityStack.add(activity);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) { }

            @Override
            public void onActivityResumed(Activity activity) { }

            @Override
            public void onActivityPaused(Activity activity) { }

            @Override
            public void onActivityStopped(Activity activity) { }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mActivityStack.remove(activity);
            }
        });
    }

    /**
     * 初始化
     *
     * @param application Application
     * @return 当前实例 INSTANCE
     */
    public static CrashCaptor init(Application application) {
        if (INSTANCE == null) {
            synchronized (CrashCaptor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CrashCaptor(application);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取传递的细腻
     *
     * @param intent Intent
     * @return 崩溃信息 CrashInfo
     */
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
    public CrashCaptor isDebug(boolean isDebug) {
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
    public CrashCaptor setCrashMode(CrashModeEnum crashModeEnum) {
        this.crashModeEnum = crashModeEnum;
        return this;
    }

    /**
     * 设置线上展示的Activity
     *
     * @param releaseCrashActivity 线上展示的Activity
     *                             可以配合CrashCaptor.getCrashInfo(getIntent())来返回CrashInfo
     *                             注意判空
     * @return 当前实例 INSTANCE
     */
    public CrashCaptor setReleaseCrashActivity(Class<? extends Activity> releaseCrashActivity) {
        this.releaseCrashActivity = releaseCrashActivity;
        return this;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        CrashInfo info = parseCrash(throwable);
        Class<? extends Activity> clazz = CrashActivity.class;
        switch (crashModeEnum) {
            case MODE_CRASH_SHOW_DEBUG_AND_RELEASE:
                if (!isDebug && releaseCrashActivity != null) {
                    clazz = releaseCrashActivity;
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
        finishAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
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

            if (exception.getStackTrace().length > 0) {
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
     * 关闭所有Activity
     */
    private void finishAllActivity() {
        for (Activity activity : mActivityStack) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        mActivityStack.clear();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    /**
     * 跳转页面
     *
     * @param clazz 跳转的页面
     * @param info  崩溃信息
     */
    private void navigation(Class<? extends Activity> clazz, CrashInfo info) {
        Intent intent = new Intent(mApplication.getApplicationContext(), clazz);
        intent.putExtra(CrashCaptor.CRASH, info);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApplication.getApplicationContext().startActivity(intent);
    }

}