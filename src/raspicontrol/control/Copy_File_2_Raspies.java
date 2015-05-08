package raspicontrol.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import raspicontrol.model.Raspi;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

import com.example.detectclient.R;

public class Copy_File_2_Raspies extends AsyncTask<Void, Raspi, Void> {

	private byte[] resource;
	private Activity activity;
	private TextView tv_percent;
	private ProgressBar progressBar;
	private TextView tv_status;

	public Copy_File_2_Raspies(Activity activity) {
		this.activity = activity;
		init();
	}

	private void init() {
		// Get view
		tv_percent = (TextView) activity.findViewById(R.id.tv_percent);
		progressBar = (ProgressBar) activity.findViewById(R.id.pb_config);
		tv_status = (TextView) activity.findViewById(R.id.tv_status);

		// Set default value
		tv_status
				.setText(R.string.config_activity_status_installing_transfering_flie);
		tv_percent.setText("0%");
		progressBar.setProgress(0);

		// Get resoure file
		SetResource();
	}

	// Get file resoure to buffer
	private void SetResource() {
		InputStream stream = activity.getResources().openRawResource(
				activity.getResources().getIdentifier("mjpg_streamer", "raw",
						activity.getPackageName()));
		byte[] buffer = new byte[2048];
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		int len = 0;
		try {
			while ((len = stream.read(buffer)) > 0) {
				byteOut.write(buffer, 0, len);
			}
			resource = byteOut.toByteArray();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to copy file to raspi
	private void CopyResourceToRaspberry(Connection connection) {
		try {
			SCPClient scpc = connection.createSCPClient();
			scpc.put(resource, "mjpg_streamer.tar.gz", "/home/" + "pi" + "/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected void onProgressUpdate(Raspi... clients) {
		super.onProgressUpdate(clients);

	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		// Copy file to raspi step by step
		for (Raspi raspi : Common.raspies.getRaspies())
			if (raspi.isIs_chosen_to_configure())
				CopyResourceToRaspberry(raspi.getConnection());
		return null;
	}

	@Override
	protected void onPostExecute(Void void1) {
		super.onPostExecute(void1);
		// Update status when copy complete
		tv_percent.setText("10%");
		progressBar.setProgress(10);
		// Install Raspi
		new Install_Raspi(activity).execute();
	}
}
