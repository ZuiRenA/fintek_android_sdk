package com.fintek.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.util_example.R;
import com.fintek.utils_androidx.call.CallUtils;
import com.fintek.utils_androidx.log.Timber;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.model.CallLog;

import java.util.List;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);


        List<CallLog> callLogList = CallUtils.getCalls();
        TimberUtil.v(callLogList.toString());
    }
}