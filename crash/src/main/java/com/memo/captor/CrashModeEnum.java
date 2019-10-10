package com.memo.captor;

/**
 * title:
 * describe:
 *
 * @author zhou
 * @date 2019-02-13 11:35
 */
public enum CrashModeEnum {
    /**
     * 崩溃的时候 测试显示测试界面 线上展示线上界面
     */
    MODE_CRASH_SHOW_DEBUG_AND_RELEASE,
    /**
     * 崩溃的时候 测试显示测试界面 线上不展示界面
     */
    MODE_CRASH_SHOW_DEBUG
}
