package raspicontrol.adapter;

import java.util.ArrayList;

import raspicontrol.activity.Control_Activity;
import raspicontrol.control.Common;
import raspicontrol.model.Raspi;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.detectclient.R;

public class Raspi_Control_Adapter extends ArrayAdapter<Raspi> {
	Activity context;
	int resource;

	public Raspi_Control_Adapter(Activity context, int resource,
			ArrayList<Raspi> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resource = resource;
	}

	@SuppressLint("NewApi")
	public View getView(int pos, View newview, ViewGroup viewgroup) {
		newview = context.getLayoutInflater().inflate(resource, null);

		if (pos >= 0) {
			// Get item
			Raspi raspi = getItem(pos);

			// Get view
			TextView tvAboveBG = (TextView) newview
					.findViewById(R.id.item_raspi_control_tv_above_bg);
			TextView tvUnderBG = (TextView) newview
					.findViewById(R.id.item_raspi_control_tv_under_bg);
			TextView tvBorder = (TextView) newview
					.findViewById(R.id.item_raspi_control_tv_border);
			TextView tvMonitor = (TextView) newview
					.findViewById(R.id.item_raspi_control_tv_monitor);
			TextView tvCamera = (TextView) newview
					.findViewById(R.id.item_raspi_control_tv_camera);
			LinearLayout lnCamera = (LinearLayout) newview
					.findViewById(R.id.item_raspi_control_ln_camera);
			ImageView imgMonitor = (ImageView) newview
					.findViewById(R.id.item_raspi_control_img_monitor);

			// Set icon and background suitable for raspi not be showing stream
			// in monitor
			if (raspi.getStreaming_camera_index() == 0) {
				tvAboveBG.setBackground(context.getResources().getDrawable(
						R.drawable.above_bg_off));
				tvUnderBG.setBackground(context.getResources().getDrawable(
						R.drawable.under_bg_off));
				imgMonitor.setImageResource(R.drawable.icon_raspi_off);
				lnCamera.setVisibility(View.INVISIBLE);

				// Change icon for the first item (all raspi item)
				if (pos == 0) {
					imgMonitor.setImageResource(R.drawable.icon_all_raspi_off);
					tvMonitor.setVisibility(View.INVISIBLE);
				}
			} else { // Set icon and background suitable for raspi is showing
						// stream in monitor
				tvAboveBG.setBackground(context.getResources().getDrawable(
						R.drawable.above_bg_on));
				tvUnderBG.setBackground(context.getResources().getDrawable(
						R.drawable.under_bg_on));
				imgMonitor.setImageResource(R.drawable.icon_raspi_on);
				lnCamera.setVisibility(View.VISIBLE);

				// Change icon for the first item (all raspi item)
				if (pos == 0) {
					imgMonitor.setImageResource(R.drawable.icon_all_raspi_on);
					tvMonitor.setVisibility(View.INVISIBLE);
				}
			}

			// Set index for raspi
			tvMonitor.setText(String.valueOf(raspi.getIndex()));

			// When all raspies are showing stream which not be from the same
			// camera, we show *
			if (raspi.getStreaming_camera_index() == -1)
				tvCamera.setText("*");
			else
				tvCamera.setText(String.valueOf(raspi
						.getStreaming_camera_index()));
			tvBorder.setVisibility(View.INVISIBLE);
			newview.setOnDragListener(new MyDragListener());
		}

		return newview;
	}

	// Process when user drap and drop camera icon
	class MyDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {

			// Handles each of the expected events
			switch (event.getAction()) {

			// signal for the start of a drag and drop operation.
			case DragEvent.ACTION_DRAG_STARTED:
				// do nothing
				break;

			// the drag point has entered the bounding box of the View
			case DragEvent.ACTION_DRAG_ENTERED:
				// change the shape of the view
				v.findViewById(R.id.item_raspi_control_tv_border)
						.setVisibility(View.VISIBLE);
				break;

			// the user has moved the drag shadow outside the bounding box of
			// the View
			case DragEvent.ACTION_DRAG_EXITED:
				v.findViewById(R.id.item_raspi_control_tv_border)
						.setVisibility(View.INVISIBLE);
				break;

			// drag shadow has been released,the drag point is within the
			// bounding box of the View
			case DragEvent.ACTION_DROP: {
				// Get the raspi item to show stream using TextView
				TextView tv = (TextView) v
						.findViewById(R.id.item_raspi_control_tv_monitor);

				// Change status view stream for the raspi
				Common.raspies
						.getRaspies()
						.get(Integer.parseInt((String) tv.getText()))
						.setStreaming_camera_index(
								Control_Activity.camera_choose);
				String view_stream_command = Common
						.get_viewstream_command(Common.raspies.getRaspies()
								.get(Control_Activity.camera_choose)
								.getIpAddress());

				// excute view stream command
				try {
					// if the raspi which was chosen was the first item
					// (all_raspi_item) => change status for all raspies step by
					// step
					if (tv.getText().equals("0"))
						for (int i = 1; i < Common.raspies.getRaspies().size(); i++) {
							Common.raspies
									.getRaspies()
									.get(i)
									.setStreaming_camera_index(
											Control_Activity.camera_choose);
							// Stop current showing stream
							Common.excute_command(i,
									Control_Activity.stop_viewstream_command);
							// Start to show new stream
							Common.excute_command(i, view_stream_command);
						}
					else { // Raspi which was chosen wasn't the first
							// Check and set new showing stream status for the
							// first item (all_raspi_item)
						Common.raspies
								.getRaspies()
								.get(0)
								.setStreaming_camera_index(
										get_status_4_all_raspi_item());
						// Stop current showing stream
						Common.excute_command(
								Integer.parseInt((String) tv.getText()),
								Control_Activity.stop_viewstream_command);
						Common.excute_command(
								Integer.parseInt((String) tv.getText()),
								view_stream_command);
					}
					notifyDataSetChanged();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				break;

			// the drag and drop operation has concluded.
			case DragEvent.ACTION_DRAG_ENDED:
				// go back to normal shape
				v.findViewById(R.id.item_raspi_control_tv_border)
						.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
			return true;
		}

		/*
		 * Get status for all_raspi_item: - not all raspi are showing stream =>
		 * 0 - all raspi are showing the same camera => index of camera - all
		 * raspi are showing but not the same cammera => -1
		 */
		private int get_status_4_all_raspi_item() {
			boolean isSame = true;
			for (int i = 2; i < Common.raspies.getRaspies().size(); i++) {
				if (Common.raspies.getRaspies().get(i)
						.getStreaming_camera_index() == 0
						|| Common.raspies.getRaspies().get(i - 1)
								.getStreaming_camera_index() == 0)
					return 0;
				if (Common.raspies.getRaspies().get(i)
						.getStreaming_camera_index() != Common.raspies
						.getRaspies().get(i - 1).getStreaming_camera_index())
					isSame = false;
			}
			if (isSame)
				return Common.raspies.getRaspies().get(1)
						.getStreaming_camera_index();
			return -1;
		}
	}
}
