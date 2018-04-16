CREATE DATABASE iot DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
drop TABLE IF EXISTS t_admin;
create table t_admin
(
id int primary key auto_increment comment '主键id',
userName varchar(200) comment '用户名',
password varchar(200) comment '密码',
saltPassword varchar(200) comment '加密盐值',
insertTime bigint comment '写入时间',
lastUpdateTime bigint comment '最后修改时间',
status int comment '账号状态'
) comment '管理员表';

drop table if  EXISTS  t_device;
create table t_device
(
id int primary key auto_increment comment '设备主键id',
mac varchar(200) comment 'mac地址'DEFAULT  '',
name varchar(200) comment '项目名称' DEFAULT  '',
deviceId varchar(200) comment '微信生成的设备id'DEFAULT  '',
devicelicence varchar(200) comment '设备序列号' DEFAULT  '',
productId int comment '产品id',
projectId int comment '项目id',
deviceTypeId int comment '设备类型',
bindStatus int comment '绑定状态1-初始化2-已绑定3-已解绑',
createTime bigint comment '添加时间',
lastUpdateTime bigint comment '最后修改时间',
bindTime bigint comment '绑定和解绑时间'
);

drop table if  EXISTS  t_device_type;
create table t_device_type
(
id int primary key auto_increment comment '类型主键',
name varchar(200) comment '类型名称' DEFAULT  '',
icon varchar(200) comment '类型图标'DEFAULT  '',
funcList varchar(200) comment '功能列表',
createTime bigint comment '创建时间',
lastUpdateTime bigint comment '最后修改时间'
);


drop table if  EXISTS  t_device_group_item;
create table t_device_group_item
(
id int primary key auto_increment comment '设备主键id',
userId varchar(200) comment '用户id' DEFAULT  '',
deviceId varchar(200) comment '设备id'DEFAULT  '',
groupId int comment '组id',
status int comment '绑定状态',
createTime bigint comment '添加时间',
lastUpdateTime bigint comment '最后修改时间',
bindTime bigint comment '绑定和解绑时间'
);


drop table if  EXISTS  t_product;
create table t_product
(
id int primary key  comment '产品id',
name varchar(200) comment '产品名称' DEFAULT  '',
insertTime bigint comment '添加时间',
lastUpdateTime bigint comment '最后修改时间'
);

drop table if  EXISTS  t_project;
create table t_project
(
id int primary key  comment '项目id',
name varchar(200) comment '项目名称' DEFAULT  '',
insertTime bigint comment '添加时间',
lastUpdateTime bigint comment '最后修改时间'
);
drop table  if EXISTS t_device_data_alarm;
create table t_device_data_alarm(
  id int primary key auto_increment comment '主键id',
  deviceId int comment '设备id',
  indexVal int comment '设备下标',
  type	int comment '报警类型',
  value int comment '报警值',
  dealStatus int comment '处理状态',
  dealTime int comment '处理时间',
  createTime bigint comment '创建时间'
);
drop table  if EXISTS t_device_data_sensor;
create table t_device_data_sensor(
  id int primary key auto_increment comment '主键id',
  deviceId int comment '设备id',
  sensorType int comment '传感器数据类型',
  sensorValue varchar comment '传感器数值',
  createTime bigint comment '创建时间'
);

drop table  if EXISTS t_device_data_location;
create table t_device_data_location(
  id int primary key auto_increment comment '主键id',
  deviceId int comment '设备id',
  wifi int comment 'wifi',
  grps int comment 'grps',
  gps int comment 'gps',
  blutooth int comment 'blutooth',
  extFields int comment '',
  createTime bigint comment '创建时间'
);

drop table  if EXISTS t_device_data_timer;
create table t_device_data_timer(
  id int primary key auto_increment comment '主键id',
  deviceId int comment '设备id',
  timingOn int comment '开机',
  timingOff int comment '关机',
  hepa varchar(2048) comment '滤网',
  acticarbon varchar(2048) comment '活性炭',
  createTime bigint comment '创建时间'
);


drop table  if EXISTS t_device_data_control;
create table t_device_data_control(
  id int primary key auto_increment comment '主键id',
  deviceId int comment '设备id',
  mode int comment '模式',
  devicelock int comment '设备锁',
  childlock int comment '童锁',
  anion int comment '负离子',
  uvl int comment '灭菌灯',
  heater int comment '加热器',
  fan varchar(2047) comment '风机',
  valve varchar(2047) comment '循环阀',
  createTime bigint comment '创建时间'
);

drop table  if EXISTS t_app_user;
create table t_app_user(
  id int primary key auto_increment comment '主键id',
  openId varchar(100) comment 'openId',
  sex int comment '性别',
  province varchar(100) comment '省',
  city varchar(100) comment '城市',
  nickname varchar(100) comment '昵称',
  unionid varchar(100) comment 'unionId',
  headimgurl varchar(200) comment '头像',
  createTime bigint comment '创建时间',
  lastUpdateTime bigint comment '最后修改时间',
  lastVisitTime bigint comment '最后访问时间'
);



drop table if not EXISTS  t_device_operlog;
create table t_device_operlog(
  id int PRIMARY  key comment '主键id',
  deviceId int comment '设备id',
  operDirective int comment '',
  operValue int comment '操作值'
  operFrom int comment '操作源1-用户,2-管理员',
  operValue int comment '操作人的实体值',
  createTime bigint comment '操作时间'
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


drop table if  EXISTS  t_user;
create table t_user(
id int primary key auto_increment comment '主键id',
userName varchar(100) comment '用户名',
password varchar(100) comment '密码',
realName varchar(100) comment '真实姓名',
createTime bigint comment '创建时间',
lastUpdateTime bigint comment '最后修改时间'
);

drop table if  EXISTS  t_role;
create table t_role(
id int primary key auto_increment comment '主键id',
roleName varchar(100) comment '角色名称',
createTime bigint comment '创建时间',
lastUpdateTime bigint comment '最后修改时间'
);

drop table if EXISTS t_device_group;
create table t_device_group(
id int primary key auto_increment comment '主键id',
groupName varchar(100) comment '设备组名称',
userId int comment '用户id',
createTime bigint comment '创建时间',
lastUpdateTime bigint comment '最后修改时间',
status int comment '状态1-正常,2-已删除'
);

drop table if EXISTS t_device_group_item;
create table t_device_group_item(
id int primary key auto_increment comment '主键id',
deviceId int comment '设备id',
userId int comment '用户id',
groupId int comment '设备组id',
createTime bigint comment '创建时间',
lastUpdateTime bigint comment '最后修改时间',
status int comment '状态1-绑定,2-已解绑'
);

drop table if EXISTS t_device_func;
create table t_device_func(
id int primary key auto_increment comment '主键id',
name varchar(512) comment '功能类型',
valueRange varchar(512) comment '值的范围',
valueType varchar(20) comment '值的类型',
createTime bigint comment '创建时间',
) comment '设备功能表';



