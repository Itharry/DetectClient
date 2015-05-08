package raspicontrol.control;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import raspicontrol.model.Raspies;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.google.gson.Gson;

public class Common {
	public static Raspies raspies;
	public static boolean is_come_back_from_Control_activity = false;

	// Read content of file in raspi
	public static String readfile(Connection connection, String file) {
		try {
			Session session = connection.openSession();
			session.execCommand("cat " + file);
			Thread.sleep(200);
			byte[] buffer = new byte[session.getStdout().available()];
			session.getStdout().read(buffer);
			session.close();
			return new String(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// Start new Activity
	public static void start_new_activity(Activity activity, Class<?> mclass,
			boolean is_finish_parent) {
		Intent myintent = new Intent(activity, mclass);
		activity.startActivity(myintent);
		if (is_finish_parent)
			activity.finish();
	}

	// Send command to raspi to excute
	public static void excute_command(int index, String command) {
		try {
			Session session = Common.raspies.getRaspies().get(index)
					.getConnection().openSession();
			session.execCommand(command);
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Get stream command
	public static String get_stream_command() {
		String stream_command = "cd ~ && cd mjpg-streamer-experimental/ && export LD_LIBRARY_PATH=. && ./mjpg_streamer -o \"output_http.so -w ./www -c ";
		stream_command += (Common.raspies.getUsername() + ":" + Common.raspies
				.getPassword());
		stream_command += "\" -i \"input_raspicam.so "
				+ Common.raspies.getResolution() + " -ex night\"";
		return stream_command;
	}

	// Get view stream command
	public static String get_viewstream_command(String ipAddress) {
		String viewstream_command = "omxplayer --live -b 'http://";
		viewstream_command += (Common.raspies.getUsername() + ":" + Common.raspies
				.getPassword());
		viewstream_command += "@" + ipAddress + ":8080/?action=stream'";
		return viewstream_command;
	}

	// Write raspies info to file in android
	public static void write_raspies_2_file(Activity activity, Raspies raspies) {
		Gson gson = new Gson();
		try {
			FileOutputStream out = activity.openFileOutput("data.dat", 0);
			OutputStreamWriter writer = new OutputStreamWriter(out);
			Log.d("write_json", gson.toJson(raspies));
			writer.write(gson.toJson(raspies));
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Delete raspi wasnot chosen to configure
	public static void delete_raspies_not_config() {
		// Delete raspi not configured
		for (int i = 0; i < Common.raspies.getRaspies().size(); i++)
			if ((!Common.raspies.getRaspies().get(i).isIs_configured())
					&& (!Common.raspies.getRaspies().get(i)
							.isIs_chosen_to_configure())) {
				Common.raspies.getRaspies().remove(i);
				i--;
			}

		// Correct the index
		for (int i = 0; i < Common.raspies.getRaspies().size(); i++)
			Common.raspies.getRaspies().get(i).setIndex(i + 1);
	}
}
