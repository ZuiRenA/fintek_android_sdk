package com.fintek.util;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.model.AppConfigReq;
import com.fintek.util_example.R;
import com.fintek.utils_androidx.FintekUtils;
import com.fintek.utils_androidx.location.LocationUtils;
import com.fintek.utils_androidx.log.Timber;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.model.BaseResponse;
import com.fintek.utils_androidx.model.CoronetResponse;
import com.fintek.utils_androidx.network.CoronetRequest;
import com.fintek.utils_androidx.network.Dispatchers;
import com.fintek.utils_androidx.network.MediaType;
import com.fintek.utils_androidx.network.Request;
import com.fintek.utils_androidx.network.RequestBody;
import com.fintek.utils_androidx.network.RequestTask;
import com.fintek.utils_androidx.thread.ThreadUtils;
import com.fintek.utils_androidx.upload.UploadUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

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

        RequestTask<BaseResponse<Map<String, String>>> task = request.call(new Request.Builder()
                .url("/api/common/get-app-config")
                .post(requestBody).build(), new TypeToken<BaseResponse<Map<String, String>>>(){});

        task.onNext(data -> TimberUtil.e(
                data, data.getData().get("alert_flag"), data.getData().get("alert_content")
        ))
                .onError(TimberUtil::e)
                .onCancel(unit -> TimberUtil.v("cancel"))
                .execute(Dispatchers.CPU);
    }
}