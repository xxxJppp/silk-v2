
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
) ENGINE=InnoDB AUTO_INCREMENT=262678 DEFAULT CHARSET=utf8 COMMENT='会员扩展表，与原member表一对一';



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
  `order_mq_id` varchar(128) DEFAULT NULL,
  `lock_detail_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=924 DEFAULT CHARSET=utf8 COMMENT='会员申请订单';


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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='会员权益表';



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
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8 COMMENT='会员购买情况日统计';



CREATE TABLE `member_level` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `name_zh` varchar(64) DEFAULT NULL COMMENT '会员等级名称-简体中文',
  `name_zh_tw` varchar(64) DEFAULT NULL COMMENT '会员等级名称-中文繁体',
  `name_en` varchar(512) DEFAULT NULL COMMENT '会员等级名称-英文',
  `name_ko` varchar(512) DEFAULT NULL COMMENT '会员等级名称-韩语',
  `upgrade_range` varchar(512) DEFAULT NULL COMMENT '可升级等级范围',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_default` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='会员等级';



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
  `biz_type` int(2) NOT NULL COMMENT '10-会员购买\r\n20-会员锁仓\r\n30-币币交易',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mq_msg_id` varchar(512) DEFAULT NULL COMMENT '消息ID',
  `commision_usdt_qty` decimal(20,8) DEFAULT NULL COMMENT '本次返佣折合成USDT后的数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3452 DEFAULT CHARSET=utf8 COMMENT='会员邀请佣金';



CREATE TABLE `member_recommend_commision_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `level_id` int(4) NOT NULL,
  `recommend_level` int(11) NOT NULL COMMENT '推荐层次\r\n0-不限层次',
  `commision_ratio` decimal(24,8) NOT NULL COMMENT '折扣率',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8 COMMENT='推荐层次折扣配置';



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
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COMMENT='会员申请条件';


CREATE TABLE `member_rule_descr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_type` varchar(512) NOT NULL COMMENT '规则类型',
  `content_zh` text COMMENT '内容-中文',
  `content_zh_tw` text COMMENT '内容-中文繁体',
  `content_en` text COMMENT '内容-英文',
  `content_ko` text COMMENT '内容-韩文',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='会员规则';


INSERT INTO `member_rule_descr`(`rule_type`, `content_zh`, `content_zh_tw`, `content_en`, `content_ko`, `create_time`, `update_time`) VALUES ('会员规则', '', '', '', '', now(), now());
INSERT INTO `member_rule_descr`(`type`, `content_zh`, `content_zh_tw`, `content_en`, `content_ko`, `create_time`, `update_time`) VALUES ('注册邀请规则', '', '', '', '', now(), now());


INSERT INTO member_level (id,name_zh,name_ko,name_en,name_zh_tw,upgrade_range) VALUES (1,'普通会员','일반 회원','Regular','普通會員','2,3,4,5'),(2,'VIP1','VIP1','VIP1','VIP1','3,4,5'),(3,'VIP2','VIP2','VIP2','VIP2','4,5'),(4,'VIP3','VIP3','VIP3','VIP3','5'),(5,'经纪人','브로커','Broker','經紀人','');

-- 2019-12-18新增
INSERT INTO `silk_data_dist` ( `dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time` )
VALUES
	( 'SYSTEM_UNIT_CONFIG', 'TOKEN_EXCHANGE_FEE_COMMISION_UNIT', 'BT', 'java_lang_String', '会员体系币币交易手续费返还/佣币种', '1', '1', now( ), now( ) );
INSERT INTO `silk_data_dist` ( `dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time` )
VALUES
	( 'SYSTEM_UNIT_CONFIG', 'MEMBER_RECOMMEND_COMMISION_UNIT', 'BT', 'java_lang_String', '会员购买/锁仓/直推会员费返佣币种', '1', '1', now( ), now( ) );
INSERT  INTO `silk_data_dist`(`dict_id`,`dict_key`,`dict_val`,`dict_type`,`remark`,`status`,`sort`,`update_time`,`create_time`)
VALUES
  ('MEMBER_COMMISION_DISTRIBUTE','IS_OPEN','ON','java_lang_String','会员体系返佣发放开关',1,0,'2019-12-24 18:15:01','2019-12-24 18:15:03');


INSERT  INTO `silk_data_dist`(`dict_id`,`dict_key`,`dict_val`,`dict_type`,`remark`,`status`,`sort`,`update_time`,`create_time`)
VALUES
  ('MEMBER_COMMISION_DISTRIBUTE','TURN_RATE','1.0001','java_math_BigDecimal','返佣币种均价上浮之后的结果',1,0,'2019-12-24 18:15:01','2019-12-24 18:15:03');
-- //2019-12-27新增
INSERT INTO `silk_data_dist` ( `dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time` )
VALUES
  ( 'MEMBER_SYSTEM_CONFIG', 'TOTAL_ACCOUNT_ID', '360641', 'java_lang_Long', '会员费归集/返佣账号', '1', '1', now( ), now( ) );
INSERT INTO `silk_data_dist` ( `dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time` )
VALUES
  ( 'MEMBER_SYSTEM_CONFIG', 'ACCOUNT_PHONE_NUMBER', '13638211877', 'java_lang_String', '会员体系返佣余额告警手机', '1', '1', now( ), now( ) );

INSERT IGNORE into member_benefits_extends (member_id, level_id, start_time, end_time )  SELECT member_id, 5, now(), (SELECT DATE_ADD(NOW(),INTERVAL 180 DAY))  FROM super_partner_community where usable = 1;
