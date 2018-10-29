/*
 Navicat Premium Data Transfer

 Source Server         : 空气设备-测试
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : 127.0.0.1:3306
 Source Schema         : newiot

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 27/09/2018 12:58:38
*/


-- ----------------------------
-- Table structure for android_config
-- ----------------------------
DROP TABLE IF EXISTS `android_config`;
CREATE TABLE `android_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customerId` int(11) NOT NULL COMMENT '客户id，可关联客户表查到公众号等信息',
  `name` varchar(100)  COMMENT 'app名称',
  `logo` varchar(200)  COMMENT 'logo图标',
  `appUrl` varchar(200)  COMMENT 'app更新下载地址',
  `qrcode` varchar(200)  COMMENT '二维码图标',
  `deviceChangePassword` varchar(100)  COMMENT '设备切换时的密码',
  `version` varchar(50)  COMMENT '版本号',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `androidConfCustomUni`(`customerId`) USING BTREE
)    COMMENT = '安卓配置表' ;

-- ----------------------------
-- Table structure for android_scene
-- ----------------------------
DROP TABLE IF EXISTS `android_scene`;
CREATE TABLE `android_scene`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) NOT NULL COMMENT '配置表的Id',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100)  COMMENT '场景名称，如：白天，晚上等。',
  `imgsCover` varchar(200)  COMMENT '图册封面',
  `description` varchar(600)  COMMENT '描述介绍',
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)    COMMENT = '安卓场景表' ;

-- ----------------------------
-- Table structure for android_scene_img
-- ----------------------------
DROP TABLE IF EXISTS `android_scene_img`;
CREATE TABLE `android_scene_img`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) NOT NULL COMMENT '配置表的Id',
  `androidSceneId` int(11) NOT NULL COMMENT '场景表的Id',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100)  COMMENT '图片名称',
  `imgVideo` varchar(200)  COMMENT '图片或视频',
  `description` varchar(600)  COMMENT '描述介绍',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '安卓场景图册表' ;

-- ----------------------------
-- Table structure for backend_config
-- ----------------------------
DROP TABLE IF EXISTS `backend_config`;
CREATE TABLE `backend_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL,
  `name` varchar(100)  COMMENT '管理后台名称',
  `logo` varchar(200)  COMMENT '管理后台的logo',
  `type` int(1) NULL DEFAULT NULL COMMENT '类型',
  `enableStatus` int(1) NULL DEFAULT NULL COMMENT '后台是否可用',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `backConfCustomUni`(`customerId`) USING BTREE
)    COMMENT = '后端配置表' ;

-- ----------------------------
-- Table structure for t_customer
-- ----------------------------
DROP TABLE IF EXISTS `t_customer`;
CREATE TABLE `t_customer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200)  COMMENT '客户名称',
  `loginName` varchar(100)  COMMENT '登录名',
  `userType` int(1) NULL DEFAULT NULL COMMENT '用户类型',
  `remark` varchar(2000)  COMMENT '描述/备注',
  `publicName` varchar(200)  COMMENT '公众号名称',
  `publicId` varchar(200)  COMMENT '公众号id',
  `appid` varchar(200) ,
  `appsecret` varchar(200) ,
  `SLD` varchar(100)  COMMENT '二级域名',
  `typeIds` varchar(1000)  COMMENT '添加客户时，分配的设备类型',
  `modelIds` varchar(1000)  COMMENT '客户所拥有的型号',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `creatUser` varchar(100) ,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `customerSld`(`SLD`) USING BTREE COMMENT '二级域名唯一索引'
)    COMMENT = '客户表  有公众号的表，B端' ;

