/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : localhost:3306
 Source Schema         : iot

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 17/08/2018 13:24:22
*/


-- ----------------------------
-- Table structure for android_bg_img
-- ----------------------------
DROP TABLE IF EXISTS `android_bg_img`;
CREATE TABLE `android_bg_img`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) NULL DEFAULT NULL COMMENT '配置表的Id',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '背景图片场景，如：白天，晚上等。',
  `bgImg` varchar(200)  NULL DEFAULT NULL COMMENT '背景图片地址',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '安卓背景图片表' ;

-- ----------------------------
-- Table structure for android_config
-- ----------------------------
DROP TABLE IF EXISTS `android_config`;
CREATE TABLE `android_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id，可关联客户表查到公众号等信息',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT 'app名称',
  `logo` varchar(200)  NULL DEFAULT NULL COMMENT 'logo图标',
  `qrcode` varchar(200)  NULL DEFAULT NULL COMMENT '二维码图标',
  `version` varchar(50)  NULL DEFAULT NULL COMMENT '版本号',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  `deviceChangePassword` varchar(100)  NULL DEFAULT NULL COMMENT '设备切换时的密码',
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '安卓配置表' ;

-- ----------------------------
-- Table structure for android_scene
-- ----------------------------
DROP TABLE IF EXISTS `android_scene`;
CREATE TABLE `android_scene`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) NULL DEFAULT NULL COMMENT '配置表的Id',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '场景名称，如：白天，晚上等。',
  `imgsCover` varchar(200)  NULL DEFAULT NULL COMMENT '图册封面',
  `describe` varchar(600)  NULL DEFAULT NULL COMMENT '描述介绍',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '安卓场景表' ;

-- ----------------------------
-- Table structure for android_scene_img
-- ----------------------------
DROP TABLE IF EXISTS `android_scene_img`;
CREATE TABLE `android_scene_img`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) NULL DEFAULT NULL,
  `androidSceneId` int(11) NULL DEFAULT NULL COMMENT '配置表的Id',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '图片名称',
  `imgVideo` varchar(200)  NULL DEFAULT NULL COMMENT '图片或视频',
  `describe` varchar(600)  NULL DEFAULT NULL COMMENT '描述介绍',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '安卓场景图册表' ;

-- ----------------------------
-- Table structure for backend_config
-- ----------------------------
DROP TABLE IF EXISTS `backend_config`;
CREATE TABLE `backend_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '管理后台名称',
  `logo` varchar(200)  NULL DEFAULT NULL COMMENT '管理后台的logo',
  `type` int(1) NULL DEFAULT NULL COMMENT '类型',
  `enableStatus` int(1) NULL DEFAULT NULL COMMENT '后台是否可用',
  `customerId` int(11) NULL DEFAULT NULL,
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '后端配置表' ;

