package logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


@SuppressWarnings("ALL")
public final class L {
    private static final String TAG_WWW = "www";
    private static final String L_CLASS_NAME = L.class.getName();
    private static final String TOP_LEFT_CORNER = "┌";
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char HORIZONTAL_LINE = '|';
    private static final String DOUBLE_DIVIDER = "──────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;

    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;

    // JSON的缩进量
    private static final int JSON_INDENT = 4;

    private static boolean mIsDebug = true;

    //Android系统的单条日志打印长度是固定的4*1024个字符长度,截取4000个字符长度作为分段打印的最大长度。
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

        //日志出处信息
        stackTraceElementInfo(logType, tag);

        logTopBorder(logType, tag);

        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            logContent(logType, tag, message);
        } else {
            for (int i = 0; i < length; i += CHUNK_SIZE) {
                int count = Math.min(length - i, CHUNK_SIZE);
                logContent(logType, tag, new String(bytes, i, count));
            }
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

    private static void logTopBorder(int logType, @Nullable String tag) {
        type(logType, tag, TOP_BORDER);
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
                Log.d(tag, line);
                break;
        }

    }

    private static void stackTraceElementInfo(int logType, String tag) {
        if (!mIsDebug) {
            return;
        }
        switch (logType) {
            case VERBOSE:
                Log.v(tag, content("", getTraceElement()));
                break;
            case DEBUG:
                debug(tag, "");
                break;
            case INFO:
                Log.i(tag, content("", getTraceElement()));
                break;
            case WARN:
                Log.w(tag, content("", getTraceElement()));
                break;
            case ERROR:
                Log.e(tag, content("", getTraceElement()));
                break;
            default:
                debug(tag, "");
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
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(L_CLASS_NAME);
            if (shouldTrace && !isLogMethod) {
                return stackTraceElement;
            }
            shouldTrace = isLogMethod;
        }
        return null;
    }

    private static String content(String msg, @Nullable StackTraceElement element) {
        if (element == null) {
            return make(msg);
        } else {
            return make(
                    "(",
                    element.getFileName(),
                    ":",
                    String.valueOf(element.getLineNumber()),
                    ")", " ", msg
            );
        }
    }

    private static String getTag(@Nullable StackTraceElement element) {
        String result;
        if (element != null) {
//            result = element.getClassName();
//            int lastIndex = result.lastIndexOf(".");
//            result = result.substring(lastIndex + 1, result.length());

            result = element.getFileName();
            int lastIndex = result.lastIndexOf(".");
            result = result.substring(0, lastIndex);
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
            L.log(logType, tag, "The log content is null or empty！");
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

    public static Builder merge() {
        return new Builder(getTag(getTraceElement()));
    }

    public static class Builder {
        private StringBuilder mStringBuilder;
        private int type;
        private String tag;

        private Builder(String tag) {
            this.tag = tag;
            mStringBuilder = new StringBuilder();
        }

        public Builder d() {
            this.type = DEBUG;
            return this;
        }

        public Builder d(String tag) {
            this.type = DEBUG;
            this.tag = tag;
            return this;
        }

        public Builder v() {
            this.type = VERBOSE;
            return this;
        }

        public Builder v(String tag) {
            this.type = DEBUG;
            this.tag = tag;
            return this;
        }

        public Builder i() {
            this.type = INFO;
            return this;
        }

        public Builder i(String tag) {
            this.type = INFO;
            this.tag = tag;
            return this;
        }

        public Builder e() {
            this.type = ERROR;
            return this;
        }

        public Builder e(String tag) {
            this.type = ERROR;
            this.tag = tag;
            return this;
        }

        public Builder w() {
            this.type = WARN;
            return this;
        }

        public Builder w(String tag) {
            this.type = WARN;
            this.tag = tag;
            return this;
        }

        public Builder append(String msg) {
            mStringBuilder.append(msg).append("\n");
            return this;
        }

        public void end() {
            doLog(type, tag, mStringBuilder.toString());
        }
    }

    /**
     * quick tag with "www"
     *
     * @param msg
     */
    public static void www(String msg) {
        if (mIsDebug) {
            Log.d(TAG_WWW, content(msg, getTraceElement()));
        }
    }

    /**
     * thread msg
     */
    public static void trace() {
        StackTraceElement traceElement = getTraceElement();
        StringBuffer stringBuffer = new StringBuffer();
        Thread thread = Thread.currentThread();
        String threadMsg = stringBuffer.append("Process_id:")
                .append(android.os.Process.myPid())
                .append("\n")
                .append("Thread_name:")
                .append(thread.getName())
                .append("\n")
                .append("Thread_id:")
                .append(thread.getId())
                .append("\n")
//                .append(fromClass(traceElement))
                .toString();

        d(threadMsg);
    }
}
