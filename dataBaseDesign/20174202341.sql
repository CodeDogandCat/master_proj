CREATE DATABASE  IF NOT EXISTS `board` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `board`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 118.89.102.238    Database: board
-- ------------------------------------------------------
-- Server version	5.5.54-0ubuntu0.14.04.1

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_friend`
--

LOCK TABLES `bd_friend` WRITE;
/*!40000 ALTER TABLE `bd_friend` DISABLE KEYS */;
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
  `meeting_url` bigint(20) unsigned NOT NULL COMMENT '12位数字',
  `meeting_theme` varchar(20) NOT NULL COMMENT '预定会议主题',
  `meeting_host_user_id` int(10) unsigned NOT NULL,
  `meeting_is_drawable` tinyint(4) NOT NULL COMMENT '与会者默认可画  1 不能 2 可以',
  `meeting_is_talkable` tinyint(4) NOT NULL COMMENT '与会者默认可说 1 不能 2 可以',
  `meeting_is_add_to_calendar` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否添加到日历 1：不添加 2：添加',
  `meeting_password` varchar(32) NOT NULL COMMENT '密码',
  `meeting_start_time` bigint(20) DEFAULT NULL COMMENT '会议开始时间',
  `meeting_end_time` bigint(20) DEFAULT NULL,
  `meeting_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '预约状态：1 ：未开始并且未到期 2：未开始并且过期了 3：正在进行 4：开会结束  5.锁定',
  `event_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '日历事件ID',
  `meeting_desc` text NOT NULL COMMENT '会议描述',
  PRIMARY KEY (`meeting_id`,`meeting_url`),
  KEY `meeting_host_user_id_idx` (`meeting_host_user_id`),
  CONSTRAINT `meeting_host_user_id` FOREIGN KEY (`meeting_host_user_id`) REFERENCES `bd_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=499 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_meeting`
--

LOCK TABLES `bd_meeting` WRITE;
/*!40000 ALTER TABLE `bd_meeting` DISABLE KEYS */;
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
  `reset_token` tinyint(4) NOT NULL COMMENT '验证码',
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
  `user_avatar` varchar(45) NOT NULL,
  PRIMARY KEY (`user_id`,`user_email`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  UNIQUE KEY `user_email_UNIQUE` (`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_user`
--

LOCK TABLES `bd_user` WRITE;
/*!40000 ALTER TABLE `bd_user` DISABLE KEYS */;
INSERT INTO `bd_user` VALUES (6,'2662083658@qq.com','李','磊','2017-04-20 23:03:46','2017-04-20 23:03:46',1,1,'5e44faa2062c939b34c66cb367483961','9f51b9f6fbe0b32db327ba5f761dc2d1','/2662083658@qq.com.2017_04_20_23_03_46.jpeg'),(7,'l2662083658l@gmail.com','li','lei','2017-04-20 23:28:38','2017-04-20 23:28:38',1,1,'ec64886af9e3a804d09a75be882a44f3','198aded7a3c663730ae7ead6152f6594','/l2662083658l@gmail.com.2017_04_20_23_28_38.j');
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
) ENGINE=InnoDB AUTO_INCREMENT=572 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_user_and_meeting`
--

LOCK TABLES `bd_user_and_meeting` WRITE;
/*!40000 ALTER TABLE `bd_user_and_meeting` DISABLE KEYS */;
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
  `version_part_one` smallint(5) unsigned NOT NULL COMMENT '主版本号',
  `version_part_two` smallint(5) unsigned NOT NULL COMMENT '子版本号',
  `version_part_three` smallint(5) unsigned NOT NULL COMMENT '修订版本号',
  `version_part_four` int(10) unsigned NOT NULL COMMENT '日期版本号',
  `version_part_five` char(10) NOT NULL COMMENT '希腊字母版本号',
  PRIMARY KEY (`version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_version`
--

LOCK TABLES `bd_version` WRITE;
/*!40000 ALTER TABLE `bd_version` DISABLE KEYS */;
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

-- Dump completed on 2017-04-20 23:41:40
