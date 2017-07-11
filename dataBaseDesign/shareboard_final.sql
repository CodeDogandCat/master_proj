CREATE DATABASE  IF NOT EXISTS `board` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `board`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 118.89.102.238    Database: board
-- ------------------------------------------------------
-- Server version	5.5.55-0ubuntu0.14.04.1

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
-- Table structure for table `bd_friend`
--

DROP TABLE IF EXISTS `bd_friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_friend` (
  `friend_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `response_status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '回复状态：1：未回复 2：拒绝 3：同意 4.好友关系已经删除 ',
  `message_time` datetime NOT NULL COMMENT '消息时间',
  `bd_user_user_id` int(10) unsigned NOT NULL,
  `bd_user_user_id1` int(10) unsigned NOT NULL,
  PRIMARY KEY (`friend_id`),
  KEY `fk_bd_add_friend_bd_user1_idx` (`bd_user_user_id`),
  KEY `fk_bd_add_friend_bd_user2_idx` (`bd_user_user_id1`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_friend`
--

LOCK TABLES `bd_friend` WRITE;
/*!40000 ALTER TABLE `bd_friend` DISABLE KEYS */;
INSERT INTO `bd_friend` VALUES (1,3,'2017-05-03 04:28:07',6,8),(2,3,'2017-05-03 04:28:07',6,9),(3,4,'2017-05-03 21:45:54',6,10),(4,3,'2017-05-03 04:28:07',6,11),(5,3,'2017-05-03 04:28:07',6,12),(6,3,'2017-05-03 21:17:34',7,12),(7,3,'2017-05-03 20:51:42',6,14),(8,3,'2017-05-03 21:27:21',6,15),(9,3,'2017-05-09 00:08:12',6,7),(10,3,'2017-05-03 21:27:21',6,16),(11,3,'2017-05-03 21:27:21',6,17),(26,4,'2017-06-11 00:09:38',7,40);
/*!40000 ALTER TABLE `bd_friend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bd_log`
--

DROP TABLE IF EXISTS `bd_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_log` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `log_device_info` varchar(45) DEFAULT NULL COMMENT '设备信息',
  `log_error_info` text COMMENT '报错信息',
  `log_app_version` varchar(45) DEFAULT NULL COMMENT '应用程序版本',
  `bd_user_user_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`log_id`),
  KEY `fk_bd_log_bd_user1_idx` (`bd_user_user_id`),
  CONSTRAINT `fk_bd_log_bd_user1` FOREIGN KEY (`bd_user_user_id`) REFERENCES `bd_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_log`
--

LOCK TABLES `bd_log` WRITE;
/*!40000 ALTER TABLE `bd_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `bd_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bd_meeting`
--

DROP TABLE IF EXISTS `bd_meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_meeting` (
  `meeting_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `meeting_url` varchar(12) NOT NULL COMMENT '12位数字',
  `meeting_theme` varchar(20) NOT NULL COMMENT '预定会议主题',
  `meeting_host_user_id` int(10) unsigned NOT NULL,
  `meeting_is_drawable` tinyint(4) NOT NULL COMMENT '与会者默认可画  1 不能 2 可以',
  `meeting_is_talkable` tinyint(4) NOT NULL COMMENT '与会者默认可说 1 不能 2 可以',
  `meeting_is_add_to_calendar` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否添加到日历 1：不添加 2：添加',
  `meeting_password` text NOT NULL COMMENT '密码',
  `meeting_start_time` bigint(20) DEFAULT NULL COMMENT '会议开始时间',
  `meeting_end_time` bigint(20) DEFAULT NULL,
  `meeting_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '预约状态：1 ：未开始并且未到期 2：未开始并且过期了 3：正在进行 4：开会结束  5.锁定',
  `event_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '日历事件ID',
  `meeting_desc` text NOT NULL COMMENT '会议描述',
  PRIMARY KEY (`meeting_id`,`meeting_url`),
  KEY `meeting_host_user_id_idx` (`meeting_host_user_id`),
  CONSTRAINT `meeting_host_user_id` FOREIGN KEY (`meeting_host_user_id`) REFERENCES `bd_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=210 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_meeting`
--

LOCK TABLES `bd_meeting` WRITE;
/*!40000 ALTER TABLE `bd_meeting` DISABLE KEYS */;
INSERT INTO `bd_meeting` VALUES (191,'593ba6ea6e88','2131099751lei的会议',7,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497081572016,1497085172016,4,-1,''),(192,'593ba8763084','2131099751fssf的会议',39,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497081967581,1497085567582,3,-1,''),(193,'593bb00540ab','2131099751fssf的会议',39,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497083908771,1497087508771,4,-1,''),(195,'593bb5124c37','2131099751fssf的会议',39,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497085196157,1497088796157,4,-1,''),(196,'593bb61d1e7e','2131099751fssf的会议',39,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497085463517,1497089063517,4,-1,''),(197,'593bb7325cbf','2131099751fsfs的会议',40,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497085740059,1497089340059,3,-1,''),(198,'593c02660928','2131099751fsfs的会议',40,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497104991596,1497108591596,4,-1,''),(199,'593c03825c71','2131099751lei的会议',7,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497105276371,1497108876371,4,-1,''),(200,'593c05283b6d','2131099751lei的会议',7,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497105698001,1497109298001,4,-1,''),(201,'593c066f0b94','2131099751lei的会议',7,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497106024934,1497109624934,4,-1,''),(202,'593c079414cd','li333lei的会议',7,0,0,0,'B888023135EEFF8150BDD184215D5ADF',1497106917563,1497110577563,2,-1,''),(203,'593c0fc85d6f','2131099751leilei的会议',40,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497108417622,1497112017622,3,-1,''),(204,'593c107d57a1','2131099751leilei的会议',40,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497108598915,1497112198916,3,-1,''),(205,'593c11406353','2131099751leilei的会议',40,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497108793448,1497112393448,4,-1,''),(206,'593c154a2616','2131099751leilei的会议',40,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497109828017,1497113428017,4,-1,''),(207,'593c16a67393','2131099751lei的会议',7,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497110175816,1497113775816,4,-1,''),(208,'593c18b01ab4','2131099751lei的会议',7,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497110697894,1497114297894,4,-1,''),(209,'593c19b1558f','li333lei的会议',7,1,1,0,'B888023135EEFF8150BDD184215D5ADF',1497114014526,1497117614526,1,-1,'');
/*!40000 ALTER TABLE `bd_meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bd_reset_password`
--

DROP TABLE IF EXISTS `bd_reset_password`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_reset_password` (
  `reset_id` int(11) NOT NULL AUTO_INCREMENT,
  `reset_token` tinyint(6) NOT NULL COMMENT '验证码',
  `reset_begin_time` datetime NOT NULL COMMENT '最近发送验证码的时间点（验证码2分钟有效）',
  `reset_result` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '重置结果：1：失败 2：成功',
  `bd_user_user_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`reset_id`),
  KEY `fk_bd_reset_password_bd_user1_idx` (`bd_user_user_id`),
  CONSTRAINT `fk_bd_reset_password_bd_user1` FOREIGN KEY (`bd_user_user_id`) REFERENCES `bd_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_reset_password`
--

LOCK TABLES `bd_reset_password` WRITE;
/*!40000 ALTER TABLE `bd_reset_password` DISABLE KEYS */;
/*!40000 ALTER TABLE `bd_reset_password` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bd_user`
--

DROP TABLE IF EXISTS `bd_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_user` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_email` varchar(30) NOT NULL COMMENT '邮箱',
  `user_family_name` varchar(15) NOT NULL COMMENT '姓',
  `user_given_name` varchar(15) NOT NULL COMMENT '名',
  `user_register_time` datetime NOT NULL COMMENT '注册时间',
  `user_login_recent_time` datetime NOT NULL COMMENT '最近登录时间',
  `user_status` int(10) unsigned NOT NULL DEFAULT '1' COMMENT '用户状态：1：未登录 2 ：登录空闲  会议ID: 登录会议中',
  `user_class` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '用户等级：1：免费用户 2：普通会员 3.超级会员',
  `user_password` varchar(32) NOT NULL COMMENT '密码',
  `user_token` varchar(32) NOT NULL COMMENT '用户唯一 TOKEN',
  `user_avatar` varchar(60) NOT NULL,
  PRIMARY KEY (`user_id`,`user_email`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  UNIQUE KEY `user_email_UNIQUE` (`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_user`
--

LOCK TABLES `bd_user` WRITE;
/*!40000 ALTER TABLE `bd_user` DISABLE KEYS */;
INSERT INTO `bd_user` VALUES (6,'1662083658@qq.com','李','2222','2017-04-20 23:03:46','2017-04-20 23:03:46',1,1,'ed0b18fa6d463e56559b13d8621380ab','80e7ea1e9b00608283945f63f1a1fa0f','/2662083658@qq.com.2017_06_06_09_57_09.jpeg'),(7,'l2662083658l@gmail.com','li333','lei','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'ec64886af9e3a804d09a75be882a44f3','649f7f619665b3623778df81e19c1b8d','/l2662083658l@gmail.com.2017_06_06_10_37_15.jpeg'),(8,'l3662083658l@gmail.com','lfsfs','ggg','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'90a64a16cdca18b82c2c745739661f88','198aded7a3c663730ae7ead6152f6595','/l3662083658l@gmail.com.2017_05_02_21_14_06.jpeg'),(9,'l4662083658l@gmail.com','布拉恩特','科比','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'da872f9bcabef1247a3cc359c8d5b886','198aded7a3c663730ae7ead6152f6596','/l4662083658l@gmail.com.2017_05_02_21_14_06.jpeg'),(10,'l5662083658l@gmail.com','华莱士','拉希德','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'a7568109e230d63ad5d25ab41da13a8c','198aded7a3c663730ae7ead6152f6597','/l5662083658l@gmail.com.2017_05_02_21_14_06.jpeg'),(11,'l6662083658l@gmail.com','詹姆斯','勒布朗','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'7443d5eda1219dcc4a08bdde9f88c0c6','198aded7a3c663730ae7ead6152f6598','/l6662083658l@gmail.com.2017_05_02_21_14_06.jpeg'),(12,'l7662083658l@gmail.com','curry30','史蒂芬','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'bed50cedfb267b2085d0b248bde1904f','198aded7a3c663730ae7ead6152f6514','/l7662083658l@gmail.com.2017_05_02_21_14_06.jpeg'),(13,'l8662083658l@gmail.com','杜兰特','kaiwen','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'a7522c1aa5b50c31805d2801082d1566','198aded7a3c663730ae7ead6152f6599','/l8662083658l@gmail.com.2017_05_02_21_14_06.jpeg'),(14,'l9662083658l@gmail.com','利拉德','达米恩','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'1cf78801acc94c26d023130896c6d9f8','198aded7a3c663730ae7ead6152f6509','/l9662083658l@gmail.com.2017_05_02_21_14_06.jpeg'),(15,'l9162083658l@gmail.com','托马斯','以赛亚','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'a400b990680cd075442e667940b9dd6f','198aded7a3c663730ae7ead6252f6509','/l9162083658l@gmail.com.2017_05_02_21_14_06.jpeg'),(16,'l9262083658l@gmail.com','Love','Kevin','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'b85fc683423f0d32064a575dec3c5cd8','198aded7a3c663730ae7ead6352f6509',''),(17,'l9362083658l@gmail.com','Irving','Kyrie','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'21cd638def415c50bfd1618bf578a833','198aded7a3c663730ae7ead6452f6509',''),(18,'l9462083658l@gmail.com','Smith','J.R.','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'03526b86f9e42fc2b7c32cced5f9d3ff','198aded7a3c663730ae7ead6552f6509',''),(19,'l9562083658l@gmail.com','Thompson','Tristan','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'4ec8fb9173ad345b43a526db38f19a3a','198aded7a3c663730ae7ead6652f6509',''),(20,'l9612083658l@gmail.com','Frye','Channing','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'855e27bffd0da550872a7222e3166799','198aded7a3c663730ae7ead6752f6509',''),(21,'l9762083658l@gmail.com','Korver','Kyle','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'5502d949e1a2d72052f33bff041ea043','198aded7a3c663730ae7ead6852f6509',''),(39,'3662083658@qq.com','fsfs','fssf','2017-06-10 16:06:06','2017-06-10 16:06:06',1,1,'5e44faa2062c939b34c66cb367483961','bdb2397efa338cf037be010767a0643f','/2662083658@qq.com.2017_06_10_16_06_06.jpeg'),(40,'4662083658@qq.com','lili','leilei','2017-06-10 17:06:40','2017-06-10 17:06:40',1,1,'5e44faa2062c939b34c66cb367483961','8e3ac2061e0b5b92d54061464051f778','/2662083658@qq.com.2017_06_10_17_06_40.jpeg'),(41,'2662083658@qq.com','lilies','fsff','2017-06-11 00:16:55','2017-06-11 00:16:55',1,1,'5e44faa2062c939b34c66cb367483961','68111b0e7164e7e043b4128faea9f96f','/2662083658@qq.com.2017_06_11_00_16_55.jpeg');
/*!40000 ALTER TABLE `bd_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bd_user_and_meeting`
--

DROP TABLE IF EXISTS `bd_user_and_meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_user_and_meeting` (
  `user_and_meeting_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `check_in_type` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '参会类型：1 ：与会 2：主持会议',
  `check_in_time` datetime NOT NULL COMMENT '最早入会时间',
  `check_out_time` datetime NOT NULL COMMENT '最迟离会时间',
  `bd_user_user_id` int(10) unsigned NOT NULL,
  `bd_meeting_meeting_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`user_and_meeting_id`),
  KEY `fk_bd_user_and_meeting_bd_user1_idx` (`bd_user_user_id`),
  KEY `fk_bd_user_and_meeting_bd_meeting1_idx` (`bd_meeting_meeting_id`),
  CONSTRAINT `fk_bd_user_and_meeting_bd_meeting1` FOREIGN KEY (`bd_meeting_meeting_id`) REFERENCES `bd_meeting` (`meeting_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_bd_user_and_meeting_bd_user1` FOREIGN KEY (`bd_user_user_id`) REFERENCES `bd_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_user_and_meeting`
--

LOCK TABLES `bd_user_and_meeting` WRITE;
/*!40000 ALTER TABLE `bd_user_and_meeting` DISABLE KEYS */;
INSERT INTO `bd_user_and_meeting` VALUES (202,2,'2017-06-10 15:59:38','2017-06-10 15:59:46',7,191),(203,2,'2017-06-10 16:06:14','0000-00-00 00:00:00',39,192),(204,2,'2017-06-10 16:38:29','2017-06-10 16:38:54',39,193),(205,2,'2017-06-10 17:00:02','2017-06-10 17:03:51',39,195),(206,2,'2017-06-10 17:04:29','2017-06-10 17:04:37',39,196),(207,2,'2017-06-10 17:09:06','0000-00-00 00:00:00',40,197),(208,2,'2017-06-10 22:29:58','2017-06-10 22:30:24',40,198),(209,2,'2017-06-10 22:34:42','2017-06-10 22:38:14',7,199),(210,1,'2017-06-10 22:35:01','2017-06-10 22:38:31',40,199),(211,2,'2017-06-10 22:41:44','2017-06-10 22:43:36',7,200),(212,1,'2017-06-10 22:42:00','2017-06-10 22:43:34',40,200),(213,2,'2017-06-10 22:47:11','2017-06-10 22:51:41',7,201),(214,1,'2017-06-10 22:51:27','2017-06-10 22:51:42',40,201),(215,2,'2017-06-10 23:27:04','0000-00-00 00:00:00',40,203),(216,2,'2017-06-10 23:30:05','0000-00-00 00:00:00',40,204),(217,1,'2017-06-10 23:30:54','0000-00-00 00:00:00',7,204),(218,2,'2017-06-10 23:33:20','2017-06-10 23:39:22',40,205),(219,1,'2017-06-10 23:39:06','2017-06-10 23:40:00',7,205),(220,2,'2017-06-10 23:50:34','2017-06-10 23:52:35',40,206),(221,1,'2017-06-10 23:52:08','2017-06-10 23:52:41',7,206),(222,2,'2017-06-10 23:56:22','2017-06-10 23:59:49',7,207),(223,1,'2017-06-10 23:56:38','2017-06-10 23:59:52',40,207),(224,2,'2017-06-11 00:05:04','2017-06-11 00:09:06',7,208),(225,1,'2017-06-11 00:08:54','2017-06-11 00:09:09',40,208);
/*!40000 ALTER TABLE `bd_user_and_meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bd_version`
--

DROP TABLE IF EXISTS `bd_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_version` (
  `version_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `app_name` varchar(32) NOT NULL COMMENT '主版本号',
  `server_version` varchar(32) NOT NULL COMMENT '子版本号',
  `last_force` tinyint(3) unsigned NOT NULL COMMENT '修订版本号',
  `server_flag` tinyint(3) unsigned NOT NULL COMMENT '日期版本号',
  `update_url` text NOT NULL COMMENT '希腊字母版本号',
  `upgrade_info` text NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`version_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_version`
--

LOCK TABLES `bd_version` WRITE;
/*!40000 ALTER TABLE `bd_version` DISABLE KEYS */;
INSERT INTO `bd_version` VALUES (1,'小喵共享白板','1.0.2',0,1,'/shareboardv1.0.2.apk','V1.0.2版本更新，你想不想要试一下哈！！！','2017-05-10 07:32:00'),(2,'小喵共享白板','1.0.3',0,1,'/shareboardv1.0.3.apk','V1.0.3版本更新，修复了修改头像时闪退问题，你想不想要试一下哈！！！','2017-06-03 07:32:00');
/*!40000 ALTER TABLE `bd_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'board'
--

--
-- Dumping routines for database 'board'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-07-11 11:19:21
