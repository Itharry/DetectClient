package com.example.detectclient;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by ITHARRY on 15/02/2015.
 */
public class NetInfo {
    private Context ctxt;
    private WifiInfo info;

    public static final String NOIP = "0.0.0.0";

    public String intf = "eth0";
    public String ip = NOIP;
    public int cidr = 24;

    public int speed =0;
    public String ssid=null;
    public String bssid=null;
    public String macAddress = "00:00:00:00:00:00";
    public String netmaskIP = "255.255.255.255";
    public String gatewayIP = "0.0.0.0";

    public static long ip_start;
    public static long ip_end;
    public NetInfo(final Context ctxt)
    {
        this.ctxt=ctxt;
        getIP();
        getWifiInfo();
        ip_start=getLongFromIp(gatewayIP)+100;
        ip_end=ip_start+8;
    }
    public void getIP()
    {
        try
        {
            for(Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();en.hasMoreElements();){
                NetworkInterface ni = en.nextElement();
                intf=ni.getName();
                ip=getInterfaceFirstIp(ni);
                if(ip!=NOIP)
                    break;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    private String getInterfaceFirstIp(NetworkInterface ni) {
        if (ni != null) {
            for (Enumeration<InetAddress> nis = ni.getInetAddresses(); nis.hasMoreElements();) {
                InetAddress ia = nis.nextElement();
                if (!ia.isLoopbackAddress()) {
                    if (ia instanceof Inet6Address) {
                        continue;
                    }
                    return ia.getHostAddress();
                }
            }
        }
        return NOIP;
    }
    public boolean getWifiInfo(){
        WifiManager wifi = (WifiManager) ctxt.getSystemService(Context.WIFI_SERVICE);
        if(wifi!=null)
        {
            info=wifi.getConnectionInfo();

            speed=info.getLinkSpeed();
            ssid=info.getSSID();
            bssid=info.getBSSID();
            macAddress=info.getMacAddress();
            gatewayIP=getIpFromInt(wifi.getDhcpInfo().gateway);
            netmaskIP = getIpFromInt(wifi.getDhcpInfo().netmask);

            return true;
        }
        return false;
    }
    public static String getIpFromInt(int ip_int) {
        String ip = "";
        for (int k = 0; k < 4; k++) {
            ip = ip + ((ip_int >> k * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }
    public static long getLongFromIp(String ip_addr) {
        String[] a = ip_addr.split("\\.");
        return (Integer.parseInt(a[0]) * 16777216 + Integer.parseInt(a[1]) * 65536
                + Integer.parseInt(a[2]) * 256 + Integer.parseInt(a[3]));
    }
    public static String getIpFromLong(long ip_long) {
        String ip = "";
        for (int k = 3; k > -1; k--) {
            ip = ip + ((ip_long >> k * 8) & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }
}
