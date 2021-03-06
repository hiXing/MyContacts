package hixing.contacts.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Groups;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import hixing.contacts.R;
import hixing.contacts.application.MyApplication;
import hixing.contacts.bean.AppeSession;
import hixing.contacts.bean.BackList;
import hixing.contacts.bean.ContactBean;
import hixing.contacts.bean.GroupBean;
import hixing.contacts.uitl.AbToastUtil;
import hixing.contacts.uitl.BaseIntentUtil;
import hixing.contacts.uitl.HttpUtil;
import hixing.contacts.uitl.PropertiesUtil;
import hixing.contacts.vcard.VCardIO;
import hixing.contacts.view.adapter.ContactHomeAdapter;
import hixing.contacts.view.sms.MessageBoxList;

public class HomeContactActivity extends Activity {

//	private MenuHorizontalScrollView scrollView;
	private ListView menuList;
	private View acbuwaPage;
	private ImageButton menuBtn;
//	private MenuListAdapter menuListAdapter;
	private View[] children;
	private LayoutInflater inflater;


	private ContactHomeAdapter adapter;
	private ListView personList;
	private List<ContactBean> list;
	private AsyncQueryHandler asyncQuery;
//	private QuickAlphabeticBar alpha;
	private ImageButton addContactBtn;

	private Map<Integer, ContactBean> contactIdMap = null;

	private boolean isAddFormContact = true; // 是否从通讯录添加
	private DbUtils dbUtils;
	private AppeSession appSession;
	// 黑名单
	private List<BackList> backLists = new ArrayList<BackList>();
	private Handler handler;

	private ArrayAdapter<String> popuAdapter;

	private PopupWindow mPopupWindow;
	private String[] items;
	private ListView mListView;

	private Handler mHandler = null;
	private VCardIO vcarIO = null;
	private ProgressDialog progressDlg = null;
	private String importing = "";
	private String fileName ;
	private String phone;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		PropertiesUtil.intializePreference(this);
		phone = PropertiesUtil.read(PropertiesUtil.USER_PHONE,"110");
		fileName =  Environment.getExternalStorageDirectory().getPath()+"/"+phone+".vcf";
		inflater = LayoutInflater.from(this);

		mHandler = new Handler();
		vcarIO = new VCardIO(this);
		// 显示进度条
		progressDlg = new ProgressDialog(this);
		progressDlg.setCancelable(false);
		progressDlg.setProgress(10000);

		dbUtils = DbUtils.create(this);

		items = getResources().getStringArray(R.array.names);
		try {
			backLists = getAllBackLists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				deleteBack();

			}
		};
		appSession = ((MyApplication) getApplication()).getAppSession();
		SharedPreferences sharedPreferences = getSharedPreferences("mycontact", Context.MODE_PRIVATE);
		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		isAddFormContact = sharedPreferences.getBoolean("isAddFormContact", false);

//		setContentView(inflater.inflate(R.layout.menu_scroll_view, null));
//		scrollView = (MenuHorizontalScrollView) findViewById(R.id.mScrollView);
//		menuListAdapter = new MenuListAdapter(this, queryGroup());
//		menuList = (ListView) findViewById(R.id.menuList);
//		menuList.setAdapter(menuListAdapter);

		acbuwaPage = inflater.inflate(R.layout.home_contact_page, null);

		setContentView(acbuwaPage);

		menuBtn = (ImageButton) this.acbuwaPage.findViewById(R.id.menuBtn);

		personList = (ListView) this.acbuwaPage.findViewById(R.id.acbuwa_list);

//		alpha = (QuickAlphabeticBar) this.acbuwaPage.findViewById(R.id.fast_scroller);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		init();

		menuBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				scrollView.clickMenuBtn(HomeContactActivity.this);
				showPopup();
			}
		});


		View leftView = new View(this);
		leftView.setBackgroundColor(Color.TRANSPARENT);
		children = new View[]{leftView, acbuwaPage};