-- ----------------------------
-- Table structure for t_customer
-- ----------------------------
DROP TABLE IF EXISTS `t_customer`;
CREATE TABLE `t_customer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200)  NULL DEFAULT NULL COMMENT '客户名称',
  `loginname` varchar(100)  NULL DEFAULT NULL COMMENT '登录名',
  `userType` int(1) NULL DEFAULT NULL COMMENT '用户类型',
  `remark` varchar(2000)  NULL DEFAULT NULL COMMENT '描述/备注',
  `publicName` varchar(200)  NULL DEFAULT NULL COMMENT '公众号名称',
  `publicId` varchar(200)  NULL DEFAULT NULL COMMENT '公众号id',
  `appid` varchar(200)  NULL DEFAULT NULL,
  `appsecret` varchar(200)  NULL DEFAULT NULL,
  `SLD` varchar(100)  NULL DEFAULT NULL COMMENT '二级域名',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  `creatUser` varchar(100)  NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '客户表  有公众号的表，B端' ;

-- ----------------------------
-- Table structure for t_customer_user
-- ----------------------------
DROP TABLE IF EXISTS `t_customer_user`;
CREATE TABLE `t_customer_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openId` varchar(100)  NULL DEFAULT NULL COMMENT '公众号下 用户openId',
  `nickname` varchar(200)  NULL DEFAULT NULL COMMENT '昵称',
  `unionid` varchar(100)  NULL DEFAULT NULL COMMENT '微信用户唯一Id',
  `headimgurl` varchar(200)  NULL DEFAULT NULL COMMENT '头像图片',
  `sex` int(1) NULL DEFAULT NULL COMMENT '性别',
  `province` varchar(100)  NULL DEFAULT NULL COMMENT '省',
  `city` varchar(100)  NULL DEFAULT NULL COMMENT '市',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  `lastVisitTime` bigint(20) NULL DEFAULT NULL,
  `mac` varchar(100)  NULL DEFAULT NULL COMMENT '用户设备mac地址',
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '终端用户表（C端用户）' ;

-- ----------------------------
-- Table structure for t_device
-- ----------------------------
DROP TABLE IF EXISTS `t_device`;
CREATE TABLE `t_device`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200)  NULL DEFAULT NULL COMMENT '设备名称',
  `mac` varchar(200)  NULL DEFAULT NULL COMMENT 'mac地址',
  `deviceId` varchar(200)  NULL DEFAULT NULL COMMENT '微信生成的设备id',
  `devicelicence` varchar(200)  NULL DEFAULT NULL COMMENT '微信生成的序列号',
  `imei` varchar(200)  NULL DEFAULT NULL COMMENT 'imei',
  `imsi` varchar(200)  NULL DEFAULT NULL COMMENT 'imsi',
  `saNo` varchar(200)  NULL DEFAULT NULL COMMENT '序列号',
  `typeId` int(11) NULL DEFAULT NULL COMMENT '设备类型',
  `productId` int(11) NULL DEFAULT NULL COMMENT '微信生成的设备类型的productid',
  `onlineStatus` int(1) NULL DEFAULT NULL COMMENT '在线状态',
  `bindStatus` int(1) NULL DEFAULT NULL COMMENT '绑定状态',
  `bindTime` bigint(20) NULL DEFAULT NULL COMMENT '绑定时间',
  `enableStatus` int(1) NULL DEFAULT NULL COMMENT '启用状态',
  `workStatus` int(1) NULL DEFAULT NULL COMMENT '工作状态',
  `ip` varchar(200)  NULL DEFAULT NULL COMMENT '机器ip',
  `speedConfig` varchar(4096)  NULL DEFAULT NULL COMMENT '转速',
  `version` varchar(300)  NULL DEFAULT NULL COMMENT '版本',
  `createTime` bigint(20) NULL DEFAULT NULL COMMENT '写入时间',
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备表 （基础属性）' ;

-- ----------------------------
-- Table structure for t_device_ablity
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity`;
CREATE TABLE `t_device_ablity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ablityName` varchar(200)  NULL DEFAULT NULL COMMENT '能力名称',
  `dirValue` varchar(1024)  NULL DEFAULT NULL COMMENT '通讯对应指令',
  `writeStatus` int(11) NULL DEFAULT NULL COMMENT '可读写状态',
  `remark` varchar(300)  NULL DEFAULT NULL COMMENT '备注',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备能力表' ;

-- ----------------------------
-- Records of t_device_ablity
-- ----------------------------
INSERT INTO `t_device_ablity` VALUES (1, '能力1', 'dirValue', 2, 'remark1', 1534383598722, 1534390218261);
INSERT INTO `t_device_ablity` VALUES (2, '能力2', 'dirValue', 1, 'remark', 1534389817771, NULL);
INSERT INTO `t_device_ablity` VALUES (3, '能力3', 'dirValue', 1, 'remark', 1534389828397, NULL);

-- ----------------------------
-- Table structure for t_device_ablity_option
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_option`;
CREATE TABLE `t_device_ablity_option`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `optionName` varchar(200)  NULL DEFAULT NULL COMMENT '能力选项名称',
  `ablityId` varchar(11)  NULL DEFAULT NULL COMMENT '通讯对应指令',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备能力选项表' ;

-- ----------------------------
-- Records of t_device_ablity_option
-- ----------------------------
INSERT INTO `t_device_ablity_option` VALUES (1, '选项1', '1', 1534400267429, 1534401684946);
INSERT INTO `t_device_ablity_option` VALUES (2, '选项2', '1', 1534400280861, NULL);
INSERT INTO `t_device_ablity_option` VALUES (3, '选项3', '1', 1534400283858, NULL);
INSERT INTO `t_device_ablity_option` VALUES (4, '选项3', '2', 1534400621478, NULL);
INSERT INTO `t_device_ablity_option` VALUES (5, '选项33', '2', 1534400624917, NULL);
INSERT INTO `t_device_ablity_option` VALUES (6, '选项33', '2', 1534401695411, NULL);

-- ----------------------------
-- Table structure for t_device_ablity_set
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_set`;
CREATE TABLE `t_device_ablity_set`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '能力集合名称',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `remark` varchar(500)  NULL DEFAULT NULL COMMENT '描述/备注',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '功能集' ;

