package raspicontrol.activity;

import raspicontrol.control.Common;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.detectclient.R;
import com.gc.materialdesign.views.ButtonRectangle;

public class Setting_Dialog_Activity extends Activity {

	EditText et_username, et_password;
	RatingBar rb_quality;
	ButtonRectangle btn_ok, btn_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_dialog);
		init();
	}

	private void init() {
		// Get view
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		rb_quality = (RatingBar) findViewById(R.id.rb_quality);
		btn_ok = (ButtonRectangle) findViewById(R.id.btn_ok);
		btn_cancel = (ButtonRectangle) findViewById(R.id.btn_cancel);

		// Set current setting value
		et_username.setText(Common.raspies.getUsername());
		et_password.setText(Common.raspies.getPassword());
		rb_quality.setRating((float) string_2_quality(Common.raspies
				.getResolution()));

		// Set onClickListener for 2 button
		btn_cancel.setOnClickListener(ocl);
		btn_ok.setOnClickListener(ocl);
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

	// OnClickListener for 2 button
	OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Click cancel
			if (v.getId() == R.id.btn_cancel)
				finish();

			// Click OK
			if (v.getId() == R.id.btn_ok) {
				// Set data to variable
				Common.raspies.setUsername(et_username.getText().toString());
				Common.raspies.setPassword(et_password.getText().toString());
				Common.raspies.setResolution(quality_2_string(Math
						.round(rb_quality.getRating())));

				// Write data to file
				Common.write_raspies_2_file(Setting_Dialog_Activity.this,
						Common.raspies);

				// Restart all stream
				for (int i = 1; i < Common.raspies.getRaspies().size(); i++) {
					Common.excute_command(i, "pkill mjpg_streamer");
					Common.excute_command(i, Common.get_stream_command());
				}

				// Restart all viewstream
				for (int i = 1; i < Common.raspies.getRaspies().size(); i++) {
					Common.excute_command(i, "pkill omxplayer.bin");
					int streaming_camera_index = Common.raspies.getRaspies()
							.get(i).getStreaming_camera_index();
					Common.excute_command(
							i,
							Common.get_viewstream_command(Common.raspies
									.getRaspies().get(streaming_camera_index)
									.getIpAddress()));
				}

				finish();
			}

		}
	};

	// Convert quality (user choise) to command string
	private String quality_2_string(int quality) {
		switch (quality) {
		case 1:
			return "-x 180 -y 90 -fps 15";
		case 2:
			return "-x 360 -y 180 -fps 15";
		case 3:
			return "-x 720 -y 360 -fps 15";
		default:
			return "-x 1280 -y 720 -fps 15";
		}
	}

	// Convert command string to quality
	private int string_2_quality(String string) {
		if (string.equals("-x 180 -y 90 -fps 15"))
			return 1;
		if (string.equals("-x 360 -y 180 -fps 15"))
			return 2;
		if (string.equals("-x 720 -y 360 -fps 15"))
			return 3;
		return 4;
	}
}
