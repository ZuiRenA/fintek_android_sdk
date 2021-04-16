package com.fintek.utils_mexico.boardcastReceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fintek.utils_mexico.network.NetworkMexicoUtils

/**
 * Created by ChaoShen on 2021/4/16
 */
class NetworkBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val configuredWifi = NetworkMexicoUtils.getConfiguredWifi()
        NetworkMexicoUtils.configuredWifi.clear()
        NetworkMexicoUtils.configuredWifi.addAll(configuredWifi)
    }
}