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
	@Option(key = "tomcat_path")
	private String tomcatPath;
	@Option(key = "project_path")
	private String projectPath;
	@Option(key = "commit_id")
	private String commitId;
	@Option
	private int index;
	@Option
	private String date;
	@Option(key = "bak_folder_name_suffix")
	private String bakFolderNameSuffix;
	
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
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
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
	public String getCommitId() {
		return commitId;
	}
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
}
