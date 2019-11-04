# ******************************************************************************************** #
# ******************************** 省级学籍管理系统数据库 ************************************ #
# ************************************** 创建人：陈元崇 ************************************** #
# *************************************** 文档号：0.03 *************************************** #
# *********************************** 创建日期：2019-09-04 *********************************** #
# *********************************** 修改日期：2019-09-04 *********************************** #
# ************************************ 最后修改人：陈元崇 ************************************ #
# *************************************** 修改内容：所有表 *************************************** #
# ***************************************** 备注：无 ***************************************** #
# ********************* 注意：每次对数据库文档的改动，都必须更新文档备案 ********************* #
# ******************************************************************************************** #

# ******************************************************************************************** #
# ************************************** 建立数据库 ****************************************** #
# ******************************************************************************************** #
create database studentinfo_mansys charset utf8;
use studentinfo_mansys;


# ******************************************************************************************** #
# **************************************** 学校表 ******************************************** #
# ******************************************************************************************** #
create table school
(
	# 唯一标识符 	
	p_schid 	int unsigned auto_increment,
	# 学校的名称
	name 		varchar(64) not null,
	# 学校所处的具体地理位置
	location 	varchar(64) not null,
	# 学校的联系电话
	phone 		char(11) not null,
	# 学校的官网
	website 	varchar(64) not null,
	
	# 约束
	primary key(p_schid)
);



# ******************************************************************************************** #
# *************************************** 用户资料表 ***************************************** #
# ******************************************************************************************** #
create table uinfo
(
	# 唯一标识符
	p_ufid 		int 		unsigned 	auto_increment,
	# 用户所属学校，指向一个学校表数据(school)
	f_school 	int 		unsigned,
	# 职位，自由填写
	position 	varchar(64)	not null,
	# 个人联系电话，可为空
	phone 		char(11),
	
	# 约束
	primary key(p_ufid),
	constraint fk_uinfo_school_schid foreign key(f_school) references school(p_schid)
);



# ******************************************************************************************** #
# **************************************** 用户表 ******************************************** #
# ******************************************************************************************** #
create table user
(
	# 用户在整个系统中的唯一标识符
	p_uid 		int unsigned 	auto_increment,
	# 用户登录所用的一组字母和数字的组合字符串，是公开的，且唯一
	name 		varchar(64) 	not null,
	# 用户登录所用的文本，也是一组字母和数字的组合字符串，但只能用户自己知道，MD5加密
	pwd 		varchar(32) 	not null,
	# 分为超级用户和普通管理用户，0：超级用户；1：普通管理用户
	role 		bit(1) 			not null,
	# 分为省级和校级权限，0：省级；1：校级
	privilege 	bit(1) 			not null,
	# 指向uinfo表，是一个普通管理员的资料，例如什么学校，什么职位，学校地址等等；但超级用户是一个特殊的值：0
	f_info 		int unsigned,
	
	# 约束
	primary key(p_uid),
	constraint fk_user_uinfo_ufid foreign key(f_info) references uinfo(p_ufid)
);



# ******************************************************************************************** #
# ************************************** 专业类别表 ****************************************** #
# ******************************************************************************************** #
create table profcategory
(
	# 唯一标识符
	p_profcatid 	int unsigned auto_increment,
	# 专业类别的全称
	name  			varchar(64) 		not null,
	# 专业类别的备注信息，可以做一些简单的解释工作
	note 			varchar(128) 		not null,
	
	# 约束
	primary key(p_profcatid)
);



# ******************************************************************************************** #
# ***************************************** 专业表 ******************************************* #
# ******************************************************************************************** #
create table professional
(
	# 唯一标识符
	p_profid 		int unsigned auto_increment,
	# 专业的全称
	name 			varchar(64) 		not null,
	# 专业所属类别，例如：计算机。指向一个专业类别数据对象
	f_profcategory 	int unsigned 		not null,
	
	# 约束
	primary key(p_profid),
	constraint fk_professional_profcategory_profcatid foreign key(f_profcategory) references profcategory(p_profcatid)
);



