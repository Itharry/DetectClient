package raspicontrol.activity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import raspicontrol.control.Common;
import raspicontrol.control.Network_Discover;
import raspicontrol.model.Raspies;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.detectclient.R;
import com.google.gson.Gson;

public class Config_Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide action bar
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.config_activity);

		init();
	}

	private void init() {
		// Set StrictMode
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitNetwork().build();
		StrictMode.setThreadPolicy(policy);

		if (is_need_to_run_Config_activity()) {
			Common.raspies = new Raspies();

			// Scan network
			Network_Discover discover = new Network_Discover(this,
					getApplicationContext());
			discover.execute();
		}

	}

	// Check whether application have raspies system data
	private boolean is_need_to_run_Config_activity() {
		// Read data from file
		Common.raspies = read_file_2_raspies();

		if ((!Common.is_come_back_from_Control_activity)
				&& (Common.raspies != null)) {
			Common.start_new_activity(this, Control_Activity.class, true);
			return false;
		}
		return true;
	}

	// Get Raspies from file
	private Raspies read_file_2_raspies() {
		String file_data = "";
		try {
			FileInputStream in = openFileInput("data.dat");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String data = "";
			StringBuilder builder = new StringBuilder();
			while ((data = reader.readLine()) != null) {
				builder.append(data);
				builder.append("\n");
			}
			in.close();
			file_data = builder.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson = new Gson();
		return gson.fromJson(file_data, Raspies.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}
}
