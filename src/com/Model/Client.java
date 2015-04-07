package com.Model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ITHARRY on 15/02/2015.
 */
public class Client {
    public boolean isAlive;
    public String ipAddress;
    public String hostname;
    public String MAC;
    private final static String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
    public Client()
    {
        this.isAlive=false;
        this.ipAddress=null;
        this.hostname=null;
        this.MAC="00:00:00:00:00:00";
    }
    public static String getHardwareAddress(String ip) {
        String hw = "00:00:00:00:00:00";
        BufferedReader bufferedReader = null;
        try {
            if (ip != null) {
                String ptrn = String.format(MAC_RE, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);
                bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), 8 * 1024);
                String line;
                Matcher matcher;
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        hw = matcher.group(1);
                        break;
                    }
                }
            } else {
                Log.e("MAC", "ip is null");
            }
        } catch (IOException e) {
            Log.e("MAC", "Can't open/read file ARP: " + e.getMessage());
            return hw;
        } finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Log.e("MAC", e.getMessage());
            }
        }
        return hw;
    }
}
