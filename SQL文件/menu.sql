/*
 Navicat Premium Data Transfer

 Source Server         : allen
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : localhost:3306
 Source Schema         : qa

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 14/07/2023 13:52:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `menuCode` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `menuName` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单选项名称',
  `menuRoute` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单导航路由名称',
  `menuRight` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限：0-超级管理员 1-管理员 2-普通用户，可用逗号组合使用',
  `menuComponentPath` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `menuIcon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, '001', '发布问题', 'publish', '0,1', '/visitor/publish', 'CirclePlusFilled');
INSERT INTO `menu` VALUES (2, '002', '提问圈', 'questions', '0', '/visitor/questions', 'HelpFilled');
INSERT INTO `menu` VALUES (3, '003', '消息中心', 'notifications', '0', '/visitor/notifications', 'Bell');
INSERT INTO `menu` VALUES (4, '004', '个人中心', 'workbench', '0', '/visitor/workbench', 'Menu');
INSERT INTO `menu` VALUES (5, '005', '用户管理', 'userManage', '1', '/manage/usersManage', 'UserFilled');
INSERT INTO `menu` VALUES (6, '006', '问题管理', 'questionsManage', '1', '/manage/questionsManage', 'QuestionFilled');
INSERT INTO `menu` VALUES (7, '007', '回答管理', 'answersManage', '1', '/manage/answersManage', 'Comment');
INSERT INTO `menu` VALUES (8, '008', '消息管理', 'messagesManage', '1', '/manage/messagesManage', 'Bell');

SET FOREIGN_KEY_CHECKS = 1;