-- ----------------------------
-- Table structure for t_device_ablity_set_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_set_relation`;
CREATE TABLE `t_device_ablity_set_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ablityId` int(11) NULL DEFAULT NULL COMMENT '能力主键',
  `ablitySetId` int(11) NULL DEFAULT NULL COMMENT '能力集合主键',
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
  `openId` varchar(100)  NULL DEFAULT NULL COMMENT '公众号该用户的openId',
  `parentOpenId` varchar(100)  NULL DEFAULT NULL COMMENT '该用户的分享人的openId',
  `deviceId` varchar(100)  NULL DEFAULT NULL COMMENT '设备id',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备 和终端用户关系表' ;

-- ----------------------------
-- Table structure for t_device_group
-- ----------------------------
DROP TABLE IF EXISTS `t_device_group`;
CREATE TABLE `t_device_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '群名称',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `masterUserId` varchar(100)  NULL DEFAULT NULL COMMENT '群主/主控人id',
  `manageUserIds` varchar(500)  NULL DEFAULT NULL COMMENT '群管理员id',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备群' ;

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
)  COMMENT = '设备群 和设备关系表' ;

-- ----------------------------
-- Table structure for t_device_model
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model`;
CREATE TABLE `t_device_model`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '型号名称',
  `typeId` int(11) NULL DEFAULT NULL COMMENT '类型Id',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `productId` int(11) NULL DEFAULT NULL COMMENT '产品id',
  `version` varchar(20)  NULL DEFAULT NULL COMMENT '版本',
  `icon` varchar(200)  NULL DEFAULT NULL COMMENT '图标',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  `remark` varchar(500)  NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备型号表' ;

-- ----------------------------
-- Records of t_device_model
-- ----------------------------
INSERT INTO `t_device_model` VALUES (1, '型号名称1-2', 111, 111, 111, '111.1', '111', 3, 1534476864007, 1534477078688, 'remark11');
INSERT INTO `t_device_model` VALUES (2, '型号名称2', 1, 1, 1, '1.1', '1', 1, 1534476900001, NULL, 'remark');
INSERT INTO `t_device_model` VALUES (3, '型号名称22', 2, 2, 2, '1.1', '2', 1, 1534476913421, NULL, 'remark');
INSERT INTO `t_device_model` VALUES (4, '型号名称1-1', 111, 111, 111, '111.1', '111', 3, 1534476959580, NULL, 'remark11');
INSERT INTO `t_device_model` VALUES (5, '型号名称1-2', 111, 111, 111, '111.1', '111', 3, 1534476989787, NULL, 'remark11');

-- ----------------------------
-- Table structure for t_device_model_ablity
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_ablity`;
CREATE TABLE `t_device_model_ablity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelId` int(11) NULL DEFAULT NULL COMMENT '型号id',
  `ablityId` int(11) NULL DEFAULT NULL COMMENT '能力id',
  `definedName` varchar(200)  NULL DEFAULT NULL COMMENT '自定义名称',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备型号 功能表' ;

-- ----------------------------
-- Table structure for t_device_model_ablity_option
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_ablity_option`;
CREATE TABLE `t_device_model_ablity_option`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelAblityId` int(11) NULL DEFAULT NULL COMMENT '型号的能力主键 即 t_device_model_ablity的id',
  `definedName` varchar(200)  NULL DEFAULT NULL COMMENT '型号的能力选项自定义名称',
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
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
  `funcValue` varchar(255)  NULL DEFAULT NULL,
  `requestId` varchar(33)  NULL DEFAULT NULL COMMENT '请求id',
  `dealRet` int(11) NULL DEFAULT NULL COMMENT '处理结果',
  `responseTime` bigint(20) NULL DEFAULT NULL COMMENT '响应时间',
  `retMsg` varchar(255)  NULL DEFAULT NULL COMMENT '处理结果',
  `createTime` bigint(20) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '操作日志表' ;

