package com.fintek.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.util_example.R;
import com.fintek.utils_androidx.battery.BatteryUtils;
import com.fintek.utils_androidx.device.DeviceUtils;
import com.fintek.utils_androidx.log.Timber;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.network.NetworkUtils;
import com.fintek.utils_androidx.storage.SDCardUtils;
import com.fintek.utils_androidx.storage.StorageUtils;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);


        TimberUtil.e(SDCardUtils.getTotalSize(), SDCardUtils.getAvailableSize());
    }
}