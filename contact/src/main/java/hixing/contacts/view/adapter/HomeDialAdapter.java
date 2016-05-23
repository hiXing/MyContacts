package hixing.contacts.view.adapter;

import java.util.List;

import hixing.contacts.R;
import hixing.contacts.bean.CallLogBean;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeDialAdapter extends BaseAdapter {

	private Context ctx;
	private List<CallLogBean> list;
	private LayoutInflater inflater;

	public HomeDialAdapter(Context context, List<CallLogBean> list) {

		this.ctx = context;
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.home_dial_calllog_list_item, null);
			holder = new ViewHolder();
			holder.call_type = (ImageView) convertView.findViewById(R.id.call_type);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.call_btn = (RelativeLayout) convertView.findViewById(R.id.call_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CallLogBean clb = list.get(position);
		switch (clb.getType()) {
			case 1:
				holder.call_type.setBackgroundResource(R.drawable.ic_calllog_outgoing_nomal);
				break;
			case 2:
				holder.call_type.setBackgroundResource(R.drawable.ic_calllog_incomming_normal);
				break;
			case 3:
				holder.call_type.setBackgroundResource(R.drawable.ic_calllog_missed_normal);
				break;
		}
		holder.name.setText(clb.getName());
		holder.number.setText(clb.getNumber());
		holder.time.setText(clb.getDate());

//		addViewListener(holder.call_btn, clb, position);

		return convertView;
	}

	private static class ViewHolder {
		ImageView call_type;
		TextView name;
		TextView number;
		TextView time;
		RelativeLayout call_btn;
	}

	private void addViewListener(View view, final CallLogBean clb, final int position) {
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Uri uri = Uri.parse("tel:" + clb.getNumber());
				Intent it = new Intent(Intent.ACTION_CALL, uri);
				if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				ctx.startActivity(it);
			}
		});
	}

}
