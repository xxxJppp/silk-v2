

INSERT  INTO `silk_data_dist`(`dict_id`,`dict_key`,`dict_val`,`dict_type`,`remark`,`status`,`sort`,`update_time`,`create_time`) 
VALUES 
  ('MEMBER_COMMISION_DISTRIBUTE','IS_OPEN','ON','java_lang_String','会员体系返佣发放开关',1,0,'2019-12-24 18:15:01','2019-12-24 18:15:03');
  
  
  INSERT  INTO `silk_data_dist`(`dict_id`,`dict_key`,`dict_val`,`dict_type`,`remark`,`status`,`sort`,`update_time`,`create_time`) 
VALUES 
  ('MEMBER_COMMISION_DISTRIBUTE','TURN_RATE','1.0001','java_math_BigDecimal','返佣币种均价上浮之后的结果',1,0,'2019-12-24 18:15:01','2019-12-24 18:15:03');
  
  
  

INSERT INTO `silk_data_dist` ( `dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time` )
VALUES
  ( 'SYSTEM_UNIT_CONFIG', 'TOKEN_EXCHANGE_FEE_COMMISION_UNIT', 'BT', 'java_lang_String', '会员体系币币交易手续费返还/佣币种', '1', '1', now( ), now( ) );
  
INSERT INTO `silk_data_dist` ( `dict_id`, `dict_key`, `dict_val`, `dict_type`, `remark`, `status`, `sort`, `update_time`, `create_time` )
VALUES
  ( 'SYSTEM_UNIT_CONFIG', 'MEMBER_RECOMMEND_COMMISION_UNIT', 'BT', 'java_lang_String', '会员购买/锁仓/直推会员费返佣币种', '1', '1', now( ), now( ) );