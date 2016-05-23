package com.tommy.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.tommy.bean.BackList;
import com.tommy.bean.MessageBean;
import com.tommy.view.sms.NewSMSActivity;
import com.tommy.R;
import java.util.List;

public class MessageBoxListAdapter extends BaseAdapter {

	private List<MessageBean> mbList;
	private Context ctx;
	private LinearLayout layout_father;
	private LayoutInflater vi;
	private LinearLayout layout_child;
	private TextView tvDate;
	private TextView tvText;
	private DbUtils dbUtils;

	public MessageBoxListAdapter(Context context, List<MessageBean> coll) {
		dbUtils = DbUtils.create(context);
		ctx = context;
		vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mbList = coll;
	}

	public int getCount() {
		return mbList.size();
	}

	public Object getItem(int position) {
		return mbList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		MessageBean mb = mbList.get(position);
		int itemLayout = mb.getLayoutID();
		layout_father = new LinearLayout(ctx);
		vi.inflate(itemLayout, layout_father, true);

		layout_father.setBackgroundColor(Color.TRANSPARENT);
		layout_child = (LinearLayout) layout_father.findViewById(R.id.layout_bj);

		tvText = (TextView) layout_father.findViewById(R.id.messagedetail_row_text);
		tvText.setText(mb.getText());

		tvDate = (TextView) layout_father.findViewById(R.id.messagedetail_row_date);
		tvDate.setText(mb.getDate());

		addListener(tvText, tvDate, layout_child, mb);

		return layout_father;
	}

	public void addListener(final TextView tvText, final TextView tvDate, LinearLayout layout_bj, final MessageBean mb) {

		layout_bj.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

			}
		});

		layout_bj.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				tvText.setTextColor(0xffffffff);
				showListDialog(newtan, mb);
				return true;
			}
		});

		layout_bj.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

				case MotionEvent.ACTION_MOVE:
					tvText.setTextColor(0xffffffff);
					break;

				default:
					tvText.setTextColor(Color.BLACK);
					break;
				}
				return false;
			}
		});
	}

	private String[] newtan = new String[] { "复制文本内容", "删除", "转发" };

	private void showListDialog(final String[] arg, final MessageBean mb) {
		new AlertDialog.Builder(ctx).setTitle("信息选项").setItems(arg, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {

				case 0:
					ClipboardManager cmb = (ClipboardManager) ctx.getSystemService(ctx.CLIPBOARD_SERVICE);
					cmb.setText(mb.getText());
					break;
				case 1:

					// ctx.getContentResolver().delete(Uri.parse("content://sms"),
					// "_id=?", new String []{mb.get_id()+""});

					BackList backList = new BackList();
					backList.setLxid("0");
					backList.setSmsid(mb.get_id() + "");
					mbList.remove(mb);
					try {
						dbUtils.save(backList);
						MessageBoxListAdapter.this.notifyDataSetChanged();
						Toast.makeText(ctx, "删除短信成功", 2000).show();
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 2:
					Intent intent = new Intent();
					intent.setClass(ctx, NewSMSActivity.class);
					intent.putExtra("neirong", mb.getText());
					ctx.startActivity(intent);
					break;
				}
				;
			}
		}).show();
	}
}
