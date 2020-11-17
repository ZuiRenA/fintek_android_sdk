package com.fintek.util;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.util_example.R;
import com.fintek.utils_androidx.call.CallUtils;
import com.fintek.utils_androidx.contact.ContactUtils;
import com.fintek.utils_androidx.device.DeviceUtils;
import com.fintek.utils_androidx.device.GaidTask;
import com.fintek.utils_androidx.log.Timber;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.model.CallLog;
import com.fintek.utils_androidx.model.Contact;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);


        DeviceUtils.getGaid(new GaidTask() {
            @Override
            public void onSuccess(String result) {
                TimberUtil.e("gaid: " + result, "thread: " + Thread.currentThread().getName());
            }
        });
    }
}