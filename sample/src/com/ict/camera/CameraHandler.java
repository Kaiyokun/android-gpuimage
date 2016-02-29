package com.ict.camera;

/**
 * 摄像头接口
 * @author yingzi
 *
 */
public interface CameraHandler {
	/**
	 * 设置数据回调接口DataProcessor
	 * @param dp
	 */
	public void setDataProcessor(DataProcessor dp);

	/**
	 * 打开摄像头
	 */
	public void openCamera();

	/**
	 *关闭摄像头
	 */
	public void closeCamera();
}
