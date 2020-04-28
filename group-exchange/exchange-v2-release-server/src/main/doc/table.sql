-- 规则配置表
CREATE TABLE `exchange_release_freeze_rule` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(32) NOT NULL COMMENT '交易对',
  `enable_buy` int(11) DEFAULT '1' COMMENT '启动买入规则：0=关闭，1=启动',
  `enable_sell` int(11) DEFAULT '1' COMMENT '启动卖出规则：0=关闭，1=启动',
  `freeze_duration` int(11) DEFAULT '0' COMMENT '买入成交后冻结时间（单位：小时）',
  `rate_buy_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '释放规则：买入数量',
  `rate_release_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '释放规则：释放数量',
  `sell_min_increment` decimal(24,8) DEFAULT '0.00000000' COMMENT '卖出价最小递增价格',
  `sell_max_trade_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '每个卖价最大交易数量',
  `create_time` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP COMMENT '创建日期',
  `update_time` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='币币交易释放与冻结-规则配置表';


-- 锁仓释放总数表
CREATE TABLE `exchange_release_wallet` (
  `id` varchar(32)  NOT NULL COMMENT 'ID, memberId:coinSymbol',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `coin_symbol` varchar(64) DEFAULT NULL COMMENT '币种名称',
  `lock_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '待释放数量',
  `create_time` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '创建日期',
  `update_time` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='币币交易释放-锁仓释放总数表';


