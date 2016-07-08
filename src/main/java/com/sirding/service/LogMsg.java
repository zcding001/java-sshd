package com.sirding.service;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

import com.sirding.model.Config;
import com.sirding.model.GlobalConfig;

public class LogMsg {

	private static final Logger logger = Logger.getLogger(LogMsg.class);
	
	private static String LOG_PATH = "";
	public static String SEP = "========================================================\n";
	
	public static void initPath(String logPath){
		LOG_PATH = logPath;
	}
	
	/**
	 * 
	 * @param msg
	 * @date 2016年7月8日
	 * @author zc.ding
	 */
	public static void saveMsg(String msg){
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LOG_PATH, true)));
			bw.write(msg);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void showMsg(String msg){
		System.out.println(msg);
	}
	
	public static void showGlobalConfig(GlobalConfig gconfig){
		logger.debug("===========全局配置配置信息=======");
		logger.debug("ip:" + gconfig.getIp() + ":" + gconfig.getPort());
		logger.debug("userName:" + gconfig.getUserName() + "/******");
		logger.debug("要应用的配置节点：" + gconfig.getRunSec() );
		logger.debug("===========全局配置配置信息=======");
	}
	
	public static void showConfig(Config config){
		logger.debug("==================应用配置信息=================");
		logger.debug("tomca位置:\t" + config.getLocalTomcatPath());
		logger.debug("git项目位置：\t");
		logger.debug("==================应用配置信息=================");
	}		
}
