package logger_test.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import logger.L;

public class MainActivity extends AppCompatActivity {
    public static final String jsonData2 = "{\"error_code\":0,\"msg\":\"ok\",\"server_time\":1528103150,\"data\":{\"customer\":{\"created_at\":\"\",\"title\":\"大社保客服\",\"content\":\"客服工作时间：周一到周五 9:30-18:30\",\"icon_url\":\"http:\\/\\/img.dashebao.com\\/icon_dashebao.jpeg\",\"view_type\":36}}}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        L.d("-------","onCreate: fsdfsdfsdf");
//        L.www("onCreate:  = " + "?sss");

//        L.merge().d().append("logMessage1").append("logMessage2").append("logMessage3").end();


    }


}
