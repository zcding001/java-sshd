package com.sirding.model;

import com.sirding.annotation.Option;

public class Generator {

	@Option(isSection = true)
	private String secName;
	@Option(key = "pom_path")
	private String pomPath;	//pom.xml文件的位置
	@Option(key = "table_list")
	private String tableList;	//数据库表名的集合 eg table1,table2,table2
	@Option(key = "pojo_list")
	private String pojoList;	//对应表名的实体类名称，必须与tableList数量一致eg Table1,Table2,Table3
	@Option(key = "generator_path")
	private String generatorPath;	//genertor.xml文件位置
	@Option(key = "old_template")
	private String oldTemplate;	//<table schme...位置的数据
	
	public String getSecName() {
		return secName;
	}
	public void setSecName(String secName) {
		this.secName = secName;
	}
	public String getPomPath() {
		return pomPath;
	}
	public void setPomPath(String pomPath) {
		this.pomPath = pomPath;
	}
	public String getTableList() {
		return tableList;
	}
	public void setTableList(String tableList) {
		this.tableList = tableList;
	}
	public String getPojoList() {
		return pojoList;
	}
	public void setPojoList(String pojoList) {
		this.pojoList = pojoList;
	}
	public String getGeneratorPath() {
		return generatorPath;
	}
	public void setGeneratorPath(String generatorPath) {
		this.generatorPath = generatorPath;
	}
	public String getOldTemplate() {
		return oldTemplate;
	}
	public void setOldTemplate(String oldTemplate) {
		this.oldTemplate = oldTemplate;
	}
}
