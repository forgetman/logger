package logger;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class L {

    private static final String TAG_WWW = "www";
//    private static final String SEPARATE = "=========";
//    private static final String EACH_BORDER = "‖";
//    private static final String END_BORDER = "╚═══════════════════════════";

    // JSON的缩进量
    private static final int JSON_INDENT = 4;

    private static boolean mIsDebug = true;

    private L() {
    }

    public static boolean isDebug() {
        return mIsDebug;
    }

    public static void setDebugState(boolean isDebug) {
        mIsDebug = isDebug;
    }

    public static void d(String msg) {
        StackTraceElement element = stackTraceElement();
        d(getTag(element), msg, element);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, stackTraceElement());
    }

    private static void d(String tag, String msg, StackTraceElement element) {
        if (mIsDebug) {
            Log.d(tag, content(msg, element));
        }
    }

    public static void e(Throwable tr) {
        StackTraceElement element = stackTraceElement();
        e(getTag(element), null, tr, element);
    }

    public static void e(String tag, Throwable tr) {
        e(tag, null, tr);
    }

    public static void e(String tag, @Nullable String msg, Throwable tr) {
        e(tag, msg, tr, stackTraceElement());
    }

    private static void e(String tag, @Nullable String msg, Throwable tr, StackTraceElement element) {
        if (mIsDebug) {
//            make(SEPARATE, msg == null ? "" : msg, SEPARATE, Log.getStackTraceString(tr))
            Log.e(tag, content(msg + Log.getStackTraceString(tr), element));
        }
    }

    public static void www(String msg) {
        if (mIsDebug) {
            Log.d(TAG_WWW, content(msg, stackTraceElement()));
        }
    }

    public static void json(String msg) {
        json(getTag(stackTraceElement()), msg);
    }

    public static void json(String tag, String msg) {
        if (!mIsDebug) {
            return;
        }

        String json;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                json = jsonObject.toString(JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                json = jsonArray.toString(JSON_INDENT);
            } else {
                json = msg;
            }
        } catch (JSONException e) {
            json = msg;
        }

        Log.d(tag, json);
    }

    @Nullable
    private static StackTraceElement stackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(L.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }

    private static String content(String msg, StackTraceElement element) {
        return make(msg,
                "(",
                element.getFileName(),
                ":",
                String.valueOf(element.getLineNumber()),
                ")"
        );
    }

    private static String getTag(StackTraceElement element) {
        String result = element.getClassName();
        int lastIndex = result.lastIndexOf(".");
        result = result.substring(lastIndex + 1, result.length());
        return result;
    }

    private static String make(String... param) {
        StringBuffer buffer = new StringBuffer();
        for (String s : param) {
            buffer.append(s);
        }
        return buffer.toString();
    }
}
