package com.ict.camera;
/**
 * 图像帧格式类
 * @author yingzi
 *
 */
public class FrameData implements Cloneable {
	private long timestamp;
	private int width;
	private int height;
	private int picType;
//	private byte[] y;
//	private byte[] u;
//	private byte[] v;
	private byte[] data;
	
	public FrameData(byte[] data, int width, int height,
			long timestamp, int picType) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.timestamp = timestamp;
		this.picType = picType;
	}


	/**
	 * 获得时间戳
	 * 
	 * @return the current value of the timestamp property
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * 设置时间戳.
	 * 
	 * @param aTimestamp
	 *            the new value of the timestamp property
	 */
	public void setTimestamp(long aTimestamp) {
		timestamp = aTimestamp;
	}

	/**
	 * 获得图像类型.
	 * 
	 * @return the current value of the picType property
	 */
	public int getPicType() {
		return picType;
	}

	/**
	 *  设置图像类型.
	 * 
	 * @param aPicType
	 *            the new value of the picType property
	 */
	public void setPicType(int aPicType) {
		picType = aPicType;
	}

	/**
	 *  获得图像数据.
	 * 
	 * @return the current value of the y property
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 *  设置图像数据
	 * 
	 * @param aY
	 *            the new value of the y property
	 */
	public void setData(byte[] data) {
		this.data = data;
	}


	/**
	 * 获得图像宽度.
	 * 
	 * @return the current value of the width property
	 */
	public int getWidth() {
		return width;
	}

	/**
	 *设置图像宽度.
	 * 
	 * @param aWidth
	 *            the new value of the width property
	 */
	public void setWidth(int aWidth) {
		width = aWidth;
	}

	/**
	 * 获得图像高度
	 * 
	 * @return the current value of the height property
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 设置图像高度
	 * 
	 * @param aHeight
	 *            the new value of the height property
	 */
	public void setHeight(int aHeight) {
		height = aHeight;
	}
	
	/**
	 * 清空图像帧数据
	 */
	public void clear() {
		this.data = null;
		this.width = 0;
		this.height = 0;
		this.timestamp = 0;
		this.picType = 0;
	}
	/**
	 * 克隆图像帧对象
	 */
	public FrameData clone() {
		FrameData object = null;
		try {
			object = (FrameData) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println(e.toString());
		}
		return object;
	}
}