package logger_test.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import logger.L;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        L.d("onCreate: fsdfsdfsdf");
        L.www("onCreate:  = " + "?sss");
    }
}