package hixing.contacts.view;

import hixing.contacts.R;
import hixing.contacts.application.MyApplication;
import hixing.contacts.uitl.PropertiesUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeSettintActivity extends Activity {

	private EditText et_name,et_nikeName, et_birthday;
	private EditText et_age, et_phone, et_email;
	private TextView editBtn;
	private ImageView headface;

	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	private boolean isEnable = false;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_setting_page);
		headface = (ImageView)findViewById(R.id.user_face) ;
		editBtn = (TextView)findViewById(R.id.user_editBtn);
		et_name = (EditText)findViewById(R.id.user_name);
		et_nikeName = (EditText)findViewById(R.id.user_nikeName);
		et_birthday = (EditText)findViewById(R.id.user_birthday);
		et_age = (EditText)findViewById(R.id.user_age);
		et_phone = (EditText)findViewById(R.id.user_phone);
		et_email = (EditText)findViewById(R.id.user_email);
		initInfo();
		setEditEnable(isEnable);
		setListener();
	}
	private void initInfo(){
		PropertiesUtil.intializePreference(HomeSettintActivity.this);
		String name =PropertiesUtil.read(PropertiesUtil.USER_NAME,"");
		String phone = PropertiesUtil.read(PropertiesUtil.USER_PHONE,"");
		String age = PropertiesUtil.read(PropertiesUtil.USER_AGE,"");
		String email = PropertiesUtil.read(PropertiesUtil.USER_EMAIL,"");
		String sex = PropertiesUtil.read(PropertiesUtil.USER_SEX,"");
		String nickname=PropertiesUtil.read(PropertiesUtil.USER_NIKENAME,"");
		String birthday = PropertiesUtil.read(PropertiesUtil.USER_BIRTHYDAY,"");
		String face = PropertiesUtil.read(PropertiesUtil.USER_FACE,"");
		et_name.setText(name);
		et_nikeName.setText(nickname);
		et_age.setText(age);
		et_email.setText(email);
		et_phone.setText(phone);
		et_birthday.setText(birthday);
		if(!TextUtils.isEmpty(face)){
			Bitmap bitmap = BitmapFactory.decodeFile(face);
			if(bitmap!=null)
			headface.setImageBitmap(bitmap);
		}else {
			headface.setImageResource(R.mipmap.ic_launcher);
		}
	}
	private void setListener(){
		editBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isEnable){
					isEnable = true;
					editBtn.setText("完成");
				}else {
					isEnable = false;
					editBtn.setText("编辑");
					saveInfo();
				}

				setEditEnable(isEnable);
			}
		});
		headface.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isEnable){
					showDialog();
				}
			}
		});
	}

	private void saveInfo() {
		PropertiesUtil.save(PropertiesUtil.USER_NAME,et_name.getText().toString());
		PropertiesUtil.save(PropertiesUtil.USER_PHONE,et_phone.getText().toString());
		PropertiesUtil.save(PropertiesUtil.USER_AGE,et_age.getText().toString());
		PropertiesUtil.save(PropertiesUtil.USER_EMAIL,et_email.getText().toString());
		PropertiesUtil.save(PropertiesUtil.USER_SEX,"man");
		PropertiesUtil.save(PropertiesUtil.USER_NIKENAME,et_nikeName.getText().toString());
		PropertiesUtil.save(PropertiesUtil.USER_BIRTHYDAY,et_birthday.getText().toString());
		PropertiesUtil.save(PropertiesUtil.USER_FACE,getSaveRealPath());
	}

	private void setEditEnable(boolean enable){
			et_name.setEnabled(enable);
			et_nikeName.setEnabled(enable);
			et_birthday.setEnabled(enable);
			et_age.setEnabled(enable);
			et_phone.setEnabled(enable);
			et_email.setEnabled(enable);
	}
	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								Intent intentFromGallery = new Intent();
								intentFromGallery.setType("image/*"); // 设置文件类型
								intentFromGallery
										.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intentFromGallery,
										IMAGE_REQUEST_CODE);
								break;
							case 1:

								Intent intentFromCapture = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								// 判断存储卡是否可以用，可用进行存储
								if (hasSdcard()) {

									intentFromCapture.putExtra(
											MediaStore.EXTRA_OUTPUT,
											Uri.fromFile(new File(SAVE_REAL_PATH,
													IMAGE_FILE_NAME)));
								}

								startActivityForResult(intentFromCapture,
										CAMERA_REQUEST_CODE);
								break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
				case IMAGE_REQUEST_CODE:
					startPhotoZoom(data.getData());
					break;
				case CAMERA_REQUEST_CODE:
					if (hasSdcard()) {
						File tempFile = new File(
								SAVE_REAL_PATH
										+ IMAGE_FILE_NAME);
						startPhotoZoom(Uri.fromFile(tempFile));
					} else {
						Toast.makeText(HomeSettintActivity.this, "未找到存储卡，无法存储照片！",
								Toast.LENGTH_LONG).show();
					}

					break;
				case RESULT_REQUEST_CODE:
					if (data != null) {
						getImageToView(data);
					}
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 *
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 *
	 * @param
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			headface.setImageDrawable(drawable);
			try {
				saveFile(photo,IMAGE_FILE_NAME,"");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 检查是否存在SDCard
	 * @return
	 */
	private boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}

	/** 首先默认个文件保存路径 */
	private static final String SAVE_PIC_PATH=Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)
			? Environment.getExternalStorageDirectory().getAbsolutePath()
			: "/mnt/sdcard";//保存到SD卡
	private static final String SAVE_REAL_PATH = SAVE_PIC_PATH+ "/savePic";//保存的确切位置

	//下面就是保存的方法，传入参数就可以了：

	private void saveFile(Bitmap bm, String fileName, String path) throws IOException {
		String subForder = SAVE_REAL_PATH + path;
		File foder = new File(subForder);
		if (!foder.exists()) {
			foder.mkdirs();
		}
		File myCaptureFile = new File(subForder, fileName);
		if (!myCaptureFile.exists()) {
			myCaptureFile.createNewFile();
		}
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}

	private String getSaveRealPath(){
		if(hasSdcard()){
			File file = new File(SAVE_REAL_PATH,IMAGE_FILE_NAME);
			if(file.exists()){
				return file.getAbsolutePath();
			}
		}
		return "";
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			((MyApplication) getApplication()).promptExit(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
