package com.fintek.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.fintek.util_example.R;
import com.fintek.utils_androidx.image.ImageUtils;
import com.fintek.utils_androidx.log.Timber;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.log.TimberUtilKt;

import java.io.IOException;
import java.util.List;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        TimberUtil.e(1, 2, "", "4");
    }
}