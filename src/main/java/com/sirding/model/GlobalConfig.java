package com.sirding.model;

import com.sirding.annotation.Option;

/**
 * 
 * @author zc.ding
 * @date 2016年7月7日
 */
public class GlobalConfig {

	@Option(isSection = true)
	private String secName = "global";
	@Option
	private String ip;
	@Option
	private String port;
	@Option(key = "user_name")
	private String userName;
	@Option
	private String pwd;
	@Option(key = "private_key", saveFlag = 2)
	private String privateKey;
	@Option(saveFlag = 2)
	private String passphrase;
	@Option(key = "run_sec")
	private String runSec;
	@Option
	private String index;
	@Option
	private String date;
	@Option(key = "auto_update_index")
	private String autoUpdateIndex;
	
	public String getSecName() {
		return secName;
	}
	public void setSecName(String secName) {
		this.secName = secName;
	}
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
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getPassphrase() {
		return passphrase;
	}
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
	public String getRunSec() {
		return runSec;
	}
	public void setRunSec(String runSec) {
		this.runSec = runSec;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getAutoUpdateIndex() {
		return autoUpdateIndex;
	}
	public void setAutoUpdateIndex(String autoUpdateIndex) {
		this.autoUpdateIndex = autoUpdateIndex;
	}
}
