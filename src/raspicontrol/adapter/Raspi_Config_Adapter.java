package raspicontrol.adapter;

import java.util.ArrayList;

import raspicontrol.model.Raspi;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.detectclient.R;

public class Raspi_Config_Adapter extends ArrayAdapter<Raspi> {
	Activity activity;
	int resource;

	public Raspi_Config_Adapter(Activity activity, int resource,
			ArrayList<Raspi> objects) {
		super(activity, resource, objects);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.resource = resource;
	}

	public View getView(int pos, View newview, ViewGroup viewgroup) {
		newview = activity.getLayoutInflater().inflate(resource, null);
		if (pos >= 0) {
			// Get view
			ImageView im_raspi_icon = (ImageView) newview
					.findViewById(R.id.item_image);
			final CheckBox cb_raspi = (CheckBox) newview
					.findViewById(R.id.item_check);
			TextView tv_raspi_index = (TextView) newview
					.findViewById(R.id.tv_raspi_index);

			// Get item
			Raspi raspi = getItem(pos);

			// Set text for item
			tv_raspi_index.setText("Raspi " + raspi.getIndex());

			// Set suitable icon for raspi is configured or not.
			if (raspi.isIs_configured()) {
				im_raspi_icon
						.setImageResource(R.drawable.config_raspi_configured_icon);

				// Hide checkbox to configure raspi
				cb_raspi.setVisibility(View.GONE);
			} else {
				im_raspi_icon
						.setImageResource(R.drawable.config_raspi_not_configured_icon);

				// In default, raspi is chosen to be configured
				cb_raspi.setChecked(raspi.isIs_chosen_to_configure());

				// Prevent user change status by clicking to checkbox; They have
				// to click to Raspi item.
				cb_raspi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						cb_raspi.setChecked(!isChecked);
					}
				});
			}
		}
		return newview;
	}
}
