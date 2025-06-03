/*
SQLyog Trial v13.1.9 (64 bit)
MySQL - 5.5.52 : Database - dbfound
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`dbfound` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `dbfound`;

/*Table structure for table `access_log` */

DROP TABLE IF EXISTS `access_log`;

CREATE TABLE `access_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` varchar(100) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `url` varchar(300) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `cost` int(11) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `ua` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;

/*Data for the table `access_log` */

insert  into `access_log`(`id`,`time`,`ip`,`url`,`status`,`cost`,`size`,`ua`) values 
(1,'2023-09-22 07:06:25','125.83.135.212','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-06/09ac2b0e50e935d54cde95809dc4f1eb.mp4',206,291,2335,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(2,'2023-09-22 07:06:32','125.83.135.212','2023-09-22 07:06:32',200,268,1260148,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(3,'2023-09-22 07:00:35','10.215.169.180','2023-09-22 07:00:35',200,272,1008407,'Dalvik/2.1.0 (Linux; U; Android 10; Redmi K30 MIUI/V12.0.5.0.QGHCNXM)'),
(4,'2023-09-22','10.215.169.180','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-04/ceb29e644bc5a35b5a67de1d3826641e.mp4',206,295,2336,'Dalvik/2.1.0 (Linux; U; Android 10; Redmi K30 MIUI/V12.0.5.0.QGHCNXM)'),
(5,'2023-09-22','125.83.135.212','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-06/09ac2b0e50e935d54cde95809dc4f1eb.mp4',206,291,2335,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(6,'07:06:32','125.83.135.212','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-06/09ac2b0e50e935d54cde95809dc4f1eb.mp4',206,286,1260154,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(7,'17:06:33','223.150.143.50','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-07-22/d17ff7ffea5da7111be28c18405a1d2c.mp4',200,264,743424,'Dalvik/2.1.0 (Linux; U; Android 6.0.1; vivo Y66 Build/MMB29M)'),
(8,'07:06:35','106.88.8.91','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-07-22/e2b54846339540fe2d8675dc3ff6d1e7.mp4',206,298,2336,'Dalvik/2.1.0 (Linux; U; Android 10; PDVM00 Build/QKQ1.200614.002)'),
(9,'128','106.88.8.91','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-07-22/e2b54846339540fe2d8675dc3ff6d1e7.mp4',206,293,532324,'Dalvik/2.1.0 (Linux; U; Android 10; PDVM00 Build/QKQ1.200614.002)'),
(10,'128.12','10.215.169.180','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-04/ceb29e644bc5a35b5a67de1d3826641e.mp4',206,290,1322015,'Dalvik/2.1.0 (Linux; U; Android 10; Redmi K30 MIUI/V12.0.5.0.QGHCNXM)'),
(11,'22/Sep/2023:07:06:39','125.83.135.212','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-06/09ac2b0e50e935d54cde95809dc4f1eb.mp4',200,268,1260148,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(12,'22/Sep/2023:07:06:39','125.83.135.212','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-06/09ac2b0e50e935d54cde95809dc4f1eb.mp4',206,291,2335,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(13,'22/Sep/2023:07:06:39','125.83.135.212','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-06/09ac2b0e50e935d54cde95809dc4f1eb.mp4',206,286,1260154,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(14,'22/Sep/2023:07:06:37','180.143.183.162','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-07-23/ed7c7c9ac42f85278634464acd9aec11.mp4',206,296,2320,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(15,'22/Sep/2023:07:06:37','180.143.183.162','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-07-23/ed7c7c9ac42f85278634464acd9aec11.mp4',206,292,995827,'Dalvik/2.1.0 (Linux; U; Android 10; V2036A Build/QP1A.190711.020)'),
(16,'22/Sep/2023:07:06:37','106.88.8.91','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-07-22/532d9615f2d264c9756494fc54f6bf88.mp4',200,268,522440,'Dalvik/2.1.0 (Linux; U; Android 10; PDVM00 Build/QKQ1.200614.002)'),
(17,'22/Sep/2023:07:06:42','175.9.162.70','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-05/db40efd1df520cf706984699ab546a26.mp4',200,267,478800,'Dalvik/2.1.0 (Linux; U; Android 10; PDVM00 Build/QKQ1.200614.002)'),
(18,'22/Sep/2023:07:06:42','106.88.8.91','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-07-22/34208d8c89cefb3665ae63b626d73b54.mp4',206,293,996872,'Dalvik/2.1.0 (Linux; U; Android 10; PDVM00 Build/QKQ1.200614.002)'),
(19,'22/Sep/2023:07:06:41','106.88.8.91','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-04/94d46f945cd6c9543259b9612303afc5.mp4',206,298,2338,'Dalvik/2.1.0 (Linux; U; Android 10; PDVM00 Build/QKQ1.200614.002)'),
(20,'22/Sep/2023:07:06:41','106.88.8.91','http://osscdn-sz.anshuntech.ltd/basis-admin/user-upload/2023-08-04/94d46f945cd6c9543259b9612303afc5.mp4',206,293,511144,'Dalvik/2.1.0 (Linux; U; Android 10; PDVM00 Build/QKQ1.200614.002)');

/*Table structure for table `excel` */

DROP TABLE IF EXISTS `excel`;

CREATE TABLE `excel` (
  `zytm` varchar(255) DEFAULT NULL,
  `bzls` varchar(255) DEFAULT NULL,
  `xkxz` varchar(255) DEFAULT NULL,
  `bj` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

insert  into `fnd_branch`(`branch_id`,`branch_code`,`branch_name`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
(1,'JSJ001','Java课程设计','Y',2,'2012-07-29',2,'2013-04-21'),
(2,'JSJ002','C语言程序设计','Y',2,'2012-07-29',2,'2012-11-24'),
(3,'JSJ003','数据库原理','Y',2,'2012-07-29',2,'2012-07-29'),
(4,'JSJ004','计算机网络','Y',2,'2012-08-15',2,'2013-05-30'),
(5,'JSJ005','计算机导论','Y',2,'2012-08-22',2,'2012-08-22'),
(6,'JSJ006','计算机组成原理','Y',2,'2012-09-27',2,'2012-09-27');

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

insert  into `fnd_class`(`class_id`,`class_code`,`class_name`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
(1,'074202','07级网络工程二班','Y',2,'2012-05-08',21,'2012-09-24'),
(2,'074201','07级网络工程一班','Y',2,'2012-05-22',21,'2012-09-25'),
(3,'074001','07级计算机科学与技术一班','Y',2,'2012-08-21',2,'2012-08-21'),
(4,'074401','07级软件工程一班','Y',21,'2012-09-22',21,'2012-09-22'),
(5,'074002','07级计算机科学与技术二班','Y',21,'2012-09-22',21,'2012-09-22'),
(6,'074402','07级软件工程二班','Y',21,'2012-09-22',2,'2013-05-30');

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

insert  into `fnd_course`(`course_id`,`enable_flag`,`class_id`,`branch_id`,`teacher_id`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
(4,'Y',2,1,1,2,'2012-07-29',2,'2012-07-29'),
(5,'Y',1,1,1,2,'2012-07-29',2,'2012-07-29'),
(6,'Y',2,2,2,2,'2012-07-29',2,'2012-07-29'),
(7,'Y',1,3,3,2,'2012-07-29',2,'2012-07-29'),
(8,'Y',2,3,3,2,'2012-07-29',2,'2012-07-29'),
(9,'Y',1,2,1,2,'2012-07-29',2,'2012-07-29'),
(10,'Y',3,1,1,2,'2012-08-21',2,'2012-08-21'),
(11,'Y',3,3,3,2,'2012-08-21',2,'2012-08-21'),
(12,'Y',1,5,4,2,'2012-08-22',2,'2012-08-22'),
(13,'Y',3,4,4,2,'2012-08-23',2,'2012-08-23'),
(14,'Y',2,4,4,2,'2012-08-27',2,'2012-08-27'),
(15,'Y',2,5,1,2,'2012-08-27',2,'2012-08-27'),
(16,'Y',1,4,4,2,'2012-08-27',2,'2012-08-27'),
(17,'Y',3,2,1,2,'2012-08-27',2,'2012-08-27'),
(18,'Y',3,5,3,2,'2012-08-27',21,'2012-08-28'),
(19,'Y',4,1,1,21,'2012-09-22',21,'2012-09-22'),
(20,'Y',6,6,3,2,'2012-09-27',2,'2012-09-27'),
(21,'Y',4,4,4,2,'2012-09-27',2,'2012-09-27'),
(22,'Y',5,3,3,21,'2012-10-11',21,'2012-10-11');

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

insert  into `fnd_student`(`student_id`,`class_id`,`student_code`,`student_name`,`telphone_num`,`email`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
(1,1,'07420207','小杨','12345678','213@163.com','Y',2,'2012-05-08',2,'2012-09-02'),
(2,1,'07420206','小徐','123456789','123@123.com','Y',2,'2012-05-08',2,'2012-09-02'),
(3,1,'07420205','小毛','12345678','213@163.com','Y',2,'2012-05-08',2,'2012-09-02'),
(4,1,'ADMIN','测试学生1','18621598333','213@163.com','Y',2,'2012-05-17',2,'2013-06-04'),
(5,2,'07420101','小明','18621598333','12321@123.com','Y',2,'2012-05-22',2,'2012-09-02'),
(6,2,'07420102','小王','123213213','213@163.com','Y',2,'2012-07-07',2,'2012-09-25'),
(7,2,'07420103','小军','18621598333','12321@123.com','Y',2,'2012-07-07',2,'2012-09-02'),
(8,1,'07420221','小黄','18621598333','213@163.com','Y',2,'2012-07-28',2,'2012-09-02'),
(9,2,'07420104','小海','18621598333','12321@123.com','Y',2,'2012-07-28',2,'2012-09-02'),
(10,2,'07420105','小朱','18621598333','12321@123.com','Y',2,'2012-07-28',2,'2012-09-02'),
(11,2,'07420106','小杨','18621598333','12321@123.com','Y',2,'2012-07-28',2,'2012-09-24'),
(12,1,'07420201','小郑','18621598333','213@163.com','Y',2,'2012-07-29',2,'2013-06-04'),
(13,1,'07420203','张三','18621598333','213@163.com','Y',2,'2012-07-29',2,'2012-09-02'),
(14,1,'07420202','小曾','18621598333','213@163.com','Y',2,'2012-07-29',2,'2012-09-02'),
(15,1,'07420204','小牛','18621598333','213@163.com','Y',2,'2012-07-29',2,'2012-09-02'),
(16,3,'07400101','小王','123456789','hello@163.com','Y',2,'2012-08-23',2,'2013-06-08'),
(17,3,'07400102','小明','123456789','123@sina.com','Y',21,'2012-08-30',2,'2012-09-02'),
(18,3,'07400103','小胡','123456789','12323@sina.com','Y',2,'2012-08-30',2,'2012-09-02'),
(19,3,'07400104','小马','123456789','123@sina.com','Y',2,'2012-08-30',2,'2012-09-02'),
(20,4,'07440101','小段','123213213','123@123.com','Y',21,'2012-09-22',21,'2012-09-22'),
(21,6,'07440201','小毛','123213213','huang@163.com','Y',2,'2012-09-27',2,'2012-09-27');

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

insert  into `fnd_teacher`(`teacher_id`,`teacher_code`,`teacher_name`,`telphone_num`,`email`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
(1,'10000','黄炯','13888889999','nfwork@sina.com','Y',2,'2012-05-08',2,'2013-06-04'),
(2,'ADMIN','测试老师','12334345454','yangyong@sina.com','Y',2,'2012-05-17',21,'2012-09-22'),
(3,'10010','杨永','13888889999','yangyong@sina.com','Y',2,'2012-07-07',2,'2013-08-14'),
(4,'10020','尹小军','12334345454','yangyong@sina.com','Y',2,'2012-07-27',21,'2012-10-16');

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

insert  into `gc_employee`(`employee_id`,`employee_code`,`employee_name`) values 
(1,'HJ','黄炯'),
(2,'YXJ','尹小军'),
(3,'YFQ','杨凤娇'),
(4,'YSM','尹双枚'),
(5,'DSF','dsf');

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

insert  into `gc_project`(`project_id`,`project_code`,`project_name`,`price`) values 
(1,'YJHY','怡景花园工程',100),
(2,'NNN','www',455);

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
) ENGINE=InnoDB AUTO_INCREMENT=364 DEFAULT CHARSET=utf8;

/*Data for the table `gc_work_record` */

insert  into `gc_work_record`(`work_id`,`employee_id`,`work_date`,`project_id`,`settlement_flag`,`settlement_date`) values 
(5,1,'2013-06-01',1,'Y','2013-06-26'),
(6,1,'2013-06-02',1,'Y','2013-06-26'),
(8,1,'2013-06-04',1,'Y','2013-06-26'),
(11,1,'2013-06-07',1,'Y','2013-06-26'),
(12,1,'2013-06-08',1,'Y','2013-06-26'),
(13,1,'2013-06-09',1,'Y','2013-06-26'),
(14,1,'2013-06-10',1,'Y','2013-06-26'),
(15,2,'2013-06-03',1,'Y','2013-06-26'),
(16,2,'2013-06-04',1,'Y','2013-06-26'),
(17,2,'2013-06-05',1,'Y','2013-06-26'),
(18,2,'2013-06-06',1,'Y','2013-06-26'),
(19,2,'2013-06-07',1,'Y','2013-06-26'),
(20,2,'2013-06-10',1,'Y','2013-06-26'),
(21,2,'2013-06-11',1,'Y','2013-06-26'),
(22,1,'2013-06-26',1,'Y','2013-06-26'),
(25,1,'2013-06-14',1,'Y','2013-06-26'),
(27,1,'2013-06-05',1,'Y','2013-06-26'),
(28,1,'2013-05-04',1,'Y','2013-06-26'),
(29,1,'2013-06-12',1,'Y','2013-06-26'),
(39,3,'2013-06-08',1,'Y','2013-06-26'),
(40,3,'2013-06-09',1,'Y','2013-06-26'),
(41,3,'2013-06-10',1,'Y','2013-06-26'),
(42,3,'2013-06-11',1,'Y','2013-06-26'),
(43,3,'2013-06-12',1,'Y','2013-06-26'),
(44,3,'2013-06-13',1,'Y','2013-06-26'),
(45,3,'2013-06-14',1,'Y','2013-06-26'),
(46,3,'2013-06-15',1,'Y','2013-06-26'),
(47,3,'2013-06-16',1,'Y','2013-06-26'),
(48,3,'2013-06-17',1,'Y','2013-06-26'),
(49,3,'2013-06-18',1,'Y','2013-06-26'),
(50,3,'2013-06-19',1,'Y','2013-06-26'),
(51,4,'2013-06-13',1,'Y','2013-06-26'),
(52,4,'2013-06-14',1,'Y','2013-06-26'),
(53,4,'2013-06-15',1,'Y','2013-06-26'),
(54,4,'2013-06-16',1,'Y','2013-06-26'),
(55,4,'2013-06-17',1,'Y','2013-06-26'),
(56,4,'2013-06-18',1,'Y','2013-06-26'),
(57,4,'2013-06-19',1,'Y','2013-06-26'),
(58,4,'2013-06-20',1,'Y','2013-06-26'),
(62,2,'2013-06-09',1,NULL,NULL),
(64,2,'2013-06-08',1,NULL,NULL),
(163,4,'2013-07-01',1,NULL,NULL),
(164,4,'2013-07-02',1,NULL,NULL),
(165,4,'2013-07-03',1,NULL,NULL),
(166,4,'2013-07-04',1,NULL,NULL),
(167,4,'2013-07-05',1,NULL,NULL),
(168,4,'2013-07-06',1,NULL,NULL),
(172,1,'2013-04-23',1,NULL,NULL),
(173,1,'2013-04-24',1,NULL,NULL),
(174,1,'2013-04-25',1,NULL,NULL),
(175,1,'2013-04-30',1,NULL,NULL),
(176,1,'2013-04-11',1,NULL,NULL),
(178,1,'2013-04-01',1,NULL,NULL),
(180,1,'2013-04-03',1,NULL,NULL),
(181,1,'2013-04-04',1,NULL,NULL),
(182,1,'2013-04-08',1,NULL,NULL),
(184,4,'2013-12-10',1,NULL,NULL),
(185,4,'2013-12-11',1,NULL,NULL),
(186,4,'2013-12-12',1,NULL,NULL),
(187,4,'2013-12-13',1,NULL,NULL),
(188,4,'2013-12-16',1,NULL,NULL),
(189,4,'2013-12-17',1,NULL,NULL),
(190,4,'2013-12-18',1,NULL,NULL),
(191,4,'2013-12-19',1,NULL,NULL),
(192,4,'2013-12-20',1,NULL,NULL),
(193,4,'2013-12-23',1,NULL,NULL),
(194,4,'2013-12-24',1,NULL,NULL),
(195,4,'2013-12-25',1,NULL,NULL),
(196,4,'2013-12-26',1,NULL,NULL),
(197,4,'2013-12-27',1,NULL,NULL),
(198,4,'2013-12-06',1,NULL,NULL),
(199,4,'2013-12-05',1,NULL,NULL),
(200,4,'2013-12-04',1,NULL,NULL),
(201,4,'2013-10-09',1,NULL,NULL),
(203,4,'2013-12-30',1,NULL,NULL),
(204,4,'2013-06-30',1,NULL,NULL),
(219,3,'2013-04-09',1,NULL,NULL),
(220,3,'2013-04-10',1,NULL,NULL),
(221,3,'2013-04-11',1,NULL,NULL),
(222,3,'2013-04-12',1,NULL,NULL),
(223,3,'2013-04-13',1,NULL,NULL),
(224,3,'2013-04-06',1,NULL,NULL),
(225,3,'2013-04-05',1,NULL,NULL),
(226,3,'2013-04-04',1,NULL,NULL),
(227,3,'2013-04-03',1,NULL,NULL),
(228,3,'2013-04-02',1,NULL,NULL),
(229,3,'2013-04-01',1,NULL,NULL),
(230,3,'2013-04-07',1,NULL,NULL),
(231,3,'2013-04-08',1,NULL,NULL),
(232,3,'2013-04-14',1,NULL,NULL),
(233,3,'2013-04-15',1,NULL,NULL),
(234,3,'2013-07-17',1,NULL,NULL),
(235,3,'2013-07-18',1,NULL,NULL),
(236,3,'2013-07-23',1,NULL,NULL),
(237,3,'2013-07-22',1,NULL,NULL),
(238,3,'2013-08-14',1,NULL,NULL),
(239,3,'2013-08-15',1,NULL,NULL),
(240,3,'2013-08-30',1,NULL,NULL),
(241,4,'2013-08-14',1,NULL,NULL),
(242,4,'2013-08-23',1,NULL,NULL),
(253,1,'2013-06-06',1,NULL,NULL),
(254,1,'2013-06-11',1,NULL,NULL),
(255,1,'2013-06-03',1,NULL,NULL),
(258,1,'2013-06-13',1,NULL,NULL),
(259,1,'2013-07-27',1,NULL,NULL),
(260,2,'2013-07-27',1,NULL,NULL),
(261,3,'2013-07-27',1,NULL,NULL),
(262,4,'2013-07-27',1,NULL,NULL),
(263,2,'2013-07-28',1,NULL,NULL),
(264,3,'2013-07-28',1,NULL,NULL),
(265,4,'2013-07-28',1,NULL,NULL),
(266,1,'2013-07-28',1,NULL,NULL),
(267,3,'2013-08-16',1,NULL,NULL),
(268,3,'2013-08-17',1,NULL,NULL),
(277,1,'2013-09-06',1,NULL,NULL),
(297,1,'2014-05-29',1,NULL,NULL),
(308,1,'2014-06-08',1,NULL,NULL),
(310,1,'2014-06-16',1,NULL,NULL),
(312,1,'2014-06-18',1,NULL,NULL),
(313,1,'2014-06-22',1,NULL,NULL),
(316,1,'2014-06-02',1,NULL,NULL),
(318,1,'2014-06-10',1,NULL,NULL),
(319,1,'2014-06-24',1,NULL,NULL),
(320,1,'2014-06-30',1,NULL,NULL),
(321,1,'2014-06-26',1,NULL,NULL),
(323,1,'2014-06-12',1,NULL,NULL),
(324,1,'2014-06-06',1,NULL,NULL),
(325,1,'2014-06-04',1,NULL,NULL),
(326,1,'2014-06-20',1,NULL,NULL),
(327,1,'2014-06-14',1,NULL,NULL),
(328,1,'2014-06-28',1,NULL,NULL),
(339,3,'2014-06-11',1,NULL,NULL),
(340,3,'2014-06-19',1,NULL,NULL),
(341,3,'2014-06-18',1,NULL,NULL),
(342,3,'2014-06-25',1,NULL,NULL),
(345,3,'2014-06-26',1,NULL,NULL),
(346,3,'2014-06-20',1,NULL,NULL),
(347,3,'2012-03-14',1,NULL,NULL),
(348,3,'2012-03-15',1,NULL,NULL),
(349,3,'2012-03-16',1,NULL,NULL),
(350,3,'2012-03-24',1,NULL,NULL),
(351,3,'2012-03-28',1,NULL,NULL),
(352,3,'2012-03-20',1,NULL,NULL),
(354,3,'2012-03-30',1,NULL,NULL),
(355,3,'2012-03-23',1,NULL,NULL),
(357,5,'2014-08-06',2,NULL,NULL),
(358,5,'2014-08-07',2,NULL,NULL),
(359,5,'2014-08-08',2,NULL,NULL),
(360,5,'2014-08-13',2,NULL,NULL),
(362,5,'2019-11-14',2,NULL,NULL),
(363,5,'2019-11-13',2,NULL,NULL);

/*Table structure for table `item` */

DROP TABLE IF EXISTS `item`;

CREATE TABLE `item` (
  `book_id` int(11) DEFAULT NULL,
  `item_num` int(11) DEFAULT NULL,
  UNIQUE KEY `code` (`book_id`,`item_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `item` */

insert  into `item`(`book_id`,`item_num`) values 
(1,10000),
(2,10000);

/*Table structure for table `job_headers` */

DROP TABLE IF EXISTS `job_headers`;

CREATE TABLE `job_headers` (
  `header_id` char(24) NOT NULL,
  `class_id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `teacher_id` int(11) NOT NULL,
  `end_time` date NOT NULL,
  `status` varchar(10) NOT NULL COMMENT '状态',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `description` varchar(2000) DEFAULT NULL COMMENT '描述',
  `create_by` int(11) NOT NULL,
  `create_date` date NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `last_update_date` date NOT NULL,
  PRIMARY KEY (`header_id`),
  KEY `tile` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `job_headers` */

insert  into `job_headers`(`header_id`,`class_id`,`course_id`,`teacher_id`,`end_time`,`status`,`title`,`description`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
('23CNJT5SGVQSDXA6AQWTTR98',2,6,2,'2014-06-27','END','111','1111',2,'2014-06-26',2,'2014-06-26'),
('6L1AUD7HO9P7XK9SPDCB2NDM',2,6,2,'2012-09-21','END','c语言作业三','c语言作业三，练习题1、2、3、4、5、6',2,'2012-09-01',2,'2012-09-01'),
('6W277M94DI9GNAL5132C4VNI',2,6,2,'2012-12-18','END','testq','testq',2,'2012-12-17',2,'2012-12-17'),
('746SBK6A6IYTHCGDZL5Y7802',1,5,1,'2012-08-24','DOWN','Java课程设计 练习题一','Java课程设计 练习题一 完成1、2、3、7、8题，请在周五之前完成。',1,'2012-08-20',1,'2012-08-20'),
('8VQ7YJTF2KF0NR35F9FB6ATX',2,6,2,'2014-10-22','NEW','32323','eweeeeee',2,'2014-10-08',2,'2014-10-08'),
('AYV97UZL07SED3FBOIPAQ3WB',2,6,2,'2013-05-24','DOWN','sadsadsa','asdasdas12描述:作业题目:\n',2,'2013-05-30',2,'2013-06-24'),
('DYAB8KAAPC2V5K6QAT1UPFEL',2,6,2,'2014-07-16','CHECK','111',NULL,2,'2014-07-15',2,'2014-08-04'),
('EGI1BF81XF2Q34AFV9XX1PVK',2,6,2,'2013-05-30','END','test','test123 123 123 123',2,'2013-05-31',2,'2013-06-06'),
('EK2Q9IYKR0EJ3UT87Q3LQD54',2,4,1,'2012-11-30','NEW','Java课程设计','Java课程设计',1,'2012-11-24',1,'2013-04-29'),
('HDNJWDRZNJI1314AZ2Y0IOEH',2,6,2,'2013-04-18','END','test0002','234324234',2,'2013-04-29',2,'2013-04-29'),
('HLXEZZWQL5IQV1J3S9MA19CB',2,6,2,'2012-08-17','END','Java课后系统一','Java课后系统一 1、3、4、6、7、8题，请按时完成。',2,'2012-08-08',2,'2012-08-08'),
('HTBVO5F7VX71AK51NKUDB6VX',1,9,1,'2012-08-17','END','c语言 课后系统一','c语言 课后系统一',1,'2012-08-08',1,'2012-08-08'),
('J42ZC7XR85ZU8ODX325RRTCE',2,6,2,'2012-08-09','END','C语言程序设计练习题二','C语言程序设计练习题二 1、2、3、5、6题 ，请按时完成。\r\n东方网9月29日消息：记者从外交部获悉，昨天，以“维护政治基础，把握发展方向”为主题的中日邦交正常化40周年座谈会在北京举行。外交部部长助理乐玉成在讲话中指出，日本不要再幻想霸占钓鱼岛，派人到中国来解释几句就万事大吉，如果继续一意孤行，中日关系这条大船就可能像“泰坦尼克”号一样触礁沉没。',2,'2012-08-08',2,'2012-09-29'),
('KJ2RECRHMCGQI1S7KC90FO5J',2,6,2,'2012-08-18','END','\'测试引号\"','测试引号是否显示正常\'\'\'\'\"\"\"/\"/\"dddd\'\'\'\'\'\',,,,777777n/////',2,'2012-08-15',2,'2012-09-27'),
('LR6CLROGCRTS5I89XR8LQYJA',2,6,2,'2013-06-19','END','12','12321321321312sdfdsfdsf',2,'2013-06-05',2,'2013-06-06'),
('NH7NGCC7441061F336TWI4FH',2,6,2,'2013-10-31','END','测试',NULL,2,'2013-10-18',2,'2013-10-18'),
('NNRXIJ577ZZHRYDG9V6M1EHP',1,9,1,'2012-08-31','END','Java课后系统二','Java课后系统二 1、3、4、5、8、9题',1,'2012-08-24',1,'2012-08-24'),
('NRVQYY8V8PJZGTCMH4M176CK',1,9,1,'2012-08-24','DOWN','C语言作业联系题二','C语言作业联系题二，完成1、2、3、6、7题',1,'2012-08-17',1,'2012-08-17'),
('OW3JXYY9ZGI5I9C7VSS2HT6S',1,7,3,'2012-08-17','NEW','数据库原理课后系统一','数据库原理课后系统一 1、2、4、5、15、18题，请按时完成。',9,'2012-08-08',9,'2012-08-08'),
('PKQBL6Y4L0SXXVD116L71DJP',4,19,1,'2013-06-11','NEW','test001','hello kity',1,'2013-06-03',1,'2013-06-03'),
('SD6OGB1WJFQHQP2MVR35GO7U',2,6,2,'2013-04-17','END','123','测试作业',2,'2013-04-09',2,'2013-04-10'),
('UO80AC6RPU64DF6C7KUIVQOA',2,8,3,'2012-08-17','DOWN','数据库原理课后练习一','数据库原理课后练习一 1、2、3、5、6，请按时完成。',9,'2012-08-08',9,'2012-08-08'),
('VBBJ88XTT92UUG4X0M0A5X2N',3,13,4,'2012-08-31','NEW','计算机网络课后练习题一','计算机网络课后练习题一 1、2、3、4、5题。',11,'2012-08-23',11,'2012-08-23'),
('WAZNJYUD0YYYY9IOR918YFQF',1,9,1,'2012-10-05','DOWN','hello kity','hello kity',1,'2012-09-21',1,'2012-09-21'),
('X676YPIAZ4RKVQTNON2TDAO6',2,6,2,'2013-04-26','DOWN','新增作业测试','新增作业测试 hello kity',2,'2013-04-20',2,'2013-04-20'),
('XNGIA1T22VB0OK1HTGQBVW7W',2,6,2,'2013-10-31','END','hello kity',NULL,2,'2013-10-22',2,'2013-10-22'),
('XSF060BUDZNTYVAU1VALQ89S',2,6,2,'2014-07-15','END','111','11',2,'2014-07-08',2,'2014-07-08'),
('ZSQS6SUD9HDZYVKYNLF5HCTP',1,12,4,'2012-08-23','DOWN','JSJ005-计算机导论 习题一','JSJ005-计算机导论 习题一 1、2、3、4',11,'2012-08-22',11,'2012-08-22');

/*Table structure for table `job_lines` */

DROP TABLE IF EXISTS `job_lines`;

CREATE TABLE `job_lines` (
  `line_id` char(24) NOT NULL,
  `header_id` char(24) NOT NULL COMMENT '头id',
  `student_id` int(11) DEFAULT NULL COMMENT '学生id',
  `description` varchar(2000) DEFAULT NULL COMMENT '描述',
  `score` float DEFAULT NULL COMMENT '分数',
  `teacher_comment` varchar(2000) DEFAULT NULL COMMENT '老师评语',
  `grade` varchar(200) DEFAULT NULL COMMENT '等级',
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

insert  into `job_lines`(`line_id`,`header_id`,`student_id`,`description`,`score`,`teacher_comment`,`grade`,`status`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
('0POBIYQQO31WGHEEL12C1HGP','746SBK6A6IYTHCGDZL5Y7802',4,'123',NULL,NULL,NULL,'SUBMIT',2,'2012-08-21',2,'2012-08-21'),
('11DQAQYW9F7ETBXY9ZDR3NF6','6W277M94DI9GNAL5132C4VNI',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),
('3A9R9CSVJHHA1PG4ACRU1YZR','ZSQS6SUD9HDZYVKYNLF5HCTP',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),
('3I6AP58IDFALNQA6SSRJBIVC','NNRXIJ577ZZHRYDG9V6M1EHP',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),
('4LT0BNTBBCXCF6F92JIGR21N','UO80AC6RPU64DF6C7KUIVQOA',5,'已经完成，请验收。时间刚刚好，哈哈哈。。。。。',NULL,NULL,NULL,'SUBMIT',7,'2012-08-08',7,'2012-08-08'),
('50PDF31TUPKG6PNI9KY6HH9L','HLXEZZWQL5IQV1J3S9MA19CB',6,NULL,NULL,NULL,NULL,'NEW',8,'2012-09-01',8,'2012-09-01'),
('59GVTWUY21VY0XLFG5IOSPFF','ZSQS6SUD9HDZYVKYNLF5HCTP',4,'123',NULL,NULL,NULL,'SUBMIT',2,'2012-08-23',2,'2012-08-23'),
('78EDRBZEWBAOE764XQU62C6A','WAZNJYUD0YYYY9IOR918YFQF',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),
('7AKWEZIFDIN89W0N8Q911JDL','HLXEZZWQL5IQV1J3S9MA19CB',7,NULL,NULL,NULL,NULL,'NEW',10,'2012-09-01',10,'2012-09-01'),
('7FZ9Q2G5E1BD3WXSVXD5MF0V','NNRXIJ577ZZHRYDG9V6M1EHP',4,'已经完成了',77,'一塌糊涂','合格','END',2,'2012-09-01',1,'2013-02-27'),
('8FFFJLICRL2ZO9SIQM6120EU','NRVQYY8V8PJZGTCMH4M176CK',3,NULL,NULL,NULL,NULL,'NEW',5,'2012-08-20',5,'2012-08-20'),
('8I5Y6E1R5L5NKYFPEP0PW2JI','6L1AUD7HO9P7XK9SPDCB2NDM',7,NULL,NULL,NULL,NULL,'NEW',10,'2012-09-01',10,'2012-09-01'),
('8J50NF7CMJXTDNJ27DLWHIEH','SD6OGB1WJFQHQP2MVR35GO7U',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),
('BKQAK25RZ6WTT0MT8T3213LQ','NNRXIJ577ZZHRYDG9V6M1EHP',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),
('C10K6Y5HP2XNGW7HAO1ANSKI','WAZNJYUD0YYYY9IOR918YFQF',12,'yes',NULL,NULL,NULL,'SUBMIT',16,'2012-09-21',16,'2012-09-21'),
('DH1IL3JF1WFFLP7GIAEFN09J','UO80AC6RPU64DF6C7KUIVQOA',6,NULL,NULL,NULL,NULL,'NEW',8,'2012-09-01',8,'2012-09-01'),
('EG2IXYYH3AVHBLEXD7GAJN5R','HLXEZZWQL5IQV1J3S9MA19CB',10,'已经完成了，请测试',80,'不错，蛮好的，继续加油','良好','END',14,'2012-08-08',2,'2012-08-27'),
('EKVHIS6ABAD6IOZ04AQQL3ZU','NNRXIJ577ZZHRYDG9V6M1EHP',12,'wancheng',89,'继续加油','良好','END',16,'2012-09-21',1,'2013-02-27'),
('GBJ8FL97WE5JO95EO0K56R5E','746SBK6A6IYTHCGDZL5Y7802',3,'已经做好了，请查收',NULL,NULL,NULL,'SUBMIT',5,'2012-08-20',5,'2012-08-20'),
('HEIIPX1GSCJZ6CK6IQ5IDXNM','WAZNJYUD0YYYY9IOR918YFQF',3,NULL,NULL,NULL,NULL,'NEW',5,'2012-10-17',5,'2012-10-17'),
('HPOSPGLAMLQA70HQK08MNXR0','ZSQS6SUD9HDZYVKYNLF5HCTP',8,'好了啊',NULL,NULL,NULL,'SUBMIT',12,'2012-09-01',12,'2012-09-01'),
('HR4PCHJOEMVPFTFS7C5FVT4O','NRVQYY8V8PJZGTCMH4M176CK',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),
('HZKIV5H3COIXTETYILNGEU2E','HTBVO5F7VX71AK51NKUDB6VX',8,'已经完成了，请验收',85,'嗯，也不错','良好','END',12,'2012-08-08',1,'2012-08-13'),
('I9MUCV1WOWZX1VQA94FJ9OVI','NRVQYY8V8PJZGTCMH4M176CK',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),
('ISRWDUSCCRAQYXBMTTJVD5K8','ZSQS6SUD9HDZYVKYNLF5HCTP',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),
('IYF5ZW3CBWNS4QO1F4NE2S8M','746SBK6A6IYTHCGDZL5Y7802',8,'ok啊',NULL,NULL,NULL,'SUBMIT',12,'2012-09-01',12,'2012-09-01'),
('JIRLT8RVBEL9XY88TQFTW5F6','HLXEZZWQL5IQV1J3S9MA19CB',5,'已经完成，请验收',96,'非常好','优秀','END',7,'2012-08-08',2,'2012-08-27'),
('JPNDSL8ARUKNO4KPMW1VRG7G','X676YPIAZ4RKVQTNON2TDAO6',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),
('K2B7MHV57MSXMJIRYBFGY7RR','NNRXIJ577ZZHRYDG9V6M1EHP',3,'已经完成',87,NULL,'良好','END',5,'2012-10-17',1,'2012-10-29'),
('LJDA087KCZDONFSJDDFHY3VH','746SBK6A6IYTHCGDZL5Y7802',12,'haole',NULL,NULL,NULL,'SUBMIT',16,'2012-08-23',16,'2012-08-23'),
('LZK5D7MKMOUQX4FDGAYCP255','HTBVO5F7VX71AK51NKUDB6VX',3,NULL,NULL,NULL,NULL,'NEW',5,'2012-08-13',5,'2012-08-13'),
('MC2LFPDKCRZQGYF6LWMBXFNO','HTBVO5F7VX71AK51NKUDB6VX',12,'已经做好了，请老师查看。谢谢',100,'太漂亮了，加油','优秀','END',16,'2012-08-09',1,'2012-08-13'),
('MYMOQH98CB6IHMW19BOHWRWY','ZSQS6SUD9HDZYVKYNLF5HCTP',12,'haole',NULL,NULL,NULL,'SUBMIT',16,'2012-08-23',16,'2012-08-23'),
('O2INGU7D9YMCYULQQ4NMH2TY','NRVQYY8V8PJZGTCMH4M176CK',4,'123',NULL,NULL,NULL,'SUBMIT',2,'2012-08-18',2,'2012-08-18'),
('OM1AM8Z930JTTIN86J7L8ZG1','HTBVO5F7VX71AK51NKUDB6VX',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),
('OOOW1YATNIENR6ZZMZWL9IHF','WAZNJYUD0YYYY9IOR918YFQF',8,'没有问题了啊',NULL,NULL,NULL,'SUBMIT',12,'2012-10-29',12,'2012-10-29'),
('PRUIC7HGJ0H00MDPQYBRVF4I','NRVQYY8V8PJZGTCMH4M176CK',12,'ok',NULL,NULL,NULL,'SUBMIT',16,'2012-08-23',16,'2012-08-23'),
('PXABL2S1CXCIHO6JIVIODWK9','UO80AC6RPU64DF6C7KUIVQOA',7,NULL,NULL,NULL,NULL,'NEW',10,'2012-09-01',10,'2012-09-01'),
('QRM215VS9WOSF56Y0JG9UNC2','J42ZC7XR85ZU8ODX325RRTCE',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),
('RR67IWIZ38W8RLVGEYSEALPK','6L1AUD7HO9P7XK9SPDCB2NDM',5,NULL,NULL,NULL,NULL,'NEW',7,'2012-09-27',7,'2012-09-27'),
('U5OTVO71K8ZCKES8BO91Y78Z','KJ2RECRHMCGQI1S7KC90FO5J',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),
('VKCVEFHSY8KITBUSNCTHJB1T','746SBK6A6IYTHCGDZL5Y7802',1,NULL,NULL,NULL,NULL,'NEW',4,'2012-09-01',4,'2012-09-01'),
('VQPH0RXULA7S1NLKP6F12ATI','HTBVO5F7VX71AK51NKUDB6VX',4,NULL,NULL,NULL,NULL,'NEW',2,'2012-08-08',2,'2012-08-08'),
('W7W233CL05ZLUSGPK1KC6OQQ','NNRXIJ577ZZHRYDG9V6M1EHP',8,'好了',88.5,'嗯，不错','良好','END',12,'2012-10-29',1,'2013-02-27'),
('W8K6O9GQ8BBTZ3Z804NKUJEE','HTBVO5F7VX71AK51NKUDB6VX',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21'),
('WCJB3N797CU999YSIAFJH1L4','ZSQS6SUD9HDZYVKYNLF5HCTP',3,'hjhg',NULL,NULL,NULL,'SUBMIT',5,'2012-10-17',5,'2012-10-17'),
('XP4NUZEYB55BWKZWX2QZEAUR','HDNJWDRZNJI1314AZ2Y0IOEH',5,NULL,NULL,NULL,NULL,'NEW',7,'2013-05-30',7,'2013-05-30'),
('XXZYD40LA2H5CCNX39CEBF9B','6L1AUD7HO9P7XK9SPDCB2NDM',6,NULL,NULL,NULL,NULL,'NEW',8,'2012-09-01',8,'2012-09-01'),
('YRJ95HHOFS65CV9L3IE4Y6EW','NRVQYY8V8PJZGTCMH4M176CK',8,'已经完成，快吧。',NULL,NULL,NULL,'SUBMIT',12,'2012-08-17',12,'2012-08-17'),
('Z9X5SZ7RH3EIGV3D6PLXS3FQ','WAZNJYUD0YYYY9IOR918YFQF',4,'123',NULL,NULL,NULL,'SUBMIT',2,'2012-09-21',2,'2012-09-21'),
('ZESVPPOYL2D2T0G8F1NWVGUD','746SBK6A6IYTHCGDZL5Y7802',13,NULL,NULL,NULL,NULL,'NEW',17,'2012-09-21',17,'2012-09-21');

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

insert  into `sys_access_control`(`ac_id`,`url`,`enable_flag`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
(1,'modules/sys/function.jsp','F',1,'2012-01-22',2,'2012-03-24'),
(2,'modules/sys/user.jsp','F',2,'2012-03-05',2,'2012-03-24'),
(4,'main.jsp','S',2,'2012-03-05',2,'2012-03-24'),
(5,'left.jsp','S',2,'2012-03-05',2,'2013-06-25'),
(6,'modules/sys/accessControl.jsp','F',2,'2012-03-05',2,'2012-03-24'),
(7,'modules/sys/module.jsp','F',2,'2012-03-05',2,'2012-03-24'),
(8,'modules/sys/role.jsp','F',2,'2012-03-05',2,'2012-03-24'),
(9,'modules/sys/power.jsp','F',2,'2012-03-05',2,'2012-03-24'),
(10,'system.jsp','F',2,'2012-03-21',2,'2012-08-16'),
(12,'modules/sys/function.query','F',2,'2012-04-26',2,'2012-04-26'),
(13,'modules/fnd/teacher.jsp','F',2,'2012-05-08',2,'2012-05-08'),
(15,'modules/fnd/class.jsp','F',2,'2012-05-08',2,'2012-05-08'),
(16,'modules/fnd/student.jsp','F',2,'2012-05-08',2,'2012-05-08'),
(17,'modules/job/jobManage.jsp','F',2,'2012-05-09',2,'2012-05-09'),
(18,'modules/job/newJob.jsp','F',2,'2012-05-09',2,'2012-05-09'),
(19,'modules/job/myJob.jsp','F',2,'2012-05-10',2,'2012-05-10'),
(20,'modules/job/myHistoryJob.jsp','F',2,'2012-05-17',2,'2012-05-17'),
(21,'modules/job/jobQuery.jsp','F',2,'2012-05-22',2,'2012-05-22'),
(22,'modules/job/showCheckedJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),
(23,'modules/job/showMyJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),
(24,'uploadShow.jsp','S',2,'2012-07-07',2,'2012-07-07'),
(25,'modules/job/updateMyJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),
(26,'modules/job/showJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),
(27,'modules/job/updateJob.jsp','F',2,'2012-07-07',2,'2012-07-07'),
(28,'modules/job/myJobQuery.jsp','F',2,'2012-07-19',2,'2012-07-19'),
(29,'modules/cos/courseQuery.jsp','F',2,'2012-07-30',2,'2012-07-30'),
(30,'modules/cos/course.jsp','F',2,'2012-07-30',2,'2012-07-30'),
(31,'cos/course.query','F',2,'2012-08-08',2,'2012-09-28'),
(32,'sys/power.query','F',2,'2012-08-08',2,'2012-08-08'),
(33,'sys/accessControl.query','F',2,'2012-08-08',2,'2012-08-08'),
(34,'sys/role.query','F',2,'2012-08-08',2,'2012-08-08'),
(35,'upload.query','S',2,'2012-08-08',2,'2012-08-08'),
(36,'sys/function.query','F',2,'2012-08-08',2,'2012-08-08'),
(37,'sys/module.query','F',2,'2012-08-08',2,'2012-08-08'),
(38,'sys/user.query','F',2,'2012-08-08',2,'2012-08-08'),
(39,'download.jsp','S',2,'2012-08-09',2,'2012-08-09'),
(40,'modules/sys/pagerAssign.jsp','F',2,'2012-08-15',2,'2012-08-15'),
(41,'functionLoad.query!load','S',2,'2012-08-17',2,'2012-08-17'),
(42,'modules/fnd/branch.jsp','F',2,'2012-08-18',2,'2012-08-18'),
(43,'index.jsp','S',2,'2013-07-26',2,'2013-07-26');

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

insert  into `sys_code`(`code_id`,`code`,`code_value`,`code_name`,`priority`) values 
(1,'JOB_STATUS','NEW','新建',10),
(2,'JOB_STATUS','DOWN','已布置',20),
(3,'JOB_STATUS','CHECK','审批中',30),
(4,'JOB_STATUS','END','完成',40),
(5,'JOB_LINE_STATUS','NEW','未提交',10),
(6,'JOB_LINE_STATUS','SUBMIT','已提交',20),
(7,'JOB_LINE_STATUS','END','已审批',30),
(8,'JOB_PRIORITY','优秀','优秀',10),
(9,'JOB_PRIORITY','良好','良好',20),
(10,'JOB_PRIORITY','合格','合格',30),
(11,'JOB_PRIORITY','不合格','不合格',40);

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
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

/*Data for the table `sys_function` */

insert  into `sys_function`(`function_id`,`function_code`,`function_des`,`jsp_pager`,`image`,`priority`,`function_module`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
(1,'SYS001','用户定义','modules/sys/user.jsp','70000001.gif',10,1,1,'2011-08-14',2,'2011-12-30'),
(2,'SYS002','功能定义','modules/sys/function.jsp','80000000.gif',40,1,1,'2011-08-14',2,'2012-04-24'),
(3,'SYS003','模块定义','modules/sys/module.jsp','50000000.gif',30,1,1,'2011-08-14',2,'2011-12-26'),
(4,'SYS004','角色定义','modules/sys/role.jsp','90000000.gif',20,1,2,'2011-12-20',2,'2012-04-11'),
(5,'SYS005','权限控制','modules/sys/power.jsp','12000000.gif',50,1,2,'2011-12-20',2,'2012-01-17'),
(13,'SYS006','访问控制','modules/sys/accessControl.jsp',NULL,60,1,2,'2012-03-05',2,'2012-04-12'),
(14,'SYS007','系统管理','system.jsp',NULL,70,1,2,'2012-03-20',2,'2013-03-30'),
(25,'JOB001','作业新增','modules/job/newJob.jsp',NULL,10,7,2,'2012-05-08',2,'2013-05-31'),
(26,'JOB002','作业工作台','modules/job/jobManage.jsp','50000000.gif',20,7,2,'2012-05-08',2,'2013-02-26'),
(27,'JOB003','作业历史查询','modules/job/jobQuery.jsp',NULL,30,7,2,'2012-05-08',2,'2012-05-08'),
(28,'JOB101','我的当前作业','modules/job/myJob.jsp',NULL,40,7,2,'2012-05-08',2,'2013-05-29'),
(29,'JOB102','我的历史作业','modules/job/myHistoryJob.jsp',NULL,50,7,2,'2012-05-08',2,'2012-07-22'),
(33,'TEST001','ViewForm 测试','modules/test/testViewForm.jsp',NULL,10,9,2,'2013-06-06',2,'2013-06-06'),
(34,'TEST002','综合测试','modules/test/testAll.jsp',NULL,20,9,2,'2013-06-06',2,'2013-06-06'),
(35,'TEST003','tree测试','modules/test/testTree.jsp',NULL,30,9,2,'2013-06-06',2,'2013-06-06'),
(36,'TEST004','grid级联测试','modules/test/cascadeGrid.jsp',NULL,40,9,2,'2013-06-06',2,'2013-06-06'),
(37,'TEST005','grid复合表头','modules/test/testGrid.jsp',NULL,50,9,2,'2013-06-06',2,'2013-06-06'),
(39,'TEST007','tab测试','modules/test/testTab.jsp',NULL,70,9,2,'2013-06-06',2,'2013-06-06'),
(44,'GC005','结算查询','modules/gc/settlementQuery.jsp',NULL,50,10,2,'2013-06-09',2,'2023-09-22'),
(45,'GC006','报工取消','modules/gc/workUnRegist.jsp',NULL,35,10,2,'2013-06-09',2,'2013-06-09'),
(46,'GC007','工时补报','modules/gc/workRegistAdd.jsp',NULL,33,10,2,'2013-06-26',2,'2013-06-26'),
(47,'TEST008','panel测试','modules/test/testPanel.jsp',NULL,80,9,2,'2013-07-18',2,'2013-07-18'),
(48,'TEST009','chart图像报表','modules/test/chart.jsp',NULL,90,9,2,'2013-07-19',2,'2014-06-03'),
(49,'TEST010','form表单','modules/test/form.jsp',NULL,5,9,2,'2013-07-21',2,'2013-07-21'),
(50,'TEST011','treegrid测试','modules/test/testTreeGrid.jsp',NULL,100,9,2,'2013-07-22',2,'2013-07-22'),
(51,'TEST012','menu右击菜单','modules/test/testMenu.jsp',NULL,35,9,2,'2013-08-16',2,'2013-08-16'),
(54,'TEST013','chart图表综合测试','modules/test/chartGrid.jsp',NULL,90,9,2,'2014-04-30',2,'2014-06-03'),
(55,'TEST014','动态列表','modules/test/dynamicGrid.jsp',NULL,70,9,2,'2014-04-30',2,'2014-04-30'),
(57,'TEST015','image图片测试','modules/test/testImage.jsp',NULL,110,9,2,'2014-07-23',2,'2014-07-23'),
(60,'TEST016','级联测试','modules/test/comboCasecade.jsp',NULL,111,9,2,'2014-08-19',2,'2014-08-19'),
(61,'SYS008','访问日志分析','modules/sys/accessLog.jsp',NULL,80,1,2,'2023-09-22',2,'2023-09-22');

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `sys_module` */

insert  into `sys_module`(`module_id`,`module_code`,`module_name`,`priority`,`create_by`,`create_date`,`last_update_by`,`last_update_date`,`image`) values 
(1,'SYS','系统设置',10,1,'2011-08-14',2,'2013-05-31','nav_icon1.png'),
(6,'FND','基础定义',20,2,'2012-05-07',2,'2013-06-25','nav_icon2.png'),
(7,'JOB','作业管理',30,2,'2012-05-08',2,'2012-05-08','nav_icon3.png'),
(8,'COS','课程管理',40,2,'2012-07-30',2,'2013-05-31','nav_icon4.png'),
(9,'TEST','测试用例',50,2,'2013-06-06',2,'2013-06-06','nav_icon5.png'),
(10,'GC','工程计费',60,2,'2013-06-09',2,'2013-06-09','nav_icon6.png');

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

insert  into `sys_pager_assign`(`assign_id`,`function_id`,`ac_id`,`create_by`,`create_date`,`last_update_by`,`last_update_date`) values 
(3,13,6,2,'2012-03-24',2,'2012-03-24'),
(5,3,7,2,'2012-03-24',2,'2012-10-10'),
(6,5,9,2,'2012-03-24',2,'2012-03-24'),
(7,4,8,2,'2012-03-24',2,'2012-03-24'),
(8,1,2,2,'2012-03-24',2,'2012-03-24'),
(10,2,1,2,'2012-03-24',2,'2012-03-24'),
(11,14,10,2,'2012-04-25',2,'2012-04-25'),
(13,2,12,2,'2012-04-26',2,'2012-04-26'),
(14,22,13,2,'2012-05-08',2,'2012-05-08'),
(16,23,15,2,'2012-05-08',2,'2012-05-08'),
(17,21,16,2,'2012-05-08',2,'2012-05-08'),
(18,26,17,2,'2012-05-09',2,'2012-05-09'),
(19,25,18,2,'2012-05-09',2,'2012-05-09'),
(20,28,19,2,'2012-05-10',2,'2012-05-10'),
(21,29,20,2,'2012-05-17',2,'2012-05-17'),
(22,27,21,2,'2012-05-22',2,'2012-05-22'),
(23,27,22,2,'2012-07-07',2,'2012-07-07'),
(24,26,22,2,'2012-07-07',2,'2012-07-07'),
(25,29,23,2,'2012-07-07',2,'2012-07-07'),
(26,28,25,2,'2012-07-07',2,'2012-07-07'),
(27,27,26,2,'2012-07-07',2,'2012-07-07'),
(28,26,27,2,'2012-07-07',2,'2012-07-07'),
(29,28,28,2,'2012-07-19',2,'2012-07-19'),
(30,32,29,2,'2012-07-30',2,'2012-07-30'),
(31,26,30,2,'2012-07-30',2,'2014-09-18'),
(32,21,31,2,'2012-08-08',2,'2014-04-11'),
(33,5,32,2,'2012-08-08',2,'2012-08-08'),
(34,13,33,2,'2012-08-08',2,'2012-08-08'),
(35,4,34,2,'2012-08-08',2,'2012-08-08'),
(36,2,36,2,'2012-08-08',2,'2012-08-08'),
(37,3,37,2,'2012-08-08',2,'2012-08-08'),
(38,1,38,2,'2012-08-08',2,'2012-08-08'),
(39,13,40,2,'2012-08-15',2,'2012-08-15'),
(40,30,42,2,'2012-08-18',2,'2012-08-18');

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
) ENGINE=InnoDB AUTO_INCREMENT=191 DEFAULT CHARSET=utf8;

/*Data for the table `sys_power` */

insert  into `sys_power`(`power_id`,`function_id`,`role_id`,`create_by`,`create_date`,`last_update_by`,`last_update_date`,`enable_flag`) values 
(87,1,3,2,'2012-03-21',2,'2012-03-21','Y'),
(88,2,3,2,'2012-03-21',2,'2012-03-24','Y'),
(89,3,3,2,'2012-03-21',2,'2012-03-21','Y'),
(90,4,3,2,'2012-03-21',2,'2012-03-21','Y'),
(91,5,3,2,'2012-03-21',2,'2012-03-21','Y'),
(92,13,3,2,'2012-03-21',2,'2012-03-21','Y'),
(93,14,3,2,'2012-03-21',2,'2012-03-21','Y'),
(97,1,5,2,'2012-03-21',2,'2012-07-29',NULL),
(98,2,5,2,'2012-03-21',2,'2012-07-29',NULL),
(99,3,5,2,'2012-03-21',2,'2012-07-29',NULL),
(100,4,5,2,'2012-03-21',2,'2012-07-29',NULL),
(101,5,5,2,'2012-03-21',2,'2012-07-29',NULL),
(102,13,5,2,'2012-03-21',2,'2012-07-29',NULL),
(103,14,5,2,'2012-03-21',2,'2012-07-29',NULL),
(124,4,6,2,'2012-04-25',2,'2012-07-28',NULL),
(127,5,6,2,'2012-04-26',2,'2012-07-28',NULL),
(135,25,3,2,'2012-05-08',2,'2012-05-08','Y'),
(136,26,3,2,'2012-05-08',2,'2012-05-08','Y'),
(137,27,3,2,'2012-05-08',2,'2012-05-08','Y'),
(138,28,3,2,'2012-05-08',2,'2012-05-08','Y'),
(139,29,3,2,'2012-05-08',2,'2012-05-08','Y'),
(140,28,6,2,'2012-05-08',2,'2012-05-08','Y'),
(141,29,6,2,'2012-05-08',2,'2012-05-08','Y'),
(142,25,5,2,'2012-05-08',2,'2012-05-08','Y'),
(143,26,5,2,'2012-05-08',2,'2012-05-08','Y'),
(144,27,5,2,'2012-05-08',2,'2012-05-08','Y'),
(145,29,5,2,'2012-05-22',2,'2012-07-29',NULL),
(159,1,7,2,'2012-08-27',2,'2012-09-02','N'),
(160,33,3,2,'2013-06-06',2,'2013-06-06','Y'),
(161,34,3,2,'2013-06-06',2,'2013-06-06','Y'),
(162,35,3,2,'2013-06-06',2,'2013-06-06','Y'),
(163,36,3,2,'2013-06-06',2,'2013-06-06','Y'),
(164,37,3,2,'2013-06-06',2,'2013-06-06','Y'),
(166,39,3,2,'2013-06-06',2,'2013-06-06','Y'),
(171,44,3,2,'2013-06-09',2,'2013-06-09','Y'),
(172,45,3,2,'2013-06-09',2,'2013-06-09','Y'),
(173,46,3,2,'2013-06-26',2,'2013-06-26','Y'),
(174,47,3,2,'2013-07-18',2,'2013-07-18','Y'),
(177,48,3,2,'2014-04-30',2,'2014-04-30','Y'),
(178,49,3,2,'2014-04-30',2,'2014-04-30','Y'),
(179,50,3,2,'2014-04-30',2,'2014-04-30','Y'),
(180,51,3,2,'2014-04-30',2,'2014-04-30','Y'),
(181,54,3,2,'2014-04-30',2,'2014-04-30','Y'),
(182,55,3,2,'2014-04-30',2,'2014-04-30','Y'),
(184,57,3,2,'2014-07-23',2,'2014-07-23','Y'),
(186,60,3,2,'2014-08-19',2,'2014-08-19','Y'),
(187,44,7,2,'2019-11-23',2,'2019-11-24','N'),
(188,45,7,2,'2019-11-23',2,'2019-11-24','N'),
(189,46,7,2,'2019-11-24',2,'2019-11-24','N'),
(190,61,3,2,'2023-09-22',2,'2023-09-22','Y');

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_code` varchar(100) NOT NULL,
  `role_description` varchar(200) NOT NULL,
  `create_by` int(11) NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `last_update_date` datetime NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;

/*Data for the table `sys_role` */

insert  into `sys_role`(`role_id`,`role_code`,`role_description`,`create_by`,`last_update_by`,`create_date`,`last_update_date`) values 
(3,'ADMIN','系统管理员',2,2,'2011-12-29 00:00:00','2023-07-28 11:53:26'),
(5,'TEACHER','授课教师',2,2,'2012-03-21 00:00:00','2023-06-09 15:27:32'),
(6,'STUDENT','学生',2,2,'2012-04-12 00:00:00','2023-06-09 17:07:48'),
(7,'JWCADMIN','教务处管理员',2,2,'2012-08-27 00:00:00','2023-07-14 15:34:43'),
(8,'TEST','测试人员',2,2,'2023-06-10 02:03:15','2023-06-10 02:03:15');

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

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(100) NOT NULL,
  `user_name` varchar(200) NOT NULL,
  `status` varchar(10) NOT NULL,
  `password` varchar(50) NOT NULL,
  `create_date` datetime NOT NULL,
  `create_by` int(11) NOT NULL,
  `last_update_date` datetime NOT NULL,
  `last_update_by` int(11) NOT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_code` (`user_code`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`user_code`,`user_name`,`status`,`password`,`create_date`,`create_by`,`last_update_date`,`last_update_by`,`role_id`) values 
(2,'ADMIN','管理员','Y','admin','2011-11-26 10:00:00',1,'2023-08-29 12:47:48',2,3),
(4,'07420207','小杨','Y','123','2012-04-11 00:00:00',2,'2023-07-25 15:24:39',2,6),
(5,'07420205','小毛','Y','123456','2012-04-12 00:00:00',2,'2023-07-18 14:07:36',2,6),
(6,'07420206','小徐','Y','123456','2012-05-06 00:00:00',2,'2012-05-23 00:00:00',2,6),
(7,'07420101','小明','Y','123456','2012-05-22 00:30:00',2,'2023-07-12 17:00:38',2,6),
(8,'07420102','小王','Y','123456','2012-07-07 00:00:00',2,'2022-05-18 00:00:00',2,6),
(9,'10010','杨永','Y','123456','2012-07-07 00:00:00',2,'2012-09-02 00:00:00',2,5),
(10,'07420103','小明11','Y','123456','2023-07-20 15:15:04',2,'2023-07-18 14:55:27',2,6),
(11,'10020','小明22','Y','123456','2023-07-20 15:13:39',2,'2023-07-18 14:55:27',2,5),
(12,'07420221','小黄','Y','123456','2012-07-28 00:00:00',2,'2012-07-28 00:00:00',2,6),
(13,'07420104','小海','Y','123456','2012-07-28 00:00:00',2,'2012-08-06 00:00:00',2,6),
(14,'07420105','小朱','Y','123456','2012-07-28 00:00:00',2,'2012-08-06 00:00:00',2,6),
(15,'07420106','小李','Y','123456','2012-07-28 00:00:00',2,'2014-07-03 00:00:00',2,6),
(16,'07420201','小郑','Y','123456','2012-07-29 00:00:00',2,'2012-08-06 00:00:00',2,6),
(17,'07420203','张三','Y','123456','2012-07-29 00:00:00',2,'2012-08-06 00:00:00',2,6),
(18,'07420202','小曾','Y','123456','2012-07-29 00:00:00',2,'2012-08-06 00:00:00',2,6),
(19,'07420204','小牛','Y','123456','2012-07-29 00:00:00',2,'2012-08-06 00:00:00',2,6),
(21,'20000','教务处老师','Y','123456','2012-08-27 00:00:00',2,'2013-02-26 00:00:00',2,7),
(22,'07400102','小明','Y','123456','2012-08-30 00:00:00',21,'2023-05-12 00:00:00',2,6),
(23,'07400103','小胡','Y','huangjiong','2012-08-30 00:00:00',2,'2013-04-19 00:00:00',2,6),
(24,'07400104','小马','Y','123456','2012-08-30 00:00:00',2,'2023-07-01 10:22:18',2,6),
(25,'07440101','小段','Y','123456','2012-09-22 00:00:00',21,'2012-09-22 00:00:00',21,6),
(26,'07440201','小毛','Y','123456','2012-09-27 00:00:00',2,'2012-09-27 00:00:00',2,6),
(27,'07420107','满1','Y','123','2014-07-03 00:00:00',2,'2014-07-03 00:00:00',2,5),
(28,'123','123','Y','213','2014-07-03 00:00:00',2,'2023-06-16 15:48:16',2,5),
(32,'10086','test_123','Y','123456','2014-07-29 00:00:00',29,'2019-11-28 00:00:00',2,3),
(33,'10087','test_456','Y','123343434','2019-11-28 11:15:04',2,'2019-11-28 00:00:00',2,6);

/*Table structure for table `test` */

DROP TABLE IF EXISTS `test`;

CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_date` date DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `image` longblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `test` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(100) NOT NULL,
  `user_name` varchar(200) NOT NULL,
  `password` varchar(50) NOT NULL,
  `create_date` datetime NOT NULL,
  `create_by` int(11) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_code` (`user_code`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`user_id`,`user_code`,`user_name`,`password`,`create_date`,`create_by`) values 
(1,'xiaoming','小明','123456123','2023-05-04 19:01:01',1),
(20,'xiaoming1','xiaoming','123','2023-07-26 12:44:10',1),
(21,'xiaoming2','xiaoming','123','2023-07-26 12:44:10',1),
(44,'xiaoming3','xiaoming','123','2023-07-26 14:41:14',1),
(45,'xiaoming4','xiaoming','123','2023-07-26 14:41:14',1),
(62,'xiaoming5','xiaoming','123','2023-07-28 10:11:19',1),
(63,'xiaoming6','xiaoming','123','2023-07-28 10:11:19',1);
