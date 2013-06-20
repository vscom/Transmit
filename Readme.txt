

=============== Change Logging ==================

--------------- transmit v2.8 -----------------
2012.3.20
������ĵ��԰汾 - �߽�
1. Fixed StreamRoundInfoQuery Integer.parseInt null error.
2. RecordType 4 �����˺ϳ��ֲ� add By: Bian Jiang 2012.3.21



--------------- transmit v2.7 -----------------
1.�޸����޿���/ֵ������Msg�·����� ����ѵ�忨Ҳ����
2.�����ϱ�ʱ���жϸý�Ŀ��Ƶ�㱨�������Ƿ�رգ���Ϊ�ر�������ñ���
3.�ڱ�channelremapping�������ʱ�� MbpsFlag �����ж� ���������ʼӴ��� �����Զ�¼����� ����״̬Ϊ1 �෴��Ϊ0
��״̬Ϊ1�ĸ������������ļ��������õ� ����ƽ̨��ʾ��Դ���� ������TSC�·�����
ALTER TABLE `transmit`.`channelremapping` ADD COLUMN `MbpsFlag` INT(1) UNSIGNED DEFAULT 0 COMMENT '�������ñ�� 0��׼ 1����׼' AFTER `TscIndex`;
4.�������Զ�¼���·�ʱ�����һ��tscͨ������ ����һ��tsc������׸���� ����   �����·��Զ�¼��֮ǰ���ж���Դ�Ƿ���� �ж����ݣ�
	1����ѯ���ݿ�¼��״̬Ϊ1 �ļ�¼ �������������1 �������������5 
	2����������Э���еı���/�����Ŀ���� ͬ�� �����������5 �����1
	3�����еļ���Э���е�����  ���� �����ļ������¼������ ��ʾ��Դ���� ����ƽ̨ ������ֹ ����tsc�·�
	4�����������¼������ �����ƽ�����䡣
