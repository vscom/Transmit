use transmit;


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


ALTER TABLE `transmit`.`channelremapping` ADD COLUMN `TscIndex` INT(10) UNSIGNED DEFAULT 0 COMMENT 'TSC发送的通道号' AFTER `ProgramName`;
ALTER TABLE `transmit`.`channelremapping` ADD COLUMN `MbpsFlag` INT(1) UNSIGNED DEFAULT 0 COMMENT '码率设置标记 0标准 1超标准' AFTER `TscIndex`;
