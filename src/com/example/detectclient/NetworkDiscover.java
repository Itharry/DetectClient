package com.example.detectclient;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NetworkDiscover extends AsyncTask<Void, Client, Void> {
	private int host_done = 0;
	private long start = 0;
	private long end = 0;
	private long size = 0;
	private String nmblookupLocation = "";
	private ArrayList<Client> clients;
	private Activity activity;
	private String rs="";

	public NetworkDiscover(Activity activity) {
		clients = new ArrayList<Client>();
		this.activity = activity;
	}

	public void setSource(Activity activity) {
		nmblookupLocation = "/data/data/" + activity.getPackageName()
				+ "/nmblookup";
		File file = new File(nmblookupLocation);
		if (!file.exists()) {
			Function.copyAsset(activity.getAssets(), "cmd/nmblookup",
					nmblookupLocation);
			try {
				Process p = Runtime.getRuntime().exec(
						"chmod 744 " + nmblookupLocation);
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setNetwork(long ipStart, long ipEnd) {
		this.start = ipStart;
		this.end = ipEnd;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		size = (int) (end - start);
	}

	@Override
	protected Void doInBackground(Void... params) {
		for (long i = start; i < end; i++) {
			host_done++;
			Client client = new Client();
			client.ipAddress = NetInfo.getIpFromLong(i);
			try {
				Process p1 = Runtime.getRuntime().exec(
						"ping -c 1 " + client.ipAddress +"-W 100");
				int returnVal = p1.waitFor();
				client.isAlive = (returnVal == 0);
				p1.destroy();
				/*if (!client.isAlive) {
					InetAddress inet = null;
					inet = InetAddress.getByName(client.ipAddress);
					client.isAlive = inet.isReachable(1000);
				}*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				publishProgress((Client) null);
			} catch (InterruptedException e) {
				// TODO: handle exception
				e.printStackTrace();
				publishProgress((Client) null);
			}
			if (client.isAlive) {
				client.MAC = client.getHardwareAddress(client.ipAddress);
				if (client.MAC.contains("b8:27:eb")) {
					clients.add(client);
					publishProgress(client);
				} else if (Function.TestConnection(client.ipAddress)) {
					clients.add(client);
					publishProgress(client);
				} else
					publishProgress((Client) null);
			}
			else
				publishProgress((Client) null);

			/*
			 * Process p2; try { p2 = Runtime.getRuntime().exec( "./" +
			 * nmblookupLocation + " -A " + client.ipAddress); BufferedReader in
			 * = new BufferedReader( new
			 * InputStreamReader(p2.getInputStream())); String line = null;
			 * while ((line = in.readLine()) != null) { if
			 * (line.contains("ACTIVE") && !line.contains("WORKGROUP")) {
			 * client.hostname = line.substring(0, line.indexOf("<")).trim() +
			 * " " + client.ipAddress + "\n"; break; } else if
			 * (line.contains("No reply from")) { //Test Connnection
			 * if(Function.TestConnection(client.ipAddress)) {
			 * clients.add(client); publishProgress(client); }
			 * 
			 * } if(line.contains("raspberry")){ clients.add(client);
			 * publishProgress(client); } } } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */

			// TODO Auto-generated method stub
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Client... clients) {
		super.onProgressUpdate(clients);
		ProgressBar progress = (ProgressBar) activity.findViewById(R.id.progressBar1);
		progress.setProgress((int) (host_done * 100 / size));
		TextView per = (TextView)activity.findViewById(R.id.textView1);
		per.setText(String.valueOf((int)(host_done * 100 / size)));
		if(clients[0]!=null){
			rs+=clients[0].ipAddress+"\n";
			TextView text = (TextView)activity.findViewById(R.id.text);
			text.setText(rs);
		}
		
		
	}
	@Override
	protected void onPostExecute(Void void1)
	{
		super.onPostExecute(void1);
		MainActivity.clients = clients;
	}
}
