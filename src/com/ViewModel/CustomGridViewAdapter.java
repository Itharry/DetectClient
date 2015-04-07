package com.ViewModel;

import java.util.ArrayList;

import com.example.detectclient.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

public class CustomGridViewAdapter extends ArrayAdapter<Item> {
	Context context;
	int layoutResourceId, c;
	ArrayList<Item> data = new ArrayList<Item>();

	public CustomGridViewAdapter(Context context, int layoutResourceId,
			ArrayList<Item> data, int c) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.c = c;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();
			holder.checkTitle = (CheckBox) row.findViewById(R.id.item_check);
			holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
			Typeface font = Typeface.createFromAsset(context.getAssets(), "Helveticaneuelight.ttf");
			holder.checkTitle.setTypeface(font);
			if (c == 1) {
				holder.checkTitle.setChecked(true);
				holder.checkTitle.setEnabled(false);
			}
			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}

		Item item = data.get(position);
		holder.checkTitle.setText(item.getTitle());
		holder.imageItem.setImageBitmap(item.getImage());
		return row;

	}

	static class RecordHolder {
		CheckBox checkTitle;
		ImageView imageItem;

	}
}