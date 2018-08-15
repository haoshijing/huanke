
-- ----------------------------
-- Table structure for t_customer
-- ----------------------------
DROP TABLE IF EXISTS `t_customer`;
CREATE TABLE `t_customer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200)  DEFAULT NULL,
  `loginname` varchar(100)  DEFAULT NULL,
  `userType` int(1) NULL DEFAULT NULL,
  `remark` varchar(2000)  DEFAULT NULL,
  `publicName` varchar(200)  DEFAULT NULL,
  `publicId` varchar(200)  DEFAULT NULL,
  `appid` varchar(200)  DEFAULT NULL,
  `appsecret` varchar(200)  DEFAULT NULL,
  `SLD` varchar(100)  DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  `creatUser` varchar(100)  DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '客户表  有公众号的表，B端' ;

-- ----------------------------
-- Table structure for t_customer_user
-- ----------------------------
DROP TABLE IF EXISTS `t_customer_user`;
CREATE TABLE `t_customer_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openId` varchar(100)  DEFAULT NULL,
  `nickname` varchar(200)  DEFAULT NULL,
  `unionid` varchar(100)  DEFAULT NULL,
  `headimgurl` varchar(200)  DEFAULT NULL,
  `sex` int(1) NULL DEFAULT NULL,
  `province` varchar(100)  DEFAULT NULL,
  `city` varchar(100)  DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  `lastVisitTime` bigint(20) NULL DEFAULT NULL,
  `mac` varchar(100)  DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '终端用户表（C端用户）' ;

-- ----------------------------
-- Table structure for t_device
-- ----------------------------
DROP TABLE IF EXISTS `t_device`;
CREATE TABLE `t_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200)  COMMENT '设备名称',
  `mac` varchar(200)  COMMENT 'mac地址',
  `deviceId` varchar(200)  COMMENT '微信生成的设备id',
  `devicelicence` varchar(200)  COMMENT '微信生成的序列号',
  `imei` varchar(200)  COMMENT 'imei',
  `imsi` varchar(200)  COMMENT 'imsi',
  `saNo` varchar(200)  COMMENT '序列号',
  `typeId` int(11) DEFAULT NULL COMMENT '设备类型',
  `productId` int(11) DEFAULT NULL COMMENT '微信生成的设备类型的productid',
  `onlineStatus` int(1) DEFAULT NULL COMMENT '在线状态',
  `bindStatus` int(1) DEFAULT NULL COMMENT '绑定状态',
  `bindTime` bigint(20) DEFAULT NULL COMMENT '绑定时间',
  `enableStatus` int(1) DEFAULT NULL COMMENT '启用状态',
  `workStatus` int(1) DEFAULT NULL COMMENT '工作状态',
  `ip` varchar(200)  COMMENT '机器ip',
  `speedConfig` varchar(4096)  COMMENT '转速',
  `version` varchar(300)  COMMENT '版本',
  `createTime` bigint(20) DEFAULT NULL COMMENT '写入时间',
  `lastUpdateTime` bigint(20) DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE
) COMMENT='设备表 （基础属性）';
-- ----------------------------
-- Table structure for t_device_ablity
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity`;
CREATE TABLE `t_device_ablity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ablityName` varchar(1024)  DEFAULT NULL COMMENT '能力名称',
  `dirValue` varchar(1024)  DEFAULT NULL COMMENT '通讯对应指令',
  `createTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备能力表' ;

-- ----------------------------
-- Table structure for t_device_ablity_option
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_option`;
CREATE TABLE `t_device_ablity_option`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `optionName` varchar(200)  DEFAULT NULL,
  `ablityId` varchar(11)  DEFAULT NULL COMMENT '通讯对应指令',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpaddateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备能力选项表' ;

-- ----------------------------
-- Table structure for t_device_ablity_set
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_set`;
CREATE TABLE `t_device_ablity_set`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100)  DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  `remark` varchar(500)  DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '功能集' ;

-- ----------------------------
-- Table structure for t_device_ablity_set_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_set_relation`;
CREATE TABLE `t_device_ablity_set_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ablityId` int(11) NULL DEFAULT NULL,
  `ablitySetId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '功能集 和能力 关联关系表' ;

-- ----------------------------
-- Table structure for t_device_alarm
-- ----------------------------
DROP TABLE IF EXISTS `t_device_alarm`;
CREATE TABLE `t_device_alarm`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备告警信息' ;

-- ----------------------------
-- Table structure for t_device_customer_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_customer_relation`;
CREATE TABLE `t_device_customer_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customerId` int(11) NULL DEFAULT NULL,
  `deviceId` int(11) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备客户关系表' ;

-- ----------------------------
-- Table structure for t_device_customer_user_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_customer_user_relation`;
CREATE TABLE `t_device_customer_user_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openId` int(11) NULL DEFAULT NULL,
  `parentOpenId` int(11) NULL DEFAULT NULL,
  `deviceId` int(11) NULL DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备 和终端用户关系表' ;

