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
  `id` varchar(32) NOT NULL,
  `author_id` varchar(32) DEFAULT NULL,
  `author_name` varchar(100) DEFAULT NULL,
  `author_name_en` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `author` */

insert  into `author`(`id`,`author_id`,`author_name`,`author_name_en`) values ('1','1','方想','fangxiang'),('2','2','我爱吃西红柿','woaichixihongshi'),('3','3','辰东','chendong');

/*Table structure for table `book` */

DROP TABLE IF EXISTS `book`;

CREATE TABLE `book` (
  `id` varchar(32) NOT NULL,
  `book_id` varchar(32) DEFAULT NULL,
  `book_name` varchar(200) DEFAULT NULL,
  `book_name_en` varchar(200) DEFAULT NULL,
  `book_desc` varchar(500) DEFAULT NULL,
  `create_time` date DEFAULT NULL,
  `update_time` date DEFAULT NULL,
  `is_completion` int(11) DEFAULT '1',
  `author_id` varchar(32) DEFAULT NULL,
  `hits` bigint(20) DEFAULT '0',
  `image_url` varchar(200) DEFAULT NULL,
  `author_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `book` */

insert  into `book`(`id`,`book_id`,`book_name`,`book_name_en`,`book_desc`,`create_time`,`update_time`,`is_completion`,`author_id`,`hits`,`image_url`,`author_name`) values ('1','1','卡徒','katu','方想的小说的方式发大水发大水发的撒个飞洒发打发第三方撒的发生大发送到','2018-08-01','2018-08-03',0,'1',12,NULL,'方想'),('2','2','师士传说','shishichuanshuo','方想的小说范德萨发的所发生的访问额外丰富的水果防守打法撒地方萨芬撒范德萨发的撒噶是的范德萨发生的范德萨','2018-06-26','2018-08-01',1,'1',2,NULL,'方想'),('3','3','神墓','shenmu','辰东的小说爱是范德萨发生的发问发送到发送到','2018-04-04','2018-07-04',0,'3',15,NULL,'辰东');

/*Table structure for table `cata_book_relation` */

DROP TABLE IF EXISTS `cata_book_relation`;

CREATE TABLE `cata_book_relation` (
  `id` varchar(32) NOT NULL,
  `cata_id` varchar(32) DEFAULT NULL,
  `book_id` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `cata_book_relation` */

insert  into `cata_book_relation`(`id`,`cata_id`,`book_id`) values ('1','2','1'),('2','2','2'),('3','9','3');

/*Table structure for table `catagory` */

DROP TABLE IF EXISTS `catagory`;

CREATE TABLE `catagory` (
  `id` varchar(32) NOT NULL,
  `cata_id` varchar(32) DEFAULT NULL,
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
  `id` varchar(32) NOT NULL,
  `model_id` varchar(32) DEFAULT NULL,
  `model_name` varchar(200) DEFAULT NULL,
  `model_name_en` varchar(100) DEFAULT NULL,
  `order_desc` int(4) DEFAULT NULL,
  `model_url` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `model` */

insert  into `model`(`id`,`model_id`,`model_name`,`model_name_en`,`order_desc`,`model_url`) values ('1','1','新书','xinshu',1,'/html/hot.html'),('2','2','推荐','tuijian',2,'/html/recommend.html'),('3','3','完本','wanben',3,'/html/full.html'),('4','4','排行','paihang',4,'/html/top.html'),('5','5','作者','zuozhe',5,'/html/author.html');

/*Table structure for table `store` */

DROP TABLE IF EXISTS `store`;

CREATE TABLE `store` (
  `id` varchar(32) NOT NULL,
  `book_id` varchar(32) DEFAULT NULL,
  `store_id` varchar(32) DEFAULT NULL,
  `store_name` varchar(200) DEFAULT NULL,
  `store_url` varchar(200) DEFAULT NULL,
  `store_content` blob,
  `pre_store_id` varchar(32) DEFAULT NULL,
  `next_store_id` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `store` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
