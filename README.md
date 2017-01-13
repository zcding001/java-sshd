功能描述：
	统计项目中更新文件列表(基于git管理项目)，可以统计当前工作空间中更新的文件列表，可以统计两个commitId之间更新的文件列表，统计信息保存到fileList.txt文件中；通过sshd将更新的文件列表上传到指定远程的项目中，更新之前会将要更新文件下载到本地已作为后期对比使用。更新完成后会将更新的信息保存到fileList.txt文件中。


使用说明：
	在文件中shift+鼠标右键->在此处打开命令窗口(w),此时出现dos窗口，执行如下命令
	java -jar java-ssd.jar查看命令参数及使用说明
	
PS：
	默认的config.ini文件支持yrtz_online和openapi增量升级，只要要更新如下配置
		local_tomcat_path	:	更新为你本地tomcat位置、或是openapi的target/class文件位置
		project_path			:	更新为你本地git项目的管理地址
		commit_id					: 更新为指定两个git节点的head，不分先后顺序，中间","分割，如果为空，那么统计你待提交的文件列表
		
		
目录结构：
	data
		20170111_1(文件名称当前年月日-索引)
			download			:	从远程服务器上下载需要更新的文件，以作备份记录
			upload				:	需要升级的文件目录结构
			fileList.txt	:	需要升级的文件列表信息（需要保存到wiki中的信息可以从此文件中获得）
			log.txt				:	程序运行期间的操作记录
	lib						:	依赖包
	config.ini		:	全局配置文件
	java-ssd.jar	:	运行程序
	

config.ini配置文件说明：
[global]节点(全局配置/主配置)
ip								:	远程服务器地址
port							: sshd端口
user_name					:	sshd用户名称
pwd								：sshd用户密码
private_key				:	秘钥
passphrase				:	秘钥密码
run_sec						:	运行此文件中的子节点
index							:	文件夹索引地址
date							：文件夹名称(系统当前时间)
auto_update_index	：自动更新升价文件夹的索引地址

[tomcat_pc]子节点(global中run_sec依赖的节点)
remote_tomcat_path		:	远程tomcat项目地址
local_tomcat_path			：本地tomcat运行项目的位置(openapi需要定位到targer的classes路径下)
project_path					：git项目的绝对地址
replace_src						：源文件替换规则
replace_dst						：目标路径替换规则
black_key_pattern			：黑名单列表(多个文件已,分割)，即使文件有更新也不更新到远程服务器上
commit_id							：git两个节点id，中间用","分割


