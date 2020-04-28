/*
SQLyog Professional v12.08 (32 bit)
MySQL - 5.7.28-log : Database - test3
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`test3` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `test3`;

/*Table structure for table `exchange_coin_extend` */

DROP TABLE IF EXISTS `exchange_coin_extend`;

CREATE TABLE `exchange_coin_extend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(32) NOT NULL COMMENT '交易对',
  `buy_fee_discount` decimal(24,8) NOT NULL COMMENT '买入吃单手续费折扣率',
  `sell_fee_discount` decimal(24,8) NOT NULL COMMENT '卖出吃单手续费费率',
  `entrust_buy_discount` decimal(24,8) NOT NULL COMMENT '挂单买入手续费费率',
  `entrust_sell_discount` decimal(24,8) NOT NULL COMMENT '挂单卖出手续费费率',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='币币交易-交易对扩展表';

/*Data for the table `exchange_coin_extend` */

/*Table structure for table `member_benefits_extends` */

DROP TABLE IF EXISTS `member_benefits_extends`;

CREATE TABLE `member_benefits_extends` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) NOT NULL,
  `level_id` int(4) NOT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员扩展表，与原member表一对一';

/*Data for the table `member_benefits_extends` */

/*Table structure for table `member_benefits_order` */

DROP TABLE IF EXISTS `member_benefits_order`;

CREATE TABLE `member_benefits_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_extend_id` bigint(20) NOT NULL,
  `order_number` varchar(128) DEFAULT NULL COMMENT '订单号',
  `operate_type` int(2) NOT NULL COMMENT '操作类型\r\n10-续费\r\n20-升级\r\n30-开通',
  `origin_level` int(2) NOT NULL COMMENT '原会员等级',
  `dest_level` int(2) NOT NULL COMMENT '新会员等级',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
  `pay_type` int(2) NOT NULL COMMENT '支付类型\r\n10-购买\r\n20-锁仓',
  `amount` decimal(24,8) NOT NULL COMMENT '费用',
  `unit` varchar(32) NOT NULL COMMENT '费用的币种单位',
  `app_id` int(4) NOT NULL COMMENT '客户端来源',
  `pay_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员申请订单';

/*Data for the table `member_benefits_order` */

/*Table structure for table `member_benefits_setting` */

DROP TABLE IF EXISTS `member_benefits_setting`;

CREATE TABLE `member_benefits_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level_id` int(4) NOT NULL,
  `buy_discount` decimal(24,8) NOT NULL COMMENT '吃买单折扣率',
  `sell_discount` decimal(24,8) NOT NULL COMMENT '吃卖单折扣率',
  `entrust_buy_discount` decimal(24,8) NOT NULL COMMENT '挂买单折扣率',
  `entrust_sell_discount` decimal(24,8) NOT NULL COMMENT '挂卖单折扣率',
  `vip1_buy_discount` decimal(24,8) DEFAULT NULL COMMENT '推荐的人以购买方式成为vip1-返佣比例',
  `vip1_lock_discount` decimal(24,8) DEFAULT NULL COMMENT '推荐的人以锁仓方式成为vip1-返佣比例',
  `vip2_buy_discount` decimal(24,8) DEFAULT NULL COMMENT '推荐的人以购买方式成为vip2-返佣比例',
  `vip2_lock_discount` decimal(24,8) DEFAULT NULL COMMENT '推荐的人以锁仓方式成为vip2-返佣比例',
  `vip3_buy_discount` decimal(24,8) DEFAULT NULL COMMENT '推荐的人以购买方式成为vip3-返佣比例',
  `vip3_lock_discount` decimal(24,8) DEFAULT NULL COMMENT '推荐的人以锁仓方式成为vip3-返佣比例',
  `agent_buy_discount` decimal(24,8) DEFAULT NULL COMMENT '推荐的人以购买方式成为经纪人-返佣比例',
  `agent_lock_discount` decimal(24,8) DEFAULT NULL COMMENT '推荐的人以锁仓方式成为经纪人-返佣比例',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员权益表';

/*Data for the table `member_benefits_setting` */

/*Table structure for table `member_fee_day_stat` */

DROP TABLE IF EXISTS `member_fee_day_stat`;

