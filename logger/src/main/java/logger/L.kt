package logger

import android.os.Process
import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import kotlin.math.min

@Suppress("unused")
object L {
    private const val TAG_WWW = "www"
    private val L_CLASS_NAME = L::class.java.name
    private const val TOP_LEFT_CORNER = "┌"
    private const val BOTTOM_LEFT_CORNER = '└'
    private const val HORIZONTAL_LINE = '|'
    private const val DOUBLE_DIVIDER = "──────────────────────────────"
    private const val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private const val BOTTOM_BORDER = BOTTOM_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5

    // JSON的缩进量
    private const val JSON_INDENT = 4

    //Android系统的单条日志打印长度是固定的4*1024个字符长度,截取4000个字符长度作为分段打印的最大长度。
    private const val CHUNK_SIZE = 4000

    var debuggable = true

    private fun log(logType: Int, onceOnlyTag: String?, message: String?) {
        if (message == null || message.isEmpty()) return

        val tag = formatTag(onceOnlyTag)

        //日志出处信息
        stackTraceElementInfo(logType, tag)
        logTopBorder(logType, tag)
        val bytes = message.toByteArray()
        val length = bytes.size
        if (length <= CHUNK_SIZE) {
            logContent(logType, tag, message)
        } else {
            var i = 0
            while (i < length) {
                val count = min(length - i, CHUNK_SIZE)
                logContent(logType, tag, String(bytes, i, count))
                i += CHUNK_SIZE
            }
        }
        logBottomBorder(logType, tag)
    }

    private fun formatTag(tag: String?): String? {
        return tag ?: getTag(traceElement)
    }

    private fun logTopBorder(logType: Int, tag: String?) {
        type(logType, tag, TOP_BORDER)
    }

    private fun type(logType: Int, tag: String?, line: String?) {
        if (!debuggable) {
            return
        }
        when (logType) {
            VERBOSE -> Log.v(tag, line!!)
            DEBUG -> Log.d(tag, line!!)
            INFO -> Log.i(tag, line!!)
            WARN -> Log.w(tag, line!!)
            ERROR -> Log.e(tag, line!!)
            else -> Log.d(tag, line!!)
        }
    }

    private fun stackTraceElementInfo(logType: Int, tag: String?) {
        if (!debuggable) {
            return
        }
        when (logType) {
            VERBOSE -> Log.v(tag, content("", traceElement))
            DEBUG -> debug(tag, "")
            INFO -> Log.i(tag, content("", traceElement))
            WARN -> Log.w(tag, content("", traceElement))
            ERROR -> Log.e(tag, content("", traceElement))
            else -> debug(tag, "")
        }
    }

    private fun logBottomBorder(logType: Int, tag: String?) {
        type(logType, tag, BOTTOM_BORDER)
    }

    private fun logContent(logType: Int, tag: String?, chunk: String) {
        if (chunk.isEmpty()) return

        val regex = System.getProperty("line.separator")?.toRegex() ?: return
        val lines = chunk.split(regex).toTypedArray()
        for (line in lines) {
            type(logType, tag, "$HORIZONTAL_LINE $line")
        }
    }

    private fun debug(msg: String) {
        val element = traceElement
        d(getTag(element), msg, element)
    }

    private fun debug(tag: String?, msg: String) {
        d(tag, msg, traceElement)
    }

    private fun d(tag: String?, msg: String, element: StackTraceElement?) {
        if (debuggable) {
            Log.d(tag, content(msg, element))
        }
    }

    fun e(tr: Throwable) {
        val element = traceElement
        e(getTag(element), null, tr, element)
    }

    fun e(tag: String, tr: Throwable) {
        e(tag, null, tr)
    }

    fun e(tag: String, msg: String?, tr: Throwable) {
        e(tag, msg, tr, traceElement)
    }

    private fun e(tag: String, msg: String?, tr: Throwable, element: StackTraceElement?) {
        if (debuggable) {
            Log.e(tag, content(msg + Log.getStackTraceString(tr), element))
        }
    }

    private fun json(logType: Int, tag: String?, msg: String) {
        if (!debuggable) {
            return
        }
        val json: String
        json = try {
            if (msg.startsWith("{")) {
                val jsonObject = JSONObject(msg)
                jsonObject.toString(JSON_INDENT)
            } else if (msg.startsWith("[")) {
                val jsonArray = JSONArray(msg)
                jsonArray.toString(JSON_INDENT)
            } else {
                msg
            }
        } catch (e: JSONException) {
            msg
        }
        log(logType, tag, json)
    }