5.��������ʷ��Ƶ�鿴/���ظ�����tsc�·�Э������ �޸ĺ�Ϊֻ��¼�Ƹ��׽�Ŀ��tsc�·�
6. ���ӱ����������ݿ�alarmswitch
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
7.�޸�����ҵ��ʱ�����ʱ���·�Ƶ��ɨ���Э�飬֮ǰЭ��ᵼ�¸����ʶɨ��������
8.�Ż����������㷨
9.��������ͼ��Ŀ��Ϣ�� AlarmTime
SET FOREIGN_KEY_CHECKS=0;
10.�޸ı�������bug  alarmsearchtable ������isSuccess���Ƿ񱨾��ɹ��� �ֶ�
11.�������ӳؿݽ�bug
12.�Ż�tscƽ������ ���ȸ���Ƶ�����
13.���Ӽ������ù���
-- ----------------------------
-- Table structure for alarmtime
-- ----------------------------
CREATE TABLE `alarmtime` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Freq` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Ƶ��',
  `ServiceID` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'serviceid',
  `TaskType` varchar(10) NOT NULL COMMENT '��̨������������Ч����MonthTime����Ч����WeeklyTime����Ч����DayTime',
  `Month` varchar(4) NOT NULL COMMENT '�·�',
  `Day` int(2) unsigned NOT NULL COMMENT '��',
  `StartTime` varchar(10) NOT NULL COMMENT '��ʼʱ��',
  `EndTime` varchar(10) NOT NULL COMMENT '����ʱ��',
  `Type` int(1) unsigned NOT NULL COMMENT '0 ����ͣ����1 ���ڲ�����2 ��ʱͣ����3 ��ʱ����',
  `AlarmEndTime` varchar(20) NOT NULL COMMENT '����ͼ��Чʱ�䣬��Ϊ������Ч',
  `DayofWeek` int(1) unsigned NOT NULL COMMENT '1~7��ʾ��һ����',
  `StartDateTime` varchar(20) NOT NULL COMMENT '��ʼ����',
  `EndDateTime` varchar(20) NOT NULL COMMENT '��������',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=gbk COMMENT='��������ͼ��Ŀ��Ϣ';

-- ----------------------------
-- Records 
-- ----------------------------
10.�޸ı����������ƣ� 
	1.��������δ�ɹ��ϱ�ʱ�� �������浽����������ɾ��������ı�����Ϣ
	  ֮ǰ���ƣ�δ�ϱ��ɹ����ڱ�����Ҳ���ڱ���������
	2.���ӱ������������������ϱ��Ƿ�ɹ�״̬ ������������ɹ����ڱ�����������ɾ���ñ���
	 ������������ϱ�δ�ɹ���ɾ�����ڱ������еı�����Ϣ ֻ���ڱ���������
11.�޸��Զ�¼���·���tsc �Ļ��� �ȸ���  ��Ƶ��һ���Ľ�Ŀ������һ��tsc

--------------- transmit v2.6 -----------------
Bian Jiang
2011.3.30
1. ����EPG��Ϣ�����ݿ��ж�ȡ. ��ҪXMLEPG2DB���ʹ��.
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
2. EPG������������, 4W��������Ҫ1����.
3. Fixed: ����¼��û���ж�, ����Դ�����õ����.
4. ���ݿ����� C:\tomcat-6.0.10\conf\context.xml
maxWait����ʱ�ȴ�ʱ���Ժ���Ϊ��λ  
maxIdle������������  
minIdle����С��������  
maxActive����������� 
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
MySQL�������8Сʱδʹ�ã��ڲ�ѯʹ�õ������ӻᱨ��
�쳣���ƣ�com.mysql.jdbc.CommunicationsException
�쳣��Ϣ: Communications link failure due to underlying exception
�����MySQL5��ǰ�İ汾����Ҫ�޸����ӳ������е�URL�����autoReconnect=true
�����MySQL5 �Ժ�İ汾����Ҫ�޸�my.cnf(����my.ini)�ļ�����[mysqld]�������
wait_timeout = 604800
interactive-timeout = 604800
��λ�����룬�ǵñ��붼��ӣ����������ã�ͨ��show variables�鿴wait_timeout��ֵ��
5. IsEPGFromDataBase EPG��Ϣ�Ƿ�����ݿ�ȡ�� 0:�������ݿ�ȡ������ 1:�����ݿ�ȡ��
<SysInfo IsHasAlarmID="0" IsEPGZip="1" IsEPGFromDataBase="0" IsAutoAlarmReply="1" MaxAutoRecordNum="60" CenterAlarmURL="http://10.134.121.4/interface/receive.asmx" SrcCode="440000M01" DstCode="440000G01"/>
6.�Զ�¼��鿴�������·���ʱ�򲻴���ͨ����
7.����TSC�·���ͨ���� 
ALTER TABLE `transmit`.`channelremapping` ADD COLUMN `TscIndex` INT(10) UNSIGNED DEFAULT 0 COMMENT 'TSC���͵�ͨ����' AFTER `ProgramName`;
8. ����TscIndex, ��TSC�·���ʱ���Զ�ƥ��TSCIndex;
9. �ڸ������Ŀ����TSC��ʱ�����ƽ������, D:\config\TransmitConfig.xml �ļ��е�TSC��ǩ�е�, IndexMin �� IndexMax Ҫ�����豸����TscIndexһ��.
10. ��MSG�ϱ����������ˣ�ͬ������δ�ָ������ϱ����������ڸñ����Ѿ��ָ����������ָ�������Ҳ������
11.�޸ĸ����Ŀ��TSC����ʱ��ͨ���ظ���bug
12.�޸�EPG��Ϣ����Ŀ¼�޷���������
13.�޸� ���޿���������Msg�·�����  //SELECT Devindex,Freq,smgURl FROM channelremapping c where Freq != 0 group by Freq;

--------------- transmit v2.5 -----------------
Bian Jiang
2011.3.30
1. �ж��Ƿ��Զ�¼���Ƿ񳬹������ļ��е����ֵ(MaxAutoRecordNum)
2. �жϱ�����Ϣ��Ƶ���Ƿ���channelremapping���������,��������ھͷ�����ر���
3. Fixed: �ֶ�¼�������ϱ��Ĵ���.
4. ����EPG��Ϣ�����ݿ��ж�ȡ.

--------------- transmit v2.4 -----------------
transmit v2.4 ���ݼ�������ȶ��汾
Bian Jiang
2011.2.18
1. Fixed: Ƶ��ɨ����ܳ��ֶ��MSGͷ��Ϣ
2. Fixed: ����������TSC���������Bug
3. Fixed: ҵ��ʱ�����, ��ʱ�Ҳ����ļ���Bug
4. ������һ��������ʷ��Ĭ�ϰѰ���ǰ�����ݵ��뱨����ʷ�����棬�����Ǹ�����������ݲ���̫�࣬��ౣ���������ݡ�
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
  `AlarmValue` int(10) unsigned DEFAULT '0' COMMENT '0:û�з���. 1:���ڷ���. 2:�ָ���.',
  `AlarmStartTime` datetime DEFAULT NULL COMMENT '��������ʱ��',
  `AlarmEndTime` datetime DEFAULT NULL COMMENT '�����ָ�ʱ��',
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
transmit v2.3 ���ݼ�������ȶ��汾
Bian Jiang
2010.12.14
1. һ��һ���ӺͶ໭�洦��������ȼ�����
2. ����ҵ��ʱ�����Ӷ�ʱ�ɼ���



--------------- transmit v2.1 -----------------
transmit v2.1 ���ݼ�������ȶ��汾
Bian Jiang
2010.11.11
1. ͨ��ӳ��ÿ��Ƶ��������������Ŀ
2. ����ϵͳ״̬�����ǵ�Bug
3. ȥ������ʷ��Ƶ�鿴�����ص��ӳ١�

--------------- transmit v2.0 -----------------
transmit v2.0 ���ݼ�����Ļ����ȶ��汾
Bian Jiang
2010.10.25
1. ʵʱ��Ƶ��صĴ�����RTVMͳһ����
2. ������һЩͨ���Զ�����Bug��
3. �޸����ݿ����ӳ�û�йر�Bug��


--------------- transmit v1.9 -----------------
transmit v1.9 ���ݼ�����ĳ���
Bian Jiang
2010.10.14
1. ��������ͼ��
2. ʵʱ��Ƶ���ֲ���������
3. ������һЩBug��


--------------- transmit v1.8 -----------------
transmit v1.8 ���ݼ�����Ĳ��԰汾
Bian Jiang
2010.9.25
1. [Java]MHP��Ϣ��ѯ
   û�з���URL
2. [Java]Table��Ϣ
1. ��ѯ����
2. ָ��Freq��ѯ, > 1M �Զ�ѹ��Ϊzip
3. [JAVA] EPG����1Mѹ��Ϊzip
4. [JAVA/TSC]�ֶ�¼�������鲥��ַ�Ͷ˿ں�
5. ʵʱ��Ƶ��Ƶ��ɨ��ֿ�
ʵʱ��Ƶ�����ݿ��м�����������RTVS
Ƶ��ɨ�裬Ƶ��ɨ�裬ָ���ѯ�������ļ���ȡ��.
6. [Java/WatchAndSee][����]����Ƶ��ص�RTVS����IP�Ͷ˿ں�
���Э��:
1. �ֶ�ѡ̨
2. �Զ��ֲ�
3. ��ѯ���

--------------- transmit v1.7 -----------------
transmit v1.7 ���ݼ�����Ĳ��԰汾
Bian Jiang
2010.9.23
1. ���ݼ�����Ĳ��԰汾
2. ����ʵʱ��Ƶ�࿴����ѯ�������
3. �������Զ�¼����Զ�������ƣ�ȡ�����·��汾���ж�
4. ��ʵʱ��Ƶ��صĶ����ò�ѯ���ݿ�
5. ͬʱ֧�ֶ�·ʵʱ��Ƶ


--------------- transmit v1.6 -----------------
transmit v1.6 ���ݼ����ʾ�汾
Bian Jiang
2010.6.11
1. ���ݼ����ʾ�汾
2. ͨ���Զ�ӳ��û������

--------------- transmit v1.5 -----------------
transmit v1.5 ���ݼ���ֳ�
Bian Jiang
2010.6.9
1. ʵʱ��Ƶ��Ƶ��ɨ�����һ��ͨ��


--------------- transmit v1.4 -----------------
transmit v1.4 ȫ��֧�ּ������2.3�汾Э��
Bian Jiang
2010.6.8
1. ֧��һ��Ƶ��ͬʱ�����׽�Ŀ.
2. �Զ�¼���Զ�ͨ��ӳ��֧���к�û��ͨ��


--------------- transmit v1.3 -----------------
transmit v1.3 ȫ��֧�ּ������2.3�汾Э��
Bian Jiang
2010.6.6
1. ֧��һ��Ƶ��ͬʱ�����׽�Ŀ.
2. ת���Զ�����ص�Ƶ�����ͨ����ÿ��Ƶ��ֻ��ռ��һ��ͨ��.
3. SMG��TSC��IPM�������ݵ��鲥��ַ�������ļ����ã�UDP�˿ںŲ���ServiceID+��׼��(9000).
4. SMG��TSC��IPM��β���ͳһ��ͨѶ��ʽ�����Զ�¼���޸ĵ�ʱ���͵�ǰƵ���е����н�Ŀ.
5. �Ժ����������ҶԽӵ�ʱ��ֻ�޸�ת����صĴ��룬�������޸�SMG��TSC��IPM, �����Ժ��˫Tuner.
6. ����¼����Զ�¼�������ͬһ��Ƶ�㣬��ʹ��һ��ͨ���š�
?7. ��������¼����ҪSMG��TSC�������ۣ������Ҫ����¼��Ľ�Ŀ�Ѿ�����TSC����ֱ��¼�ƣ��������
��Ҫ����¼��Ľ�Ŀû���ڵ�ǰ��Ƶ��������SMG��TSC��Ҫѡ��һ��Ĭ�ϵ�UDP�˿ںŴ������
?8. ����Զ�¼��ɾ���Ƿ��SMG,TSC�Լ�IPM���������Ϣ��
    a. ���ĳһ��ͨ���պ�ֻ��һ�׽�Ŀ����ʱ���յ�һ���Զ�¼�����������ز�����
?9. û�п��Ǹ�������⡣

--------------- transmit v1.2 -----------------
transmit v1.2 ����֧��2.0��2.3�汾Э��
Bian Jiang

2010.5.30
1. �ֶ�¼������2.3Э���֧�֡�
2. �����ֶ�ѡ̨��2.3Э���֧�֡�
3. �����Զ��ֲ���2.3Э���֧�֡�

2010.5.29
1. �����Զ�¼����ر�����¼����ر�
2. �ڶ����·��Զ�¼�������¼��ʱ�������Զ�¼������¼����Ϣ��������Ӧ��ͨ�������滻�����·�����Ϣ��
�ֱ��͸�����ת������ݼ�⡣
3. ����2.0��2.3�汾Э�顣

TODOList 2010.5.28
1. ȷ��2.2�汾��2.3�汾�ļ��ݡ�
2. �����ݿ�����������ֱ�Ϊ: ��Ŀ�б���Ϣ���Զ�¼����ر�����¼����ر�
3. ����Ƶ��ɨ����Ϣ�������⣬�ر��Ǹ��壬���ű�ǡ�
4. �����Զ�¼�������¼����Ϣ����⡣
5. �ڶ����·��Զ�¼�������¼��ʱ������Ƶ��ɨ�裬�Զ�¼������¼����Ϣ��������Ӧ��ͨ���Լ��Ƿ������Ϣ�����滻�����·�����Ϣ��
�ֱ��͸�����ת�룬����ת������ݼ�⡣

1. Ƶ��ɨ��SDT ͨ����Ƶ��ȡ������Ƶ��Ľ�Ŀ���ơ�
2. ȡ�ø���ʶ���ǡ�
3. ָ����ƵPID�����ص���Ƶ��ת������ݼ�⡣


--------------- transmit v1.1 -----------------
transmit v1.1 ��ͨ��Ϊ����İ汾
Bian Jiang

2010.5.25
1. ���Ӷ�Ӳ������ת�뿨��֧��, Э�����: 
    * ��Ƶת��¼��Ĭ�ϲ�������
    * ʵʱ��������
    * �Զ�¼������

2010.5.21
* �����ļ�Ƶ��ɨ���ָ���ѯ������ChannelScanQuery

    
