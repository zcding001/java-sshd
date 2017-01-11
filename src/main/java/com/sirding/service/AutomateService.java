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
import com.sirding.model.Generator;
import com.sirding.model.GlobalConfig;
import com.sirding.singleton.IniTool;
import com.sirding.util.FileUtil;
import com.sirding.util.SftpUtil;

public class AutomateService {
	private static final String FILE_LIST_SEQ = "<=####=>";
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
		path = replaceSeq(System.getProperty("user.dir")) + "/";
//		path = "C:/yrtz/test/java-sshd-0.0.1-SNAPSHOT-bin/";
//		path = "C:/yrtz/test/java-sshd-2017/";
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
		rootPath = path + "data/" + date + "_" + index;
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
//		List<String> gitList = new ArrayList<String>();
//		gitList.add("需要记录到wiki中的文件列表-start" + LogMsg.SEP);
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
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileListPath), "UTF-8"));
			bw.write("===========git中更新的文件列表============START========\n");
			String line = null;
			int index = 0;
			outter:
				while((line = br.readLine()) != null){
					//line eg: M src/main/java/com/qiankundai/web/controller/UserController.java
					sb.append("git ").append(line).append("\n");
					String[] arr = line.trim().split("\\s+");
					if(arr.length != 2){
						continue;
					}
					line = arr[1];
					//黑名单处理特殊文件
					String blackKeyPattern = config.getBlackKeyPattern();
					if(blackKeyPattern != null && blackKeyPattern.length() > 0){
						String[] key = blackKeyPattern.split(",");
						if(key != null){
							for(String tmp : key){
								if(line.indexOf(tmp) > -1){
									continue outter;
								}
							}
						}
					}
					String srcFile = replaceKey(line, config.getReplaceSrc());
					String dstFile = replaceKey(line, config.getReplaceDst());
//					gitList.add(dstFile);
					bw.write(srcFile + FILE_LIST_SEQ + dstFile);
					bw.write("\n");
					sb.append(FILE_LIST_SEQ + srcFile + "\n");
					index++;
				}
			bw.write("===========git中更新的文件列表=============END=======\n\n");
