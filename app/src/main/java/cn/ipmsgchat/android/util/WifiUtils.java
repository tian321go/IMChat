package  cn.ipmsgchat.android.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.apache.http.conn.util.InetAddressUtils;
import android.content.Context;
import android.net.wifi.WifiManager;


public class WifiUtils {
    public static final String TAG = "SZU_WifiUtils";

    private static WifiUtils mWifiUtils = null;
    public WifiManager mWifiManager;

    private WifiUtils(Context paramContext) {
        mWifiManager = (WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE);

    }

    public static WifiUtils getInstance(Context paramContext) {
        if (mWifiUtils == null)
            mWifiUtils = new WifiUtils(paramContext);
        return mWifiUtils;
    }


    public String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress mInetAddress = enumIpAddr.nextElement();
                    if (!mInetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(mInetAddress.getHostAddress())) {
                        return mInetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}