package com.example.comp7506.todolist.Utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
/**
 * Network Utility class
 */
public class NetWorkUtils {
    /**
     * Check whether there is a network connection
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            // Obtain all connection management objects of the mobile phone (including wi-fi, NET and other connection management)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // Obtain NetworkInfo Object
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null)
                return networkInfo.isAvailable();
        }
        return false;
    }
    /**
     * Determine whether the WIFI network is available
     *
     * @param context
     * @return
    /
    /**
     * Check whether the MOBILE network is available
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            // Obtain all connection management objects of the mobile phone (including wi-fi, NET and other connection management)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // Obtain NetworkInfo Object
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return networkInfo.isAvailable();
        }
        return false;
    }
    /**
     * Gets information about the current network connection type
     * native
     *
     * @param context
     * @return
     */    public static int getConnectedType(Context context) {
        if (context != null) {
            // Obtain all connection management objects of the mobile phone (including wi-fi, NET and other connection management)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // Obtain NetworkInfo Object
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {

                return networkInfo.getType();
            }
        }
        return -1;
    }
    /**
     * Obtain the current network status: No network -0: WIFI network 1:4g network - 4:3g network - 3:2g network -2
     * the custom
     *
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        int netType = 0;
        //Obtain all connection management objects of the mobile phone
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Obtain NetworkInfo Object
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return netType;
        }

        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //3G
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 4;
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 3;
                //2G
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 2;
            } else {
                netType = 2;
            }
        }
        return netType;
    }
    /**
     * Determine whether GPS is on
     * ACCESS_FINE_LOCATION permissions
     * @param context
     * @return
     */
    public static boolean isGPSEnabled(Context context) {
        // Get all connection LOCATION_SERVICE objects for the phone
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}