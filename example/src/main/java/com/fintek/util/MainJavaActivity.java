package com.fintek.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.util_example.R;
import com.fintek.utils_androidx.device.DeviceUtils;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.network.NetworkUtils;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        DeviceUtils.getGaid(TimberUtil::e);
        NetworkUtils.getIPAddressAsync(true, TimberUtil::w);
        NetworkUtils.getDnsAsync(TimberUtil::i);
    }
}