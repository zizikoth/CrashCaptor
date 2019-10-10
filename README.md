# CrashHunter
写在前面：其实写这个的目的很简单，就是崩溃日志的展示一下，随便百度一下都有很多的CrashHandler，然后里面写了存储到本地，上传服务器等等。不过对于我自己来说，崩溃日志最重要的不是说上传服务器，因为有很多第三方的，比如app集成了极光，友盟，Bugly等等他们都有崩溃日志的收集功能，并且展示效果还都不错，就没有必要再去自己后台再弄一个崩溃收集系统。所以我就只需要一个测试手中发生崩溃的时候及时让他知道发生了什么崩溃，直接拿给我看就可以了，不至于拼命回想之前发生了什么进行重现，所以才有了这个。好吧，其实主要测试是个妹子。。。

## 正文  
### 一.展示的模式
1.Debug模式下崩溃后展示CrashActivity  Release模式直接退出APP
2.Debug模式下崩溃后展示CrashActivity Release模式下展示抱歉提示界面

### 二.崩溃信息的内容
1.文件名 
2.行数
3.时间
4.具体信息
5.更多其他信息

### 三.崩溃日志收集
1.设置Thread.setDefaultUncaughtExceptionHandler(this)，通过重写uncaughtException方法获取到需要的数据
2.将信息传递给CrashActivity展示，关闭应用

### 四.发现的问题
1.问题：在一些手机上，打开多个页面的时候崩溃，使用以下代码并不能退出应用，点击返回还是在上一级页面，并且重新加载数据，这就可能导致上一级页面也出现崩溃，然后周而复始，不停的崩
```
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
```
解决方案：通过Application注册Activity生命周期回调然后finishAllActivity()，达到退出APP的目的

### 五.界面展示
Debug模式下展示的崩溃界面 和 Release模式下展示的崩溃界面

![Debug模式下展示的崩溃界面.jpg](https://github.com/zmemo/CrashHunter/blob/master/screenshot/CrashActivity.jpg)    ![Release模式下展示的崩溃界面.jpg](https://github.com/zmemo/CrashHunter/blob/master/screenshot/ReleaseCrashActivity.jpg)

### 六.使用
Step 1
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2.注意版本号
[![](https://jitpack.io/v/zmemo/CrashCaptor.svg)](https://jitpack.io/#zmemo/CrashCaptor)
```
	dependencies {
	        implementation 'com.github.zmemo:CrashCaptor:x.y.z'
	}
````
Step 3. 在Application中初始化
```
        CrashCaptor.init(this)
                .setCrashMode(CrashModeEnum.MODE_CRASH_SHOW_DEBUG_AND_RELEASE)
                .setReleaseCrashActivity(CrashReleaseActivity.class)
                .isDebug(BuildConfig.DEBUG);
```
