package com.sirding.main;

public class MvnMain {

	/**
	 * 展示提示信息
	 * @author zc.ding
	 * @date 2016年10月16日
	 */
	static void showTs(){
		System.out.println("usage: \tjava -jar java-ssd.jar [option1] [option2]\n");
		System.out.println("option1:\n");
		System.out.println("\t1、mvn，执行mvn mybatis-generator:generte命令\n");
		System.out.println("\t2、git，统计更新文件列表\n");
		System.out.println("option2:(此参数只有option1是git才有意义)\n");
		System.out.println("\t git \t统计更新的文件列表、保存到fileList.txt文件中\n");
		System.out.println("\t copy\t将fileList.txt中更新的文件保存upload文件，以便上传使用\n");
		System.out.println("\t download\t将fileList.txt中更新的文件从远程服务器上下载下来作为备份文件\n");
		System.out.println("\t upload\t将uplate中更新的文件上传至服务器\n");
		System.out.println("\t all 包含上述git、copy、download、upoad操作\n");

	}
}
