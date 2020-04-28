
-- 2020-02-13
-- 需求：币币交易 最小挂单数量，拆分为“最小买单挂单数量”和“最小卖单挂单数量”
-- 备份数据
create table exchange_coin_bak202002 like exchange_coin;
insert into exchange_coin_bak202002 select * from exchange_coin;

-- 增加字段“最小卖单挂单数量”
alter table exchange_coin
  add column `min_sell_amount` decimal(24,8) default 0 COMMENT '最小卖单挂单数量';

-- 修改备注：“最小挂单数量” 修改为“最小买单挂单数量”
alter table exchange_coin
  modify column `min_amount`  decimal(24,8) comment '最小买单挂单数量';

-- 初始化挂单手续费
update exchange_coin set min_sell_amount = min_amount;

