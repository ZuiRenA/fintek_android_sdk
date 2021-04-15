package com.fintek.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.model.AppConfigReq;
import com.fintek.model.UserExtInfoReq;
import com.fintek.util_example.R;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.model.BaseResponse;
import com.fintek.utils_androidx.network.Convert;
import com.fintek.utils_androidx.network.CoronetRequest;
import com.fintek.utils_androidx.network.Dispatchers;
import com.fintek.utils_androidx.network.MediaType;
import com.fintek.utils_androidx.network.Request;
import com.fintek.utils_androidx.network.RequestBody;
import com.fintek.utils_androidx.network.RequestTask;
import com.fintek.ntl_utils.upload.UploadUtils;
import com.fintek.utils_mexico.FintekMexicoUtils;
import com.fintek.utils_mexico.model.ExtensionModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
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
                .setBaseUrl("http://47.117.39.82:8180")
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Content-Type", "application/Json;charset:Utf-8")
                .addHeader("x-merchant", "mexico")
                .addHeader("x-version", "1.0.0")
                .addHeader("x-package-name", "com.fintek.mexico_laon")
                .addHeader("x-auth-token", "d47fd6a35b034d249ed5bafb5333d6b3")
                .setConnectTimeout(60, TimeUnit.SECONDS)
                .setReadTimeout(60, TimeUnit.SECONDS)
                .build();

        ExtensionModel temp = FintekMexicoUtils.INSTANCE.getTemp();
        temp.setUserId(19);
        temp.setMerchant("mexico");
        UserExtInfoReq req = new UserExtInfoReq(temp);

        RequestBody requestBody = RequestBody.create(
                new Gson().toJson(req),
                MediaType.toMediaType("application/json; charset=utf-8")
        );

        RequestTask<BaseResponse<Void>> task = request.call(new Request.Builder()
                .url("/api/auth/ext-info")
                .post(requestBody).build(), s -> {
                    try {
                        ParameterizedType types = Types.newParameterizedType(BaseResponse.class, Void.class);
                        JsonAdapter<BaseResponse<Void>> adapter = new Moshi.Builder().build()
                                .adapter(types);
                        return adapter.fromJson(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                });

        task.onNext(TimberUtil::e)
                .onError(TimberUtil::e)
                .onCancel(unit -> TimberUtil.v("cancel"))
                .execute(Dispatchers.CPU);
    }
}