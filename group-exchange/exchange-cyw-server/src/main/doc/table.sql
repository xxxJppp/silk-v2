-- 机器人订单表
CREATE TABLE `exchange_cyw_order` (
  `order_id` varchar(255) NOT NULL COMMENT '订单号，S开头的订单为星客机器人订单',
  `member_id` bigint(20) NOT NULL  COMMENT '会员ID',
  `amount` decimal(18,8) NOT NULL  COMMENT '交易数量',
  `direction` int(11)  NOT NULL  COMMENT '订单方向 0买，1卖',
  `price` decimal(18,8) NOT NULL  COMMENT '挂单价格',
  `symbol` varchar(255) NOT NULL  COMMENT '交易对',
  `type` int(11) NOT NULL  COMMENT '挂单类型，0市价，1限价',

  `coin_symbol` varchar(255) DEFAULT NULL COMMENT '交易币',
  `base_symbol` varchar(255) DEFAULT NULL COMMENT '结算币',

  `status` int(11) NOT NULL COMMENT '订单状态 0交易，1完成，2取消，3超时',

  `time` bigint(20) NOT NULL COMMENT '下单时间',
  `completed_time` bigint(20) DEFAULT NULL COMMENT '交易完成时间',
  `canceled_time` bigint(20) DEFAULT NULL COMMENT '交易取消时间',

  `traded_amount` decimal(19,8) DEFAULT '0.00000000' COMMENT '成交量',
  `turnover` decimal(19,8) DEFAULT '0.00000000' COMMENT '成交额，对市价买单有用',
  `freeze_amount` decimal(18,8) NOT NULL COMMENT '买入或卖出量 对应的 冻结币数量',
  PRIMARY KEY (`order_id`),
  KEY `index_exchange_cyw_order` (`member_id`,`symbol`,`status`) USING BTREE,
  KEY `complated_time_index` (`completed_time`),
  KEY `time` (`time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;