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
-- Table structure for table `bd_meeting`
--

DROP TABLE IF EXISTS `bd_meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_meeting` (
  `meeting_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `meeting_url` int(10) unsigned NOT NULL COMMENT '9位数字 xxx-xxx-xxx',
  `meeting_theme` varchar(20) NOT NULL COMMENT '预定会议主题',
  `meeting_host_user_id` int(10) unsigned NOT NULL,
  `meeting_is_drawable` tinyint(4) NOT NULL COMMENT '与会者默认可画  1 不能 2 可以',
  `meeting_is_talkable` tinyint(4) NOT NULL COMMENT '与会者默认可说 1 不能 2 可以',
  `meeting_is_use_pmi` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否使用个人会议ID 1：不使用 2 ：使用',
  `meeting_is_add_to_calendar` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否添加到日历 1：不添加 2：添加',
  `meeting_password` char(8) DEFAULT NULL COMMENT '密码可空',
  `meeting_start_time` datetime NOT NULL COMMENT '会议开始时间',
  `meeting_end_time` datetime NOT NULL,
  `meeting_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '预约状态：1 ：未开始并且未到期 2：未开始并且过期了 3：正在进行 4：开会结束 ',
  PRIMARY KEY (`meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_meeting`
--

LOCK TABLES `bd_meeting` WRITE;
/*!40000 ALTER TABLE `bd_meeting` DISABLE KEYS */;
/*!40000 ALTER TABLE `bd_meeting` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-02 16:02:56
