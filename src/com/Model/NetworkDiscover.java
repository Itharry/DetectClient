package com.Model;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import com.ViewModel.CustomGridViewAdapter;
import com.ViewModel.Item;
import com.example.detectclient.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NetworkDiscover extends AsyncTask<Void, Client, Void> {
	private GridView gridView;
	private int host_done = 0;
	private long start = 0;
	private long end = 0;
	private long size = 0;
	private String nmblookupLocation = "";
	private ArrayList<Client> clients;
	private ArrayList<Item> gridArray;
	private Activity activity;
	private String rs="";
	private CustomGridViewAdapter customGridViewAdapter;
	private Button btnInstall;

	public NetworkDiscover(Activity activity) {
		clients = new ArrayList<Client>();
		this.activity = activity;
		gridArray = new ArrayList<Item>();
	}

	/*public void setSource(Activity activity) {
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
	}*/

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
				InetAddress inet = null;
				inet = InetAddress.getByName(client.ipAddress);
				client.isAlive = inet.isReachable(200);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			// TODO Auto-generated method stub
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Client... clients) {
		super.onProgressUpdate(clients);
		ProgressBar progress = (ProgressBar) activity.findViewById(R.id.progressBar1);
		progress.setProgress((int) (host_done * 100 / size));
		TextView per = (TextView)activity.findViewById(R.id.txtPercent);
		per.setText(String.valueOf((int)(host_done * 100 / size))+ "%");
		/*Ras da duoc config*/
		if(clients[0]!=null){
			Bitmap imgRaspi = BitmapFactory.decodeResource(activity.getResources(), R.drawable.pc1);
			gridArray.add(new Item(imgRaspi,"Máy " + String.valueOf("1")));
			gridView = (GridView) activity.findViewById(R.id.gridView1);
			customGridViewAdapter = new CustomGridViewAdapter(this.activity, R.layout.row_grid, gridArray, 1);
			gridView.setAdapter(customGridViewAdapter);
		}
	}
	@Override
	protected void onPostExecute(Void void1)
	{
		super.onPostExecute(void1);
		MainActivity.clients = clients;
		
		btnInstall = (Button)activity.findViewById(R.id.btnStart);
		btnInstall.setEnabled(true);
	}
}
