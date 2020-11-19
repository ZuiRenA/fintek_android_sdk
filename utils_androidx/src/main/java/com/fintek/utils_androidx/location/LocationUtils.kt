package com.fintek.utils_androidx.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.model.LocationData


class LocationUtils : LocationListener, LifecycleObserver {
    private val locationManager: LocationManager?
        get() = FintekUtils.requiredContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private var locationData: LocationData? = null

    /**
     * Register listener, it will listen location data when use it.
     * if you want auto register, please use [Lifecycle.addObserver]
     *
     * e.g.
     * ```java
     * public class MainActivity extends AppCompatActivity {
     *      private LocationUtils utils = new LocationUtils();
     *
     *      @Override
     *      protected void onCreate(Bundle savedInstanceState) {
     *          super.onCreate(savedInstanceState);
     *
     *          getLifecycle().addObserver(utils);
     *      }
     * }
     * ```
     *
     * other way
     * ``` java
     * public class Test {
     *      public LocationData test() {
     *          LocationUtils utils = new LocationUtils();
     *          utils.registerLocationListener();
     *          LocationData data = utils.getLocationData();
     *          utils.unregisterLocationListener();
     *          return data;
     *      }
     * }
     * ```
     *
     *
     * @throws IllegalArgumentException if provider is null or doesn't exist
     * on this device
     * @throws RuntimeException if the calling thread has no Looper
     * @throws SecurityException if no suitable permission is present
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @Throws(IllegalArgumentException::class, RuntimeException::class, SecurityException::class)
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun registerLocationListener() {
        locationData = LocationData()
        locationData?.locationType = LocationData.INIT
        initLocationService()
    }

    /**
     * Unregister listener, please use it if you don't need [LocationUtils]
     * if you want auto unregister please use [Lifecycle.addObserver]
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun unregisterLocationListener() {
        if (Build.VERSION.SDK_INT >= 23
            && ActivityCompat.checkSelfPermission(FintekUtils.requiredContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(FintekUtils.requiredContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (locationManager != null) {
            locationManager?.removeUpdates(this)
        }

        if (locationData != null) {
            locationData = null
        }
    }

    /**
     * Return location data
     *
     * get location data, please used between [registerLocationListener] and [unregisterLocationListener]
     *
     * @return if will null!!
     */
    fun getLocationData(): LocationData? = locationData

    override fun onLocationChanged(location: Location) {
        location.accuracy
        locationData?.location = location
    }

    /**
     * @throws IllegalArgumentException if provider is null or doesn't exist
     * on this device
     * @throws RuntimeException if the calling thread has no Looper
     * @throws SecurityException if no suitable permission is present
     */
    @Throws(IllegalArgumentException::class, RuntimeException::class, SecurityException::class)
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun initLocationService() {
        locationManager?.let {
            val providers = it.getProviders(true)
            val providerType: String = when {
                providers.contains(LocationManager.NETWORK_PROVIDER) -> {
                    locationData?.locationType = LocationData.NETWORK
                    LocationManager.NETWORK_PROVIDER
                }

                providers.contains(LocationManager.GPS_PROVIDER) -> {
                    locationData?.locationType = LocationData.GPS
                    LocationManager.GPS_PROVIDER
                }

                else -> {
                    locationData?.locationType = LocationData.LOCATION_NULL
                    ""
                }
            }

            //check permission
            if (Build.VERSION.SDK_INT >= 23
                && ActivityCompat.checkSelfPermission(FintekUtils.requiredContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FintekUtils.requiredContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            if (ActivityCompat.checkSelfPermission(FintekUtils.requiredContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FintekUtils.requiredContext , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }

            val preLocation = locationManager?.getLastKnownLocation(providerType)
            if (preLocation != null) {
                locationData?.location = preLocation
            } else {
                locationData?.locationType = LocationData.NATIVE_NULL
            }

            locationManager?.requestLocationUpdates(providerType, 100L, 0f, this)
        }
    }
}