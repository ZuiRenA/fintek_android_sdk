package com.fintek.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fintek.util_example.R;
import com.fintek.utils_androidx.image.ImageUtils;
import com.fintek.utils_androidx.log.TimberUtil;
import com.fintek.utils_androidx.model.ImageInfo;
import com.fintek.utils_androidx.model.Sms;
import com.fintek.utils_androidx.sms.SmsUtils;

import java.util.List;

/**
 * @author admin
 */
public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);


        List<String> imagePathList = ImageUtils.getImageList();
        StringBuilder sb = new StringBuilder();
        for (String path : imagePathList) {
            ImageInfo info = ImageUtils.getImageParams(path);
            if (info != null) {
                sb.append("[").append(info.toString()).append("]\n");
            }
        }
        TimberUtil.e(sb.toString());
    }
}