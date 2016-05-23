package com.tommy.view.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.pim.vcard.AppeSession;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.tommy.application.MyApplication;
import com.tommy.bean.ContactBean;
import com.tommy.view.ui.QuickAlphabeticBar;
import com.tommy.R;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ContactHomeAdapter02 extends BaseAdapter {

	private LayoutInflater inflater;
	private List<ContactBean> list;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Context ctx;
	private AppeSession appSession;
	private List<String> numberList;

	public ContactHomeAdapter02(Context context, List<ContactBean> list, List<String> numberList, QuickAlphabeticBar alpha) {

		this.ctx = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.alphaIndexer = new HashMap<String, Integer>();
		this.sections = new String[list.size()];
		this.appSession = ((MyApplication) context.getApplicationContext()).getAppSession();
		this.numberList = numberList;

		for (int i = 0; i < list.size(); i++) {
			String name = getAlpha(list.get(i).getSortKey());
			if (!alphaIndexer.containsKey(name)) {
				alphaIndexer.put(name, i);
			}
		}

		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);

		alpha.setAlphaIndexer(alphaIndexer);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
		list.remove(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contact_home_list_item, null);
			holder = new ViewHolder();
			holder.qcb = (QuickContactBadge) convertView.findViewById(R.id.qcb);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.cb = (CheckBox) convertView.findViewById(R.id.cb);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ContactBean cb = list.get(position);
		String name = cb.getDisplayName();
		final String number = cb.getPhoneNum();
		holder.name.setText(name);
		holder.number.setText(number);
		holder.cb.setVisibility(View.VISIBLE);
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					numberList.add(number);
				} else {
					numberList.remove(number);
				}

			}
		});
		holder.qcb.assignContactUri(Contacts.getLookupUri(cb.getContactId(), cb.getLookUpKey()));

		if (0 == cb.getPhotoId()) {
			holder.qcb.setImageResource(R.drawable.touxiang);
		} else {
			Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, cb.getContactId());
			InputStream input = Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri);
			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
			holder.qcb.setImageBitmap(contactPhoto);
		}

		String currentStr = getAlpha(cb.getSortKey());

		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getSortKey()) : " ";

		if (!previewStr.equals(currentStr)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}

	public List<String> getZidingyi() {
		return numberList;
	}

	public void setZidingyi(List<String> zidingyi) {
		this.numberList = zidingyi;
	}

	private static class ViewHolder {
		QuickContactBadge qcb;
		TextView alpha;
		TextView name;
		TextView number;
		CheckBox cb;
	}

	/**
	 * ��ȡӢ�ĵ�����ĸ����Ӣ����ĸ��#���档
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // ��д���
		} else {
			return "#";
		}
	}
}
