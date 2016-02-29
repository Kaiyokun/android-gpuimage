package com.ict.camera;

import java.util.concurrent.LinkedBlockingDeque;

import android.content.Context;
import android.util.Log;

/**
 * 数据处理类
 * @author yingzi
 *
 */
public class DataModel implements DataProcessor {
	private static LinkedBlockingDeque<FrameData> frameBuffer;
//	private DataControllers dataControllers;
	private Context context;

	@Override
	public void onDataCaptured(FrameData frameData) {
			FrameData curFrameData = new FrameData(frameData.getData(), frameData.getWidth(), frameData.getHeight(), frameData.getTimestamp(),
				frameData.getPicType());
		try {
			while (frameBuffer.size() >= 20)
				frameBuffer.take();
			frameBuffer.put(curFrameData);
		} catch (InterruptedException e) {
//			e.printStackTrace();
		} finally {
			frameData = null;
		}
	}

	@Override
	public void onCameraOpen(boolean result) {
		if (result) {
			Log.d("DataModel", "CameraOpen");
			frameBuffer = new LinkedBlockingDeque<FrameData>(20);
//			dataControllers = new DataControllers();
//			dataControllers.init(context, frameBuffer);
//			dataControllers.startDataProcess();
		}
	}

	@Override
	public void onCameraClose(boolean result) {
//		dataControllers.stopDataProcess();
	}
	
	/**
	 * @param context
	 */
	public DataModel(Context context) {
		this.context = context;
	}

}
