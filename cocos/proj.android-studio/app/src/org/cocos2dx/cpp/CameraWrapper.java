package org.cocos2dx.cpp;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.Frame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraWrapper {
	private int mCameraOrientation = 0;
	private Size mRequestedPreviewSize;
	private Float mRequestedFps = null;
	private Camera mCamera;
	private CameraDetectorThread mDetectorThread;

	public void setRequestedFps(float fps){
		mRequestedFps = fps;
	}

	public void setRequestedPreviewSize(int width, int height){
		mRequestedPreviewSize = new Size(width, height);
	}

	public void start(SurfaceHolder preview){
		cameraStartCommon();
		try {
			mCamera.setPreviewDisplay(preview);
		} catch (Exception e) {
			e.printStackTrace();
		}
		orientateCameraPreview(mCameraOrientation);
		if(mDetectorThread != null){
			mDetectorThread.startDetect();
		}
	}

	public void start(){
		cameraStartCommon();
		mCamera.startPreview();
		orientateCameraPreview(mCameraOrientation);
		if(mDetectorThread != null){
			mDetectorThread.startDetect();
		}
	}

	private void cameraStartCommon(){
		int cameraId = 0;
		if(Camera.getNumberOfCameras() > 1){
			cameraId = 1;
		}
		try {
			mCamera = Camera.open(cameraId); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			return;
		}
		Camera.Parameters cameraParams = mCamera.getParameters();
		Size previewSize = new Size(cameraParams.getPreviewSize().width, cameraParams.getPreviewSize().height);
		List<int[]> fpsSupportRange = cameraParams.getSupportedPreviewFpsRange();
		cameraParams.setPreviewFormat(ImageFormat.NV21);
		//cameraParams.setRotation(mCameraOrientation);
		if(cameraParams.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
			cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		} else {
			Log.i(Config.TAG, "Camera auto focus is not supported on this device.");
		}
		if(mDetectorThread != null){
			mDetectorThread.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
		}

		mCamera.setParameters(cameraParams);
		mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				if(mDetectorThread != null){
					mDetectorThread.previewFrame(data, CameraWrapper.this);
				}
			}
		});
		mCamera.addCallbackBuffer(mDetectorThread.putBuffer());
		mCamera.addCallbackBuffer(mDetectorThread.putBuffer());
		mCamera.addCallbackBuffer(mDetectorThread.putBuffer());
		mCamera.addCallbackBuffer(mDetectorThread.putBuffer());
	}

	public void orientateCameraPreview(int orientation){
		mCamera.stopPreview();
		mCamera.setDisplayOrientation(orientation);
		mCamera.startPreview();
	}
	public void stop() {
		if(mDetectorThread != null){
			mDetectorThread.stopDetect();
		}
		if (mCamera != null){
			mCamera.cancelAutoFocus();
			mCamera.stopPreview();
			mCamera.setPreviewCallbackWithBuffer(null);
			mCamera.setPreviewCallback(null);
			try {
				mCamera.setPreviewDisplay(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mCamera.release();
			mCamera = null;
		};
	}

	public void release(){
		stop();
		if(mDetectorThread != null){
			mDetectorThread.release();
		}
	}

	public void setDetectorThread(CameraDetectorThread detectorThread) {
		mDetectorThread = detectorThread;
		mDetectorThread.addBufferedCallback(new CameraDetectorThread.BufferedCallback() {
			@Override
			public void onBuffered(byte[] buffer) {
				if(mCamera != null){
					mCamera.addCallbackBuffer(buffer);
				}
			}
		});
	}

	public void addCallbackBuffer(byte[] buffer){
		mCamera.addCallbackBuffer(buffer);
	}

	public Size getPreviewSize(){
		if(mDetectorThread != null){
			return mDetectorThread.getPreviewSize();
		}else{
			return mRequestedPreviewSize;
		}
	}

	public int getOrientation(){
		return mCameraOrientation;
	}
}