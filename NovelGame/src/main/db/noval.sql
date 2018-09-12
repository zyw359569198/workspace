/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 8.0.12 : Database - noval
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`noval` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `noval`;

/*Table structure for table `author` */

DROP TABLE IF EXISTS `author`;

CREATE TABLE `author` (
  `id` varchar(36) NOT NULL,
  `author_id` varchar(36) DEFAULT NULL,
  `author_name` varchar(100) DEFAULT NULL,
  `author_name_en` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `index_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `author` */

/*Table structure for table `book` */

DROP TABLE IF EXISTS `book`;

CREATE TABLE `book` (
  `id` varchar(36) NOT NULL,
  `book_id` varchar(36) DEFAULT NULL,
  `book_name` varchar(200) DEFAULT NULL,
  `book_name_en` varchar(200) DEFAULT NULL,
  `book_desc` varchar(500) DEFAULT NULL,
  `create_time` date DEFAULT NULL,
  `update_time` date DEFAULT NULL,
  `is_completion` int(11) DEFAULT '1',
  `author_id` varchar(36) DEFAULT NULL,
  `hits` bigint(20) DEFAULT '0',
  `image_url` varchar(200) DEFAULT NULL,
  `author_name` varchar(100) DEFAULT NULL,
  `author_name_en` varchar(100) DEFAULT NULL,
  `last_store_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `index_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=8;

/*Data for the table `book` */

/*Table structure for table `cata_book_relation` */

DROP TABLE IF EXISTS `cata_book_relation`;

CREATE TABLE `cata_book_relation` (
  `id` varchar(36) NOT NULL,
  `cata_id` varchar(36) DEFAULT NULL,
  `book_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `cata_book_relation` */

/*Table structure for table `catagory` */

DROP TABLE IF EXISTS `catagory`;

CREATE TABLE `catagory` (
  `id` varchar(36) NOT NULL,
  `cata_id` varchar(36) DEFAULT NULL,
  `cata_name` varchar(100) DEFAULT NULL,
  `cata_name_en` varchar(100) DEFAULT NULL,
  `order_desc` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `catagory` */

insert  into `catagory`(`id`,`cata_id`,`cata_name`,`cata_name_en`,`order_desc`) values ('0','0','首页','shouye',1),('1','1','都市','dushi',2),('10','10','其它','qita',11),('2','2','玄幻','xuanhuan',3),('3','3','武侠','wuxia',4),('4','4','言情','yanqing',5),('5','5','穿越','chuanyue',6),('6','6','网游','wangyou',7),('7','7','恐怖','kongbu',8),('8','8','科幻','kehuan',9),('9','9','修真','xiuzhen',10);

/*Table structure for table `model` */

DROP TABLE IF EXISTS `model`;

CREATE TABLE `model` (
  `id` varchar(36) NOT NULL,
  `model_id` varchar(36) DEFAULT NULL,
  `model_name` varchar(200) DEFAULT NULL,
  `model_name_en` varchar(100) DEFAULT NULL,
  `order_desc` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `model` */

insert  into `model`(`id`,`model_id`,`model_name`,`model_name_en`,`order_desc`) values ('1','1','新书','hot',1),('2','2','推荐','recommend',2),('3','3','完本','full',3),('4','4','排行','top',4),('5','5','作者','authors',5);

/*Table structure for table `store` */

DROP TABLE IF EXISTS `store`;

CREATE TABLE `store` (
  `id` varchar(36) NOT NULL,
  `book_id` varchar(36) DEFAULT NULL,
  `store_id` varchar(36) DEFAULT NULL,
  `store_name` varchar(200) DEFAULT NULL,
  `store_url` varchar(200) DEFAULT NULL,
  `pre_store_id` varchar(36) DEFAULT NULL,
  `next_store_id` varchar(36) DEFAULT NULL,
  `create_time` date DEFAULT NULL,
  `order_index` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `store_id_index` (`store_id`),
  KEY `index_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=8;

/*Data for the table `store` */

/*Table structure for table `store_data` */

DROP TABLE IF EXISTS `store_data`;

CREATE TABLE `store_data` (
  `id` varchar(36) NOT NULL,
  `store_id` varchar(36) DEFAULT NULL,
  `store_content` blob,
  PRIMARY KEY (`id`),
  KEY `index_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=8;

/*Data for the table `store_data` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
