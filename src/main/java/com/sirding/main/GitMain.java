package com.sirding.main;

import java.util.Scanner;

import com.sirding.model.Config;
import com.sirding.model.GlobalConfig;
import com.sirding.service.AutomateService;
import com.sirding.service.LogMsg;
import com.sirding.util.SftpUtil;
import com.sirding.util.mail.MailUtil;

public class GitMain {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		try {
			String sendMail = "0";
			String title = "未定义";
			String ope = "";
			String sendEmailMsg = "";
			while(true){
				System.out.println("是否发送邮件通知运维?	0:否	1:是");
				String msg = scan.next();
				if("0".equals(msg) || "1".equals(msg)){
					sendMail = msg;
					break;
				}
				if(isBreak(msg)){
					return;
				}
			}
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
			
			while(true){
				System.out.println("请输入如下参数：");
				System.out.println("\t git \t统计更新的文件列表、保存到fileList.txt文件中\n");
				System.out.println("\t copy\t将fileList.txt中更新的文件保存upload文件，以便上传使用\n");
				System.out.println("\t download\t将fileList.txt中更新的文件从远程服务器上下载下来作为备份文件\n");
				System.out.println("\t upload\t将uplate中更新的文件上传至服务器\n");
				System.out.println("\t all 包含上述git、copy、download、upoad操作\n");
				System.out.println("\t quit	退出循环\n");
				String msg = scan.next();
				if("git".equalsIgnoreCase(msg) || "all".equalsIgnoreCase(msg)){
					//统计更新的文件列表保存到fileList.txt文件在中
					auto.getOptionFileInfo(gconfig, config);
				}
				if("copy".equalsIgnoreCase(msg) || "all".equalsIgnoreCase(msg)){
					//在upload文件中创建要升级的文件结构目录
					sendEmailMsg = auto.initUploadFiles(config);
				}
				if("download".equalsIgnoreCase(msg) || "all".equalsIgnoreCase(msg)){
					//将要更新的文件列表内容从远程服务器下载下来作为备份使用
					auto.initDownloadFiles(config);
				}
				if("upload".equalsIgnoreCase(msg) || "all".equalsIgnoreCase(msg)){
					//更新文件执上传操作
					auto.uploadOper(config);
				}
				if(isBreak(msg) || "all".equalsIgnoreCase(msg)){
					ope = "all";
					break;
				}
			}
			//断开与服务器连接
			SftpUtil.disconnected();
			//发送邮件
			if("all".equalsIgnoreCase(ope) && "1".equalsIgnoreCase(sendMail)){
				MailUtil mailUtil = new MailUtil(
						gconfig.getEmailUserName(),
						gconfig.getEmailPwd(),
						gconfig.getEmailHost()
						);
				boolean success = mailUtil.sendMail(
						gconfig.getFromEmail(),
						gconfig.getToEmail().split(","), 
						title, 
						sendEmailMsg);
				if(success){
					System.out.println("已成功向" + gconfig.getToEmail() + "发送邮件!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			scan.close();
		}
	}
	
	static boolean isBreak(String msg){
		if("quit".equalsIgnoreCase(msg)){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 展示提示信息
	 * @author zc.ding
	 * @date 2016年10月16日
	 */
	static void showTs(){
		System.out.println("usage: \tjava -jar java-ssd.jar [option1] [option2]\n");
		System.out.println("option1:\n");
		System.out.println("\t1、mvn，执行mvn mybatis-generator:generte命令\n");
		System.out.println("\t2、git，统计更新文件列表\n");
		System.out.println("option2:(此参数只有option1是git才有意义)\n");
		System.out.println("\t git \t统计更新的文件列表、保存到fileList.txt文件中\n");
		System.out.println("\t copy\t将fileList.txt中更新的文件保存upload文件，以便上传使用\n");
		System.out.println("\t download\t将fileList.txt中更新的文件从远程服务器上下载下来作为备份文件\n");
		System.out.println("\t upload\t将uplate中更新的文件上传至服务器\n");
		System.out.println("\t all 包含上述git、copy、download、upoad操作\n");

	}
}
