package com.sirding.main;

import java.util.List;

import com.jcraft.jsch.ChannelSftp;
import com.sirding.model.Config;
import com.sirding.model.FileInfo;
import com.sirding.model.GlobalConfig;
import com.sirding.service.AutomateService;
import com.sirding.service.LogMsg;
/**
 * 通过sshd自动化更新工具类
 * @author zc.ding
 * @date 2016年7月7日
 */
public class Automate {
	
	public static void main(String[] args) {
		try {
			AutomateService auto = new AutomateService();
			GlobalConfig gconfig = auto.loadGlobalConfig();
			if(gconfig == null){
				LogMsg.showMsg("找不到【global】配置信息");
			}
			Config config = auto.loadConfig(gconfig.getRunSec());
			if(config == null){
				LogMsg.showMsg("找不到【" + gconfig.getRunSec() + "】配置");
			}
			ChannelSftp sftp = auto.initSftp(gconfig);
			if(sftp == null){
				LogMsg.showMsg("不能实例化sshd连接");
			}
			
			//统计升级文件的列表
			String projectPath = config.getProjectPath();
			String commitId = config.getCommitId();
			List<FileInfo> list = auto.getOptionFileInfo(projectPath, commitId);
			if(list != null){
				
			}
			
			String remoteTomcatPath = config.getRemoteTomcatPath();
			String localTomcatPath = config.getLocalTomcatPath();
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
