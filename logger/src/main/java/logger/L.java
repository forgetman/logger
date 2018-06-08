package logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


@SuppressWarnings("unused")
public final class L {
    private static final String TAG_WWW = "www";

    private static final String TOP_LEFT_CORNER = "";
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char HORIZONTAL_LINE = '|';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String TOP_BORDER = DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;

    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;

    // JSON的缩进量
    private static final int JSON_INDENT = 4;

    private static boolean mIsDebug = true;

    //Android系统的单条日志打印长度是固定的4*1024个字符长度。
    private static final int CHUNK_SIZE = 4000;

    private L() {
    }

    public static void setDebug(boolean debug) {
        mIsDebug = debug;
    }

    private static void log(int logType, @Nullable String onceOnlyTag, @Nullable String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        String tag = formatTag(onceOnlyTag);

        logTopBorder(tag);

        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            logContent(logType, tag, message);
            logBottomBorder(logType, tag);
            return;
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            logContent(logType, tag, new String(bytes, i, count));
        }
        logBottomBorder(logType, tag);
    }


    @Nullable
    private static String formatTag(@Nullable String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return tag;
        }
        return getTag(getTraceElement());
    }

    private static void logTopBorder(@Nullable String tag) {
        debug(tag, TOP_BORDER);
    }

    public static void type(int logType, String tag, String line) {
        if (!mIsDebug) {
            return;
        }
        switch (logType) {
            case VERBOSE:
                Log.v(tag, line);
                break;
            case DEBUG:
                Log.d(tag, line);
                break;
            case INFO:
                Log.i(tag, line);
                break;
            case WARN:
                Log.w(tag, line);
                break;
            case ERROR:
                Log.e(tag, line);
                break;
            default:
                Log.d(tag, TOP_BORDER);
                break;
        }

    }

    private static void logBottomBorder(int logType, @Nullable String tag) {
        type(logType, tag, BOTTOM_BORDER);
    }

    private static void logContent(int logType, @Nullable String tag, @NonNull String chunk) {
        if (TextUtils.isEmpty(chunk)) {
            return;
        }
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            type(logType, tag, HORIZONTAL_LINE + " " + line);
        }
    }


    private static void debug(String msg) {
        StackTraceElement element = getTraceElement();
        d(getTag(element), msg, element);
    }

    private static void debug(String tag, String msg) {
        d(tag, msg, getTraceElement());
    }

    private static void d(String tag, String msg, StackTraceElement element) {
        if (mIsDebug) {
            Log.d(tag, content(msg, element));
        }
    }

    public static void e(Throwable tr) {
        StackTraceElement element = getTraceElement();
        e(getTag(element), null, tr, element);
    }

    public static void e(String tag, Throwable tr) {
        e(tag, null, tr);
    }

    public static void e(String tag, @Nullable String msg, Throwable tr) {
        e(tag, msg, tr, getTraceElement());
    }

    private static void e(String tag, @Nullable String msg, Throwable tr, StackTraceElement element) {
        if (mIsDebug) {
            Log.e(tag, content(msg + Log.getStackTraceString(tr), element));
        }
    }

    public static void www(String msg) {
        if (mIsDebug) {
            Log.d(TAG_WWW, content(msg, getTraceElement()));
        }
    }

    private static void json(int logType, String tag, String msg) {
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

        log(logType, tag, json);
    }

    @Nullable
    private static StackTraceElement getTraceElement() {
        // find the target invoked method
        StackTraceElement element = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(L.class.getName());
            if (shouldTrace && !isLogMethod) {
                element = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return element;
    }

    private static String content(String msg, @Nullable StackTraceElement element) {
        if (element == null) {
            return make(msg);
        } else {
            return make(msg,
                    "(",
                    element.getFileName(),
                    ":",
                    String.valueOf(element.getLineNumber()),
                    ")"
            );
        }
    }

    private static String getTag(@Nullable StackTraceElement element) {
        String result = null;
        if (element != null) {
            result = element.getClassName();
            int lastIndex = result.lastIndexOf(".");
            result = result.substring(lastIndex + 1, result.length());
        } else {
            result = "temp_tag";
        }

        return result;
    }

    private static String make(String... param) {
        StringBuilder buffer = new StringBuilder();
        for (String s : param) {
            buffer.append(s);
        }
        return buffer.toString();
    }


    private static boolean isJSONValid(String jsonInString) {
        try {
            Object json = new JSONTokener(jsonInString).nextValue();
            if (json instanceof JSONObject || json instanceof JSONArray) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    private static void doLog(int logType, @Nullable String tag, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (isJSONValid(message)) {
            L.json(logType, tag, message);
        } else {
            L.log(logType, tag, message);
        }
    }

    public static void v(String msg) {
        v(null, msg);
    }

    public static void d(String msg) {
        d(null, msg);
    }

    public static void i(String msg) {
        i(null, msg);
    }

    public static void w(String msg) {
        w(null, msg);
    }

    public static void e(String msg) {
        e(null, msg);
    }

    public static void v(@Nullable String tag, String msg) {
        doLog(VERBOSE, tag, msg);
    }

    public static void d(@Nullable String tag, String msg) {
        doLog(DEBUG, tag, msg);
    }

    public static void i(@Nullable String tag, String msg) {
        doLog(INFO, tag, msg);
    }

    public static void w(@Nullable String tag, String msg) {
        doLog(WARN, tag, msg);
    }

    public static void e(@Nullable String tag, String msg) {
        doLog(ERROR, tag, msg);
    }

}
