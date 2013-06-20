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
  `freq` varchar(10) DEFAULT '0' COMMENT '�����ñ������ص�Ƶ�� arr��������Ƶ��',
  `serviceid` varchar(10) DEFAULT '0' COMMENT '��Ŀ���������� arr�����Ƶ���ȫƵ��',
  `switchvalue` int(1) DEFAULT '0' COMMENT '�������ص�״̬ 0:����� 1:����',
  `switchtype` int(1) DEFAULT '0' COMMENT '�ñ����������� ��Ŀ:1 Ƶ��:2',
  `alarmtype` int(10) DEFAULT '0' COMMENT '��������',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=gb2312 COMMENT='�������ؼ�¼';
SET character_set_client = @saved_cs_client;


ALTER TABLE `transmit`.`channelremapping` ADD COLUMN `TscIndex` INT(10) UNSIGNED DEFAULT 0 COMMENT 'TSC���͵�ͨ����' AFTER `ProgramName`;
ALTER TABLE `transmit`.`channelremapping` ADD COLUMN `MbpsFlag` INT(1) UNSIGNED DEFAULT 0 COMMENT '�������ñ�� 0��׼ 1����׼' AFTER `TscIndex`;
