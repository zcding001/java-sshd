package com.sirding.util;

import java.io.File;
import java.util.Properties;
 

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 连接sshd的工具类
 * @author zc.ding
 * @date 2016年7月7日
 */
public class SftpUtil {
 
//    private static String host;//sftp服务器ip
//    private static String userName;//用户名
//    private static String password;//密码
//    private static String privateKey;//密钥文件路径
//    private static String passphrase;//密钥口令
//    private static int port;//默认的sftp端口号是22
 
    /**
     * 
     * @param host 主机地址
     * @param port 目的端口
     * @param userName 用户名称
     * @param password 口令
     * @param privateKey 
     * @param passphrase
     * @return
     * @date 2016年7月7日
     * @author zc.ding
     */
    public static ChannelSftp initSftp(String host, int port, String userName, String password, String privateKey, String passphrase) {
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
            Session session = jsch.getSession(userName, host, port);
            if (password != null && !"".equals(password)) {
                session.setPassword(password);
            }
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");// do not verify host key
            session.setConfig(sshConfig);
            session.setTimeout(10000);
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
    public static void upload(String directory, String uploadFile, ChannelSftp sftp) throws Exception {
        sftp.cd(directory);
        File file = new File(uploadFile);
        sftp.put(uploadFile, file.getName());
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
    public static void download(String directory, String downloadFile, String saveFile, ChannelSftp sftp) throws Exception {
        sftp.cd(directory);
        sftp.get(downloadFile,saveFile);
    }
    
    /**
     * 
     * @param downloadFile 服务器上要下载的文件
     * @param saveFile 保存到本地的文件
     * @param sftp
     * @author zc.ding
     * @date 2016年7月7日
     */
    public static void download(String downloadFile, String saveFile, ChannelSftp sftp) throws Exception {
        sftp.get(downloadFile,saveFile);
    }
 
    /**
     * 删除指定的文件
     * @param directory
     * @param deleteFile
     * @param sftp
     * @author zc.ding
     * @date 2016年7月7日
     */
    public static void delete(String directory, String deleteFile, ChannelSftp sftp) throws Exception{
        sftp.rm(deleteFile);
    }
    
    /**
     * 删除文件
     * @param deleteFile 服务器上要删除的文件
     * @param sftp
     * @throws Exception
     * @date 2016年7月7日
     * @author zc.ding
     */
    public static void delete(String deleteFile, ChannelSftp sftp) throws Exception{
        sftp.rm(deleteFile);
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
}