# ******************************************************************************************** #
# ***************************************** 学籍表 ******************************************* #
# ******************************************************************************************** #
create table student
(
	# 学籍号，唯一标识符，这个不能直接用int，这个需要录入学籍时候指定，其计算方式为 学校id + 学生学号 的字符串
	p_stuid 		varchar(64),
	# 学生的学号
	code 			varchar(64) 		not null,
	# 学生的姓名
	name 			char(7) 			not null,
	# 学生的身份证号
	idnumber 		char(18) 			not null,
	# 学生的真实一寸彩色照片，存放的是相对于系统的相对路径
	picture 		varchar(128) 		not null,
	# 学生的性别，从身份证号上计算；用1个字节表示，0：女，1：男
	gender 			bit(1) 				not null,
	# 学生的年龄，通过身份证号计算
	age 			tinyint unsigned 	not null,
	# 学生的家庭住址
	homeadd 		varchar(64) 		not null,
	# 学生的个人联系方式
	phone 			char(11) 			not null,
	# 学生的学校，指向一个学校数据对象
	f_school 		int unsigned 		not null,
	# 学生的入学时间
	enrolltime 		date 				not null,
	# 学生的学年制，一般本科四年制
	scho_year_sys 	tinyint unsigned 	not null,
	# 学生的毕业时间，如果还未毕业，这里为null
	graduatime 		date,
	# 学生的文凭，0：本科；1：研究生；用二进制表示
	diploma 		bit(1) 				not null,
	# 学生所学专业，指向一个专业数据对象
	f_professional 	int unsigned 		not null,
	
	# 约束
	primary key(p_stuid),
	constraint fk_student_school_schid foreign key(f_school) references school(p_schid),
	constraint fk_student_professional_profid foreign key(f_professional) references professional(p_profid)
);



# ******************************************************************************************** #
# *********************************** 学校专业映射表 ***************************************** #
# ******************************************************************************************** #
create table sch2prof
(
	# 唯一标识符
	p_sch_prof_id 		int unsigned auto_increment,
	# 外键，指向一个学校数据对象
	f_school 			int unsigned,
	# 外键，指向一个学校专业数据对象
	f_professional 		int unsigned,
	
	# 约束
	primary key(p_sch_prof_id),
	constraint fk_sch2prof_school_profcatid foreign key(f_school) references school(p_schid),
	constraint fk_sch2prof_professional_profcatid foreign key(f_professional) references professional(p_profid)
	
);



# ******************************************************************************************** #
# ***************************************** 消息表 ******************************************* #
# ******************************************************************************************** #
create table message
(
	# 消息的唯一标识符
	p_messid 		int unsigned auto_increment,
	# 消息的来源，即是哪个用户发来的消息，指向一个用户数据对象
	f_user_source 	int unsigned 		not null,
	# 消息的类型，不同值代表处理不同事件。具体则需要在开发中才能确定类型表。
	messtype 		int unsigned 		not null,
	# 消息携带的数据，可以为空；这里指定使用json字符串，最长只能存放19988个字节。
	data 			varchar(19988),
	# 是否需要h5页面，不需要则返回消息的json格式，1：需要，0：不需要
	has_view 			bit(1) 				not null,
	# 请求服务的索引
	to_service    int unsigned    not null,
	
	# 约束
	primary key(p_messid),
	constraint fk_message_user_uid foreign key(f_user_source) references user(p_uid)
);

# ******************************************************************************************** #
# *************************************** 申请消息表 ***************************************** #
# ******************************************************************************************** #
create table apply
(
	# 消息的唯一标识符
	p_applid 		int unsigned auto_increment,
	# 申请消息的主体，引用自消息表
	f_message 		int unsigned 		not null,
	# 处理状态，0：未处理；1：已处理
	state       bit(1)        not nukk,
	# 处理结果，0：未处理；1：已处理
	result 			bit(1) 				not null,
	
	# 约束
	primary key(p_applid),
	constraint fk_apply_message_messid foreign key(f_message) references message(p_messid)
);

# ******************************************************************************************** #
# *************************************** 回复消息表 ***************************************** #
# ******************************************************************************************** #
create table reply
(
	# 消息的唯一标识符
	p_replid 		int unsigned auto_increment,
	# 回复消息的主体，引用自消息表，这个数据来源于当初申请的消息
	f_message 	int unsigned 		not null,
	# 提示信息
	note       varchar(64)      not null,
	# 是否已经被用户查看
	be_viewed    bit(1)           not null,


	# 约束
	primary key(p_replid),
	constraint fk_reply_message_messid foreign key(f_message) references message(p_messid)
);