-- ----------------------------
-- Table structure for t_customer_user
-- ----------------------------
DROP TABLE IF EXISTS `t_customer_user`;
CREATE TABLE `t_customer_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL,
  `openId` varchar(100)  COMMENT '公众号下 用户openId',
  `nickname` varchar(200)  COMMENT '昵称',
  `unionid` varchar(100)  COMMENT '微信用户唯一Id',
  `headimgurl` varchar(200)  COMMENT '头像图片',
  `sex` int(1) NULL DEFAULT NULL COMMENT '性别',
  `province` varchar(100)  COMMENT '省',
  `city` varchar(100)  COMMENT '市',
  `mac` varchar(100)  COMMENT '用户设备mac地址',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `lastVisitTime` bigint(20) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '终端用户表（C端用户）' ;

-- ----------------------------
-- Table structure for t_device
-- ----------------------------
DROP TABLE IF EXISTS `t_device`;
CREATE TABLE `t_device`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200)  COMMENT '设备名称',
  `mac` varchar(200)  COMMENT 'mac地址',
  `wxDeviceId` varchar(200)  COMMENT '微信生成的设备id',
  `wxDevicelicence` varchar(200)  COMMENT '微信生成的序列号',
  `wxQrticket` varchar(300)  COMMENT '设备二维码生成码',
  `imei` varchar(200)  COMMENT 'imei',
  `imsi` varchar(200)  COMMENT 'imsi',
  `saNo` varchar(200)  COMMENT '序列号',
  `typeId` int(11) NULL DEFAULT NULL COMMENT '设备类型',
  `modelId` int(11) NULL DEFAULT NULL COMMENT '设备型号',
  `productId` varchar(50)  COMMENT '微信生成的设备类型的productid',
  `onlineStatus` int(1) NULL DEFAULT NULL COMMENT '在线状态',
  `bindStatus` int(1) NULL DEFAULT NULL COMMENT '绑定状态',
  `bindTime` bigint(20) NULL DEFAULT NULL COMMENT '绑定时间',
  `assignStatus` int(1) NULL DEFAULT NULL COMMENT '分配状态',
  `assignTime` bigint(20) NULL DEFAULT NULL COMMENT '分配时间',
  `enableStatus` int(1) NULL DEFAULT NULL COMMENT '启用状态',
  `workStatus` int(1) NULL DEFAULT NULL COMMENT '工作状态',
  `ip` varchar(200)  COMMENT '机器ip',
  `speedConfig` varchar(4096)  COMMENT '转速',
  `version` varchar(300)  COMMENT '版本',
  `location` varchar(500)  COMMENT '位置',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态：1-正常，2-删除',
  `hardVersion` varchar(100)  COMMENT '硬件版本',
  `communicationVersion` varchar(100)  COMMENT '通信版本',
  `softVersion` varchar(100)  COMMENT '软件版本',
  `birthTime` bigint(20) NULL DEFAULT NULL COMMENT '设备生产时间',
  `hostStatus` int(1) NULL DEFAULT NULL COMMENT '是否是主设备；1-是，2-从',
  `hostDeviceId` int(11) NULL DEFAULT NULL COMMENT '主设备id',
  `childId` varchar(100)  COMMENT '从设备编号',
  `createTime` bigint(20) NULL DEFAULT NULL COMMENT '写入时间',
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '设备表 （基础属性）' ;

-- ----------------------------
-- Table structure for t_device_ability
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ability`;
CREATE TABLE `t_device_ability`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `abilityName` varchar(200)  COMMENT '能力名称',
  `dirValue` varchar(1024)  COMMENT '通讯对应指令',
  `writeStatus` int(1) NULL DEFAULT NULL COMMENT '是否可写',
  `readStatus` int(1) NULL DEFAULT NULL COMMENT '是否可读',
  `runStatus` int(1) NULL DEFAULT NULL COMMENT '是否可执行',
  `configType` int(1) NULL DEFAULT NULL COMMENT '配置方式',
  `abilityType` int(1) NULL DEFAULT NULL COMMENT '能力类型：1-文本类、2-单选类、3-多选类、4、阈值选择类',
  `minVal` int(20) NULL DEFAULT NULL COMMENT '最小值',
  `maxVal` int(20) NULL DEFAULT NULL COMMENT '最大值',
  `remark` varchar(300)  COMMENT '备注',
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '设备能力表' ;

-- ----------------------------
-- Table structure for t_device_ability_option
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ability_option`;
CREATE TABLE `t_device_ability_option`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `abilityId` varchar(11)  COMMENT '所在能力主键',
  `optionName` varchar(200)  COMMENT '能力选项名称',
  `optionValue` varchar(11)  COMMENT '通讯对应指令/能力选项阈值',
  `minVal` int(20) NULL DEFAULT NULL COMMENT '最小值',
  `maxVal` int(20) NULL DEFAULT NULL COMMENT '最大值',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态1-正常；2-删除',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)    COMMENT = '设备能力选项表' ;

-- ----------------------------
-- Table structure for t_device_ability_set
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ability_set`;
CREATE TABLE `t_device_ability_set`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100)  COMMENT '能力集合名称',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `remark` varchar(500)  COMMENT '描述/备注',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '功能集' ;

