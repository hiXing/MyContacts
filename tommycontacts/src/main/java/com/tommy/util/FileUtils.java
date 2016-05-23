package com.tommy.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Description 文件工具类
 */
public class FileUtils {
	/** 主目录 **/
	public static final String ROOT = "/AshineDoctor";
	/** 日志文件 **/
	public static final String LOG = "/Log";
	/** 图片 **/
	public static final String IMAGE = "/Image";
	/** 更新apk放置的地址 **/
	public static final String APKS = "/Apks";
	/** 录音 **/
	public static final String AUDIO = "/audio";
	/** 音频格式 **/
	public static final byte FILE_TYPE_AUDIO = 0x00;
	/** 日志格式 **/
	public static final byte FILE_TYPE_LOG = 0x01;
	/** 图片格式 **/
	public static final byte FILE_TYPE_IMAGE = 0x02;
	/** 病历图片格式 **/
	public static final byte FILE_TYPE_MEDICAL = 0x03;

	/**
	 * 是否挂载sdcard
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(status) || !Environment.isExternalStorageRemovable()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取存放文件根地址
	 * 
	 * @return
	 */
	public static String getRootDir(Context context) {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getPath() + ROOT;
		} else {
			return context.getFilesDir().getAbsolutePath() + ROOT;
		}
	}

	/**
	 * 获取存放文件根地址
	 * 
	 * @return
	 */
	public static String getRootFilePath(Context context) {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOT;
		} else {
			return context.getFilesDir().getAbsolutePath();
		}
	}

	/**
	 * 获取日志存放地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getLogDir(Context context) {
		String saveDir = FileUtils.getRootDir(context) + LOG + File.separator;
		FileUtils.createDirectory(saveDir);
		return saveDir;
	}

	/**
	 * 获取日志地址
	 */
	public static String getLogFileDir(Context context) {
		String savePath = FileUtils.getRootFilePath(context) + LOG;
		// 获取日志存放地址
		FileUtils.createDirectory(savePath);
		return savePath;
	}

	/**
	 * 图片地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getImageDir(Context context) {
		String imageDir = FileUtils.getRootDir(context) + IMAGE + File.separator;
		FileUtils.createDirectory(imageDir);
		return imageDir;
	}

	/**
	 * apks存放的地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getApksDir(Context context) {
		String apkDir = FileUtils.getRootDir(context) + APKS + File.separator;
		FileUtils.createDirectory(apkDir);
		return apkDir;
	}

	/**
	 * 录音地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getAudioDir(Context context) {
		String recordDir = FileUtils.getRootDir(context) + AUDIO + File.separator;
		FileUtils.createDirectory(recordDir);
		return recordDir;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param fileDir
	 * @return
	 */
	public static boolean createDirectory(String fileDir) {
		if (fileDir == null) {
			return false;
		}

		File file = new File(fileDir);

		if (file.exists()) {
			return true;
		}

		return file.mkdirs();
	}

	/**
	 * 删除文件
	 * 
	 * @param fileDir
	 * @return
	 */
	public static boolean deleteDirectory(String fileDir) {
		if (fileDir == null) {
			return false;
		}

		File file = new File(fileDir);

		if (file == null || !file.exists()) {
			return false;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i].getAbsolutePath());
				} else {
					files[i].delete();
				}
			}
		}

		file.delete();
		return true;
	}

	/**
	 * 获取文件名
	 * 
	 * @param filename
	 *            文件完整路径名
	 * @return 去除路径的文件名
	 */
	public static String getSimpleName(String filename) {
		final int index = filename.lastIndexOf('/');
		if (index == -1) {
			return filename;
		} else {
			return filename.substring(index + 1);
		}
	}

	/**
	 * 是否为相同文件(只是比较另一个文件大小)
	 * 
	 * @param file
	 *            文件名
	 * @param size
	 *            另一个文件大小
	 * @return
	 */
	public static boolean isSameFile(File file, long size) {
		boolean isSameFile = false;
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				long tmpSize = fis.available();// 读取文件长度
				if (tmpSize == size) {
					isSameFile = true;
				} else {
					file.delete();
				}
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isSameFile;
	}
}