CREATE TABLE `member_fee_day_stat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `statistic_date` date NOT NULL COMMENT '时间yyyy-MM-dd',
  `unit` varchar(32) NOT NULL COMMENT '币种',
  `buy_unit_quantity` decimal(24,8) NOT NULL COMMENT '购买方式的费用总数',
  `lock_unit_quantity` decimal(24,8) NOT NULL COMMENT '锁仓方式的费用总数',
  `buy_count` bigint(20) NOT NULL COMMENT '购买人次',
  `lock_count` bigint(20) NOT NULL COMMENT '锁仓人次',
  `buy_commision` decimal(24,8) NOT NULL COMMENT '购买费用的总返佣数量',
  `lock_commision` decimal(24,8) NOT NULL COMMENT '锁仓方式费用的总返佣数量',
  `unlock_unit_quantity` decimal(24,8) NOT NULL COMMENT '锁仓到期释放的总币数',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '记录版本，高并发使用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员购买情况日统计';

/*Data for the table `member_fee_day_stat` */

/*Table structure for table `member_level` */

DROP TABLE IF EXISTS `member_level`;

CREATE TABLE `member_level` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `name_zh` varchar(64) DEFAULT NULL COMMENT '会员等级名称-简体中文',
  `name_zh_tw` varchar(64) DEFAULT NULL COMMENT '会员等级名称-中文繁体',
  `name_en` varchar(512) DEFAULT NULL COMMENT '会员等级名称-英文',
  `name_ko` varchar(512) DEFAULT NULL COMMENT '会员等级名称-韩语',
  `upgrade_range` varchar(512) DEFAULT NULL COMMENT '可升级等级范围',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员等级';

/*Data for the table `member_level` */

/*Table structure for table `member_recommend_commision` */

DROP TABLE IF EXISTS `member_recommend_commision`;

CREATE TABLE `member_recommend_commision` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ref_id` varchar(64) NOT NULL COMMENT '关联单据',
  `deliver_to_member_id` bigint(20) NOT NULL COMMENT '推荐人',
  `order_member_id` bigint(20) NOT NULL COMMENT '交易会员',
  `invite_level` int(11) NOT NULL COMMENT '邀请层级',
  `commision_unit` varchar(32) NOT NULL COMMENT '佣金币种（平台币）',
  `commision_quantity` decimal(24,8) NOT NULL COMMENT '佣金数量(以平台币计)',
  `platform_unit_cny_rate` decimal(24,8) NOT NULL COMMENT '平台币与CNY的汇率',
  `distribute_status` int(2) NOT NULL COMMENT '发放状态\r\n10-未发放\r\n20-已发放',
  `distribute_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '发放时间',
  `accumulative_quantity` decimal(24,8) NOT NULL COMMENT '累计未发数量（以平台币计）',
  `transfer_id` bigint(20) DEFAULT NULL COMMENT '转账记录id',
  `platform_unit_rate` decimal(24,8) NOT NULL COMMENT '手续费币种与平台币的汇率',
  `biz_type` int(2) NOT NULL COMMENT '10-会员锁仓\r\n20-会员购买\r\n30-币币交易',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员邀请佣金';

/*Data for the table `member_recommend_commision` */

/*Table structure for table `member_recommend_commision_setting` */

DROP TABLE IF EXISTS `member_recommend_commision_setting`;

CREATE TABLE `member_recommend_commision_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `level_id` int(4) NOT NULL,
  `recommend_level` int(11) NOT NULL COMMENT '推荐层次\r\n0-不限层次',
  `commision_ratio` decimal(24,8) NOT NULL COMMENT '折扣率',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='推荐层次折扣配置';

/*Data for the table `member_recommend_commision_setting` */

/*Table structure for table `member_require_condition` */

DROP TABLE IF EXISTS `member_require_condition`;

CREATE TABLE `member_require_condition` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `level_id` int(4) NOT NULL,
  `type` int(2) NOT NULL COMMENT '类型\r\n10-购买\r\n20-锁仓\r\n30-建立社区',
  `unit` varchar(32) DEFAULT NULL COMMENT '币种',
  `quantity` decimal(24,8) NOT NULL COMMENT '费用数量',
  `duration` int(11) DEFAULT NULL COMMENT '开通时长，只能是30的倍数',
  `flag_discount` tinyint(4) NOT NULL COMMENT '此类型是否折扣开关\r\n1-开\r\n0-关',
  `discount` decimal(24,8) DEFAULT NULL COMMENT '折扣率',
  `condition_id` int(4) DEFAULT NULL COMMENT '关联条件ID',
  `condition_relationship` int(2) DEFAULT NULL COMMENT '与关联条件的关系\r\n10-AND\r\n20-OR',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员申请条件';

/*Data for the table `member_require_condition` */

/*Table structure for table `member_rule_descr` */

DROP TABLE IF EXISTS `member_rule_descr`;

CREATE TABLE `member_rule_descr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_type` varchar(512) NOT NULL COMMENT '规则类型',
  `content_zh` text(0) DEFAULT NULL COMMENT '内容-中文',
  `content_zh_tw` text(0) DEFAULT NULL COMMENT '内容-中文繁体',
  `content_en` text(0) DEFAULT NULL COMMENT '内容-英文',
  `content_ko` text(0) DEFAULT NULL COMMENT '内容-韩文',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员规则';

/*Data for the table `member_rule_descr` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
