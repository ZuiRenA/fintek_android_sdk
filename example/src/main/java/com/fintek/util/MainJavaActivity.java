package com.fintek.util;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.util_example.R;
import com.fintek.utils_androidx.FintekUtils;
import com.fintek.utils_androidx.battery.BatteryUtils;
import com.fintek.utils_androidx.call.CallUtils;
import com.fintek.utils_androidx.contact.ContactUtils;
import com.fintek.utils_androidx.device.DeviceUtils;
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
import com.fintek.utils_androidx.network.NetworkUtils;
import com.fintek.utils_androidx.phone.PhoneUtils;
import com.fintek.utils_androidx.sms.SmsUtils;
import com.fintek.utils_androidx.storage.SDCardUtils;
import com.fintek.utils_androidx.storage.StorageUtils;
import com.fintek.utils_androidx.thread.ThreadUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    private final LocationUtils utils = new LocationUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        getLifecycle().addObserver(utils);

        try {
            setupUtilsText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupClick();
    }

    private void setupUtilsText() throws IOException {
        TextView content = findViewById(R.id.tvTextContent);

        StringBuilder sbImage = new StringBuilder();
        List<String> imagePathList = ImageUtils.getImageList();
        sbImage.append("imagePathList").append("{ size = ").append(imagePathList.size()).append(" }, \n")
                .append("imagePathList[0]")
                .append(" = ")
                .append(ImageUtils.getImageParams(imagePathList.get(0)))
                .append("\n\n");

        StringBuilder sbSms = new StringBuilder();
        List<Sms> smsList = SmsUtils.getAllSms();
        sbSms.append("smsList").append("{ size = ").append(smsList.size()).append(" }, \n")
                .append("smsList[0]").append(" = ")
                .append(smsList.get(0).toString())
                .append("\n\n");

        StringBuilder sbCallLog = new StringBuilder();
        List<CallLog> callLogList = CallUtils.getCalls();
        sbCallLog.append("callLogList").append("{ size = ").append(callLogList.size()).append(" }, \n")
                .append("callLogList[0]").append(" = ")
                .append(callLogList.get(0).toString())
                .append("\n\n");

        StringBuilder sbContact = new StringBuilder();
        List<Contact> contactList = ContactUtils.getContacts();
        sbContact.append("contactList").append("{ size = ").append(contactList.size()).append(" }, \n")
                .append("contactList[0]").append(" = ")
                .append(contactList.get(0).toString())
                .append("\n\n");

        StringBuilder sbOther = new StringBuilder();

        sbOther.append("userAgent = ").append(NetworkUtils.getUserAgent()).append("\n")
                .append("imei = ").append(DeviceUtils.getImei()).append("\n")
                .append("androidId = ").append(DeviceUtils.getAndroidId()).append("\n")
                .append("mac = ").append(MacUtils.getMacAddress()).append("\n")
                .append("ip = ").append(NetworkUtils.getIPAddress(true)).append("\n")
                .append("storageTotalSize = ").append(StorageUtils.getTotalSize()).append("\n")
                .append("storageAvailableSize = ").append(StorageUtils.getAvailableSize()).append("\n")
                .append("imsi = ").append(DeviceUtils.getImsi()).append("\n")
                .append("isRoot = ").append(DeviceUtils.isRoot()).append("\n")
                .append("isLocationServiceEnable = ").append(LocationUtils.isLocationServiceEnable()).append("\n")
                .append("isNetwork = ").append(NetworkUtils.isNetworkEnable()).append("\n")
                .append("currentLang = ").append(LanguageUtils.getCurrentLocale()).append("\n")
                .append("model = ").append(HardwareUtils.getModel()).append("\n")
                .append("brand = ").append(HardwareUtils.getBrand()).append("\n")
                .append("deviceName = ").append(HardwareUtils.getDevice()).append("\n")
                .append("product = ").append(HardwareUtils.getProduct()).append("\n")
                .append("systemVersion = ").append(HardwareUtils.getSystemVersion()).append("\n")
                .append("release = ").append(HardwareUtils.getRelease()).append("\n")
                .append("sdkVersion = ").append(HardwareUtils.getSDKVersion()).append("\n")
                .append("physicalSize = ").append(HardwareUtils.getPhysicalSize()).append("\n")
                .append("serialNumber = ").append(HardwareUtils.getSerialNumber()).append("\n")
                .append("networkOperatorName = ").append(NetworkUtils.getNetworkOperatorName()).append("\n")
                .append("networkOperator = ").append(NetworkUtils.getNetworkOperator()).append("\n")
                .append("networkType = ").append(NetworkUtils.getNetworkType()).append("\n")
                .append("phoneType = ").append(PhoneUtils.getPhoneType()).append("\n")
                .append("phoneNumber = ").append(PhoneUtils.getPhoneNumber()).append("\n")
                .append("mcc = ").append(PhoneUtils.getMCC()).append("\n")
                .append("mnc = ").append(PhoneUtils.getMNC()).append("\n")
                .append("localeIso3Language = ").append(LanguageUtils.getIso3Language()).append("\n")
                .append("localeIso3Country = ").append(LanguageUtils.getIso3Country()).append("\n")
                .append("timeZoneId = ").append(PhoneUtils.getTimeZoneId()).append("\n")
                .append("cid = ").append(PhoneUtils.getCID()).append("\n")
                .append("dns = ").append(NetworkUtils.getDns()).append("\n")
                .append("percent = ").append(BatteryUtils.getPercent()).append("\n")
                .append("isCharging = ").append(BatteryUtils.isCharging()).append("\n")
                .append("isUsbCharging = ").append(BatteryUtils.isUsbCharging()).append("\n")
                .append("isAcCharging = ").append(BatteryUtils.isAcCharging()).append("\n")
                .append("bssid = ").append(NetworkUtils.getBSSID()).append("\n")
                .append("ssid = ").append(NetworkUtils.getSSID()).append("\n")
                .append("configuredBSSID = ").append(NetworkUtils.getConfiguredBSSID()).append("\n")
                .append("configuredSSID = ").append(NetworkUtils.getConfiguredSSID()).append("\n")
                .append("configuredMac = ").append(NetworkUtils.getConfiguredMacByWifi()).append("\n")
                .append("name = ").append(NetworkUtils.getConnectingWifiName()).append("\n")
                .append("mainStoragePath = ").append(StorageUtils.getMainStoragePath()).append("\n")
                .append("externalStoragePath = ").append(StorageUtils.getExternalStoragePath()).append("\n");

        DeviceUtils.getGaid(s -> sbOther.append("gaid = ").append(s).append("\n"));

        String all = sbImage.toString() + sbSms.toString() + sbCallLog.toString()
                + sbContact.toString() + sbOther.toString();

        ThreadUtils.runOnUiThreadDelay(() -> content.setText(all), 4000);
    }

    private void setupClick() {
        Button gotoKotlinPage = findViewById(R.id.tvGotoKotlin);
        gotoKotlinPage.setOnClickListener((view) -> {
            Intent intent = new Intent(MainJavaActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}