package third.com.snail.trafficmonitor.engine.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import third.com.snail.trafficmonitor.engine.data.table.Network;
import third.com.snail.trafficmonitor.engine.manager.SpManager;

/**
 * Created by lic on 2014/9/25.
 * 查询网络类型
 */
public class CheckNetTypeTools {
    private static final String TAG = CheckNetTypeTools.class.getSimpleName();

    //TODO 网络切换时候是否能够获取正确的网络iface
    public static Network checkNet(Context context) {
        SpManager spManager = SpManager.getInstance(context.getApplicationContext());
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        Network network = new Network("none", Network.NetworkType.NONE);
        if (info == null) {
            return network;
        }
        String iface = getIfaceName();
        String typeName = info.getTypeName();
        if (typeName.equalsIgnoreCase("WIFI")) { //wifi连接
            try {
                WifiManager wifiManger = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManger.getConnectionInfo();
                if (wifiInfo != null) {
                    network.setWifiSSID(wifiInfo.getSSID());
                    //add by licong for invalid bssid begin
                    if (isBssid(wifiInfo.getBSSID())) {
                        network.setWifiBSSID(wifiInfo.getBSSID());
                    } else {
                        //防止查询出错，给予bssid为初始值
                        network.setWifiBSSID("00:00:00:00:00:00");
                    }
                } else {
                    network.setWifiSSID("0");
                    network.setWifiBSSID("00:00:00:00:00:00");
                }
            } catch (IllegalArgumentException exception) {
                network.setWifiSSID("0");
                network.setWifiBSSID("00:00:00:00:00:00");
            }
            //add by licong for invalid bssid end
            network.setNetworkType(Network.NetworkType.WIFI);
            if (spManager.getString(SpManager.SpData.DEFAULT_WIFI_IFACE).equals(SpManager.SpData.INVALID_STRING)) {
                spManager.putString(SpManager.SpData.DEFAULT_WIFI_IFACE, iface);
            }
            network.setIface(iface);
        } else if (typeName.equalsIgnoreCase("MOBILE")) {
            TelephonyManager telephone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String operator = telephone.getSimOperator();
            network.setNetworkName(operator);
            if (spManager.getString(SpManager.SpData.DEFAULT_MOBILE_IFACE).equals(SpManager.SpData.INVALID_STRING)) {
                spManager.putString(SpManager.SpData.DEFAULT_MOBILE_IFACE, iface);
            }
            network.setIface(iface);
            switch (telephone.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    network.setNetworkType(Network.NetworkType.MOBILE_2G);
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    network.setNetworkType(Network.NetworkType.MOBILE_3G);
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    network.setNetworkType(Network.NetworkType.MOBILE_4G);
                    break;
                default:
                    network.setNetworkType(Network.NetworkType.OTHER);
                    break;
            }
        } else {
            network.setNetworkName("other network");
            network.setIface(iface);
            network.setNetworkType(Network.NetworkType.OTHER);
        }
        LogWrapper.d(TAG, network.toString());
        return network;
    }

    private static String getIfaceName() {
        String iface = "";
        try {
            for (Enumeration<NetworkInterface> en =
                 NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                // Iterate over all IP addresses in each network interface.
                if (intf.isPointToPoint() || intf.isLoopback()
                        || !intf.isUp() || intf.getName().contains("p2p")) {
                    continue;
                }
                if (intf.getInterfaceAddresses().size() > 0) {
                    LogWrapper.e(TAG, intf.getName());
                    iface = intf.getName();
                    break;
                }
                /*for (Enumeration<InetAddress> enumIPAddr =
                             intf.getInetAddresses(); enumIPAddr.hasMoreElements(); ) {
                    InetAddress iNetAddress = enumIPAddr.nextElement();
                    // Loop back address (127.0.0.1) doesn't count as an in-use IP address.
                    if (!iNetAddress.isLoopbackAddress()) {
//                        LogWrapper.e(TAG, iNetAddress.getHostAddress() + "-" + intf.getName());
                        iface = intf.getName();
                    }
                }*/
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        LogWrapper.d(TAG, "Found iface: " + iface);
        return iface;
    }

    /**
     * 判断bssid（热点的mac地址）是不是标准的mac地址
     */
    public static boolean isBssid(String string) {
        boolean flag;
        try {
            String check = "^[a-f0-9]{2}(:[a-f0-9]{2}){5}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(string);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

}
