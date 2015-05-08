package raspicontrol.activity;

import raspicontrol.adapter.Camera_Adapter;
import raspicontrol.adapter.Raspi_Control_Adapter;
import raspicontrol.control.Common;
import raspicontrol.model.Raspi;
import raspicontrol.model.Raspies;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.example.detectclient.R;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.sileria.android.view.HorzListView;

public class Control_Activity extends Activity {

	public static int camera_choose;
	private HorzListView hlv_raspies;
	private GridView gv_cameras;
	private Raspi_Control_Adapter raspi_control_Adapter;
	private Camera_Adapter camera_Adapter;
	private ButtonFloat btn_menu;
	public static String stop_viewstream_command = "pkill omxplayer.bin";
	private Raspies raspies_temp;
	private boolean is_connect_complete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide action bar
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.control_activity);

		// Backup raspies array for not connect to all raspies
		raspies_temp = new Raspies();
		raspies_temp.getRaspies().addAll(Common.raspies.getRaspies());

		// Init activity
		new Init_Control_Activity().execute();

	}

	private class Init_Control_Activity extends AsyncTask<Void, Void, Void> {
		private ProgressBarCircularIndeterminate pb_waiting;

		protected void onPreExecute() {
			// NOTE: You can call UI Element here.
			pb_waiting = (ProgressBarCircularIndeterminate) findViewById(R.id.pb_waiting);
			pb_waiting.setVisibility(View.VISIBLE);
			is_connect_complete = false;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			connect_2_raspies();
			return null;
		}

		protected void onPostExecute(Void unused) {
			// NOTE: You can call UI Element here.
			// Hide progressbar
			pb_waiting.setVisibility(View.GONE);

			// if can't connect to all raspi -> show dialog
			if (Common.raspies.getRaspies().size() == 0)
				show_error_dialog();
			else
				is_connect_complete = true;
			init();
		}

	}

	// Method to show error_dialog when we cannot connect to any raspi
	private void show_error_dialog() {
		// Init dialog
		AlertDialog.Builder error_dialog = new AlertDialog.Builder(
				Control_Activity.this);
		error_dialog.setTitle(R.string.control_activity_dialog_title);
		error_dialog.setMessage(R.string.control_activity_dialog_content);

		// Set reinstall, reconnect and exit button to dialog
		error_dialog.setPositiveButton(R.string.menu_exit,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				});
		error_dialog.setNegativeButton(
				R.string.control_activity_dialog_reconnect,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// Restore raspies data from backup variable
						// raspies_temp
						Common.raspies = new Raspies();
						Common.raspies.getRaspies().addAll(
								raspies_temp.getRaspies());
						new Init_Control_Activity().execute();
					}
				});
		error_dialog.setNeutralButton(
				R.string.control_activity_dialog_reinstall,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// Start Config Activity
						Common.is_come_back_from_Control_activity = true;
						Common.start_new_activity(Control_Activity.this,
								Config_Activity.class, true);
					}
				});

		// Prevent user close dialog by clicking outside dialog
		error_dialog.setCancelable(false);

		// Show dialog
		error_dialog.create().show();
	}

	@SuppressLint("NewApi")
	private void connect_2_raspies() {

		// Connect to raspi
		for (int i = 0; i < Common.raspies.getRaspies().size(); i++) {
			// If cannot connect to a raspi => remove it from raspies array
			if (!Common.raspies.getRaspies().get(i).ConnectSSH()) {
				Common.raspies.getRaspies().remove(i);
				i--;
			}
		}
		// Correct the index after remove
		for (int i = 0; i < Common.raspies.getRaspies().size(); i++)
			Common.raspies.getRaspies().get(i).setIndex(i + 1);
	}

	private void init() {
		// Get view
		btn_menu = (ButtonFloat) findViewById(R.id.btn_menu);
		hlv_raspies = (HorzListView) findViewById(R.id.control_activity_hlv_raspies);
		gv_cameras = (GridView) findViewById(R.id.control_activity_gv_cameras);

		// Set default value
		camera_choose = 0;

		// Add all_raspi_item to raspies array
		Common.raspies.getRaspies().add(0,
				new Raspi("", 0, 0, true, false, null));

		// start stream in all raspies
		start_stream();

		// Set onclicklistener for menu button
		btn_menu.setOnClickListener(ocl);

		// init and set adapter
		raspi_control_Adapter = new Raspi_Control_Adapter(this,
				R.layout.item_raspi_control, Common.raspies.getRaspies());
		hlv_raspies.setAdapter(raspi_control_Adapter);
		camera_Adapter = new Camera_Adapter(this, R.layout.item_camera,
				Common.raspies.getRaspies());
		gv_cameras.setAdapter(camera_Adapter);

		// Process Drag and drop item to view stream
		gv_cameras.setOnItemLongClickListener(oilcl_gv_cameras);

		// Show popup menu to stop view stream
		hlv_raspies.setOnItemLongClickListener(oilcl_hlv_raspies);
	}

	// Send command to start stream in all raspies
	private void start_stream() {
		for (int i = 1; i < Common.raspies.getRaspies().size(); i++)
			Common.excute_command(i, Common.get_stream_command());
	}

	// onClickListener for Menu button
	OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Prevent user click menu when Connecting to Raspies Progress
			// hasnot yet been complete.
			if (is_connect_complete) {
				PopupMenu popup = new PopupMenu(Control_Activity.this, v);
				// Inflating the Popup using xml file
				popup.getMenuInflater().inflate(R.menu.control_activity,
						popup.getMenu());

				// registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						int id = item.getItemId();
						switch (id) {
						case R.id.menu_setup:
							Common.is_come_back_from_Control_activity = true;
							Common.start_new_activity(Control_Activity.this,
									Config_Activity.class, true);
							break;
						case R.id.menu_setting:
							Common.start_new_activity(Control_Activity.this,
									Setting_Dialog_Activity.class, false);
							break;
						case R.id.menu_about:
							Common.start_new_activity(Control_Activity.this,
									About_Dialog_Activity.class, false);
							break;
						case R.id.menu_exit:
							finish();
							break;
						}

						return false;
					}
				});
				popup.show();// showing popup menu
			}
		}
	};

	// OnItemLongClickListener for GridView Camera
	OnItemLongClickListener oilcl_gv_cameras = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			// Drag camera
			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
			view.startDrag(null, shadowBuilder, view, 0);
			view.setVisibility(View.VISIBLE);
			camera_choose = position + 1;
			return true;
		}
	};

	// OnItemLongClickListener for Horizontal Raspi Listview
	OnItemLongClickListener oilcl_hlv_raspies = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			// Only show popupmenu when that raspi was showing stream
			if (Common.raspies.getRaspies().get(position)
					.getStreaming_camera_index() != 0) {
				final int pos = position;
				PopupMenu popup = new PopupMenu(Control_Activity.this, view);
				// Inflating the Popup using xml file
				popup.getMenuInflater().inflate(
						R.menu.control_activity_popup_menu, popup.getMenu());

				// registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {

						if (item.getItemId() == R.id.control_activity_popup_menu_turnoff_viewstream) {
							// Update value to raspies array
							Common.raspies.getRaspies().get(pos)
									.setStreaming_camera_index(0);
							Common.raspies.getRaspies().get(0)
									.setStreaming_camera_index(0);

							// Send command to raspies
							if (pos == 0)
								// Turn off view stream in all raspies
								for (int i = 1; i < Common.raspies.getRaspies()
										.size(); i++) {
									Common.raspies.getRaspies().get(i)
											.setStreaming_camera_index(0);
									Common.excute_command(i,
											stop_viewstream_command);
								}
							else
								// Turn off view stream in one raspi
								Common.excute_command(pos,
										stop_viewstream_command);

							// Update adapter view
							raspi_control_Adapter.notifyDataSetChanged();
						}
						return true;
					}
				});

				popup.show();// showing popup menu
			}
			return false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.control_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}
}