-- ----------------------------
-- Table structure for t_device_team
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team`;
CREATE TABLE `t_device_team`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `icon` varchar(100)  NULL DEFAULT NULL COMMENT '图标、缩略图',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '组名',
  `remark` varchar(500)  NULL DEFAULT NULL COMMENT '备注、描述',
  `masterUserId` varchar(100)  NULL DEFAULT NULL COMMENT '组控制人',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `manageUserIds` varchar(500)  NULL DEFAULT NULL COMMENT '组管理员',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态',
  `createUser` varchar(100)  NULL DEFAULT NULL,
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
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTIme` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备组和设备关系表' ;

-- ----------------------------
-- Table structure for t_device_type
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type`;
CREATE TABLE `t_device_type`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '类型名称',
  `typeNo` varchar(50)  NULL DEFAULT NULL COMMENT '类型编号（暂冗余）',
  `icon` varchar(200)  NULL DEFAULT NULL COMMENT '图标',
  `remark` varchar(500)  NULL DEFAULT NULL COMMENT '备注',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTIme` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '设备类型 表' ;

-- ----------------------------
-- Records of t_device_type
-- ----------------------------
INSERT INTO `t_device_type` VALUES (1, '类型名称11', '11', 'icon11', 'remark11', 1534475337628, 1534475590213);
INSERT INTO `t_device_type` VALUES (2, '类型名称2', '2', 'icon', 'remark', 1534475348334, NULL);

-- ----------------------------
-- Table structure for t_device_type_ablity_set
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type_ablity_set`;
CREATE TABLE `t_device_type_ablity_set`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `typeId` int(11) NULL DEFAULT NULL COMMENT '类型主键',
  `ablitySetId` int(11) NULL DEFAULT NULL COMMENT '能力集合主键',
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备类型对应的 功能集表 (1v1)' ;

-- ----------------------------
-- Table structure for t_deviceid_pool
-- ----------------------------
DROP TABLE IF EXISTS `t_deviceid_pool`;
CREATE TABLE `t_deviceid_pool`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `deviceId` int(11) NULL DEFAULT NULL COMMENT '设备的deviceId',
  `deviceLicence` varchar(255)  NULL DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '设备id池表' ;

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `publicId` int(11) NULL DEFAULT NULL COMMENT '公众号id',
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '产品名称',
  `qrcode` char(10)  NULL DEFAULT NULL COMMENT '产品二维码',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpadateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '微信 设备型号备案表' ;

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
)  ;

-- ----------------------------
-- Table structure for wx_config
-- ----------------------------
DROP TABLE IF EXISTS `wx_config`;
CREATE TABLE `wx_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) NULL DEFAULT NULL COMMENT '客户id',
  `password` varchar(100)  NOT NULL COMMENT '高级设置密码',
  `defaultTeamName` varchar(100)  NULL DEFAULT NULL COMMENT '默认组名',
  `htmlTypeId` int(11) NULL DEFAULT NULL COMMENT '页面板式类型',
  `backgroundImg` varchar(200)  NULL DEFAULT NULL COMMENT '背景图片',
  `themeName` varchar(100)  NULL DEFAULT NULL COMMENT '主题名称',
  `logo` varchar(200)  NULL DEFAULT NULL COMMENT 'h5logo图标',
  `version` varchar(100)  NULL DEFAULT NULL COMMENT '版本',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '微信h5端配置表' ;

-- ----------------------------
-- Table structure for wx_html_type
-- ----------------------------
DROP TABLE IF EXISTS `wx_html_type`;
CREATE TABLE `wx_html_type`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100)  NULL DEFAULT NULL COMMENT '页面版式名称',
  `htmlUrl` varchar(200)  NULL DEFAULT NULL COMMENT '页面访问地址',
  `previewImg` varchar(255)  NULL DEFAULT NULL COMMENT '页面版式预览图',
  `createTime` bigint(20) NULL DEFAULT NULL,
  `lastUpdateTime` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  COMMENT = '微信h5端版型表' ;

SET FOREIGN_KEY_CHECKS = 1;
