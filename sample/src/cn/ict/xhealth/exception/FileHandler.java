package cn.ict.xhealth.exception;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.Environment;

/**
 * 文件操作类
 * 
 * @author yingzi
 * 
 */
public class FileHandler {
	/**
	 * 创建新文件filePath。
	 * 如果文件不存在，则先判断父目录是否存在，父目录不存在则创建父目录，如果父目录创建失败则返回null。父目录创建成功后，再在当前目录下创建该文件
	 * 
	 * @param filePath
	 *            需要创建的文件的绝对路径
	 * @return 返回创建的文件file，父目录创建失败，返回null
	 */
	public File createNewFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			if (!isFolderExists(file.getParent())) { return null; }
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	/**
	 * 判断文件路径是否存在
	 * 
	 * @param path
	 * @return
	 */
	public Boolean isFileExisted(String path) {
		return (new File(path)).exists();
	}

	/**
	 * 判断是否有SD卡
	 * 
	 * @return
	 */
	public Boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断目录路径（只包含所有文件夹）是否存在，不存在则创建此路径
	 * 
	 * @param path
	 * @return
	 */
	public Boolean isFolderExists(String path) {
		if (!isFileExisted(path)) {
			File file = new File(path);
			if (file.mkdir() || file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 把text写到file中，isAppend为true时，则在原来的基础上增加数据，isAppend为false,则覆盖原有数据
	 * 
	 * @param file
	 * @param text
	 * @param isAppend
	 */
	public void savedToText(File file, String text, boolean isAppend) {
		if (hasSdcard() && null != file) {
			OutputStreamWriter outWriter = null;
			try {
				if (!file.exists()) {
					file.createNewFile();
					outWriter = new OutputStreamWriter(new FileOutputStream(
							file), "utf-8");
				} else {
					outWriter = new OutputStreamWriter(new FileOutputStream(
							file, isAppend), "utf-8");
				}
				outWriter.write(text);
				outWriter.flush();
				outWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					this.deleteFile(files[i]);
				}
				file.delete();
			}

		}
	}

	/**
	 * 把b保存到路径outputFile下的文件中
	 * 
	 * @param b
	 *            需要保存的byte数组
	 * @param outputFile
	 *            文件路径
	 */
	public static void getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		FileOutputStream fstream = null;
		try {
			file = new File(outputFile);
			fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
