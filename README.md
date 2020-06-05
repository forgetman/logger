# logger [![](https://jitpack.io/v/forgetman/logger.svg)](https://jitpack.io/#forgetman/logger)
it is a log printer that print the log in style

Update
------
+ v1.2.0:
```text
升级到androidx
```
+ v1.1.2:
```text
修复json打印的错误
```

+ v1.0.7:
```text
加入方法 merge(): 合并多条log
```
+ v1.0.6:
```text
加入更改debug状态的设置
```
+ v1.0.5: 
```text
1. 加入json完整打印(不会截断)
2. 加入每条log头尾的间隔符以作醒目区分
```

Download
--------
+ Step 1. Add it in your root build.gradle at the end of repositories
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
+ Step 2. Add the dependency
```groovy
dependencies {
  implementation 'com.github.forgetman:logger:1.0.7'
}
```
How to use
----------
```java
public class Test{
    
    private void fun(){
        L.d("logMessage");
        
        //You can also add tag
        L.d("TAG","logMessage with TAG");
        
        //More output
        L.merge().d().append("logMessage1").append("logMessage2").append("logMessage3").end();
        
        //Other information,such as threads, processes, etc
        L.trace(); 
    }
} 
```
Here are some test results
```text
06-26 13:56:07.460 2121-2121/? D/MainActivity: (MainActivity.java:18)
    ┌────────────────────────────────────────────────────────────
    | logMessage
    └────────────────────────────────────────────────────────────

06-26 13:56:07.460 2121-2121/? D/TAG: (MainActivity.java:18)
    ┌────────────────────────────────────────────────────────────
    | logMessage with TAG
    └────────────────────────────────────────────────────────────

06-25 17:02:14.348 1382-1382/? D/MainActivity: (L.java:373)
    ┌────────────────────────────────────────────────────────────
    | logMessage1
    | logMessage2
    | logMessage3
    └────────────────────────────────────────────────────────────
06-25 17:02:14.478 1382-1382/? D/libEGL: loaded /system/lib/egl/libEGL_adreno.so
    ┌────────────────────────────────────────────────────────────
    | Process_id:1796
    | Thread_name:main
    | Thread_id:1
    └────────────────────────────────────────────────────────────
```