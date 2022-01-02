/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : localhost:3306
 Source Schema         : srs

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 01/01/2022 13:49:10
*/

create database 'srs';
user 'srs';

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config_item
-- ----------------------------
DROP TABLE IF EXISTS `config_item`;
CREATE TABLE `config_item`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `val` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '值，多个用空格隔开',
  `pid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父id',
  `type` int(11) NULL DEFAULT NULL COMMENT '1--param  2--block  ',
  `belong` int(11) NULL DEFAULT 1 COMMENT '所属srs',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of config_item
-- ----------------------------
INSERT INTO `config_item` VALUES ('1', 'listen', '1935', NULL, 1, 1, 10);
INSERT INTO `config_item` VALUES ('2', 'max_connections', '1000', NULL, 1, 1, 20);
INSERT INTO `config_item` VALUES ('3', 'daemon', 'on', '', 1, 1, 30);
INSERT INTO `config_item` VALUES ('4', 'inotify_auto_reload', 'on', NULL, 1, 1, 40);
INSERT INTO `config_item` VALUES ('5', 'auto_reload_for_docker', 'on', NULL, 1, 1, 50);
INSERT INTO `config_item` VALUES ('6', 'http_api', NULL, NULL, 2, 1, 60);
INSERT INTO `config_item` VALUES ('61', 'enabled', 'on', '6', 1, 1, 611);
INSERT INTO `config_item` VALUES ('62', 'listen', '1985', '6', 1, 1, 612);
INSERT INTO `config_item` VALUES ('63', 'crossdomain', 'on', '6', 1, 1, 613);
INSERT INTO `config_item` VALUES ('64', 'raw_api', NULL, '6', 2, 1, 614);
INSERT INTO `config_item` VALUES ('641', 'enabled', 'on', '64', 1, 1, 641);
INSERT INTO `config_item` VALUES ('642', 'allow_reload', 'on', '64', 1, 1, 642);
INSERT INTO `config_item` VALUES ('7', 'http_server', NULL, '', 2, 1, 70);
INSERT INTO `config_item` VALUES ('71', 'enabled', 'on', '7', 1, 1, 71);
INSERT INTO `config_item` VALUES ('72', 'listen', '8080', '7', 1, 1, 72);
INSERT INTO `config_item` VALUES ('73', 'dir', './objs/nginx/html', '7', 1, 1, 73);
INSERT INTO `config_item` VALUES ('8', 'vhost', '__defaultVhost__', NULL, 2, 1, 80);
INSERT INTO `config_item` VALUES ('81', 'enabled', 'on', '8', 1, 1, 81);
INSERT INTO `config_item` VALUES ('82', 'hls', '', '8', 2, 1, 82);
INSERT INTO `config_item` VALUES ('821', 'enabled', 'on', '82', 1, 1, 821);
INSERT INTO `config_item` VALUES ('83', 'http_remux', '', '8', 2, 1, 83);
INSERT INTO `config_item` VALUES ('831', 'enabled', 'on', '83', 1, 1, 831);
INSERT INTO `config_item` VALUES ('832', 'mount', '[vhost]/[app]/[stream].flv', '83', 1, 1, 832);

SET FOREIGN_KEY_CHECKS = 1;
