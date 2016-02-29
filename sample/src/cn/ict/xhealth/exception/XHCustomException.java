package cn.ict.xhealth.exception;

import android.util.Log;

/**
 * 知晓的异常
 * @author yingzi
 *
 */
public class XHCustomException extends Exception{
	
	
	public XHCustomException(){
		super();
	}
	
	/**
	 * @param detailMessage 异常详细信息
	 */
	public XHCustomException(String detailMessage){
		super(detailMessage);
	}
	
	/**
	 * 获取Sdk版本名
	 * @return
	 */
	
	/**
	 * 获取sdk版本号
	 * @return
	 */
	
	/**
	 * 异常发生所在的类名、方法名、行号组成的hashcode
	 * @return
	 */
	public int getHashId(){
		StackTraceElement[] stackTrace = this.getStackTrace();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(stackTrace[0].getClassName())
				.append(stackTrace[0].getMethodName())
				.append(stackTrace[0].getLineNumber());
		Log.e("XHCustomException", ""+stringBuilder.toString());
		return stringBuilder.toString().hashCode();
	}
	
}
