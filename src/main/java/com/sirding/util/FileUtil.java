package com.sirding.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.sirding.service.LogMsg;
/**
 * 文件工具类
 * @author zc.ding
 * @date 2016年7月8日
 */
public class FileUtil {

	/**
	 * 创建文件目录
	 * @param directory
	 * @author zc.ding
	 * @date 2016年7月10日
	 */
	public static void mkdir(String directory){
		File file = new File(directory);
		file.mkdirs();
	}

	/**
	 * 拷贝文件
	 * @param src
	 * @param dst
	 * @author zc.ding
	 * @date 2016年7月10日
	 */
	public static void copyFile(String src, String dst){
		File srcFile = new File(src);
		File dstFile = new File(dst);
		mkdir(dstFile.getParent());
		if(!srcFile.isFile() || !srcFile.exists()){
			LogMsg.saveMsg("找不到【" + srcFile.getAbsolutePath()+ "】\n");
			return;
		}
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(src);
			fo = new FileOutputStream(dst);
			in = fi.getChannel();//得到对应的文件通道
			out = fo.getChannel();//得到对应的文件通道
			in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 拷贝文件夹
	 * @param src
	 * @param dst
	 * @author zc.ding
	 * @date 2016年7月10日
	 */
	public static void copyFolder(String src, String dst){
		src = src.replaceAll("\\\\", "/");
		dst = dst.replaceAll("\\\\", "/");
		File srcFile = new File(src);
		File dstFile = new File(dst);
		if(srcFile.isDirectory()){
			mkdir(dstFile.getAbsolutePath());
			File[] files = srcFile.listFiles();
			if(files.length > 0){
				for(File file : files){
					if(file.isDirectory()){
						copyFolder(file.getAbsolutePath(), file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(src, dst));
					}
					copyFile(file.getAbsolutePath(), file.getAbsolutePath().replaceAll("\\\\", "/").replaceAll(src, dst));
				}
			}
		}else{
			copyFile(src, dst);
		}
	}
	
	public static void main(String[] args) {
		String path = "C:\\yrtz\\test\\automate\\data\\20160707_1\\upload\\WEB-INF\\classes\\com\\sirding\\test\\Demo1.class";
//		File file = new File(path);
//		System.out.println(file.getParentFile().mkdirs());
		
		System.out.println(path.replaceAll("\\\\", "/"));
	}
}
