package com.fintek.util;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.model.AppConfigReq;
import com.fintek.model.BaseResponse;
import com.fintek.util_example.R;
import com.fintek.utils_androidx.FintekUtils;
import com.fintek.utils_androidx.location.LocationUtils;
import com.fintek.utils_androidx.log.Timber;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.model.CoronetResponse;
import com.fintek.utils_androidx.network.CoronetRequest;
import com.fintek.utils_androidx.network.Dispatchers;
import com.fintek.utils_androidx.network.MediaType;
import com.fintek.utils_androidx.network.Request;
import com.fintek.utils_androidx.network.RequestBody;
import com.fintek.utils_androidx.network.RequestTask;
import com.fintek.utils_androidx.thread.ThreadUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    private LocationUtils locationUtils = new LocationUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        CoronetRequest request = new CoronetRequest.Builder()
                .setBaseUrl("https://loanmarket.fastloan.id/")
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Content-Type", "application/Json;charset:Utf-8")
                .addHeader("x-merchant", "Kota Emas")
                .addHeader("x-version", "1.0.2")
                .addHeader("x-package-name", "com.fintek.supermarket_flutter")
                .setConnectTimeout(60, TimeUnit.SECONDS)
                .setReadTimeout(60, TimeUnit.SECONDS)
                .build();

        AppConfigReq req = new AppConfigReq(List.of(
                AppConfigReq.AppConfigTypeEnum.AlertFlag.INSTANCE.enumName(),
                AppConfigReq.AppConfigTypeEnum.AlertContent.INSTANCE.enumName()));

        RequestBody requestBody = RequestBody.create(
                new Gson().toJson(req),
                MediaType.toMediaType("application/json; charset=utf-8")
        );

        RequestTask<BaseResponse<String>> task = request.call(new Request.Builder()
                .url("/api/common/get-app-config")
                .post(requestBody).build());

        task.onNext(TimberUtil::e)
                .onError(TimberUtil::e)
                .onCancel(unit -> {
                    TimberUtil.v("cancel");
                })
                .execute(Dispatchers.CPU);
    }
}