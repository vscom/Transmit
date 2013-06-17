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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `AlarmDesc` varchar(45) DEFAULT NULL,
  `AlarmValue` int(10) unsigned DEFAULT '0' COMMENT '0:没有发生. 1:正在发生. 2:恢复了.',
  `AlarmStartTime` datetime DEFAULT NULL COMMENT '报警发生时间',
  `AlarmEndTime` datetime DEFAULT NULL COMMENT '报警恢复时间',
  `Lastdatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `alarmsearchtable`
--

LOCK TABLES `alarmsearchtable` WRITE;
/*!40000 ALTER TABLE `alarmsearchtable` DISABLE KEYS */;
INSERT INTO `alarmsearchtable` VALUES (78,658000,0,0,0,1,'???',2,'2011-04-21 16:56:01','2011-04-21 17:00:26','2011-04-21 17:07:21'),(79,658000,0,0,0,1,'???',2,'2011-04-21 17:08:06','2011-04-21 17:11:44','2011-04-21 17:11:44'),(80,658000,0,0,0,1,'???',2,'2011-04-21 17:19:48','2011-04-21 17:21:01','2011-04-21 17:21:01'),(81,658000,10,2060,2061,31,'???',1,'2002-08-17 15:30:00',NULL,'2011-04-21 17:23:07'),(82,658000,0,0,0,31,'???',1,'2011-04-21 17:49:21',NULL,'2011-04-21 17:49:22'),(83,658000,0,0,0,1,'???',1,'2011-04-21 17:50:09',NULL,'2011-04-21 17:50:09'),(84,115000,800,512,650,31,'???',1,'2002-08-17 15:30:00',NULL,'2011-05-12 21:05:01'),(85,115000,800,512,650,32,'???',1,'2002-08-17 15:30:00',NULL,'2011-05-12 21:05:01'),(86,115000,800,512,650,33,'?????',1,'2002-08-17 15:30:00',NULL,'2011-05-12 21:05:01'),(87,115000,0,0,0,1,'???',1,'2011-05-12 21:43:53',NULL,'2011-05-12 21:44:05');
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
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=gb2312 COMMENT='报警开关记录';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `alarmswitch`
--

LOCK TABLES `alarmswitch` WRITE;
/*!40000 ALTER TABLE `alarmswitch` DISABLE KEYS */;
INSERT INTO `alarmswitch` VALUES (80,'115000','0',1,2,1),(81,'115000','0',1,2,1),(82,'115000','0',1,2,1);
/*!40000 ALTER TABLE `alarmswitch` ENABLE KEYS */;
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
INSERT INTO `channelprogramstatus` VALUES (1,2,1,'1',1,1,1,'2010-06-02 16:46:15',2,0,0),(1,658000,6875,'64',10,2060,2061,'2010-05-29 16:15:42',2,1,0),(2,474000,6875,'64',6,2060,2061,'2010-06-06 18:27:34',2,0,0),(2,474000,6875,'64',7,2060,2061,'2010-06-06 19:12:26',2,0,0),(3,698000,6875,'64',1,2062,2063,'2010-09-06 16:14:41',2,0,0),(11,722000,6875,'64',5,2060,2061,'2010-05-29 18:43:49',2,1,0),(12,716000,6875,'64',5,2062,2063,'2010-05-29 18:43:49',2,1,0);
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
  `RecordType` int(10) unsigned DEFAULT '0' COMMENT '录像类型: 0：不录像，1:代表故障触发录制   2：24小时录像 3:任务录像',
  `DelFlag` int(10) unsigned DEFAULT '0',
  `udp` varchar(45) DEFAULT NULL COMMENT '组播地址',
  `port` int(10) unsigned DEFAULT NULL COMMENT '组播端口',
  `Action` varchar(45) DEFAULT NULL COMMENT '执行操作',
  `smgURL` varchar(45) DEFAULT NULL COMMENT 'SMG接收URL',
  `ProgramName` varchar(45) DEFAULT NULL COMMENT '节目名称',
  `TscIndex` int(10) unsigned DEFAULT '0' COMMENT 'TSC发送的通道号',
  `MbpsFlag` int(1) unsigned DEFAULT '0' COMMENT '码率设置标记 0标准 1超标准',
  PRIMARY KEY (`channelindex`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `channelremapping`
--

LOCK TABLES `channelremapping` WRITE;
/*!40000 ALTER TABLE `channelremapping` DISABLE KEYS */;
INSERT INTO `channelremapping` VALUES (1,1,0,6875,'64',0,0,0,0,'2011-05-13 15:58:54',0,0,0,0,'',0,'','','',0,0),(1,2,115000,6875,'64',801,513,660,1,'2011-04-22 18:54:54',0,1,2,0,'239.0.0.28',2801,'Set','http://192.168.0.60:8080/Setup1/','CCTV-HD',30,0),(1,3,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(1,4,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(1,5,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(1,6,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(1,7,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(1,8,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(2,9,123000,6875,'64',301,512,650,1,'2011-04-22 18:54:54',0,1,2,0,'239.0.0.30',2301,'Set','http://192.168.0.60:8080/Setup2/','CCTV-1',2,0),(2,10,123000,6875,'64',302,513,660,1,'2011-04-22 18:54:54',0,1,2,0,'239.0.0.30',2302,'Set','http://192.168.0.60:8080/Setup2/','CCTV-2',33,0),(2,11,123000,6875,'64',303,514,670,1,'2011-04-22 18:54:54',0,1,2,0,'239.0.0.30',2303,'Set','http://192.168.0.60:8080/Setup2/','CCTV-7',3,0),(2,12,123000,6875,'64',304,515,680,1,'2011-04-22 18:54:55',0,1,2,0,'239.0.0.30',2304,'Set','http://192.168.0.60:8080/Setup2/','CCTV-10',34,0),(2,13,123000,6875,'64',305,516,690,1,'2011-04-22 18:54:55',0,1,2,0,'239.0.0.30',2305,'Set','http://192.168.0.60:8080/Setup2/','CCTV-11',4,0),(2,14,123000,6875,'64',306,517,700,1,'2011-04-22 18:54:55',0,1,2,0,'239.0.0.30',2306,'Set','http://192.168.0.60:8080/Setup2/','CCTV-12',35,0),(2,15,123000,6875,'64',307,518,710,1,'2011-04-22 18:54:55',0,1,2,0,'239.0.0.30',2307,'Set','http://192.168.0.60:8080/Setup2/','CCTV-MUSIC',5,0),(2,16,123000,6875,'64',65534,8765,0,1,'2011-04-22 18:54:55',0,1,2,0,'239.0.0.30',67534,'Set','http://192.168.0.60:8080/Setup2/','Skystream data',36,0),(3,17,211000,6875,'64',1,103,104,1,'2011-04-22 18:54:55',0,1,2,0,'239.0.0.52',2001,'Set','http://192.168.0.61:8080/Setup1/','FASHION TV',6,0),(3,18,0,6875,'64',0,0,0,0,'2011-03-02 14:08:24',0,0,2,0,'239.0.0.66',2103,'Set',NULL,'null',0,0),(3,19,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(3,20,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(3,21,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(3,22,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(3,23,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(3,24,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(4,25,0,6875,'64',0,0,0,0,'2011-05-13 16:21:55',0,0,0,0,'',0,'','','',0,0),(4,26,0,6875,'64',0,0,0,0,'2011-04-22 18:08:47',0,0,0,0,'',0,'','','',0,0),(4,27,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(4,28,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(4,29,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(4,30,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(4,31,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(4,32,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(5,33,0,6875,'64',0,0,0,0,'2011-05-13 16:21:55',0,0,0,0,'',0,'','','',0,0),(5,34,0,6875,'64',0,0,0,0,'2011-04-22 18:08:47',0,0,0,0,'',0,'','','',0,0),(5,35,0,6875,'64',0,0,0,0,'2011-04-22 18:08:47',0,0,0,0,'',0,'','','',0,0),(5,36,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(5,37,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(5,38,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(5,39,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(5,40,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(6,41,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(6,42,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(6,43,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(6,44,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(6,45,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(6,46,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(6,47,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(6,48,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(7,49,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(7,50,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(7,51,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(7,52,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(7,53,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(7,54,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(7,55,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(7,56,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(8,57,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(8,58,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(8,59,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(8,60,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(8,61,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(8,62,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(8,63,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(8,64,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(9,65,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(9,66,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(9,67,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(9,68,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(9,69,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(9,70,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(9,71,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(9,72,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(10,73,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(10,74,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(10,75,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(10,76,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(10,77,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(10,78,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(10,79,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(10,80,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(11,81,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(11,82,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(11,83,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(11,84,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(11,85,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(11,86,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(11,87,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(11,88,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(12,89,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(12,90,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(12,91,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(12,92,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(12,93,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(12,94,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(12,95,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(12,96,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(13,97,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(13,98,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(13,99,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(13,100,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(13,101,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(13,102,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(13,103,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(13,104,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(14,105,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(14,106,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(14,107,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(14,108,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(14,109,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(14,110,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(14,111,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(14,112,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(15,113,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(15,114,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(15,115,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(15,116,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(15,117,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(15,118,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(15,119,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(15,120,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(16,121,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(16,122,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(16,123,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(16,124,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(16,125,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(16,126,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(16,127,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(16,128,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(17,129,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(17,130,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(17,131,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(17,132,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(17,133,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(17,134,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(17,135,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(17,136,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(18,137,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(18,138,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(18,139,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(18,140,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(18,141,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(18,142,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(18,143,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(18,144,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(19,145,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(19,146,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(19,147,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(19,148,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(19,149,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(19,150,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(19,151,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(19,152,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(20,153,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(20,154,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(20,155,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(20,156,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(20,157,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(20,158,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0),(20,160,0,6875,'64',0,0,0,0,NULL,0,0,0,0,NULL,NULL,NULL,NULL,NULL,0,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=gb2312;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `channelscanlist`
--

LOCK TABLES `channelscanlist` WRITE;
/*!40000 ALTER TABLE `channelscanlist` DISABLE KEYS */;
INSERT INTO `channelscanlist` VALUES (1,115000,'64',6875,'CCTV1-HD',800,512,650,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(2,115000,'64',6875,'CCTV-HD',801,513,660,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(3,123000,'64',6875,'CCTV-1',301,512,650,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(4,123000,'64',6875,'CCTV-2',302,513,660,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(5,123000,'64',6875,'CCTV-7',303,514,670,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(6,123000,'64',6875,'CCTV-10',304,515,680,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(7,123000,'64',6875,'CCTV-11',305,516,690,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(8,123000,'64',6875,'CCTV-12',306,517,700,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(9,123000,'64',6875,'CCTV-MUSIC',307,518,710,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(10,123000,'64',6875,'Skystream data',65534,8765,0,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(11,211000,'64',6875,'FASHION TV',1,103,104,0,1,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(12,211000,'64',6875,'Lotus     ',2,202,203,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(13,211000,'64',6875,'Sun TV    ',3,302,303,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(14,211000,'64',6875,'Channel [V',4,402,403,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(15,211000,'64',6875,'FOX News  ',5,502,503,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(16,211000,'64',6875,'STAR Movie',6,602,603,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(17,331000,'64',6875,'Ahtv     S',1,102,103,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(18,331000,'64',6875,'Hebei_TV  ',2,202,203,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(19,331000,'64',6875,'Tianjin TV',3,302,303,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(20,331000,'64',6875,'BTV       ',4,402,403,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(21,355000,'64',6875,'fjtv',1,102,103,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(22,355000,'64',6875,'hntv',2,202,203,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(23,355000,'64',6875,'gdtv',3,302,303,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(24,355000,'64',6875,'SZTV',4,402,403,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(25,355000,'64',6875,'SHTV',5,502,503,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(26,355000,'64',6875,'CCTVNEWS',6,602,603,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(27,850000,'64',6875,'CCTV 4',1,512,650,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(28,850000,'64',6875,'CCTV 9',2,513,660,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(29,850000,'64',6875,'CCTV OPERA',3,514,670,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(30,850000,'64',6875,'BEIJING',101,2100,2101,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(31,850000,'64',6875,'SHANGHAI',102,2292,2293,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:03',1),(32,850000,'64',6875,'JIANGSU',103,4092,4093,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:04',1),(33,850000,'64',6875,'HUNAN',104,4272,4273,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:04',1),(34,850000,'64',6875,'FUJIAN',105,4812,4813,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:04',1),(35,850000,'64',6875,'XIAMEN',106,4818,4819,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:04',1),(36,850000,'64',6875,'GUANGDONG',107,4992,4993,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:04',1),(37,850000,'64',6875,'SHENZHENG',108,4998,4999,0,0,'2010-11-23 11:19:15','2010-12-31 11:34:04',1);
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
INSERT INTO `channelstatus` VALUES (1,259000,6875,'QAM64',102,2021,2022,'2010-10-17 14:31:37',2),(2,275000,6875,'QAM64',127,4421,4422,'2010-10-11 09:35:28',2),(3,474000,6875,'64',1,816,818,'2010-05-28 14:57:33',2),(4,482000,6875,'QAM64',11,2062,2063,'2010-05-27 16:57:41',2),(5,634000,6875,'QAM64',1,816,818,'2010-05-27 16:56:31',2),(6,474000,6875,'64',1,816,818,'2010-05-28 14:58:26',2),(7,474000,6875,'64',1,816,818,'2010-05-28 14:58:26',2),(8,618000,6875,'QAM64',1,816,818,'2010-05-27 22:43:33',2),(9,618000,6875,'QAM64',1,816,818,'2010-05-27 22:43:57',2),(10,474000,6875,'64',1,816,818,'2010-05-28 14:58:26',2),(11,722000,6875,'QAM64',1,816,818,'2010-05-27 22:12:07',2),(12,634000,6875,'QAM64',1,816,818,'2010-05-27 17:06:16',2),(13,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(14,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(16,403000,6875,'',1,1020,1021,'2010-05-24 14:37:15',2),(17,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(18,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(19,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(20,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(21,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(22,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(23,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(24,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(25,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(26,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(27,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(28,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(29,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(30,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(31,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2),(32,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2);
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
  `Program` varchar(45) DEFAULT NULL,
  `ProgramType` varchar(45) DEFAULT NULL,
  `StartTime` datetime DEFAULT NULL,
  `ProgramLen` varchar(45) DEFAULT NULL,
  `State` varchar(45) DEFAULT NULL,
  `Encryption` int(10) unsigned DEFAULT NULL,
  `Lastdatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`epg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24526 DEFAULT CHARSET=utf8 COMMENT='EPG Infomation';
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
INSERT INTO `monitorprogramquery` VALUES (1,'2010-10-23 21:40:00','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Msg Version=\"2.3\" MsgID=\"33293\" Type=\"MonDown\" DateTime=\"2010-10-23 21:40:02\" SrcCode=\"440000G01\" DstCode=\"440000M01\" SrcURL=\"http://10.134.121.4:100/interface/receive.asmx\" Priority=\"1\"><MonitorProgramQuery><MonitorProgram Index=\"0\" Freq=\"259000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"105\" VideoPID=\"2031\" AudioPID=\"2032\"/></MonitorProgramQuery></Msg>','239.0.0.64',2105,'',1,2,25,'http://192.168.2.238:6701/Setup',0,259000,105),(2,'2010-10-13 14:21:16','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Msg Version=\"2.3\" MsgID=\"15683\" Type=\"MonDown\" DateTime=\"2010-10-13 14:21:16\" SrcCode=\"440000G01\" DstCode=\"440000M01\" SrcURL=\"http://10.134.121.4/interface/receive.asmx\" Priority=\"1\"><MonitorProgramQuery><MonitorProgram Index=\"0\" Freq=\"299000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"133\" VideoPID=\"4111\" AudioPID=\"4112\"/></MonitorProgramQuery></Msg>','239.0.1.4',1234,'http://192.168.0.108:8080/Setup1/',4,2,23,'http://192.168.2.238:6701/Setup',0,NULL,NULL),(3,NULL,NULL,'239.0.3.1',1234,NULL,5,0,24,'http://192.168.2.238:6701/Setup',0,0,0),(4,'2011-01-18 10:09:11','<?xml version=\"1.0\" encoding=\"GB2312\"?>\n<Msg Version=\"4\" MsgID=\"6\" Type=\"MonUp\" DateTime=\"2011-1-18 10:09:51\" SrcCode=\"330000\" DstCode=\"110000G01\" ReplyID=\"14983\">\n  <Return Type=\"ProgramPatrol\" Value=\"0\" Desc=\"\"/>\n  <ReturnInfo>\n    <PatrolGroup Index=\"1\">\n      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"306\" VideoPID=\"517\" AudioPID=\"700\"/>\n      <Channel Freq=\"211000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"800\" VideoPID=\"512\" AudioPID=\"650\"/>\n      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"65534\" VideoPID=\"8765\" AudioPID=\"0\"/>\n      <Channel Freq=\"123000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"307\" VideoPID=\"518\" AudioPID=\"710\"/>\n      <Channel Freq=\"339000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"5\" VideoPID=\"502\" AudioPID=\"503\"/>\n    </PatrolGroup>\n    <PatrolGroup Index=\"2\">\n      <Channel Freq=\"355000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"1\" VideoPID=\"512\" AudioPID=\"650\"/>\n    </PatrolGroup>\n  </ReturnInfo>\n</Msg>','239.0.2.2',1234,'http://192.168.0.100:8080/Setup1/',2,3,27,'http://192.168.2.238:6701/Setup',1,0,0),(5,'2010-11-09 14:57:17','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Msg Version=\"2.3\" MsgID=\"93702\" Type=\"MonDown\" DateTime=\"2010-11-09 14:57:16\" SrcCode=\"440000G01\" DstCode=\"440000M01\" SrcURL=\"http://10.134.121.4:100/interface/receive.asmx\" Priority=\"1\"><ChangeProgramQuery><ChangeProgram Index=\"-1\" Freq=\"339000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"285\" VideoPID=\"2851\" AudioPID=\"2852\"/></ChangeProgramQuery></Msg>','239.0.1.1',1234,'http://192.168.0.86:8080/Setup2/',3,1,22,'http://192.168.2.238:6701/Setup',0,339000,285);
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
  `replyXML` text,
  `errorMsg` text,
  `lastDateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='报警上报错误表';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `replyalarmerrortable`
