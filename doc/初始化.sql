/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : 127.0.0.1:3306
 Source Schema         : iot

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 31/08/2018 11:21:49
*/

CREATE DATABASE iot DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
use iot;
-- ----------------------------
-- Table structure for android_config
-- ----------------------------
DROP TABLE IF EXISTS `android_config`;
CREATE TABLE `android_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customerId` int(11) NOT NULL COMMENT '客户id，可关联客户表查到公众号等信息',
  `name` varchar(100) DEFAULT NULL COMMENT 'app名称',
  `logo` varchar(200) DEFAULT NULL COMMENT 'logo图标',
  `qrcode` varchar(200) DEFAULT NULL COMMENT '二维码图标',
  `deviceChangePassword` varchar(100) DEFAULT NULL COMMENT '设备切换时的密码',
  `version` varchar(50) DEFAULT NULL COMMENT '版本号',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `androidConfCustomUni` (`customerId`) USING BTREE
)  AUTO_INCREMENT=18  COMMENT='安卓配置表';

-- ----------------------------
-- Records of android_config
-- ----------------------------
BEGIN;
INSERT INTO `android_config` VALUES (6, 12, 'name', 'logo', 'qrcode', 'deviceChangePassword', 'version', 1, 1535039115470, NULL);
INSERT INTO `android_config` VALUES (7, 13, 'string1', 'string1', 'string1', 'string1', 'string1', 1, 1535080238159, 1535095503940);
INSERT INTO `android_config` VALUES (8, 1, 'string', 'string', 'string', 'string', 'string', 1, 1535093903752, 1535093928620);
INSERT INTO `android_config` VALUES (9, 14, '3', 'string', 'string', '3', '3', 1, 1535098205781, NULL);
INSERT INTO `android_config` VALUES (10, 16, 'string', 'string', 'string', 'string', 'string', 1, 1535100162813, NULL);
INSERT INTO `android_config` VALUES (11, 17, '456', 'string', 'string', '456', '456', 1, 1535101748106, NULL);
INSERT INTO `android_config` VALUES (12, 18, 'asdasd', '', '', '234234', '123', 1, 1535102383943, NULL);
INSERT INTO `android_config` VALUES (13, 19, 'ad', '', '', 'asd', 'ad', 1, 1535107044797, NULL);
INSERT INTO `android_config` VALUES (14, 20, '1', '', '', '1', '1', 1, 1535109165377, NULL);
INSERT INTO `android_config` VALUES (15, 21, 'my app', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/thinking3.png', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/gs.jpg', '1188', '', 1, 1535168536987, NULL);
INSERT INTO `android_config` VALUES (16, 23, 'string', 'string', 'string', 'string', 'string', 0, 1535192475570, 1535193056766);
INSERT INTO `android_config` VALUES (17, 25, '456', '', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-6-580x616.png', '567678', '657567', 1, 1535609325741, NULL);
COMMIT;

-- ----------------------------
-- Table structure for android_scene
-- ----------------------------
DROP TABLE IF EXISTS `android_scene`;
CREATE TABLE `android_scene` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) DEFAULT NULL COMMENT '配置表的Id',
  `customerId` int(11) DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100) DEFAULT NULL COMMENT '场景名称，如：白天，晚上等。',
  `imgsCover` varchar(200) DEFAULT NULL COMMENT '图册封面',
  `description` varchar(600) DEFAULT NULL COMMENT '描述介绍',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=28  COMMENT='安卓场景表';

