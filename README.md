# logger [![](https://jitpack.io/v/forgetman/logger.svg)](https://jitpack.io/#forgetman/logger)
it is a log printer that print the log in style

Update
------
+ v2.0.0:
```text
1. use kotlin
2. remove merge(), use compose() instead
```

+ v1.2.0:
```text
update to androidx
```
+ v1.1.2:
```text
fix bug of json
```

+ v1.0.7:
```text
add merge(): compose multiply log
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
  implementation 'com.github.forgetman:logger:last_version'
}
```
How to use
----------
```kotlin
class Test {
    
    fun test() {
        L.d("logMessage");
        
        //You can also add tag
        L.d("TAG","logMessage with TAG");
        
        //More output
        L.compose {
            append("LogMessage1")
            append("LogMessage2")
            append("LogMessage3")
        }.d()
        
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