package com.sirding.model;
/**
 * 
 * @author zc.ding
 * @date 2016年7月6日
 *
 */
public class Config {

	private String ip;
	private String port;
	private String userName;
	private String pwd;
	private String secName;
	private String tomcatPath;
	private String gitPath;
	private int index;
	private String date;
	private String bakFolderNameSuffix;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSecName() {
		return secName;
	}
	public void setSecName(String secName) {
		this.secName = secName;
	}
	public String getTomcatPath() {
		return tomcatPath;
	}
	public void setTomcatPath(String tomcatPath) {
		this.tomcatPath = tomcatPath;
	}
	public String getGitPath() {
		return gitPath;
	}
	public void setGitPath(String gitPath) {
		this.gitPath = gitPath;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getBakFolderNameSuffix() {
		return bakFolderNameSuffix;
	}
	public void setBakFolderNameSuffix(String bakFolderNameSuffix) {
		this.bakFolderNameSuffix = bakFolderNameSuffix;
	}
}
