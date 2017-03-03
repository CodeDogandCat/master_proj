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
  `user_pmi` int(10) unsigned NOT NULL COMMENT '个人会议专用ID',
  `user_token` varchar(32) NOT NULL COMMENT '用户唯一 TOKEN',
  PRIMARY KEY (`user_id`,`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_user`
--

LOCK TABLES `bd_user` WRITE;
/*!40000 ALTER TABLE `bd_user` DISABLE KEYS */;
INSERT INTO `bd_user` VALUES (1,'1662083658@qq.com','李','磊','2017-02-23 17:52:59','0000-00-00 00:00:00',1,1,'123412341234',100000000,'vvvvvvvvfsf1233fs'),(2,'3662083658@qq.com','陈','晓','2017-02-23 18:00:00','0000-00-00 00:00:00',1,1,'123412341234',100000001,'rewregvv'),(8,'2662083658@qq.com','李','磊','2017-03-02 14:37:45','2017-03-02 14:37:45',1,1,'5e44faa2062c939b34c66cb367483961',0,'22d1bd50b43bac1ab1086dc4fef36099');
/*!40000 ALTER TABLE `bd_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-02 16:02:57
