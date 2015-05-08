package raspicontrol.control;

import java.io.IOException;
import java.util.ArrayList;

import raspicontrol.activity.Control_Activity;
import raspicontrol.model.Raspi;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import ch.ethz.ssh2.Session;

import com.example.detectclient.R;

public class Install_Raspi extends AsyncTask<Void, Raspi, Void> {

	private Activity activity;
	private int count_percent;
	private TextView tv_status;

	public Install_Raspi(Activity activity) {

		this.activity = activity;
		init();
	}

	private void init() {
		// Get view
		tv_status = (TextView) activity.findViewById(R.id.tv_status);

		// Set default values
		count_percent = 0;
		tv_status
				.setText(R.string.config_activity_status_installing_configuring);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		String install_cmd = "sudo tar -zxvf mjpg_streamer.tar.gz && cd mjpg-streamer-experimental/deb-folder/ &&  sudo dpkg -i emacsen-common_2.0.5_all.deb &&  sudo dpkg -i cmake-data_2.8.9-1_all.deb &&  sudo dpkg -i libxmlrpc-core-c3_1.16.33-3.2_armhf.deb &&  sudo dpkg -i cmake_2.8.9-1_armhf.deb &&  sudo dpkg -i libjpeg62_6b1-3+deb7u1_armhf.deb &&  sudo dpkg -i libjpeg62-dev_6b1-3+deb7u1_armhf.deb &&  sudo dpkg -i libssh-4_0.5.4-1+deb7u1_armhf.deb &&  sudo dpkg -i omxplayer_0.3.6~git20150210~337004e_armhf.deb && cd ~ && cd mjpg-streamer-experimental/ && echo 'configured' >>status.txt && export LD_LIBRARY_PATH=.";
		// Send command to install
		ArrayList<Session> sessions = new ArrayList<Session>();
		for (Raspi raspi : Common.raspies.getRaspies())
			if (raspi.isIs_chosen_to_configure())
				try {
					sessions.add(raspi.getConnection().openSession());
					sessions.get(sessions.size() - 1).execCommand(
							get_setip_command(raspi.getIpAddress()) + " && "
									+ install_cmd);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		// Set percent in progress bar
		while (count_percent < 17) {
			try {
				Thread.sleep(7000);
				count_percent++;
				publishProgress((Raspi) null);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Check installation progess whether completed.
		boolean is_all_complete = false;
		while (!is_all_complete) {
			is_all_complete = true;
			for (Raspi raspi : Common.raspies.getRaspies())
				if (raspi.isIs_chosen_to_configure()
						&& (Common.readfile(raspi.getConnection(),
								"mjpg-streamer-experimental/status.txt"))
								.length() == 0)
					is_all_complete = false;
			if (!is_all_complete)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else {
				count_percent++;

				// Close all session
				for (Session session : sessions)
					session.close();

				publishProgress((Raspi) null);
			}
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Raspi... clients) {
		super.onProgressUpdate(clients);

		// Update progressbar and textview status
		ProgressBar progressBar = (ProgressBar) activity
				.findViewById(R.id.pb_config);
		TextView tv_percent = (TextView) activity.findViewById(R.id.tv_percent);

		tv_percent.setText((10 + count_percent * 5) + "%");
		progressBar.setProgress((10 + count_percent * 5));
	}

	@Override
	protected void onPostExecute(Void void1) {
		super.onPostExecute(void1);
		// Delete all raspies which werenot chosen to configured
		Common.delete_raspies_not_config();

		// Save raspies info to file in android
		Common.write_raspies_2_file(activity, Common.raspies);

		// Start Control Activity
		Common.is_come_back_from_Control_activity = false;
		Common.start_new_activity(activity, Control_Activity.class, true);
	}

	// Get command to set static ip for raspi
	private String get_setip_command(String ipAddress) {
		String command = "sudo chmod 777 /etc/network/interfaces && sudo echo -e \"auto lo \niface lo inet loopback \niface eth0 inet static ";
		int last_point_index = ipAddress.lastIndexOf(".") + 1;
		command += "\naddress " + ipAddress;
		command += "\nnetwork " + ipAddress.substring(0, last_point_index)
				+ "0 ";
		command += "\ngateway " + ipAddress.substring(0, last_point_index)
				+ "1 ";
		command += "\nnetmask 255.255.255.0 ";
		command += "\nbroadcast " + ipAddress.substring(0, last_point_index)
				+ "255 ";
		command += "\">/etc/network/interfaces";
		return command;
	}

}