-- ----------------------------
-- Records of android_scene
-- ----------------------------
BEGIN;
INSERT INTO `android_scene` VALUES (2, 6, 12, 'name', 'imgsCover', 'description', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (3, 7, 13, 'string1', 'string1', 'string1', NULL, 1535095503968, 1);
INSERT INTO `android_scene` VALUES (4, 8, 1, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (5, 8, 1, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (6, 7, 13, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (7, 7, 13, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (8, 7, 13, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (9, 7, 13, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (10, 7, 13, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (11, 7, 13, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (12, 7, 13, 'string', 'string', 'string', NULL, NULL, NULL);
INSERT INTO `android_scene` VALUES (13, 7, 13, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (14, 7, 13, 'string', 'string', 'string', NULL, NULL, 1);
INSERT INTO `android_scene` VALUES (15, 9, 14, '3', '3', '3', 1535098205831, NULL, 1);
INSERT INTO `android_scene` VALUES (16, 9, 14, '3', '5', '3', 1535098205927, NULL, NULL);
INSERT INTO `android_scene` VALUES (17, 11, 17, '456', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/2c2dc0556800c0386b66218240a7a50a--x-rays-plants.jpg', '456', 1535101748138, NULL, NULL);
INSERT INTO `android_scene` VALUES (18, 12, 18, '234', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-19.png', '234', 1535102383965, NULL, NULL);
INSERT INTO `android_scene` VALUES (19, 13, 19, 'ad', '', 'asd', 1535107044875, NULL, NULL);
INSERT INTO `android_scene` VALUES (20, 14, 20, '1', '', '1', 1535109165467, NULL, NULL);
INSERT INTO `android_scene` VALUES (21, 15, 21, '', '', '', 1535168537001, NULL, NULL);
INSERT INTO `android_scene` VALUES (22, 16, 23, 'string', 'string', 'string', 1535192475585, NULL, NULL);
INSERT INTO `android_scene` VALUES (23, 16, 23, 'string', 'string', 'string', 1535192515793, NULL, NULL);
INSERT INTO `android_scene` VALUES (24, 16, 23, 'string', 'string', 'string', 1535192874685, NULL, NULL);
INSERT INTO `android_scene` VALUES (25, 16, 23, 'string', 'string', 'string', 1535192982095, NULL, NULL);
INSERT INTO `android_scene` VALUES (26, 16, 23, 'string', 'string', 'string', 1535193056795, NULL, NULL);
INSERT INTO `android_scene` VALUES (27, 17, 25, '1', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-11-580x514.png', '1', 1535609325780, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for android_scene_img
-- ----------------------------
DROP TABLE IF EXISTS `android_scene_img`;
CREATE TABLE `android_scene_img` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) DEFAULT NULL COMMENT '配置表的Id',
  `androidSceneId` int(11) DEFAULT NULL COMMENT '场景表的Id',
  `customerId` int(11) DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100) DEFAULT NULL COMMENT '图片名称',
  `imgVideo` varchar(200) DEFAULT NULL COMMENT '图片或视频',
  `description` varchar(600) DEFAULT NULL COMMENT '描述介绍',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=31  COMMENT='安卓场景图册表';

-- ----------------------------
-- Records of android_scene_img
-- ----------------------------
BEGIN;
INSERT INTO `android_scene_img` VALUES (2, 6, 2, 12, 'string1', 'string1', 'string1', 1535039177481, 1535095504001, 1);
INSERT INTO `android_scene_img` VALUES (3, 7, 3, 13, 'string2', 'string2', 'string2', 1535080238224, 1535095504026, 1);
INSERT INTO `android_scene_img` VALUES (4, 8, 4, 1, 'string', 'string', 'string', 1535093903830, NULL, 1);
INSERT INTO `android_scene_img` VALUES (5, 8, 5, 1, 'string', 'string', 'string', 1535093928659, NULL, 1);
INSERT INTO `android_scene_img` VALUES (6, 7, 6, 13, 'string', 'string', 'string', 1535094054471, NULL, 1);
INSERT INTO `android_scene_img` VALUES (7, 7, 7, 13, 'string', 'string', 'string', 1535094139366, NULL, 1);
INSERT INTO `android_scene_img` VALUES (8, 7, 8, 13, 'string', 'string', 'string', 1535094164487, NULL, 1);
INSERT INTO `android_scene_img` VALUES (9, 7, 9, 13, 'string', 'string', 'string', 1535094701736, NULL, 1);
INSERT INTO `android_scene_img` VALUES (10, 7, 10, 13, 'string', 'string', 'string1', 1535094822703, NULL, 1);
INSERT INTO `android_scene_img` VALUES (11, 7, 11, 13, 'string', 'string', 'string1', 1535094879260, NULL, 1);
INSERT INTO `android_scene_img` VALUES (12, 7, 12, 13, 'string', 'string', 'string1', 1535094935329, NULL, 1);
INSERT INTO `android_scene_img` VALUES (13, 7, 13, 13, 'string', 'string', 'string1', 1535094976736, NULL, 1);
INSERT INTO `android_scene_img` VALUES (14, 7, 14, 13, 'string', 'string', 'string1', 1535095040569, NULL, 1);
INSERT INTO `android_scene_img` VALUES (15, 7, 3, 13, 'string', 'string', 'string1', 1535095322580, NULL, 1);
INSERT INTO `android_scene_img` VALUES (16, 9, 15, 14, '3', '1', '3', 1535098205875, NULL, 1);
INSERT INTO `android_scene_img` VALUES (17, 9, 15, 14, '3', '2', '3', 1535098205902, NULL, 1);
INSERT INTO `android_scene_img` VALUES (18, 9, 16, 14, '3', '4', '3', 1535098205955, NULL, NULL);
INSERT INTO `android_scene_img` VALUES (19, 11, 17, 17, '456', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', '456', 1535101748213, NULL, 1);
INSERT INTO `android_scene_img` VALUES (20, 12, 18, 18, '234', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-11-580x514.png', '234', 1535102383991, NULL, 1);
INSERT INTO `android_scene_img` VALUES (21, 13, 19, 19, 'asd', '', 'asd', 1535107044913, NULL, 1);
INSERT INTO `android_scene_img` VALUES (22, 14, 20, 20, '1', '', '1', 1535109165548, NULL, 1);
INSERT INTO `android_scene_img` VALUES (23, 15, 21, 21, '', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/BingWallpaper-2018-06-13.jpg', '', 1535168537014, NULL, 1);
INSERT INTO `android_scene_img` VALUES (24, 16, 22, 23, 'string', 'string', 'string', 1535192475600, NULL, 1);
INSERT INTO `android_scene_img` VALUES (25, 16, 23, 23, 'string', 'string', 'string', 1535192515796, NULL, 1);
INSERT INTO `android_scene_img` VALUES (26, 16, 24, 23, 'string', 'string', 'string', 1535192874695, NULL, 1);
INSERT INTO `android_scene_img` VALUES (27, 16, 25, 23, 'string', 'string', 'string', 1535192982107, NULL, 1);
INSERT INTO `android_scene_img` VALUES (28, 16, 26, 23, 'string', 'string', 'string', 1535193056816, NULL, 1);
INSERT INTO `android_scene_img` VALUES (29, 17, 27, 25, '22', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-19.png', '22', 1535609325837, NULL, 1);
INSERT INTO `android_scene_img` VALUES (30, 17, 27, 25, '33', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-18.png', '33', 1535609325860, NULL, 1);
COMMIT;

-- ----------------------------
-- Table structure for backend_config
-- ----------------------------
DROP TABLE IF EXISTS `backend_config`;
CREATE TABLE `backend_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL COMMENT '管理后台名称',
  `logo` varchar(200) DEFAULT NULL COMMENT '管理后台的logo',
  `type` int(1) DEFAULT NULL COMMENT '类型',
  `enableStatus` int(1) DEFAULT NULL COMMENT '后台是否可用',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `backConfCustomUni` (`customerId`) USING BTREE
)  AUTO_INCREMENT=13  COMMENT='后端配置表';

-- ----------------------------
-- Records of backend_config
-- ----------------------------
BEGIN;
INSERT INTO `backend_config` VALUES (2, 12, 'name', 'logo', 1, 1, NULL, 1535039177748, NULL);
INSERT INTO `backend_config` VALUES (3, 13, 'string1', 'string1', 1, 1, NULL, 1535080238306, 1535095504056);
INSERT INTO `backend_config` VALUES (4, 1, 'string', 'string', 0, 1, NULL, 1535093903858, NULL);
INSERT INTO `backend_config` VALUES (5, 14, '3', 'string', NULL, NULL, NULL, 1535098205999, NULL);
INSERT INTO `backend_config` VALUES (6, 17, '456', 'string', NULL, NULL, 1, 1535101748290, NULL);
INSERT INTO `backend_config` VALUES (7, 18, 'asdasd', '', NULL, NULL, 1, 1535102384017, NULL);
INSERT INTO `backend_config` VALUES (8, 19, 'ad', '', NULL, NULL, 1, 1535107044956, NULL);
INSERT INTO `backend_config` VALUES (9, 20, '1', '', NULL, NULL, 1, 1535109165603, NULL);
INSERT INTO `backend_config` VALUES (10, 21, 'my app', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/thinking3.png', NULL, NULL, 1, 1535168537029, NULL);
INSERT INTO `backend_config` VALUES (11, 23, 'string', 'string', 0, 0, 0, 1535192475618, 1535193056833);
INSERT INTO `backend_config` VALUES (12, 25, '456', '', NULL, NULL, 1, 1535609325886, NULL);
COMMIT;

-- ----------------------------
-- Table structure for t_customer
-- ----------------------------
DROP TABLE IF EXISTS `t_customer`;
CREATE TABLE `t_customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200) DEFAULT NULL COMMENT '客户名称',
  `loginName` varchar(100) DEFAULT NULL COMMENT '登录名',
  `userType` int(1) DEFAULT NULL COMMENT '用户类型',
  `remark` varchar(2000) DEFAULT NULL COMMENT '描述/备注',
  `publicName` varchar(200) DEFAULT NULL COMMENT '公众号名称',
  `publicId` varchar(200) DEFAULT NULL COMMENT '公众号id',
  `appid` varchar(200) DEFAULT NULL,
  `appsecret` varchar(200) DEFAULT NULL,
  `SLD` varchar(100) DEFAULT NULL COMMENT '二级域名',
  `typeIds` varchar(1000) DEFAULT NULL COMMENT '添加客户时，分配的设备类型',
  `modelIds` varchar(1000) DEFAULT NULL COMMENT '客户所拥有的型号',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  `creatUser` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `customerSld` (`SLD`) USING BTREE COMMENT '二级域名唯一索引'
)  AUTO_INCREMENT=26  COMMENT='客户表  有公众号的表，B端';

-- ----------------------------
-- Records of t_customer
-- ----------------------------
BEGIN;
INSERT INTO `t_customer` VALUES (12, 'name', 'loginName', 1, 'remark', 'publicName', '1', 'appid', 'appid', 'sld', '4,5,6', '1,2,3', 2, 1535039114171, NULL, NULL);
INSERT INTO `t_customer` VALUES (13, 'string1', 'string12', 1, 'string1', 'string1', '1', 'string', 'string', 'string1', 'string1', 'string1', 2, 1535080238055, 1535095503632, NULL);
INSERT INTO `t_customer` VALUES (14, '1', '1', 1, '1', '1', NULL, '1', '1', '1', '43,44', NULL, 2, 1535098205679, NULL, NULL);
INSERT INTO `t_customer` VALUES (16, 'string', 'string', 0, 'string', 'string', '0', 'string', 'string', 'string', 'string', 'string', 2, 1535100162521, NULL, NULL);
INSERT INTO `t_customer` VALUES (17, '11', '11', 1, '23', '11123', NULL, '123', '23', '123123', '43', NULL, 2, 1535101747893, NULL, NULL);
INSERT INTO `t_customer` VALUES (18, '1', '345', 1, '1', '1', NULL, '1', '1', '345', '43,44', NULL, 2, 1535102383872, NULL, NULL);
INSERT INTO `t_customer` VALUES (19, 'asd', 'asd', 1, 'asd', 'ad', NULL, 'asd', 'asdad', 'adadasd', '44,43', NULL, 2, 1535107044704, NULL, NULL);
INSERT INTO `t_customer` VALUES (20, '1', '1', 1, '1', '1', NULL, '1', '1', '111111', '44,43', NULL, 2, 1535109165289, NULL, NULL);
INSERT INTO `t_customer` VALUES (21, 'agae', 'hcoiot', 1, 'adsf', 'adfff', NULL, 'adfa', 'adga', 'hco', '45,44', NULL, 2, 1535168536848, NULL, NULL);
INSERT INTO `t_customer` VALUES (22, 'string', 'string', 0, 'string', 'string', '0', 'string', 'string', 'string123', 'string', 'string', 2, 1535192306691, NULL, NULL);
INSERT INTO `t_customer` VALUES (23, 'string', 'string', 0, 'string', 'string', '0', 'string', 'string', 'string1231301', 'string', 'string', 1, 1535192475408, 1535193048810, NULL);
INSERT INTO `t_customer` VALUES (25, '123', 'asd', 2, '345345', '123', NULL, '213', '324', 'asd', '43,44', NULL, 1, 1535609325526, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for t_customer_user
-- ----------------------------
DROP TABLE IF EXISTS `t_customer_user`;
CREATE TABLE `t_customer_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openId` varchar(100) DEFAULT NULL COMMENT '公众号下 用户openId',
  `nickname` varchar(200) DEFAULT NULL COMMENT '昵称',
  `unionid` varchar(100) DEFAULT NULL COMMENT '微信用户唯一Id',
  `headimgurl` varchar(200) DEFAULT NULL COMMENT '头像图片',
  `sex` int(1) DEFAULT NULL COMMENT '性别',
  `province` varchar(100) DEFAULT NULL COMMENT '省',
  `city` varchar(100) DEFAULT NULL COMMENT '市',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  `lastVisitTime` bigint(20) DEFAULT NULL,
  `mac` varchar(100) DEFAULT NULL COMMENT '用户设备mac地址',
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='终端用户表（C端用户）';

-- ----------------------------
-- Table structure for t_device
-- ----------------------------
DROP TABLE IF EXISTS `t_device`;
CREATE TABLE `t_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(200) DEFAULT NULL COMMENT '设备名称',
  `mac` varchar(200) DEFAULT NULL COMMENT 'mac地址',
  `deviceId` varchar(200) DEFAULT NULL COMMENT '微信生成的设备id',
  `devicelicence` varchar(200) DEFAULT NULL COMMENT '微信生成的序列号',
  `imei` varchar(200) DEFAULT NULL COMMENT 'imei',
  `imsi` varchar(200) DEFAULT NULL COMMENT 'imsi',
  `saNo` varchar(200) DEFAULT NULL COMMENT '序列号',
  `typeId` int(11) DEFAULT NULL COMMENT '设备类型',
  `modelId` int(11) DEFAULT NULL COMMENT '设备型号',
  `productId` int(11) DEFAULT NULL COMMENT '微信生成的设备类型的productid',
  `onlineStatus` int(1) DEFAULT NULL COMMENT '在线状态',
  `bindStatus` int(1) DEFAULT NULL COMMENT '绑定状态',
  `bindTime` bigint(20) DEFAULT NULL COMMENT '绑定时间',
  `enableStatus` int(1) DEFAULT NULL COMMENT '启用状态',
  `workStatus` int(1) DEFAULT NULL COMMENT '工作状态',
  `ip` varchar(200) DEFAULT NULL COMMENT '机器ip',
  `speedConfig` varchar(4096) DEFAULT NULL COMMENT '转速',
  `version` varchar(300) DEFAULT NULL COMMENT '版本',
  `location` varchar(500) DEFAULT '' COMMENT '位置',
  `status` int(1) DEFAULT NULL COMMENT '状态：1-正常，2-删除',
  `hardVersion` varchar(100) DEFAULT NULL COMMENT '硬件版本',
  `communicationVersion` varchar(100) DEFAULT NULL COMMENT '通信版本',
  `softVersion` varchar(100) DEFAULT NULL COMMENT '软件版本',
  `birthTime` bigint(20) DEFAULT NULL COMMENT '设备生产时间',
  `createTime` bigint(20) DEFAULT NULL COMMENT '写入时间',
  `lastUpdateTime` bigint(20) DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=8  COMMENT='设备表 （基础属性）';

-- ----------------------------
-- Records of t_device
-- ----------------------------
BEGIN;
INSERT INTO `t_device` VALUES (1, '设备1', '3A-5B-SK-76-K9', NULL, NULL, NULL, NULL, NULL, 12, NULL, NULL, 0, 1, NULL, 0, 0, NULL, NULL, NULL, NULL, 0, '1.0', NULL, NULL, 1535039115470, 1535167874517, 1535167874517);
INSERT INTO `t_device` VALUES (2, '设备2', '3B-5A-SK-76-K9', NULL, NULL, NULL, NULL, NULL, 12, NULL, NULL, 0, 1, NULL, 0, 0, NULL, NULL, NULL, NULL, 0, '1.0', NULL, NULL, 1535039115400, 1535167947173, 1535167947173);
INSERT INTO `t_device` VALUES (3, '设备3', '3B-5A-SK-71-K9', NULL, NULL, NULL, NULL, NULL, 13, NULL, NULL, 0, 1, NULL, 0, 0, NULL, NULL, NULL, NULL, 0, '1.0', NULL, NULL, 1535039115300, 1535167959509, 1535167959509);
INSERT INTO `t_device` VALUES (4, '设备4', '3B-5A-SK-90-K9', NULL, NULL, NULL, NULL, NULL, 13, NULL, NULL, 0, 1, NULL, 0, 0, NULL, NULL, NULL, NULL, 0, '1.0', NULL, NULL, 1535039115100, 1535167969375, 1535167969375);
INSERT INTO `t_device` VALUES (5, '123', '123', NULL, NULL, NULL, NULL, NULL, 45, NULL, NULL, 0, 1, NULL, 0, 0, NULL, NULL, NULL, NULL, 0, '123', NULL, NULL, 1535536450822, 1535536450792, 1535536450792);
INSERT INTO `t_device` VALUES (6, '345', '345', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, NULL, 0, 0, NULL, NULL, NULL, NULL, 0, '546', NULL, NULL, 1535536488794, 1535536490310, 1535536490310);
INSERT INTO `t_device` VALUES (7, 'asdasd', 'asdasd', NULL, NULL, NULL, NULL, NULL, 50, NULL, NULL, 0, 1, NULL, 0, 0, NULL, NULL, NULL, NULL, 0, 'asdasd', NULL, NULL, 1535536544721, 1535536544979, 1535536544979);
COMMIT;

-- ----------------------------
-- Table structure for t_device_ablity
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity`;
CREATE TABLE `t_device_ablity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ablityName` varchar(200) DEFAULT NULL COMMENT '能力名称',
  `dirValue` varchar(1024) DEFAULT NULL COMMENT '通讯对应指令',
  `writeStatus` int(1) DEFAULT NULL COMMENT '是否可写',
  `readStatus` int(1) DEFAULT NULL COMMENT '是否可读',
  `runStatus` int(1) DEFAULT NULL COMMENT '是否可执行',
  `configType` int(1) DEFAULT NULL COMMENT '配置方式',
  `ablityType` int(1) DEFAULT NULL COMMENT '能力类型：1-硬件；2-软件',
  `remark` varchar(300) DEFAULT NULL COMMENT '备注',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=15  COMMENT='设备能力表';

-- ----------------------------
-- Records of t_device_ablity
-- ----------------------------
BEGIN;
INSERT INTO `t_device_ablity` VALUES (12, 'onoff', '0x01', 0, 0, 0, 1, NULL, '', 1535166927696, NULL);
INSERT INTO `t_device_ablity` VALUES (13, 'onoff', '0x01', 1, 0, 0, 2, NULL, 'this is a fanspeed ablity', 1535166969249, 1535167010714);
INSERT INTO `t_device_ablity` VALUES (14, '1', '2', 0, 1, 1, 1, 4, '3', 1535640160323, NULL);
COMMIT;

-- ----------------------------
-- Table structure for t_device_ablity_option
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_option`;
CREATE TABLE `t_device_ablity_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ablityId` varchar(11) DEFAULT NULL COMMENT '所在能力主键',
  `optionName` varchar(200) DEFAULT NULL COMMENT '能力选项名称',
  `optionValue` varchar(11) DEFAULT NULL COMMENT '通讯对应指令/能力选项阈值',
  `status` int(1) DEFAULT NULL COMMENT '状态1-正常；2-删除',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=40  COMMENT='设备能力选项表';

-- ----------------------------
-- Records of t_device_ablity_option
-- ----------------------------
BEGIN;
INSERT INTO `t_device_ablity_option` VALUES (9, '0', '1挡', '1', NULL, 1534940511987, NULL);
INSERT INTO `t_device_ablity_option` VALUES (10, '0', '2挡', '2', NULL, 1534940512020, NULL);
INSERT INTO `t_device_ablity_option` VALUES (11, '0', '1挡', '1', NULL, 1534940564625, NULL);
INSERT INTO `t_device_ablity_option` VALUES (12, '0', '2挡', '2', NULL, 1534940564666, NULL);
INSERT INTO `t_device_ablity_option` VALUES (34, '13', 'dangwei', '1', 1, 1535166969384, NULL);
INSERT INTO `t_device_ablity_option` VALUES (35, '13', 'dangwei', '2', 1, 1535166969409, NULL);
INSERT INTO `t_device_ablity_option` VALUES (36, '13', 'dangwei', '3', 1, 1535166969416, NULL);
INSERT INTO `t_device_ablity_option` VALUES (37, '13', 'dangwei', '1', 1, 1535167010848, NULL);
INSERT INTO `t_device_ablity_option` VALUES (38, '13', 'dangwei', '2', 1, 1535167010851, NULL);
INSERT INTO `t_device_ablity_option` VALUES (39, '13', 'dangwei', '3', 1, 1535167010854, NULL);
COMMIT;

-- ----------------------------
-- Table structure for t_device_ablity_set
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_set`;
CREATE TABLE `t_device_ablity_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '能力集合名称',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '描述/备注',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='功能集';

-- ----------------------------
-- Table structure for t_device_ablity_set_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_ablity_set_relation`;
CREATE TABLE `t_device_ablity_set_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ablityId` int(11) DEFAULT NULL COMMENT '能力主键',
  `ablitySetId` int(11) DEFAULT NULL COMMENT '能力集合主键',
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='功能集 和能力 关联关系表';

-- ----------------------------
-- Table structure for t_device_alarm
-- ----------------------------
DROP TABLE IF EXISTS `t_device_alarm`;
CREATE TABLE `t_device_alarm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备告警信息';

-- ----------------------------
-- Table structure for t_device_customer_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_customer_relation`;
CREATE TABLE `t_device_customer_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) DEFAULT NULL COMMENT '客户主键',
  `deviceId` int(11) DEFAULT NULL COMMENT '设备Id',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备客户关系表';

-- ----------------------------
-- Table structure for t_device_customer_user_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_device_customer_user_relation`;
CREATE TABLE `t_device_customer_user_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) DEFAULT NULL COMMENT '客户id',
  `openId` varchar(100) DEFAULT NULL COMMENT '公众号该用户的openId',
  `parentOpenId` varchar(100) DEFAULT NULL COMMENT '该用户的分享人的openId',
  `deviceId` varchar(100) DEFAULT NULL COMMENT '设备id',
  `defineName` varchar(400) DEFAULT NULL COMMENT '社会自定义设备名称',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备 和终端用户关系表';

-- ----------------------------
-- Table structure for t_device_group
-- ----------------------------
DROP TABLE IF EXISTS `t_device_group`;
CREATE TABLE `t_device_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '群名称',
  `customerId` int(11) DEFAULT NULL COMMENT '客户id',
  `masterUserId` varchar(100) DEFAULT NULL COMMENT '群主/主控人id',
  `manageUserIds` varchar(500) DEFAULT NULL COMMENT '群管理员id',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备群';

-- ----------------------------
-- Table structure for t_device_group_item
-- ----------------------------
DROP TABLE IF EXISTS `t_device_group_item`;
CREATE TABLE `t_device_group_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `deviceId` int(11) DEFAULT NULL COMMENT '设备id',
  `groupId` int(11) DEFAULT NULL COMMENT '群id',
  `userId` int(11) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTIme` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备群 和设备关系表';

-- ----------------------------
-- Table structure for t_device_model
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model`;
CREATE TABLE `t_device_model` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `typeId` int(11) DEFAULT NULL COMMENT '类型Id',
  `name` varchar(100) DEFAULT NULL COMMENT '型号名称',
  `modelNo` varchar(50) DEFAULT NULL COMMENT '型号编码',
  `customerId` int(11) DEFAULT NULL COMMENT '客户id',
  `productId` int(11) DEFAULT NULL COMMENT '产品id',
  `formatId` int(11) DEFAULT NULL COMMENT '版式主键',
  `version` varchar(20) DEFAULT NULL COMMENT '版本',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=9  COMMENT='设备型号表';

-- ----------------------------
-- Records of t_device_model
-- ----------------------------
BEGIN;
INSERT INTO `t_device_model` VALUES (1, 111, '型号名称1-2', NULL, 111, 111, NULL, '111.1', '111', 3, 1534476864007, 1534477078688, 'remark11');
INSERT INTO `t_device_model` VALUES (2, 1, '型号名称2', NULL, 1, 1, NULL, '1.1', '1', 1, 1534476900001, NULL, 'remark');
INSERT INTO `t_device_model` VALUES (3, 2, '型号名称22', NULL, 2, 2, NULL, '1.1', '2', 1, 1534476913421, NULL, 'remark');
INSERT INTO `t_device_model` VALUES (4, 111, '型号名称1-1', NULL, 111, 111, NULL, '111.1', '111', 3, 1534476959580, NULL, 'remark11');
INSERT INTO `t_device_model` VALUES (5, 111, '型号名称1-2', NULL, 111, 111, NULL, '111.1', '111', 3, 1534476989787, NULL, 'remark11');
INSERT INTO `t_device_model` VALUES (6, 0, 'string', NULL, 0, 0, NULL, 'string', 'string', 0, 1535118489980, NULL, 'string');
INSERT INTO `t_device_model` VALUES (7, 0, 'string', NULL, 0, 0, NULL, 'string', 'string', 0, 1535120486103, NULL, 'string');
INSERT INTO `t_device_model` VALUES (8, 0, 'string', NULL, 0, 0, NULL, 'string', 'string', 0, 1535511286029, NULL, 'string');
COMMIT;

-- ----------------------------
-- Table structure for t_device_model_ablity
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_ablity`;
CREATE TABLE `t_device_model_ablity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelId` int(11) DEFAULT NULL COMMENT '型号id',
  `ablityId` int(11) DEFAULT NULL COMMENT '能力id',
  `definedName` varchar(200) DEFAULT NULL COMMENT '自定义名称',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备型号 功能表';

-- ----------------------------
-- Table structure for t_device_model_ablity_option
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_ablity_option`;
CREATE TABLE `t_device_model_ablity_option` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelAblityId` int(11) DEFAULT NULL COMMENT '型号的能力主键 即 t_device_model_ablity的id',
  `ablityOptionId` int(11) DEFAULT NULL,
  `definedName` varchar(200) DEFAULT NULL COMMENT '型号的能力选项自定义名称',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备型号 功能选项自定义表';

-- ----------------------------
-- Table structure for t_device_model_format_config
-- ----------------------------
DROP TABLE IF EXISTS `t_device_model_format_config`;
CREATE TABLE `t_device_model_format_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `modelId` int(11) DEFAULT NULL COMMENT '型号主键',
  `formatId` int(11) DEFAULT NULL COMMENT '版式主键',
  `pageId` int(11) DEFAULT NULL COMMENT '所在版式页面主键',
  `itemId` int(11) DEFAULT NULL COMMENT '版式软件功能项主键',
  `ablityId` int(11) DEFAULT NULL COMMENT '能力主键',
  `showStatus` int(1) DEFAULT NULL COMMENT '是否展示',
  `showName` varchar(200) DEFAULT NULL COMMENT '展示名称',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  ;

-- ----------------------------
-- Table structure for t_device_operlog
-- ----------------------------
DROP TABLE IF EXISTS `t_device_operlog`;
CREATE TABLE `t_device_operlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `deviceId` int(11) DEFAULT NULL COMMENT '设备id',
  `funcId` int(11) DEFAULT NULL,
  `funcValue` varchar(255) DEFAULT NULL,
  `requestId` varchar(33) DEFAULT NULL COMMENT '请求id',
  `dealRet` int(11) DEFAULT NULL COMMENT '处理结果',
  `responseTime` bigint(20) DEFAULT NULL COMMENT '响应时间',
  `retMsg` varchar(255) DEFAULT NULL COMMENT '处理结果',
  `createTime` bigint(20) DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='操作日志表';

-- ----------------------------
-- Table structure for t_device_team
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team`;
CREATE TABLE `t_device_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `icon` varchar(100) DEFAULT NULL COMMENT '图标、缩略图',
  `name` varchar(100) DEFAULT NULL COMMENT '组名',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注、描述',
  `createUserId` int(11) DEFAULT NULL COMMENT '创建人',
  `masterUserId` int(11) DEFAULT NULL COMMENT '组控制人',
  `customerId` int(11) DEFAULT NULL COMMENT '客户id',
  `manageUserIds` varchar(500) DEFAULT NULL COMMENT '组管理员',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `teamStatus` int(11) DEFAULT NULL COMMENT '状态',
  `teamType` int(11) DEFAULT NULL COMMENT '组类型',
  `sceneDescription` varchar(2048) DEFAULT NULL COMMENT '分组场景说明',
  `videoCover` varchar(1024) DEFAULT NULL COMMENT '分组封面',
  `videoUrl` varchar(1024) DEFAULT NULL COMMENT '分组视频链接',
  `qrcode` varchar(1024) DEFAULT NULL COMMENT '二维码链接',
  `adImages` varchar(1024) DEFAULT NULL COMMENT '广告图片',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备组';

-- ----------------------------
-- Table structure for t_device_team_item
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team_item`;
CREATE TABLE `t_device_team_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `deviceId` int(11) DEFAULT NULL COMMENT '设备主键',
  `teamId` int(11) DEFAULT NULL COMMENT '所在组id',
  `userId` int(11) DEFAULT NULL,
  `linkAgeStatus` int(11) DEFAULT NULL COMMENT '联动状态',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTIme` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备组和设备关系表';

-- ----------------------------
-- Table structure for t_device_team_scene
-- ----------------------------
DROP TABLE IF EXISTS `t_device_team_scene`;
CREATE TABLE `t_device_team_scene` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `teamId` int(11) DEFAULT NULL COMMENT '所在组id',
  `imgVideo` varchar(1024) DEFAULT NULL COMMENT '视频或图片链接',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTIme` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备组和组场景关系表';

-- ----------------------------
-- Table structure for t_device_timer
-- ----------------------------
DROP TABLE IF EXISTS `t_device_timer`;
CREATE TABLE `t_device_timer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` int(11) DEFAULT NULL COMMENT '设备id',
  `userId` int(11) DEFAULT NULL COMMENT '用户id',
  `name` varchar(255) DEFAULT NULL COMMENT '定时器设备',
  `timerType` int(11) DEFAULT NULL COMMENT '类型',
  `executeTime` bigint(20) DEFAULT NULL COMMENT '执行时间',
  `status` int(11) DEFAULT NULL COMMENT '1-正常,2-已取消,3-已失效',
  `executeRet` int(11) DEFAULT NULL COMMENT '执行结果',
  `createTime` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `lastUpdateTime` bigint(20) DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE
)  ;

-- ----------------------------
-- Table structure for t_device_type
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type`;
CREATE TABLE `t_device_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) DEFAULT NULL COMMENT '类型名称',
  `typeNo` varchar(50) DEFAULT NULL COMMENT '类型编号（暂冗余）',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标',
  `stopWatch` varchar(200) DEFAULT NULL COMMENT '码表',
  `source` varchar(200) DEFAULT NULL COMMENT '机型来源',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTIme` bigint(20) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '状态 1-正常，2-删除',
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=52  COMMENT='设备类型 表';

-- ----------------------------
-- Records of t_device_type
-- ----------------------------
BEGIN;
INSERT INTO `t_device_type` VALUES (27, 'a', 'a', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/c9c681a5dd326b0234172e1d213a7eea.jpg', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', 'a', 'a', 1534926706944, 1534932176725, 2);
INSERT INTO `t_device_type` VALUES (28, '123b', 'b', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-19.png', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', 'b', 'b', 1534926851196, NULL, 2);
INSERT INTO `t_device_type` VALUES (29, 'dd', 'd', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', 'd', 'd', 1534927026950, NULL, 2);
INSERT INTO `t_device_type` VALUES (30, 'e', 'e', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/77faf2de7998394fce4f05c789e5e6b8.jpg', 'e', 'e', 1534927146798, NULL, 2);
INSERT INTO `t_device_type` VALUES (31, 'asd', 'asd', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', '', 'asd', 'asd', 1534927367723, NULL, 2);
INSERT INTO `t_device_type` VALUES (32, 'sad', 'asd', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', '', 'asd', '', 1534927454899, NULL, 2);
INSERT INTO `t_device_type` VALUES (33, '23', '123', '', '', '23', '23', 1534927761989, NULL, 2);
INSERT INTO `t_device_type` VALUES (34, '123', '123', '', '', '123', '', 1534930123816, NULL, 2);
INSERT INTO `t_device_type` VALUES (35, '123', '123', '', '', '123', '', 1534930123816, 1534932202834, 2);
INSERT INTO `t_device_type` VALUES (36, '1', '1', '', '', '1', '', 1534932282484, NULL, 2);
INSERT INTO `t_device_type` VALUES (37, '1', '1', '', '', '1', '', 1534932423255, 1534933603035, 2);
INSERT INTO `t_device_type` VALUES (38, '2', '2', '', '', '2', '', 1534932483983, 1534933630323, 2);
INSERT INTO `t_device_type` VALUES (39, '3', '3', '', '', '3', '', 1534932755420, 1534933734379, 2);
INSERT INTO `t_device_type` VALUES (40, '4', '4', '', '', '4', '', 1534932800100, NULL, 2);
INSERT INTO `t_device_type` VALUES (41, '5', '5', '', '', '5', '', 1534933081948, NULL, 2);
INSERT INTO `t_device_type` VALUES (42, '1', '1', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/c9c681a5dd326b0234172e1d213a7eea.jpg', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-19.png', '1', '1', 1534998503863, NULL, 2);
INSERT INTO `t_device_type` VALUES (43, '12', '12', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-18.png', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/2c2dc0556800c0386b66218240a7a50a--x-rays-plants.jpg', '12', '12', 1534998532575, NULL, 1);
INSERT INTO `t_device_type` VALUES (44, '4', '3', '', '', '3', '3', 1535015351594, NULL, 1);
INSERT INTO `t_device_type` VALUES (45, 'niubi', '082501', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/gs.jpg', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/BingWallpaper-2018-06-13.jpg', 'hco', 'this is a  test type', 1535167085380, NULL, 1);
INSERT INTO `t_device_type` VALUES (46, '123123', '123', '', '', '123', '343434', 1535186093374, NULL, 2);
INSERT INTO `t_device_type` VALUES (47, '123', '123', '', '', '4343434', '2323', 1535186182827, NULL, 2);
INSERT INTO `t_device_type` VALUES (48, '1', '1', '', '', '1', '1', 1535186236428, NULL, 2);
INSERT INTO `t_device_type` VALUES (49, '123', '12', '', '', '213', '', 1535186687969, NULL, 2);
INSERT INTO `t_device_type` VALUES (50, '123', '123', '', '', '123', '', 1535186791250, NULL, 1);
INSERT INTO `t_device_type` VALUES (51, '123', '123123123', '', '', '123', '', 1535186996186, NULL, 1);
COMMIT;

-- ----------------------------
-- Table structure for t_device_type_ablity_set
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type_ablity_set`;
CREATE TABLE `t_device_type_ablity_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `typeId` int(11) DEFAULT NULL COMMENT '类型主键',
  `ablitySetId` int(11) DEFAULT NULL COMMENT '能力集合主键',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_typeId` (`typeId`) USING BTREE
)   COMMENT='设备类型对应的 功能集表 (1v1)';

-- ----------------------------
-- Table structure for t_device_type_ablitys
-- ----------------------------
DROP TABLE IF EXISTS `t_device_type_ablitys`;
CREATE TABLE `t_device_type_ablitys` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ablityId` int(11) DEFAULT NULL COMMENT '能力主键',
  `typeId` int(11) DEFAULT NULL COMMENT '设备类型主键',
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=80  COMMENT='功能集 和能力 关联关系表';

-- ----------------------------
-- Records of t_device_type_ablitys
-- ----------------------------
BEGIN;
INSERT INTO `t_device_type_ablitys` VALUES (1, 7, 39);
INSERT INTO `t_device_type_ablitys` VALUES (2, 2, 1);
INSERT INTO `t_device_type_ablitys` VALUES (3, 2, 1);
INSERT INTO `t_device_type_ablitys` VALUES (4, 2, 18);
INSERT INTO `t_device_type_ablitys` VALUES (5, 1, 18);
INSERT INTO `t_device_type_ablitys` VALUES (6, 1, 19);
INSERT INTO `t_device_type_ablitys` VALUES (7, 1, 20);
INSERT INTO `t_device_type_ablitys` VALUES (8, 2, 21);
INSERT INTO `t_device_type_ablitys` VALUES (9, 1, 21);
INSERT INTO `t_device_type_ablitys` VALUES (10, 2, 22);
INSERT INTO `t_device_type_ablitys` VALUES (11, 1, 22);
INSERT INTO `t_device_type_ablitys` VALUES (12, 2, 1);
INSERT INTO `t_device_type_ablitys` VALUES (13, 2, 1);
INSERT INTO `t_device_type_ablitys` VALUES (14, 2, 23);
INSERT INTO `t_device_type_ablitys` VALUES (15, 1, 24);
INSERT INTO `t_device_type_ablitys` VALUES (16, 2, 24);
INSERT INTO `t_device_type_ablitys` VALUES (17, 2, 25);
INSERT INTO `t_device_type_ablitys` VALUES (18, 1, 25);
INSERT INTO `t_device_type_ablitys` VALUES (19, 1, 29);
INSERT INTO `t_device_type_ablitys` VALUES (20, 2, 29);
INSERT INTO `t_device_type_ablitys` VALUES (21, 2, 30);
INSERT INTO `t_device_type_ablitys` VALUES (22, 1, 30);
INSERT INTO `t_device_type_ablitys` VALUES (23, 1, 31);
INSERT INTO `t_device_type_ablitys` VALUES (24, 2, 31);
INSERT INTO `t_device_type_ablitys` VALUES (25, 1, 32);
INSERT INTO `t_device_type_ablitys` VALUES (26, 2, 33);
INSERT INTO `t_device_type_ablitys` VALUES (27, 1, 33);
INSERT INTO `t_device_type_ablitys` VALUES (28, 2, 34);
INSERT INTO `t_device_type_ablitys` VALUES (29, 2, 35);
INSERT INTO `t_device_type_ablitys` VALUES (30, 1, 34);
INSERT INTO `t_device_type_ablitys` VALUES (31, 1, 35);
INSERT INTO `t_device_type_ablitys` VALUES (32, 1, 24);
INSERT INTO `t_device_type_ablitys` VALUES (33, 2, 24);
INSERT INTO `t_device_type_ablitys` VALUES (34, 1, 24);
INSERT INTO `t_device_type_ablitys` VALUES (35, 1, 24);
INSERT INTO `t_device_type_ablitys` VALUES (36, 2, 27);
INSERT INTO `t_device_type_ablitys` VALUES (37, 2, 27);
INSERT INTO `t_device_type_ablitys` VALUES (38, 1, 27);
INSERT INTO `t_device_type_ablitys` VALUES (39, 1, 35);
INSERT INTO `t_device_type_ablitys` VALUES (40, 1, 35);
INSERT INTO `t_device_type_ablitys` VALUES (41, 2, 35);
INSERT INTO `t_device_type_ablitys` VALUES (42, 1, 36);
INSERT INTO `t_device_type_ablitys` VALUES (43, 2, 36);
INSERT INTO `t_device_type_ablitys` VALUES (44, 2, 37);
INSERT INTO `t_device_type_ablitys` VALUES (45, 1, 37);
INSERT INTO `t_device_type_ablitys` VALUES (46, 2, 38);
INSERT INTO `t_device_type_ablitys` VALUES (47, 1, 38);
INSERT INTO `t_device_type_ablitys` VALUES (48, 2, 39);
INSERT INTO `t_device_type_ablitys` VALUES (49, 1, 39);
INSERT INTO `t_device_type_ablitys` VALUES (50, 2, 40);
INSERT INTO `t_device_type_ablitys` VALUES (51, 1, 40);
INSERT INTO `t_device_type_ablitys` VALUES (52, 2, 41);
INSERT INTO `t_device_type_ablitys` VALUES (53, 1, 41);
INSERT INTO `t_device_type_ablitys` VALUES (54, 1, 37);
INSERT INTO `t_device_type_ablitys` VALUES (55, 1, 37);
INSERT INTO `t_device_type_ablitys` VALUES (56, 1, 37);
INSERT INTO `t_device_type_ablitys` VALUES (57, 2, 37);
INSERT INTO `t_device_type_ablitys` VALUES (58, 1, 37);
INSERT INTO `t_device_type_ablitys` VALUES (59, 1, 37);
INSERT INTO `t_device_type_ablitys` VALUES (60, 1, 37);
INSERT INTO `t_device_type_ablitys` VALUES (61, 1, 38);
INSERT INTO `t_device_type_ablitys` VALUES (62, 11, 42);
INSERT INTO `t_device_type_ablitys` VALUES (63, 10, 42);
INSERT INTO `t_device_type_ablitys` VALUES (64, 10, 43);
INSERT INTO `t_device_type_ablitys` VALUES (65, 11, 44);
INSERT INTO `t_device_type_ablitys` VALUES (66, 12, 45);
INSERT INTO `t_device_type_ablitys` VALUES (67, 11, 45);
INSERT INTO `t_device_type_ablitys` VALUES (68, 13, 46);
INSERT INTO `t_device_type_ablitys` VALUES (69, 11, 46);
INSERT INTO `t_device_type_ablitys` VALUES (70, 12, 47);
INSERT INTO `t_device_type_ablitys` VALUES (71, 10, 47);
INSERT INTO `t_device_type_ablitys` VALUES (72, 11, 48);
INSERT INTO `t_device_type_ablitys` VALUES (73, 13, 48);
INSERT INTO `t_device_type_ablitys` VALUES (74, 12, 49);
INSERT INTO `t_device_type_ablitys` VALUES (75, 11, 49);
INSERT INTO `t_device_type_ablitys` VALUES (76, 12, 50);
INSERT INTO `t_device_type_ablitys` VALUES (77, 13, 50);
INSERT INTO `t_device_type_ablitys` VALUES (78, 13, 51);
INSERT INTO `t_device_type_ablitys` VALUES (79, 11, 51);
COMMIT;

-- ----------------------------
-- Table structure for t_deviceid_pool
-- ----------------------------
DROP TABLE IF EXISTS `t_deviceid_pool`;
CREATE TABLE `t_deviceid_pool` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) DEFAULT NULL COMMENT '客户id',
  `deviceId` int(11) DEFAULT NULL COMMENT '设备的deviceId',
  `deviceLicence` varchar(255) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='设备id池表';

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) DEFAULT NULL COMMENT '客户id',
  `publicId` int(11) DEFAULT NULL COMMENT '公众号id',
  `name` varchar(100) DEFAULT NULL COMMENT '产品名称',
  `qrcode` char(10) DEFAULT NULL COMMENT '产品二维码',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpadateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)   COMMENT='微信 设备型号备案表';

-- ----------------------------
-- Table structure for t_productid_pool
-- ----------------------------
DROP TABLE IF EXISTS `t_productid_pool`;
CREATE TABLE `t_productid_pool` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) DEFAULT NULL COMMENT '客户id',
  `productId` int(11) DEFAULT NULL COMMENT '型号的微信的productId',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  ;

-- ----------------------------
-- Table structure for wx_bg_img
-- ----------------------------
DROP TABLE IF EXISTS `wx_bg_img`;
CREATE TABLE `wx_bg_img` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `configId` int(11) DEFAULT NULL COMMENT '配置表的Id',
  `customerId` int(11) DEFAULT NULL COMMENT '客户主键',
  `name` varchar(100) DEFAULT NULL COMMENT '背景图片场景，如：白天，晚上等。',
  `bgImg` varchar(200) DEFAULT NULL COMMENT '背景图片地址',
  `description` varchar(500) DEFAULT NULL COMMENT '背景图片描述',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=7  COMMENT='安卓背景图片表';

-- ----------------------------
-- Records of wx_bg_img
-- ----------------------------
BEGIN;
INSERT INTO `wx_bg_img` VALUES (1, 11, 23, 'string2', 'string2', 'string2', 2, 1535192475546, 1535193055417);
INSERT INTO `wx_bg_img` VALUES (2, 11, 23, 'string3', 'string3', 'string3', 3, 1535192515770, 1535193056729);
INSERT INTO `wx_bg_img` VALUES (3, 11, 23, 'string2', 'string2', 'string2', 1, 1535192874633, NULL);
INSERT INTO `wx_bg_img` VALUES (4, 11, 23, 'string3', 'string3', 'string3', 1, 1535192874659, NULL);
INSERT INTO `wx_bg_img` VALUES (5, 11, 23, 'string2', 'string2', 'string2', 1, 1535192982067, NULL);
INSERT INTO `wx_bg_img` VALUES (6, 11, 23, 'string3', 'string3', 'string3', 1, 1535192982079, NULL);
COMMIT;

-- ----------------------------
-- Table structure for wx_config
-- ----------------------------
DROP TABLE IF EXISTS `wx_config`;
CREATE TABLE `wx_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customerId` int(11) DEFAULT NULL COMMENT '客户id',
  `password` varchar(100) DEFAULT NULL COMMENT '高级设置密码',
  `defaultTeamName` varchar(100) DEFAULT NULL COMMENT '默认组名',
  `htmlTypeId` int(11) DEFAULT NULL COMMENT '页面板式类型',
  `backgroundImg` varchar(200) DEFAULT NULL COMMENT '背景图片',
  `themeName` varchar(100) DEFAULT NULL COMMENT '主题名称',
  `logo` varchar(200) DEFAULT NULL COMMENT 'h5logo图标',
  `version` varchar(100) DEFAULT NULL COMMENT '版本',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `wxConfCustomUni` (`customerId`) USING BTREE
)  AUTO_INCREMENT=13  COMMENT='微信h5端配置表';

-- ----------------------------
-- Records of wx_config
-- ----------------------------
BEGIN;
INSERT INTO `wx_config` VALUES (1, 12, 'string', 'string', 0, 'string', 'string', 'string', 'string', '1', 1535017649781, 1535093926361);
INSERT INTO `wx_config` VALUES (2, 13, 'string1', 'string1', 1, 'string1', 'string1', 'string1', 'string1', '1', 1535080238111, 1535095503893);
INSERT INTO `wx_config` VALUES (3, 14, '2', '2', 2, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/77faf2de7998394fce4f05c789e5e6b8.jpg', '2', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', '2', '1', 1535098205734, NULL);
INSERT INTO `wx_config` VALUES (4, 16, 'string', 'string', 0, 'string', 'string', 'string', 'string', '1', 1535100162780, NULL);
INSERT INTO `wx_config` VALUES (5, 17, '43', '43', 1, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/c9c681a5dd326b0234172e1d213a7eea.jpg', '34343', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', '2', '1', 1535101747997, NULL);
INSERT INTO `wx_config` VALUES (6, 18, '343434', '234234', 1, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/2c2dc0556800c0386b66218240a7a50a--x-rays-plants.jpg', '3434', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/77faf2de7998394fce4f05c789e5e6b8.jpg', '1', '1', 1535102383912, NULL);
INSERT INTO `wx_config` VALUES (7, 19, 'asd', 'asd', 1, '', 'asd', '', '3', '1', 1535107044748, NULL);
INSERT INTO `wx_config` VALUES (8, 20, '1', '1', 1, '', '1', '', '2', '1', 1535109165344, NULL);
INSERT INTO `wx_config` VALUES (9, 21, '1188', 'NOT MORENG', 2, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/thinking3.png', 'my wechat', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/gs.jpg', '2', '1', 1535168536972, NULL);
INSERT INTO `wx_config` VALUES (10, 22, 'string', 'string', 0, 'string', 'string', 'string', 'string', '1', 1535192306789, NULL);
INSERT INTO `wx_config` VALUES (11, 23, 'string', 'string', 0, 'string', 'string', 'string', 'string', '0', 1535192475525, 1535193048996);
INSERT INTO `wx_config` VALUES (12, 25, '45', '234', 2, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-19.png', '456', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/c9c681a5dd326b0234172e1d213a7eea.jpg', '3', '1', 1535609325686, NULL);
COMMIT;

-- ----------------------------
-- Table structure for wx_format
-- ----------------------------
DROP TABLE IF EXISTS `wx_format`;
CREATE TABLE `wx_format` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '页面版式名称',
  `htmlUrl` varchar(200) DEFAULT NULL COMMENT '页面访问地址',
  `icon` varchar(500) DEFAULT NULL COMMENT '首页缩图',
  `previewImg` varchar(255) DEFAULT NULL COMMENT '页面版式预览图',
  `status` int(1) DEFAULT NULL COMMENT '状态：1-正常；2-删除',
  `type` int(1) DEFAULT NULL COMMENT '1-公众号版式；2-型号版式',
  `typeIds` varchar(2000) DEFAULT NULL COMMENT '适用的设备类型（逗号隔开）',
  `owerType` int(1) DEFAULT NULL COMMENT '1-公有；2-私有；3-有限专属',
  `customerIds` varchar(2000) DEFAULT NULL COMMENT '可使用该版式的客户，为空则不做限制',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `version` varchar(200) DEFAULT NULL COMMENT '版本',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=23  COMMENT='微信h5端版型表';

-- ----------------------------
-- Records of wx_format
-- ----------------------------
BEGIN;
INSERT INTO `wx_format` VALUES (1, '1', NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/c9c681a5dd326b0234172e1d213a7eea.jpg', NULL, 2, NULL, NULL, NULL, '21,1', NULL, '2', 1535534162735, 1535636541381);
INSERT INTO `wx_format` VALUES (2, '1', NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-6-580x616.png', NULL, 2, NULL, NULL, NULL, '21,221', NULL, '2', 1535534266807, 1535636544988);
INSERT INTO `wx_format` VALUES (3, 'string', 'string', 'string', 'string', 2, 0, 'string', 0, '3', NULL, 'string', 1535534422608, 1535636652692);
INSERT INTO `wx_format` VALUES (4, 'string', 'string', 'string', 'string', 2, 0, 'string', 0, '4', NULL, 'string', 1535534452586, 1535636740982);
INSERT INTO `wx_format` VALUES (5, 'string', 'string', 'string', 'string', 2, 0, 'string', 0, '5,8', NULL, 'string', 1535534568082, 1535636741699);
INSERT INTO `wx_format` VALUES (6, 'string', 'string', 'string', 'string', 2, 0, 'string', 0, '2,24,21', NULL, 'string', 1535534772350, 1535636742420);
INSERT INTO `wx_format` VALUES (7, 'string', 'string', 'string', 'string', 2, 0, 'string', 0, '6', NULL, 'string', 1535535024647, 1535636742924);
INSERT INTO `wx_format` VALUES (8, '1', NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', NULL, 2, NULL, NULL, NULL, '23', NULL, '2', 1535535062526, 1535636744264);
INSERT INTO `wx_format` VALUES (9, '121231231233', NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', NULL, 2, NULL, NULL, NULL, '22', NULL, '1asdasd23', 1535535770447, 1535636744870);
INSERT INTO `wx_format` VALUES (10, '123', NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', NULL, 2, NULL, NULL, NULL, '22', NULL, '123', 1535536186967, 1535636745485);
INSERT INTO `wx_format` VALUES (11, 'string', 'string', 'string', 'string', 2, 0, '1,2', 0, 'string1112', NULL, 'string', 1535611600034, 1535636746000);
INSERT INTO `wx_format` VALUES (12, 'string3', 'string3', 'string3', 'string3', 2, 0, 'string3', 0, 'string3', 'string3', 'string3', 1535623969269, 1535637211302);
INSERT INTO `wx_format` VALUES (13, '123', NULL, NULL, NULL, 2, NULL, NULL, NULL, '23', '345345', '123123', 1535634218303, 1535637212094);
INSERT INTO `wx_format` VALUES (14, '123', NULL, NULL, NULL, 2, NULL, NULL, NULL, '23', '454', '123', 1535634349210, 1535637212899);
INSERT INTO `wx_format` VALUES (15, '版式1', 'www.html', 'xx.png', 'xxx.img', 2, 1, '1,2,3,4,5', 1, '1,2,3', '这是个版式1', '1.1', 1535634775154, 1535637213558);
INSERT INTO `wx_format` VALUES (16, '123', NULL, NULL, NULL, 2, NULL, NULL, NULL, '23', '454', '123', 1535635315464, 1535637214382);
INSERT INTO `wx_format` VALUES (17, '12312', NULL, NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-11-580x514.png', 2, NULL, NULL, 2, '25', 'asdasd', '3123', 1535635744428, 1535637272033);
INSERT INTO `wx_format` VALUES (18, '版式2', 'www.html', 'xx.png', 'xxx.img', 2, 1, '1,2,3,4,5', 1, '1,2,3', '这是个版式2', '1.1', 1535636010811, 1535637266699);
INSERT INTO `wx_format` VALUES (19, '1', NULL, NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-6-580x616.png', 2, NULL, NULL, 2, '25', '1', '1', 1535636876898, 1535637270384);
INSERT INTO `wx_format` VALUES (20, '1', NULL, NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-6-580x616.png', 1, NULL, NULL, 2, '25', '1', '1', 1535636876897, 1535638069098);
INSERT INTO `wx_format` VALUES (21, '1', NULL, NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-6-580x616.png', 1, NULL, NULL, 2, '25', '1', '1', 1535636916954, NULL);
INSERT INTO `wx_format` VALUES (22, '1', NULL, NULL, 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', 1, NULL, NULL, 2, '23', '3', '2', 1535637975403, NULL);
COMMIT;

-- ----------------------------
-- Table structure for wx_format_items
-- ----------------------------
DROP TABLE IF EXISTS `wx_format_items`;
CREATE TABLE `wx_format_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `formatId` int(11) NOT NULL COMMENT '版式主键',
  `pageId` int(11) DEFAULT NULL COMMENT '所在页面名称',
  `name` varchar(200) DEFAULT NULL COMMENT '中文名',
  `ablityType` int(1) DEFAULT NULL COMMENT '可配置的功能项类型',
  `status` int(1) DEFAULT NULL,
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTIme` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
)  AUTO_INCREMENT=47 ;

-- ----------------------------
-- Records of wx_format_items
-- ----------------------------
BEGIN;
INSERT INTO `wx_format_items` VALUES (3, 7, NULL, 'string', NULL, 1, 1535535028427, NULL);
INSERT INTO `wx_format_items` VALUES (4, 8, NULL, '5', NULL, 1, 1535535062620, NULL);
INSERT INTO `wx_format_items` VALUES (5, 8, NULL, '6', NULL, 1, 1535535062625, NULL);
INSERT INTO `wx_format_items` VALUES (6, 9, NULL, '234234', NULL, 1, 1535535771068, NULL);
INSERT INTO `wx_format_items` VALUES (7, 9, NULL, '4545', NULL, 1, 1535535771073, NULL);
INSERT INTO `wx_format_items` VALUES (8, 9, NULL, '234234', NULL, 1, 1535535807165, NULL);
INSERT INTO `wx_format_items` VALUES (9, 9, NULL, '4545', NULL, 1, 1535535807170, NULL);
INSERT INTO `wx_format_items` VALUES (10, 10, 12, '234234', NULL, 1, 1535536187064, NULL);
INSERT INTO `wx_format_items` VALUES (11, 10, 12, '4545', NULL, 1, 1535536187069, NULL);
INSERT INTO `wx_format_items` VALUES (12, 11, 12, 'string1', NULL, 1, 1535611600114, NULL);
INSERT INTO `wx_format_items` VALUES (13, 11, 11, 'string', NULL, 1, 1535611835814, NULL);
INSERT INTO `wx_format_items` VALUES (14, 11, 11, 'string', NULL, 1, 1535611858767, NULL);
INSERT INTO `wx_format_items` VALUES (15, 11, 11, 'string', NULL, 1, 1535611861670, NULL);
INSERT INTO `wx_format_items` VALUES (16, 11, 11, 'string', NULL, 1, 1535612070236, NULL);
INSERT INTO `wx_format_items` VALUES (17, 11, 12, 'string', NULL, 1, 1535612086277, NULL);
INSERT INTO `wx_format_items` VALUES (18, 12, 12, 'string3', 1, 1, 1535623970368, NULL);
INSERT INTO `wx_format_items` VALUES (19, 13, NULL, '3', 4, 1, 1535634218433, NULL);
INSERT INTO `wx_format_items` VALUES (20, 13, NULL, '1', 2, 1, 1535634218437, NULL);
INSERT INTO `wx_format_items` VALUES (21, 13, NULL, '6', 7, 1, 1535634218444, NULL);
INSERT INTO `wx_format_items` VALUES (22, 13, NULL, '8', 9, 1, 1535634218447, NULL);
INSERT INTO `wx_format_items` VALUES (23, 14, NULL, '456', 456546, 1, 1535634349245, NULL);
INSERT INTO `wx_format_items` VALUES (24, 15, NULL, '版式1的页面1的配置项1', 1, 1, 1535634775217, NULL);
INSERT INTO `wx_format_items` VALUES (25, 15, NULL, '版式1的页面1的配置项2', 1, 1, 1535634775227, NULL);
INSERT INTO `wx_format_items` VALUES (26, 15, NULL, '版式1的页面2的配置项1', 1, 1, 1535634775238, NULL);
INSERT INTO `wx_format_items` VALUES (27, 15, NULL, '版式1的页面2的配置项2', 1, 1, 1535634775245, NULL);
INSERT INTO `wx_format_items` VALUES (28, 16, NULL, '456', 456546, 1, 1535635315492, NULL);
INSERT INTO `wx_format_items` VALUES (29, 17, NULL, '2', 32, 1, 1535635744530, NULL);
INSERT INTO `wx_format_items` VALUES (30, 18, 20, '版式2的页面1的配置项1', 1, 1, 1535636011431, NULL);
INSERT INTO `wx_format_items` VALUES (31, 18, 20, '版式2的页面1的配置项2', 1, 1, 1535636011438, NULL);
INSERT INTO `wx_format_items` VALUES (32, 18, 21, '版式2的页面2的配置项1', 1, 1, 1535636011450, NULL);
INSERT INTO `wx_format_items` VALUES (33, 18, 21, '版式2的页面2的配置项2', 1, 1, 1535636011458, NULL);
INSERT INTO `wx_format_items` VALUES (34, 19, 22, '2', 2, 1, 1535636877028, NULL);
INSERT INTO `wx_format_items` VALUES (35, 20, 23, '2', 2, 1, 1535636877041, 1535638069156);
INSERT INTO `wx_format_items` VALUES (36, 21, 24, '2', 2, 1, 1535636916978, NULL);
INSERT INTO `wx_format_items` VALUES (37, 21, 25, '33', 33, 1, 1535636916983, NULL);
INSERT INTO `wx_format_items` VALUES (38, 21, 25, '44', 55, 1, 1535636916988, NULL);
INSERT INTO `wx_format_items` VALUES (39, 22, 26, '5', 6, 1, 1535637975519, NULL);
INSERT INTO `wx_format_items` VALUES (40, 22, 26, '7', 8, 1, 1535637975524, NULL);
INSERT INTO `wx_format_items` VALUES (41, 22, 27, '10', 11, 1, 1535637975533, NULL);
INSERT INTO `wx_format_items` VALUES (42, 22, 27, '12', 13, 1, 1535637975536, NULL);
INSERT INTO `wx_format_items` VALUES (43, 22, 27, '14', 15, 1, 1535637975540, NULL);
INSERT INTO `wx_format_items` VALUES (44, 20, 23, '88', 99, 1, 1535638043558, 1535638069161);
INSERT INTO `wx_format_items` VALUES (45, 20, 28, '44', 55, 1, 1535638043571, 1535638069167);
INSERT INTO `wx_format_items` VALUES (46, 20, 28, '66', 77, 1, 1535638043574, 1535638069170);
COMMIT;

-- ----------------------------
-- Table structure for wx_format_page
-- ----------------------------
DROP TABLE IF EXISTS `wx_format_page`;
CREATE TABLE `wx_format_page` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `formatId` int(11) DEFAULT NULL COMMENT '版式主键',
  `pageNo` int(5) DEFAULT NULL COMMENT '页号',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `showImg` varchar(200) DEFAULT NULL COMMENT '缩略图',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `createTime` bigint(20) DEFAULT NULL,
  `lastUpdateTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
)  AUTO_INCREMENT=29 ;

-- ----------------------------
-- Records of wx_format_page
-- ----------------------------
BEGIN;
INSERT INTO `wx_format_page` VALUES (1, 7, 0, 'string', 'string', 1, 1535535028296, NULL);
INSERT INTO `wx_format_page` VALUES (2, 8, 1, '4', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/77faf2de7998394fce4f05c789e5e6b8.jpg', 1, 1535535062613, NULL);
INSERT INTO `wx_format_page` VALUES (3, 9, 1, '234234', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-6-580x616.png', 1, 1535535771054, NULL);
INSERT INTO `wx_format_page` VALUES (4, 9, 1, '234234', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-6-580x616.png', 1, 1535535807160, NULL);
INSERT INTO `wx_format_page` VALUES (5, 10, 1, '234234', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-6-580x616.png', 1, 1535536187055, NULL);
INSERT INTO `wx_format_page` VALUES (6, 11, 1, 'string1', 'string1', 1, 1535611600099, NULL);
INSERT INTO `wx_format_page` VALUES (7, 11, 0, 'string', 'string', 1, 1535611835794, NULL);
INSERT INTO `wx_format_page` VALUES (8, 11, 0, 'string', 'string', 1, 1535611858758, NULL);
INSERT INTO `wx_format_page` VALUES (9, 11, 0, 'string', 'string', 1, 1535611861652, NULL);
INSERT INTO `wx_format_page` VALUES (10, 11, 0, 'string', 'string', 1, 1535612070198, NULL);
INSERT INTO `wx_format_page` VALUES (11, 11, 0, 'string', 'string', 1, 1535612086269, NULL);
INSERT INTO `wx_format_page` VALUES (12, 12, 1, 'string3', 'string3', 1, 1535623970332, NULL);
INSERT INTO `wx_format_page` VALUES (13, 13, 1, '345435', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-11-580x514.png', 1, 1535634218422, NULL);
INSERT INTO `wx_format_page` VALUES (14, 13, 2, '5', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', 1, 1535634218442, NULL);
INSERT INTO `wx_format_page` VALUES (15, 14, 1, '456', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-11-580x514.png', 1, 1535634349240, NULL);
INSERT INTO `wx_format_page` VALUES (16, 15, 10, '版式1的页面1', 'xxx.img', 1, 1535634775206, NULL);
INSERT INTO `wx_format_page` VALUES (17, 15, 1, '版式1的页面2', 'xxx1.img', 1, 1535634775235, NULL);
INSERT INTO `wx_format_page` VALUES (18, 16, 1, '456', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-11-580x514.png', 1, 1535635315488, NULL);
INSERT INTO `wx_format_page` VALUES (19, 17, 1, '1', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', 1, 1535635744527, NULL);
INSERT INTO `wx_format_page` VALUES (20, 18, 10, '版式2的页面1', 'xxx.img', 1, 1535636011411, NULL);
INSERT INTO `wx_format_page` VALUES (21, 18, 1, '版式2的页面2', 'xxx1.img', 1, 1535636011447, NULL);
INSERT INTO `wx_format_page` VALUES (22, 19, 1, '2', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-18.png', 1, 1535636876987, NULL);
INSERT INTO `wx_format_page` VALUES (23, 20, 1, '2', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/2c2dc0556800c0386b66218240a7a50a--x-rays-plants.jpg', 1, 1535636876990, 1535638069151);
INSERT INTO `wx_format_page` VALUES (24, 21, 1, '2', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-18.png', 1, 1535636916973, NULL);
INSERT INTO `wx_format_page` VALUES (25, 21, 2, '33', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/5869ac2f522a4f6e2e1febbc1d8709ca.jpg', 1, 1535636916980, NULL);
INSERT INTO `wx_format_page` VALUES (26, 22, 1, '4', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-9.png', 1, 1535637975504, NULL);
INSERT INTO `wx_format_page` VALUES (27, 22, 2, '9', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-19.png', 1, 1535637975528, NULL);
INSERT INTO `wx_format_page` VALUES (28, 20, 2, '33', 'http://mybucket42.oss-cn-beijing.aliyuncs.com/X-rays-of-flowers-by-Hugh-Turvey-18.png', 1, 1535638043562, 1535638069164);
COMMIT;

