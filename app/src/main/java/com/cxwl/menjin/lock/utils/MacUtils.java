package com.cxwl.menjin.lock.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import com.cxwl.menjin.lock.MainApplication;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


/**
 * @author xlei
 * @Date 2018/4/26.
 */

public class MacUtils {
    public static String getMac() {
        String mac = getWifiMac();
        if (mac == null) {
            return getEthMac();
        } else {
            return mac;
        }
    }

    @SuppressLint("WifiManagerLeak")
    public static String getWifiMac() {
        String mac = getMacFromLocal();
        if (mac == null) {
            WifiManager wifi = (WifiManager) MainApplication.getApplication().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
            if (mac != null) {
                saveMacToLocal(mac);
            }
        }
        return mac;
    }
    private static void saveMacToLocal(String mac) {
        SharedPreferences sharedPref = MainApplication.getApplication().getSharedPreferences("residential", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("wifi_mac_address", mac);
        editor.commit();
    }

    private static String getMacFromLocal() {
        SharedPreferences sharedPref = MainApplication.getApplication().getSharedPreferences("residential", Context.MODE_PRIVATE);
        String mac = sharedPref.getString("wifi_mac_address", null);
        return mac;
    }
    public static String getEthMac() {
        byte[] mac = null;
        StringBuffer sb = new StringBuffer();
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress ip = address.nextElement();
                    if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress()) {
                        continue;
                    }
                    if (ip.isSiteLocalAddress()) {
                        mac = ni.getHardwareAddress();
                    } else if (!ip.isLinkLocalAddress()) {
                        mac = ni.getHardwareAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (mac != null) {
            for (int i = 0; i < mac.length; i++) {
                sb.append(parseByte(mac[i]));
            }
            return sb.substring(0, sb.length() - 1);
        } else {
            return null;
        }
    }

    public static String parseByte(byte b) {
        String s = "00" + Integer.toHexString(b) + ":";
        return s.substring(s.length() - 3);
    }

}
