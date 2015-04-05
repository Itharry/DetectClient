package com.example.detectclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import android.content.res.AssetManager;
import android.util.Log;

public class Function {
	public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    public static boolean copyAsset(AssetManager assetManager,
                               String fromAssetPath, String toPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);

            File file = new File(toPath);
            if (file.exists())
                file.delete();
            file = new File(toPath);
            boolean created = file.createNewFile();
            System.out.println("created file = " +created);
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean isX86Cpu() {
        boolean bool;
        do {
            StringBuilder localStringBuilder;
            try {
                BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ro.product.cpu.abi").getInputStream()));
                char[] arrayOfChar = new char[4096];
                localStringBuilder = new StringBuilder();
                for (; ; ) {
                    int i = localBufferedReader.read(arrayOfChar);
                    if (i <= 0) {
                        break;
                    }
                    localStringBuilder.append(arrayOfChar, 0, i);
                }
                localBufferedReader.close();
            } catch (IOException localIOException) {
                Log.d("ScanLanNetworkPC", "-------get sys prop error------" + localIOException.toString());
                localIOException.printStackTrace();
                return false;
            }
            bool = localStringBuilder.toString().contains("x86");
        } while (!bool);
        return true;
    }
    public static boolean TestConnection(String hostname)
    {
    	Connection conn = new Connection(hostname);
    	try {
			conn.connect(null,100,100);
			return conn.authenticateWithPassword("pi", "raspberry");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
}
