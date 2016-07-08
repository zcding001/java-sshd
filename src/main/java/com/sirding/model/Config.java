package com.sirding.model;

import com.sirding.annotation.Option;

/**
 * 
 * @author zc.ding
 * @date 2016年7月6日
 *
 */
public class Config {
	@Option(isSection = true)
	private String secName;
	@Option(key = "remote_tomcat_path")
	private String remoteTomcatPath;
	@Option(key = "local_tomcat_path")
	private String localTomcatPath;
	@Option(key = "project_path")
	private String projectPath;
	@Option(key = "commit_id")
	private String commitId;
	
	public String getSecName() {
		return secName;
	}
	public void setSecName(String secName) {
		this.secName = secName;
	}
	public String getRemoteTomcatPath() {
		return remoteTomcatPath;
	}
	public void setRemoteTomcatPath(String remoteTomcatPath) {
		this.remoteTomcatPath = remoteTomcatPath;
	}
	public String getLocalTomcatPath() {
		return localTomcatPath;
	}
	public void setLocalTomcatPath(String localTomcatPath) {
		this.localTomcatPath = localTomcatPath;
	}
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public String getCommitId() {
		return commitId;
	}
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
}
