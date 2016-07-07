package com.sirding.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.sirding.model.Config;
import com.sirding.model.FileInfo;
import com.sirding.model.GlobalConfig;
import com.sirding.singleton.IniTool;
import com.sirding.util.SftpUtil;

public class AutomateService {

	private String filePath = "C:\\yrtz\\test\\automate\\config.ini";
	private IniTool iniTool;
	private Logger logger = Logger.getLogger(AutomateService.class);

	//diff --stat --name-status
	private String gitDiff = "staus --short";
	private String gitDiffCommitId = "diff-tree -r --name-status --no-commit-id SHA1 SHA2";
	
	public AutomateService(){
		iniTool = IniTool.newInstance();
	}

	/**
	 * 加载globalconfig配置
	 * @return
	 * @throws Exception
	 * @date 2016年7月7日
	 * @author zc.ding
	 */
	public GlobalConfig loadGlobalConfig() throws Exception{
		GlobalConfig gconfig = iniTool.loadSingleSec(GlobalConfig.class, filePath, true, "global");
		if(gconfig == null){
			logger.debug("找不到【global】配置");
			return null;
		}
		return gconfig;
	}

	/**
	 * 加载config配置信息
	 * @param runSec
	 * @return
	 * @throws Exception
	 * @date 2016年7月7日
	 * @author zc.ding
	 */
	public Config loadConfig(String runSec) throws Exception{
		Config config = iniTool.loadSingleSec(Config.class, filePath, true, runSec);
		if(config == null){
			logger.debug("找不到【" + runSec + "】配置");
			return config;
		}
		return config;
	}

	/**
	 * 初始化sshd连接
	 * @param gconfig
	 * @return
	 * @date 2016年7月7日
	 * @author zc.ding
	 */
	public ChannelSftp initSftp(GlobalConfig gconfig){
		String host = gconfig.getIp();
		int port = Integer.parseInt(gconfig.getPort());
		String userName = gconfig.getUserName();
		String password = gconfig.getPwd();
		String privateKey = gconfig.getPrivateKey();
		String passphrase = gconfig.getPassphrase();
		ChannelSftp sftp = SftpUtil.initSftp(host, port, userName, password, privateKey, passphrase);
		if(sftp == null){
			logger.debug("SSHD连接初始化失败");
		}
		return sftp;
	}

	/**
	 * 获得更新的文件列表
	 * @param projectPath
	 * @param commitId
	 * @return
	 * @date 2016年7月7日
	 * @author zc.ding
	 */
	public List<FileInfo> getOptionFileInfo(String projectPath, String commitId) {
		List<FileInfo> list = new ArrayList<FileInfo>();
		String cmd = "git -C " + projectPath + " " + gitDiff;
		if(commitId != null && commitId.length() > 0){
			String[] arr = commitId.split(",");
			cmd = gitDiffCommitId.replaceAll("SHA1", arr[0]).replaceAll("SHA2", arr[1]);
		}
		try {
			Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象 
			logger.debug("执行的cmd命令:" + cmd);
			Process p = run.exec(cmd);// 启动另一个进程来执行命令  
			BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()))); 
			String line = null;
			while((line = br.readLine()) != null){
				if(line.length() > 0 && (line.startsWith("M") || 
						line.startsWith("A") || line.startsWith("D") ||
						line.startsWith("?"))){
					String[] arr = line.split(" +");
					FileInfo fileInfo = new FileInfo(arr[0], arr[1]);
					list.add(fileInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
