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
deviceSn varchar(200) comment '设备序列号' DEFAULT  '',
productId int comment '产品id',
projectId int comment '项目id',
deviceTypeId int comment '设备类型',
isBind int comment '绑定状态1-初始化2-已绑定3-已解绑',
insertTime bigint comment '添加时间',
lastUpdateTime bigint comment '最后修改时间',
bindTime bigint commnt '绑定和解绑时间'
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


drop table  if EXISTS t_app_user;
create table t_app_user(
  id int primary key auto_increment comment '主键id',
  openId varchar(100) comment 'openId',
  sex int comment '性别',
  province varchar(100) comment '省',
  city	varchar(100) comment '城市',
  nickname varchar(100) comment '昵称',
  unionid varchar(100) comment 'unionId',
  headimgurl varchar(200) comment '头像',
  createTime bigint comment '创建时间',
  lastUpdateTime bigint comment '最后修改时间',
  lastVisitTime bigint comment '最后访问时间'
);

drop table  if  EXISTS  t_device_attribute;
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
  operDirective int comment '',
  operValue int comment '操作值'
  operFrom int comment '操作源1-用户,2-管理员',
  operValue int comment '操作人的实体值',
  createTime bigint comment '操作时间'
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
deviceIds varchar(1024) comment '设备idList',
createTime bigint comment '创建时间',
lastUpdateTime bigint comment '最后修改时间'
);

mac deviceTypeId productKey projectKey ps

