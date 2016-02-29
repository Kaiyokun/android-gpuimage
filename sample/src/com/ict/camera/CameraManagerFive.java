package com.ict.camera;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

import javax.security.auth.login.LoginException;

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
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View.MeasureSpec;

/**
 * 适合android5.0以上，android6.0以下的系统调用
 * @author yingzi
 *
 */
public class CameraManagerFive implements CameraHandler {
	public static final String TAG = "CameraManagerFive";
	
	private Size mPreviewSize;
	private CameraDevice mCameraDevice;
	private CaptureRequest.Builder mPreviewBuilder;
	private CameraCaptureSession mPreviewSession;
	private Context mContext;
	private String currentCameraId;
	private boolean isLight;
	private CameraManager manager;
	private boolean isInit;
	private DataProcessor dataProcessor;
	private Looper looper;
	private MyHandler myHandler = null;
	private OpenCameraThread gThread;
	private ImageReader iReader;
	private Surface mSurface;

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
			myHandler.obtainMessage(100).sendToTarget();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *关闭摄像头
	 */
	@Override
	public void closeCamera() {
		try {
			stopCameraPreview();
			if (dataProcessor != null) {
				dataProcessor.onCameraClose(true);
				dataProcessor = null;
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * 初始化摄像头数据
	 * @param context
	 */
	public synchronized void intData(Context context) {
		try {
			mContext = context;
			gThread = new OpenCameraThread();
			if (!gThread.isAlive()) {
				gThread.start();
			}
			isLight = isSurportFlashlight(mContext);
			manager = (CameraManager) mContext
					.getSystemService(Context.CAMERA_SERVICE);
			currentCameraId = manager.getCameraIdList()[0];
			for (String cid : manager.getCameraIdList()) {
				CameraCharacteristics characteristics = manager
						.getCameraCharacteristics(cid);
				if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
					currentCameraId = cid;
					StreamConfigurationMap map = characteristics
							.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
					Size[] sizes = map.getOutputSizes(SurfaceTexture.class);
					Arrays.sort(sizes, new ComparatorSize());
					for (Size e : sizes) {
//						L.e("h:" + e.getHeight() + ",w:"+e.getWidth()+",m:"+(e.getHeight()*e.getWidth()));
					}
					
					for (Size e : sizes) {
						if (e.getWidth() < 320) {
							continue;
						}
						mPreviewSize = e;
						break;
					}
					break;
				}
			}
			isInit = true;
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private boolean isSurportFlashlight(Context context) {
		boolean flag = false;
		PackageManager pm = context.getPackageManager();
		FeatureInfo[] features = pm.getSystemAvailableFeatures();
		for (FeatureInfo f : features) {
			if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

		@Override
		public void onOpened(CameraDevice camera) {
			mCameraDevice = camera;
			startCameraPreview();
			notifyCameraOpenResult(true);
		}

		@Override
		public void onDisconnected(CameraDevice camera) {
		}

		@Override
		public void onError(CameraDevice camera, int error) {
		}

	};

	private void notifyCameraOpenResult(boolean result) {
		try {
			if (dataProcessor != null) {
				dataProcessor.onCameraOpen(result);
			}
		} catch (Exception e) {
		}
	}

	private void getCapturedImgToData(Image image) {
		Image.Plane[] planes = image.getPlanes();
		if (planes[0].getBuffer() == null) {
			return;
		}
		ByteBuffer buffer = planes[0].getBuffer();
		ByteBuffer bu = planes[1].getBuffer();
		ByteBuffer bv = planes[2].getBuffer();
		byte[] dy = new byte[buffer.remaining()];
		byte[] du = new byte[bu.remaining()];
		byte[] dv = new byte[bv.remaining()];
		buffer.get(dy);
		bu.get(du);
		bv.get(dv);
//		L.e("Y:"+dy.length+"U:"+du.length+"V:"+dv.length);
		if (dataProcessor != null) {
			int width = planes[0].getRowStride();
			int height = dy.length / width;
			FrameData frameData = new FrameData(byteMerger(dy, du, dv), width,
					height, image.getTimestamp() / 1000000,
					ImageFormat.YUV_420_888);
			dataProcessor.onDataCaptured(frameData);
		}
	}

	private byte[]  byteMerger(byte[] y,byte[] u, byte[] v){
		byte[] data = new byte[y.length+u.length+v.length];
		 System.arraycopy(y, 0, data, 0, y.length); 
		 System.arraycopy(u, 0, data, y.length, u.length); 
		 System.arraycopy(v, 0, data, y.length+u.length, v.length); 
		 return data;
		
	}
	private void startCameraPreview() {
		if (null == mCameraDevice || null == mPreviewSize) {
			return;
		}
		iReader = ImageReader.newInstance(mPreviewSize.getWidth(),
				mPreviewSize.getHeight(), ImageFormat.YUV_420_888, 3);
		iReader.setOnImageAvailableListener(new OnImageAvailableListener() {
			@Override
			public void onImageAvailable(ImageReader arg0) {
				Image image = arg0.acquireLatestImage();
				if (null != image) {
					getCapturedImgToData(image);
					image.close();
				}
			}

		}, null);
		mSurface = iReader.getSurface();
		try {
			mPreviewBuilder = mCameraDevice
					.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			mPreviewBuilder.addTarget(mSurface);
			mCameraDevice.createCaptureSession(Arrays.asList(mSurface),
					new CameraCaptureSession.StateCallback() {

						@Override
						public void onConfigured(CameraCaptureSession session) {
							mPreviewSession = session;
							updatePreview();
						}

						@Override
						public void onConfigureFailed(
								CameraCaptureSession session) {
						}
					}, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void updatePreview() {
		if (null == mCameraDevice) {
			return;
		}
		mPreviewBuilder.set(CaptureRequest.CONTROL_MODE,
				CameraMetadata.CONTROL_MODE_AUTO);
		mPreviewBuilder.set(CaptureRequest.FLASH_MODE,
				CameraMetadata.FLASH_MODE_TORCH);

		HandlerThread thread = new HandlerThread("CameraPreview");
		thread.start();
		Handler backgroundHandler = new Handler(thread.getLooper());

		try {
			mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null,
					backgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void stopCameraPreview() {
		if (mSurface != null && mPreviewBuilder != null) {
			mPreviewBuilder.removeTarget(mSurface);
			mPreviewBuilder = null;
		}
		if (mPreviewSession != null) {
			mPreviewSession.close();
			mPreviewSession = null;
		}
		if (null != mCameraDevice) {
			mCameraDevice.close();
			mCameraDevice = null;

		}
		if (iReader != null) {
			iReader.close();
			iReader = null;
		}
	}

	private class ComparatorSize implements Comparator<Size> {

		@Override
		public int compare(Size s0, Size s1) {
			if (s0.getWidth() > s1.getWidth())
				return 1;
			else
				return -1;
		}

	}

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				try {
					CameraDevice curCamDevice = mCameraDevice;
					if (curCamDevice != null) {
						synchronized (curCamDevice) {
							curCamDevice.close();
							curCamDevice = null;
						}
					}
					manager.openCamera(currentCameraId, mStateCallback, null);
				} catch (Exception e) {
					notifyCameraOpenResult(false);
				}
				break;
			default:
				break;
			}
		}
	}

	private class OpenCameraThread extends Thread {
		@SuppressLint("NewApi")
		public void run() {
			Looper.prepare();
			looper = Looper.myLooper();
			myHandler = new MyHandler();
			Looper.loop();
		}
	}
}
