package org.cocos2dx.cpp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Surface;
import android.webkit.WebView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Util {

	//ImageViewを使用したときのメモリリーク対策
	public static void releaseImageView(ImageView imageView){
		if (imageView != null) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable)(imageView.getDrawable());
			if (bitmapDrawable != null) {
				bitmapDrawable.setCallback(null);
			}
			imageView.setImageBitmap(null);
		}
	}

	//WebViewを使用したときのメモリリーク対策
	public static void releaseWebView(WebView webview){
		webview.stopLoading();
		webview.setWebChromeClient(null);
		webview.setWebViewClient(null);
		webview.destroy();
		webview = null;
	}

	public static int getCameraDisplayOrientation(Activity act, int nCameraID){
		if(Build.VERSION.SDK_INT >= 9){
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(nCameraID, info);
			int rotation = act.getWindowManager().getDefaultDisplay().getRotation();
			int degrees = 0;
			switch (rotation) {
				//portate:縦向き
				case Surface.ROTATION_0: degrees = 0; break;
				//landscape:横向き
				case Surface.ROTATION_90: degrees = 90; break;
				case Surface.ROTATION_180: degrees = 180; break;
				case Surface.ROTATION_270: degrees = 270; break;
			}
			int result;
			//Camera.CameraInfo.CAMERA_FACING_FRONT:アウトカメラ
			//Camera.CameraInfo.CAMERA_FACING_BACK:インカメラ

			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360;  // compensate the mirror
			} else {  // back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
			return result;
		}
		return 90;
	}

	public static ArrayList<PermissionInfo> getSettingPermissions(Context context){
		ArrayList<PermissionInfo> list = new ArrayList<PermissionInfo>();
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
			if(packageInfo.requestedPermissions != null){
				for(String permission : packageInfo.requestedPermissions){
					list.add(context.getPackageManager().getPermissionInfo(permission, PackageManager.GET_META_DATA));
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean hasSelfPermission(Context context, String permission) {
		if(Build.VERSION.SDK_INT < 23) return true;
		return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
	}

	public static void requestPermissions(Activity activity, int requestCode){
		if(Build.VERSION.SDK_INT >= 23) {
			ArrayList<String> requestPermissionNames = new ArrayList<String>();
			ArrayList<PermissionInfo> permissions = Util.getSettingPermissions(activity);
			for(PermissionInfo permission : permissions){
				if(permission.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS && !Util.hasSelfPermission(activity, permission.name)){
					requestPermissionNames.add(permission.name);
				}
			}
			if(!requestPermissionNames.isEmpty()) {
				activity.requestPermissions(requestPermissionNames.toArray(new String[0]), requestCode);
			}
		}
	}

	public static boolean existConfirmPermissions(Activity activity){
		if(Build.VERSION.SDK_INT >= 23) {
			ArrayList<PermissionInfo> permissions = Util.getSettingPermissions(activity);
			boolean isRequestPermission = false;
			for(PermissionInfo permission : permissions){
				if(permission.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS && !Util.hasSelfPermission(activity, permission.name)){
					isRequestPermission = true;
					break;
				}
			}
			return isRequestPermission;
		}
		return true;
	}

	public static Bitmap loadImageFromAsset(Context context, String path){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		AssetManager mngr = context.getAssets();
		try {
			InputStream is = mngr.open(path);
			Bitmap image = BitmapFactory.decodeStream(is, null, options);
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[] loadFilePathes(Context context, String path){
		AssetManager mngr = context.getAssets();
		try {
			return  mngr.list(path);
		}catch (IOException e){
			e.printStackTrace();
		}
		return new String[]{};
	}
}