//		scrollView.initViews(children, new SizeCallBackForMenu(this.menuBtn), this.menuList);
//		scrollView.setMenuBtn(this.menuBtn);

		addContactBtn = (ImageButton) findViewById(R.id.addContactBtn);
		addContactBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
				Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
				startActivityForResult(intent, 1008);
			}
		});

		startReceiver1();
	}

	private void init() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
		String[] projection = {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1,
				"sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
		}; // 查询的列
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (MenuHorizontalScrollView.menuOut == true)
//				this.scrollView.clickMenuBtn(HomeContactActivity.this);
//			else
			((MyApplication) getApplication()).promptExit(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 数据库异步查询类AsyncQueryHandler
	 *
	 * @author administrator
	 *
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		/**
		 * 查询结束的回调函数
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {

				contactIdMap = new HashMap<Integer, ContactBean>();

				list = new ArrayList<ContactBean>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);

					if (contactIdMap.containsKey(contactId)) {

					} else {

						ContactBean cb = new ContactBean();
						cb.setDisplayName(name);
//					if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
//						cb.setPhoneNum(number.substring(3));
//					} else {
						cb.setPhoneNum(number);
//					}
						cb.setSortKey(sortKey);
						cb.setContactId(contactId);
						cb.setPhotoId(photoId);
						cb.setLookUpKey(lookUpKey);
						list.add(cb);

						contactIdMap.put(contactId, cb);

					}
				}
				if (backLists != null && backLists.size() > 0) {
					for (int i = 0; i < backLists.size(); i++) {
						for (int j = 0; j < list.size(); j++) {
							if (backLists.get(i).getLxid().equals(list.get(j).getContactId() + "")) {
								list.remove(j);
							}
						}
					}
				}
				if (list.size() > 0) {
					setAdapter(list);
					appSession.setAllContacts(list);// 存储所有联系人
				}
			}
		}

	}


	private void setAdapter(List<ContactBean> list) {
		adapter = new ContactHomeAdapter(this, list);
//		adapter = new ContactHomeAdapter(this, list, alpha);
		personList.setAdapter(adapter);
//		alpha.init(HomeContactActivity.this);
//		alpha.setListView(personList);
//		alpha.setHight(alpha.getHeight());
//		alpha.setVisibility(View.VISIBLE);
		personList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactBean cb = (ContactBean) adapter.getItem(position);
				showContactDialog(lianxiren1, cb, position);
			}
		});
	}


//	private String[] lianxiren1 = new String[]{"拨打电话", "发送短信", "查看详细", "移动分组", "移出群组", "删除"};
	private String[] lianxiren1 = new String[]{"拨打电话", "发送短信", "查看详细", "删除"};

	//群组联系人弹出页
	private void showContactDialog(final String[] arg, final ContactBean cb, final int position) {
		new AlertDialog.Builder(this).setTitle(cb.getDisplayName()).setItems(arg,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Uri uri = null;

						switch (which) {

							case 0://打电话
								String toPhone = cb.getPhoneNum();
								uri = Uri.parse("tel:" + toPhone);
								Intent it = new Intent(Intent.ACTION_CALL, uri);
								if (ActivityCompat.checkSelfPermission(HomeContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
									// TODO: Consider calling
									//    ActivityCompat#requestPermissions
									// here to request the missing permissions, and then overriding
									//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
									//                                          int[] grantResults)
									// to handle the case where the user grants the permission. See the documentation
									// for ActivityCompat#requestPermissions for more details.
									return;
								}
								startActivity(it);
								break;

							case 1://发短息

								String threadId = getSMSThreadId(cb.getPhoneNum());

								Map<String, String> map = new HashMap<String, String>();
								map.put("phoneNumber", cb.getPhoneNum());
								map.put("threadId", threadId);
								BaseIntentUtil.intentSysDefault(HomeContactActivity.this, MessageBoxList.class, map);
								break;

							case 2:// 查看详细       修改联系人资料

								uri = ContactsContract.Contacts.CONTENT_URI;
								Uri personUri = ContentUris.withAppendedId(uri, cb.getContactId());
								Intent intent2 = new Intent();
								intent2.setAction(Intent.ACTION_VIEW);
								intent2.setData(personUri);
								startActivity(intent2);
								break;

							case 3:// 移动分组

								//					Intent intent3 = null;
								//					intent3 = new Intent();
								//					intent3.setClass(ContactHome.this, GroupChoose.class);
								//					intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								//					intent3.putExtra("联系人", contactsID);
								//					Log.e("contactsID", "contactsID---"+contactsID);
								//					ContactHome.this.startActivity(intent3);
								break;

							case 4:// 移出群组

								//					moveOutGroup(getRaw_contact_id(contactsID),Integer.parseInt(qzID));
								break;

							case 5:// 删除
								addBack(cb.getContactId() + "", position, "1");
								showDelete(cb.getContactId(), position);
								break;
							case 6:// 加入黑名单
								addBack(cb.getContactId() + "", position, "0");
								break;
						}
					}
				}).show();
	}

	// 删除联系人方法
	private void showDelete(final int contactsID, final int position) {
		new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("是否删除此联系人")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//源码删除
						Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactsID);
						Uri lookupUri = ContactsContract.Contacts.getLookupUri(HomeContactActivity.this.getContentResolver(), deleteUri);
						if(lookupUri != Uri.EMPTY){
							HomeContactActivity.this.getContentResolver().delete(deleteUri, null, null);
						}
						adapter.remove(position);
						adapter.notifyDataSetChanged();
						Toast.makeText(HomeContactActivity.this, "该联系人已经被删除.", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				}).show();
	}




	/**
	 *
	 *查询所有群组
	 *返回值List<ContactGroup>
	 */
	public List<GroupBean> queryGroup(){

		List<GroupBean> list=new ArrayList<GroupBean>();

		GroupBean cg_all=new GroupBean();
		cg_all.setId(0);
		cg_all.setName("全部");
		list.add(cg_all);

		Cursor cur = getContentResolver().query(Groups.CONTENT_URI, null, null, null, null);
		for (cur.moveToFirst();!(cur.isAfterLast());cur.moveToNext()) {
			if(null!=cur.getString(cur.getColumnIndex(Groups.TITLE))&&(!"".equals(cur.getString(cur.getColumnIndex(Groups.TITLE))))){
				GroupBean cg=new GroupBean();
				cg.setId(cur.getInt(cur.getColumnIndex(Groups._ID)));
				cg.setName(cur.getString(cur.getColumnIndex(Groups.TITLE)));
				list.add(cg);
			}
		}
		cur.close();
		return list;
	}

	/**
	 * 添加黑名单
	 */
	public void addBack(String id, int position, String state) {
		try {
			BackList backList = new BackList();
			backList.setLxid(id);
			backList.setSmsid(state);
			dbUtils.save(backList);
			adapter.remove(position);
			adapter.notifyDataSetChanged();
			Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 获得黑名单的信息
	 */
	public List<BackList> getAllBackLists() throws Exception {
		return dbUtils.findAll(BackList.class);
	}

	/**
	 * 删除黑名单
	 */
	public void deleteBack() {
		try {
			List<BackList> backLists = dbUtils.findAll(BackList.class);
			for (int i = 0; i < backLists.size(); i++) {
				if (backLists.get(i).getSmsid().equals("0") || backLists.get(i).getSmsid().equals("1")) {
					dbUtils.delete(backLists.get(i));
				}
			}
			AbToastUtil.shortShow(this, "恢复成功");
			this.backLists = null;
			startReceiver1();
		} catch (Exception e) {
		}
	}







	private void queryGroupMember(GroupBean gb){

		String[] RAW_PROJECTION = new String[]{ContactsContract.Data.RAW_CONTACT_ID};

		Cursor cur = getContentResolver().query(ContactsContract.Data.CONTENT_URI,RAW_PROJECTION,
				ContactsContract.Data.MIMETYPE+" = '"+GroupMembership.CONTENT_ITEM_TYPE
						+"' AND "+ContactsContract.Data.DATA1+"="+ gb.getId(),
				null,
				"data1 asc");

		StringBuilder inSelectionBff = new StringBuilder().append(ContactsContract.RawContacts._ID + " IN ( 0");
		while(cur.moveToNext()){
			inSelectionBff.append(',').append(cur.getLong(0));
		}
		cur.close();
		inSelectionBff.append(')');

		Cursor contactIdCursor =  getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
				new String[] { ContactsContract.RawContacts.CONTACT_ID }, inSelectionBff.toString(), null, ContactsContract.Contacts.DISPLAY_NAME+"  COLLATE LOCALIZED asc ");
		Map<Integer,Integer> map=new HashMap<Integer,Integer>();
		while (contactIdCursor.moveToNext()) {
			map.put(contactIdCursor.getInt(0), 1);
		}
		contactIdCursor.close();

		Set<Integer> set = map.keySet();
		Iterator<Integer> iter = set.iterator();
		List<ContactBean> list=new ArrayList<ContactBean>();
		while(iter.hasNext()){
			Integer key = iter.next();
			list.add(queryMemberOfGroup(key));
		}
		setAdapter(list);
	}

	private ContactBean queryMemberOfGroup(int id){

		ContactBean cb = null;

		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
		String[] projection = {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1,
				"sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
		}; // 查询的列
		Cursor cursor = getContentResolver().query(uri, projection, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			list = new ArrayList<ContactBean>();
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String name = cursor.getString(1);
				String number = cursor.getString(2);
				String sortKey = cursor.getString(3);
				int contactId = cursor.getInt(4);
				Long photoId = cursor.getLong(5);
				String lookUpKey = cursor.getString(6);

				cb = new ContactBean();
				cb.setDisplayName(name);
//				if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
//					cb.setPhoneNum(number.substring(3));
//				} else {
				cb.setPhoneNum(number);
//				}
				cb.setSortKey(sortKey);
				cb.setContactId(contactId);
				cb.setPhotoId(photoId);
				cb.setLookUpKey(lookUpKey);
			}
		}
		cursor.close();
		return cb;
	}


	/**
	 * 数据库异步查询类AsyncQueryHandler
	 *
	 * @author administrator
	 *
	 */
	//	private class GroupQueryHandler extends AsyncQueryHandler {
	//
	//		public GroupQueryHandler(ContentResolver cr) {
	//			super(cr);
	//		}
	//
	//		/**
	//		 * 查询结束的回调函数
	//		 */
	//		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	//			if (cursor != null && cursor.getCount() > 0) {
	//				list = new ArrayList<ContactBean>();
	//				cursor.moveToFirst();
	//				for (int i = 0; i < cursor.getCount(); i++) {
	//					ContentValues cv = new ContentValues();
	//					cursor.moveToPosition(i);
	//					String name = cursor.getString(1);
	//					String number = cursor.getString(2);
	//					String sortKey = cursor.getString(3);
	//					int contactId = cursor.getInt(4);
	//					Long photoId = cursor.getLong(5);
	//					String lookUpKey = cursor.getString(6);
	//
	//					ContactBean cb = new ContactBean();
	//					cb.setDisplayName(name);
	//					if (number.startsWith("+86")) {// 去除多余的中国地区号码标志，对这个程序没有影响。
	//						cb.setPhoneNum(number.substring(3));
	//					} else {
	//						cb.setPhoneNum(number);
	//					}
	//					cb.setSortKey(sortKey);
	//					cb.setContactId(contactId);
	//					cb.setPhotoId(photoId);
	//					cb.setLookUpKey(lookUpKey);
	//					list.add(cb);
	//				}
	//				if (list.size() > 0) {
	//					setAdapter(list);
	//				}
	//			}
	//		}
	//
	//	}

	public static String[] SMS_COLUMNS = new String[]{
			"thread_id"
	};
	private String getSMSThreadId(String adddress){
		Cursor cursor = null;
		ContentResolver contentResolver = getContentResolver();
		cursor = contentResolver.query(Uri.parse("content://sms"), SMS_COLUMNS, " address like '%" + adddress + "%' ", null, null);
		String threadId = "";
		if (cursor == null || cursor.getCount() > 0){
			cursor.moveToFirst();
			threadId = cursor.getString(0);
			cursor.close();
			return threadId;
		}else{
			cursor.close();
			return threadId;
		}
	}







	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(1008 == requestCode){
			init();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onDestroy() {
		super.onDestroy();
		stopReceiver1();
	}

	private String ACTION1 = "SET_DEFAULT_SIG";
	private HomeContactActivity.BaseReceiver1 receiver1 = null;
	/**
	 * 打开接收器
	 */
	private void startReceiver1() {
		if(null==receiver1){
			IntentFilter localIntentFilter = new IntentFilter(ACTION1);
			receiver1 = new HomeContactActivity.BaseReceiver1();
			this.registerReceiver(receiver1, localIntentFilter);
		}
	}
	/**
	 * 关闭接收器
	 */
	private void stopReceiver1() {
		if (null != receiver1)
			unregisterReceiver(receiver1);
	}
	public class BaseReceiver1 extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION1)) {

				String str_bean = intent.getStringExtra("groupbean");
				Gson gson = new Gson();
				GroupBean gb = gson.fromJson(str_bean, GroupBean.class);
				if(gb.getId() == 0){

					init();
				}else{

					queryGroupMember(gb);
				}
			}
		}
	}
	/**
	 * 更新进度条
	 *
	 * @param progress
	 *            进度
	 */
	public void updateProgress(final int progress) {
		mHandler.post(new Runnable() {
			public void run() {
				progressDlg.setProgress(progress * 100);
				progressDlg.setMessage(importing + progress + "%");
				if (progress == 100) {
					progressDlg.cancel();
				}

			}
		});
	}

	/**
	 * 导出
	 *
	 */
	public void doExport() {
		if (vcarIO != null) {
			// 判断存储卡是否存在
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				// 更新进度
				progressDlg.show();
				importing = "正在导出,请稍后...";
				updateProgress(0);
				vcarIO.doExport(fileName, HomeContactActivity.this);
			} else {
				AbToastUtil.shortShow(HomeContactActivity.this, "导出失败，可能是因为SD卡不存在！");
			}
		}
	}
	public void doUpload(File file){

		HttpUtils httpUtils = new HttpUtils();
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("usertel",phone);
		requestParams.addBodyParameter("uploadedfile",file);
		String url = "http://tryworld.cn/index.php/Home/Appapi/upload";
		httpUtils.send(HttpRequest.HttpMethod.POST, url,requestParams, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.e("onSuccess-response:"+responseInfo.result);
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					int code = jsonObject.getInt("code");
					if(code == 200){
						AbToastUtil.shortShow(HomeContactActivity.this,"备份成功");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					AbToastUtil.shortShow(HomeContactActivity.this,"备份失败");
				}

			}

			@Override
			public void onFailure(HttpException e, String s) {
				LogUtils.e(e.getMessage()+"onFailure-response:"+s);
				AbToastUtil.shortShow(HomeContactActivity.this,"备份失败,请检查网络");
			}
		});
	}
	public void doGetContacts(){
		HttpUtils httpUtils = new HttpUtils();
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("usertel",phone);
		LogUtils.e("doGetContacts-usertel:"+phone);
		String url = "http://tryworld.cn/index.php/Home/Appapi/getConnect";
		httpUtils.send(HttpRequest.HttpMethod.POST, url,requestParams, new RequestCallBack<String>() {
			@Override
			public void onStart() {
				super.onStart();
				AbToastUtil.shortShow(HomeContactActivity.this,"正在从云端获取...");
			}
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.e("onSuccess-response:"+responseInfo.result);
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					int code = jsonObject.getInt("code");
					if(code == 200){
						AbToastUtil.shortShow(HomeContactActivity.this,"获取成功");

						String fileUrl = jsonObject.getString("data");
						if(!TextUtils.isEmpty(fileUrl)){
							fileUrl = "http://tryworld.cn"+fileUrl;
							downLoadFile(fileUrl);
						}
					}else if(code==202){
						AbToastUtil.shortShow(HomeContactActivity.this,"备份文件不存在");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					AbToastUtil.shortShow(HomeContactActivity.this,"获取备份通讯录失败");
				}

			}

			@Override
			public void onFailure(HttpException e, String s) {
				LogUtils.e(e.getMessage()+"onFailure-response:"+s);
				AbToastUtil.shortShow(HomeContactActivity.this,"获取备份文件失败,请检查网络");
			}
		});
	}
	private void downLoadFile(String fileUrl){
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.download(fileUrl, fileName, new RequestCallBack<File>() {
			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				LogUtils.e("onSuccess-response:"+responseInfo.result.getName());
				doImport();
//				AbToastUtil.shortShow(HomeContactActivity.this,"恢复成功");
			}

			@Override
			public void onFailure(HttpException e, String s) {
				LogUtils.e("onFailure-response: "+s+" --HttpException:  "+e.getMessage());
				AbToastUtil.shortShow(HomeContactActivity.this,"恢复失败,请检查网络");
			}
		});
	}

	/**
	 * 倒入
	 *
	 */
	public void doImport() {
		if (vcarIO != null) {
			// 判断存储卡是否存在
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				if(new File(fileName).exists()){
					LogUtils.e("filename: "+fileName);
					// 更新进度
					progressDlg.show();
					importing = "正在导入,请稍后...";
					updateProgress(0);
					vcarIO.doImport(fileName, true, HomeContactActivity.this);
				}else {
					doGetContacts();
				}

			} else {
				AbToastUtil.shortShow(HomeContactActivity.this, "导入失败，可能是因为SD卡或者导入包不存在！");
			}
		}
	}

	private void showPopup() {
		if (null == mPopupWindow) {
			View layout = LayoutInflater.from(this).inflate(R.layout.bottom_popup, null);
			mListView = (ListView) layout.findViewById(R.id.listView1);
			mPopupWindow = new PopupWindow(layout, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setFocusable(true);
			mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.window_bg));
			mPopupWindow.setAnimationStyle(R.style.PopupAnimation);

			popuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
			mListView.setAdapter(popuAdapter);

		}

		int[] pos = new int[2];
		menuBtn.getLocationOnScreen(pos);
		mPopupWindow.showAtLocation(menuBtn, Gravity.LEFT | Gravity.TOP, pos[0], pos[1]);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				if(!HttpUtil.isConnected(HomeContactActivity.this)){
					AbToastUtil.shortShow(HomeContactActivity.this,"请检查网络");
					return;
				}
				if (position == 0) {// 从sd恢复联系人信息
					doImport();
					// 恢复成功初始化联系人
					init();
				} else if (position == 1) {// 导出到sd并通过邮件上传，模拟线上备份
					doExport();
				} else if (position == 2) {// 从通讯录获取
					SharedPreferences sharedPreferences = getSharedPreferences("mycontact", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
					editor.putBoolean("isAddFormContact", true);
					editor.commit();
					AbToastUtil.shortShow(HomeContactActivity.this, "从通讯录添加成功");
					init();
				}

				if (mPopupWindow != null) {
					mPopupWindow.dismiss();
				}
			}
		});

	}


}
