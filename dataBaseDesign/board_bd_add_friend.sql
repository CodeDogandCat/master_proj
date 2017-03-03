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
-- Table structure for table `bd_add_friend`
--

DROP TABLE IF EXISTS `bd_add_friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bd_add_friend` (
  `add_friend_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `response_status` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '回复状态：1：未回复 2：拒绝 3：同意',
  `message_time` datetime NOT NULL COMMENT '消息时间',
  `bd_user_user_id` int(10) unsigned NOT NULL,
  `bd_user_user_id1` int(10) unsigned NOT NULL,
  PRIMARY KEY (`add_friend_id`),
  KEY `fk_bd_add_friend_bd_user1_idx` (`bd_user_user_id`),
  KEY `fk_bd_add_friend_bd_user2_idx` (`bd_user_user_id1`),
  CONSTRAINT `fk_bd_add_friend_bd_user1` FOREIGN KEY (`bd_user_user_id`) REFERENCES `bd_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_bd_add_friend_bd_user2` FOREIGN KEY (`bd_user_user_id1`) REFERENCES `bd_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bd_add_friend`
--

LOCK TABLES `bd_add_friend` WRITE;
/*!40000 ALTER TABLE `bd_add_friend` DISABLE KEYS */;
/*!40000 ALTER TABLE `bd_add_friend` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-03-02 16:02:53
