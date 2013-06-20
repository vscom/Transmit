-- MySQL dump 10.13
--
-- Host: localhost    Database: transmit
-- ------------------------------------------------------
-- Server version	6.0.4-alpha-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarmhistorysearchtable`
--

DROP TABLE IF EXISTS `alarmhistorysearchtable`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `alarmhistorysearchtable` (
  `id` bigint(20) unsigned NOT NULL,
  `Freq` int(10) unsigned DEFAULT '0',
  `ServiceID` int(10) unsigned DEFAULT '0',
  `VideoPID` int(10) unsigned DEFAULT '0',
  `AudioPID` int(10) unsigned DEFAULT '0',
  `AlarmType` int(10) unsigned DEFAULT '0',
  `AlarmDesc` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `AlarmValue` int(10) unsigned DEFAULT '0' COMMENT '0:没有发生. 1:正在发生. 2:恢复了.',
  `AlarmStartTime` datetime DEFAULT NULL COMMENT '报警发生时间',
  `AlarmEndTime` datetime DEFAULT NULL COMMENT '报警恢复时间',
  `Lastdatetime` datetime DEFAULT NULL,
  `IsSuccess` int(1) unsigned DEFAULT '0' COMMENT '0ʧ 1ɹ',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `alarmhistorysearchtable`
--

LOCK TABLES `alarmhistorysearchtable` WRITE;
/*!40000 ALTER TABLE `alarmhistorysearchtable` DISABLE KEYS */;
/*!40000 ALTER TABLE `alarmhistorysearchtable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alarmsearchtable`
--

DROP TABLE IF EXISTS `alarmsearchtable`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `alarmsearchtable` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Freq` int(10) unsigned DEFAULT '0',
  `ServiceID` int(10) unsigned DEFAULT '0',
  `VideoPID` int(10) unsigned DEFAULT '0',
  `AudioPID` int(10) unsigned DEFAULT '0',
  `AlarmType` int(10) unsigned DEFAULT '0',
  `AlarmDesc` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `AlarmValue` int(10) unsigned DEFAULT '0' COMMENT '0:没有发生. 1:正在发生. 2:恢复了.',
  `AlarmStartTime` datetime DEFAULT NULL COMMENT '报警发生时间',
  `AlarmEndTime` datetime DEFAULT NULL COMMENT '报警恢复时间',
  `Lastdatetime` datetime DEFAULT NULL,
  `IsSuccess` int(1) unsigned DEFAULT '0' COMMENT '0?  1?',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `alarmsearchtable`
--

