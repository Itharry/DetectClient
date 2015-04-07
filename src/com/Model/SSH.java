package com.Model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.example.detectclient.R;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SSH extends AsyncTask<Void, Client, Void>{
	private ArrayList<SSHClient> sshclients;
	private ArrayList<Client> Rasclients;
	private byte[] reSource=null;
	private Activity activity;
	private int count;
	private TextView txtpercent;
	private ProgressBar progressBar;
	public SSH(Activity activity, ArrayList<Client> clients)
	{
		this.activity=activity;
		this.Rasclients=clients;
		this.count = 0;
		this.txtpercent =(TextView) activity.findViewById(R.id.txtPercent);
		this.progressBar = (ProgressBar)activity.findViewById(R.id.progressBar1);
		SetResource();
	}
	private void SetResource()
	{
		InputStream stream=null;
		stream=activity.getResources().openRawResource(activity.getResources().getIdentifier("source", "raw", activity.getPackageName()));
		byte[] buffer = new byte[2048];
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        int len = 0;
        try {
            while ((len = stream.read(buffer)) > 0) {
                byteOut.write(buffer, 0, len);
            }
            reSource = byteOut.toByteArray();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void CopyResourceToRaspberry(SSHClient sshClient)
	{
		try {
			SCPClient scpc = sshClient.getConnnection().createSCPClient();
			scpc.put(reSource, "source.jpg","/home/" + "pi" + "/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		
	}
	@Override
	protected void onProgressUpdate(Client...clients)
	{
		super.onProgressUpdate(clients);
		txtpercent.setText(String.valueOf(count*100/Rasclients.size()) + " %");
		progressBar.setProgress((int)count*100/Rasclients.size());
		
	}
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		for (Client client : Rasclients) {
			SSHClient sshClient = new SSHClient(client);
			if(sshClient.getisConnection()){
				CopyResourceToRaspberry(sshClient);
			}
			count++;
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void void1)
	{
		super.onPostExecute(void1);
	}
}