--

LOCK TABLES `replyalarmerrortable` WRITE;
/*!40000 ALTER TABLE `replyalarmerrortable` DISABLE KEYS */;
/*!40000 ALTER TABLE `replyalarmerrortable` ENABLE KEYS */;
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
  `xml` text CHARACTER SET latin1 COMMENT 'XML Ϣ',
  `DayofWeek` varchar(45) DEFAULT '0' COMMENT '1~7ʾһ, ALL ʾÿ',
  `TaskType` int(10) unsigned DEFAULT NULL COMMENT '0:Time 1:WeeklyTime',
  `StartTime` varchar(45) DEFAULT NULL COMMENT 'ʼʱ HHMMSS',
  `EndTime` varchar(45) DEFAULT NULL COMMENT 'ʱ HHMMSS',
  `StartDateTime` varchar(45) DEFAULT NULL COMMENT 'ʼʱ YYYYMMDDHHMMSS',
  `EndDateTime` varchar(45) DEFAULT NULL COMMENT 'ʱ YYYYMMDDHHMMSS',
  `ExpireDays` int(10) unsigned DEFAULT '0' COMMENT 'Ч',
  `Freq` int(10) unsigned DEFAULT '0' COMMENT 'Ƶ',
  `ServiceID` int(10) unsigned DEFAULT '0',
  `statusFlag` int(10) unsigned DEFAULT '0' COMMENT '0:Ч 1:Ч',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=gb2312 COMMENT='¼';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `taskrecord`
--

LOCK TABLES `taskrecord` WRITE;
/*!40000 ALTER TABLE `taskrecord` DISABLE KEYS */;
INSERT INTO `taskrecord` VALUES (1,'1','set',1,NULL,'2011-04-01 10:00:00',NULL,'0',NULL,'12:09:03','12:15:03','2011-03-02 12:09:03','2011-04-02 13:09:03',1,482000,123,0);
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

-- Dump completed on 2011-05-20  7:30:38
