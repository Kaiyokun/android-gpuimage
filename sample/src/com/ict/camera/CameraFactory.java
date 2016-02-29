package com.ict.camera;


import android.content.Context;
/**
 *摄像头工厂
 * @author yingzi
 */
public class CameraFactory {
	/**
	 * 根据sdk版本，返回对应的CameraHandler
	 * @param context
	 * @return
	 */
	public static CameraHandler getCameraHandler(Context context){
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		if(sdkVersion > 13 && sdkVersion < 21){
			CameraManagerFour cameraHandler = new CameraManagerFour();
			cameraHandler.intData(context);
			return cameraHandler;
			
		}else if(sdkVersion >= 21){
			CameraManagerFive cameraHandler = new CameraManagerFive();
			cameraHandler.intData(context);
			return cameraHandler;
		}else {
			return null;
		}
		
	}

}
