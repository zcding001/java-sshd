package com.sirding.ini;


import com.sirding.model.Config;
import com.sirding.model.GlobalConfig;
import com.sirding.service.AutomateService;
import com.sirding.singleton.IniTool;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class ConfigTest {

	private static String filePath;
	private static IniTool iniTool;
	private Logger logger = Logger.getLogger(ConfigTest.class);
	
	@BeforeClass
	public static void init(){
		filePath = ConfigTest.class.getResource("/").toString() + "config.ini";
		filePath = filePath.replaceFirst("file:/", "");
		iniTool = IniTool.newInstance();
	}
	
	@Test
	public void loadConfig(){
		try {
			GlobalConfig gconfig = iniTool.loadSingleSec(GlobalConfig.class, filePath, true, "global");
			if(gconfig == null){
				logger.debug("找不到【global】配置");
				return;
			}
			String runSec = gconfig.getRunSec();
			logger.debug("===========全局配置配置信息=======");
			logger.debug("ip:" + gconfig.getIp() + ":" + gconfig.getPort());
			logger.debug("userName:" + gconfig.getUserName() + "/******");
			logger.debug("要应用的配置节点：" + runSec );
			logger.debug("===========全局配置配置信息=======");
			Config config = iniTool.loadSingleSec(Config.class, filePath, true, runSec);
			if(config == null){
				logger.debug("找不到【" + runSec + "】配置");
				return;
			}
			logger.debug("==================应用配置信息=================");
			logger.debug("tomca位置:\t" + config.getLocalTomcatPath());
			logger.debug("git项目位置：\t");
			logger.debug("==================应用配置信息=================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2(){
		String[] cmd = new String[]{"cmd.exe", "/C", "git -C C:/yrtz/git/github/demo-web status --short"};
//		String[] cmd = new String[]{"cmd.exe", "/C", "java -version"};
		System.out.println( "CLASSPATH = " + System.getenv("CLASSPATH"));
		System.out.println( "CLASSPATH = " + System.getenv("Path"));
		try {
			Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象 
			logger.debug("执行的cmd命令:" + cmd);
			Process p = run.exec(cmd);//启动另一个进程来执行命令  
			BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()), "GBK")); 
			BufferedReader error = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getErrorStream()), "GBK")); 
			String line = null;
			while((line = br.readLine()) != null){
				logger.debug(line);
			}
			while((line = error.readLine()) != null){
				logger.debug(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void test3(){
//		try {
//			AutomateService auto = new AutomateService();
//			GlobalConfig gconfig = auto.loadGlobalConfig();
//			if(gconfig == null){
//				LogMsg.showMsg("找不到【global】配置信息");
//			}
//			Config config = auto.loadConfig(gconfig.getRunSec());
//			if(config == null){
//				LogMsg.showMsg("找不到【" + gconfig.getRunSec() + "】配置");
//			}
//			String flag = auto.initSftp(gconfig);
//			if(!flag.equals("ok")){
//				LogMsg.showMsg("不能实例化sshd连接");
//			}
//			String src = "/data/www/temp/zcding/a.txt";
//			String dst = "C:\\yrtz\\test\\cc\\downFile.txt";
//			SftpUtil.download(src, dst);
//			SftpUtil.download("/data/www/temp/zcding/a.txt", "C:\\yrtz\\test\\a.txt");
//			SftpUtil.download("/data/www/temp/zcding/b.txt", "C:\\yrtz\\test\\b.txt");
//			String path = "/data/www/temp/zcding/aa";
//			SftpUtil.mkdirPath(path);
//			SftpUtil.exec("mkdir -p /data/www/temp/zcding/aa/bb/cc");
//			SftpUtil.exec("mkdir -p /data/www/temp/zcding/cc/bb/cc");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	@Test
	public void test4(){
		try {
			GlobalConfig gconfig = iniTool.loadSingleSec(GlobalConfig.class, filePath, true, "global");
			gconfig.setIndex("100");
			iniTool.saveSec(gconfig, filePath);
			
			Config config = iniTool.loadSingleSec(Config.class, filePath, true, "tomcat1");
			config.setCommitId("1000");
			iniTool.saveSec(config, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test5(){
		try {
			Config config = iniTool.loadSingleSec(Config.class, filePath, true, "tomcat1");
			AutomateService auto = new AutomateService();
			auto.restartTomcat(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
