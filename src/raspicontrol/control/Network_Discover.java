package raspicontrol.control;

import java.io.IOException;
import java.net.InetAddress;

import raspicontrol.activity.Control_Activity;
import raspicontrol.adapter.Raspi_Config_Adapter;
import raspicontrol.model.Raspi;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.detectclient.R;
import com.gc.materialdesign.views.ButtonRectangle;

public class Network_Discover extends AsyncTask<Void, Raspi, Void> {

	private GridView gv_raspi;
	private int host_done = 0;
	private long ip_start = 0;
	private Activity activity;
	private Raspi_Config_Adapter raspi_config_Adapter;
	private ButtonRectangle btn_install, btn_next, btn_rescan;
	private Context context;
	private TextView tv_status;

	public Network_Discover(Activity activity, Context ctxt) {

		this.activity = activity;
		context = ctxt;

		init();
	}

	private void init() {
		// Get IP start
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String gatewayIP = "0.0.0.0";
		if (wifi != null)
			gatewayIP = getIpFromInt(wifi.getDhcpInfo().gateway);
		ip_start = getLongFromIp(gatewayIP);

		// Get view
		btn_install = (ButtonRectangle) activity.findViewById(R.id.btn_install);
		btn_next = (ButtonRectangle) activity.findViewById(R.id.btn_next);
		btn_rescan = (ButtonRectangle) activity.findViewById(R.id.btn_rescan);
		gv_raspi = (GridView) activity.findViewById(R.id.gv_raspi);
		tv_status = (TextView) activity.findViewById(R.id.tv_status);
		tv_status.setText(R.string.config_activity_status_scanning);

		// Hide all button
		set_enable_button(0);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		for (long i = ip_start; i < ip_start + 253; i++) {
			host_done++;
			final Raspi client = new Raspi(getIpFromLong(i));
			try {
				// Get inetAddress
				InetAddress inet = null;
				inet = InetAddress.getByName(client.getIpAddress());

				// Check if inet is reachable and is a raspi
				if (inet.isReachable(200) && check_Raspi_SSH_connection(client)) {

					// Check whether raspi was configured => set value
					boolean is_raspi_configured = client.check_raspi_config();
					client.setIs_configured(is_raspi_configured);

					// If configured, we don't need to configure
					if (is_raspi_configured)
						client.setIs_chosen_to_configure(false);

					// Add client to raspies array
					Common.raspies.getRaspies().add(client);
					publishProgress(client);
				} else
					publishProgress((Raspi) null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				publishProgress((Raspi) null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Raspi... clients) {
		super.onProgressUpdate(clients);

		// Update status in progress bar and textview percent
		ProgressBar progress = (ProgressBar) activity
				.findViewById(R.id.pb_config);
		progress.setProgress((int) (host_done * 100 / 253));
		TextView per = (TextView) activity.findViewById(R.id.tv_percent);
		per.setText(String.valueOf((int) (host_done * 100 / 253)) + "%");

		// If client is a raspi => update adapter
		if (clients[0] != null) {
			raspi_config_Adapter = new Raspi_Config_Adapter(this.activity,
					R.layout.item_raspi_config, Common.raspies.getRaspies());
			gv_raspi.setAdapter(raspi_config_Adapter);
		}
	}

	@Override
	protected void onPostExecute(Void void1) {
		super.onPostExecute(void1);

		// When complete => update textview status
		tv_status.setText(R.string.config_activity_status_scan_complete);

		// Set onClicklistener for 3 button
		btn_rescan.setOnClickListener(ocl);
		btn_next.setOnClickListener(ocl);
		btn_install.setOnClickListener(ocl);

		// Set onItemClickListener for GridView
		gv_raspi.setOnItemClickListener(oicl);

		// if no rapi found => enable rescan button
		if (Common.raspies.getRaspies().size() == 0)
			set_enable_button(3);
		else
		// if all raspies which be found were configured
		if (raspi_configured_count() == Common.raspies.getRaspies().size()) {
			Common.write_raspies_2_file(activity, Common.raspies);
			Common.is_come_back_from_Control_activity = false;
			Common.start_new_activity(activity, Control_Activity.class, true);
		} else
			// Some rapies werenot configured
			set_enable_button(1);

	}

	// onClickListener for 3 button
	OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_install:
				set_enable_button(0);
				gv_raspi.setEnabled(false);
				new Copy_File_2_Raspies(activity).execute();
				break;
			case R.id.btn_next:
				Common.delete_raspies_not_config();
				Common.write_raspies_2_file(activity, Common.raspies);
				Common.is_come_back_from_Control_activity = false;
				Common.start_new_activity(activity, Control_Activity.class,
						true);
				break;
			case R.id.btn_rescan:
				set_enable_button(0);
				new Network_Discover(activity, context).execute();
				break;
			}
		}
	};

	// Gridview onItemClickListener
	OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			// Only process when user click on the Raspi which wasn't configured
			if (!Common.raspies.getRaspies().get(position).isIs_configured()) {
				Common.raspies
						.getRaspies()
						.get(position)
						.setIs_chosen_to_configure(
								!Common.raspies.getRaspies().get(position)
										.isIs_chosen_to_configure());
				raspi_config_Adapter.notifyDataSetChanged();

				// choose at least 1 raspi to configure => enable install button
				if (raspi_chosen_to_configure_count() > 0)
					set_enable_button(1);
				else // no raspi chosen to configure
						// if all raspi not configured => unable all button
				if (raspi_configured_count() == 0)
					set_enable_button(0);
				else
					// all raspies were configured => enable button next
					set_enable_button(2);
			}
		}
	};

	// count raspi was configured
	private int raspi_configured_count() {
		int raspi_configured_count = 0;
		for (Raspi raspi : Common.raspies.getRaspies())
			if (raspi.isIs_configured())
				raspi_configured_count++;
		return raspi_configured_count;
	}

	// count raspi was chosen to configure
	private int raspi_chosen_to_configure_count() {
		int raspi_chosen_to_configure_count = 0;
		for (Raspi raspi : Common.raspies.getRaspies())
			if (raspi.isIs_chosen_to_configure())
				raspi_chosen_to_configure_count++;
		return raspi_chosen_to_configure_count;
	}

	// Enable btn_install, btn_next, btn_rescan: 0: unenable all; 1: enable
	// btn_install; 2: enable btn_next; 3: enable btn_rescan
	private void set_enable_button(int id) {
		btn_install.setEnabled(false);
		btn_next.setEnabled(false);
		btn_rescan.setEnabled(false);
		switch (id) {
		case 1:
			btn_install.setEnabled(true);
			break;
		case 2:
			btn_next.setEnabled(true);
			break;
		case 3:
			btn_rescan.setEnabled(true);
			break;
		default:
			break;
		}
	}

	// check_Raspi_SSH_connection
	private boolean check_Raspi_SSH_connection(Raspi raspi) {
		return raspi.ConnectSSH();
	}

	// CONVERT IP FORMAT
	private String getIpFromLong(long ip_long) {
		String ip = "";
		for (int k = 3; k > -1; k--) {
			ip = ip + ((ip_long >> k * 8) & 0xFF) + ".";
		}
		return ip.substring(0, ip.length() - 1);
	}

	private String getIpFromInt(int ip_int) {
		String ip = "";
		for (int k = 0; k < 4; k++) {
			ip = ip + ((ip_int >> k * 8) & 0xFF) + ".";
		}
		return ip.substring(0, ip.length() - 1);
	}

	private long getLongFromIp(String ip_addr) {
		String[] a = ip_addr.split("\\.");
		return (Integer.parseInt(a[0]) * 16777216 + Integer.parseInt(a[1])
				* 65536 + Integer.parseInt(a[2]) * 256 + Integer.parseInt(a[3]));
	}
}
