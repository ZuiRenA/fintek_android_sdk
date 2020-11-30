package com.fintek.util;

import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.util_example.R;
import com.fintek.utils_androidx.FintekUtils;
import com.fintek.utils_androidx.app.AppUtils;
import com.fintek.utils_androidx.battery.BatteryUtils;
import com.fintek.utils_androidx.call.CallUtils;
import com.fintek.utils_androidx.contact.ContactUtils;
import com.fintek.utils_androidx.device.DeviceUtils;
import com.fintek.utils_androidx.file.FileUtils;
import com.fintek.utils_androidx.hardware.HardwareUtils;
import com.fintek.utils_androidx.image.ImageUtils;
import com.fintek.utils_androidx.language.LanguageUtils;
import com.fintek.utils_androidx.location.LocationUtils;
import com.fintek.utils_androidx.log.Timber;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.mac.MacUtils;
import com.fintek.utils_androidx.model.CallLog;
import com.fintek.utils_androidx.model.Contact;
import com.fintek.utils_androidx.model.LocationData;
import com.fintek.utils_androidx.model.Sms;
import com.fintek.utils_androidx.model.StructPool;
import com.fintek.utils_androidx.network.NetworkUtils;
import com.fintek.utils_androidx.phone.PhoneUtils;
import com.fintek.utils_androidx.sms.SmsUtils;
import com.fintek.utils_androidx.storage.SDCardUtils;
import com.fintek.utils_androidx.storage.StorageUtils;
import com.fintek.utils_androidx.thread.ThreadUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    private LocationUtils locationUtils = new LocationUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        StructPool structPool = FintekUtils.INSTANCE.getAllStruct();
        String json = new Gson().toJson(structPool);
        TimberUtil.v(json);
    }
}