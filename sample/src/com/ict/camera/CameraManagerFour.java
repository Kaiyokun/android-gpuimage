package com.ict.camera;

import java.io.IOException;
import java.util.List;

import cn.ict.xhealth.exception.ArgumentExpt;
import cn.ict.xhealth.exception.DefaultExpt;
import cn.ict.xhealth.exception.InitializeExpt;
import cn.ict.xhealth.exception.PermissionExpt;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.opengl.GLES11Ext;
import android.util.Log;

/**
 * 适合android4.0以上，android5.0以下的系统调用
 * @author yingzi
 *
 */
public class CameraManagerFour implements PreviewCallback, CameraHandler {
	private static final String TAG = "CameraManagerFour";

	private Camera currentCamera;
	private Camera.Parameters oriPara;
	private int currentCameraId;
	private byte[] previewBuffer;
	private SurfaceTexture gTexture;
	private DataProcessor dataProcessor;
	private boolean isLight;
	private boolean isInit;
	private int buffersize;
	private long timestamp;
	private byte[] y;
	private byte[] u;
	private byte[] v;
	private boolean updateSurface;

	/**
	 * 设置数据回调接口DataProcessor
	 * @param dp
	 */
	@Override
	public void setDataProcessor(DataProcessor dp) {
		dataProcessor = dp;
	}

	/**
	 * 打开摄像头
	 */
	@Override
	public void openCamera() {	
		try {
			if(!isInit)
				throw new InitializeExpt("CameraHandler don't initialize data");
			if(dataProcessor == null)
				throw new ArgumentExpt("app don't set DataProcessor");
			setCameraParamters();
			if (gTexture != null) {
				currentCamera.setPreviewTexture(gTexture);
				startCameraPreview();
			}
			notifyCameraOpenResult(true);
		} catch (Exception e) {
			e.printStackTrace();
			notifyCameraOpenResult(false);
		}

	}

	/**
	 *关闭摄像头
	 */
	@Override
	public void closeCamera() {
		if (currentCamera != null) {
			stopCameraPreview();
		}
		if (dataProcessor != null) {
			dataProcessor.onCameraClose(true);
			dataProcessor = null;
		}
		if (gTexture != null) {
			gTexture.release();
			gTexture = null;
		}
		if (currentCamera != null) {
			currentCamera.release();
			currentCamera = null;
		}
	}

	/**
	 * 采集图像数据函数
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		FrameData frameData = null;
		try {
			gTexture.updateTexImage();
			timestamp = gTexture.getTimestamp() / 1000000;
			camera.addCallbackBuffer(this.previewBuffer);
//			dataTranslate(data);
			frameData = new FrameData(data, 320, 240,
					timestamp, ImageFormat.NV21);
			dataProcessor.onDataCaptured(frameData);
		} catch (Exception e) {
			e.printStackTrace();
			if (camera != null)
				camera.addCallbackBuffer(this.previewBuffer);
			return;
		} finally {
			frameData = null;
		}
	}

	private void dataTranslate(byte[] data) {
		int frameSize = 320 * 240;
		y = new byte[frameSize];
		v = new byte[frameSize / 4];
		u = new byte[frameSize / 4];
		int vp = 0;
		int up = 0;
		int uvp;
		for (int i = 0; i < frameSize; i++) {
			y[i] = data[i];
		}
		uvp = frameSize;
		for (int j = 0; j < 240 * 320 / 4; j++) {
			v[vp++] = data[uvp++];
			u[up++] = data[uvp++];
		}
	}
	
	/**
	 * 初始化摄像头数据
	 * @param context
	 */
	public synchronized void intData(Context context) {
		try {
			isLight = isSurportFlashlight(context);
			buffersize = 320 * 240
					* ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
			currentCameraId = 0;
			int totalCameraCount = Camera.getNumberOfCameras();
			CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < totalCameraCount; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
					currentCameraId = i;
					break;
				}
			}
			isInit = true;
		} catch (Exception e) {
		
		}
	}

	@SuppressLint("NewApi")
	private boolean isSurportFlashlight(Context context) {
		boolean flag = false;
		PackageManager pm = context.getPackageManager();
		FeatureInfo[] features = pm.getSystemAvailableFeatures();
		for (FeatureInfo feature : features) {
			if (PackageManager.FEATURE_CAMERA_FLASH.equals(feature.name)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private void setCameraParamters() {
		previewBuffer = new byte[buffersize];
		gTexture = new SurfaceTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
		currentCamera = Camera.open(currentCameraId);
		Parameters cameraParameters = currentCamera.getParameters();
		oriPara = currentCamera.getParameters();
		cameraParameters.setPreviewSize(320, 240);
		currentCamera.setDisplayOrientation(90);
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(currentCameraId, info);
		int gCurrentCameraFacing = info.facing;
		if (gCurrentCameraFacing == CameraInfo.CAMERA_FACING_BACK && isLight) {
			cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		} else {
			cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		}
		currentCamera.setParameters(cameraParameters);
	}

	private void startCameraPreview() throws Exception{
		currentCamera.addCallbackBuffer(previewBuffer);
		currentCamera.setPreviewCallbackWithBuffer(CameraManagerFour.this);
		if (dataProcessor == null) {
			if (oriPara != null)
				currentCamera.setParameters(oriPara);
			throw new ArgumentExpt("app don't set DataProcessor");
		}
		currentCamera.startPreview();
	}

	private void stopCameraPreview() {
		if (oriPara != null) {
			try {
				currentCamera.setParameters(oriPara);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				oriPara = null;
			}
		}
		currentCamera.stopPreview();
		currentCamera.setPreviewCallback(null);
	}

	private void notifyCameraOpenResult(boolean result) {
		try {
			if (dataProcessor != null) {
				dataProcessor.onCameraOpen(result);
			}
		} catch (Exception e) {

		}
	}

}
