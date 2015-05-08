package raspicontrol.adapter;

import java.util.ArrayList;

import raspicontrol.model.Raspi;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.detectclient.R;

public class Camera_Adapter extends ArrayAdapter<Raspi> {
	Activity activity;
	int resource;

	public Camera_Adapter(Activity activity, int resource,
			ArrayList<Raspi> raspies) {
		super(activity, resource, raspies);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.resource = resource;
	}

	public View getView(int pos, View newview, ViewGroup viewgroup) {
		newview = activity.getLayoutInflater().inflate(resource, null);

		// Raspies.get(0) is fake object (all raspi item). So at position x, we
		// show item (x+1)
		if (pos < getCount() - 1) {
			Raspi raspi = getItem(pos + 1);
			TextView tvCamera = (TextView) newview
					.findViewById(R.id.item_camera_tv_index);
			tvCamera.setText("Camera " + raspi.getIndex());
		} else {
			LinearLayout item_camera = (LinearLayout) newview
					.findViewById(R.id.item_camera);
			item_camera.setVisibility(View.GONE);
		}
		return newview;
	}
}
