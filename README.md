# logger [![](https://jitpack.io/v/forgetman/logger.svg)](https://jitpack.io/#forgetman/logger)
it is a log printer that print the log in style

Update
------
+ v1.0.7:
```text
add method 'merge()': merge several logs  
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
```groovy
dependencies {
  implementation ''
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