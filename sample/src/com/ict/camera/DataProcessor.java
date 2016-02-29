package com.ict.camera;

/**
 *  数据处理接口
 * @author yingzi
 * 2015-12-5 下午3：00
 */
public interface DataProcessor{
	/**
	 * 捕捉图像帧数据
	 * @param frameData
	 */
	public void onDataCaptured(FrameData frameData);
	/**
	 * 摄像头是否打开
	 * @param result
	 */
	public void onCameraOpen(boolean result);
	/**
	 * 摄像头是否关闭
	 * @param result
	 */
	public void onCameraClose(boolean result);
}