LOCK TABLES `alarmsearchtable` WRITE;
/*!40000 ALTER TABLE `alarmsearchtable` DISABLE KEYS */;
/*!40000 ALTER TABLE `alarmsearchtable` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='报警开关记录';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `alarmswitch`
--

LOCK TABLES `alarmswitch` WRITE;
/*!40000 ALTER TABLE `alarmswitch` DISABLE KEYS */;
/*!40000 ALTER TABLE `alarmswitch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alarmtime`
--

DROP TABLE IF EXISTS `alarmtime`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `alarmtime` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Freq` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '频点',
  `ServiceID` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'serviceid',
  `TaskType` varchar(10) CHARACTER SET gbk NOT NULL COMMENT '修台任务类型月有效任务：MonthTime周有效任务：WeeklyTime天有效任务：DayTime',
  `Month` varchar(4) CHARACTER SET gbk NOT NULL COMMENT '月份',
  `Day` int(2) unsigned NOT NULL COMMENT '天',
  `StartTime` varchar(10) CHARACTER SET gbk NOT NULL COMMENT '开始时间',
  `EndTime` varchar(10) CHARACTER SET gbk NOT NULL COMMENT '结束时间',
  `Type` int(1) unsigned NOT NULL COMMENT '0 长期停播，1 长期播出，2 临时停播，3 临时播出',
  `AlarmEndTime` varchar(20) CHARACTER SET gbk NOT NULL COMMENT '运行图有效时间，空为长期有效',
  `DayofWeek` int(1) unsigned NOT NULL COMMENT '1~7表示周一至周',
  `StartDateTime` varchar(20) CHARACTER SET gbk NOT NULL COMMENT '开始日期',
  `EndDateTime` varchar(20) CHARACTER SET gbk NOT NULL COMMENT '结束日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='储存运行图节目信息';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `alarmtime`
--

LOCK TABLES `alarmtime` WRITE;
/*!40000 ALTER TABLE `alarmtime` DISABLE KEYS */;
/*!40000 ALTER TABLE `alarmtime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `channelprogramstatus`
--

DROP TABLE IF EXISTS `channelprogramstatus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `channelprogramstatus` (
  `channelindex` int(10) unsigned NOT NULL,
  `Freq` int(10) unsigned NOT NULL DEFAULT '0',
  `SymbolRate` int(10) unsigned DEFAULT NULL,
  `qam` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `ServiceID` int(10) unsigned NOT NULL DEFAULT '0',
  `VideoPID` int(10) unsigned DEFAULT NULL,
  `AudioPID` int(10) unsigned DEFAULT NULL,
  `lasttime` datetime DEFAULT NULL,
  `indexstatus` int(10) unsigned DEFAULT '2' COMMENT '0: 无板卡 1: 板卡故障 2: 正在自动录制 3: 正在任务录制 4: 正在手动录制 5: 正在频谱扫描 6: 正在频道扫描 7: 正在指标查询',
  `channelFlag` tinyint(1) unsigned DEFAULT '0',
  `HDFlag` tinyint(1) unsigned DEFAULT '0' COMMENT '0:标清 1:高清',
  PRIMARY KEY (`channelindex`,`Freq`,`ServiceID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='最新节目信息，通道状态信息';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `channelprogramstatus`
--

LOCK TABLES `channelprogramstatus` WRITE;
/*!40000 ALTER TABLE `channelprogramstatus` DISABLE KEYS */;
/*!40000 ALTER TABLE `channelprogramstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `channelremapping`
--

DROP TABLE IF EXISTS `channelremapping`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `channelremapping` (
  `DevIndex` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '设备需要映射到通道号',
  `channelindex` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '中心下发的通道号',
  `Freq` int(10) unsigned NOT NULL DEFAULT '0',
  `SymbolRate` int(10) unsigned DEFAULT '6875',
  `qam` varchar(45) CHARACTER SET latin1 DEFAULT '64',
  `ServiceID` int(10) unsigned NOT NULL DEFAULT '0',
  `VideoPID` int(10) unsigned DEFAULT '0',
  `AudioPID` int(10) unsigned DEFAULT '0',
  `StatusFlag` int(10) unsigned DEFAULT '0',
  `lasttime` datetime DEFAULT NULL,
  `DownIndex` int(10) unsigned DEFAULT '0',
  `HDFlag` int(10) unsigned DEFAULT '0' COMMENT '高清标记0:标清 1:高清',
  `RecordType` int(10) unsigned DEFAULT '0' COMMENT '录像类型: 0：不录像，1:代表故障触发录制   2：24小时录像 3:任务录像 4:马赛克合成轮播',
  `DelFlag` int(10) unsigned DEFAULT '0',
  `udp` varchar(45) DEFAULT NULL COMMENT '组播地址',
  `port` int(10) unsigned DEFAULT NULL COMMENT '组播端口',
  `Action` varchar(45) DEFAULT NULL COMMENT '执行操作',
  `smgURL` varchar(45) DEFAULT NULL COMMENT 'SMG接收URL',
  `ProgramName` varchar(45) DEFAULT NULL COMMENT '节目名称',
  `TscIndex` int(10) unsigned DEFAULT '0' COMMENT 'TSC发送的通道号',
  `MbpsFlag` int(1) unsigned DEFAULT '0' COMMENT '码率设置标记 0标准 1超标准',
  `IpmIndex` int(10) unsigned DEFAULT '0' COMMENT 'IPM发送的通道号',
  PRIMARY KEY (`channelindex`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `channelremapping`
--

LOCK TABLES `channelremapping` WRITE;
/*!40000 ALTER TABLE `channelremapping` DISABLE KEYS */;
INSERT INTO `channelremapping` VALUES (2,2,0,6875,'64',0,0,0,0,'2011-10-21 01:07:00',0,0,0,0,'',0,'','','',0,0,0),(2,3,0,6875,'64',0,0,0,0,'2011-10-21 01:07:00',0,0,0,0,'',0,'','','',0,0,0);
/*!40000 ALTER TABLE `channelremapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `channelscanlist`
--

DROP TABLE IF EXISTS `channelscanlist`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `channelscanlist` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Freq` int(10) unsigned DEFAULT NULL,
  `QAM` varchar(45) DEFAULT NULL,
  `SymbolRate` int(10) unsigned DEFAULT NULL,
  `Program` varchar(45) DEFAULT NULL,
  `ServiceID` int(10) unsigned DEFAULT NULL,
  `VideoPID` int(10) unsigned DEFAULT NULL,
  `AudioPID` int(10) unsigned DEFAULT NULL,
  `EncryptFlg` int(10) unsigned DEFAULT NULL,
  `HDTV` int(10) unsigned DEFAULT NULL,
  `ScanTime` datetime DEFAULT NULL,
  `LastTime` datetime DEFAULT NULL,
  `LastFlag` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=gb2312;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `channelscanlist`
--

LOCK TABLES `channelscanlist` WRITE;
/*!40000 ALTER TABLE `channelscanlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `channelscanlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `channelstatus`
--

DROP TABLE IF EXISTS `channelstatus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `channelstatus` (
  `channelindex` int(10) unsigned NOT NULL,
  `Freq` int(10) unsigned DEFAULT NULL,
  `SymbolRate` int(10) unsigned DEFAULT NULL,
  `qam` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `ServiceID` int(10) unsigned DEFAULT NULL,
  `VideoPID` int(10) unsigned DEFAULT NULL,
  `AudioPID` int(10) unsigned DEFAULT NULL,
  `lasttime` datetime DEFAULT NULL,
  `indexstatus` int(10) unsigned DEFAULT '2' COMMENT '0: 无板卡 1: 板卡故障 2: 正在自动录制 3: 正在任务录制 4: 正在手动录制 5: 正在频谱扫描 6: 正在频道扫描 7: 正在指标查询',
  PRIMARY KEY (`channelindex`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='最新节目信息，通道状态信息';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `channelstatus`
--

LOCK TABLES `channelstatus` WRITE;
/*!40000 ALTER TABLE `channelstatus` DISABLE KEYS */;
/*!40000 ALTER TABLE `channelstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `epginfo`
--

DROP TABLE IF EXISTS `epginfo`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `epginfo` (
  `epg_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ScanTime` datetime NOT NULL,
  `Freq` int(10) unsigned DEFAULT NULL,
  `ProgramID` int(10) unsigned DEFAULT NULL,
  `Program` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `ProgramType` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `StartTime` datetime DEFAULT NULL,
  `ProgramLen` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `State` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `Encryption` int(10) unsigned DEFAULT NULL,
  `Lastdatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`epg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='EPG Infomation';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `epginfo`
--

LOCK TABLES `epginfo` WRITE;
/*!40000 ALTER TABLE `epginfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `epginfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `indexstatus`
--

DROP TABLE IF EXISTS `indexstatus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `indexstatus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `xml` text CHARACTER SET latin1 COMMENT 'XML信息',
  `lasttime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='通道指标信息';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `indexstatus`
--

LOCK TABLES `indexstatus` WRITE;
/*!40000 ALTER TABLE `indexstatus` DISABLE KEYS */;
/*!40000 ALTER TABLE `indexstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monitorprogramquery`
--

DROP TABLE IF EXISTS `monitorprogramquery`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `monitorprogramquery` (
  `rtvsIndex` int(10) unsigned NOT NULL,
  `lastDatatime` datetime DEFAULT NULL,
  `xml` text CHARACTER SET latin1,
  `rtvsIP` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `rtvsPort` int(10) unsigned DEFAULT NULL,
  `smgURL` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `statusFlag` int(10) unsigned DEFAULT '0' COMMENT '0:空闲 1:一对一监视多画面 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面(马赛克) 6: 一对一监测的手动选台',
  `RunType` int(10) unsigned DEFAULT '0' COMMENT '1:手动选台 2:一对一监测 2:轮询监测 3:轮播',
  `smgIndex` int(10) unsigned DEFAULT '0' COMMENT 'SMG通道号',
  `rtvsResetURL` varchar(100) CHARACTER SET latin1 DEFAULT NULL COMMENT 'RTVS 实时视频重新配置URL',
  `patrolGroupIndex` int(10) unsigned DEFAULT '0' COMMENT 'statusFlag=2时有效， 轮播监视群号',
  `Freq` int(10) unsigned DEFAULT '0',
  `ServiceID` int(10) unsigned DEFAULT '0',
  PRIMARY KEY (`rtvsIndex`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='实时视频监看';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `monitorprogramquery`
--

LOCK TABLES `monitorprogramquery` WRITE;
/*!40000 ALTER TABLE `monitorprogramquery` DISABLE KEYS */;
INSERT INTO `monitorprogramquery` VALUES (1,'2011-08-07 15:10:17','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Msg Version=\"2.3\" MsgID=\"1000_ID\" Type=\"MonDown\" DateTime=\"2009-08-17 15:30:00\" SrcCode=\"110000G01\" DstCode=\"110000M01\" SrcURL=\"http://10.24.32.28:8089/servlet/receiver\" Priority=\"1\"><StreamRoundInfoQuery>    <RoundStream Index=\"1\" RoundTime=\"00:00:10\" Switch=\"1\">      <Channel Freq=\"395000\" SymbolRate=\"6875\" QAM=\"QAM32\" ServiceID=\"2012\" VideoPID=\"108\" AudioPID=\"109\"/>      <Channel Freq=\"395000\" SymbolRate=\"6875\" QAM=\"QAM16\" ServiceID=\"2013\" VideoPID=\"116\" AudioPID=\"117\"/>      <Channel Freq=\"395000\" SymbolRate=\"6875\" QAM=\"QAM128\" ServiceID=\"2014\" VideoPID=\"124\" AudioPID=\"125\"/>      <Channel Freq=\"395000\" SymbolRate=\"6875\" QAM=\"QAM256\" ServiceID=\"2015\" VideoPID=\"132\" AudioPID=\"133\"/>   </RoundStream>  </StreamRoundInfoQuery></Msg>','239.0.54.4',8088,'',6,2,1,'',0,0,0),(2,'2011-10-20 09:53:48','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Msg Version=\"2.3\" MsgID=\"13960\" Type=\"MonDown\" DateTime=\"2011-10-20 09:56:29\" SrcCode=\"110000G01\" DstCode=\"110001\" SrcURL=\"http://192.168.0.23:8088/Web2.0/servlet/StreamRoundInfoQueryServlet\" Priority=\"9\">  <StreamRoundInfoQuery>    <RoundStream Index=\"2\" RoundTime=\"00:00:10\" Switch=\"1\">      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"1\" VideoPID=\"512\" AudioPID=\"650\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"2\" VideoPID=\"513\" AudioPID=\"660\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"101\" VideoPID=\"2100\" AudioPID=\"2101\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"108\" VideoPID=\"4998\" AudioPID=\"4999\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"107\" VideoPID=\"4992\" AudioPID=\"4993\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"106\" VideoPID=\"4818\" AudioPID=\"4819\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"105\" VideoPID=\"4812\" AudioPID=\"4813\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"104\" VideoPID=\"4272\" AudioPID=\"4273\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"103\" VideoPID=\"4092\" AudioPID=\"4093\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"102\" VideoPID=\"2292\" AudioPID=\"2293\"/>      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"3\" VideoPID=\"514\" AudioPID=\"670\"/>      <Channel Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"301\" VideoPID=\"512\" AudioPID=\"650\"/>      <Channel Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"302\" VideoPID=\"513\" AudioPID=\"660\"/>      <Channel Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"303\" VideoPID=\"514\" AudioPID=\"670\"/>      <Channel Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"304\" VideoPID=\"515\" AudioPID=\"680\"/>      <Channel Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"305\" VideoPID=\"516\" AudioPID=\"690\"/>      <Channel Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"306\" VideoPID=\"517\" AudioPID=\"700\"/>      <Channel Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"307\" VideoPID=\"518\" AudioPID=\"710\"/>      <Channel Freq=\"331000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"801\" VideoPID=\"513\" AudioPID=\"660\"/>    </RoundStream>  </StreamRoundInfoQuery></Msg>','239.0.1.4',1234,'http://192.168.0.106:8080/Setup1/',4,2,2,'http://192.168.0.169:6701/Setup',0,0,0),(3,'2011-10-19 17:24:11','','239.0.3.1',1234,'',5,1,1,'http://192.168.0.169:6701/Setup',0,0,0),(4,'2011-10-19 17:24:11','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Msg Version=\"2.3\" MsgID=\"13943\" Type=\"MonDown\" DateTime=\"2011-10-19 17:26:03\" SrcCode=\"110000G01\" DstCode=\"110100\" SrcURL=\"http://192.168.0.23:8088/Web2.0/servlet/ChangeProgramServlet\" Priority=\"5\">  <ChangeProgramQuery>    <ChangeProgram Index=\"0\" Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"301\" VideoPID=\"512\" AudioPID=\"650\"/>  </ChangeProgramQuery></Msg>','239.0.1.1',1234,'http://192.168.0.101:8080/Setup1/',3,1,2,'http://192.168.0.169:6701/Setup',0,211000,301),(5,NULL,NULL,'239.0.1.1',1234,NULL,1,2,1,'http://192.168.0.169:6701/Setup',0,0,0),(6,NULL,NULL,'239.0.1.1',1234,'http://192.168.0.101:8080/Setup1/',2,2,2,'http://192.168.0.169:6701/Setup',0,0,0);
/*!40000 ALTER TABLE `monitorprogramquery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `replyalarmerrortable`
--

DROP TABLE IF EXISTS `replyalarmerrortable`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `replyalarmerrortable` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `replyXML` text CHARACTER SET utf8,
  `errorMsg` text CHARACTER SET utf8,
  `lastDateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=125511 DEFAULT CHARSET=gb2312 COMMENT='报警上报错误表';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `replyalarmerrortable`
--

LOCK TABLES `replyalarmerrortable` WRITE;
/*!40000 ALTER TABLE `replyalarmerrortable` DISABLE KEYS */;
/*!40000 ALTER TABLE `replyalarmerrortable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `student` (
  `StuID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `StuName` varchar(45) DEFAULT NULL,
  `StuAddress` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`StuID`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='测试存储过程表';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `systemstatus`
--

DROP TABLE IF EXISTS `systemstatus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `systemstatus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `datetime` datetime DEFAULT NULL COMMENT '时间',
  `temperature` int(10) unsigned DEFAULT NULL COMMENT '温度',
  `voltage` int(10) unsigned DEFAULT NULL COMMENT '电压',
  `humidity` int(10) unsigned DEFAULT NULL COMMENT '湿度',
  `status` int(10) unsigned DEFAULT NULL COMMENT '状态',
  `cpu` int(10) unsigned DEFAULT NULL COMMENT 'CPU',
  `harddisk` int(10) unsigned DEFAULT NULL COMMENT '硬盘',
  `mem` int(10) unsigned DEFAULT NULL COMMENT '内存',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='系统状态信息，CPU，内存';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `systemstatus`
--

LOCK TABLES `systemstatus` WRITE;
/*!40000 ALTER TABLE `systemstatus` DISABLE KEYS */;
/*!40000 ALTER TABLE `systemstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `taskrecord`
--

DROP TABLE IF EXISTS `taskrecord`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `taskrecord` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Taskid` varchar(45) DEFAULT NULL,
  `tr_action` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `tr_index` int(10) unsigned DEFAULT NULL,
  `url` varchar(120) CHARACTER SET latin1 DEFAULT NULL,
  `lasttime` varchar(120) DEFAULT NULL,
  `xml` text CHARACTER SET latin1 COMMENT 'XML ?',
  `DayofWeek` varchar(45) DEFAULT '0' COMMENT '1~7??, ALL ??',
  `TaskType` int(10) unsigned DEFAULT NULL COMMENT '0:Time 1:WeeklyTime',
  `StartTime` varchar(45) DEFAULT NULL COMMENT '?? HHMMSS',
  `EndTime` varchar(45) DEFAULT NULL COMMENT '? HHMMSS',
  `StartDateTime` varchar(45) DEFAULT NULL COMMENT '?? YYYYMMDDHHMMSS',
  `EndDateTime` varchar(45) DEFAULT NULL COMMENT '? YYYYMMDDHHMMSS',
  `ExpireDays` int(10) unsigned DEFAULT '0' COMMENT 'Ч',
  `Freq` int(10) unsigned DEFAULT '0' COMMENT '?',
  `ServiceID` int(10) unsigned DEFAULT '0',
  `statusFlag` int(10) unsigned DEFAULT '0' COMMENT '0:Ч 1:Ч',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312 COMMENT='?';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `taskrecord`
--

LOCK TABLES `taskrecord` WRITE;
/*!40000 ALTER TABLE `taskrecord` DISABLE KEYS */;
/*!40000 ALTER TABLE `taskrecord` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-10-28  5:39:04