//			gitList.add("===========需要记录到wiki中的文件列表-end====================");
//			for(String tmp : gitList){
//				bw.write(tmp);
//				bw.write("\n");
//			}
			bw.flush();
			bw.close();
			if(index == 0){
				sb.append("\nCan not find the updated list of files.[找不到更新的文件列表]\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogMsg.saveMsg(sb.toString());
	}
	
	/**
	 * 根据replacePattern替换line中的内容
	 * @param line
	 * @param replacePattern
	 * @return
	 * @date 2016年8月4日
	 * @author zc.ding
	 */
	private String replaceKey(String line, String replacePattern){
		if(replacePattern != null && replacePattern.length() > 0){
			String[] oldNewArr = replacePattern.split(",");
			if(oldNewArr != null){
				for(String tmp : oldNewArr){
					if(tmp != null){
						String[] oldNew = tmp.split("_");
						if(oldNew == null || oldNew.length < 2){
							continue;
						}
						String old = oldNew[0];
						String New = oldNew[1];
						line = line.replaceAll(old, "/".equals(New)?"":New);
					}
				}
			}
		}
		if(line.endsWith(".java")){
			line = line.replaceAll("\\.java", ".class");
		}
		return line;
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
		StringBuffer sb2 = new StringBuffer();
		String localTomcatPath = config.getLocalTomcatPath();
		//先清空历史文件
		FileUtil.delFolder(uploadPath);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileListPath))); 
			String line = null;
			while((line = br.readLine()) != null){
				if(line.indexOf(FILE_LIST_SEQ) < 0){
					continue;
				}
				String src = line.split(FILE_LIST_SEQ)[0];
				if(!src.startsWith("/")){
					src = localTomcatPath + "/" + src;
				}else{
					src = localTomcatPath + src;
				}
				String dst = line.split(FILE_LIST_SEQ)[1];
				if(!dst.startsWith("/")){
					dst = uploadPath + "/" + dst;
				}else{
					dst = uploadPath + dst;
				}
				if(new File(src).isFile()){
					FileUtil.copyFile(src, dst);
					//判断文件是不是含有内部类
					hasInnerClasses(src, dst, sb, sb2);
				}else{
					FileUtil.copyFolder(src, dst);
				}
//				sb.append(src + "   ===>   " + dst + "\n");
				sb.append(src + FILE_LIST_SEQ + dst + "\n");
				sb2.append(dst + "\n");
			}
			br.close();
			//生成用于升级的文件列表
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileListPath, true), "UTF-8"));
			bw.write("===========用于升级的文件列表============START=================\n");
			bw.write(sb2.toString().replaceAll(uploadPath, ""));
			bw.write("===========用于升级的文件列表============END=================\n");
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogMsg.saveMsg(sb.toString());
	}
	
	/**
	 * 赋值内部类
	 * @param srcFile
	 * @param dstFile
	 * @param sb
	 * @param sb2
	 * @date 2016年8月24日
	 * @author zc.ding
	 */
	private static void hasInnerClasses(String srcFile, String dstFile, StringBuffer sb, StringBuffer sb2){
		if(!srcFile.endsWith(".class")){
			return;
		}
		File file = new File(srcFile);
		srcFile = srcFile.replaceAll("\\.class", "") + "$";
		File pathFile = file.getParentFile();
		File[] arr = pathFile.listFiles();
		if(arr != null){
			for(File tmp : arr){
				String filePath = replaceSeq(tmp.getAbsolutePath());
				if(filePath.indexOf(srcFile) > -1){
					String dstFilePath = replaceSeq(new File(dstFile).getParent()) + "/" + tmp.getName();
					FileUtil.copyFile(filePath, dstFilePath);
					sb.append("内部类：" + filePath + "   ===>   " + dstFilePath + "\n");
					sb2.append(dstFilePath + "\n");
				}
			}
		}
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
	
	/**
	 * 
	 * @param config
	 * @return
	 * @date 2016年8月3日
	 * @author zc.ding
	 */
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
					String src = replaceSeq(tmp.getAbsolutePath());
					String dst = src.replaceAll(replaceSeq(uploadPath), replaceSeq(remoteTomcatPath));
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
		System.out.println(replaceSeq(stopCmd));
		System.out.println(replaceSeq(startCmd));
		SftpUtil.exec(replaceSeq(stopCmd));
		SftpUtil.exec(replaceSeq(startCmd));
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @date 2016年8月4日
	 * @author zc.ding
	 */
	private static String replaceSeq(String filePath){
		return filePath.replaceAll("\\\\", "/");
	}
	
	
	/**
	 * 加载Genrator配置信息
	 * @return
	 * @throws Exception
	 * @author zc.ding
	 * @date 2016年10月15日
	 */
	public Generator getGenerator() throws Exception{
		return iniTool.loadSingleSec(Generator.class, filePath, true, "generator");
	}
	
	/**
	 * 执行mvn mybatis-generator:generator
	 * @param obj
	 * @throws Exception
	 * @author zc.ding
	 * @date 2016年10月16日
	 */
	public void mvnGenerator(Generator obj) throws Exception{
		this.resetGeneratorConfig(obj);
		String tableList = obj.getTableList();
		String pojoList = obj.getPojoList();
		String[] tableArr = tableList.split(",");
		String[] pojoArr = pojoList.split(",");
		String generatorPath = path + obj.getGeneratorPath();
		String oldTemplate = obj.getOldTemplate();
		String cmd = "cmd exe /c mvn -f " + obj.getPomPath() + " mybatis-generator:generate";
		String tableName = "TABLE_NAME";
		String pojoName = "POJO_NAME";
		for(int i = 0; i < tableArr.length; i++){
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(generatorPath), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while((line = br.readLine()) != null){
				if(line.indexOf(oldTemplate) > -1){
					//对模板中的表名称及实体类名称进行替换操作
					line = line.replaceAll(tableName, tableArr[i].trim()).replaceAll(pojoName, pojoArr[i].trim());
					tableName = tableArr[i];
					pojoName = pojoArr[i];
					oldTemplate = line;
				}
				sb.append(line).append("\n");
			}
			br.close();
			//将更新的内容重新回写到文件中
			this.saveDataToFile(sb, generatorPath);
			//执行mvn命令
			Runtime run = Runtime.getRuntime();
			Process p = run.exec(cmd);// 启动另一个进程来执行命令  
			br = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()))); 
			while((line = br.readLine()) != null){
				System.out.println(line);
			}
			br.close();
		}
	}
	
	/**
	 * 重置generatorConfig.xml中table的配置
	 * @param obj
	 * @throws Exception
	 * @author zc.ding
	 * @date 2016年10月16日
	 */
	private void resetGeneratorConfig(Generator obj) throws Exception{
		String generatorPath = path + "/" + obj.getGeneratorPath();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(generatorPath), "UTF-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while((line = br.readLine()) != null){
			if(line.indexOf("<table schema=") > -1){
				line = obj.getOldTemplate();
			}
			sb.append(line).append("\n");
		}
		br.close();
		this.saveDataToFile(sb, generatorPath);
	}
	
	/**
	 * 保存数据到文件中
	 * @param sb
	 * @param path
	 * @throws Exception
	 * @author zc.ding
	 * @date 2016年10月16日
	 */
	private void saveDataToFile(StringBuffer sb, String path) throws Exception{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
		bw.write(sb.toString());
		bw.flush();
		bw.close();
	}
}
