package com.sirding.main;

import com.sirding.model.Config;
import com.sirding.model.GlobalConfig;
import com.sirding.service.AutomateService;

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
				System.out.println("找不到【global】配置信息");
				return;
			}
			Config config = auto.loadConfig(gconfig.getRunSec());
			if(config == null){
				System.out.println("找不到【" + gconfig.getRunSec() + "】配置");
				return;
			}
			//初始化项目路径
			auto.initPath(gconfig, config);
			
			//实例化sshd
			String flag = auto.initSftp(gconfig);
			if("0".equals(flag)){
				System.out.println("不能连接到远程服务器");
				return;
			}
			
			//统计更新的文件列表保存到fileList.txt文件在中
			String projectPath = config.getProjectPath();
			String commitId = config.getCommitId();
			auto.getOptionFileInfo(projectPath, commitId);
			
			//在upload文件中中创建要升级的文件结构目录
			auto.initUploadFiles(config);
			
			//将要更新的文件列表内容从远程服务器下载下来作为备份使用
			auto.initDownloadFiles(config);
			
			//更新文件执上传操作
//			auto.uploadOper(config);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