-- ----------------------------
-- Table structure for t_device_ability_set_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ability_set_relation`;
CREATE TABLE `t_device_ability_set_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `abilityId` int(11) NULL DEFAULT NULL COMMENT '能力主键',
  `abilitySetId` int(11) NULL DEFAULT NULL COMMENT '能力集合主键',
  PRIMARY KEY (`id`) USING BTREE
) COMMENT = '功能集 和能力 关联关系表' ;

-- ----------------------------
-- Table structure for t_device_alarm
-- ----------------------------
DROP TABLE IF EXISTS `t_device_alarm`;
CREATE TABLE `t_device_alarm`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '设备告警信息' ;

-- ----------------------------
-- Table structure for t_device_customer_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_customer_relation`;
CREATE TABLE `t_device_customer_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户主键',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备Id',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备客户关系表' ;

-- ----------------------------
-- Table structure for t_device_customer_user_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_customer_user_relation`;
CREATE TABLE `t_device_customer_user_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `openId` varchar(100)  COMMENT '公众号该用户的openId',
  `parentOpenId` varchar(100)  COMMENT '该用户的分享人的openId',
  `deviceId` varchar(100)  COMMENT '设备id',
  `defineName` varchar(400)  COMMENT '设备自定义设备名称',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)    COMMENT = '设备 和终端用户关系表' ;

-- ----------------------------
-- Table structure for t_device_data_control
-- ----------------------------
DROP TABLE IF EXISTS `t_device_data_control`;
CREATE TABLE `t_device_data_control`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备id',
  `funcId` varchar(20)  COMMENT '功能id',
  `funcValue` int(11) NULL DEFAULT NULL COMMENT '功能当前值',
  `createTime` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
)   ;

-- ----------------------------
-- Table structure for t_device_data_sensor
-- ----------------------------
DROP TABLE IF EXISTS `t_device_data_sensor`;
CREATE TABLE `t_device_data_sensor`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备id',
  `sensorType` varchar(20)  COMMENT '传感器数据类型',
  `sensorValue` int(11) NULL DEFAULT NULL COMMENT '传感器数值',
  `createTime` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_did`(`deviceId`) USING BTREE
)  ;

-- ----------------------------
-- Table structure for t_device_group
-- ----------------------------
DROP TABLE IF EXISTS `t_device_group`;
CREATE TABLE `t_device_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100)  COMMENT '群名称',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `masterUserId` int(100) NULL DEFAULT NULL COMMENT '群主/主控人id',
  `manageUserIds` varchar(500)  COMMENT '群管理员id',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态',
  `introduction` varchar(100) ,
  `remark` varchar(100) ,
  `createLocation` varchar(100) ,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '设备群' ;

-- ----------------------------
-- Table structure for t_device_group_item
-- ----------------------------
DROP TABLE IF EXISTS `t_device_group_item`;
CREATE TABLE `t_device_group_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备id',
  `groupId` int(11) NULL DEFAULT NULL COMMENT '群id',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTIme` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)    COMMENT = '设备群 和设备关系表' ;

-- ----------------------------
-- Table structure for t_device_group_scene
-- ----------------------------
DROP TABLE IF EXISTS `t_device_group_scene`;
CREATE TABLE `t_device_group_scene`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `groupId` int(11) NULL DEFAULT NULL,
  `imgVideo` varchar(1024) ,
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  ;

