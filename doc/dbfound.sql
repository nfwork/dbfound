/*
SQLyog Enterprise - MySQL GUI v8.12 
MySQL - 5.5.19 : Database - dbfound
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`dbfound` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `dbfound`;

/*Table structure for table `fnd_branch` */

DROP TABLE IF EXISTS `fnd_branch`;

CREATE TABLE `fnd_branch` (
  `branch_id` int(11) NOT NULL AUTO_INCREMENT,
  `branch_code` varchar(100) NOT NULL,
  `branch_name` varchar(200) NOT NULL,
  `enable_flag` varchar(1) DEFAULT NULL,
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`branch_id`),
  UNIQUE KEY `branch_code` (`branch_code`),
  KEY `branch_name` (`branch_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `fnd_branch` */

insert  into `fnd_branch`(`branch_id`,`branch_code`,`branch_name`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values (1,'JSJ001','Javaè¯¾ç¨‹è®¾è®¡','Y',2,'2012-07-29',2,'2013-04-21'),(2,'JSJ002','Cè¯­è¨€ç¨‹åºè®¾è®¡','Y',2,'2012-07-29',2,'2012-11-24'),(3,'JSJ003','æ•°æ®åº“åŽŸç†','Y',2,'2012-07-29',2,'2012-07-29'),(4,'JSJ004','è®¡ç®—æœºç½‘ç»œ','Y',2,'2012-08-15',2,'2013-05-30'),(5,'JSJ005','è®¡ç®—æœºå¯¼è®º','Y',2,'2012-08-22',2,'2012-08-22'),(6,'JSJ006','è®¡ç®—æœºç»„æˆåŽŸç†','Y',2,'2012-09-27',2,'2012-09-27');

/*Table structure for table `fnd_class` */

DROP TABLE IF EXISTS `fnd_class`;

CREATE TABLE `fnd_class` (
  `class_id` int(11) NOT NULL AUTO_INCREMENT,
  `class_code` varchar(100) NOT NULL,
  `class_name` varchar(200) NOT NULL,
  `enable_flag` varchar(1) NOT NULL,
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`class_id`),
  UNIQUE KEY `class_code` (`class_code`),
  KEY `class_name` (`class_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `fnd_class` */

insert  into `fnd_class`(`class_id`,`class_code`,`class_name`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values (1,'074202','07çº§ç½‘ç»œå·¥ç¨‹äºŒç­','Y',2,'2012-05-08',21,'2012-09-24'),(2,'074201','07çº§ç½‘ç»œå·¥ç¨‹ä¸€ç­','Y',2,'2012-05-22',21,'2012-09-25'),(3,'074001','07çº§è®¡ç®—æœºç§‘å­¦ä¸ŽæŠ€æœ¯ä¸€ç­','Y',2,'2012-08-21',2,'2012-08-21'),(4,'074401','07çº§è½¯ä»¶å·¥ç¨‹ä¸€ç­','Y',21,'2012-09-22',21,'2012-09-22'),(5,'074002','07çº§è®¡ç®—æœºç§‘å­¦ä¸ŽæŠ€æœ¯äºŒç­','Y',21,'2012-09-22',21,'2012-09-22'),(6,'074402','07çº§è½¯ä»¶å·¥ç¨‹äºŒç­','Y',21,'2012-09-22',2,'2013-05-30');

/*Table structure for table `fnd_course` */

DROP TABLE IF EXISTS `fnd_course`;

CREATE TABLE `fnd_course` (
  `course_id` int(11) NOT NULL AUTO_INCREMENT,
  `enable_flag` varchar(1) DEFAULT NULL,
  `class_id` int(11) NOT NULL,
  `branch_id` int(11) NOT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`course_id`),
  KEY `NewIndex1` (`class_id`),
  KEY `NewIndex2` (`branch_id`),
  KEY `NewIndex3` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

/*Data for the table `fnd_course` */

insert  into `fnd_course`(`course_id`,`enable_flag`,`class_id`,`branch_id`,`teacher_id`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values (4,'Y',2,1,1,2,'2012-07-29',2,'2012-07-29'),(5,'Y',1,1,1,2,'2012-07-29',2,'2012-07-29'),(6,'Y',2,2,2,2,'2012-07-29',2,'2012-07-29'),(7,'Y',1,3,3,2,'2012-07-29',2,'2012-07-29'),(8,'Y',2,3,3,2,'2012-07-29',2,'2012-07-29'),(9,'Y',1,2,1,2,'2012-07-29',2,'2012-07-29'),(10,'Y',3,1,1,2,'2012-08-21',2,'2012-08-21'),(11,'Y',3,3,3,2,'2012-08-21',2,'2012-08-21'),(12,'Y',1,5,4,2,'2012-08-22',2,'2012-08-22'),(13,'Y',3,4,4,2,'2012-08-23',2,'2012-08-23'),(14,'Y',2,4,4,2,'2012-08-27',2,'2012-08-27'),(15,'Y',2,5,1,2,'2012-08-27',2,'2012-08-27'),(16,'Y',1,4,4,2,'2012-08-27',2,'2012-08-27'),(17,'Y',3,2,1,2,'2012-08-27',2,'2012-08-27'),(18,'Y',3,5,3,2,'2012-08-27',21,'2012-08-28'),(19,'Y',4,1,1,21,'2012-09-22',21,'2012-09-22'),(20,'Y',6,6,3,2,'2012-09-27',2,'2012-09-27'),(21,'Y',4,4,4,2,'2012-09-27',2,'2012-09-27'),(22,'Y',5,3,3,21,'2012-10-11',21,'2012-10-11');

/*Table structure for table `fnd_student` */

DROP TABLE IF EXISTS `fnd_student`;

CREATE TABLE `fnd_student` (
  `student_id` int(11) NOT NULL AUTO_INCREMENT,
  `class_id` int(11) NOT NULL,
  `student_code` varchar(100) NOT NULL,
  `student_name` varchar(200) NOT NULL,
  `telphone_num` varchar(20) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  `enable_flag` char(1) DEFAULT NULL,
  `create_by` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `last_update_by` int(11) DEFAULT NULL,
  `last_update_date` date DEFAULT NULL,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `class_id` (`student_code`),
  KEY `student_name` (`student_name`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

/*Data for the table `fnd_student` */

insert  into `fnd_student`(`student_id`,`class_id`,`student_code`,`student_name`,`telphone_num`,`email`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values (1,1,'07420207','å°æ¨','12345678','213@163.com','Y',2,'2012-05-08',2,'2012-09-02'),(2,1,'07420206','å°å¾','123456789','123@123.com','Y',2,'2012-05-08',2,'2012-09-02'),(3,1,'07420205','å°æ¯›','12345678','213@163.com','Y',2,'2012-05-08',2,'2012-09-02'),(4,1,'ADMIN','æµ‹è¯•å­¦ç”Ÿ1','18621598333','213@163.com','Y',2,'2012-05-17',2,'2013-06-04'),(5,2,'07420101','å°æ˜Ž','18621598333','12321@123.com','Y',2,'2012-05-22',2,'2012-09-02'),(6,2,'07420102','å°çŽ‹','123213213','213@163.com','Y',2,'2012-07-07',2,'2012-09-25'),(7,2,'07420103','å°å†›','18621598333','12321@123.com','Y',2,'2012-07-07',2,'2012-09-02'),(8,1,'07420221','å°é»„','18621598333','213@163.com','Y',2,'2012-07-28',2,'2012-09-02'),(9,2,'07420104','å°æµ·','18621598333','12321@123.com','Y',2,'2012-07-28',2,'2012-09-02'),(10,2,'07420105','å°æœ±','18621598333','12321@123.com','Y',2,'2012-07-28',2,'2012-09-02'),(11,2,'07420106','å°æ¨','18621598333','12321@123.com','Y',2,'2012-07-28',2,'2012-09-24'),(12,1,'07420201','å°éƒ‘','18621598333','213@163.com','Y',2,'2012-07-29',2,'2013-06-04'),(13,1,'07420203','å¼ ä¸‰','18621598333','213@163.com','Y',2,'2012-07-29',2,'2012-09-02'),(14,1,'07420202','å°æ›¾','18621598333','213@163.com','Y',2,'2012-07-29',2,'2012-09-02'),(15,1,'07420204','å°ç‰›','18621598333','213@163.com','Y',2,'2012-07-29',2,'2012-09-02'),(16,3,'07400101','å°çŽ‹','123456789','hello@163.com','Y',2,'2012-08-23',2,'2013-06-08'),(17,3,'07400102','å°æ˜Ž','123456789','123@sina.com','Y',21,'2012-08-30',2,'2012-09-02'),(18,3,'07400103','å°èƒ¡','123456789','12323@sina.com','Y',2,'2012-08-30',2,'2012-09-02'),(19,3,'07400104','å°é©¬','123456789','123@sina.com','Y',2,'2012-08-30',2,'2012-09-02'),(20,4,'07440101','å°æ®µ','123213213','123@123.com','Y',21,'2012-09-22',21,'2012-09-22'),(21,6,'07440201','å°æ¯›','123213213','huang@163.com','Y',2,'2012-09-27',2,'2012-09-27');

/*Table structure for table `fnd_teacher` */

DROP TABLE IF EXISTS `fnd_teacher`;

CREATE TABLE `fnd_teacher` (
  `teacher_id` int(11) NOT NULL AUTO_INCREMENT,
  `teacher_code` varchar(100) NOT NULL,
  `teacher_name` varchar(200) NOT NULL,
  `telphone_num` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `enable_flag` char(1) DEFAULT NULL,
  `create_by` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `last_update_by` int(11) DEFAULT NULL,
  `last_update_date` date DEFAULT NULL,
  PRIMARY KEY (`teacher_id`),
  UNIQUE KEY `teacher_code` (`teacher_code`),
  KEY `teacher_name` (`teacher_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `fnd_teacher` */

insert  into `fnd_teacher`(`teacher_id`,`teacher_code`,`teacher_name`,`telphone_num`,`email`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values (1,'10000','é»„ç‚¯','13888889999','nfwork@sina.com','Y',2,'2012-05-08',2,'2013-06-04'),(2,'ADMIN','æµ‹è¯•è€å¸ˆ','12334345454','yangyong@sina.com','Y',2,'2012-05-17',21,'2012-09-22'),(3,'10010','æ¨æ°¸','13888889999','yangyong@sina.com','Y',2,'2012-07-07',2,'2013-08-14'),(4,'10020','å°¹å°å†›','12334345454','yangyong@sina.com','Y',2,'2012-07-27',21,'2012-10-16');

/*Table structure for table `gc_employee` */

DROP TABLE IF EXISTS `gc_employee`;

CREATE TABLE `gc_employee` (
  `employee_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_code` varchar(100) NOT NULL,
  `employee_name` varchar(200) NOT NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `employee_code` (`employee_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `gc_employee` */

insert  into `gc_employee`(`employee_id`,`employee_code`,`employee_name`) values (1,'HJ','é»„ç‚¯'),(2,'YXJ','å°¹å°å†›'),(3,'YFQ','æ¨å‡¤å¨‡'),(4,'YSM','å°¹åŒæžš'),(5,'DSF','dsf');

/*Table structure for table `gc_project` */

DROP TABLE IF EXISTS `gc_project`;

CREATE TABLE `gc_project` (
  `project_id` int(11) NOT NULL AUTO_INCREMENT,
  `project_code` varchar(100) NOT NULL,
  `project_name` varchar(200) NOT NULL,
  `price` int(11) NOT NULL,
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `branch_code` (`project_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `gc_project` */

insert  into `gc_project`(`project_id`,`project_code`,`project_name`,`price`) values (1,'YJHY','æ€¡æ™¯èŠ±å›­å·¥ç¨‹',100),(2,'NNN','www',455);

/*Table structure for table `gc_work_record` */

DROP TABLE IF EXISTS `gc_work_record`;

CREATE TABLE `gc_work_record` (
  `work_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL,
  `work_date` date NOT NULL,
  `project_id` int(11) NOT NULL,
  `settlement_flag` char(1) DEFAULT NULL,
  `settlement_date` date DEFAULT NULL,
  PRIMARY KEY (`work_id`)
) ENGINE=InnoDB AUTO_INCREMENT=361 DEFAULT CHARSET=utf8;

/*Data for the table `gc_work_record` */

insert  into `gc_work_record`(`work_id`,`employee_id`,`work_date`,`project_id`,`settlement_flag`,`settlement_date`) values (5,1,'2013-06-01',1,'Y','2013-06-26'),(6,1,'2013-06-02',1,'Y','2013-06-26'),(8,1,'2013-06-04',1,'Y','2013-06-26'),(11,1,'2013-06-07',1,'Y','2013-06-26'),(12,1,'2013-06-08',1,'Y','2013-06-26'),(13,1,'2013-06-09',1,'Y','2013-06-26'),(14,1,'2013-06-10',1,'Y','2013-06-26'),(15,2,'2013-06-03',1,'Y','2013-06-26'),(16,2,'2013-06-04',1,'Y','2013-06-26'),(17,2,'2013-06-05',1,'Y','2013-06-26'),(18,2,'2013-06-06',1,'Y','2013-06-26'),(19,2,'2013-06-07',1,'Y','2013-06-26'),(20,2,'2013-06-10',1,'Y','2013-06-26'),(21,2,'2013-06-11',1,'Y','2013-06-26'),(22,1,'2013-06-26',1,'Y','2013-06-26'),(25,1,'2013-06-14',1,'Y','2013-06-26'),(27,1,'2013-06-05',1,'Y','2013-06-26'),(28,1,'2013-05-04',1,'Y','2013-06-26'),(29,1,'2013-06-12',1,'Y','2013-06-26'),(39,3,'2013-06-08',1,'Y','2013-06-26'),(40,3,'2013-06-09',1,'Y','2013-06-26'),(41,3,'2013-06-10',1,'Y','2013-06-26'),(42,3,'2013-06-11',1,'Y','2013-06-26'),(43,3,'2013-06-12',1,'Y','2013-06-26'),(44,3,'2013-06-13',1,'Y','2013-06-26'),(45,3,'2013-06-14',1,'Y','2013-06-26'),(46,3,'2013-06-15',1,'Y','2013-06-26'),(47,3,'2013-06-16',1,'Y','2013-06-26'),(48,3,'2013-06-17',1,'Y','2013-06-26'),(49,3,'2013-06-18',1,'Y','2013-06-26'),(50,3,'2013-06-19',1,'Y','2013-06-26'),(51,4,'2013-06-13',1,'Y','2013-06-26'),(52,4,'2013-06-14',1,'Y','2013-06-26'),(53,4,'2013-06-15',1,'Y','2013-06-26'),(54,4,'2013-06-16',1,'Y','2013-06-26'),(55,4,'2013-06-17',1,'Y','2013-06-26'),(56,4,'2013-06-18',1,'Y','2013-06-26'),(57,4,'2013-06-19',1,'Y','2013-06-26'),(58,4,'2013-06-20',1,'Y','2013-06-26'),(62,2,'2013-06-09',1,NULL,NULL),(64,2,'2013-06-08',1,NULL,NULL),(163,4,'2013-07-01',1,NULL,NULL),(164,4,'2013-07-02',1,NULL,NULL),(165,4,'2013-07-03',1,NULL,NULL),(166,4,'2013-07-04',1,NULL,NULL),(167,4,'2013-07-05',1,NULL,NULL),(168,4,'2013-07-06',1,NULL,NULL),(172,1,'2013-04-23',1,NULL,NULL),(173,1,'2013-04-24',1,NULL,NULL),(174,1,'2013-04-25',1,NULL,NULL),(175,1,'2013-04-30',1,NULL,NULL),(176,1,'2013-04-11',1,NULL,NULL),(178,1,'2013-04-01',1,NULL,NULL),(180,1,'2013-04-03',1,NULL,NULL),(181,1,'2013-04-04',1,NULL,NULL),(182,1,'2013-04-08',1,NULL,NULL),(184,4,'2013-12-10',1,NULL,NULL),(185,4,'2013-12-11',1,NULL,NULL),(186,4,'2013-12-12',1,NULL,NULL),(187,4,'2013-12-13',1,NULL,NULL),(188,4,'2013-12-16',1,NULL,NULL),(189,4,'2013-12-17',1,NULL,NULL),(190,4,'2013-12-18',1,NULL,NULL),(191,4,'2013-12-19',1,NULL,NULL),(192,4,'2013-12-20',1,NULL,NULL),(193,4,'2013-12-23',1,NULL,NULL),(194,4,'2013-12-24',1,NULL,NULL),(195,4,'2013-12-25',1,NULL,NULL),(196,4,'2013-12-26',1,NULL,NULL),(197,4,'2013-12-27',1,NULL,NULL),(198,4,'2013-12-06',1,NULL,NULL),(199,4,'2013-12-05',1,NULL,NULL),(200,4,'2013-12-04',1,NULL,NULL),(201,4,'2013-10-09',1,NULL,NULL),(203,4,'2013-12-30',1,NULL,NULL),(204,4,'2013-06-30',1,NULL,NULL),(219,3,'2013-04-09',1,NULL,NULL),(220,3,'2013-04-10',1,NULL,NULL),(221,3,'2013-04-11',1,NULL,NULL),(222,3,'2013-04-12',1,NULL,NULL),(223,3,'2013-04-13',1,NULL,NULL),(224,3,'2013-04-06',1,NULL,NULL),(225,3,'2013-04-05',1,NULL,NULL),(226,3,'2013-04-04',1,NULL,NULL),(227,3,'2013-04-03',1,NULL,NULL),(228,3,'2013-04-02',1,NULL,NULL),(229,3,'2013-04-01',1,NULL,NULL),(230,3,'2013-04-07',1,NULL,NULL),(231,3,'2013-04-08',1,NULL,NULL),(232,3,'2013-04-14',1,NULL,NULL),(233,3,'2013-04-15',1,NULL,NULL),(234,3,'2013-07-17',1,NULL,NULL),(235,3,'2013-07-18',1,NULL,NULL),(236,3,'2013-07-23',1,NULL,NULL),(237,3,'2013-07-22',1,NULL,NULL),(238,3,'2013-08-14',1,NULL,NULL),(239,3,'2013-08-15',1,NULL,NULL),(240,3,'2013-08-30',1,NULL,NULL),(241,4,'2013-08-14',1,NULL,NULL),(242,4,'2013-08-23',1,NULL,NULL),(253,1,'2013-06-06',1,NULL,NULL),(254,1,'2013-06-11',1,NULL,NULL),(255,1,'2013-06-03',1,NULL,NULL),(258,1,'2013-06-13',1,NULL,NULL),(259,1,'2013-07-27',1,NULL,NULL),(260,2,'2013-07-27',1,NULL,NULL),(261,3,'2013-07-27',1,NULL,NULL),(262,4,'2013-07-27',1,NULL,NULL),(263,2,'2013-07-28',1,NULL,NULL),(264,3,'2013-07-28',1,NULL,NULL),(265,4,'2013-07-28',1,NULL,NULL),(266,1,'2013-07-28',1,NULL,NULL),(267,3,'2013-08-16',1,NULL,NULL),(268,3,'2013-08-17',1,NULL,NULL),(277,1,'2013-09-06',1,NULL,NULL),(297,1,'2014-05-29',1,NULL,NULL),(308,1,'2014-06-08',1,NULL,NULL),(310,1,'2014-06-16',1,NULL,NULL),(312,1,'2014-06-18',1,NULL,NULL),(313,1,'2014-06-22',1,NULL,NULL),(316,1,'2014-06-02',1,NULL,NULL),(318,1,'2014-06-10',1,NULL,NULL),(319,1,'2014-06-24',1,NULL,NULL),(320,1,'2014-06-30',1,NULL,NULL),(321,1,'2014-06-26',1,NULL,NULL),(323,1,'2014-06-12',1,NULL,NULL),(324,1,'2014-06-06',1,NULL,NULL),(325,1,'2014-06-04',1,NULL,NULL),(326,1,'2014-06-20',1,NULL,NULL),(327,1,'2014-06-14',1,NULL,NULL),(328,1,'2014-06-28',1,NULL,NULL),(339,3,'2014-06-11',1,NULL,NULL),(340,3,'2014-06-19',1,NULL,NULL),(341,3,'2014-06-18',1,NULL,NULL),(342,3,'2014-06-25',1,NULL,NULL),(345,3,'2014-06-26',1,NULL,NULL),(346,3,'2014-06-20',1,NULL,NULL),(347,3,'2012-03-14',1,NULL,NULL),(348,3,'2012-03-15',1,NULL,NULL),(349,3,'2012-03-16',1,NULL,NULL),(350,3,'2012-03-24',1,NULL,NULL),(351,3,'2012-03-28',1,NULL,NULL),(352,3,'2012-03-20',1,NULL,NULL),(354,3,'2012-03-30',1,NULL,NULL),(355,3,'2012-03-23',1,NULL,NULL),(357,5,'2014-08-06',2,NULL,NULL),(358,5,'2014-08-07',2,NULL,NULL),(359,5,'2014-08-08',2,NULL,NULL),(360,5,'2014-08-13',2,NULL,NULL);

/*Table structure for table `job_headers` */

DROP TABLE IF EXISTS `job_headers`;

CREATE TABLE `job_headers` (
  `header_id` char(24) NOT NULL,
  `class_id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `teacher_id` int(11) NOT NULL,
  `end_time` date NOT NULL,
  `status` varchar(10) NOT NULL COMMENT 'çŠ¶æ€',
  `title` varchar(200) NOT NULL COMMENT 'æ ‡é¢˜',
  `description` varchar(2000) DEFAULT NULL COMMENT 'æè¿°',
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`header_id`),
  KEY `tile` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `job_headers` */

insert  into `job_headers`(`header_id`,`class_id`,`course_id`,`teacher_id`,`end_time`,`status`,`title`,`description`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values ('23CNJT5SGVQSDXA6AQWTTR98',2,6,2,'2014-06-27','END','111','1111',2,'2014-06-26',2,'2014-06-26'),('6L1AUD7HO9P7XK9SPDCB2NDM',2,6,2,'2012-09-21','END','cè¯­è¨€ä½œä¸šä¸‰','cè¯­è¨€ä½œä¸šä¸‰ï¼Œç»ƒä¹ é¢˜1ã€2ã€3ã€4ã€5ã€6',2,'2012-09-01',2,'2012-09-01'),('6W277M94DI9GNAL5132C4VNI',2,6,2,'2012-12-18','END','testq','testq',2,'2012-12-17',2,'2012-12-17'),('746SBK6A6IYTHCGDZL5Y7802',1,5,1,'2012-08-24','DOWN','Javaè¯¾ç¨‹è®¾è®¡ ç»ƒä¹ é¢˜ä¸€','Javaè¯¾ç¨‹è®¾è®¡ ç»ƒä¹ é¢˜ä¸€ å®Œæˆ1ã€2ã€3ã€7ã€8é¢˜ï¼Œè¯·åœ¨å‘¨äº”ä¹‹å‰å®Œæˆã€‚',1,'2012-08-20',1,'2012-08-20'),('8VQ7YJTF2KF0NR35F9FB6ATX',2,6,2,'2014-10-22','NEW','32323','eweeeeee',2,'2014-10-08',2,'2014-10-08'),('AYV97UZL07SED3FBOIPAQ3WB',2,6,2,'2013-05-24','DOWN','sadsadsa','asdasdas12æè¿°:ä½œä¸šé¢˜ç›®:\n',2,'2013-05-30',2,'2013-06-24'),('DYAB8KAAPC2V5K6QAT1UPFEL',2,6,2,'2014-07-16','CHECK','111',NULL,2,'2014-07-15',2,'2014-08-04'),('EGI1BF81XF2Q34AFV9XX1PVK',2,6,2,'2013-05-30','END','test','test123 123 123 123',2,'2013-05-31',2,'2013-06-06'),('EK2Q9IYKR0EJ3UT87Q3LQD54',2,4,1,'2012-11-30','NEW','Javaè¯¾ç¨‹è®¾è®¡','Javaè¯¾ç¨‹è®¾è®¡',1,'2012-11-24',1,'2013-04-29'),('HDNJWDRZNJI1314AZ2Y0IOEH',2,6,2,'2013-04-18','END','test0002','234324234',2,'2013-04-29',2,'2013-04-29'),('HLXEZZWQL5IQV1J3S9MA19CB',2,6,2,'2012-08-17','END','Javaè¯¾åŽç³»ç»Ÿä¸€','Javaè¯¾åŽç³»ç»Ÿä¸€ 1ã€3ã€4ã€6ã€7ã€8é¢˜ï¼Œè¯·æŒ‰æ—¶å®Œæˆã€‚',2,'2012-08-08',2,'2012-08-08'),('HTBVO5F7VX71AK51NKUDB6VX',1,9,1,'2012-08-17','END','cè¯­è¨€ è¯¾åŽç³»ç»Ÿä¸€','cè¯­è¨€ è¯¾åŽç³»ç»Ÿä¸€',1,'2012-08-08',1,'2012-08-08'),('J42ZC7XR85ZU8ODX325RRTCE',2,6,2,'2012-08-09','END','Cè¯­è¨€ç¨‹åºè®¾è®¡ç»ƒä¹ é¢˜äºŒ','Cè¯­è¨€ç¨‹åºè®¾è®¡ç»ƒä¹ é¢˜äºŒ 1ã€2ã€3ã€5ã€6é¢˜ ï¼Œè¯·æŒ‰æ—¶å®Œæˆã€‚\r\nä¸œæ–¹ç½‘9æœˆ29æ—¥æ¶ˆæ¯ï¼šè®°è€…ä»Žå¤–äº¤éƒ¨èŽ·æ‚‰ï¼Œæ˜¨å¤©ï¼Œä»¥â€œç»´æŠ¤æ”¿æ²»åŸºç¡€ï¼ŒæŠŠæ¡å‘å±•æ–¹å‘â€ä¸ºä¸»é¢˜çš„ä¸­æ—¥é‚¦äº¤æ­£å¸¸åŒ–40å‘¨å¹´åº§è°ˆä¼šåœ¨åŒ—äº¬ä¸¾è¡Œã€‚å¤–äº¤éƒ¨éƒ¨é•¿åŠ©ç†ä¹çŽ‰æˆåœ¨è®²è¯ä¸­æŒ‡å‡ºï¼Œæ—¥æœ¬ä¸è¦å†å¹»æƒ³éœ¸å é’“é±¼å²›ï¼Œæ´¾äººåˆ°ä¸­å›½æ¥è§£é‡Šå‡ å¥å°±ä¸‡äº‹å¤§å‰ï¼Œå¦‚æžœç»§ç»­ä¸€æ„å­¤è¡Œï¼Œä¸­æ—¥å…³ç³»è¿™æ¡å¤§èˆ¹å°±å¯èƒ½åƒâ€œæ³°å¦å°¼å…‹â€å·ä¸€æ ·è§¦ç¤æ²‰æ²¡ã€‚',2,'2012-08-08',2,'2012-09-29'),('KJ2RECRHMCGQI1S7KC90FO5J',2,6,2,'2012-08-18','END','\'æµ‹è¯•å¼•å·\"','æµ‹è¯•å¼•å·æ˜¯å¦æ˜¾ç¤ºæ­£å¸¸\'\'\'\'\"\"\"/\"/\"dddd\'\'\'\'\'\',,,,777777n/////',2,'2012-08-15',2,'2012-09-27'),('LR6CLROGCRTS5I89XR8LQYJA',2,6,2,'2013-06-19','END','12','12321321321312sdfdsfdsf',2,'2013-06-05',2,'2013-06-06'),('NH7NGCC7441061F336TWI4FH',2,6,2,'2013-10-31','END','æµ‹è¯•',NULL,2,'2013-10-18',2,'2013-10-18'),('NNRXIJ577ZZHRYDG9V6M1EHP',1,9,1,'2012-08-31','END','Javaè¯¾åŽç³»ç»ŸäºŒ','Javaè¯¾åŽç³»ç»ŸäºŒ 1ã€3ã€4ã€5ã€8ã€9é¢˜',1,'2012-08-24',1,'2012-08-24'),('NRVQYY8V8PJZGTCMH4M176CK',1,9,1,'2012-08-24','DOWN','Cè¯­è¨€ä½œä¸šè”ç³»é¢˜äºŒ','Cè¯­è¨€ä½œä¸šè”ç³»é¢˜äºŒï¼Œå®Œæˆ1ã€2ã€3ã€6ã€7é¢˜',1,'2012-08-17',1,'2012-08-17'),('OW3JXYY9ZGI5I9C7VSS2HT6S',1,7,3,'2012-08-17','NEW','æ•°æ®åº“åŽŸç†è¯¾åŽç³»ç»Ÿä¸€','æ•°æ®åº“åŽŸç†è¯¾åŽç³»ç»Ÿä¸€ 1ã€2ã€4ã€5ã€15ã€18é¢˜ï¼Œè¯·æŒ‰æ—¶å®Œæˆã€‚',9,'2012-08-08',9,'2012-08-08'),('PKQBL6Y4L0SXXVD116L71DJP',4,19,1,'2013-06-11','NEW','test001','hello kity',1,'2013-06-03',1,'2013-06-03'),('SD6OGB1WJFQHQP2MVR35GO7U',2,6,2,'2013-04-17','END','123','æµ‹è¯•ä½œä¸š',2,'2013-04-09',2,'2013-04-10'),('UO80AC6RPU64DF6C7KUIVQOA',2,8,3,'2012-08-17','DOWN','æ•°æ®åº“åŽŸç†è¯¾åŽç»ƒä¹ ä¸€','æ•°æ®åº“åŽŸç†è¯¾åŽç»ƒä¹ ä¸€ 1ã€2ã€3ã€5ã€6ï¼Œè¯·æŒ‰æ—¶å®Œæˆã€‚',9,'2012-08-08',9,'2012-08-08'),('VBBJ88XTT92UUG4X0M0A5X2N',3,13,4,'2012-08-31','NEW','è®¡ç®—æœºç½‘ç»œè¯¾åŽç»ƒä¹ é¢˜ä¸€','è®¡ç®—æœºç½‘ç»œè¯¾åŽç»ƒä¹ é¢˜ä¸€ 1ã€2ã€3ã€4ã€5é¢˜ã€‚',11,'2012-08-23',11,'2012-08-23'),('WAZNJYUD0YYYY9IOR918YFQF',1,9,1,'2012-10-05','DOWN','hello kity','hello kity',1,'2012-09-21',1,'2012-09-21'),('X676YPIAZ4RKVQTNON2TDAO6',2,6,2,'2013-04-26','DOWN','æ–°å¢žä½œä¸šæµ‹è¯•','æ–°å¢žä½œä¸šæµ‹è¯• hello kity',2,'2013-04-20',2,'2013-04-20'),('XNGIA1T22VB0OK1HTGQBVW7W',2,6,2,'2013-10-31','END','hello kity',NULL,2,'2013-10-22',2,'2013-10-22'),('XSF060BUDZNTYVAU1VALQ89S',2,6,2,'2014-07-15','END','111','11',2,'2014-07-08',2,'2014-07-08'),('ZSQS6SUD9HDZYVKYNLF5HCTP',1,12,4,'2012-08-23','DOWN','JSJ005-è®¡ç®—æœºå¯¼è®º ä¹ é¢˜ä¸€','JSJ005-è®¡ç®—æœºå¯¼è®º ä¹ é¢˜ä¸€ 1ã€2ã€3ã€4',11,'2012-08-22',11,'2012-08-22');

/*Table structure for table `job_lines` */

DROP TABLE IF EXISTS `job_lines`;

CREATE TABLE `job_lines` (
  `line_id` char(24) NOT NULL,
  `header_id` char(24) NOT NULL COMMENT 'å¤´id',
  `student_id` int(11) DEFAULT NULL COMMENT 'å­¦ç”Ÿid',
  `description` varchar(2000) DEFAULT NULL COMMENT 'æè¿°',
  `score` float DEFAULT NULL COMMENT 'åˆ†æ•°',
  `teacher_comment` varchar(2000) DEFAULT NULL COMMENT 'è€å¸ˆè¯„è¯­',
  `grade` varchar(200) DEFAULT NULL COMMENT 'ç­‰çº§',
  `status` varchar(100) DEFAULT NULL,
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`line_id`),
  UNIQUE KEY `student_id` (`student_id`,`header_id`),
  KEY `header_id` (`header_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `job_lines` */

insert  into `job_lines`(`line_id`,`header_id`,`student_id`,`description`,`score`,`teacher_comment`,`grade`,`status`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values ('0POBIYQQO31WGHEEL12C1HGP','746SBK6A6IYTHCGDZL5Y7802',4,'123',NULL,NULL,NULL,'SUBMIT',2,'2012-08-21',2,'2012-08-21'),('11DQAQYW9F7ETBXY9ZDR3NF6','6W277M94DI9GNAL5132C4VNI',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),('3A9R9CSVJHHA1PG4ACRU1YZR','ZSQS6SUD9HDZYVKYNLF5HCTP',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),('3I6AP58IDFALNQA6SSRJBIVC','NNRXIJ577ZZHRYDG9V6M1EHP',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),('4LT0BNTBBCXCF6F92JIGR21N','UO80AC6RPU64DF6C7KUIVQOA',5,'å·²ç»å®Œæˆï¼Œè¯·éªŒæ”¶ã€‚æ—¶é—´åˆšåˆšå¥½ï¼Œå“ˆå“ˆå“ˆã€‚ã€‚ã€‚ã€‚ã€‚',NULL,NULL,NULL,'SUBMIT',7,'2012-08-08',7,'2012-08-08'),('50PDF31TUPKG6PNI9KY6HH9L','HLXEZZWQL5IQV1J3S9MA19CB',6,NULL,NULL,NULL,NULL,'NEW',8,'2012-09-01',8,'2012-09-01'),('59GVTWUY21VY0XLFG5IOSPFF','ZSQS6SUD9HDZYVKYNLF5HCTP',4,'123',NULL,NULL,NULL,'SUBMIT',2,'2012-08-23',2,'2012-08-23'),('78EDRBZEWBAOE764XQU62C6A','WAZNJYUD0YYYY9IOR918YFQF',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),('7AKWEZIFDIN89W0N8Q911JDL','HLXEZZWQL5IQV1J3S9MA19CB',7,NULL,NULL,NULL,NULL,'NEW',10,'2012-09-01',10,'2012-09-01'),('7FZ9Q2G5E1BD3WXSVXD5MF0V','NNRXIJ577ZZHRYDG9V6M1EHP',4,'å·²ç»å®Œæˆäº†',77,'ä¸€å¡Œç³Šæ¶‚','åˆæ ¼','END',2,'2012-09-01',1,'2013-02-27'),('8FFFJLICRL2ZO9SIQM6120EU','NRVQYY8V8PJZGTCMH4M176CK',3,NULL,NULL,NULL,NULL,'NEW',5,'2012-08-20',5,'2012-08-20'),('8I5Y6E1R5L5NKYFPEP0PW2JI','6L1AUD7HO9P7XK9SPDCB2NDM',7,NULL,NULL,NULL,NULL,'NEW',10,'2012-09-01',10,'2012-09-01'),('8J50NF7CMJXTDNJ27DLWHIEH','SD6OGB1WJFQHQP2MVR35GO7U',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),('BKQAK25RZ6WTT0MT8T3213LQ','NNRXIJ577ZZHRYDG9V6M1EHP',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),('C10K6Y5HP2XNGW7HAO1ANSKI','WAZNJYUD0YYYY9IOR918YFQF',12,'yes',NULL,NULL,NULL,'SUBMIT',16,'2012-09-21',16,'2012-09-21'),('DH1IL3JF1WFFLP7GIAEFN09J','UO80AC6RPU64DF6C7KUIVQOA',6,NULL,NULL,NULL,NULL,'NEW',8,'2012-09-01',8,'2012-09-01'),('EG2IXYYH3AVHBLEXD7GAJN5R','HLXEZZWQL5IQV1J3S9MA19CB',10,'å·²ç»å®Œæˆäº†ï¼Œè¯·æµ‹è¯•',80,'ä¸é”™ï¼Œè›®å¥½çš„ï¼Œç»§ç»­åŠ æ²¹','è‰¯å¥½','END',14,'2012-08-08',2,'2012-08-27'),('EKVHIS6ABAD6IOZ04AQQL3ZU','NNRXIJ577ZZHRYDG9V6M1EHP',12,'wancheng',89,'ç»§ç»­åŠ æ²¹','è‰¯å¥½','END',16,'2012-09-21',1,'2013-02-27'),('GBJ8FL97WE5JO95EO0K56R5E','746SBK6A6IYTHCGDZL5Y7802',3,'å·²ç»åšå¥½äº†ï¼Œè¯·æŸ¥æ”¶',NULL,NULL,NULL,'SUBMIT',5,'2012-08-20',5,'2012-08-20'),('HEIIPX1GSCJZ6CK6IQ5IDXNM','WAZNJYUD0YYYY9IOR918YFQF',3,NULL,NULL,NULL,NULL,'NEW',5,'2012-10-17',5,'2012-10-17'),('HPOSPGLAMLQA70HQK08MNXR0','ZSQS6SUD9HDZYVKYNLF5HCTP',8,'å¥½äº†å•Š',NULL,NULL,NULL,'SUBMIT',12,'2012-09-01',12,'2012-09-01'),('HR4PCHJOEMVPFTFS7C5FVT4O','NRVQYY8V8PJZGTCMH4M176CK',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),('HZKIV5H3COIXTETYILNGEU2E','HTBVO5F7VX71AK51NKUDB6VX',8,'å·²ç»å®Œæˆäº†ï¼Œè¯·éªŒæ”¶',85,'å—¯ï¼Œä¹Ÿä¸é”™','è‰¯å¥½','END',12,'2012-08-08',1,'2012-08-13'),('I9MUCV1WOWZX1VQA94FJ9OVI','NRVQYY8V8PJZGTCMH4M176CK',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),('ISRWDUSCCRAQYXBMTTJVD5K8','ZSQS6SUD9HDZYVKYNLF5HCTP',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),('IYF5ZW3CBWNS4QO1F4NE2S8M','746SBK6A6IYTHCGDZL5Y7802',8,'okå•Š',NULL,NULL,NULL,'SUBMIT',12,'2012-09-01',12,'2012-09-01'),('JIRLT8RVBEL9XY88TQFTW5F6','HLXEZZWQL5IQV1J3S9MA19CB',5,'å·²ç»å®Œæˆï¼Œè¯·éªŒæ”¶',96,'éžå¸¸å¥½','ä¼˜ç§€','END',7,'2012-08-08',2,'2012-08-27'),('JPNDSL8ARUKNO4KPMW1VRG7G','X676YPIAZ4RKVQTNON2TDAO6',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),('K2B7MHV57MSXMJIRYBFGY7RR','NNRXIJ577ZZHRYDG9V6M1EHP',3,'å·²ç»å®Œæˆ',87,NULL,'è‰¯å¥½','END',5,'2012-10-17',1,'2012-10-29'),('LJDA087KCZDONFSJDDFHY3VH','746SBK6A6IYTHCGDZL5Y7802',12,'haole',NULL,NULL,NULL,'SUBMIT',16,'2012-08-23',16,'2012-08-23'),('LZK5D7MKMOUQX4FDGAYCP255','HTBVO5F7VX71AK51NKUDB6VX',3,NULL,NULL,NULL,NULL,'NEW',5,'2012-08-13',5,'2012-08-13'),('MC2LFPDKCRZQGYF6LWMBXFNO','HTBVO5F7VX71AK51NKUDB6VX',12,'å·²ç»åšå¥½äº†ï¼Œè¯·è€å¸ˆæŸ¥çœ‹ã€‚è°¢è°¢',100,'å¤ªæ¼‚äº®äº†ï¼ŒåŠ æ²¹','ä¼˜ç§€','END',16,'2012-08-09',1,'2012-08-13'),('MYMOQH98CB6IHMW19BOHWRWY','ZSQS6SUD9HDZYVKYNLF5HCTP',12,'haole',NULL,NULL,NULL,'SUBMIT',16,'2012-08-23',16,'2012-08-23'),('O2INGU7D9YMCYULQQ4NMH2TY','NRVQYY8V8PJZGTCMH4M176CK',4,'123',NULL,NULL,NULL,'SUBMIT',2,'2012-08-18',2,'2012-08-18'),('OM1AM8Z930JTTIN86J7L8ZG1','HTBVO5F7VX71AK51NKUDB6VX',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),('OOOW1YATNIENR6ZZMZWL9IHF','WAZNJYUD0YYYY9IOR918YFQF',8,'æ²¡æœ‰é—®é¢˜äº†å•Š',NULL,NULL,NULL,'SUBMIT',12,'2012-10-29',12,'2012-10-29'),('PRUIC7HGJ0H00MDPQYBRVF4I','NRVQYY8V8PJZGTCMH4M176CK',12,'ok',NULL,NULL,NULL,'SUBMIT',16,'2012-08-23',16,'2012-08-23'),('PXABL2S1CXCIHO6JIVIODWK9','UO80AC6RPU64DF6C7KUIVQOA',7,NULL,NULL,NULL,NULL,'NEW',10,'2012-09-01',10,'2012-09-01'),('QRM215VS9WOSF56Y0JG9UNC2','J42ZC7XR85ZU8ODX325RRTCE',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),('RR67IWIZ38W8RLVGEYSEALPK','6L1AUD7HO9P7XK9SPDCB2NDM',5,NULL,NULL,NULL,NULL,'NEW',7,'2012-09-27',7,'2012-09-27'),('U5OTVO71K8ZCKES8BO91Y78Z','KJ2RECRHMCGQI1S7KC90FO5J',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),('VKCVEFHSY8KITBUSNCTHJB1T','746SBK6A6IYTHCGDZL5Y7802',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),('VQPH0RXULA7S1NLKP6F12ATI','HTBVO5F7VX71AK51NKUDB6VX',4,NULL,NULL,NULL,NULL,'NEW',2,'2012-08-08',2,'2012-08-08'),('W7W233CL05ZLUSGPK1KC6OQQ','NNRXIJ577ZZHRYDG9V6M1EHP',8,'å¥½äº†',88.5,'å—¯ï¼Œä¸é”™','è‰¯å¥½','END',12,'2012-10-29',1,'2013-02-27'),('W8K6O9GQ8BBTZ3Z804NKUJEE','HTBVO5F7VX71AK51NKUDB6VX',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),('WCJB3N797CU999YSIAFJH1L4','ZSQS6SUD9HDZYVKYNLF5HCTP',3,'hjhg',NULL,NULL,NULL,'SUBMIT',5,'2012-10-17',5,'2012-10-17'),('XP4NUZEYB55BWKZWX2QZEAUR','HDNJWDRZNJI1314AZ2Y0IOEH',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),('XXZYD40LA2H5CCNX39CEBF9B','6L1AUD7HO9P7XK9SPDCB2NDM',6,NULL,NULL,NULL,NULL,'NEW',8,'2012-09-01',8,'2012-09-01'),('YRJ95HHOFS65CV9L3IE4Y6EW','NRVQYY8V8PJZGTCMH4M176CK',8,'å·²ç»å®Œæˆï¼Œå¿«å§ã€‚',NULL,NULL,NULL,'SUBMIT',12,'2012-08-17',12,'2012-08-17'),('Z9X5SZ7RH3EIGV3D6PLXS3FQ','WAZNJYUD0YYYY9IOR918YFQF',4,'123',NULL,NULL,NULL,'SUBMIT',2,'2012-09-21',2,'2012-09-21'),('ZESVPPOYL2D2T0G8F1NWVGUD','746SBK6A6IYTHCGDZL5Y7802',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21');

/*Table structure for table `sys_access_control` */

DROP TABLE IF EXISTS `sys_access_control`;

CREATE TABLE `sys_access_control` (
  `ac_id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(500) NOT NULL,
  `enable_flag` char(1) NOT NULL,
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`ac_id`),
  KEY `url` (`url`(255))
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8;

/*Data for the table `sys_access_control` */

insert  into `sys_access_control`(`ac_id`,`url`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values (1,'modules/sys/function.jsp','F',1,'2012-01-22',2,'2012-03-24'),(2,'modules/sys/user.jsp','F',2,'2012-03-05',2,'2012-03-24'),(4,'main.jsp','S',2,'2012-03-05',2,'2012-03-24'),(5,'left.jsp','S',2,'2012-03-05',2,'2013-06-25'),(6,'modules/sys/accessControl.jsp','F',2,'2012-03-05',2,'2012-03-24'),(7,'modules/sys/module.jsp','F',2,'2012-03-05',2,'2012-03-24'),(8,'modules/sys/role.jsp','F',2,'2012-03-05',2,'2012-03-24'),(9,'modules/sys/power.jsp','F',2,'2012-03-05',2,'2012-03-24'),(10,'system.jsp','F',2,'2012-03-21',2,'2012-08-16'),(12,'modules/sys/function.query','F',2,'2012-04-26',2,'2012-04-26'),(13,'modules/fnd/teacher.jsp','F',2,'2012-05-08',2,'2012-05-08'),(15,'modules/fnd/class.jsp','F',2,'2012-05-08',2,'2012-05-08'),(16,'modules/fnd/student.jsp','F',2,'2012-05-08',2,'2012-05-08'),(17,'modules/job/jobManage.jsp','F',2,'2012-05-09',2,'2012-05-09'),(18,'modules/job/newJob.jsp','F',2,'2012-05-09',2,'2012-05-09'),(19,'modules/job/myJob.jsp','F',2,'2012-05-10',2,'2012-05-10'),(20,'modules/job/myHistoryJob.jsp','F',2,'2012-05-17',2,'2012-05-17'),(21,'modules/job/jobQuery.jsp','F',2,'2012-05-22',2,'2012-05-22'),(22,'modules/job/showCheckedJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),(23,'modules/job/showMyJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),(24,'uploadShow.jsp','S',2,'2012-07-07',2,'2012-07-07'),(25,'modules/job/updateMyJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),(26,'modules/job/showJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),(27,'modules/job/updateJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),(28,'modules/job/myJobQuery.jsp','F',2,'2012-07-19',2,'2012-07-19'),(29,'modules/cos/courseQuery.jsp','F',2,'2012-07-30',2,'2012-07-30'),(30,'modules/cos/course.jsp','F',2,'2012-07-30',2,'2012-07-30'),(31,'cos/course.query','F',2,'2012-08-08',2,'2012-09-28'),(32,'sys/power.query','F',2,'2012-08-08',2,'2012-08-08'),(33,'sys/accessControl.query','F',2,'2012-08-08',2,'2012-08-08'),(34,'sys/role.query','F',2,'2012-08-08',2,'2012-08-08'),(35,'upload.query','S',2,'2012-08-08',2,'2012-08-08'),(36,'sys/function.query','F',2,'2012-08-08',2,'2012-08-08'),(37,'sys/module.query','F',2,'2012-08-08',2,'2012-08-08'),(38,'sys/user.query','F',2,'2012-08-08',2,'2012-08-08'),(39,'download.jsp','S',2,'2012-08-09',2,'2012-08-09'),(40,'modules/sys/pagerAssign.jsp','F',2,'2012-08-15',2,'2012-08-15'),(41,'functionLoad.query!load','S',2,'2012-08-17',2,'2012-08-17'),(42,'modules/fnd/branch.jsp','F',2,'2012-08-18',2,'2012-08-18'),(43,'index.jsp','S',2,'2013-07-26',2,'2013-07-26');

/*Table structure for table `sys_code` */

DROP TABLE IF EXISTS `sys_code`;

CREATE TABLE `sys_code` (
  `code_id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(200) NOT NULL,
  `code_value` varchar(400) NOT NULL,
  `code_name` varchar(400) NOT NULL,
  `priority` int(11) DEFAULT NULL,
  PRIMARY KEY (`code_id`),
  KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Data for the table `sys_code` */

insert  into `sys_code`(`code_id`,`code`,`code_value`,`code_name`,`priority`) values (1,'JOB_STATUS','NEW','æ–°å»º',10),(2,'JOB_STATUS','DOWN','å·²å¸ƒç½®',20),(3,'JOB_STATUS','CHECK','å®¡æ‰¹ä¸­',30),(4,'JOB_STATUS','END','å®Œæˆ',40),(5,'JOB_LINE_STATUS','NEW','æœªæäº¤',10),(6,'JOB_LINE_STATUS','SUBMIT','å·²æäº¤',20),(7,'JOB_LINE_STATUS','END','å·²å®¡æ‰¹',30),(8,'JOB_PRIORITY','ä¼˜ç§€','ä¼˜ç§€',10),(9,'JOB_PRIORITY','è‰¯å¥½','è‰¯å¥½',20),(10,'JOB_PRIORITY','åˆæ ¼','åˆæ ¼',30),(11,'JOB_PRIORITY','ä¸åˆæ ¼','ä¸åˆæ ¼',40);

/*Table structure for table `sys_function` */

DROP TABLE IF EXISTS `sys_function`;

CREATE TABLE `sys_function` (
  `function_id` int(11) NOT NULL AUTO_INCREMENT,
  `function_code` varchar(100) NOT NULL,
  `function_des` varchar(200) NOT NULL,
  `jsp_pager` varchar(150) NOT NULL,
  `image` varchar(150) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `function_module` int(11) NOT NULL,
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`function_id`),
  KEY `function_code` (`function_code`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;

/*Data for the table `sys_function` */

insert  into `sys_function`(`function_id`,`function_code`,`function_des`,`jsp_pager`,`image`,`priority`,`function_module`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values (1,'SYS001','ç”¨æˆ·å®šä¹‰','modules/sys/user.jsp','70000001.gif',10,1,1,'2011-08-14',2,'2011-12-30'),(2,'SYS002','åŠŸèƒ½å®šä¹‰','modules/sys/function.jsp','80000000.gif',40,1,1,'2011-08-14',2,'2012-04-24'),(3,'SYS003','æ¨¡å—å®šä¹‰','modules/sys/module.jsp','50000000.gif',30,1,1,'2011-08-14',2,'2011-12-26'),(4,'SYS004','è§’è‰²å®šä¹‰','modules/sys/role.jsp','90000000.gif',20,1,2,'2011-12-20',2,'2012-04-11'),(5,'SYS005','æƒé™æŽ§åˆ¶','modules/sys/power.jsp','12000000.gif',50,1,2,'2011-12-20',2,'2012-01-17'),(13,'SYS006','è®¿é—®æŽ§åˆ¶','modules/sys/accessControl.jsp',NULL,60,1,2,'2012-03-05',2,'2012-04-12'),(14,'SYS007','ç³»ç»Ÿç®¡ç†','system.jsp',NULL,70,1,2,'2012-03-20',2,'2013-03-30'),(25,'JOB001','ä½œä¸šæ–°å¢ž','modules/job/newJob.jsp',NULL,10,7,2,'2012-05-08',2,'2013-05-31'),(26,'JOB002','ä½œä¸šå·¥ä½œå°','modules/job/jobManage.jsp','50000000.gif',20,7,2,'2012-05-08',2,'2013-02-26'),(27,'JOB003','ä½œä¸šåŽ†å²æŸ¥è¯¢','modules/job/jobQuery.jsp',NULL,30,7,2,'2012-05-08',2,'2012-05-08'),(28,'JOB101','æˆ‘çš„å½“å‰ä½œä¸š','modules/job/myJob.jsp',NULL,40,7,2,'2012-05-08',2,'2013-05-29'),(29,'JOB102','æˆ‘çš„åŽ†å²ä½œä¸š','modules/job/myHistoryJob.jsp',NULL,50,7,2,'2012-05-08',2,'2012-07-22'),(33,'TEST001','ViewForm æµ‹è¯•','modules/test/testViewForm.jsp',NULL,10,9,2,'2013-06-06',2,'2013-06-06'),(34,'TEST002','ç»¼åˆæµ‹è¯•','modules/test/testAll.jsp',NULL,20,9,2,'2013-06-06',2,'2013-06-06'),(35,'TEST003','treeæµ‹è¯•','modules/test/testTree.jsp',NULL,30,9,2,'2013-06-06',2,'2013-06-06'),(36,'TEST004','gridçº§è”æµ‹è¯•','modules/test/cascadeGrid.jsp',NULL,40,9,2,'2013-06-06',2,'2013-06-06'),(37,'TEST005','gridå¤åˆè¡¨å¤´','modules/test/testGrid.jsp',NULL,50,9,2,'2013-06-06',2,'2013-06-06'),(39,'TEST007','tabæµ‹è¯•','modules/test/testTab.jsp',NULL,70,9,2,'2013-06-06',2,'2013-06-06'),(44,'GC005','ç»“ç®—æŸ¥è¯¢','modules/gc/settlementQuery.jsp',NULL,50,10,2,'2013-06-09',2,'2013-06-09'),(45,'GC006','æŠ¥å·¥å–æ¶ˆ','modules/gc/workUnRegist.jsp',NULL,35,10,2,'2013-06-09',2,'2013-06-09'),(46,'GC007','å·¥æ—¶è¡¥æŠ¥','modules/gc/workRegistAdd.jsp',NULL,33,10,2,'2013-06-26',2,'2013-06-26'),(47,'TEST008','panelæµ‹è¯•','modules/test/testPanel.jsp',NULL,80,9,2,'2013-07-18',2,'2013-07-18'),(48,'TEST009','chartå›¾åƒæŠ¥è¡¨','modules/test/chart.jsp',NULL,90,9,2,'2013-07-19',2,'2014-06-03'),(49,'TEST010','formè¡¨å•','modules/test/form.jsp',NULL,5,9,2,'2013-07-21',2,'2013-07-21'),(50,'TEST011','treegridæµ‹è¯•','modules/test/testTreeGrid.jsp',NULL,100,9,2,'2013-07-22',2,'2013-07-22'),(51,'TEST012','menuå³å‡»èœå•','modules/test/testMenu.jsp',NULL,35,9,2,'2013-08-16',2,'2013-08-16'),(54,'TEST013','chartå›¾è¡¨ç»¼åˆæµ‹è¯•','modules/test/chartGrid.jsp',NULL,90,9,2,'2014-04-30',2,'2014-06-03'),(55,'TEST014','åŠ¨æ€åˆ—è¡¨','modules/test/dynamicGrid.jsp',NULL,70,9,2,'2014-04-30',2,'2014-04-30'),(57,'TEST015','imageå›¾ç‰‡æµ‹è¯•','modules/test/testImage.jsp',NULL,110,9,2,'2014-07-23',2,'2014-07-23'),(60,'TEST016','çº§è”æµ‹è¯•','modules/test/comboCasecade.jsp',NULL,111,9,2,'2014-08-19',2,'2014-08-19');

/*Table structure for table `sys_module` */

DROP TABLE IF EXISTS `sys_module`;

CREATE TABLE `sys_module` (
  `module_id` int(11) NOT NULL AUTO_INCREMENT,
  `module_code` varchar(100) NOT NULL,
  `module_name` varchar(200) NOT NULL,
  `priority` int(11) DEFAULT NULL,
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  `image` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`module_id`),
  UNIQUE KEY `module_code` (`module_code`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8;

/*Data for the table `sys_module` */

insert  into `sys_module`(`module_id`,`module_code`,`module_name`,`priority`,`create_by`,`create_date`,`last_update_by`,`last_update_date`,`image`) values (1,'SYS','ç³»ç»Ÿè®¾ç½®',10,1,'2011-08-14',2,'2013-05-31','nav_icon1.png'),(6,'FND','åŸºç¡€å®šä¹‰',20,2,'2012-05-07',2,'2013-06-25','nav_icon2.png'),(7,'JOB','ä½œä¸šç®¡ç†',30,2,'2012-05-08',2,'2012-05-08','nav_icon3.png'),(8,'COS','è¯¾ç¨‹ç®¡ç†',40,2,'2012-07-30',2,'2013-05-31','nav_icon4.png'),(9,'TEST','æµ‹è¯•ç”¨ä¾‹',50,2,'2013-06-06',2,'2013-06-06','nav_icon5.png'),(10,'GC','å·¥ç¨‹è®¡è´¹',60,2,'2013-06-09',2,'2013-06-09','nav_icon6.png');

/*Table structure for table `sys_pager_assign` */

DROP TABLE IF EXISTS `sys_pager_assign`;

CREATE TABLE `sys_pager_assign` (
  `assign_id` int(11) NOT NULL AUTO_INCREMENT,
  `function_id` int(11) NOT NULL,
  `ac_id` int(11) NOT NULL,
  `create_by` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `last_update_by` int(11) DEFAULT NULL,
  `last_update_date` date DEFAULT NULL,
  PRIMARY KEY (`assign_id`),
  KEY `ac_id` (`ac_id`),
  KEY `function_id` (`function_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;

/*Data for the table `sys_pager_assign` */

insert  into `sys_pager_assign`(`assign_id`,`function_id`,`ac_id`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values (3,13,6,2,'2012-03-24',2,'2012-03-24'),(5,3,7,2,'2012-03-24',2,'2012-10-10'),(6,5,9,2,'2012-03-24',2,'2012-03-24'),(7,4,8,2,'2012-03-24',2,'2012-03-24'),(8,1,2,2,'2012-03-24',2,'2012-03-24'),(10,2,1,2,'2012-03-24',2,'2012-03-24'),(11,14,10,2,'2012-04-25',2,'2012-04-25'),(13,2,12,2,'2012-04-26',2,'2012-04-26'),(14,22,13,2,'2012-05-08',2,'2012-05-08'),(16,23,15,2,'2012-05-08',2,'2012-05-08'),(17,21,16,2,'2012-05-08',2,'2012-05-08'),(18,26,17,2,'2012-05-09',2,'2012-05-09'),(19,25,18,2,'2012-05-09',2,'2012-05-09'),(20,28,19,2,'2012-05-10',2,'2012-05-10'),(21,29,20,2,'2012-05-17',2,'2012-05-17'),(22,27,21,2,'2012-05-22',2,'2012-05-22'),(23,27,22,2,'2012-07-07',2,'2012-07-07'),(24,26,22,2,'2012-07-07',2,'2012-07-07'),(25,29,23,2,'2012-07-07',2,'2012-07-07'),(26,28,25,2,'2012-07-07',2,'2012-07-07'),(27,27,26,2,'2012-07-07',2,'2012-07-07'),(28,26,27,2,'2012-07-07',2,'2012-07-07'),(29,28,28,2,'2012-07-19',2,'2012-07-19'),(30,32,29,2,'2012-07-30',2,'2012-07-30'),(31,26,30,2,'2012-07-30',2,'2014-09-18'),(32,21,31,2,'2012-08-08',2,'2014-04-11'),(33,5,32,2,'2012-08-08',2,'2012-08-08'),(34,13,33,2,'2012-08-08',2,'2012-08-08'),(35,4,34,2,'2012-08-08',2,'2012-08-08'),(36,2,36,2,'2012-08-08',2,'2012-08-08'),(37,3,37,2,'2012-08-08',2,'2012-08-08'),(38,1,38,2,'2012-08-08',2,'2012-08-08'),(39,13,40,2,'2012-08-15',2,'2012-08-15'),(40,30,42,2,'2012-08-18',2,'2012-08-18');

/*Table structure for table `sys_power` */

DROP TABLE IF EXISTS `sys_power`;

CREATE TABLE `sys_power` (
  `power_id` int(11) NOT NULL AUTO_INCREMENT,
  `function_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  `enable_flag` char(1) DEFAULT NULL,
  PRIMARY KEY (`power_id`),
  KEY `role_id` (`role_id`),
  KEY `function_id` (`function_id`)
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8;

/*Data for the table `sys_power` */

insert  into `sys_power`(`power_id`,`function_id`,`role_id`,`create_by`,`create_date`,`last_update_by`,`last_update_date`,`enable_flag`) values (87,1,3,2,'2012-03-21',2,'2012-03-21','Y'),(88,2,3,2,'2012-03-21',2,'2012-03-24','Y'),(89,3,3,2,'2012-03-21',2,'2012-03-21','Y'),(90,4,3,2,'2012-03-21',2,'2012-03-21','Y'),(91,5,3,2,'2012-03-21',2,'2012-03-21','Y'),(92,13,3,2,'2012-03-21',2,'2012-03-21','Y'),(93,14,3,2,'2012-03-21',2,'2012-03-21','Y'),(97,1,5,2,'2012-03-21',2,'2012-07-29',NULL),(98,2,5,2,'2012-03-21',2,'2012-07-29',NULL),(99,3,5,2,'2012-03-21',2,'2012-07-29',NULL),(100,4,5,2,'2012-03-21',2,'2012-07-29',NULL),(101,5,5,2,'2012-03-21',2,'2012-07-29',NULL),(102,13,5,2,'2012-03-21',2,'2012-07-29',NULL),(103,14,5,2,'2012-03-21',2,'2012-07-29',NULL),(124,4,6,2,'2012-04-25',2,'2012-07-28',NULL),(127,5,6,2,'2012-04-26',2,'2012-07-28',NULL),(135,25,3,2,'2012-05-08',2,'2012-05-08','Y'),(136,26,3,2,'2012-05-08',2,'2012-05-08','Y'),(137,27,3,2,'2012-05-08',2,'2012-05-08','Y'),(138,28,3,2,'2012-05-08',2,'2012-05-08','Y'),(139,29,3,2,'2012-05-08',2,'2012-05-08','Y'),(140,28,6,2,'2012-05-08',2,'2012-05-08','Y'),(141,29,6,2,'2012-05-08',2,'2012-05-08','Y'),(142,25,5,2,'2012-05-08',2,'2012-05-08','Y'),(143,26,5,2,'2012-05-08',2,'2012-05-08','Y'),(144,27,5,2,'2012-05-08',2,'2012-05-08','Y'),(145,29,5,2,'2012-05-22',2,'2012-07-29',NULL),(159,1,7,2,'2012-08-27',2,'2012-09-02','N'),(160,33,3,2,'2013-06-06',2,'2013-06-06','Y'),(161,34,3,2,'2013-06-06',2,'2013-06-06','Y'),(162,35,3,2,'2013-06-06',2,'2013-06-06','Y'),(163,36,3,2,'2013-06-06',2,'2013-06-06','Y'),(164,37,3,2,'2013-06-06',2,'2013-06-06','Y'),(166,39,3,2,'2013-06-06',2,'2013-06-06','Y'),(171,44,3,2,'2013-06-09',2,'2013-06-09','Y'),(172,45,3,2,'2013-06-09',2,'2013-06-09','Y'),(173,46,3,2,'2013-06-26',2,'2013-06-26','Y'),(174,47,3,2,'2013-07-18',2,'2013-07-18','Y'),(177,48,3,2,'2014-04-30',2,'2014-04-30','Y'),(178,49,3,2,'2014-04-30',2,'2014-04-30','Y'),(179,50,3,2,'2014-04-30',2,'2014-04-30','Y'),(180,51,3,2,'2014-04-30',2,'2014-04-30','Y'),(181,54,3,2,'2014-04-30',2,'2014-04-30','Y'),(182,55,3,2,'2014-04-30',2,'2014-04-30','Y'),(184,57,3,2,'2014-07-23',2,'2014-07-23','Y'),(186,60,3,2,'2014-08-19',2,'2014-08-19','Y');

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_code` varchar(100) NOT NULL,
  `role_description` varchar(200) NOT NULL,
  `create_by` int(11) NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `sys_role` */

insert  into `sys_role`(`role_id`,`role_code`,`role_description`,`create_by`,`last_update_by`,`create_date`,`last_update_date`) values (3,'ADMIN','ç³»ç»Ÿç®¡ç†å‘˜',2,2,'2011-12-29','2014-08-14'),(5,'TEACHER','æŽˆè¯¾æ•™å¸ˆ',2,2,'2012-03-21','2013-10-17'),(6,'STUDENT','å­¦ç”Ÿ',2,2,'2012-04-12','2013-10-17'),(7,'JWCADMIN','æ•™åŠ¡å¤„ç®¡ç†å‘˜',2,2,'2012-08-27','2013-05-31');

/*Table structure for table `sys_upload_file` */

DROP TABLE IF EXISTS `sys_upload_file`;

CREATE TABLE `sys_upload_file` (
  `file_id` char(24) NOT NULL,
  `file_name` varchar(1000) DEFAULT NULL,
  `file_content` longblob,
  `file_type` varchar(200) DEFAULT NULL,
  `file_size` varchar(100) DEFAULT NULL,
  `table_name` varchar(200) DEFAULT NULL,
  `pk_value` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  KEY `table_name` (`table_name`,`pk_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_upload_file` */

insert  into `sys_upload_file`(`file_id`,`file_name`,`file_content`,`file_type`,`file_size`,`table_name`,`pk_value`) values ('15WAS558F3C1S7WCQ6BQ0YE3','u14.png','‰PNG\r\n\Z\n\0\0\0\rIHDR\0\0™\0\0\0=\0\0\0\'3­C\0\0\0sRGB\0®Îé\0\0\0gAMA\0\0±üa\0\0\0	pHYs\0\0Ã\0\0ÃÇo¨d\0\0MIDATx^íÝ»Žâ@P3ûM¼!â«œmÆWñ†š,¡]$g¾Žê ‘ÌÈWªÓ•”ÜÝLÚ¶ýnz>Ûí¶Ùl6}ÿò7 @€\0(,p:šãñØ+0y\r™¿ßÂ<J\'@€\0 @€\0„À~¿oþü¾±üëe‚S @€\0j¼Þp~Õ&P= @€\0$™IMY @€\0(.`È,Þ\0Ê\'@€\0 @€@RÀ™Ô”E€\0 @€\0âÝÅ?ëõº8ƒò	 @€\0 @`¨Àù|vñÏPDÏ @€\0 @€À?Ûeu @€\0Ä™1JA @€\0 àL¦ @€\0 @€\0ˆ€3™F! @€\0 ð°]V/ @€\0 @€@LÀ£D€\0 @€\0Ý™ÌÕjE‚\0 @€\0¸\\.~\'s ‡	 @€\0 @àCÀvY\rA€\0 @€\01CfŒR @€\08“© @€\0 @ \"àLf„Q @€\0¼l—Õ @€\0 0dÆ( @€\0 @€@w&s¹\\’ @€\0 @€\0ƒ®×«ßÉ$èa @€\0ø°]VC @€\0 @€@LÀ£D€\0 @€\0†L=@€\0 @€\01ÿÄ( @€\0 @ ¶€‹j¯¿ê	 @€\0 °]6N*\0 @€\0u™u×^å @€\0ˆtg2‹E<X  @€\0Ô¸Ýn7™µÖ\\µ @€\0UÀ9*¯p @€\0Ô0dÖZoÕ @€\0 @`TCæ¨¼Â	 @€\0 PKÀÅ?µÖ[µ @€\0MÀÅ?£Ñ\n&@€\0 @€@MÛek®»ª	 @€\0 0Š€!sV¡ @€\0¨)ÐÉœÏç5«W5 @€\0Äî÷{ãMfŒS @€\02õ\0 @€\0Ä™1JA @€\0 àL¦ @€\0 @€\0ˆ€3™F! @€\0 ð°]V/ @€\0 @€@LÀ£D€\0 @€\0Ý™ÌÙlF‚\0 @€\0x<~\'s ‡	 @€\0 @àCÀvY\rA€\0 @€\01CfŒR @€\08“© @€\0 @ \"àLf„Q @€\0¼l—Õ @€\0 0dÆ( @€\0 @€€!S @€\0 @€@L »øg:Æ @€\0 @€@MçóÙx“YsíUM€\0 @€\0Q&mÛ~÷%ïv»æõõ!@€\0 @€\0ÿ‡æõíûü\0qŒQÆ£w€\0\0\0\0IEND®B`‚','image/png','1.43KB','job_headers','XSF060BUDZNTYVAU1VALQ89S'),('6VW1RBG21QM44V67BTHJ1F4S','RankTransform.class','Êþº¾\0\0\01\0µ\0\0!com/nfwork/erp/core/RankTransform\0\0)com/nfwork/dbfound/model/base/JavaSupport\0<init>\0()V\0Code\n\0\0	\0\0\0LineNumberTable\0LocalVariableTable\0this\0#Lcom/nfwork/erp/core/RankTransform;\0execute\0\nExceptions\0\0java/lang/Exception	\0\0\0\0\0context\0!Lcom/nfwork/dbfound/core/Context;\n\0\0\0\0com/nfwork/dbfound/core/Context\0\Z\0\0getCurrentModel\0()Ljava/lang/String;	\0\0\0\0\0params\0Ljava/util/Map;\0!\0\nquery_name\0#\0%\0$\0\rjava/util/Map\0&\0\'\0get\0&(Ljava/lang/Object;)Ljava/lang/Object;\0)\0#com/nfwork/dbfound/model/bean/Param\n\0(\0+\0,\0\0getStringValue\0.\0base_column\00\0transform_column\n\02\04\03\0$com/nfwork/dbfound/model/ModelEngine\05\06\0query\0s(Lcom/nfwork/dbfound/core/Context;Ljava/lang/String;Ljava/lang/String;)Lcom/nfwork/dbfound/dto/QueryResponseObject;\n\08\0:\09\0*com/nfwork/dbfound/dto/QueryResponseObject\0;\0<\0getDatas\0()Ljava/util/List;\0>\0java/util/ArrayList\n\0=\0	\0A\0java/util/HashMap\n\0@\0	\0D\0F\0E\0java/util/List\0G\0H\0iterator\0()Ljava/util/Iterator;\0J\0L\0K\0java/util/Iterator\0M\0N\0next\0()Ljava/lang/Object;\n\0P\0R\0Q\0java/lang/Object\0S\0\0toString\0D\0U\0V\0W\0add\0(Ljava/lang/Object;)Z\0#\0Y\0Z\0[\0put\08(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;\0J\0]\0^\0_\0hasNext\0()Z\0D\0a\0&\0b\0(I)Ljava/lang/Object;\0#\0d\0e\0f\0keySet\0()Ljava/util/Set;\0h\0F\0i\0\rjava/util/Set\n\0k\0m\0l\0java/lang/String\0n\0W\0equals\n\0\0p\0q\0r\0getValue\0G(Ljava/util/Map;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;	\0\0t\0u\0v\0\noutMessage\0Z\n\08\0	\n\08\0y\0z\0{\0\nsetSuccess\0(Z)V\n\08\0}\0~\0\0setDatas\0(Ljava/util/List;)V\0D\0\0‚\0ƒ\0size\0()I\n\08\0…\0†\0‡\0setTotalCounts\0(J)V	\0\0‰\0Š\0‹\0response\0(Ljavax/servlet/http/HttpServletResponse;\n\0\0\0Ž\0 com/nfwork/dbfound/util/JsonUtil\0\0‘\0\nbeanToJson\0&(Ljava/lang/Object;)Ljava/lang/String;\n\0“\0•\0”\0 com/nfwork/dbfound/web/WebWriter\0–\0—\0\njsonWriter\0=(Ljavax/servlet/http/HttpServletResponse;Ljava/lang/String;)V\0	modelName\0Ljava/lang/String;\0	queryName\0\nbaseColumn\0transformColumn\0datas\0Ljava/util/List;\0rows\0bufferDatas\0map\0row\0columns\0Ljava/util/Iterator;\0key\0newDatas\0column\0newData\0value\0Ljava/lang/Object;\0object\0,Lcom/nfwork/dbfound/dto/QueryResponseObject;\0LocalVariableTypeTable\0!Ljava/util/List<Ljava/util/Map;>;\0$Ljava/util/List<Ljava/lang/String;>;\02Ljava/util/Map<Ljava/lang/String;Ljava/util/Map;>;\0	Signature\0j(Ljava/util/Map<Ljava/lang/String;Ljava/util/Map;>;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;\0\nSourceFile\0RankTransform.java\0!\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0/\0\0\0\0\0*·\0±\0\0\0\0\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\r\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0f\0\0\0\0²*´\0¶\0L*´\0 ¹\0\"\0À\0(¶\0*M*´\0-¹\0\"\0À\0(¶\0*N*´\0/¹\0\"\0À\0(¶\0*:*´\0+,¸\01¶\07:»\0=Y·\0?:»\0@Y·\0B:¹\0C\0:	§\02	¹\0I\0À\0#:-¹\0\"\0¶\0O:\n\n¹\0T\0W\n¹\0X\0W	¹\0\\\0šÿÊ»\0=Y·\0?:¹\0`\0À\0#¹\0c\0¹\0g\0:	§\0%	¹\0I\0¶\0O:\n\n-¶\0j™\0§\0\r\n¹\0T\0W	¹\0\\\0šÿ×»\0=Y·\0?:	¹\0C\0:§\0h¹\0I\0À\0k:\n»\0@Y·\0B:\n¹\0X\0W	¹\0T\0W¹\0C\0:§\0\'¹\0I\0À\0k:\r*\r\n·\0o:\r¹\0X\0W¹\0\\\0šÿÕ¹\0\\\0šÿ”*´\0µ\0s»\08Y·\0w:\n\n¶\0x\n	¶\0|\n	¹\0€\0…¶\0„*´\0´\0ˆ\n¸\0Œ¸\0’±\0\0\0\0\n\0\0\0¢\0(\0\0\0\0\0\0\Z\0\0,\0\0?\0\0H\0\Z\0K\0\0M\0\0V\0\0_\0 \0w\0!\0„\0\"\0Ž\0#\0š\0 \0¤\0\'\0­\0)\0Ç\0+\0Ó\0,\0Ü\0-\0ß\0/\0é\0)\0ë\0*\0ð\0)\0ó\03\0ü\04\05\06)\073\08K\09W\0:c\08m\04w\0>\0?ˆ\0@Ž\0A•\0B¢\0C±\0D\0\0\0\0À\0\0\0²\0\0\r\0\0\0ª\0˜\0™\0\0\Z˜\0š\0™\0\0,†\0›\0™\0\0?s\0œ\0™\0\0Me\0\0ž\0\0V\\\0Ÿ\0ž\0\0_S\0 \0\0\0w\0#\0¡\0\0\0„\0\0¢\0™\0\n\0­\0£\0ž\0\0Ä\0/\0G\0¤\0	\0Ó\0\0¥\0™\0\n\0ü\0¶\0¦\0ž\0	\0Y\0§\0™\0\n\0P\0¨\0\0K\0\0¢\0™\0\rW\0\0©\0ª\0ˆ\0*\0«\0¬\0\n\0­\0\0\04\0\0Me\0\0®\0\0V\\\0Ÿ\0¯\0\0_S\0 \0°\0\0­\0£\0¯\0\0ü\0¶\0¦\0®\0	\0\0q\0r\0\0±\0\0\0\0²\0\0\0\0k\0\0\0\0\0+,¹\0\"\0À\0#-¹\0\"\0°\0\0\0\0\n\0\0\0\0\0\0\0G\0\0\0\0*\0\0\0\0\0\0\r\0\0\0\0\0\0\0\0\0\0\0\0¢\0™\0\0\0\0\0§\0™\0\0­\0\0\0\0\0\0\0\0\0°\0\0\0³\0\0\0\0´','application/octet-stream','3.58KB','job_headers','8VQ7YJTF2KF0NR35F9FB6ATX'),('BL5Z5UWSRJRCM66FYOSDYO6W','2014-05-15 14:22:07çš„å±å¹•æˆªå›¾.png','‰PNG\r\n\Z\n\0\0\0\rIHDR\0\0Ð\0\0ê\0\0\0¢<î\0\0\0sBITÛáOà\0\0\0tEXtSoftware\0gnome-screenshotï¿>\0\0 \0IDATxœìÝwxåÚðç™íé½\0Iè½iè±wQìXÑ£ØQV¬(\n¨+Ui*E©Ò	’„ô^6ÛwfÞïM–ô\0Ç…Àwÿ./Üò.!Ù;oy†qÎ	\0\0\0À—„sÝ\0\0\0\0¸ð!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€ÏIçº\0ÿŒÔRûº¤ò\rÇ*³Kcz¿ÿ¯ŽçºEpJÊíò|kb%«ÒQíT$Q’5¢–ñöFa@”qT\\`As®Û\0ÿ\0Æ9?×m\08s²Ê7¤W}»³(1ÃìýÇÌ­{¼_‡ Ý¹m´,£Â¹âHùþ|«[VUÎµÓK¢^§©p)V§ÕêpÛ\\Lvï\Ztß¨øNá¦sÝ^\0øŸ ‡ÎWY]y¸bÑŽÂÜbÕÏÍœÓöLs‡áçªmÐ2»¬.9Xòg†E½FÒkH$®™ŸVâL°ËŠŽ‘cäv+«öæ¯Þ•}ÏÅñNè¦1\np¾Bà€óSáË’Ë¿Ú^T\\áäœ3¸JDÄ8ãT“<ŽäZ£M*±º_û3·Ôªu’V#I¢ ×	ÌO\"“Vª°Ë‘†‘@Äj¿š²[™¿úÈî£yŸÏdBÇÀy	¿.ÀùDQùÊ£•×,L}k}^q•›#bDŒy°“{f”9Ï]3¡Ye6÷Óë³\nÌ²À¸Ûå´Z-6«•d—¿Ä¢ô~:‰ˆTóÅ<ù%eloJÑíï®±:]çªñ\0ð¿@œ7¶åXßÛR˜^`¯@aDœ˜÷·`FT§“#¯£Í‘Uþìê”äcÙöªJ—Ã©ªž/#F‚(øÚG…EFGsIÏUNœçœó“ãeŒŽd?õÕŸ=xÕ¹{\0p†8à<p¼Òõö¶¢éÕ¤rÆˆs\"Æ÷üþ[þ†çUVÙ¥¨Zù·%‹6Ú¸i\'4L’HÒ‚@Þ®)N2$&»ËóLã\"çB½ØQû•]»óÈã†Žë‚ñ2€ó´iÕnõóýe?ì¯Ý\ncž„Á#Nœx½NŽš®VóÁär)U%ÜÔJàP«Ž]YÌ¤5Ç2Zß²8ÝŸüvHÐè™$‘ \n‚À1FŒ…èßkRßöèÙ¹ÚáZžTüÅöÜJ‹ZÓÉÁÉ>˜$}´íÄØÎac-_\0Úh»ÖgYÞÙVRVí&•“çÓ…q^“*¼R½§žQÎ©Ê.‡›š)áÀ¹í¯´êÏ¶ØÖ$q«“é4í3_—¢ÏÚ[ûhMb–Å¡2F%&Š‚(zGBDÀWwð¯7Ô_¯½{H»qÝÂïþrov¡ƒTO/‡ÊIu†¬\"ËöóèªÝ®˜Í¤ªL¯ý˜;\0Ú.h‹\nmòœ¥Û3-¤ÔN×ðvfœœLÈjz5¼i£Î#\"ªr*ÏÌÝŠuÅÁÊ·sí=qr£Óm[w8àž>~[mEEy¹F«õóó;›Ý˜œçI¢$y2‰‚N+Í»uHƒ´á {õºžS>ÙÁ¹J\\%.sUÖ˜Â¹[ùmÉšöûV;‹JÈé‚tBˆQªë˜ íØÍÐ£Ÿ®SWBÿ@ƒÀmÎòã–÷w—Ù\ncŒ³:#&Þ¨áIÞ®ªÙÎ«»±ÚQ?pÈjõÏû+_Yã>VÐø¢Î™tÊ£ººº  ßírûDFFêt§±PÓb±Øl6Q%é|.úê«•+Wh4šzxÌØ±gíºÉU‚(‰’F$&I‚$2Qß\'¦chK½†\'÷‰:˜RÄU™«2c$üUYI6;åÌŠ(\nœT«›é]reÏHwæZ~[-„NºÁxÑð³öî\0 UÐ†”9•Ww—oÏ²’Êkz5X¡“ËR¼¬¿Q\'‚ØÜªwëºÃÏ­tÌiîÒîãÅ§ÒÂÌÌÌo¾^|01Ñív{¶†¾ýú]Ã=zôháÀÂÂÂõëÖíú{gaa¡,ËDd2ùuïÞ}ìå—1¢qòp:Û·o3\ZÃ.\ZÎjY·X,yyyÔTu`½Á×jûóóó—/_æ9ÿW_.¸høp£Ñx\nïûåt+f»,J’ I‚Æ8$AÅ…¶zìðN!‰Gò¹*sÅ-ù‡p…)¥‚IaDÄUÆ·ªØ\\Ì av\'Ói˜Që.Ê-~ý5C¯AaÏ=%g#eŠ¢pÎEQdè¤´ÊœÏm+-³ÈÞeÄˆ	ÄkBFOÙ“]µ38Xƒá\"\"§¬‘31§ì©åŽG[¾ºœSÑj·nÙòÁûï¹\\õê@Øíö]ÿ½{×®›o¹õö;îh|”Ûíþñ‡–/_æ® ÕjÙ·oï¾}{Û·o?ó‰\'»víZ÷Õy¼¿uË\"ºúšI÷Ï˜ADYYYÏÍzÆb±4×¼	WL|ø‘G\Zl¬6›KJK¼OÍæjATU%\"³Ùüýwß]6fŒ÷o-2\"Òä›q•ˆ	‚@“D&I¢FÃ$‰i4\Z¹õÕËÈ7WLE] ª¨‘[QÝœ3\"F\\%&ØdÕà\"$8\\ÌÏÀLz\npÙvíÌ¿û¨OßÑÄÆœnƒ«««ËJKív»(IÁAA¡aa‚ÐúŠ§j³¹¤¤ÄátJ’\Z\Zr¡aÎë¯›ÍæÙ/¾ØlTª®®ÎÉi6=7G„îÝ»ŸîQ\0ÿhVdYßÞ[!Ëœcçjý—g5£*¼^ò¨Y%Ûcî2[ÙÃ¿›?ÙÆ©‰É\r¨%.+L›Ûáøñãï¿÷®·c£Îùß\ZzÅën·ÛíoÌy=ñÀ.““3ûùöù!!!ÞGñ<Xýë/qqqWLœ¸uË_-¤\r\"Ú´qã½Ó§ëõzï–¼ñúk‡£¹CV®X¾rÅrïÓ€€€9o¼ŸÐÂUÎŒVŒ­ÍÅQò©0†$Í±Ürê×¡åcf—qÕI¤H¦0®ªD¤rntZ«e·A4Œ1â\\&‡ÙépÉî2‹\"‚ hTÅ !Ca~ÑOD-üD\nk½+…ˆÇ–¿þÚ¶mk^nž\'–y\r6ôÊ+¯\n\n\nj|ç|×ßoØðGff¦ªÔÅ(*2jôÅ½üòÓ\Zt«¨(¯¬¨|qökq\Ztç]wQjjêÇ~xêçôEñË…O÷(€\nœ{KÒ-Ÿ$V©jý)5õ6ˆª˜w¯Æjž1äÜÔÊ›¿®*’O±\r¼Ê¥Z]b ¡¹¾ýz±7m0&1¼gÏ^¹997môv]|¿dÉØ±—kj×J¨ªúÎÜ¹\rÒ†Á`\nv»ÝåeeÞ4›Í–•™Y7pL¸bâ’ÿ~ëyœœ”tÅÄ‰=zôlù-¨ªª(õÞïº5kZH™ÍæÏ>ûô?sß>õCN‘(°ŽÉùf&\nL™(’(‘$­:ñÈø>zM³9¯¤Òºñ@&qE4†\'®râ*‘Z™_©È²\"\Z™XépU:œ.E!Nó,ž%Eå2Wc%]ž~¡çWŸ°Öº(Nde½=wnQQ!qŠíØ©S`` ÃéÈ9‘žž¾bÙ²\r¿ÿ1ëÙg»Õï$°X,ó>ø ñÀ~â\ZÚ­W÷«Õš‘‘‘ž––žž¶níÚ§gÍêÐ¡•\\åe³X-ÕÕÝºvÕ6Š)¡µÿBÚµkwÃ76x•1¶fõ¯ÅEÅ½ûô<dHã3‹b³Ï\0gœcûË\\ó“ÌäZ?<4£aÂ8Y‚ƒ1n³(»7+ÅyÜ)2b§x7dN²Zíl!p”–•y‚ðøO^zÙež§;wúôã=ËËË³23»ÔŽü¶~ýîÝ»¼gˆ‰‰¹{ê=ƒ\ròü¾[TT´~Ýº_Yåt:ÃÂÂ\Z|ŒÝ|Ë-ååeëÖ®Õjµ—KDƒ‡yë?sOdŸ¨»#Ú¸aÃ±cÇšyS§}/è£GŽDEGŸî­\ZÕ%âp¡…	EEÒhÊê«ë¾1i`“‡¸õ©owº¹\"\Z¹Jœ8SUq)2/©RûõÑtëàQVf>pÐš’ê9På\\å¤¨Ü­ªNYÙóûŸâ÷?u¿ý–š—››ûÔ“OT›Íqññ?úXŸ>}ê¾š“ýÁûïNNzqö_|ù•w°Ãét>ÿïçŽ=ê0ýþûÇŒ[w:Nrrò¼÷ßOK=öÔ¿ÿÁ¼Øví\Z\\Ôår••–6Øh®6WW›¯»þú€€€ºÛõCpp°çqddäUW_ÝàÀãéé}8O„{îÖ¾ý©æ€³Î±ÕrÓc\"g€15ã°º;)*Âˆ{ÜÖÒ|‚‡~ä­7æ8Î©÷ÜãMDÔ³g¯º»ÉJMoŒËåüî»%Þí»ty}Î&ÓÉ‘‘‘wO™rÅÄ‰‡““\r\ZT÷%\"bŒ=øÐÃW_}ŸŸ_HhÍp@¯Þ½{õîÝ aGm.pxf§zÎöâË/Í®Ù·wÏO?þHDœóÔÔT_Ž«úÄ.ØžEŒÕü\'L4AA+äÚéù1ÝÂŒõ~¡Ï©°¾²jïÁÒ\n}ÑíäŠ‹sÅ“9H”«‡%˜®\\¸40¾ÞèOÕž½™¯ÿÇq\"—WU®pîæª[Q\r\Z%ã‹E]o¾AhfYç|Îk¯æççwìÔéÝ÷?h¼`¸}‡¯Í™3õî»rss¿ÿî»<àÙ¾xÑÂ½{ö¼óî{qññ\rŽêÝ»÷»ï¿ÿàŒ™™/¿ôÒç4˜’œ”ôè#7h‰çÁ=S§4˜ÿqÉ%—ÎyóMïSÏR©ºw›ùbþ|³Ù<dèP»Ý‘ššÚø]ÆÇ\'hµÚ&ÿ\0Î8Ç®ì`X{Âv¸ÄYsÓŒÆá£‰8Â›x¦¸Ý;ÿ ìÌ3ˆ\Z5§±µtW°=z,øj!ç¼Áxü‘Ã‡½%IŠõ<Þ»w_eEÍDT­VûÌ¬gD\nÈÈÈÈÈÈ&¯XRRÔÂÌÁVœO@Ô¹s—&çxDEEyUT”Ÿñ[bšÐ\'ú÷Ï/ôŒˆ8‘6$T¶V­Û}ø¯Ä×h?¬SLÑPítíÊ)Ú™QlµªZ£ ;kö¯ÍìÆº;øRh4@8dpïo¾<>ãaGz†J¤r®(Ü-ª.Qt—”îü;bô¨&Û¶oßÞC‡	‚0{ö‹Í•\'1\ZwO™úÁûï•”Ô,hªªªúù§Ÿˆèþhœ6<^˜=ûÞi÷9rx÷î]Õ_©ëYÈÝ¿ÿ€û˜ÑÂ_ÝÑ£G?üàƒwýý÷K/În¼óžÝ»§î¾«Éó|ûß%;ujáB\0>…Àç˜N`ïyzgÙGMtàDÞ;vñÚÿ¼x½GžQÕZ-oXI–êºiC„Ó\ZSà®Væ–6øíÐjµnÚ¸ñëÅ‹¼[úàí?˜˜èÝ>rÔ¨è:}EEEÅÅÅuß‰ÑhŒ‹‹¯Û¿bù²Å‹I’ôÌ³ÏvÑ©¿‹º\\îš%BË5?TU­©bâKO^Öõï•¹ö‹ª’_€4 ‡­S´hÔÈ¬üPe•¹@5[¹ÙF-©\nq\'óÜ˜#ÅÉÛ›ø¿§]Ý8mxHq¯½œ~ç4AQˆqU \r´\n×ÈBÕöfÇoë×Qß~ýºvëÖBûÇO˜0vìX]í´Ü;vØív£ÉÔ`¦pÝ{ôèÛ·ßÁƒ‰üöûEM•q:¥%\rVêªª¬j¼ÑÓYÒ¯_ÿ+¯º’ˆ-\\XTTôèÌ™FƒˆÒÒÒ–þüsïÞ}®™t\r}óÍ7y¹¹(†çœ{þ\Zá£Qas+W¦T×Þ ´ö5^û¿“7å\'_òD[µ¼î\'r¹\Zü<Õ°SšâÅ]§:Ã”ˆ\'\'¿þÚ«u—DFE=ôðÉU©ùyyÞÇœœ£°|Ù²¯/ª»Â£s—.oýg®·ûä×_~QUÕår­[»öÌ‡ëdà8–’Â„f?ovîØáM!!§´ ãDøéæ\\Ýë‰_Ž¨Šª%Çèö‡‡Wh$?•ë\n³Ë2iˆ‘(—.	$2gŒ«©Ä\"ryz/YoÐ·p}çN~ƒXwíåÄD\"1Aâ¢ÀÜ™MîÏ9?xð \r:¬åöK’TŠFuéÜ¹îÊ &\r6ôàÁÄäädÎyã…²))G_yùåþ­¶cÛÅ^}Í$\"ZúóÒ¢¢¢qãÆ{æylÝ²eéÏ?GÇÄx^ýeÕ/y¹¹-7À×8 MÐìùÁ}Cµoï*·9”Úûuq^5êìZ¯Ïƒ3ApïÛJ®†£!œH¯œ<Œ‘@5Ý!ÿÌ/ñ‡j°HÕl6oøã÷›o¹Õó‰RwyHÝ^ú;v4ND”ž–¶ß¾á#jªz÷ñÎÃ8Þ6¸Ýî—_zñT¡{–j2gbÏwoßu§A#¹?…+’ègM’ ˆn.1.\n¢$pQP%Æ%&ˆ÷Ü§M$9æàg½®©Õ«{ô°íÚç]1-c1Ku“;;ÎÒ’\"j×hRgË\n‰(22ªÕ=cbb‰¨¼¼Ìív7žE1|Äˆ—^~¥™åÝDDû÷ïîÙY§Ò¤´ÔTÏÈ]NîiWé\0ð5hC®‰3õÕÍÞ^’T`\'\"R‰Tª;ÎBuº<<r®\n{(\'ŽS£¢:WÍ½ÝlTj¦\\;U„Sw?jýã¡I\r~7|íµ6«u÷žÝùùž@d·Ù–ü÷¿ƒqòµ×RýQV»Â…ˆÆ^~yzzZ“1¢å2gÀn³î!ýú÷øg›Ñ@Làž«º­RIëVôSU®(\\Y#øùkõ’Ì%KL“.	\\H”DcynŸ¥‹L®SˆŒ2©\"cjÍ}x8\'ˆ‰Í(È²[Q\"Òê\ZF´ÔÔ_œÝø°ÿ¼ýNûöíviNáŽq\Z­–ˆdYn2kKI™óúk-tcTVT¶z	\"âœ?>ó±SÙàœ@à€¶¥½Ÿ´p|Ô×GÍ_ì-w*ªw$ÅÛçáU©;9CŒMà£Æ«;6Ô[*Ë˜Nà•”UN™nrxf”ÑqŠm®Ó4ýí Ëò×‹mß¶M¯×?òè£=zö\"\"£Ñ8múôiÓ§ŸÈÊš÷Áii5‹Ö¯[7iòdÆX\\\\ÜÞ½{<ÿþ{çk†ù¯˜8qØEz>`¾ýæëä¤¤ÚKý“S(\\.Wõi&˜€€€ûî»ÿlCcée[¶f|Æ˜Ä˜Ì˜Â™ÊI¸¬ªŠ**’ $“$ˆ\"ã’ J\"—4‚à¨Ôý¶>|ÛÖ.õ®Ò$&ÒðVnS~è ÞO\"›Âe•×Ž¬ij×”6 Õê4Z­,Ëæ*sƒ—\\.WNv¶÷)cÌ“<Y<}	NWëÅRmVé\r†éÄßßøˆŒ±ºô`b¢ÕjíÓ§¯€Ínþ#FŽly~‰§ysÞ|ËS«þprò‚/>oµa\0g´9cS{Žio|m[ÉþV¢:Ý¼ÎJ\"vèÂLÊ–µd·&Û«Ž•ºò‰Ï*LÏ™]dQÉí^iB3å§öíÝ»rÅ\nÏãùóçÏûð£º¯ÆÅÇO½çž?÷¬çiII±§Û|ð!Ë–-õlÜ¿oßÁÄÄ~ýû{ž{*øî–­™™»ÝóxÔèÑÓ¦ÝÛjœ1™L>½µJ•£ð÷Ô÷Uî˜**ã*gªJªÀT©\nWAY«\rðôªÍ^™š[µq—²;1V–£]0iüEÉôÍZ×mÓ´Í®¸©:”Tšx¨Cd ]Ü®¨NÅS£ÅÐµK“ûkµÚØ˜˜ôôô´FKI»uïþËê5Þ§ÕÕÕ·ßz‹·+\"¡cÂ¶m[Oœ8A­IKK\'¢ØØXoé-ÎyrR’,Ë·ßÞ°~nn®Õj½bâÄÆ7Ç9°tLLTT³½t}úôñ”ó~éÚh£âü5_NŒY•^=o[q¹Å]¯{Ã;ÈBµëY8‚ÂÙÄÛ”]¸Rþ(.=àVÝzŠbT¯;\\%ÕMv-5]ŽB06Ý7^÷æ)™)))\rnH‘™ur6¢ ž‘—ž½z%$tÌÌÌ \"ÎùÜÿ¼õâK/wkt\'‹ªª“$©õÎùSw8ùäzÝŽ;†…‡ÿƒ\'?3ûÜâ°h%AU®ªã¬¦–—ªM]ÃúGùuÖ‰¦Â¬â¤{ü´‚JŠƒª7„;Õ ÎŒ¢hEÙ–ÿükÞ{Shª¤„³¨øÀ“Ï1(øë¸È¸ä&IP2)ä?ªÙ›Ç><==}ëÖ­?úhÝi¡’$Õ-ÿêùâzÇEÃG|ûÍ7Ççää´oß¾¹“Ë²¼mëV\"\Z2d¨w#çüÉ\'·Z­ÍõöÜÿ4¹}ÊÔ{¦ßw_sG¥§¥a´YÐ¦Mîìiã§{Ê–&–ËnîíÛ¨³h¥v1Ù}ÐÑy\"˜ÕíªVá†¦Êj¸ÉÖ\\à`Æ¦k\"Å¶‹õ>æœ¿ùÆœ™3ïÛ¯Ÿ(Šn·{ÇŽíß~ów‡Ž;zºÍA˜vï½³_xÞóùd6›ŸõÌØË/;öò¸øxFSVV¶fõ¯Þ{¦QLl,>ÖhòŠGÝu¹NáF²¾VT}â`î6†8W9g’H\\àãefG·°‘Wõš¢×Ô”yêÓ®Ÿ7ÜzÛæ÷ß/^÷[¨Svr­ŽiDA#Š’È\\ïÉþpøS\Zúœ,ƒÆe¹lóŸGÞx»ìD~ÂàÁ¤\'QP%$™$&EFk{˜\Z›tÍ¤¿ÿ¾°°àÇ¾¿ýŽ;›Û­Á“¾}ûvîÜ9==ýóùó_Ÿ3§¹£V­ZYP/Iš«¯¹¦î©^ãMÏÐ#š÷Á¹¹9¯¼jÌØ1¼÷~^^îÃ>\ZýÊË/¹\\®é÷Ýï-_×l	QÌá€6Úº@­øÜÈˆz¾½¥pWF5÷†òF\rAq°Wü¤8rˆ‰¬CPxÔ ý¾\\]²Úx²†LÍu5‹‚Ó‹:vïÞ=%%Åó´¼¬ìÅÙ/˜üüÌUU\r~I½þ†“w¸è×¿ÿM7ßüã?Ô\\Z–[¿þ·õë%IÒh4‡£î<Áø„„.]šîóoÒ/«V®]³F–e³¹áÌ\"²ÛíGŽÖDI’ºu;÷÷Ý—½ÉéV9\'Î™§\nSùžÔê‘o¸¾ÿ÷7øûOœ=û°@ÊÒßµZOaRÆDFŒ8‘ãÈ±÷<¨é¯ïÑ™L®òòŠý‰•9ù—ÛÅyh‡p‘“DE’\\$	a÷Nmá^*íÚ·¿íö;–ü÷Û/,ðó÷Ÿ4yrƒ¾1\"r:«V®cµÃ\"‚ <öøOÎ|lË_~>þôûîk|GÙíÛ¶}öÉ\'¢(ÞrË-±u%clèÐšM7ä‡‡‡?òè£_-X Šbï^½ûôí{üøqÏü¡Ûn»­Á­Uc¢(z¯(Š‚F’Þœ;×ÓÃ‘œ”4ÿ³ÏÄÚ…Ð¢(ˆ¢ˆ*pn!pÀù¡Kˆî‹kã¶f[>ø«0­ÀæíÞàªÓ^±ÜUý·g!\'U#OŠv>ž/¾ÆYÖÉ“0\"™ÜŒXã‚`‚I+ø5}KOÆØãO<9ë™§++O.0›Í?é¯û×õC†­»åö;îTeéÏ?×Ý(ËrƒU*O>õtÝ«–ç[}¹`Aë\ZvïúÛ;Šß³W¯jŒž5ÉùûnÎ¹ç£œÛìêîÃ–¨°è{F?ÔÜ!Œ±.OÌÌÜ²KWj%Fœg\\­SÎy<Ó~<S%îVT›[v(²Ý-„úiì.2é#I\"“ãà!—Ži¹y÷NŸ^YYñç¦Mó?ùäÏM›<wË”¥ ?ÿ¾}üþ[ii©ÑhìÙ«WDDÍBžÎ|â‰Ï?›¿rù²´Ôc7Ý|K=Œ&“Ëå:‘•µnÝÚ\r¿ÿ®‘¤Q#GM¿¿‰Ù¸œóÍ›7øÁáaa¯¼þº§ª¬Ao0‚(Ñ&\n;\0\0 \0IDAT”)SrsrþÞ¹ãÕW^~ê™Yu¿Ž¢(šŒFCm	ƒÁ`4šzöìå9‰Ýf3z½ÁûªÉhlœ‡\0Î&8ŸŒîà7òŽN¿¦TÍßV’[ìtÛ÷ÙJàŠ\ZÍ\r÷w¹sÇ2¦ýškÕ”``*5]¿œ…Yó÷,‰ûÎ;ï½ónJÊÑ&wÐëõ·Þvû¿®¿¾ái»{ÊÔ=z~õå‚üüü&í?`ÀC?Ò`&`HppyY…†6Q†K’$A¥^N``€÷¦¢¶ª:ÝxãMÍ½¯³FåJvY®[©™cwð}G-’N×{¢$´ôSHgH¹k`Ï¶H\nãŒ×›·ÃjFØ•;eÕ¡(·â•~Ar……9ÝÌÏÀŒ:mL»ðûZhEñ™YÏöìÙë§Ÿ~ÌÈÈøô“Oˆˆ	ÂÉ‘;¢qq×ßpãåãÆÕýäž|íu1±±>ÿ<--mÎë¯‰¢h0\Z]N§gêŸÀäk\'ß|Ë­ïÔzèÐ¡Å‹OOOH˜9óñ„Ž=Û%­F«ÓyîM/JÒó³g·ä¿¿üòËƒÌ¸øâK¦Ý{oÍ˜(huº”cÇ>œ7ˆ*++5:í‚/¾ðÔù(--ÑêtéééžWKKKµ:]•ß\0Î8ÏŒMîtEõÝ³~þ=ÙêÖSã£œb\"ÜŠÊ8#ç4æ¾ŽtŸ’´’“›TR›¼#­ÛJ@ttÌÜwÞIL<ð×Ÿ¦¥¥U”—»ÝnN=pÐ ñ&„††5wìÐaÃ\Z´{×®íÛ·effZª«AëÞ³çèÑ£›ï˜ùÄ“‹-4MS¦ÞÓøÕÐÐÐ\'ž|ê·õë¼÷ŠÓiu7ßr‹÷SmÌØË÷íÛ—›“3ñª«úÐò[;ÜŠ\\esq\"•óäcV¦Qµ:ßê±Æn¿¼xé”íá:ó†Îjb‡§{Ã©(·buËqA—Ê«œÜ­2YÖFu¿ïIñÔ–1Æ®™4iÜøñ{vïN<p //ÏRmaŒÅ\'Ä\Z<¸wŸ>MVˆ2dh¿~ýwïÚµwÏž¼¼<«Õ¦Ój###{÷í3jôèæº—rsrÚÅÄÞ|Ë-ƒ©G¢\"£UEõ H’t×ÝS&L¸bóæMÉ‡’Ýn·\'pèuúØ˜vD”rä(øøf¤÷ž§î«~&?“ÿ?;+àtùüî	\0ÿ¸\nûÁ™ØœŒQb¦aÓÞÀìB=	uþ%«ì±›òã\"êtfH$d‘þc\nÚ£äÔ°þ’éŽa‘ßN=õ6(Š¢(Š§§átÛßd}ë›KvÝ¹èZ\"§^C%ÅN³Ýaôãz£úÈØÆumØ-ÔÀoû?þeÛ§bMÞ_ ‘®RÍØŠw0Åâv›nIÛ30KD:‘Œ’ÿä«C¦Þ+èZ©;\0gz8à<“WµnWÖLEuçÔ7Þ> “=»X³=) )Ãhµ‰D ÇG¸êEi™Ôvd{—‚r:´®8@õËéû7;ù¿I¢(6î$?EÿßÒi%A\\i-ÝTVá40I£ŠOÊßÙjàÈ·í	\n×:êò	æŽÙÚá!eˆ³“ƒ)6·ª×w4ù©Ä‰ú¾ý‚ïŸjèÝ»å3ÀÙ„Àç“\nû¿3Uy½—ŠB±¡î[Ç”ÝxIYN©¦°BÛ1ª™ânb=M‘Ëïu¥š?þËòÝµ¢¦§~ü¹_ÇqAcqaÝò«òf·(‘$©¢È‘ånÉ®8Ö!¸Ù\ZšÇŠwæZRý‚5Z½¢·ó\"“úsKh©x©µT‰ÎœyÂ]^)«J\\hhxT¸&®ƒað\0Ó¥£µN/>ÀY€Àç«3»AÚðRTbŒ:„»;„»©ùUL%\"Òv\nûøæ·¯³o:æØ*¶Ôõ9“\ZpêFt½áèf—MÖëII=so”ùÛž~ü²OÂýš(œ•QšòåÖ9‚(ù™)HÐU·“d¹ü©ý¸ÇâÂúçŠÃÁeYÔj™®éEF\0ÐF pÀù¤]Ðän‘-:ã30:9\"´¦«ú˜®êóO4í<¦(ÊºK;|GêT³™H¢À™ÀEF£*{Ñ\'[¦\rj7eXüÄˆ€šŠïUEk“V¯KþI+9Lå¢jd~zY£#ÅÍ\"ŒÝ;„õ!\"bL4|Ýr\0øG pÀy¦oÌ‹MÔ¡¼¹*oúl-cÂ™OÔß·wï‰YåååAãÆou6FfFÆöíÛ‚CBz÷î½nÍÚ˜ØØI“\'·z¡½{÷:xPs8÷Ý«SS«ªªRRRª*+OdŸèÛ§oDddBBB«²ÙlŠ¢üôã7Ý|cÌw7v!\"¤}`ô´7~˜-0ÆbŒÆÆD\\²%¥hþ¶ÔÅÇ¢NƒU®®°h5ŠIG&=SURURUQUÉÏ K:º¤ÇŒÆ…¹\0 C8ÿt	¿ïÒ.ß›t1gp¬Èš®_~*\"##E?á\n³Ù±víÚVy•——ççåNNþeÕªÔ´Ô½{÷äææ¶z¡œìlYv{\n¸çåå5¨·Ñ¤6h4š¨¨¨‹†]¤×ëWÿúk«‡¤¤¤|½xÑoë×§¥¦ý¶~ýª•+7mÜØêQÿ‹ëú_1º×hbÄy#“&2Ö=Z½kTÉ¤Áy]Ã+ü4‚ÓÅª¬Teå•VVa,+z\r\rˆ½=>ä\"Ÿ¶\0|=p^\n5\r×mÝ¡ü×2J—žÖ¢p†wCUU599ùÄ‰,®ªùG’õœ¸ªª-FÜ5e\níÙ½ûèÑ£3¼]»v-_Èív‹¢XPPHD*Wr²³;vêÔZó”£G‚HDŒQ“õÎ¨ªª¼lÌØîÝ»[,Õ7Üx£Ëåúãß[=êÁ{õ¦Wg.¹¯ÀzÌ³E`L`$1’Ò¤XŸÍ%,n–kv—Ze§ÌD‘t‰\"WU5Ü¯÷èŽ3|ÚH\0ð8_iÄ€AíßntÕþœÙgëÝZ)àÌ.\'ÂÅ—\\R\\\\Ti®êÞ½GVVæ]wÝÝêÔ‡.]»~>ÿ3—ËuË-·\ZFÙÝôŒ×ŠOH0›«,Õ–ËÆŒ‘$)0(°Õ£DQ?á\nÏÈc´jÕªS9ä×_VíÜ±ýÐ¡C‹~åv»£¢¢[=êä¯÷ÿ¶ùsÖ=•Z¶‡qqQP%kÒ\n\\\'2»[­°)*Wzf$FŒ¸Ê•Ç…Õñ1‘¡zÀy	Îo‘þ—Žëþ[JÑG©Å=Å9Z¦“BZÝ§97n0›Í“¯½®¨°0==Íér©ªÚòì\n»ÝÞ¹s—êêê?ÿÜ<`À€¼¼¼Ò²Ò¡C‡µpˆ(ŠcUfshHÈÁƒA6lXSõÍëQu÷®¿EQ\"\"bTZRÒêÛQåšI“»wï¾xÑ¢)S§ž…\0Càœk?[–øåú£‹sŒ‹ÞN‘$Æ+.‹‹Û®p&0FŒiÃ€v7÷ˆ¼ú,4\0|s8à¼\'	ÆÞÑ³Æw_tY«;ë¥Ö>º›®ÓévîØ^PPÐ¹K—o¾^Üê!I‡:Ž”••õêÕ{ûömž›y¶ ¸¨hÛ¶m¾øÀŒ1N—Ýê…ŒF£Ëér{¸Üžj´,À?`Ç¶mkÖ¬ÎÊÌ\\³fõêÕ¿\Z\rg8Þtº$AºyàŒ¹×.¿´Ë&­ŸÀT‘©’ jÕ¡ÈU·Å-;ÜŠ[Q$Á¿GääI½?BÚ\08ß¡´9\\PJ,Ûå¿Un=ÜÜc»ýb|f\'///+).éÚ­cL–åìììŽµ7ÜjAaaADD$ç\\E³ÙÐÊ˜ŽÃáÐh4žÁ\Z—Ëåt:Al5¦pÎíµ·‡%\"­VÛä]?\Zp¹\\ž:ëžŸºsQÊÂ¥8r+”U\'r5G/ÙªN‹[/ˆ!Æ„ØÀÞQþ½Äÿaa\0´p¡á¤æU®>Røa•ýxƒ—®é½[÷?tr\0\0À™Aà€çJ^ÕÚcÅ_”[“½ýõí¯è±å¶\n\0àÿ-¸À•Ywg–ýTT½Õ%WuœÞ+êÉsÝ\"\0€ÿ8\0Ú¹23ÛÞ!ÊïZEæÊZúÆû–ûtöí›Ÿœ!WÝºiëÁÊÞwNêóç­àŠÓZm®ª¨(/+)u„\rÑ-°©‰íª­¸Œ‡†›\Z5W¶”TŠaa”!h{°, ­qfþøÂÃËK)âê÷¾x¤W‹·\náŠÓáRlt@Äm¹GRÒª1–†;Ôç8¶bÁ²c|“8|à#=õžMG¿|éÓDk½_FÄ°KfþûÆ„ÚÕ/Îô%¯¸³¢åS{Ž»xæ¿oJ8¹jF.úkÑ×Û\nlN—ÓépØív»Ãn³Úl6«ÍY¿¤ªÐùŽ÷ß¾£»±a|pgýøô?TÄ]õò»ô¯yU)ÞøÖSïn)\n¾îÃ…3ºáFn\0mÀ¹\'[KËÌîšo9gÝ–R\"’¢´••uö4þ¡auú=xåŸ³n™{´ÁÙúÌ^öÎ¨VïŠ\"¬Ÿ÷áÆB™ÈYADT±fÎÓ\'bôŒHî3 <)5­AAõâN¶:ñBµf\'K³Â›+J°Ö‹%RX÷˜â¹K“š­Æt~Á!¡aaááa•UuT¿—Ãyü·\r…D¤émd¤8¬v™‘.®³IÙB¥¬8xÃÃ5É‰D½É ¡¿ -@à\08çäü•³¦Ó°Xª|è£‡§4Ø}ëüSê,e’DTÿ£[#ºKŽ=ápd–¹ˆH®È<zXãù_ôëÚÁÏóñ­Z³ì?T§>/MM*%\"\"?c§8\"\"ãà»îéNùñã_²IèÐ+²qqðI¯¿ù¯ð´OýÏng»Ûßyõriû«3¿Êû=ùÉã=+×¾ðìOŽ#\'ÌzEÜY¨êt:V¼éóoöZˆüF>ñæô!ÑARÁï‹ÖTõšpåÅ=Â´M%^µï‡ßJ‰¤þ“ûUíO²Úœ9g»Î–Í³oß\\û¤ï‹ËÞéÃ[ÒÀ)Cà\08±à1ï¬Óp«;sñôß×~Ð»Íî‰šÇÂÀW–¾y‘§¤‡&zÜ}·f~ñÓþ’àqÝ74ˆ‘ãèâw—çQðàÛ®ŠÛ³‡ˆüâ‡ö÷æyDdyÓðÐÆó)JÖ¼õäÑUé$¢Ü%OÝ³ÄSýC9øé¬™ZÅRÕt»¥ðÁWL\"’‹·¼÷ø^…]ùÜcã»X’¿}áÙ%)nú«4 ó‹“ãu‡3cÅW;íDA—_ë¿êåÖU´ø¤×žëY)\0P ÍèýüÏï\\Ðä\0€uçÓÿzùÐ©†i\"Âƒì.We•ˆÈ¤U,U™Qðž™FŒüÃ÷ûKJSmgNjï>´ç#\"bÑ£/é´¿f/¥dû²½.¢Ðq7ôóoªeŠ¥ªî É5Ý-öªJ{»{qËÑeo¾¸`¯™È0`Æœ{»;RÖ/øÏûŽœùÞ¬‰íšš†áÊþåƒs‰¤^×ö=²hn‘qä‹‹g¨Úöê”×vØ©ó´7®:þá¼?«G>ÿå‡ ”2@ÛÀÐf¤~ûêsk›þžT*Ž5Þh?øÖ]Ïln|_Ø‰ï.ú±7?ôŸ;žÞd	ÿú7Ov/_öÐÔ/²±Þ\'°sÙõ}¿š{èÄ†m×_Sðë63‘Ôïš¡¡BzÍî¼?–§p¢¸É“\Z,tÑÇ_vqìTgÍSî,ÎÌ³±°¸„\0O¯‚à×i`TÆªM,BËyÍíèkÈk_Ÿ9ogmR9þõŒÍ÷ÌP•:]÷ÂËwÄWYÔÚ±Ÿ:¬~›ª‘|xñÜÃDÄºÜqÏ°@ªÚµà£v¢€±S\'ö\n^©#*Üþñ¢ÄOôÃü\r€¶ Ípe\'hý¶)u¨Š»©Íž{Òr‡ÅADZ“–©ŠJD¢F¬ý\0vfüøöç{+ª2\"Êýå­Y[ŠÛˆHÉ_ùæ3k*S\"\"WÆêÍ¹DÒ»WhÃŸrUÆÑŒŒ†—æ¥\'2JkŸd¤\"\"ížY×wéQo­	·å{ûE4Zr{:CbÆ=óÊ”È¿?¼ïå]•]n÷?wö0ÕÏºÈ.á´óäd—˜½:VÎ\\þúë*‰´}o½:47)S×1œN”TýþéŠë?»3uÑÚ€6#bü´›º6½Öu|éë\nlÔ÷zø‹¯ïq—lxù©ÿf“iäsïMï¦#b’1TGJqa™LDþ‘þ¯\rµÇªUé{ÕŒzðòôÃå5‹*®ÝIÓþòkzü±øhùš¹‹ÇÌŸÑ»&483~|ûóÝE¶ÈˆˆÚ•³Ü]URá$\"¿°­µ¸üäpŠ)JJ^øæ—wÎº·¯·»AŠyçfŠven[½lc*ùõ¾á‰§î\Z­SŠ3Œ*‘’¶äù·cæ¿pyDÝŸQRûÞûñÊ¢õ/>¶(U¥èëŸ¹­‹’8÷é/¹ˆˆ\\‡>|F½s–}½gÒ‹#ÑÉÐ& p\0´C®¸ºÙ9[\Z¦\rŒˆò/=¸?‡ˆü]xbÇšýínšq…†‘½(¥ˆˆ´‘íEâ²S&\"Q\'ÕSh;ÞøÜ£M×ýsûú¥¹DÌØõæ—ž=~ïë[‹V~ôËÕŸÜÒ^\"ª	+ÉÞL!h\rFƒN#‘S&²”×?—5ûð¡lr—Ö[J#E\rñÓ—Ÿ´«‡\rø×“wOê£/ËÙ±v{NÎ‰e‘*²î|÷µå]ß½©Cµ1Lr\'-xyQªJ8x”~ó{/©ý;‡U×¾«\0ÑeV(pä-ýÿ°¥rÇ÷›‰£\0m@›qìë—ŸùµéOGµ2¥écäüÍ+Žp¢ÀQ×t³­xàçv?çðQ³éŠZˆ(¶;‘ì¶¹‰H£÷~Ç‹	Ñ+?ßQ®4<£:òÎáQ\"å*DDBððiwtÚ:ÿxÖú9×{–äê{>øù·÷ëBîêÅ«OØe¹é©%5‰B’Ð«_h½ÛfµZ­VÅ_{ÏÕñau‚”\\°ú…>:`\'\"\nˆÖe¯{wæò&ßtêWo-öÁ­qÚÚ¿„Ý=ñúÆ2\"\"ªÚûó’½D:ç„…«Ö˜Rß¿ãé\r–°K®ŽØðÝ®owÑõT–Ž¾kT8Ò@ÀÐf¸sjX£eÜzxéÒL\"Š¾òÚÑ±~×Fïø¾`ëÏ{îïÓyç–|\"\nØ;T$rÙÍv\"Òšt\'?sÚž½\ZO9¥À¨k‡Õy*…õ\ZEÇç;(ACÄ­YÛW,ýsÏþ¤\\Kã£å™û7gž|®3ÝÛ¡}½\"É?ÂXÛµb.,oT’Uà$\n¿ü¾©£:ÅDDµÉýbÆó¿Wÿï§Ž{s|˜@DÄ$2×´—tè3hÈE5¬W˜^É=rÌBÄ¢ºFˆ<WíxûÜ¹Œa4 í@à\0h3\"¯¸ï–nMÏáp¦ÿ4M£\ZZÎÌŸ¬«$Òö¹²gÙ¶›v\'V‘|pÍŽÝ~Í!¢°Q—vÐ‘b)·‘>@ßè#Ø4dÊƒc£D\"\"¹pÃ§‹÷ž¬ZyüXž%!Aã²¹‰ˆ„š•&LR2þÜ’ä­€}Ý¬4ZR\"møàÃÍMÖÉ0\r|lþÂûYõŽO^ú^˜öñìñ~‰/ßüüNE¿¸Sõ·o¼ùwä½oÝï°”ßÛÝþÐhoùæ×ïÞ?7Îßµs´¦,-é`â¾eóÙŸ›¹eCµÚÍÿ@Í¾öoL}\'7~øµÎ˜‹‰£\0m\0@›>hÜÄfçpln8xÕîßŸ \"r%}5;éäjÒ‡¯%%\\{uGÉUùœˆù…0˜â†\\|Yg-‘3-uqÝÀá:ðáŒë?4øIvE÷Š­Y«ï4aL—äŒÎÃ£ý°6\nV¼ýÊ¯+^pEn4\\SƒI–ß^û÷W¬D_~“<òÚ–[Ó~_öû	\'?ñÉSôØ‡Ÿ¼Ý®nqSgþÞI‰GŽýðÑQOU\"¢\0cb/ç²\"Š72Jª\rªËR^–QúgbÙ½\0m@›‘<wÊ\róšp5½`~‡ÆÑþãDºàØñºvïÙ·o»â-Ü]Ä‰(hüÔA…[6ç…û\'•‘Tpã^ÕY]UU¥õ\\ÃYï®\'Æö!¶œr»E&¢€QS®êP{´®Û}AdÝ9ëÇµyœ4}5AI±ZìMMJU«S~™7ç³­ÅD¤íq×+ô3±}ž—¿¾Óß+>ýÌO™ÖÝóžû<ê“‡žL`¢š÷ûOë²jž	Á‡Œ9¬cõ²yË-D†wŽ‹•<ÕJ¸ê¶–W«Ddn±\0àœ@à\0h;ÜVK“…5š!F]óÎÒ	\Z?£Æû™,ç¯YnáDD¡ãŸ¼·?-›þÖE5¯iºi¢|gé¯ÏÞñkÓç7\r~zîCÕ©YBTï‘C;759ýfsóÿ~iYš“ˆ(â²\'^½#4cï^%¤Èª#FÌ¿ïÔ·^­~xöº’âÕ¯½×{Á‹—…Õ\\XŠ{çÕ)Û¤Î}úöØ¯[´ŸP¹ýi¯u†>øÀð`æ”´‘»øçG§‹ì‚î\r€¶ Íˆ¿ëµGú›šìâ°\'}üü¢Fe¶˜dô¯ÿ=,†ôè— Ùs¼÷Œ7\Z¨µÜñ÷\rN’¢/~ð‹‚Nk%âì2à´ÞD+§4FE0‹óÈ\\ÙI›ÿóo|Y[íÌî\'‘4ô×¦ð«T[âú…£\'ÅÔ¼I!tÔ#/ŽªsºÀ×Ml·u¥ñ¶9³ÆEˆD†î7Ý<ðÄÒý…\"b=o¾{X0&Ž´\r\0m†_ûî½z53‡Ã|j·<eúŽ7¼:€=¢s˜–ˆü<úÙ¶xDÄ\rŸ|5½vÇç÷<¼¼´ÅÝ›ÐÌ-`äì%÷Mÿ&¯ÁV)jÂÌ\'uæ—Ó…\r¸¸OØÒ¤R±A7_ß­æ¶òº„=ÿ‚îHäØK;7®p~3öœòîW×bB=c:Ì¯÷o~}çé¾\08çMWþ\0\0\0ø§ ‡þyKå¼ÑÖó îŸ\0àkž¢$uÿ¬û .?¿SëM8Màsu£†—ç©ªª-\n\0ÿAX-Î¹÷5;\0þqà[Þlá¡ªjÝØÀpvxGÝäQ7œëÂ…|È“6¼!CUÕº9ç²,·z\0øßI’äIªªz2‡ ‚ 2œà+uÓ†Z‡¢(Þn÷é”\0€3¥Óé<!CEOÔðDÏcd88À\'êŽ¤xC†¢(žáp8EA«Õ¶z*\0øßY,Î¹F£Ñét’$‰¢(I\'þ#sÀY€À¾âíØð¤\rY–eY¶Ùlœóððð   5 Î‡ÃQYYYVV&‚Éd\"\"oæh°zÀP‡þyÕÕÕDäMŠ¢È²ìt:G```tt´gä\0Î>EQrrrl6›ÉdÒh4’$Õg!\"ÿsÝF¸0áç>øDÝY¢žÀáp8\"##ccc‘6\0Î!Qããã¬V«,ËžÈºëÕ|C*à\r‡Íf\r\r=×í\0\"¢ØØX—Ëe·Û=ªªbø\Z~×„^Ý…¯žÙœóèèèsÝ.\0¨Ák×®Ëåòzz¾[Ïu£à‡À¾â‰Š¢8Ž°°0Œ¤\0´)Z­600Ðét6(Çw®Û,|À?¯n!QOG@@À¹n\04èv»1‡Îð‰ÅËõzý¹n\04d4\ZëÖÿ%ÜO|	þyu+—»\\.Nw®[\0MÐh4žqOoýßsÝ\"¸!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0\0\0€Ï!p\0ü¿ÀÝ—ây¤(ê9n\0ü?$ë\0À?ˆÛ3þX¹­Ð]o£j+ÎË+st¸ñ‰›CSÿXµ>-ò_ÞØ\' Î¯ªùÈº_÷–)Í–™ºN˜|QDË?/TKÊúU»ËôF\röÏ:˜8|üÀpMk\rV«®ûeOeÀ€«¯êˆ_€\0.`\0wyÚ¡£¹\r6êcÛI”›ñóÜ7=Ï+·mÚ©½dX·àÚ\0ÜY|,ùhIó§5F]sQ+—fª%+ùhŽŽÇùÙ7í<!\'Û^ßÃO \"R-ÙÇ²ªd^gwÑ¯C×„@æ,NM>Z\Z}ùD€\ZÀ…„ùõœteâWëJÆ]{iä‰Õßý•«PäÈko	ßüÑGœDÚ˜ž=ÃìÇw­^|$ï¶‡o¨éç‚Þú@‚‹7>!·§­üzcQÃÍJÙþuÓ¬õËKˆÈ™›R,RIõþßñÂ+&_ÚAÇÜy›–,9V¿¥ÃMÏÏ`øß<\0´e\0nËÏ,vòê”ß¿9FbˆF\n\0\0 \0IDATÄ‰L½n¼û²vÁì_·f.ÞUîÊ?’˜ODRt÷Ža:Vs“Œ”öÝ»+\ZÏîPæ&.£Z²$Z›l‚%óð1Ï£êÌ)eW^ÚAGbØÀËÇÄ:­;we9µqCGtÒ—\\öÆËWT\"*Û4ï¥¿OsÄÈ±=pY+ã7\0p¾Á÷4À…ƒ»*rÕÈøðcI%*yºxÁæEóþ$\"R\'÷ÔÇÄ‹öj‡ª3xÆ1¸»º¬Òl9Í¶»òÞIñÚÚgrñ¶ÿ.=d»øÎzû{NËô¡FFDRhßKÇ‘R²ùð®¬’€.#/R*KÞPäM8\\Uj:@§ŒY­\0€·g¬ÿþ—,•ˆüã‡]:Ì´ûÇMEå¥¶“{D^ö¯Þ…n>Zž½÷÷ìôª©OîjpUUT»¹aÄ´Çš˜¥ÁíÇ~úb}¡j¯,+-Õ0p Î;ÕB0„Ç¶o¯¯}ªbüè´aíbÄ]—‹~ùØvÍLÌ\"ÇÎœ3–ä¢?>ø`SYÄ„\'½4œJþüð½ßŠ‰±¦€óÀ…Cè9aÂ¥Yú@û‰¬*gNŠ#²oŸˆºó,+?^¦K¸ä¦q†ü|Ó°ñ]MŒW\'/ygiN+gvýéÃ£D3yÖCÕf5gÓÿÝ+zžhÛlbDÖr«l«LÊ,ÈÊØÛçâÁí[œ¦ÁÕN\"ÒµŒˆ«ŠJD‚ˆÄpáAà\0¸€0CüÅâ]?®[uÐÑü^ú^}ïUýkž‰í»Ä‹nÕQ’[almHµäf—ÊDDRX‡v~‘ê(+S‚ÂMúè ©npä¤®},)C&vŒô£Ìê’²ŠìÃ¹œ(¢Gœ©åèÀÅ\"MP°^ ’U·BD‚$žñß\0\0´U\0(1aÒ´«:hëm“7-^zÄYw3v¹æžN•û¾·Tuj#/¹ýÚîúÂµï|´µŠˆ¸_Ÿë¦ðÏÝ¸ðËìŠð‘7Þvi{=#\"â²S&b	7<vsg%é›×’¨	êÊþ®.>´{{–LÖ·gH+ÑÁUt¬ˆˆ‚ÛKDÄe—BD¢FDÀà¥”$íØ–]ÿ[\\­Îw5þ4‚ú]51)sÅ±=K¾¼k˜µÐUsŽ¬5ó?;(åçV‹Œi¢­=TvXÝDZÿ àÀ@Iaœˆ´hŒíJY¥i;3‰(rpßÐò†ê¬¶¹‹¤9ˆü;u‰ˆ+.™ˆ$„ÀpÁAà\0¸PY2“Oyg)lÈM·fü}rÞÚ…ß{‰õìNGR*ós‰¤¸‰3¦ŽŽõ.£•«rËU\"ÿ?‘HuÙÜD¤ó×11¤[÷À?¶U‘ØéâÍä\rsêÆçË¬Šè]h%òïÝ?JCDÄÝv7‘ 3 p\0\\x8\0.8šØËï¼ÒúßµiöÀa·Þ:(¸fá«5eÙ×›Š)hÐÍ×$Ôiá²9\'yÏÎí;+œD¤I0ägV¡Ó•·÷3-ü~_9É\'¶¬ÝxÍ˜>Qzˆ”ªô”\n\"mdŒÖjqjÕ\"2…šD\"m ?£*Nÿ×Þ}Ç×tÿ\0Ý•›½±bE£ví¦öª¢U«5[UjÔªMµv‹¢(¥_”¢h\njÕ¦f¬ÄIDö¼Inî½ïß7“øu¨x=÷Aî¹çœÏçž“{óºŸÏç|®C•Æ®_ŠÉïSÜE+ñ{w	¼u\'$€ñNÀy@­×Ÿ» `ÚE¬yÃd0¤öÎ¶œr”(ïaà ÊsTú|%«×÷=põTÜÙ€Ä&Ý*8ª\0sÔ‘­\0\\+¾é[0û_ôÔ›Ì^rÂ:»—sÉ7›¼Ó²ŠìùnþÁ8@ëVù½î…6ü²íbÜƒk¿?üWÅVv¬—?úôñ0@[¢²Ë••Sþ\0¸w‰:¹iËæî	\0$]8pºb‘#«öDj1t@C{ãÍsWC,°q-ZÊÛ»ŒOI‡[[~Þup®Õº†³Ù\ZÆðë‘Èh7!¢<†ƒ(1ÞÚ²tCA\0ˆ1@êÅÕsg¹éU€Å+\0Ïý:÷’\n\0ô%Û÷}ßÛV_´vu€\0×\Z~ßªdsdþ÷“¶ha ±Ñ¨ ²-Z¯Û°²wÿ±ùÀuƒ}‘RùlRo<èË×-“ßñ^1ÇˆÐ]¡judË›Â¨\nøVP^¸¸yE €|å|Ü4*µO»~µ\nºÙj\0KB ÿŠŸ…˜\0†][	^ûõÊ ³ÎFf4N%K¸0på=Dyˆ¤Fß‹ŠÎñ•%’}\nrSBLtúÎ)\0Ðj2h|SZÀèãSðÈÑû©P»V¬]:ýšV•.Å–}ÊÕ\r‹Ò,¨ƒx5z¯QÊI}ãÒv6ÚæýÇ6\0XÎÞØ£ªÙú¿².©·þÞ¸qßåÛ2M;¿å©`[ XáŒ*¨lÝó9ê Î_·{ïæÅm%¦‚·ë­+±©fÀ¾X½Š>ö;f‰èÕ£ÉåûšˆþØØX“Éd6›SSS“““U*•ÏË®)ND zÒ)»L	‘Û|Î:Ž}Éôz½N§ÓjµZ­ÖÅÅåeWŠò&¶pÑóñÄa\0´Nù•«\ný÷p08)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀAyQJ,n\\À¹PÈË®	#Œ?§!:é)¶}î§Ò’„ÃsÚ=íË®\0Ñ3œ]Šega0À„ÄD$&\">±±ˆ‰FLæß³üX}\n]½²m˜Šo»âb%Œ_Çôeaû0û\'D´ÄÒs{A¤`~üeÀ¸e¨tŸŽGìCúôeðýLÖ -³ç#ÂœõPÚ}\\Œ†½æÁM¼:aN7èñìöÃìËxwú”yÄ°àÂ:,‹Å7ýà ÊXhÆ–‘øé6>šƒŽÅ±áSyÊRn­Á°µHË¹•RŽâTuœZâ:Äï†W+ØúáÐN”³yÆS™xOAVlP¡ú¸ý\"-9ŠÖÄ´(§Ï¶H° \ZÿŒš“q`<lÿßgŸðàéö|3kâó‰ˆÏ¶Ð¾–O€}n{0‡cô\0¦>ô€Í#Ï‘á8ZG¥ž˜ÙÚü²­zÂã¡ß¢W‘=o111aaa·oß\n\nzþeÄî“ÂävsÈ\'¥|¥~3éÒ[¾þM’³me8)Å (\"Ç\rY#6‹3%älòCÅˆH‚tv@6DKüN±ËµÐÚrÏ,\"’tL<Q«n^Ã%×ÒDDÌ²¬¦\0Òïè#Ÿ~âq)\rdà>ñï#eËJÙ²RÖG\\ €8ye,)+V‹éiî³–rfd.ÏTW]\Z¹ 5fH²Èá¾ˆk7‰Ï(åNeÔo®Yxz­rÜ<å˜!Ç32œ“Š@ò7®]sÞzÈÆ;9V~øtøDB·‹Í¥øI¬ˆˆ$ì•’.âä”usÔ=ò`üùlµ:#õ}¥Ý8‰4Ë¹H‘Á’” #¼\"åZÊ³žÂÇ;wîÜµk×îÞ½«\\Yôšc½š\\\Zâïƒ¸i„Þ„…=°.Ž-qr=|´øe Öêðùp´(Uöm‹»@™Þð¸›GÐl\0Ìˆp\rJÀI\0Úãð8<T¨}uÌn‡Ïþ@þŽXÒ: é:\r·2pÊÞ;é…o#j3æÈXR.‚‹\n\0B×áƒù9v›rà~f»ˆ ô4\0¬í³.Y«•ê…ŸûÂ\0àP«G ö,Ìï\r[OåØaÂd.p‰yöÎˆ§-¥ì@LßQh¼\0c-h9©U°{¼ÏÂ÷}œü\Z;:Áÿ\0x·œ2·}†S	\0°o…¿gáÔgè—yœkàØ\n8æ¼ßW`Ÿí¼H¾êŒ\0€ÈƒXóÐ½¾@‡‡Z\n|ˆ£a9‰F=²:µÅÞi£hø1R2ZR‡äœ›×œÁø`>òwÂÿêâýÁ0ÕÃŸ³à[:³ZØ7\r‡.¡Bw8©qä/\0¨è{GLÜ„\rá¿õ,8¶\n%lª1Ñ«„ƒ^Qjx×G©8Ì~ëÂ\0/¬[2i˜þ.Æì…¶\Z¾pzðO”ñ\nÆ¬l0ôM´(‰ ‡v™p	Ö5£`v£lkDZf€÷= Ó¡Ã\0è€¸8¼Õ\Zî*\\€âµ½Ù¥0 /Æ|\0&À2âï`ùBèU\0pñÁrM1ØqÕ\'ö2Že»ZY=*Ô\ZîÿÃ¦|¨µ=úàX »‚8ÀÉ…í\0@S{ÿ‹WùS–¢-\noW\0(à‹\Zè€T{\\ZõaÈ¨+bûH¬€ˆ5˜’„/Û@‹g9•V:W”óE¤s¶EŽ(ë[T&˜\0¸Á-ãÉK\"¾ïˆ—\0Nµï–€\nD,þ+nB]\r]}r)%â\0¾J‚„#Y]0	\'0y–•6\08·€Ap¬?ê,BÕù8ñyú‘¿ù=\0ØÃ›•¡TP§NVÞ2ÝÁ”\r€Ã?„M¶œ\0€•À¡6A‡º¸SìU¡WßËnb¡<èEt©ˆHäéX<½ºí(7PÞ*,€¸·Sñ­m”Å¢½dJC¤Ü$I¹·N (%þ\"Þ*ñ%‰[Äï·‡ZÂ{®”*¸Ëß‰\"ÙÔ<GßGz—ŠŸœ_.:ªË™¹5õçìRIØ\'.T“ãA$A—eJE¤ó:	\n’  Ù=&Ç&)áré¢\\¼(‡·I@¸˜îÉ[vhº/\"åÙ<[)\Z\n Ðˆ­F\0A\riëöøN¥§;•™]*Z)QJ\n¨²u©øIÄ5©b#nîH¾^Yg3ñ„”…@\'åÝÓj3NþüEZ@PXÖÝ}°”\\zÐ*É¯Sz.]*\"\"©2ÙG\0™t!k?Ö^¯á±O\\ ví$ûsº½è‰úà —?ãžñTþ¿Ø¥B/[8è•†Ÿ?Áo·Óïm™Ž-\0\0¿‰XØsÞÛ8Lm‚Ì…)0m/\0ÿŒ±\0òcÎ@hïã‹þHZMA£Z(¤ÅÁéXØ#Ê€S3Db·£hk$Ías?è˜2gƒ°þ<\Zúâ·ã€=Ú”ËY·ÌŸŒ4 þ@”²6ƒ—ÇúÙéjÃ~G¯%¹=#xûÀ€žv\0àê\0ÐyäXñò·¨:3ýç\"mQ9æê¨À‚›GxVwÆØ	}°pÊçhy\"ÿª\r´\0Ì€\'£V\0œýî¡bot.\0\ZdŒ™}ÊS™Å„[7\\¦óD¢8{°Ç°ÁYýb5±cŽ–EÇ‚ø¦-¾:‚mß`›õ1O¬:ŒŠ>¸+‡º8y¦Œ»æûèÖ]®céQ”:ŠŽCW›—¢¸{Vs…ñ:V_J ÷ÿt³¬‹òZ\\6=n=[^SH¯:zEéÐ¼7NÀÛÁGá¿ÑÞ˜µƒêaG,ý8û£˜P3½5^_~•qÖŒs Õ\\4Òà›XÄÛIøn!Ì@,¾…žQÀúî.8¼ÖK%v~†Ï½°¸ÚwÅ¸‰ømF¶ÇŸ±Ð7G-—œu³Á;±dMEÀ\\\0@Ö­|d—Jºƒ(h“^Û´4\0X\\Ë´\0`Îyá‡[U|Ðq×°ó<LQØ}øÁCÂÎ\",óŽbÍxÿ¦”Î;°Ä‚ÂM¨C«Ïá@°Öî¡ZŒm˜sGOy*3¹tEðjz-·e,º„Qc¡’a²ÁýŸñ}G¬‡ˆØu\nW‚pqFGðã,ÂðQIŒ«†Z5Pµ\njø¡qy¨ÃCÐl%²†ÖX»ièÛ\0*,\0Î Í›Ð©Ñx\r¶¿p~9‚\0$b|h÷·±`àcŽ³¾2.Yl*&TÀ××1à¼ù˜­ˆ^E/»‰…ò Ô¥bŽ“\r“¤¼^à\"Í”€+rx‡,›+#?‘¢ÖVèü²é^¶\rRdQÄ©ÜJ€9¹4\\«!€LLß\"í–ÔÓd[Á^]ã5©´,+€¼»Y,%dv©Ä$Ê°âHÛqOÜ¥ò¸Û¶/@¼\'H\\°ŒþP:vÌåÖcªÄ?ó5*ÏTÊƒ]*nÒª•´l)-[HE;$\riÙRZ¶”VåtÆ%$O{*ÃV\n .]%AÄ¿M¶.•‡n6Šˆ\\ù6ÇB]iÞEVŸ”˜›²âki]-Ç…\'lK?¡‡?—ünâæ&n.Ù¶Íü}p7·ô[ûßÅ\"b—öNOÖ?ÑÕ•`sŽCµ=£O+:]Î›“L»ô¯Îã£±K…^zþ^Dà0‡I[çô÷nO/±è\rÝÉF\0ql#ÁÖ¿…Fù­GúCÞÕÄÒt®ë%í* …›ˆUe]?¤ÕJ1‰ˆY¶$€x{ Ÿ¶@ì‹Y~m‘QP9šmDfàˆ9;Z\0)ÒXì (/óû îíe³¿,ì.MçHæ¥Žé£D¥?·/‹ÍKà˜(	WÄ÷QØ²/x&O[Jzà°×G\røÈ6(Á?î™N¥Èí…®“8\nË[E¥á—òã\"éRX\0~*½ˆþ­äÃ2w•œ¼-&£Ì©$ÐÉø³¿NIr~¿ü4K+)\"	’½[dñù¬“TÌŸ^J‹™&Ã« p•=dêBÙºO.Ý–T‹œ.€¨4{™5Cô@líD›QIÛŒ°bk\'šKh¶Àa‰“/Jü‡káÍw\"‰ƒ^v©Ð«Ií†z•ðOlã¡*­¶;\"Pá+üÖI¡(í†f¾8¹\rË1¾bw£çŠôm¯†«/Þ(Žiƒ±£\rþ¸ˆêqz7€ª#°c\0šV„\ZˆÜ‰Þ«€Rè’_‡£ñ4”lŒþ\r¡R¡õ(äßH Ö8Ô|øòÙdÄ¥Ábþ/ò£]OL_ˆëpOÁÐ•¸¾Ócb¥l[Cß®Ð«\0AÐ%\0ðnÀÿÓ“\n\rZÂ5c¤ƒé>üO<Û¡}¥tÞ†Yaðî†T á÷˜ù&Òn¡KgÐcæ4tJ‡rŽÀÓŸJ\0Q7\0N(S	×–ÙíŠ¿|÷ÐñKìþpB3o\0PÄ·SÐñS¬:‚Us®€%]±Ý.«Úºøy5Êè t\ZMÈz¨JGŒ„¾P³Ž¡É\\Œ‰+°ÃúKåƒçQ¸&\nD»RXxåÊBè:\"t=NtB“õðû\r[ò£ØÛ0¶Ãýß³†}\0€þÃ0çVÆÝòøû0\Zº©˜ßƒþFÁ>èRüYNÑÊËN<”½ .	Û)U4âÔJn$ËÊÚHÝÅ2¶Ž\0R{¢ì_\"ë/ˆõc¤%R¶”®dÞ¯pOÂ/ÉžóbŠ—îîÈÜR‚ªr=DÌ‘]ç%5IF–@:m”%5\rÑ¥&Ë¢–éŸ;55å\\RV}žø«ÃøôÖ‹Hƒ÷ÊZ^¬ÜÏø¼þoºTŠõ’í»¤¼Â-OXŠÙ ?T@ÔöYöÙ.f³ìè•µ¤ÜXI´<XÐ“ŸJÙÚJ\0©¾XLt©T–VîH—~¢ƒ¸uËzúOt½ätæ!N‘•²ú8¬y¨+;JAë¼^™ÏÑA>Y\'i\"\"}OÖ5¸ÊÖ?ÄâØQâSdtIdÂùô:<p•ŠXäôôôæO·Ê´7M%Ù óÞ@à!Û\"þÝYüÿ°…ƒ^Ž{¦W”	g¢Rsœ5ÃÞæŒ*Ô.x¯\\€c_aTZú¦í7iÐ{üŠâÔ/x×¾h97a}4ð\Zyfì5s¿@³¦8£Â¸Mè7Z#1Ûdæ(LmÏüxÃæÐ´®?4wµu¢ëÚ_`zÆ\0I•\rëÇéÒŸãÄâ‡æ«.õþð÷‡ÿŸ\\\Z\0ZÌ†¿?üýñsßG†»?£U3\\ BƒVxçô[ËZ¹¬òŠ¹ÃÝÅZ#äiF’>Q)ÿnp\n\0,è¬áýð]K\\]Š~€ÉÛPGƒÀ)´5k0æÓžJ¤`ßy\0(Wö¡KWÜ1}€_#\r>\Z™Ãyj#à®_GÐa´q€Úßâêu\\¿Žë×qy;Š°ƒmæ[¢ME[\0Ðh‘–Š„D„!%÷€4I_Ù±¾û }ü½›’Xü°‰\0€¤SXq(‰¹ÎR/8õ-\ZŽB*Pof·Æˆ-äóy¼_ƒ¶z|³­ò?á‰\"ú/c—\n½‚bN`ÄgXv\Z\0ÞˆeƒQ(ó[IÔ¨:G4¨=ÇÆ¢o9¬ê\0\rpgªÉÚƒg5´{ý hù%JfÌáhI†€#ÕpªŠEUTÄZ/j° h#úõÅþh ~Ý‡Á¨ß\07 z¶¬GÃÌ¿\n~ÙW\0@â~ë8°‡¶B\0\0×Waú›˜Ô%½oÂÖsæ#µÚ¶€€¡îÀu”¨ƒµ ®\0BÃ­aîß½R¤,»pOppûcŽ›9Á1\0sO{åÊãKQ¡^?T¹…êmÑñÔEñ¦H4aßdtŸˆ ÉŒl÷g òp,ø?tFòÓŸÊ´Ûð¿\0MË€äüþ”|5QÛ{’{˜Âaªþ&§²…“[Vã»ïpÞ\0›zø©?Ši¡×@Äi„#ÇDaY\\±ç.êÞC\\ÊXæ5A³º®ír®lFŒ\0vì\0¤âÇ!Þ‡\nú³„€àè84œ\nPÿkl{.ž<‘U’\'ŒW^	9Í(½ú^våAŠw©Dý)nØÉÀub0ÉÏµ³ZÅ[o³.€ ¤œH1…ÊU¥UO™½F.GJZˆt-$€èêJPª$Ÿ/ë EWDÝPîg¶ùdp¤aµŒÞör<&ýÁû;¤²&½5þ`|ŽA£V´ä;·”…‹\nˆë»œ–ÛÓ{‚ïR‘ÄSò¶«¸µ–â—ì¿+ááreUzû< ¶ïHöÉ¢îþ,NÖ‡êJˆù‘{þ—¥XÅïÇlO¼íI°R“l´ŽÞUÉ’;Ïr*¯~\'€ ‚Ìn\'ù‰+w\'ˆ6éwm=ÓwRéS	NãMiQ kÏÞåtœH’|ê)P‹“‹h €ý\"çÜÄI[[ÄÕS\nYÇ–’?f ÐI¡Ââé,°ö›dn’\"cK	leâxÑe§«+)\"’K—Jì1©[@>Z*ëfH—Æâ¡Î¨¤­t,o{fÝõë.+ÏÈ“´§Â.za8èùS~‡Q6|\'Ç#Óï]œ%ž6ˆý²+c¡$ËêYòOÔ#ö&«Û‹Æ[¶X/¶L”Y­3®p•a{²½³\'JOwdÖtñ,)c7IRÎñ	çåÃ2Rj¨$J.#-Tú×”J~2àkÙ~VR,\"	Z+\rßå×Q·\'\"bŠ“¨T1Z¯)%)\"\"æûÒÞS\0Ñ‘)Ç%{e-±Ò«€\02ö„<4ˆâ¹•b•8\\¤tùîpÎ/3Ê¦~âÓGb,Ïr*¯.—Š¶Raº\\ýQl­1±™Ü&.¨E\r©7Bî$ÉþÉâé°ZÒDÄ$¿¶8K£ž²æ1¦ÙÞCÜôCdüdKHÎç8²n™#Û-Gà0È€BWÙ!×K;q,)›BÓÌu‡É(b‘?;gDR_0W‚âED,)ràGiU6ý¡û}x&ôÂ¨Däe7²P^k2™Ìfsjjjrr²J¥òñÉí[*^.KÂ…¿f&³šÜ†=‰±¸ýÿ_vþpûG”î‡\Z3±Äc¾™]q‚T3ôÏÚ¥kN@° x¶oQ‘Ü½‹\r BñÂéS„ÅÞM1Ø«\0@L°hs›®ô¿!í–íBÕ&¨Q\"—J†ŸÃ–chÕ…Ÿ\'x@@€ƒƒƒ^¯×étZ­V«Õº¸¸<~3¢§ÇÀAÏß«8^C±Wp%>U²®k¥×½04JôÚpõAn¯½\0¼,–ˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠãÔæD¯¦#1=\0-¦¢ùÇ¯l‰Æ7o0úÕþw_`–‚ùýñ—ã–¡–“>ÁiÃƒ«¸7Ââ!Y_\'	˜Ú\'í0í”¸Žþc`ª	­‘_q#YK†#8eK¦WìÈDL?óDuñýS›ñCÑ«‚ƒèU$¸³[Ãí‹\'‚¿†`âZ`-6NÄ¯ãPð™_ø&Ùˆ­ñè±5-Ø³‡ZÅÑ†dÝÜ…)k ëO˜Â±i3â6cÕ(Ìù\r[7áN&•GµNÈÿVÀéÏëIÜmŽo8ˆ^D¯‚›k0éO˜2ïnœ€=“Ð­P¶õ´hóº”È¹±\nMçãûþû¾Bå3øk5*;þÛ*©œñ­?\"¬u2aÛüxÐáË/á¹R\ZV|ƒdàÃŽó!¼}b]nÆwË‘ÀŒ-ó\n”nñåÕ-bRµG}yz­û·õ\'¢‹ƒèUq+×ä²<dXœïó‡‡ M¾kP}šEø¼Ù\n\'v¢’ÝST a7Ê¶F¤i&\0xß:>ÙêB’±òcüx\0nÅèêPelu¾9TE£Ãè²ºBHôµQáœÞ\0¸€iñ€†µÉÑ×sý\'tƒ9×ªØ¡}•§¨9ý70p½:JR ²É\0\0VIDAT~Ž­¡ ØÿfÁo¾¬\0°`ÛçøáfŽõãáàŒä(]aåxGìP(òU@!=`FÐQ¸Õ†Ç“¼XbDZæ]ÒLH³ ù\ZF¼Î*Ü†9Í³rƒ)Ÿ‚8 F-ÌY\0CÆ`É@ó£SmÓpá`‡ZÜÍóç(-%wCQ\râ*<A…‰è¿…ýŸD¯÷JhÞ-Z ETÉ\0^µÓï¶hŽŠn9×6`Xyi€=‘KTxcNúcïäOÁèj(×\0Óžl„¦S3DâvÁ	\0Ð}3L&ô>ƒÊ>øáP\0³Žbn«­ñÇ±9\0Nþˆ©p~Ÿ•€ä8Zå]¡Çªð¾ŒÕWr÷ö/ÄŒe5`ò¥Œ%&)ýT‡ˆþØÂA”GYbp&÷baÒe[ªBÙ\0\0Aí’@\0VÌÃW«àüdû<ñ?$\0\0Vv„K?,˜—1è\"#jcDÆj¥Çáâ×pªŒŽ•€ÿ\0*Lœ‚ÖF‹pü±)cÕxlZ\0®ÐÛ\'« ”Kè;‘f@pý\0¬úg\\\0À¾:ú>Ý‘ ¢ÿ¢<ÊpW”Dé\\Çj¨à7\0.›³‡cÑÒõñ;4‡bæÆŒ;FÌÿ|J#-7ƒÀÕ±é#B¡ÖAèJã—?0º&v¾ãñi	ƒ@[;Æbtkücl1j%ÚGAße¥ÝÇú\rˆË¶äê^\\µþƒ^UŸô Ñ»Tˆ^§¾@±\"(REŠâ“Ã\0°ê]±.)†a§s¬|÷0b÷\Z(d“ûÞ\\ÞDK ?\0Ã5üº\n«¬·µ6æ\\[p|þJ…«=\0¼]ˆG¹Y80\rz@ë‡éo@ÕÆ\0 ÕC \rkzcF FÑPÚ³‚\0@—ù¯ã”\0‚éc¯^9¯š±¯…£çpîÎÆÄò\0ðA8¶\r°{iz·½RØÂAô\n1à^Î‰¶$¡±¹­iÁ‰í\0P¾	rmàˆ¿xtmˆµ[±{5›#ú/tíŸµÂ†hÍ–TO¢÷< ?º{aÞi|²ù—aVMŒ¬ŠTàÃ>ø£/`‹¶¥qflì¡®-À‡Ò+³ËpBt°DbüX\n¡jÎ€xŒ©ûÛð]Ë¬! \Z”Ï{Ò\0*|„¢;1û æÇ¸g8tDô’±…ƒèÕQu“‘œŒdVÔ€®»l]’ˆëzØYÿhÇã³\0Ð¢zî¯òs_£˜¾J€˜ýJÉm¥L)˜Ö\rAÀ[_¡’\0lÊaí,,}¿FÂ¥=Þ;ƒÉpm7U\0`ë5àY@Á\nè4+v#:\nÓê@¤ÂÖhTê\r·4 .V|X³Aå×Þ¨Suê N]|u\Z\0öÅn\0›†ñÇŸùÑËÂ¢WAõYˆ™­¬­5\0hõ°Í˜E¼ïÁ¬Ñ”‰çpÀ\0@£âÀù\\v˜\0š:(·7\\Ÿ‚êŸA>{Dñ¶4~Ãü^øýG\0°DaX[Ì½TÀ²Nø¢3 Â¸IP	\0ŽnPŽ\rp×Äâø~üµM>DrýŒ¹ËòaJLhÐz~oŠÆms\\á’vÇ®ç¨Âýó¸\0ˆ¼ˆ³> ¢W\rÑ«@cWÛÇ¯–)ÕoG\\nˆ\nö‹,Yóh™£°=\0\0*½…ÞQ¹.×ØY°967ŒˆM\0µzÂ¾µ3\0ÓºâPc*”ÆÊ»\0PÄ\r\0þ™ŠOãLpÖNÜ/ÂØµÅ©9(aÁh\0€Êí>x°¸\"MÐ¸ð#+ý¶=îÑQ^”¯1Ž†àlœTØ\0¸‚\núœ+Ù£MeÔÍ÷4û5§¨Q©öz£fSÜ\0\nwAËßà8æd\0¨Z\0Šû 0pD-?4mŽV­Pá6Š7Bœ?:\\bà êT„°¯‰?—¡@Æ¥!»±òß\"úaà Ê£ÔÎ¨æ\0¶¾ø¦/&®ÇÍlÃKóû çL´~ª´À’8\0\0nu1¨–zaû¸ø!p\Z\0jA×â\0àÑo£¨2çI\n—†À‹Y;¹r\0ìÜ³Mc\nÔ\Z‰~åY‹›«ñõž§¬9½d*yüZDO#66Öd2™ÍæÔÔÔääd•JåãÃN÷¼Ê‹š£Ï_]z½^§ÓiµZ­Vëââò²+Eyß\'ˆèß`Ú ¢\'Â·\n\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆžKj¢ÁdýÉ”fyÉ•!¢ÿíË®\0=WÆàíßÎ½PéÓ­Ë8d| °$^ÙöýÔ_S»ÌÐ¼`n/zóý}?­»]´^³&Õ\nëa¼p6¹LÍÚ›ë§ÿðö­A#ÚŠ9±ë‚mµº=mÕ€ÄŸZ4mõµÔû0ÅÝ¼|7±ò¤\r“½Ž.ž¾àxé±?Ojâ¡ÉZÃ±ÿûo·ÜM{TÕÕnu|Ù±¤Ís8\nDôŸÃÀA”—˜ïmŸúÍ¦ÆMûwï¹à›Ž>ö*@RCOüéájÊ¸a%J/ëí­p«Ôk¿ÍYº2Ôá^Ù†Õò‡®ú¤Ó¼‹Ž¾Y;¾@ÀÞ¿÷é=z©wåÛ‘ö&ztþßÆáí ©!Çþ:pé½8–÷ÕãÒÉIm[Yï‡­ùi½]Ï÷êÎHfÃÍÃ{ÜztíºïXò¹\"ú¯aà ÊK4…Þ½Ö~îÈq®˜9lIÅ\rC|õPÙùtõý½?]¸}ûõ®ƒ|mslc¾¿ëû5¡\0’þÛ¡¹VÒLŽÛ;y|Å‘®\0`ºç?å›½‰°«?¸‡¯\0¨Ýý¾²ã³ïoVûlt¯Rç¾½âR\ZJw=¥Ä²ÇþØ•óó+ÿÏ¦ï¸<}åÄôvm¡6S—WK–‡«-	Ç§YrCé£CD/QÞ¢²õjöåòRÞSç\\ñ\nÿª}Ëøô¿ï’\Zhc¶kïŸ¾¦mù?~Û*_ÄŽ©³N\0Æø˜h@ãóÉ¨¦NÛ£OŽJ4ê¨Û÷µùllK÷ê^^cØ¨\0KlÐéë‰:´pèa@\0·F_Íéí[X3nÚékƒ6†$î÷`ãS¿†—ƒ*£j6.86jÜï¡¦‡jmNŠPüÈÑKÅÀA”—ƒ·OŸq¼LŸÁ|óCÊÑ¡M—Ds<™í»kœÉù×7S\0x4èØ¬¸€Úró”á@èÒE\0r`ñr\0¸³°oÏÛKþüªšmrè¥«¦ÒUKÚ}Ëkž±-Øy9\0˜\rY8–+_X•hvp¶å°¤FÝ½­ØÓ\'¢ÿ.•H.í›DÿFll¬Éd2›Í©©©ÉÉÉ*•ÊÇÇçeWêµ`¾¿í‹÷\'Iì*÷˜>ýÓZ®Èx}NŽo;h¡êÔ?ø9¥/Siu:uZ°ÿô‘³¶\\‰ÏÜ‹ƒ—CÒ$§jm[•Ív*)7ý?çÜzÉŸ_UÓ‡où´Íä3\0ùª¾×»ƒëÆñËrö†”î=¶ñµÿýt Ä P‡y+GÕuI	‰L‹É˜f~ø=GŽLì;ÿª}ÃÉ?\r©h«Òç+\\ÐAóÐZ¤€€€\0½^¯Óé´Z­V«uqqyÙ•¢¼‰-Dy‡¦`›o7¸ÿ4æËåç¶ýÔ­N÷ŒUL:\r\0¨t6669.±)Úrüò7Zn]0vÆ®èÂïò¦úÔ¼Éw’Š¶è7¬ƒGFàˆÝuù÷cç¬wÔùýú~ç¬£gBÀ™ûIç%”nÚ¤Tö¡RŸ¼c_½çäÏœƒÜÞÿ¬®›Ú¹{DûÉSÃ	]\0(;jÛª÷=yÑ>QžÂÀA”§Ø¬ÛájïÕ\'Kt®ïþ„²U¶…«ÔôvÂ®h×òuý\Z¦†,pyQ¿Îk3ß Ìq7³ÖW;WíþyUÃ©qó¦ïL|ä^Õµš\r]8´Ez6+Õ®¢K±$Þ¾êâëãª9úÒù;F\0°ñªäë®,‰ÁwMžÅ]|\nòÒX¢<‡ƒ(ÏÑÙZB/ÜiVÒe÷°¾SÀ’m\0pzBûfvj\0ÐzuY°°W)›´»Û\\}66öâ=\0··Î›xì\0Ä„…2†{BŒè{ÕV¹ð‹Jv9–¯.<y¿!û\"•ó›ÃÔ¸·eX×Éfƒw™£8^™ÛþÃ_î÷¦cvÎñÇÏ?¸W¼Ë¤½*:ª@DyQ^c‰8¼öß/ìMóZ\\662::ÇD[É±ÑÉ\0\0µ“uNÐ´ð#6ìHo¨H:·ã÷ôŽ“òcþX‘½Kåã&cÎåR˜éÖîu«t9–™£s›ÆXS¨ÅÐ{ÎL=üû—cJÍîsÍZ¤ýqïºÀKQP—.W±¨=ÓQÄÀA”Ç˜B÷l¸\0hÊÕõ.ÓlåÑ.Ö¥†Æ´øl—¡Ú¬}KÞvÊ¾º­OŸ¹Û\ZÂ¶Ož¼-²h×©£jþœôôå¹=Ú.Ï¸iI{DqÑ§÷ìxâºÙxµ›<íÂG£ö\\žûù3\0ÀÓ¯ö¼Ø¼1hÙ¼Ë;pðQ^ÄÀA”·on]hª´©îú$-j§’Uj•L»uÁˆt.U¥fõ„@W \Z†¸s¶.•‡6´-ßïÛ!±#ç‹/øÞ´©ïJ¿ð5öà×C–Ý„ç;_«fŸ}}1F\\Øûûú5kýC\r\0œÊUs\n<\nçšCf6w0zKŒçVÎ]ZpÄÇM½™9ˆò¾¬‰òI8½â·`À¦æ{µÝžþå}kÃ˜ZôY ü˜?>|èàƒ‡>´}Ê®«¶/Z­m··Üû;þŠ*ì[±bÅŠË¹‡»\rÀ³Ñûo•Î’NO{·e¯qKü/Å£@µ÷Æ­ÙöÓÐF\0ºBMÇý²rX£‚@ì©_ÆvnüÞÈ_¯&?ë ¢ÿ(¶på!¦»[Ø87úðÍ´mýÞžz6ã·˜Lf\0§G4©­Íh·p¨;ëÙ\rœ\0@¥±Ñ0žM¨üVñ[gÌf89áþ;\r¶v’ÀÆÁF\r ùìÌO¿:g Éñ\0’öŽêÔ®½\n0Ç‡„Y\0DïÝéo\08T»dB-G‡\nï¿Sr×®Bm{}Ü³©ÝÚ®]Þši#\0­Nµ“o—ëí[2múÊâœ}«Ë9•ˆ^}Dy‡9*àØm\0EÞë^Í	\ZSM¦‡f7g-2¦Y2®=Ñm7qdì1×FíÞ.“´¶ÛßgÒç³/ßöí‡¶ÞJ²À¦Xã¾|ô\0,†«wCrŒEµÄ„†Äd»oŒ\n\rIÿÑ#Á\0zŸ~¿îýL«V0Ô­[zÝúëIPjÜ±FzKŒÊÖ«Ñ …õ;_\rÖ{—Êùm/D”p¦Qzþ8ÓèKdŠ8¾n›¹eºO:	½Þ8Ó(½0lá ÊS´ÞìÖëeW‚ˆè!üDDDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8¢×@êí½ÛOF˜¬wŒ¡\'^ˆ4>Áf¦Ð³¿ýýöãV5\'Ü½’,ÿ¶–D”—ñëé‰ò¾” Õ³—:~×¤FÀ±gúð))}¦|\\Þ\0T¶ž*zÙ«bw}ÜdÌ¹Œ-l\ZÌûkN]Ë5ÿ?öi>ðÁýYŒÉ)¦Ì|!‰g—\ry½çê…í<5Õ6v¶ZÃ¯\\NÊ\\Sgñcï±ÇLÙvå3bËªN…5 ¢¼Žƒ(ÏK¹¶ãª~|rÑò„ZGÏšu¾ûÿ·h?Ì‰·.Ýtè¾zÓ ²6®M¨o€ÔÀyÝ¾¸À|êº¹hó0\Z3\Z94:ÊpbL³Ï÷§<PÊìÞžu¯Ô Mkº‰þgÙ´%î‡„\'¹.â -ÐtòÜGlÒW1‡où´÷ñ®LD¯¢¼.éü†½æú“œN_iwbãQGOÖ·çŒ¡Õ#~Üÿ~‹¡ÝÊØ\0€Zoo\0ÐØêT\0`?s<,õÚ„Võ&dìÊ­ýò­c+°)þéÊµŸxÅ=zË½tÊÚÑ‹¢üíV5uÏ‚U	í?+4§Ã|\0Ðx¶ž¹þíÆµŸáµè×¾¥t\0Òîn›·ÖøÞà%lR¢¯Æº–) {á„ˆ^Žá ÊÛ,á{–îˆ´+è¬UAíÑlòÊu¿®ù¾Åõ‰í[·éµ6ß°¥ãåËýmÀuz÷Ý¢½×<~üøñ?àáì7oÓèÊ¶€Î«ÕÇí\\Îº\Z°hìw‡ñæçã:h®^4©O¯\"*Õp585ý¸[5Wën“¯î:ëàçWÔš+ÄpeÛæ±P–¤à«‰î¥óñSÑëƒ(/“¤³ËÎ4‘\Z}÷ÊÞÅ“§ýrNUÜ·rIã¡¿ž2õöwÀdˆ‰‰‰‰MH±,Ñ\'¶^0ºuÕk4\Zuê½k	®e=mÕ\0 ó¨ïÒOs×œ‰³\0cÌ­«Á±agN¦4Ÿ³fJã¸ŸúÜç\\³œ³\Z€$žßxÈ¦a=×¤˜˜ØD£¤Ý¿xOï]ÑC¤Ý¿éXÊÓVõ²Ž\r½PüpA”—%n9îîWÁx×z×xwïŽªª½Œª/dïÆ—Õ_=~(à\r¿Z0œ™ÚöÓ‰ÖýÂ÷®;c¬ÙÜª»WÚíc·lÊ}ìim¦°¤DDØ5õEåðÝk\rWçwjcW¦Y÷qëg×ñˆÚ4ð“ÀNCLŸ0iKE\n«Â÷ü´3âŽy@ç­šøxïÉþ³m/Ç¸Õ°OˆŒLŠ¼cï§Š‹Šsvs±åg¢¼Žƒ(/³cÈ‚Y‹zÏ±Þu¬Ô¹“ÙßŽé»”RºEíZÖi÷®µÁ\0hÝÛþ´yB;\0iÁ[g¿?æ½ØÅK~9×a¨ÝÞãÉ>ƒ¼m\0–˜S[vÛvaËo¥=uHKs,dº±sÞ-S#ƒÃìÛ-¬Z¿Ì¬é¡…=40œþqq G·>kæ{Ln÷?¤E\\J½±¨gëEéõ;ß½Õü\nün[€‰ƒ(cà ÊÓ´®E\\³†eª´š¤›wÌÞíÆ}ö}û›;ù¦ÛªúßÍëêõÐÈM]Ñw¾…´Û‰>šþcÉ²;*¯™>*CíRÞïOêjX»l>UüÝ›Á1É&¨4Z½½“{ÁÂ4@Åºn\0 .õÎ€‰oŸ³&c¯zïO×ÿ4«”´›Ë»ô8éÊö\r¢×\0ÑëD“¯Æï[Ø>¥ÛÐ€ÄÒÍzí—t5<Í×#×‹EtÅ?×eË‡ßíÈ×ai}÷ŒX õxómïc×’LÈgïRÌÇ¥XâÁï|_aéš~l\0KìåcAúÊ5K9ªÕ®UÛÔ=s2½y’rk×ª?BÓ2÷^´~¥X£ƒ§3ß‡ˆ^ü`Aô\Z±DìžÔkàÿ[Î~_,ß¾ezkíŽÌÛœc2QI\r¿œd\0KrtŒ@ZôÝ¨lë¤.?kwxæ`T1–ôù½R®¯ž4m[h¶Ù½,† Ë·Ì0ÇÝ86ËùëQQ!IÎÅÝye,Ñk€Ÿ,ˆ^öµ¦n­¥ÖÛtÚTõê…3ÿÝ=³ûø«ºêï}²ø÷6¾.jÀˆ1ìÄoßýoóïÇ4]WüòY‰°3OøÓ©ÇŒ	‘³¿þhpäÜé=«¸¤J1\'\\;þ÷þ[Z\0H>™–tóŸ¿÷‡è€Ô«÷RÅ#{Ñi··Î¥Iñtx£ßÌ·Õ\0$-1&.Ùd¸¼¸Ÿºtçü|\"z\rð…NôzP©mlÕ÷¶îñm Sé\noT¯Õ}ÖÐúê3¯JÕ:pHÜõû~÷v}oZÁ.pA>+¢ë}±üë.¾Žæòzuÿ±bY÷ËÇ¥m qñ®èqlÛŠeÛ–-æ¿r\0˜n&ÉÙ‹Ö—ï·hY[Û˜$ÛôQ 0Ý]óñKî\0ùë]PÙþÅ\"z©T\"üÆ%zÎbccM&“ÙlNMMMNNV©T>>>/»Rôx–Ôd‹6=‚¤…Ÿ9“T¶FIûô`I¼q.ÜãRŽìˆÍKôz½N§ÓjµZ­ÖÅÅåeWŠò&¶pQ:µÞ.[˜ÐyT­•ãQÇRU_t•ˆ(Ïàg\"\"\"R[8èùS©TjµÚb±hµZ½^Ÿ \"*§°&ú1\ZAAA...666ÖÎFÃ×))‡-ôü©²Q«Õ*•Ê`0¼ìJQ‰‰‰\ZÆú\neÎ €ƒ‘™6ÔjµV«‹‹{Ù5\"¢bccu:õEÊÌA/\0=ÙÛ6Ôjµ­­mtttZZÚã·$¢\"))É`0ØØØ¨Õjäl•|ÙU£<‹ƒ”bM\ZÆÚ=|÷î]^ƒMô_`6›ïÞ½kcccíRa½¤ˆì\ZÆÞÞ>%%%88˜™ƒèå2›Í·nÝ;;;­V«Îð²ëEyÉHÙZ9nÜ¸‘’’ò²kGôšJJJºzõªÑhtppÈlÞÈìUyÙµ£<Ž3Òó—˜˜(Ìf³Åb±X,&“Éd2%\'\'FgggWWW[[[½^ÿ²+KôZˆ‰‰‰‰‰±ŽÛ°³³³~°~ÈìR±fGGÎïFŠ`à çÏ\Z8\0ˆˆ%s†ÔÔÔ´´4‹ÅÂ_?¢C«Õêt:ë¸Ìž”.‹eà Eqâ/R„J¥²Nö•}<€Ì·9kÚ0™LÛ=ÖRö@Ú R)%{æ°¶d¨T*‹Åb}_³Fó²«IôZ°¶j #ô?ÐÂ´A/\0)(3s\0°ÆŽÌü‘ùïË®#ÑkÁ:myæPndkwdÚ ƒƒ”•™9¬?dŽí°þ`±X^v‰^jÒ`Ú †ƒg}GËŒÖŸ­ÿ²…ƒèÅx¸1ƒQƒ^0^¥BDDDŠãçK\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)ŽƒˆˆˆÇÀADDDŠcà \"\"\"Å1p‘â8ˆˆˆHqDDD¤8\"\"\"R)îÿ\0¾7¦ÿBØ§R\0\0\0\0IEND®B`‚','image/png','31.48KB','job_headers','NH7NGCC7441061F336TWI4FH'),('FFZJ67EJI5ATHHHG7939QBSX','u4.png','‰PNG\r\n\Z\n\0\0\0\rIHDR\0\0\0\0\0ƒ\0\0\0Ì8ù\0\0\0sRGB\0®Îé\0\0\0gAMA\0\0±üa\0\0\0	pHYs\0\0Ã\0\0ÃÇo¨d\0\0IDATx^íÜ;R$IÐbëLô—”8Ziœ\n	úË¡fmmµ¶‰(³]m†6xN¼HÉ-é§···_ÓƒŸ1Æôòòòàoùç @€\0 @€ÀIàëëkúüü|ø¿üôS6üþyøý @€\0 @àßïïïÓ_X @€\0 @€\0•Ê†JMY @€\0 @€€—\rî\0 @€\0 P+0ÿþ²ÇÅ—=Ö¢J#@€\0 @€\0©?_*éÏ(R·on @€\0 Ð$ lh‚K€\0 @€\0R”\r©›77 @€\0hP64ÁŠ%@€\0 @€\0©Ê†ÔÍ››\0 @€\04	(š`Å @€\0 @€\0TeCêæÍM€\0 @€\0š”\rM°b	 @€\0 @€@ªÀ<ÆXŽÇcêüæ&@€\0 @€\0\nN§ÓäeC!¨( @€\0 @`R6¸ @€\0 @€@­€—\rµžÒ @€\0 @€@¼€²!þ\n\0 @€\0 @€\0µÊ†ZOi @€\0 @ ^@Ù\0 @€\0 @€\0ZeC­§4 @€\0 / lˆ¿\0 @€\0 @€@­À<ÆXŽÇcmª4 @€\0 @ Ràt:M^6D®ÞÐ @€\0 @ O@ÙÐg+™\0 @€\0D\n(\"×nh @€\0 Ð\' lè³•L€\0 @€\0\"”\r‘k74 @€\0èP6ôÙJ&@€\0 @€\0‘Ê†Èµš\0 @€\0ô	(úl% @€\0 @€\0HeCäÚ\rM€\0 @€\0úæ1Ær8úNL€\0 @€\0ÄœÏçÉË†˜u”\0 @€\0¬# lXÇÙ) @€\0 @ F@Ù³jƒ @€\0 @€\0u”\rë8;…\0 @€\0Ä(bVmP @€\0 °Ž€²ag§ @€\0 @€\0eCÌª\rJ€\0 @€\0ÖP6¬ãì @€\0 #01–Ãá3°A	 @€\0 @€\0>óù<yÙÐç+™\0 @€\0D\n(\"×nh @€\0 Ð\' lè³•L€\0 @€\0\"”\r‘k74 @€\0èP6ôÙJ&@€\0 @€\0‘Ê†Èµš\0 @€\0ô	(úl% @€\0 @€\0HeCäÚ\rM€\0 @€\0úæ1Æ²ßïûNL€\0 @€\0Ä\\.—ÉË†˜u”\0 @€\0¬# lXÇÙ) @€\0 @ F@Ù³jƒ @€\0 @€\0u”\rë8;…\0 @€\0Ä(bVmP @€\0 °Ž€²ag§ @€\0 @€\0eCÌª\rJ€\0 @€\0ÖP6¬ãì @€\0 #01–ý~3°A	 @€\0 @€\0>Ëå2yÙÐç+™\0 @€\0D\n(\"×nh @€\0 Ð\' lè³•L€\0 @€\0\"”\r‘k74 @€\0èP6ôÙJ&@€\0 @€\0‘Ê†Èµš\0 @€\0ô	(úl% @€\0 @€\0HeCäÚ\rM€\0 @€\0ú”\r}¶’	 @€\0 @€@¤À<ÆXv»]äð†&@€\0 @€\0j®×ëäeC­©4 @€\0 / lˆ¿\0 @€\0 @€@­€²¡ÖS\Z @€\0ˆP6Ä_\0 @€\0 @ V@ÙPë)\0 @€\0Ä(â¯\0\0 @€\0 P+ l¨õ”F€\0 @€\0â”\rñW\0\0 @€\0¨˜ÇËn·«M•F€\0 @€\0D\n\\¯×ÉË†ÈÕš\0 @€\0ô	(úl% @€\0 @€\0HeCäÚ\rM€\0 @€\0ú”\r}¶’	 @€\0 @€@¤€²!rí†&@€\0 @€\0}Ê†>[É @€\0 @ R@Ù¹vC @€\0 @€\0>eCŸ­d @€\0 )01–ív9¼¡	 @€\0 @€\0ZÛí6yÙPk*\0 @€\0Ä(â¯\0\0 @€\0 P+ l¨õ”F€\0 @€\0â”\rñW\0\0 @€\0¨P6ÔzJ#@€\0 @€\0ñÊ†ø+\0€\0 @€\0Ô\n(j=¥ @€\0 @€\0xeCü\0@€\0 @€\0j”\rµžÒ @€\0 @€@¼À<ÆX¶Ûm<\0 @€\0 @àÏn·ÛäeÃŸ;J @€\0 @€\0þ! lp @€\0 @€\0ReC)§0 @€\0 @@Ùà @€\0 @€\0¥Ê†RNa @€\0 @€€²Á @€\0 @€\0J”\r¥œÂ @€\0 @€\0eƒ;@€\0 @€\0”\nÌcŒe³Ù”†\n#@€\0 @€\02î÷ûäeCæîMM€\0 @€\0Ú”\rm´‚	 @€\0 @€@¦€²!sï¦&@€\0 @€\0mÊ†6ZÁ @€\0 @ S@Ù¹wS @€\0 @€\06eC­` @€\0 ) lÈÜ»©	 @€\0 @€@›€²¡V0 @€\0È˜ÇËf³ÉœÞÔ @€\0 @€@©Àý~Ÿ¼l(%F€\0 @€\0(Ü @€\0 @ T@ÙPÊ)Œ\0 @€\0P6¸ @€\0 @€@©€²¡”S @€\0  lp @€\0 @€\0ReC)§0 @€\0 @@Ùà @€\0 @€\0¥ócy~~.\rF€\0 @€\0d\n|O^6dîÞÔ @€\0 @ M@ÙÐF+˜\0 @€\0d\n(2÷nj @€\0 Ð& lh£L€\0 @€\02žÞÞÞ~=:úëëëôóãC€\0 @€\0ü>>>¦ŸŸG?åÛRRRw@\0\0\0\0IEND®B`‚','image/png','3.13KB','job_headers','EGI1BF81XF2Q34AFV9XX1PVK'),('ST12PBWTETRJAHZ7MCSECN68','u4.png','‰PNG\r\n\Z\n\0\0\0\rIHDR\0\0\0\0\0ƒ\0\0\0Ì8ù\0\0\0sRGB\0®Îé\0\0\0gAMA\0\0±üa\0\0\0	pHYs\0\0Ã\0\0ÃÇo¨d\0\0IDATx^íÜ;R$IÐbëLô—”8Ziœ\n	úË¡fmmµ¶‰(³]m†6xN¼HÉ-é§···_ÓƒŸ1Æôòòòàoùç @€\0 @€ÀIàëëkúüü|ø¿üôS6üþyøý @€\0 @àßïïïÓ_X @€\0 @€\0•Ê†JMY @€\0 @€€—\rî\0 @€\0 P+0ÿþ²ÇÅ—=Ö¢J#@€\0 @€\0©?_*éÏ(R·on @€\0 Ð$ lh‚K€\0 @€\0R”\r©›77 @€\0hP64ÁŠ%@€\0 @€\0©Ê†ÔÍ››\0 @€\04	(š`Å @€\0 @€\0TeCêæÍM€\0 @€\0š”\rM°b	 @€\0 @€@ªÀ<ÆXŽÇcêüæ&@€\0 @€\0\nN§ÓäeC!¨( @€\0 @`R6¸ @€\0 @€@­€—\rµžÒ @€\0 @€@¼€²!þ\n\0 @€\0 @€\0µÊ†ZOi @€\0 @ ^@Ù\0 @€\0 @€\0ZeC­§4 @€\0 / lˆ¿\0 @€\0 @€@­À<ÆXŽÇcmª4 @€\0 @ Ràt:M^6D®ÞÐ @€\0 @ O@ÙÐg+™\0 @€\0D\n(\"×nh @€\0 Ð\' lè³•L€\0 @€\0\"”\r‘k74 @€\0èP6ôÙJ&@€\0 @€\0‘Ê†Èµš\0 @€\0ô	(úl% @€\0 @€\0HeCäÚ\rM€\0 @€\0úæ1Ær8úNL€\0 @€\0ÄœÏçÉË†˜u”\0 @€\0¬# lXÇÙ) @€\0 @ F@Ù³jƒ @€\0 @€\0u”\rë8;…\0 @€\0Ä(bVmP @€\0 °Ž€²ag§ @€\0 @€\0eCÌª\rJ€\0 @€\0ÖP6¬ãì @€\0 #01–Ãá3°A	 @€\0 @€\0>óù<yÙÐç+™\0 @€\0D\n(\"×nh @€\0 Ð\' lè³•L€\0 @€\0\"”\r‘k74 @€\0èP6ôÙJ&@€\0 @€\0‘Ê†Èµš\0 @€\0ô	(úl% @€\0 @€\0HeCäÚ\rM€\0 @€\0úæ1Æ²ßïûNL€\0 @€\0Ä\\.—ÉË†˜u”\0 @€\0¬# lXÇÙ) @€\0 @ F@Ù³jƒ @€\0 @€\0u”\rë8;…\0 @€\0Ä(bVmP @€\0 °Ž€²ag§ @€\0 @€\0eCÌª\rJ€\0 @€\0ÖP6¬ãì @€\0 #01–ý~3°A	 @€\0 @€\0>Ëå2yÙÐç+™\0 @€\0D\n(\"×nh @€\0 Ð\' lè³•L€\0 @€\0\"”\r‘k74 @€\0èP6ôÙJ&@€\0 @€\0‘Ê†Èµš\0 @€\0ô	(úl% @€\0 @€\0HeCäÚ\rM€\0 @€\0ú”\r}¶’	 @€\0 @€@¤À<ÆXv»]äð†&@€\0 @€\0j®×ëäeC­©4 @€\0 / lˆ¿\0 @€\0 @€@­€²¡ÖS\Z @€\0ˆP6Ä_\0 @€\0 @ V@ÙPë)\0 @€\0Ä(â¯\0\0 @€\0 P+ l¨õ”F€\0 @€\0â”\rñW\0\0 @€\0¨˜ÇËn·«M•F€\0 @€\0D\n\\¯×ÉË†ÈÕš\0 @€\0ô	(úl% @€\0 @€\0HeCäÚ\rM€\0 @€\0ú”\r}¶’	 @€\0 @€@¤€²!rí†&@€\0 @€\0}Ê†>[É @€\0 @ R@Ù¹vC @€\0 @€\0>eCŸ­d @€\0 )01–ív9¼¡	 @€\0 @€\0ZÛí6yÙPk*\0 @€\0Ä(â¯\0\0 @€\0 P+ l¨õ”F€\0 @€\0â”\rñW\0\0 @€\0¨P6ÔzJ#@€\0 @€\0ñÊ†ø+\0€\0 @€\0Ô\n(j=¥ @€\0 @€\0xeCü\0@€\0 @€\0j”\rµžÒ @€\0 @€@¼À<ÆX¶Ûm<\0 @€\0 @àÏn·ÛäeÃŸ;J @€\0 @€\0þ! lp @€\0 @€\0ReC)§0 @€\0 @@Ùà @€\0 @€\0¥Ê†RNa @€\0 @€€²Á @€\0 @€\0J”\r¥œÂ @€\0 @€\0eƒ;@€\0 @€\0”\nÌcŒe³Ù”†\n#@€\0 @€\02î÷ûäeCæîMM€\0 @€\0Ú”\rm´‚	 @€\0 @€@¦€²!sï¦&@€\0 @€\0mÊ†6ZÁ @€\0 @ S@Ù¹wS @€\0 @€\06eC­` @€\0 ) lÈÜ»©	 @€\0 @€@›€²¡V0 @€\0È˜ÇËf³ÉœÞÔ @€\0 @€@©Àý~Ÿ¼l(%F€\0 @€\0(Ü @€\0 @ T@ÙPÊ)Œ\0 @€\0P6¸ @€\0 @€@©€²¡”S @€\0  lp @€\0 @€\0ReC)§0 @€\0 @@Ùà @€\0 @€\0¥ócy~~.\rF€\0 @€\0d\n|O^6dîÞÔ @€\0 @ M@ÙÐF+˜\0 @€\0d\n(2÷nj @€\0 Ð& lh£L€\0 @€\02žÞÞÞ~=:úëëëôóãC€\0 @€\0ü>>>¦ŸŸG?åÛRRRw@\0\0\0\0IEND®B`‚','image/png','3.13KB','job_headers','EGI1BF81XF2Q34AFV9XX1PVK'),('T0ZEHQO1KHFG2X77J2304PQV','u8.png','‰PNG\r\n\Z\n\0\0\0\rIHDR\0\0\0—\0\0\0)\0\0\08_»|\0\0\0sRGB\0®Îé\0\0\0gAMA\0\0±üa\0\0\0	pHYs\0\0Ã\0\0ÃÇo¨d\0\0WIDATx^íÜA\nƒ@…áiÏ¥+/¥7ðR®ô^»r–á„R^aòOò^V}LÓ´—ÓÓ÷}éºîü•Ï\\Ø¶­¬ëú¥{Í5Žãå	ˆ˜ç¹<#? E B@sEhÑ†h®.âwsíûîÅàÖ8úŠsEF‘6D€sq¬[ë“‚œ+4‡ÄQb1JŒ¾š€X‹b±z\\›! ›¹Š|‹bQ,æ›ëüq.ÎÅ¹òÏy¾\n-ôùî´™Š4W3W‘ï v.;—+ß\\ç¯ˆsq.Î•ÎóUh¡Ïw§ÍT¤¹š¹Š|±sÙ¹ì\\ùæ:Eœ‹sq®üsž¯B}¾;m¦\"±(Åb3ãè ÕÄb5*Â(±(ÅbtjèÿO€sq.Îõÿ9t‚(}”}5ÍUŠ0JÀÎeçúÙÎõþ«ðsGÃPŽ×ƒ@„À²,åxÏÏR¬´%ámõ\0\0\0\0IEND®B`‚','image/png','450B','job_headers','XSF060BUDZNTYVAU1VALQ89S');

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(100) NOT NULL,
  `user_name` varchar(200) NOT NULL,
  `status` varchar(10) NOT NULL,
  `password` varchar(50) NOT NULL,
  `create_date` date NOT NULL,
  `create_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_code` (`user_code`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`user_code`,`user_name`,`status`,`password`,`create_date`,`create_by`,`last_update_date`,`last_update_by`,`role_id`) values (2,'ADMIN','ç®¡ç†å‘˜','Y','admin','2011-11-26',1,'2012-09-02',2,3),(4,'07420207','å°æ¨123','Y','123','2012-04-11',2,'2014-09-30',2,6),(5,'07420205','å°æ¯›','Y','123456','2012-04-12',2,'2012-05-08',2,6),(6,'07420206','å°å¾','Y','123456','2012-05-06',2,'2012-05-23',2,6),(7,'07420101','å°æ˜Ž','Y','123456','2012-05-22',2,'2012-08-23',2,6),(8,'07420102','å°çŽ‹','Y','123456','2012-07-07',2,'2012-08-23',2,6),(9,'10010','æ¨æ°¸','Y','123456','2012-07-07',2,'2012-09-02',2,5),(10,'07420103','å°å†›','Y','123456','2012-07-07',2,'2012-08-23',2,6),(11,'10020','å°¹å°å†›','Y','123456','2012-07-27',2,'2012-09-02',2,5),(12,'07420221','å°é»„','Y','123456','2012-07-28',2,'2012-07-28',2,6),(13,'07420104','å°æµ·','Y','123456','2012-07-28',2,'2012-08-06',2,6),(14,'07420105','å°æœ±','Y','123456','2012-07-28',2,'2012-08-06',2,6),(15,'07420106','å°æŽ','Y','123456','2012-07-28',2,'2014-07-03',2,6),(16,'07420201','å°éƒ‘','Y','123456','2012-07-29',2,'2012-08-06',2,6),(17,'07420203','å¼ ä¸‰','Y','123456','2012-07-29',2,'2012-08-06',2,6),(18,'07420202','å°æ›¾','Y','123456','2012-07-29',2,'2012-08-06',2,6),(19,'07420204','å°ç‰›','Y','123456','2012-07-29',2,'2012-08-06',2,6),(21,'20000','æ•™åŠ¡å¤„è€å¸ˆ','Y','123456','2012-08-27',2,'2013-02-26',2,7),(22,'07400102','å°æ˜Ž','Y','123456','2012-08-30',21,'2015-01-07',2,6),(23,'07400103','å°èƒ¡','Y','huangjiong','2012-08-30',2,'2013-04-19',2,6),(24,'07400104','å°é©¬','Y','123456','2012-08-30',2,'2012-08-30',2,6),(25,'07440101','å°æ®µ','Y','123456','2012-09-22',21,'2012-09-22',21,6),(26,'07440201','å°æ¯›','Y','123456','2012-09-27',2,'2012-09-27',2,6),(27,'07420107','æ»¡1','Y','123','2014-07-03',2,'2014-07-03',2,5),(28,'123','123','Y','213','2014-07-03',2,'2014-07-03',2,NULL),(32,'10086','test_1234','Y','123456','2014-07-29',29,'2014-07-29',29,3);

/* Function  structure for function  `nvl` */

/*!50003 DROP FUNCTION IF EXISTS `nvl` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `nvl`(param1  VARCHAR(255),param2   VARCHAR(255)) RETURNS varchar(255) CHARSET utf8
BEGIN
  
 RETURN(IFNULL(param1,param2));
 END */$$
DELIMITER ;

/* Function  structure for function  `to_char` */

/*!50003 DROP FUNCTION IF EXISTS `to_char` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `to_char`(p_date DATETIME,p_format varchar(255)) RETURNS varchar(255) CHARSET utf8
BEGIN
    
    set p_format = UPPER(p_format);
    
    set p_format = REPLACE(p_format,'YYYY','%Y');
    
    SET p_format = REPLACE(p_format,'MM','%m');
    
    SET p_format = REPLACE(p_format,'DD','%d');
    
    SET p_format = REPLACE(p_format,'HH','%H');
    
    SET p_format = REPLACE(p_format,'MI','%i');
    
    SET p_format = REPLACE(p_format,'SS','%s');
    return date_format(p_date,p_format);
    END */$$
DELIMITER ;

/* Function  structure for function  `to_date` */

/*!50003 DROP FUNCTION IF EXISTS `to_date` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `to_date`(p_value varchar(255),p_format varchar(255)) RETURNS datetime
BEGIN
	SET p_format = UPPER(p_format);
    
	SET p_format = REPLACE(p_format,'YYYY','%Y');
    
	SET p_format = REPLACE(p_format,'MM','%m');
    
	SET p_format = REPLACE(p_format,'DD','%d');
    
	SET p_format = REPLACE(p_format,'HH','%H');
    
	SET p_format = REPLACE(p_format,'MI','%i');
    
	SET p_format = REPLACE(p_format,'SS','%s');
    
	return str_to_date(p_value, p_format); 
    END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
