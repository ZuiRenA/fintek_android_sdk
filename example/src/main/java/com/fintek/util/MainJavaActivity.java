package com.fintek.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fintek.model.BaseResponse;
import com.fintek.model.UserExtInfoReq;
import com.fintek.util_example.R;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.network.CoronetRequest;
import com.fintek.utils_androidx.network.Dispatchers;
import com.fintek.utils_androidx.network.MediaType;
import com.fintek.utils_androidx.network.Request;
import com.fintek.utils_androidx.network.RequestBody;
import com.fintek.utils_androidx.network.RequestTask;
import com.fintek.utils_mexico.FintekMexicoUtils;
import com.fintek.utils_mexico.model.ExtensionModel;
import com.google.gson.Gson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);
    }
}