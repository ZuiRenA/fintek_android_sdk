package com.fintek.utils_androidx.device

import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.thread.SimpleTask
import com.google.android.gms.ads.identifier.AdvertisingIdClient


/**
 * This is a task implement runnable
 * Inherit it should override [com.fintek.utils_androidx.thread.Task.onSuccess]
 */
abstract class GaidTask : SimpleTask<String>() {

    override fun doInBackground(): String {
        return AdvertisingIdClient.getAdvertisingIdInfo(FintekUtils.requiredContext).id
    }
}