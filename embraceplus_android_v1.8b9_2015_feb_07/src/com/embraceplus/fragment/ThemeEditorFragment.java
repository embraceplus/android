package com.embraceplus.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.embraceplus.app.R;
import com.embraceplus.database.DefinedThemeDB;
import com.embraceplus.database.NotificationCommandMappingDB;
import com.embraceplus.model.ThemeObj;
import com.embraceplus.utils.SkinSettingManager;

public class ThemeEditorFragment extends BaseFragment {
	ImageView mCameraButton, mPhotoButton, bg_CameraButton, bg_PhotoButton;
	private Bitmap newThemeIcon;
	private Bitmap newThemeBackground;
	private String newThemeName;
	private String themeName = null;
	private ThemeObj selectedThemeObj;
	SkinSettingManager mSettingManager;
	EditText themeNameTx;
	Uri fileUri;
	private boolean updateMsg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_add_style, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSettingManager = new SkinSettingManager(this.getActivity());
		initTitleBar();
		mCameraButton = (ImageView) this.getActivity().findViewById(R.id.camera_image);
		mPhotoButton = (ImageView) this.getActivity().findViewById(R.id.ptoto_image);
		bg_CameraButton = (ImageView) this.getActivity().findViewById(R.id.bg_camera_image);
		bg_PhotoButton = (ImageView) this.getActivity().findViewById(R.id.bg_photo_image);
		themeNameTx = (EditText) ThemeEditorFragment.this.getActivity().findViewById(R.id.name_editText);
		if (getArguments() != null) {
			themeName = getArguments().getString("themeName");
		}

		if (themeName != null) {
			updateMsg = true;

			// List<ThemeObj> themeList =
			// DbBuilder.getInstant().getThemes();//.getMsgObjByMsgName(msgName);
			List<ThemeObj> themeList = DefinedThemeDB.getInstance().getThemes();
			for (ThemeObj obj : themeList) {
				if (obj.getThemeName().equals(themeName)) {
					selectedThemeObj = obj;
					themeNameTx.setText(themeName);
					themeNameTx.setEnabled(false);
					ImageView imgView = (ImageView) getActivity().findViewById(R.id.background_image);
					// fd

					String backgroundName = selectedThemeObj.getThemeBackground();
					if (backgroundName.equals("")) {
						backgroundName = themeName;
					}

					mSettingManager.changeSkin(backgroundName);

					Bitmap bmpDefaultPic = null;
					// ImageView iv = (ImageView)
					// contentView.findViewById(R.id.x);
					String filePath = "/embrace/themes/" + themeName + "/iconPic.png";
					filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + filePath;
					File f = new File(filePath);

					// bmpDefaultPic = BitmapFactory.decodeFile(filePath,null);
					try {
						bmpDefaultPic = BitmapFactory.decodeStream(new FileInputStream(f));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					ImageView tmeimgView = (ImageView) getActivity().findViewById(R.id.background_image);
					ImageView tmeimgView = (ImageView) getView().findViewById(R.id.background_image);
					tmeimgView.setImageBitmap(bmpDefaultPic);
					newThemeIcon = bmpDefaultPic;

					String backgroundFilePath = "/embrace/themes/" + backgroundName + "/backgroundPic.png";
					filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + filePath;
					Drawable bgPic = Drawable.createFromPath(filePath);

					File f1 = new File(filePath);

					// bmpDefaultPic = BitmapFactory.decodeFile(filePath,null);
					try {
						bmpDefaultPic = BitmapFactory.decodeStream(new FileInputStream(f1));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					newThemeBackground = bmpDefaultPic;
					break;
				}
			}

		}

		addListener();

	}

	public void initTitleBar() {
		initBackButton();
		setDoneButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				newThemeName = themeNameTx.getText().toString();
				if (!updateMsg) {
					// if(DbBuilder.getInstant().isThemeExisted(newThemeName))
					if (DefinedThemeDB.getInstance().isThemeExisted(newThemeName)) {
						alertMsg("The theme " + newThemeName + " is already existed, please change to a new name");
						return;
					}
				}

				if (newThemeName == null || newThemeName.equals("")) {
					alertMsg("Style Name can not be empty");
					return;
				} else if (newThemeIcon == null) {
					alertMsg("Please select a style image");
					return;
				} else if (newThemeBackground == null) {
					alertMsg("Please select a style background image");
					return;
				} else if (newThemeIcon != null && newThemeBackground != null) {
					saveMyBitmap(newThemeName, "iconPic", newThemeIcon);
					saveMyBitmap(newThemeName, "backgroundPic", newThemeBackground);
				}

				if (!updateMsg) {
					NotificationCommandMappingDB.getInstance().addNewTheme(newThemeName);
				}
				popBackStack();
			}

		});
