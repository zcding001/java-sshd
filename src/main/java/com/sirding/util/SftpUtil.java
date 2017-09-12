package com.sirding.util;

import java.io.File;
import java.util.Properties;




import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.sirding.service.LogMsg;

/**
 * 连接sshd的工具类
 * @author zc.ding
 * @date 2016年7月7日
 */
public class SftpUtil {
	private static Session session;
	private static ChannelSftp sftp = null;

	/**
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param privateKey
	 * @param passphrase
	 * @return
	 * @date 2016年7月8日
	 * @author zc.ding
	 */
	public static String initSftp(String host, int port, String userName, String password,
			String privateKey, String passphrase) {
		Channel channel = null;
		try {
			initSshdSession(host, port, userName, password, privateKey, passphrase);
			//参数sftp指明要打开的连接是sftp连接
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			if(sftp == null){
				return "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param privateKey
	 * @param passphrase
	 * @return
	 * @date 2016年7月8日
	 * @author zc.ding
	 */
	private static void initSshdSession(String host, int port, String userName, String password,
			String privateKey, String passphrase) throws Exception{
		JSch jsch = new JSch();
		if (privateKey != null && !"".equals(privateKey)) {
			//使用密钥验证方式，密钥可以使有口令的密钥，也可以是没有口令的密钥
			if (passphrase != null && "".equals(passphrase)) {
				jsch.addIdentity(privateKey, passphrase);
			} else {
				jsch.addIdentity(privateKey);
			}
		}
		session = jsch.getSession(userName, host, port);
		if (password != null && !"".equals(password)) {
			session.setPassword(password);
		}
		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");// do not verify host key
		session.setConfig(sshConfig);
		session.setTimeout(10000);
		session.setServerAliveInterval(1000 * 60 * 10);
		session.connect();
	}

	/**
	 * 上传文件
	 * @param srcFile
	 * @param dstFile
	 * @throws Exception
	 * @date 2016年7月8日
	 * @author zc.ding
	 */
	public static void upload(String srcFile, String dstFile) throws Exception {
		File file = new File(dstFile);
//		exec("mkdir -p " + file.getParent());
		SftpUtil.mkdirMultiPath(file.getParent());
		sftp.put(srcFile, dstFile);
	}

	/**
	 * 
	 * @param downloadFile 服务器上要下载的文件
	 * @param saveFile 保存到本地的文件
	 * @param sftp
	 * @author zc.ding
	 * @date 2016年7月7日
	 */
	public static void download(String downloadFile, String saveFile) {
		try {
			FileUtil.mkdir(new File(saveFile).getParent());
			sftp.get(downloadFile,saveFile);
		} catch (Exception e) {
			String msg = e.getMessage();
			if(msg.indexOf("No such file") > -1){
				System.out.println("找不到远端文件[" + downloadFile + "]");
			}else{
				System.out.println("找不到本地文件[" + saveFile + "]");
			}
		}
	}

	/**
	 * 删除指定的文件
	 * @param directory
	 * @param deleteFile
	 * @param sftp
	 * @author zc.ding
	 * @date 2016年7月7日
	 */
	public static void delete(String directory, String deleteFile) throws Exception{
		sftp.rm(deleteFile);
	}

	/**
	 * 删除文件
	 * @param deleteFile 服务器上要删除的文件
	 * @throws Exception
	 * @date 2016年7月7日
	 * @author zc.ding
	 */
	public static void delete(String deleteFile) throws Exception{
		sftp.rm(deleteFile);
	}

	/**
	 * 创建远程路径
	 * @param path
	 * @throws Exception
	 * @author zc.ding
	 * @date 2016年7月7日
	 */
	public static void mkdirPath(String path) throws Exception{
		sftp.mkdir(path);
	}

	/**
	 * 销毁sftp连接
	 * @throws Exception
	 * @author zc.ding
	 * @date 2016年7月7日
	 */
	public static void disconnected(){
		try {
			if (sftp != null) {
				sftp.getSession().disconnect();
				sftp.disconnect();
				System.out.println("sshd连接已正常关闭!" + LogMsg.SEP + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断指定的路径是否存在
	 * @param directory
	 * @return
	 * @throws Exception
	 * @author zc.ding
	 * @date 2016年7月7日
	 */
	public static boolean isDirExist(String directory) throws Exception {
		boolean isDirExistFlag = false;
		try {
			SftpATTRS sftpATTRS = sftp.lstat(directory);
			isDirExistFlag = sftpATTRS.isDir();
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().equals("no such file")) {
				isDirExistFlag = false;
			}
		}
		return isDirExistFlag;
	}
	
	/**
	 *  @Description    : 创建远程服务器路径
	 *  @Method_Name    : createPath
	 *  @param path
	 *  @throws Exception
	 *  @return         : void
	 *  @Creation Date  : 2017年9月12日 下午2:31:17 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static void mkdirMultiPath(String path) throws Exception{
		String[] pathArr = path.replaceAll("\\\\", "/").split("/");
		StringBuilder sb = new StringBuilder("/");
		if(pathArr != null){
			for(String tmp : pathArr){
				if(tmp == null || tmp.length() <= 0){
					continue;
				}
				sb.append(tmp).append("/");
				if(!isDirExist(sb.toString())){
					sftp.mkdir(sb.toString());
				}
				sftp.cd(sb.toString());
			}
		}
	}

	public static void exec(String cmd){
		ChannelExec exec = null;
		try {
			//参数sftp指明要打开的连接是exec连接
			exec = (ChannelExec)session.openChannel("exec");
			exec.setCommand(cmd);
			exec.connect();
			exec.setErrStream(System.err);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(exec != null && exec.isConnected()){
				exec.disconnect();
			}
		}
	}
}