-- 锁仓明细表
CREATE TABLE `exchange_release_lock_record` (
  `id` bigint(20)  NOT NULL COMMENT 'ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `coin_symbol` varchar(64) DEFAULT NULL COMMENT '币种名称',
  `amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '锁仓数量',
  `ref_id` varchar(64)  COMMENT '关联的充值流水ID',
  `create_time` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '创建日期',
  `update_time` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='币币交易释放-锁仓明细表';

-- 释放任务表
CREATE TABLE `exchange_release_task` (
  `id` bigint(20)  NOT NULL COMMENT 'ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `coin_symbol` varchar(64) DEFAULT NULL COMMENT '币种名称',
  `type` int(11) NOT NULL COMMENT '类型：0=锁仓释放，1=冻结释放',
  `amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '释放数量',
  `ref_id` varchar(64)  COMMENT '关联的账户流水ID',
  `release_status` int(11) NOT NULL COMMENT '释放状态：0=未释放,1=已释放',
  `release_time` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '释放时间',
  `create_time`  TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '创建日期',
  `update_time`  TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='币币交易释放-释放任务表';

-- exchange_release_task 索引优化
ALTER TABLE exchange_release_task ADD INDEX index_exchange_release_task_rr (release_status,release_time);

-- exchange_order 索引优化
ALTER TABLE exchange_order ADD INDEX index_exchange_order_sdtp (symbol,direction,type,price);


-- ESP邀请奖励 2020-01-17
-- 推荐人闪兑订单表
CREATE TABLE `exchange_release_referrer_order` (
  `ref_order_id` varchar(64) DEFAULT NULL COMMENT '订单ID，PK',
  `ref_symbol` varchar(64) DEFAULT NULL COMMENT '交易对',
  `coin_symbol` varchar(64) DEFAULT NULL COMMENT '交易币',
  `base_symbol` varchar(64) DEFAULT NULL COMMENT '结算币',

  `ref_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '计划下单数量',
  `ref_place_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '实际下单数量',
  `invitee_freeze_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '被推荐人闪兑冻结币数量',

  `inviter_id` bigint(20) DEFAULT NULL COMMENT '推荐人用户ID',
  `invitee_id` bigint(20) DEFAULT NULL COMMENT '被推荐人用户ID',
  `status` int(11) NOT NULL COMMENT '订单状态：0=交易中，1=待完成，2=已完成',

  `rate` decimal(24,8) DEFAULT '0.00000000' COMMENT '成交均价',
  `freeze_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '计划闪兑数量',
  `traded_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '实际闪兑数量',
  `traded_turnover` decimal(24,8) DEFAULT '0.00000000' COMMENT '实际闪兑额',

  `inviter_fee` decimal(24,8) DEFAULT '0.00000000' COMMENT '推荐人手续费（基币）',
  `invitee_fee` decimal(24,8) DEFAULT '0.00000000' COMMENT '被推荐人手续费（交易币）',

  `create_time`  TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '创建日期',
  `update_time`  TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '更新日期',
  PRIMARY KEY (`ref_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='币币交易-推荐人闪兑订单表';

-- 推荐人奖励任务表
CREATE TABLE `exchange_release_award_task` (
  `id` bigint(20)  NOT NULL COMMENT 'ID',
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `award_symbol` varchar(64) DEFAULT NULL COMMENT '奖励币种',

  `ref_id` varchar(64)  COMMENT '关联ID',
  `ref_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '参考数量',
  `invitee_id` bigint(20) DEFAULT NULL COMMENT '被推荐人用户ID',
  `amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '奖励币数量',

  `type` int(11) NOT NULL COMMENT '任务类型：0=手续费返佣、1=直推用户买币累计奖励',
  `status` int(11) NOT NULL COMMENT '奖励状态：0=未处理,1=处理中,2=已处理',

  `release_time` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '奖励到账时间',
  `create_time`  TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '创建日期',
  `update_time`  TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '更新日期',
  `remark` varchar(512)  COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='币币交易-推荐人奖励任务表';

-- 推荐人累计买入奖励累计表
CREATE TABLE `exchange_release_award_total` (
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `symbol` varchar(64) DEFAULT NULL COMMENT '币种',
  `total_buy_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '直推会员累计买入数量',
  `total_award_times` int(11) DEFAULT 0 COMMENT '累计已发放奖励次数',
  `total_award_amount` decimal(24,8) DEFAULT '0.00000000' COMMENT '累计已发放奖励',
  `create_time`  TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '创建日期',
  `update_time`  TIMESTAMP NOT NULL default CURRENT_TIMESTAMP  COMMENT '更新日期',
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='币币交易-推荐人累计买入奖励累计表';


-- 配置参数，需要修改第一个参数
INSERT INTO `silk_data_dist` (`dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time`) VALUES ('exchange-release', 'award-account', '0000', 'java_lang_Long', '归集总帐号：奖励赠送账户', '1', '1', '2020-01-21 15:28:24', '2020-01-21 15:28:24');
INSERT INTO `silk_data_dist` (`dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time`) VALUES ('exchange-release', 'buy-max-exchange-ratio', '0.5', 'java_math_BigDecimal', '闪兑比例：买单最大闪兑比例，默认为0.5，用小数表示', '1', '2', '2020-01-21 15:28:24', '2020-01-21 15:28:24');
INSERT INTO `silk_data_dist` (`dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time`) VALUES ('exchange-release', 'exchange-rate', '0.01', 'java_math_BigDecimal', '闪兑手续费：闪兑手续费比例，默认为0.01，用小数表示', '1', '3', '2020-01-21 15:28:24', '2020-01-21 15:28:24');
INSERT INTO `silk_data_dist` (`dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time`) VALUES ('exchange-release', 'fee-award-ratio', '0.8', 'java_math_BigDecimal', '手续费奖励比例：交易手续费的奖励比例，默认为0.8，用小数表示', '1', '4', '2020-01-21 15:28:24', '2020-01-21 15:28:24');
INSERT INTO `silk_data_dist` (`dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time`) VALUES ('exchange-release', 'accumulation-buy-total-amount', '5000', 'java_math_BigDecimal', '累计奖购买数量：直推用户累计购买ESP的数量', '1', '5', '2020-01-21 15:28:24', '2020-01-21 15:28:24');
INSERT INTO `silk_data_dist` (`dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time`) VALUES ('exchange-release', 'accumulation-buy-award-amount', '40', 'java_math_BigDecimal', '累计奖励数量：直推用户达到累计奖购买数量后，奖励推荐人ESP的数量', '1', '6', '2020-01-21 15:28:24', '2020-01-21 15:28:24');

