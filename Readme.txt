

=============== Change Logging ==================

--------------- transmit v2.8 -----------------
2012.3.20
监测中心调试版本 - 边江
1. Fixed StreamRoundInfoQuery Integer.parseInt null error.
2. RecordType 4 马赛克合成轮播 add By: Bian Jiang 2012.3.21



--------------- transmit v2.7 -----------------
1.修改门限开关/值向所有Msg下发问题 把轮训板卡也加上
2.报警上报时候判断该节目或频点报警开关是否关闭，若为关闭则放弃该报警
3.在表channelremapping增加码率标记 MbpsFlag 并且判断 若设置码率加大则 更改自动录像表中 码率状态为1 相反则为0
若状态为1的个数超过配置文件中所设置的 则向平台提示资源不足 并不给TSC下发命令
ALTER TABLE `transmit`.`channelremapping` ADD COLUMN `MbpsFlag` INT(1) UNSIGNED DEFAULT 0 COMMENT '码率设置标记 0标准 1超标准' AFTER `TscIndex`;
4.更新了自动录像下发时候如果一个tsc通道满后 另外一个tsc分配多套高清的 问题   并且下发自动录像之前先判断资源是否充足 判断依据：
	1）查询数据库录制状态为1 的记录 标清则计数器加1 高清则计数器加5 
	2）遍历所发协议中的标清/高清节目个数 同样 高清计数器加5 标清加1
	3）库中的加上协议中的数量  大于 配置文件中最大录制数量 提示资源不足 返回平台 方法终止 不往tsc下发
	4）不大于最大录制数量 则进行平均分配。
5.更新了历史视频查看/下载给所有tsc下发协议问题 修改后为只给录制该套节目的tsc下发
6. 增加报警门限数据库alarmswitch
--
-- Table structure for table `alarmswitch`
--

DROP TABLE IF EXISTS `alarmswitch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `alarmswitch` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `freq` varchar(10) DEFAULT '0' COMMENT '所设置报警开关的频点 arr代表所有频点',
  `serviceid` varchar(10) DEFAULT '0' COMMENT '节目级报警开关 arr代表该频点的全频道',
  `switchvalue` int(1) DEFAULT '0' COMMENT '报警开关的状态 0:代表关 1:代表开',
  `switchtype` int(1) DEFAULT '0' COMMENT '该报警开关属于 节目:1 频点:2',
  `alarmtype` int(10) DEFAULT '0' COMMENT '报警类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=gb2312 COMMENT='报警开关记录';
SET character_set_client = @saved_cs_client;
7.修改数据业务时间分析时候下发频道扫描的协议，之前协议会导致高清标识扫不出来。
8.优化报警过滤算法
9.增加运行图节目信息表 AlarmTime
SET FOREIGN_KEY_CHECKS=0;
10.修改报警补报bug  alarmsearchtable 中增加isSuccess（是否报警成功） 字段
11.处理连接池枯竭bug
12.优化tsc平均分配 优先根据频点分配
13.增加集中配置功能
-- ----------------------------
-- Table structure for alarmtime
-- ----------------------------
CREATE TABLE `alarmtime` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Freq` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '频点',
  `ServiceID` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'serviceid',
  `TaskType` varchar(10) NOT NULL COMMENT '修台任务类型月有效任务：MonthTime周有效任务：WeeklyTime天有效任务：DayTime',
  `Month` varchar(4) NOT NULL COMMENT '月份',
  `Day` int(2) unsigned NOT NULL COMMENT '天',
  `StartTime` varchar(10) NOT NULL COMMENT '开始时间',
  `EndTime` varchar(10) NOT NULL COMMENT '结束时间',
  `Type` int(1) unsigned NOT NULL COMMENT '0 长期停播，1 长期播出，2 临时停播，3 临时播出',
  `AlarmEndTime` varchar(20) NOT NULL COMMENT '运行图有效时间，空为长期有效',
  `DayofWeek` int(1) unsigned NOT NULL COMMENT '1~7表示周一至周',
  `StartDateTime` varchar(20) NOT NULL COMMENT '开始日期',
  `EndDateTime` varchar(20) NOT NULL COMMENT '结束日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=gbk COMMENT='储存运行图节目信息';

