package com.tommy.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tommy.bean.MessageBean;
import com.tommy.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MsgSearchAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<MessageBean> list;
	private Context context;
	private Date d;
	private SimpleDateFormat sdf;

	public MsgSearchAdapter(Context context, List<MessageBean> list) {
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
		this.d = new Date();
		this.sdf = new SimpleDateFormat("MM/dd HH:mm");
	}

	public List<MessageBean> getList() {
		return list;
	}

	public void setList(List<MessageBean> list) {
		this.list = list;
	}

	public void assignment(List<MessageBean> list) {
		this.list = list;
	}

	public void add(MessageBean bean) {
		list.add(bean);
	}

	public void remove(int position) {
		list.remove(position);
	}

	public int getCount() {
		return list.size();
	}

	public MessageBean getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.search_home_sms_list_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.content = (TextView) convertView.findViewById(R.id.content);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(list.get(position).getName());

		holder.date.setText(this.sdf.format(d));

		holder.content.setText(list.get(position).getText());

		convertView.setTag(holder);
		return convertView;
	}

	public final class ViewHolder {
		public TextView name;
		public TextView count;
		public TextView date;
		public TextView content;
	}
}
