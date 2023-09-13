CREATE DATABASE IF NOT EXISTS `oauth-center` DEFAULT CHARACTER SET = utf8;

Use `oauth-center`;

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用标识',
  `resource_ids` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资源限定串(逗号分割)',
  `client_secret` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用密钥(bcyt) 加密',
  `client_secret_str` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用密钥(明文)',
  `scope` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '范围',
  `authorized_grant_types` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '5种oauth授权方式(authorization_code,password,refresh_token,client_credentials)',
  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回调地址 ',
  `authorities` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限',
  `access_token_validity` int(11) NULL DEFAULT NULL COMMENT 'access_token有效期',
  `refresh_token_validity` int(11) NULL DEFAULT NULL COMMENT 'refresh_token有效期',
  `additional_information` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '{}' COMMENT '{}',
  `autoapprove` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'true' COMMENT '是否自动授权 是-true',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `client_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '应用名称',
  `support_id_token` tinyint(1) NULL DEFAULT 1 COMMENT '是否支持id_token',
  `id_token_validity` int(11) NULL DEFAULT 60 COMMENT 'id_token有效期',
  `if_limit` int(11) NULL DEFAULT NULL COMMENT '是否应用限流',
  `limit_count` bigint(20) NULL DEFAULT NULL COMMENT '限流阈值',
  `public_key` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公钥',
  `private_key` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '私钥',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES (1, 'webApp', NULL, '$2a$10$06msMGYRH8nrm4iVnKFNKOoddB8wOwymVhbUzw/d3ZixD7Nq8ot72', 'webApp', 'app', 'authorization_code,password,refresh_token,client_credentials,implicit,password_code,openId,mobile_password,cas_ticket,faceId,password_sso,password_goole,password_smkey', NULL, NULL, 3600, NULL, '{\"LOGOUT_NOTIFY_URL_LIST\":\"http://127.0.0.1:8082/logoutNotify\"}', 'true', NULL, NULL, 'pc端', 1, 60, 0, 10000, NULL, NULL);
INSERT INTO `oauth_client_details` VALUES (2, 'app', '', '{bcrypt}$2a$04$0BnMM7SnOx4g8hg3dWfws.YozSMNCE84Zb0oQWTwenQb2Xlb8gCLm', 'app', 'all', 'authorization_code,password,refresh_token', 'http://127.0.0.1:8081/callback.html', '', 3600, 18000, '{}', 'true', NULL, '2023-09-12 10:19:43', '移动端', 1, 60, 0, 10000, '', '');
INSERT INTO `oauth_client_details` VALUES (3, 'owen', NULL, '$2a$10$a1ZEXiZQr604LN.wVxet.etPm6RvDs.HIaXP48J2HKRaEnZORTVwe', 'owen', 'app', 'authorization_code,password,refresh_token,client_credentials', 'http://127.0.0.1:9997/clientOne/login', NULL, 3600, 28800, '{}', 'true', '2018-12-27 00:50:30', '2018-12-27 00:50:30', '第三方应用', 1, 60, 0, 10000, NULL, NULL);

-- ----------------------------
-- Table structure for worker_node
-- ----------------------------
DROP TABLE IF EXISTS `worker_node`;
CREATE TABLE `worker_node`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
  `host_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'host name',
  `port` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'port',
  `type` int(11) NOT NULL COMMENT 'node type: actual or container',
  `launch_date` date NOT NULL COMMENT 'launch date',
  `modified` timestamp(0) NULL DEFAULT NULL COMMENT 'modified time',
  `created` timestamp(0) NULL DEFAULT NULL COMMENT 'created time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'db workerid assigner for uid generator' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of worker_node
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