-- ----------------------------
-- Records 
-- ----------------------------
10.修改报警补报机制： 
	1.正常报警未成功上报时候 报警保存到报警补报表删除报警表的报警信息
	  之前机制：未上报成功存在报警表也存在报警补报表
	2.增加报警补报或正常报警上报是否成功状态 如果报警补报成功则在报警补报库中删除该报警
	 如果正常报警上报未成功则删除存在报警表中的报警信息 只存在报警补报表
11.修改自动录像下发给tsc 的机制 先根据  把频点一样的节目尽量给一个tsc

--------------- transmit v2.6 -----------------
Bian Jiang
2011.3.30
1. 增加EPG信息从数据库中读取. 需要XMLEPG2DB配合使用.
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `epginfo` (
  `epg_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ScanTime` varchar(45) NOT NULL,
  `Freq` int(10) unsigned DEFAULT NULL,
  `ProgramID` int(10) unsigned DEFAULT NULL,
  `Program` varchar(45) DEFAULT NULL,
  `ProgramType` varchar(45) DEFAULT NULL,
  `StartTime` varchar(45) DEFAULT NULL,
  `ProgramLen` varchar(45) DEFAULT NULL,
  `State` varchar(45) DEFAULT NULL,
  `Encryption` int(10) unsigned DEFAULT NULL,
  `Lastdatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`epg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50001 DEFAULT CHARSET=utf8 COMMENT='EPG Infomation';
/*!40101 SET character_set_client = @saved_cs_client */;
2. EPG入库采用事务处理, 4W条数据需要1分钟.
3. Fixed: 任务录像没有判断, 无资源可利用的情况.
4. 数据库连接 C:\tomcat-6.0.10\conf\context.xml
maxWait：超时等待时间以毫秒为单位  
maxIdle：最大空闲连接  
minIdle：最小空闲连接  
maxActive：最大连接数 
 <Resource 
 	name="jdbc/mysql"
 	auth="Container" 
 	type="javax.sql.DataSource" 
 	maxActive="500" 
    maxIdle="20"
    minIdle="2"
 	maxWait="10000" 
 	username="transmit" 
 	password="transmit"  
 	driverClassName="com.mysql.jdbc.Driver" 
 	url="jdbc:mysql://127.0.0.1:3306/transmit?autoReconnect=true" />
MySQL连接如果8小时未使用，在查询使用到该连接会报：
异常名称：com.mysql.jdbc.CommunicationsException
异常信息: Communications link failure due to underlying exception
如果是MySQL5以前的版本，需要修改连接池配置中的URL，添加autoReconnect=true
如果是MySQL5 以后的版本，需要修改my.cnf(或者my.ini)文件，在[mysqld]后面添加
wait_timeout = 604800
interactive-timeout = 604800
单位都是秒，记得必须都添加，否则不起作用，通过show variables查看wait_timeout的值。
5. IsEPGFromDataBase EPG信息是否从数据库取得 0:不从数据库取得数据 1:从数据库取得
<SysInfo IsHasAlarmID="0" IsEPGZip="1" IsEPGFromDataBase="0" IsAutoAlarmReply="1" MaxAutoRecordNum="60" CenterAlarmURL="http://10.134.121.4/interface/receive.asmx" SrcCode="440000M01" DstCode="440000G01"/>
6.自动录像查看和下载下发的时候不处理通道号
7.增加TSC下发的通道号 
ALTER TABLE `transmit`.`channelremapping` ADD COLUMN `TscIndex` INT(10) UNSIGNED DEFAULT 0 COMMENT 'TSC发送的通道号' AFTER `ProgramName`;
8. 增加TscIndex, 在TSC下发的时候自动匹配TSCIndex;
9. 在给高清节目分配TSC的时候采用平均分配, D:\config\TransmitConfig.xml 文件中的TSC标签中的, IndexMin 和 IndexMax 要和你设备配置TscIndex一样.
10. 对MSG上报报警做过滤，同样报警未恢复继续上报不处理，对于该报警已经恢复还继续发恢复报警的也不处理
11.修改高清节目给TSC分配时候通道重复的bug
12.修改EPG信息保存目录无法创建错误
13.修改 门限开关向所有Msg下发问题  //SELECT Devindex,Freq,smgURl FROM channelremapping c where Freq != 0 group by Freq;

--------------- transmit v2.5 -----------------
Bian Jiang
2011.3.30
1. 判断是否自动录像是否超过配置文件中的最大值(MaxAutoRecordNum)
2. 判断报警信息的频点是否在channelremapping表里面存在,如果不存在就放弃相关报警
3. Fixed: 手动录制主动上报的错误.
4. 增加EPG信息从数据库中读取.

--------------- transmit v2.4 -----------------
transmit v2.4 广州监测中心稳定版本
Bian Jiang
2011.2.18
1. Fixed: 频道扫描可能出现多个MSG头信息
2. Fixed: 不能正常给TSC发送命令的Bug
3. Fixed: 业务时间分析, 有时找不到文件的Bug
4. 增加了一个报警历史表，默认把半天前的数据导入报警历史表里面，这样那个表里面的数据不会太多，最多保存半天的数据。
-----------AlarmHistorySearchTable-------------
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AlarmHistorySearchTable` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Freq` int(10) unsigned DEFAULT '0',
  `ServiceID` int(10) unsigned DEFAULT '0',
  `VideoPID` int(10) unsigned DEFAULT '0',
  `AudioPID` int(10) unsigned DEFAULT '0',
  `AlarmType` int(10) unsigned DEFAULT '0',
  `AlarmDesc` varchar(45) DEFAULT NULL,
  `AlarmValue` int(10) unsigned DEFAULT '0' COMMENT '0:没有发生. 1:正在发生. 2:恢复了.',
  `AlarmStartTime` datetime DEFAULT NULL COMMENT '报警发生时间',
  `AlarmEndTime` datetime DEFAULT NULL COMMENT '报警恢复时间',
  `Lastdatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AlarmHistorySearchTable`
--

LOCK TABLES `AlarmHistorySearchTable` WRITE;
/*!40000 ALTER TABLE `AlarmHistorySearchTable` DISABLE KEYS */;
/*!40000 ALTER TABLE `AlarmHistorySearchTable` ENABLE KEYS */;
UNLOCK TABLES;
-----------AlarmHistorySearchTable-------------



--------------- transmit v2.3 -----------------
transmit v2.3 广州监测中心稳定版本
Bian Jiang
2010.12.14
1. 一对一监视和多画面处理采用优先级处理。
2. 数据业务时间增加定时采集。



--------------- transmit v2.1 -----------------
transmit v2.1 广州监测中心稳定版本
Bian Jiang
2010.11.11
1. 通道映射每个频道可以任意多个节目
2. 修正系统状态高清标记的Bug
3. 去掉是历史视频查看和下载的延迟。

--------------- transmit v2.0 -----------------
transmit v2.0 广州监测中心基本稳定版本
Bian Jiang
2010.10.25
1. 实时视频相关的处理有RTVM统一处理。
2. 修正了一些通道自动分配Bug。
3. 修复数据库连接池没有关闭Bug。


--------------- transmit v1.9 -----------------
transmit v1.9 广州监测中心初验
Bian Jiang
2010.10.14
1. 增加运行图。
2. 实时视频和轮播单独处理。
3. 修正了一些Bug。


--------------- transmit v1.8 -----------------
transmit v1.8 广州监测中心测试版本
Bian Jiang
2010.9.25
1. [Java]MHP信息查询
   没有返回URL
2. [Java]Table信息
1. 查询报错
2. 指定Freq查询, > 1M 自动压缩为zip
3. [JAVA] EPG大于1M压缩为zip
4. [JAVA/TSC]手动录制增加组播地址和端口号
5. 实时视频和频道扫描分开
实时视频从数据库中检索，并重启RTVS
频道扫描，频谱扫描，指标查询从配置文件中取得.
6. [Java/WatchAndSee][测试]和视频相关的RTVS配置IP和端口号
相关协议:
1. 手动选台
2. 自动轮播
3. 轮询监测

--------------- transmit v1.7 -----------------
transmit v1.7 广州监测中心测试版本
Bian Jiang
2010.9.23
1. 广州监测中心测试版本
2. 增加实时视频监看和轮询监测设置
3. 修正了自动录像的自动分配机制，取消了下发版本的判断
4. 和实时视频相关的都采用查询数据库
5. 同时支持多路实时视频


--------------- transmit v1.6 -----------------
transmit v1.6 广州监测演示版本
Bian Jiang
2010.6.11
1. 广州监测演示版本
2. 通道自动映射没有问题

--------------- transmit v1.5 -----------------
transmit v1.5 广州监测现场
Bian Jiang
2010.6.9
1. 实时视频和频道扫描采用一个通道


--------------- transmit v1.4 -----------------
transmit v1.4 全面支持监测中心2.3版本协议
Bian Jiang
2010.6.8
1. 支持一个频点同时监测多套节目.
2. 自动录像自动通道映射支持有和没有通道


--------------- transmit v1.3 -----------------
transmit v1.3 全面支持监测中心2.3版本协议
Bian Jiang
2010.6.6
1. 支持一个频点同时监测多套节目.
2. 转发自动给相关的频点分配通道，每个频点只能占用一个通道.
3. SMG，TSC和IPM接收数据的组播地址在配置文件配置，UDP端口号采用ServiceID+基准数(9000).
4. SMG，TSC和IPM这次采用统一的通讯方式，在自动录像修改的时候发送当前频点中的所有节目.
5. 以后在与其他家对接的时候只修改转发相关的代码，不用再修改SMG，TSC和IPM, 包括以后的双Tuner.
6. 任务录像和自动录像如果是同一个频点，就使用一个通道号。
?7. 关于任务录像，需要SMG，TSC单独讨论，如果需要任务录像的节目已经存在TSC可以直接录制，但是如果
需要任务录像的节目没有在当前的频点打出来，SMG和TSC需要选择一个默认的UDP端口号打出来。
?8. 如果自动录像删除是否给SMG,TSC以及IPM发生相关信息。
    a. 如果某一个通道刚好只有一套节目，这时接收到一个自动录像，如果进行相关操作。
?9. 没有考虑高清的问题。

--------------- transmit v1.2 -----------------
transmit v1.2 基本支持2.0和2.3版本协议
Bian Jiang

2010.5.30
1. 手动录制增加2.3协议的支持。
2. 增加手动选台对2.3协议的支持。
3. 增加自动轮播对2.3协议的支持。

2010.5.29
1. 增加自动录像相关表，任务录像相关表。
2. 在东软下发自动录像和任务录像时，根据自动录像，任务录像信息分析出对应的通道，并替换东软下发的信息。
分别发送给标清转码和内容监测。
3. 兼容2.0和2.3版本协议。

TODOList 2010.5.28
1. 确保2.2版本和2.3版本的兼容。
2. 在数据库增加三个表分别为: 节目列表信息，自动录像相关表，任务录像相关表
3. 解析频道扫描信息结果并入库，特别是高清，加扰标记。
4. 解析自动录像和任务录像信息并入库。
5. 在东软下发自动录像和任务录像时，根据频道扫描，自动录像，任务录像信息分析出对应的通道以及是否高清信息，并替换东软下发的信息。
分别发送给高清转码，标清转码和内容监测。

1. 频道扫描SDT 通过主频点取得所有频点的节目名称。
2. 取得高清识别标记。
3. 指定音频PID输出相关的音频给转码和内容监测。


--------------- transmit v1.1 -----------------
transmit v1.1 已通道为概念的版本
Bian Jiang

2010.5.25
1. 增加对硬件高清转码卡的支持, 协议包括: 
    * 视频转码录像默认参数设置
    * 实时流率设置
    * 自动录像设置

2010.5.21
* 配置文件频道扫描和指标查询都采用ChannelScanQuery

    
