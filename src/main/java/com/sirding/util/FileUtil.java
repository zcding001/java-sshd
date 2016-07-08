package com.sirding.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
/**
 * 文件工具类
 * @author zc.ding
 * @date 2016年7月8日
 */
public class FileUtil {

	public static void mkdir(String directory){
		File file = new File(directory);
		file.mkdirs();
	}

	public static void copyFile(String src, String dst){
		File srcFile = new File(src);
		File dstFile = new File(dst);
		mkdir(dstFile.getParent());
		if(!srcFile.isFile() || !srcFile.exists()){
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
	
	public static void main(String[] args) {
		String path = "C:\\yrtz\\test\\automate\\data\\20160707_1\\upload\\WEB-INF\\classes\\com\\sirding\\test\\Demo1.class";
		File file = new File(path);
		System.out.println(file.getParentFile().mkdirs());
	}
}