    // find the target invoked method
    private val traceElement: StackTraceElement?
        get() {
            // find the target invoked method
            var shouldTrace = false
            val stackTrace = Thread.currentThread().stackTrace
            for (stackTraceElement in stackTrace) {
                val isLogMethod = stackTraceElement.className == L_CLASS_NAME
                if (shouldTrace && !isLogMethod) {
                    return stackTraceElement
                }
                shouldTrace = isLogMethod
            }
            return null
        }

    private fun content(msg: String, element: StackTraceElement?): String {
        return if (element == null) {
            make(msg)
        } else {
            make(
                    "(",
                    element.fileName,
                    ":", element.lineNumber.toString(),
                    ")", " ", msg
            )
        }
    }

    private fun getTag(element: StackTraceElement?): String {
        var result: String
        if (element != null) {
//            result = element.getClassName();
//            int lastIndex = result.lastIndexOf(".");
//            result = result.substring(lastIndex + 1, result.length());
            result = element.fileName
            val lastIndex = result.lastIndexOf(".")
            result = result.substring(0, lastIndex)
        } else {
            result = "temp_tag"
        }
        return result
    }

    private fun make(vararg param: String): String {
        val buffer = StringBuilder()
        for (s in param) {
            buffer.append(s)
        }
        return buffer.toString()
    }

    private fun isJSONValid(jsonInString: String): Boolean {
        try {
            val json = JSONTokener(jsonInString).nextValue()
            if (json is JSONObject || json is JSONArray) {
                return true
            }
        } catch (e: JSONException) {
//            e.printStackTrace();
            return false
        }
        return false
    }

    private fun doLog(logType: Int, tag: String?, message: String) {
        if (TextUtils.isEmpty(message)) {
            log(logType, tag, "The log content is null or empty！")
            return
        }
        if (isJSONValid(message)) {
            json(logType, tag, message)
        } else {
            log(logType, tag, message)
        }
    }

    @JvmStatic
    fun v(msg: String) {
        v(null, msg)
    }

    @JvmStatic
    fun d(msg: String) {
        d(null, msg)
    }

    @JvmStatic
    fun i(msg: String) {
        i(null, msg)
    }

    @JvmStatic
    fun w(msg: String) {
        w(null, msg)
    }

    @JvmStatic
    fun e(msg: String) {
        e(null, msg)
    }

    @JvmStatic
    fun v(tag: String?, msg: String) {
        doLog(VERBOSE, tag, msg)
    }

    @JvmStatic
    fun d(tag: String?, msg: String) {
        doLog(DEBUG, tag, msg)
    }

    @JvmStatic
    fun i(tag: String?, msg: String) {
        doLog(INFO, tag, msg)
    }

    @JvmStatic
    fun w(tag: String?, msg: String) {
        doLog(WARN, tag, msg)
    }

    @JvmStatic
    fun e(tag: String?, msg: String) {
        doLog(ERROR, tag, msg)
    }

    @JvmStatic
    fun compose(block: ComposeBuilder.Setter.() -> Unit): ComposeBuilder {
        val builder = ComposeBuilder(getTag(traceElement))
        block(builder.setter)
        return builder
    }

    /**
     * quick tag with "www"
     *
     * @param msg
     */
    @JvmStatic
    fun www(msg: String) {
        if (debuggable) {
            Log.d(TAG_WWW, content(msg, traceElement))
        }
    }

    /**
     * thread msg
     */
    fun trace() {
        traceElement // 获取一下信息
        val thread = Thread.currentThread()
        val threadMsg = buildString {
            append("Process_id:")
            append(Process.myPid())
            append("\n")
            append("Thread_name:")
            append(thread.name)
            append("\n")
            append("Thread_id:")
            append(thread.id)
        }
        d(threadMsg)
    }

    class ComposeBuilder(private var tag: String?) {
        private var type = 0
        internal val setter = Setter()

        inner class Setter internal constructor() {
            private val stringBuilder: StringBuilder = StringBuilder()

            fun append(msg: String?) {
                stringBuilder.append(msg).append("\n")
            }

            override fun toString(): String {
                return setter.stringBuilder.toString().trimEnd {
                    it == '\n'
                }
            }
        }

        fun d(tag: String? = null) {
            type = DEBUG
            this.tag = tag
            print()
        }

        fun v(tag: String? = null) {
            type = DEBUG
            this.tag = tag
            print()
        }

        fun i(tag: String? = null) {
            type = INFO
            this.tag = tag
            print()
        }

        fun e(tag: String? = null) {
            type = ERROR
            this.tag = tag
            print()
        }

        fun w(tag: String? = null) {
            type = WARN
            this.tag = tag
            print()
        }

        fun www() {
            type = DEBUG
            tag = TAG_WWW
            print()
        }

        private fun print() {
            doLog(type, tag, setter.toString())
        }
    }
}