//		*/

	}

	public void alertMsg(String msg) {
		new AlertDialog.Builder(ThemeEditorFragment.this.getActivity()).setTitle("Warning").setMessage(msg).setPositiveButton("OK", null).show();
	}

	public void addListener() {
		mCameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Camera();
			}
		});

		mPhotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Photo();
			}
		});

		bg_CameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CameraBK();
			}
		});

		bg_PhotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PhotoBK();
			}
		});
	}

	public void CameraBK() {
		/*
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, 0);
		startActivityForResult(intent, 11);*/

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, 11);
	}

	public void PhotoBK() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 1);
	}

	public void Camera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, 0);
		startActivityForResult(intent, 12);
	}

	public void Photo() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		ContentResolver resolver = getActivity().getContentResolver();

		switch (requestCode) {
		case 12:
			if (null != data) {
				Bundle extras = data.getExtras();
				if (null != extras) {
					Object object = extras.get("data");
					Bitmap thumbnail = (Bitmap) object;
					//			Bitmap thumbnail = (Bitmap) data.getStringExtra("data");
					// if(resultCode==getActivity().RESULT_OK){
//					Bitmap newThumbnail = resizeImage(thumbnail, 193, 413);
//					Bitmap newThumbnail = resizeImage(thumbnail, 150, 300);
					Bitmap newThumbnail = resizeImage(thumbnail, 100, 200);
					newThemeIcon = newThumbnail;
					ImageView imgView = (ImageView) getView().findViewById(R.id.background_image);
					imgView.setImageBitmap(newThumbnail);	
				}
			}
			// }
			break;
		case 0:
			try {
				// 获得图片的uri
				Uri originalUri = data.getData();
				byte[] mContent;
				// 将图片内容解析成字节数组
				mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
				// 将字节数组转换为ImageView可调用的Bitmap对象
				Bitmap myBitmap = getPicFromBytes(mContent, null);
				myBitmap = resizeImage(myBitmap, 193, 413);
				newThemeIcon = myBitmap;
				// //把得到的图片绑定在控件上显示
				// imageView.setImageBitmap(myBitmap);
				ImageView imgView2 = (ImageView) getView().findViewById(R.id.background_image);
				imgView2.setImageBitmap(myBitmap);
			} catch (Exception e) {

			}

			break;

		case 11:
			// Bitmap thumbnailBK = (Bitmap) data.getExtras().get("data");
			// if(resultCode==getActivity().RESULT_OK){
			// thumbnailBK = resizeImage(thumbnailBK,640,1140);
			// Bundle bundle = data.getExtras();
			// Uri cameraBKUri = (Uri)bundle.get( MediaStore.EXTRA_OUTPUT);
			byte[] mContent1 = null;
			// 将图片内容解析成字节数组
			try {
				mContent1 = readStream(resolver.openInputStream(Uri.parse(fileUri.toString())));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (null != mContent1) {
				Bitmap thumbnailBK = getPicFromBytes(mContent1, null);
				thumbnailBK = resizeImage(thumbnailBK, 640, 1140);
				newThemeBackground = thumbnailBK;
				mSettingManager.changeSkin(thumbnailBK);
				// ImageView imgViewBK =
				//			// (ImageView)getActivity().findViewById(R.id.background_image);
				// imgViewBK.setImageBitmap(newThumbnail);
				// }
			}
			break;
		case 1:
			try {
				// 获得图片的uri
				Uri originalUri = data.getData();
				byte[] mContent;
				// 将图片内容解析成字节数组
				mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
				// 将字节数组转换为ImageView可调用的Bitmap对象
				Bitmap myBitmap = getPicFromBytes(mContent, null);
				// myBitmap = resizeImage(myBitmap,193,413);
				myBitmap = resizeImage(myBitmap, 640, 1140);
				newThemeBackground = myBitmap;

				mSettingManager.changeSkin(newThemeBackground);
				// //把得到的图片绑定在控件上显示
				// imageView.setImageBitmap(myBitmap);
				// ImageView imgView2 =
//				// (ImageView)getActivity().findViewById(R.id.background_image);
				// imgView2.setImageBitmap(myBitmap);
			} catch (Exception e) {

			}

			break;
		}
	}

	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

		// load the origial Bitmap
		Bitmap BitmapOrg = bitmap;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		return resizedBitmap;

	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	public void saveMyBitmap(String themeName, String bitName, Bitmap mBitmap) {
		File fdir = new File("/sdcard/embrace/themes/" + themeName);
		fdir.mkdirs();
		File f = new File("/sdcard/embrace/themes/" + themeName + "/" + bitName + ".png");
		try {
			boolean createResult = f.createNewFile();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// DebugMessage.put("在保存图片时出错："+e.toString());
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			if (fOut != null)
				fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (fOut != null)
				fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}
}
