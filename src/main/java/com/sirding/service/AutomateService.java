package com.sirding.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.sirding.model.Config;
import com.sirding.model.GlobalConfig;
import com.sirding.singleton.IniTool;
import com.sirding.util.FileUtil;
import com.sirding.util.SftpUtil;

public class AutomateService {
	private String path = "";
	private String rootPath = "";
	private String uploadPath = "";
	private String downloadPath = "";
	private String fileListPath = "";
	private String filePath = "";
	private IniTool iniTool;
	
	private Logger logger = Logger.getLogger(AutomateService.class);

	//diff --stat --name-status
	private String gitDiff = "status --short";
	private String gitDiffCommitId = "diff-tree -r --name-status --no-commit-id SHA1 SHA2";
	
	public AutomateService(){
		iniTool = IniTool.newInstance();
		path = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/";
		System.out.println("工作路径：" + path);
//		filePath = "C:\\yrtz\\test\\automate\\config.ini";
		filePath = path + "config.ini";
	}
	
	/**
	 * 
	 * @param gconfig
	 * @param config
	 * @date 2016年7月8日
	 * @author zc.ding
	 */
	public void initPath(GlobalConfig gconfig, Config config){
		StringBuffer sb = new StringBuffer(LogMsg.SEP);
		String date = gconfig.getDate();
		String index = gconfig.getIndex();
		if("1".equals(gconfig.getAutoUpdateIndex()) && new File(rootPath).isDirectory() && new File(rootPath).exists()){
			index = Integer.parseInt(index) + 1 + "";
		}
		String currDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		if(!date.equals(currDate)){
			date = currDate;
			index = "1";
		}
//		rootPath = "C:/yrtz/test/automate/data/" + date + "_" + index;
		rootPath = path + "/data/" + date + "_" + index;
		//创建用户上传的文件夹
		uploadPath = rootPath + "/upload";
		FileUtil.mkdir(uploadPath);
		//创建用于存储远程服务器下载回来的文件
		downloadPath = rootPath + "/download";
		FileUtil.mkdir(downloadPath);
		//初始化用于存储升级文件列表的文件
		fileListPath = rootPath + "/" + "fileList.txt";
		LogMsg.initPath(rootPath + "/log.txt");
		try {
			//更新升级日期及索引
			if("1".equals(gconfig.getAutoUpdateIndex())){
				gconfig.setIndex(Integer.parseInt(index) + 1 + "");
			}
			gconfig.setDate(date);
			iniTool.saveSec(gconfig, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.append("第[" + index + "]次升级\n");
		sb.append("root:\t" + rootPath + "\n");
		sb.append("uploadPath:\t" + uploadPath + "\n");
		sb.append("downloadPath:\t" + downloadPath + "\n");
		sb.append("log_path:\t" + rootPath + "/" + "log.txt\n");
		sb.append("fileListPath:\t" + fileListPath + "\n");
		sb.append(LogMsg.SEP + "\n");
		LogMsg.saveMsg(sb.toString());
	}

	/**
	 * 加载globalconfig配置
	 * @return
	 * @throws Exception
	 * @date 2016年7月7日
	 * @author zc.ding
	 */
	public GlobalConfig loadGlobalConfig() throws Exception{
		return iniTool.loadSingleSec(GlobalConfig.class, filePath, true, "global");
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
		return iniTool.loadSingleSec(Config.class, filePath, true, runSec);
	}

	/**
	 * 获得更新的文件列表
	 * @param projectPath
	 * @param commitId
	 * @return
	 * @date 2016年7月7日
	 * @author zc.ding
	 */
	public void getOptionFileInfo(GlobalConfig gconfig, Config config) {
		String projectPath = config.getProjectPath();
		String commitId = config.getCommitId();
		StringBuffer sb = new StringBuffer("\n" + "统计更新文件列表" + LogMsg.SEP);
		String cmd = "git -C " + projectPath + " " + gitDiff;
		if(commitId != null && commitId.length() > 0){
			String[] arr = commitId.split(",");
			cmd = "git -C " + projectPath + " " + gitDiffCommitId.replaceAll("SHA1", arr[0]).replaceAll("SHA2", arr[1]);
		}
		try {
			Runtime run = Runtime.getRuntime();//返回与当前 Java 应用程序相关的运行时对象 
			logger.debug("执行的cmd命令:" + cmd);
			sb.append(cmd + "\n");
			Process p = run.exec(cmd);// 启动另一个进程来执行命令  
			BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()))); 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileListPath)));
			String line = null;
			while((line = br.readLine()) != null){
				sb.append("git ").append(line).append("\t");
				String[] arr = line.trim().split(" +");
				if(arr.length != 2){
					continue;
				}
				line = arr[1];
				//处理xml,java
				line = line.replaceAll("src/source|src/java", "/WEB-INF/classes");
				line = line.replaceAll("src/main/resources|src/main/java", "/WEB-INF/classes");
				if(line.startsWith("src/")){
					line = line.replaceAll("src/", "/WEB-INF/classes/");
				}
				if(line.endsWith(".java")){
					line = line.replaceAll(".java", ".class");
				}
				//替换ftl,css文件
				line = line.replaceAll("WebRoot", "");
				String suffix = gconfig.getSuffix();
				if(suffix != null && suffix.length() > -1){
					String[] s = suffix.split(",");
					if(s != null){
						for(String tmp : s){
							if(line.indexOf(tmp) > -1){
								continue;
							}
						}
					}
				}
				bw.write(line);
				bw.write("\n");
				sb.append(line + "\n");
			}
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogMsg.saveMsg(sb.toString());
	}
	
	/**
	 * 初始化上传文件结构列表
	 * @param config
	 * @date 2016年7月8日
	 * @author zc.ding
	 */
	public void initUploadFiles(Config config){
		LogMsg.saveMsg("\n" + "准备要更新的文件" + LogMsg.SEP);
		StringBuffer sb = new StringBuffer();
		String localTomcatPath = config.getLocalTomcatPath();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileListPath))); 
			String line = null;
			while((line = br.readLine()) != null){
				String src = "";
				String dst = "";
				if(!line.startsWith("/")){
					src = localTomcatPath + "/" + line;
					dst = uploadPath + "/" + line;
				}else{
					src = localTomcatPath + line;
					dst = uploadPath + line;
				}
				if(new File(src).isFile()){
					FileUtil.copyFile(src, dst);
				}else{
					FileUtil.copyFolder(src, dst);
				}
				sb.append(src + "   ===>   " + dst + "\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogMsg.saveMsg(sb.toString());
	}
	
	/**
	 * 
	 * @param config
	 * @date 2016年7月8日
	 * @author zc.ding
	 */
	public void initDownloadFiles(Config config){
		StringBuffer sb = new StringBuffer("\n" + "下载要备份的文件" + LogMsg.SEP);
		String remoteTomcatPath = config.getRemoteTomcatPath();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileListPath))); 
			String line = null;
			while((line = br.readLine()) != null){
				String src = "";
				String dst = "";
				if(!line.startsWith("/")){
					src = remoteTomcatPath + "/" + line;
					dst = downloadPath + "/" + line;
				}else{
					src = remoteTomcatPath + line;
					dst = downloadPath + line;
				}
				if(src.endsWith("\\\\") || src.endsWith("/")){
					continue;
				}
				SftpUtil.download(src, dst);
				//记录下载文件的列表
				sb.append(src + "    ===download==>   " + dst + "\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogMsg.saveMsg(sb.toString());
	}
	
	public String uploadOper(Config config){
		File file = new File(uploadPath);
		String remoteTomcatPath = config.getRemoteTomcatPath();
		StringBuffer sb = new StringBuffer("\n已上传至服务器的文件列表" + LogMsg.SEP);
		recursion(file, remoteTomcatPath, sb);
		//将sb写入到log.txt文件中
		LogMsg.saveMsg(sb.toString());
		return "";
	}

	/**
	 * 
	 * @param file
	 * @param remoteTomcatPath
	 * @param sb
	 * @date 2016年7月8日
	 * @author zc.ding
	 */
	private void recursion(File file, String remoteTomcatPath, StringBuffer sb){
		File[] files = file.listFiles();
		if(files != null){
			for(File tmp : files){
				if(tmp.isDirectory()){
					recursion(tmp, remoteTomcatPath, sb);
				}else{
					String src = tmp.getAbsolutePath().replaceAll("\\\\", "/");
					String dst = src.replaceAll(uploadPath.replaceAll("\\\\", "/"), remoteTomcatPath.replaceAll("\\\\", "/"));
					try {
						SftpUtil.upload(src, dst);
						sb.append("ok\t" + src + " ==> " + dst).append("\n");
					} catch (Exception e) {
						sb.append("fail\t" + src + " ==> " + dst).append("\n");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 重启tomcat
	 * @param config
	 * @author zc.ding
	 * @date 2016年7月10日
	 */
	public void restartTomcat(Config config){
		String remoteTomcatPath = config.getRemoteTomcatPath();
		String shPath = new File(remoteTomcatPath).getParentFile().getParent() + "/bin/";
		String stopCmd = "/bin/sh " + shPath + "shutdown.sh";
		String startCmd = "/bin/sh " + shPath + "start.sh";
		System.out.println(stopCmd.replaceAll("\\\\", "/"));
		System.out.println(startCmd.replaceAll("\\\\", "/"));
		SftpUtil.exec(stopCmd.replaceAll("\\\\", "/"));
		SftpUtil.exec(startCmd.replaceAll("\\\\", "/"));
	}
}
