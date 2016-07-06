package com.sirding.util;

import java.io.File;
import java.util.Properties;
 

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
 
public class SftpUtil {
 
    private static String host = "192.168.231.132";//sftp服务器ip
    private static String username = "root";//用户名
    private static String password = "jtsec@123456";//密码
    private static String privateKey;//密钥文件路径
    private static String passphrase;//密钥口令
    private static int port = 22;//默认的sftp端口号是22
 
    /**
     * 获取连接
     * @return channel
     */
    public static ChannelSftp connectSFTP() {
        JSch jsch = new JSch();
        Channel channel = null;
        try {
            if (privateKey != null && !"".equals(privateKey)) {
                //使用密钥验证方式，密钥可以使有口令的密钥，也可以是没有口令的密钥
                if (passphrase != null && "".equals(passphrase)) {
                    jsch.addIdentity(privateKey, passphrase);
                } else {
                    jsch.addIdentity(privateKey);
                }
            }
            Session session = jsch.getSession(username, host, port);
            if (password != null && !"".equals(password)) {
                session.setPassword(password);
            }
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");// do not verify host key
            session.setConfig(sshConfig);
            // session.setTimeout(timeout);
            session.setServerAliveInterval(1000 * 60 * 10);
            session.connect();
            //参数sftp指明要打开的连接是sftp连接
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return (ChannelSftp) channel;
    }
     
    /**
     * @param directory 目标路径
     * @param uploadFile 要上传的文件
     * @param sftp
     * @author zc.ding
     * @date 2016年7月7日
     */
    public static void upload(String directory, String uploadFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            File file = new File(uploadFile);
            sftp.put(uploadFile, file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 
     * @param directory 服务器上要下载文件的路径
     * @param downloadFile 服务器上要下载的文件
     * @param saveFile 保存到本地的文件
     * @param sftp
     * @author zc.ding
     * @date 2016年7月7日
     */
    public static void download(String directory, String downloadFile,
            String saveFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            sftp.get(downloadFile,saveFile);
        } catch (Exception e) {
            e.printStackTrace();
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
    public static void delete(String directory, String deleteFile, ChannelSftp sftp) {
        try {
//            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    public static void disconnected(ChannelSftp sftp){
        if (sftp != null) {
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                e.printStackTrace();
            }
            sftp.disconnect();
            System.out.println("okok...");
        }
    }
    
    public static void main(String[] args) {
		ChannelSftp sftp = connectSFTP();
		System.out.println("开始上传....");
		long start = System.currentTimeMillis();
		String directory = "/home/zcding";
		String uploadFile = "G:/test/sshd/test.txt";
		
		upload(directory, uploadFile, sftp);
		System.out.println("使用时间：" + (System.currentTimeMillis() - start));
		
		delete(null, "/home/zcding/test.txt", sftp);
		disconnected(sftp);
	}
}