-- ----------------------------
-- Table structure for t_device_model
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model`;
CREATE TABLE `t_device_model`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `typeId` int(11) NULL DEFAULT NULL COMMENT '类型Id',
  `name` varchar(100)  COMMENT '型号名称',
  `modelNo` varchar(50)  COMMENT '型号编码',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `productId` varchar(100)  COMMENT '产品id',
  `formatId` int(11) NULL DEFAULT NULL COMMENT '版式主键',
  `version` varchar(20)  COMMENT '版本',
  `icon` varchar(200)  COMMENT '图标',
  `remark` varchar(500) ,
  `childModelIds` varchar(2000)  COMMENT '适用丛机型号',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备型号表' ;

-- ----------------------------
-- Table structure for t_device_model_ability
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_ability`;
CREATE TABLE `t_device_model_ability`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelId` int(11) NULL DEFAULT NULL COMMENT '型号id',
  `abilityId` int(11) NULL DEFAULT NULL COMMENT '能力id',
  `definedName` varchar(200)  COMMENT '自定义名称',
  `maxVal` int(20) NULL DEFAULT NULL COMMENT '最大值',
  `minVal` int(20) NULL DEFAULT NULL COMMENT '最小值',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备型号 功能表' ;

-- ----------------------------
-- Table structure for t_device_model_ability_option
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_ability_option`;
CREATE TABLE `t_device_model_ability_option`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelAbilityId` int(11) NULL DEFAULT NULL COMMENT '型号的能力主键 即 t_device_model_ability的id',
  `abilityOptionId` int(11) NULL DEFAULT NULL,
  `definedName` varchar(200)  COMMENT '型号的能力选项自定义名称',
  `minVal` int(20) NULL DEFAULT NULL COMMENT '最小值',
  `maxVal` int(20) NULL DEFAULT NULL COMMENT '最大值',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)    COMMENT = '设备型号 功能选项自定义表' ;

-- ----------------------------
-- Table structure for t_device_model_format
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_format`;
CREATE TABLE `t_device_model_format`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelId` int(11) NULL DEFAULT NULL COMMENT '型号主键',
  `formatId` int(11) NULL DEFAULT NULL COMMENT '版式主键',
  `pageId` int(11) NULL DEFAULT NULL COMMENT '所在版式页面主键',
  `showStatus` int(1) NULL DEFAULT NULL COMMENT '是否展示',
  `showName` varchar(100)  COMMENT '展示名称',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)    ;

-- ----------------------------
-- Table structure for t_device_model_format_item
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_format_item`;
CREATE TABLE `t_device_model_format_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelFormatId` int(11) NULL DEFAULT NULL COMMENT '型号版式主键',
  `itemId` int(11) NULL DEFAULT NULL COMMENT '版式软件功能项主键',
  `abilityId` int(11) NULL DEFAULT NULL COMMENT '能力主键',
  `showStatus` int(1) NULL DEFAULT NULL COMMENT '是否展示',
  `showName` varchar(200)  COMMENT '展示名称',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)    ;

-- ----------------------------
-- Table structure for t_device_operlog
-- ----------------------------
DROP TABLE IF EXISTS `t_device_operlog`;
CREATE TABLE `t_device_operlog`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备id',
  `funcId` varchar(11) ,
  `funcValue` varchar(255) ,
  `requestId` varchar(33)  COMMENT '请求id',
  `dealRet` int(11) NULL DEFAULT NULL COMMENT '处理结果',
  `responseTime` bigint(20) NULL DEFAULT NULL COMMENT '响应时间',
  `operUserId` int(11) NULL DEFAULT NULL COMMENT '操作人id',
  `operType` int(11) NULL DEFAULT NULL COMMENT '操作类型：1-h5，2-安卓，3-管理端',
  `retMsg` varchar(255)  COMMENT '处理结果',
  `createTime` bigint(20) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '操作日志表' ;

-- ----------------------------
-- Table structure for t_device_sensor_stat
-- ----------------------------
DROP TABLE IF EXISTS `t_device_sensor_stat`;
CREATE TABLE `t_device_sensor_stat`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备Id',
  `co2` int(11) NULL DEFAULT NULL COMMENT '平均co2',
  `hcho` int(11) NULL DEFAULT NULL COMMENT '平均hcho',
  `pm` int(11) NULL DEFAULT NULL COMMENT '平均pm',
  `hum` int(11) NULL DEFAULT NULL COMMENT '平均hum',
  `tvoc` int(11) NULL DEFAULT NULL COMMENT '平均tvoc',
  `tem` int(11) NULL DEFAULT NULL COMMENT '平均温度',
  `startTime` bigint(20) NULL DEFAULT NULL COMMENT '开始时间',
  `endTime` bigint(20) NULL DEFAULT NULL COMMENT '结束时间',
  `insertTime` bigint(20) NULL DEFAULT NULL COMMENT '写入时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_did`(`deviceId`) USING BTREE
)    ;

-- ----------------------------
-- Table structure for t_device_team
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team`;
CREATE TABLE `t_device_team`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `icon` varchar(100)  COMMENT '图标、缩略图',
  `name` varchar(100)  COMMENT '组名',
  `remark` varchar(500)  COMMENT '备注、描述',
  `createUserId` int(11) NULL DEFAULT NULL COMMENT '创建人',
  `masterUserId` int(11) NULL DEFAULT NULL COMMENT '组控制人',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `manageUserIds` varchar(500)  COMMENT '组管理员',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态',
  `teamStatus` int(11) NULL DEFAULT NULL COMMENT '状态',
  `teamType` int(11) NULL DEFAULT NULL COMMENT '组类型',
  `sceneDescription` varchar(2048)  COMMENT '分组场景说明',
  `videoCover` varchar(1024)  COMMENT '分组封面',
  `videoUrl` varchar(1024)  COMMENT '分组视频链接',
  `qrcode` varchar(1024)  COMMENT '二维码链接',
  `adImages` varchar(1024)  COMMENT '广告图片',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备组' ;

-- ----------------------------
-- Table structure for t_device_team_item
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team_item`;
CREATE TABLE `t_device_team_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备主键',
  `teamId` int(11) NULL DEFAULT NULL COMMENT '所在组id',
  `manageName` varchar(100) ,
  `userId` int(11) NULL DEFAULT NULL,
  `linkAgeStatus` int(11) NULL DEFAULT NULL COMMENT '联动状态',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTIme` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '设备组和设备关系表' ;

