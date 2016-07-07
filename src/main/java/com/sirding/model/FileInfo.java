package com.sirding.model;

public class FileInfo {

	//文件名称
	private String fileName;
	//文件路径
	private String filePath;
	//文件的操作  M(更新) A(添加) D(删除)
	private String option;
	
	public FileInfo(){}
	
	public FileInfo(String option, String filePath){
		this.filePath = filePath;
		this.option = option;
	}
	
	public FileInfo(String option, String fileName, String filePath){
		this.option = option;
		this.fileName = fileName;
		this.filePath = filePath;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
