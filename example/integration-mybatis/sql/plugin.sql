/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50624
Source Host           : 127.0.0.1:3306
Source Database       : plugin

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2019-07-02 18:13:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for plugin1
-- ----------------------------
DROP TABLE IF EXISTS `plugin1`;
CREATE TABLE `plugin1` (
  `id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of plugin1
-- ----------------------------
INSERT INTO `plugin1` VALUES ('1', 'my');
INSERT INTO `plugin1` VALUES ('4', 'name');
INSERT INTO `plugin1` VALUES ('6', 'is');
INSERT INTO `plugin1` VALUES ('7', 'plugin1');

-- ----------------------------
-- Table structure for plugin2
-- ----------------------------
DROP TABLE IF EXISTS `plugin2`;
CREATE TABLE `plugin2` (
  `id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of plugin2
-- ----------------------------
INSERT INTO `plugin2` VALUES ('1', 'my');
INSERT INTO `plugin2` VALUES ('4', 'name');
INSERT INTO `plugin2` VALUES ('6', 'is');
INSERT INTO `plugin2` VALUES ('7', 'plugin');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `role_id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', 'role');
INSERT INTO `role` VALUES ('2', 'role1');
INSERT INTO `role` VALUES ('4', '4');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL,
  `username` varchar(32) NOT NULL,
  `password` varchar(128) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'zhangsan', 'zhangsan', '123');
INSERT INTO `user` VALUES ('2', 'lisi', 'lisi', '456');
INSERT INTO `user` VALUES ('23', 'wangwu', 'wangwu', '987');
