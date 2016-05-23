package com.tommy.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.tommy.application.MyApplication;
import com.tommy.bean.BackList;
import com.tommy.bean.SMSBean;
import com.tommy.util.BaseIntentUtil;
import com.tommy.util.RexseeSMS;
import com.tommy.view.adapter.HomeSMSAdapter;
import com.tommy.view.sms.MessageBoxList;
import com.tommy.view.sms.NewSMSActivity;
import com.tommy.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class HomeSMSActivity extends Activity implements OnCreateContextMenuListener {

	private ListView listView;
	private HomeSMSAdapter adapter;
	private RexseeSMS rsms;
	private Button newSms;
	private DbUtils dbUtils;
	private List<BackList> backLists = new ArrayList<BackList>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	public void init() {
		setContentView(R.layout.home_sms_page);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		dbUtils = DbUtils.create(this);
		listView = (ListView) findViewById(R.id.list);
		adapter = new HomeSMSAdapter(HomeSMSActivity.this);
		try {
			backLists = dbUtils.findAll(BackList.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		rsms = new RexseeSMS(HomeSMSActivity.this);
		List<SMSBean> list_mmt = rsms.getThreadsNum(rsms.getThreads(0));
		List<SMSBean> smsBeans = new ArrayList<SMSBean>();
		if (backLists != null && backLists.size() > 0) {
			for (int i = 0; i < list_mmt.size(); i++) {
				int temp = 0;
				for (int j = 0; j < backLists.size(); j++) {
					if (backLists.get(j).getSmsid().equals(list_mmt.get(i).getThread_id())) {
						temp = 1;
						break;
					}
				}
				if (temp == 0) {
					smsBeans.add(list_mmt.get(i));
				}
			}
		} else {
			smsBeans = list_mmt;
		}

		adapter.assignment(smsBeans);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, String> map = new HashMap<String, String>();
				SMSBean sb = adapter.getItem(position);
				map.put("phoneNumber", sb.getAddress());
				map.put("threadId", sb.getThread_id());
				BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, MessageBoxList.class, map);
			}
		});

		listView.setOnCreateContextMenuListener(this);

		newSms = (Button) findViewById(R.id.newSms);
		newSms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, NewSMSActivity.class, null);
			}
		});

	}

	/**
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("操作");
		// 添加菜单项
		menu.add(0, 0, 0, "删除");

	}

	/**
	 * 上下文菜单的事件处理
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			// 在这里怎样该获取要删除的ListView的选中项的内容呢？
			SMSBean sms = (SMSBean) adapter.getItem(item.getItemId());

			adapter.getList().remove(sms);
			adapter.notifyDataSetChanged();
			BackList backList = new BackList();
			backList.setLxid("0");
			backList.setSmsid(sms.getThread_id());
			try {
				dbUtils.save(backList);
				Toast.makeText(HomeSMSActivity.this, "删除短信成功", 2000).show();
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			((MyApplication) getApplication()).promptExit(this);
			return true;
		}
		return false;
	}

}