-- ----------------------------
-- Table structure for t_device_team_scene
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team_scene`;
CREATE TABLE `t_device_team_scene`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `teamId` int(11) NULL DEFAULT NULL COMMENT '所在组id',
  `imgVideo` varchar(1024)  COMMENT '视频或图片链接',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备组和组场景关系表' ;

-- ----------------------------
-- Table structure for t_device_timer
-- ----------------------------
DROP TABLE IF EXISTS `t_device_timer`;
CREATE TABLE `t_device_timer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备id',
  `userId` int(11) NULL DEFAULT NULL COMMENT '用户id',
  `name` varchar(255)  COMMENT '定时器设备',
  `timerType` int(11) NULL DEFAULT NULL COMMENT '类型',
  `executeTime` bigint(20) NULL DEFAULT NULL COMMENT '执行时间',
  `status` int(11) NULL DEFAULT NULL COMMENT '1-正常,2-已取消,3-已失效',
  `executeRet` int(11) NULL DEFAULT NULL COMMENT '执行结果',
  `type` int(11) NULL DEFAULT NULL,
  `hour` int(11) NULL DEFAULT NULL,
  `minute` int(11) NULL DEFAULT NULL,
  `second` int(11) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL COMMENT '创建时间',
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE
)    ;

-- ----------------------------
-- Table structure for t_device_timer_day
-- ----------------------------
DROP TABLE IF EXISTS `t_device_timer_day`;
CREATE TABLE `t_device_timer_day`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timeId` int(11) NULL DEFAULT NULL,
  `dayOfWeek` int(11) NULL DEFAULT NULL,
  `status` int(11) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  ;

-- ----------------------------
-- Table structure for t_device_type
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type`;
CREATE TABLE `t_device_type`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100)  COMMENT '类型名称',
  `typeNo` varchar(50)  COMMENT '类型编号（暂冗余）',
  `icon` varchar(200)  COMMENT '图标',
  `stopWatch` varchar(1000)  COMMENT '码表',
  `source` varchar(200)  COMMENT '机型来源',
  `remark` varchar(500)  COMMENT '备注',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTIme` bigint(20) NULL DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL COMMENT '状态 1-正常，2-删除',
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备类型 表' ;

-- ----------------------------
-- Table structure for t_device_type_ability_set
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type_ability_set`;
CREATE TABLE `t_device_type_ability_set`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `typeId` int(11) NULL DEFAULT NULL COMMENT '类型主键',
  `abilitySetId` int(11) NULL DEFAULT NULL COMMENT '能力集合主键',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_typeId`(`typeId`) USING BTREE
)  COMMENT = '设备类型对应的 功能集表 (1v1)' ;

-- ----------------------------
-- Table structure for t_device_type_abilitys
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type_abilitys`;
CREATE TABLE `t_device_type_abilitys`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `abilityId` int(11) NULL DEFAULT NULL COMMENT '能力主键',
  `typeId` int(11) NULL DEFAULT NULL COMMENT '设备类型主键',
  PRIMARY KEY (`id`) USING BTREE
)    COMMENT = '功能集 和能力 关联关系表' ;

-- ----------------------------
-- Table structure for t_deviceid_pool
-- ----------------------------
DROP TABLE IF EXISTS `t_deviceid_pool`;
CREATE TABLE `t_deviceid_pool`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `productId` varchar(100)  COMMENT '微信的产品id',
  `wxDeviceId` varchar(100)  COMMENT '微信的设备的deviceId',
  `wxDeviceLicence` varchar(255) ,
  `wxQrticket` varchar(300)  COMMENT '二维码生成码',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态:0-可用，1-被占用',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '设备id池表' ;

