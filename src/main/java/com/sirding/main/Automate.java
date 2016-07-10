package com.sirding.main;

import com.sirding.model.Config;
import com.sirding.model.GlobalConfig;
import com.sirding.service.AutomateService;
import com.sirding.service.LogMsg;
import com.sirding.util.SftpUtil;

/**
 * 通过sshd自动化更新工具类
 * @author zc.ding
 * @date 2016年7月7日
 */
public class Automate {
	
	public static void main(String[] args) {
		try {
			if(args != null && args.length > 0){
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
				String host = gconfig.getIp();
				int port = Integer.parseInt(gconfig.getPort());
				String userName = gconfig.getUserName();
				String password = gconfig.getPwd();
				String privateKey = gconfig.getPrivateKey();
				String passphrase = gconfig.getPassphrase();
				String flag = SftpUtil.initSftp(host, port, userName, password, privateKey, passphrase);
				if("0".equals(flag)){
					System.out.println("Fail sshd连接建立失败" + LogMsg.SEP);
					return;
				}
				System.out.println("OK sshd连接已正常建立" + LogMsg.SEP);
				String option = args[0];
				if("git".equalsIgnoreCase(option) || "all".equalsIgnoreCase(option)){
					//统计更新的文件列表保存到fileList.txt文件在中
					auto.getOptionFileInfo(gconfig, config);
				}
				if("copy".equalsIgnoreCase(option) || "all".equalsIgnoreCase(option)){
					//在upload文件中创建要升级的文件结构目录
					auto.initUploadFiles(config);
				}
				if("download".equalsIgnoreCase(option) || "all".equalsIgnoreCase(option)){
					//将要更新的文件列表内容从远程服务器下载下来作为备份使用
					auto.initDownloadFiles(config);
				}
				if("upload".equalsIgnoreCase(option) || "all".equalsIgnoreCase(option)){
					//更新文件执上传操作
					auto.uploadOper(config);
				}
//				if("restart".equalsIgnoreCase(option)){
//					//重启tomcat
//					auto.restartTomcat(config);
//				}
				//断开与服务器连接
				SftpUtil.disconnected();
			}else{
				System.out.println("usage: \tjava -jar java-ssd.jar [options]\n");
				System.out.println("options:\n");
				System.out.println("\t git \t统计更新的文件列表、保存到fileList.txt文件中\n");
				System.out.println("\t copy\t将fileList.txt中更新的文件保存upload文件，以便上传使用\n");
				System.out.println("\t download\t将fileList.txt中更新的文件从远程服务器上下载下来作为备份文件\n");
				System.out.println("\t upload\t将uplate中更新的文件上传至服务器\n");
				System.out.println("\t all 包含上述git、copy、download、upoad操作\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