-- ----------------------------
-- Table structure for t_device_group
-- ----------------------------
DROP TABLE IF EXISTS `t_device_group`;
CREATE TABLE `t_device_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100)  DEFAULT NULL,
  `customerId` int(11) NULL DEFAULT NULL,
  `masterOpenId` varchar(100)  DEFAULT NULL,
  `manageOpenIds` varchar(200)  DEFAULT NULL,
  `status` int(11) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备群' ;

-- ----------------------------
-- Table structure for t_device_group_item
-- ----------------------------
DROP TABLE IF EXISTS `t_device_group_item`;
CREATE TABLE `t_device_group_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) NULL DEFAULT NULL,
  `groupId` int(11) NULL DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  `createTIme` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备群 和设备关系表' ;

-- ----------------------------
-- Table structure for t_device_model
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model`;
CREATE TABLE `t_device_model`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100)  DEFAULT NULL,
  `typeId` int(11) NULL DEFAULT NULL,
  `customerId` int(11) NULL DEFAULT NULL,
  `productId` int(11) NULL DEFAULT NULL,
  `version` varchar(20)  DEFAULT NULL,
  `icon` varchar(200)  DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  `remark` varchar(500)  DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备型号表' ;

-- ----------------------------
-- Table structure for t_device_model_ablity
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_ablity`;
CREATE TABLE `t_device_model_ablity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `modelId` int(11) NULL DEFAULT NULL,
  `ablityId` int(11) NULL DEFAULT NULL,
  `definedName` varchar(200)  DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备型号 功能表' ;

-- ----------------------------
-- Table structure for t_device_model_ablity_option
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_ablity_option`;
CREATE TABLE `t_device_model_ablity_option`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `modelAblityId` int(11) NULL DEFAULT NULL,
  `definedName` varchar(200)  DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备型号 功能选项自定义表' ;

-- ----------------------------
-- Table structure for t_device_operlog
-- ----------------------------
DROP TABLE IF EXISTS `t_device_operlog`;
CREATE TABLE `t_device_operlog`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备id',
  `funcId` int(11) NULL DEFAULT NULL,
  `funcValue` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `requestId` varchar(33) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求id',
  `dealRet` int(11) NULL DEFAULT NULL COMMENT '处理结果',
  `responseTime` bigint(20) NULL DEFAULT NULL COMMENT '响应时间',
  `retMsg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处理结果',
  `createTime` bigint(20) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1328 CHARACTER SET = utf8 COLLATE = utf8_general_ci ;

-- ----------------------------
-- Table structure for t_device_team
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team`;
CREATE TABLE `t_device_team`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `icon` varchar(100)  DEFAULT NULL,
  `name` varchar(100)  DEFAULT NULL,
  `remark` varchar(500)  DEFAULT NULL,
  `masterOpenId` varchar(100)  DEFAULT NULL,
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `manageOpenIds` varchar(200)  DEFAULT NULL,
  `status` int(11) NULL DEFAULT NULL,
  `createUser` varchar(100)  DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备组' ;

-- ----------------------------
-- Table structure for t_device_team_item
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team_item`;
CREATE TABLE `t_device_team_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) NULL DEFAULT NULL,
  `teamId` int(11) NULL DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  `createTIme` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备组和设备关系表' ;

-- ----------------------------
-- Table structure for t_device_type
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type`;
CREATE TABLE `t_device_type`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100)  DEFAULT NULL,
  `typeNo` varchar(50)  DEFAULT NULL,
  `icon` varchar(200)  DEFAULT NULL,
  `remark` varchar(500)  DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTIme` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备类型 表' ;

-- ----------------------------
-- Table structure for t_device_type_ablity_set
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type_ablity_set`;
CREATE TABLE `t_device_type_ablity_set`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeId` int(11) NULL DEFAULT NULL,
  `ablitySetId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备类型对应的 功能集表 (1v1)' ;

-- ----------------------------
-- Table structure for t_deviceid_pool
-- ----------------------------
DROP TABLE IF EXISTS `t_deviceid_pool`;
CREATE TABLE `t_deviceid_pool`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customerId` int(11) NULL DEFAULT NULL,
  `deviceId` varchar(1024)  DEFAULT NULL,
  `status` int(11) NULL DEFAULT NULL,
  `deviceLicence` varchar(1024)  DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备备案号 池' ;

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100)  DEFAULT NULL,
  `qrcode` char(10)  DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpadateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '微信 设备型号备案表' ;
