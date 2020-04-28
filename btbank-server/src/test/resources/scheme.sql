SET MODE = MySQL;

create table bt_bank_data_dict
(
    dict_id     varchar(100)  not null comment '配置编号',
    dict_key    varchar(100)  not null comment '配置KEY',
    dict_val    varchar(500)  not null comment '配置VALUE',
    dict_type   varchar(100)  not null comment '数据类型',
    remark      varchar(500)  null comment '描述',
    status      int default 1 not null comment '状态：0-失效 1-生效',
    sort        int default 0 not null comment '排序',
    update_time datetime      null comment '更新时间',
    create_time datetime      not null comment '添加时间',
    primary key (dict_id, dict_key)
);

create table bt_bank_miner_balance
(
    id                    bigint                            not null comment 'ID'
        primary key,
    member_id             bigint                            not null comment '用户ID',
    balance_amount        decimal(24, 8) default 0.00000000 null comment '可用余额',
    lock_amount           decimal(24, 8) default 0.00000000 null comment '锁仓余额',
    processing_reward_sum decimal(24, 8) default 0.00000000 null comment '进中的佣金统计',
    got_reward_sum        decimal(24, 8) default 0.00000000 null comment '已得到的佣金统计',
    create_time           datetime                          null comment '创建时间',
    update_time           datetime                          null comment '更新时间'
);

create table bt_bank_miner_balance_transaction
(
    id                   bigint                              not null comment 'ID'
        primary key,
    member_id            bigint           default 0          not null comment '用户ID',
    type                 tinyint unsigned default 0          not null comment '类型：1  转入，2 抢单本金转出，3 抢单佣金转入，4 抢单佣金转出，5 派单本金转出，6 派单佣金转入，7 派单佣金转出，8 转出，9 固定佣金转出，10 固定佣金转入，11 抢单锁仓，12 派单锁仓',
    money                decimal(24, 8)   default 0.00000000 not null comment '订单金额',
    balance              decimal(24, 8)   default 0.00000000 not null comment '当前余额',
    create_time          datetime                            not null comment '创建时间',
    order_transaction_id bigint           default 0          not null comment '订单流水ID 转入转出流水无此ID',
    ref_id               bigint           default 0          null comment '关联id',
    remark               varchar(255)     default ''         null comment '备注'
);

create table bt_bank_miner_order
(
    id                bigint                              not null
        primary key,
    upstream_order_id varchar(32)                         not null comment '上游订单ID',
    money             decimal(24, 8)   default 0.00000000 not null comment '订单金额',
    create_time       datetime                            null comment '创建时间',
    status            tinyint unsigned default 0          null comment '订单状态:(0 新订单，1 已抢单，2 已派单,3抢单结算完成，4，派单结算完成)',
    process_time      datetime                            null comment '操作时间',
    member_id         bigint           default 0          null comment '矿工ID'
);

create table bt_bank_miner_order_transaction
(
    id             bigint               not null comment 'ID'
        primary key,
    create_time    datetime             null comment '创建时间',
    miner_order_id bigint               not null comment '矿工订单ID',
    member_id      bigint               not null comment '矿工ID',
    reward_amount  decimal(24, 8)       not null comment '佣金金额',
    money          decimal(24, 8)       not null comment '金额',
    type           tinyint(3) default 0 not null comment '1,抢单，2派单，3，抢单结算，4，派单结算',
    unlock_time    datetime             null comment '解锁时间'
);


