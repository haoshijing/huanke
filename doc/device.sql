drop table if not EXISTS  t_device;
cretae table t_device
(
id int primary key  comment '设备主键id',
mac varchar(200) comment 'mac地址'DEFAULT  ''
deviceTypeId int comment '设备类型',
addTime bigint comment '添加时间',
);
drop table if not EXISTS  t_device_attribute;
create table t_device_attribute(
deviceId int comment '设备Id',
attributeVal varchar(2048) comment '以json的方式记录'
)

drop table if not EXISTS  t_device_notify;
create table t_device_notify(
  id int primary key comment '主键id',
  deviceId int comment '设备id',
  notifyData varchar(4000) comment '上报数据内容'
  notifyTime bigint comment '上报时间'
);

drop table if not EXISTS  t_device_operlog;
create table t_device_operlog(
  id int PRIMARY  key comment '主键id',
  deviceId int comment '设备id',
  operDirective int comment ''
);
drop table if not EXISTS  t_device_exception;
create table t_device_exception(
  id int primary key comment '主键id',
  deviceId int comment '设备id',
  exceptionType int comment '异常类型',
  exceptionData varchar(4000) comment '上报数据内容'
  receiveTime bigint comment '上报时间'
);


drop table if not EXISTS  t_device_operlog;
create table t_device_operlog(
  id int PRIMARY  key comment '主键id',
  deviceId int comment '设备id',
  operDirective int comment ''
);

drop table if not EXISTS  t_product;
create table t_product(
  id int PRIMARY  key comment '主键id',
  productKey varchar comment 'PK',
  productName varchar comment '产品名称',
  insertTime bigint comment '写入时间',
  lastUpdateTime bigint comment '最后修改时间'
);

drop table if not EXISTS t_project ;
create table t_project(
  id int PRIMARY  key comment '主键id',
  projectSn varchar comment '项目序列号',
  projectName varchar comment '项目名称',
  insertTime bigint comment '写入时间',
  lastUpdateTime bigint comment '最后修改时间'
);

create table if not EXISTS t_device_type(
id int primary key comment '主键id',
deviceTypeName varchar(200) comment '设备类型名称',
functionList varchar(200) comment '功能列表'
insertTime bigint comment '写入时间'
)


mac deviceTypeId productKey projectKey ps

