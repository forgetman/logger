package logger_test.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import logger.L
import logger.L.d
import logger.L.e
import logger.L.i
import logger.L.w
import logger.L.www

class MainActivity : AppCompatActivity() {

    companion object {
        const val jsonData2 = "{\"error_code\":0,\"msg\":\"ok\",\"server_time\":1528103150,\"data\":{\"customer\":{\"created_at\":\"\",\"title\":\"大社保客服\",\"content\":\"客服工作时间：周一到周五 9:30-18:30\",\"icon_url\":\"http:\\/\\/img.dashebao.com\\/icon_dashebao.jpeg\",\"view_type\":36}}}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        d("-------", "onCreate: ddddd")
        i("-------", "onCreate: iiiii")
        e("-------", "onCreate: eeeee")
        w("-------", "onCreate: wwwww")
        www("onCreate:  = " + "wwwww2")

        L.compose {
            append("LogMessage1")
            append("LogMessage2")
            append("LogMessage3")
        }.d()

        L.trace()
    }
}