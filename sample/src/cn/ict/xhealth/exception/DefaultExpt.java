package cn.ict.xhealth.exception;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * 未捕获异常类
 * @author yingzi
 *
 */
public class DefaultExpt implements XHDefaultExceptionHandler{

	private static DefaultExpt defaultExpt;
	private Context mContext;
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private DefaultExpt() {
	}

	/**
	 * 获取UncaughtException对象
	 * 
	 * @return
	 */
	public static DefaultExpt getInstance() {
		if (defaultExpt == null) {
			synchronized (DefaultExpt.class) {
				if (defaultExpt == null) {
					defaultExpt = new DefaultExpt();
				}
			}
		}
		return defaultExpt;
	}

	@Override
	public void init(Context context) {
		this.mContext = context;
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		dumpExToSDCard(ex);
	}

	@Override
	public void dumpExToSDCard(Throwable ex) {
		String path = Environment.getExternalStorageDirectory()
				+ File.separator + "exception.txt";
		FileHandler fileHandler = new FileHandler();
		File file = fileHandler.createNewFile(path);
		fileHandler.savedToText(file, getNeedDeviceThrowableInfo(ex), true);
	}

	private String getNeedDeviceThrowableInfo(Throwable throwable) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		Throwable cause = throwable.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		return writer.toString();
	}

}
