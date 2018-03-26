drop table if not EXISTS  t_device;
cretae table t_device
(
id int primary key  comment '设备主键id',
mac varchar(200) comment 'mac地址'DEFAULT  ''
addTime bigint comment '添加时间'
);

drop table if not EXISTS  t_device_notify;
create table t_device_notify(
  id int primary key comment '主键id',
  deviceId int comment '设备id',
  notifyData varchar(4000) comment '上报数据内容'
  notifyTime bigint comment '上报时间'
);

drop table if not EXISTS  t_device_operlog;
create table t_device_operlog(

);
drop table if not EXISTS  t_device_exception;
create table t_device_exception(
  id int primary key comment '主键id',
  deviceId int comment '设备id',
  exceptionType int comment '异常类型',
  exceptionData varchar(4000) comment '上报数据内容'
  receiveTime bigint comment '上报时间'
);