-- ----------------------------
-- Table structure for t_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_dict`;
CREATE TABLE `t_dict`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` int(11) NOT NULL COMMENT '字典值',
  `label` varchar(50)  NOT NULL COMMENT '标签',
  `type` varchar(50)  NOT NULL COMMENT '类型',
  `description` varchar(100)  COMMENT '描述',
  `sort` int(11) NOT NULL,
  `remarks` varchar(255)  COMMENT '备注',
  `operationUserId` int(11) NULL DEFAULT NULL COMMENT '操作人id',
  `createTime` datetime(0) NULL DEFAULT NULL,
  `updateTime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `isDelete` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uni_value_type`(`value`, `type`) USING BTREE
)   COMMENT = '字典表' ;

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `publicId` int(11) NULL DEFAULT NULL COMMENT '公众号id',
  `name` varchar(100)  COMMENT '产品名称',
  `qrcode` char(10)  COMMENT '产品二维码',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpadateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT = '微信 设备型号备案表' ;

-- ----------------------------
-- Table structure for t_productid_pool
-- ----------------------------
DROP TABLE IF EXISTS `t_productid_pool`;
CREATE TABLE `t_productid_pool`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `productId` int(11) NULL DEFAULT NULL COMMENT '型号的微信的productId',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   ;

-- ----------------------------
-- Table structure for wx_bg_img
-- ----------------------------
DROP TABLE IF EXISTS `wx_bg_img`;
CREATE TABLE `wx_bg_img`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) NULL DEFAULT NULL COMMENT '配置表的Id',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100)  COMMENT '背景图片场景，如：白天，晚上等。',
  `bgImg` varchar(200)  COMMENT '背景图片地址',
  `description` varchar(500)  COMMENT '背景图片描述',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)    COMMENT = '安卓背景图片表' ;

-- ----------------------------
-- Table structure for wx_config
-- ----------------------------
DROP TABLE IF EXISTS `wx_config`;
CREATE TABLE `wx_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `password` varchar(100)  COMMENT '高级设置密码',
  `defaultTeamName` varchar(100)  COMMENT '默认组名',
  `htmlTypeIds` varchar(500)  COMMENT '页面板式类型',
  `backgroundImg` varchar(200)  COMMENT '背景图片',
  `themeName` varchar(100)  COMMENT '主题名称',
  `logo` varchar(200)  COMMENT 'h5logo图标',
  `version` varchar(100)  COMMENT '版本',
  `status` varchar(255)  COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `wxConfCustomUni`(`customerId`) USING BTREE
)  COMMENT = '微信h5端配置表' ;

-- ----------------------------
-- Table structure for wx_format
-- ----------------------------
DROP TABLE IF EXISTS `wx_format`;
CREATE TABLE `wx_format`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100)  COMMENT '页面版式名称',
  `htmlUrl` varchar(200)  COMMENT '页面访问地址',
  `icon` varchar(500)  COMMENT '首页缩图',
  `previewImg` varchar(255)  COMMENT '页面版式预览图',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态：1-正常；2-删除',
  `type` int(1) NULL DEFAULT NULL COMMENT '1-公众号版式；2-型号版式',
  `typeIds` varchar(2000)  COMMENT '适用的设备类型（逗号隔开）',
  `owerType` int(1) NULL DEFAULT NULL COMMENT '1-公有；2-私有；3-有限专属',
  `customerIds` varchar(2000)  COMMENT '可使用该版式的客户，为空则不做限制',
  `description` varchar(500)  COMMENT '描述',
  `version` varchar(200)  COMMENT '版本',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '微信h5端版型表' ;

-- ----------------------------
-- Table structure for wx_format_items
-- ----------------------------
DROP TABLE IF EXISTS `wx_format_items`;
CREATE TABLE `wx_format_items`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `formatId` int(11) NOT NULL COMMENT '版式主键',
  `pageId` int(11) NULL DEFAULT NULL COMMENT '所在页面名称',
  `name` varchar(200)  COMMENT '中文名',
  `abilityType` int(1) NULL DEFAULT NULL COMMENT '可配置的功能项类型',
  `status` int(1) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTIme` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  ;

-- ----------------------------
-- Table structure for wx_format_page
-- ----------------------------
DROP TABLE IF EXISTS `wx_format_page`;
CREATE TABLE `wx_format_page`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `formatId` int(11) NULL DEFAULT NULL COMMENT '版式主键',
  `pageNo` int(5) NULL DEFAULT NULL COMMENT '页号',
  `name` varchar(200)  COMMENT '名称',
  `showImg` varchar(200)  COMMENT '缩略图',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ;

