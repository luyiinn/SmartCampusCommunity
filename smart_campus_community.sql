/*
 Navicat Premium Data Transfer

 Source Server         : Connection
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : smart_campus_community

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 26/12/2025 16:45:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_at` timestamp NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `student_id` bigint NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID（主键）',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `is_anonymous` tinyint NULL DEFAULT 0 COMMENT '是否匿名（0-否，1-是）',
  `reply_comment_id` bigint NULL DEFAULT NULL COMMENT '回复评论ID',
  `reply_user_id` bigint NULL DEFAULT NULL COMMENT '回复用户ID',
  `create_at` datetime NOT NULL COMMENT '评论时间',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `update_at` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除（0-正常，1-删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_comment_time`(`create_at` ASC) USING BTREE,
  INDEX `idx_reply_user_id`(`reply_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 29, 10, '这可能是本项目第一条成功发布的评论，也是第三次发布的评论', 0, NULL, NULL, '2025-11-24 20:45:40', 0, '2025-11-24 20:45:40', 0);
INSERT INTO `comment` VALUES (2, 29, 10, '这是本项目第二条成功发布的评论', 0, NULL, NULL, '2025-11-24 20:46:39', 0, '2025-11-24 20:46:39', 0);
INSERT INTO `comment` VALUES (3, 29, 10, '这是本项目第三条成功发布的评论', 0, NULL, NULL, '2025-11-24 20:47:08', 0, '2025-11-24 20:47:08', 0);
INSERT INTO `comment` VALUES (4, 29, 10, '来！', 0, NULL, NULL, '2025-11-24 20:49:09', 0, '2025-11-24 20:49:09', 0);
INSERT INTO `comment` VALUES (5, 29, 10, '来！', 0, NULL, NULL, '2025-11-24 20:49:24', 0, '2025-11-24 20:49:24', 0);
INSERT INTO `comment` VALUES (6, 29, 10, '嘿嘿', 0, NULL, NULL, '2025-11-24 20:52:30', 0, '2025-11-24 20:52:30', 0);
INSERT INTO `comment` VALUES (7, 29, 10, '完成！耶耶耶', 0, NULL, NULL, '2025-11-24 21:32:11', 0, '2025-11-24 21:32:11', 0);
INSERT INTO `comment` VALUES (8, 29, 9, '我是苹果，我也来发条评论！', 0, NULL, NULL, '2025-11-24 21:37:19', 0, '2025-11-24 21:37:19', 0);
INSERT INTO `comment` VALUES (9, 29, 9, '测试匿名评论', 1, NULL, NULL, '2025-11-24 21:37:27', 0, '2025-11-24 21:37:27', 0);
INSERT INTO `comment` VALUES (10, 29, 9, '其实我是苹果~', 1, NULL, NULL, '2025-11-24 21:40:53', 0, '2025-11-24 21:40:53', 0);
INSERT INTO `comment` VALUES (11, 29, 9, '测试评论后评论数是否+1', 0, NULL, NULL, '2025-11-24 21:49:47', 0, '2025-11-24 21:49:47', 0);
INSERT INTO `comment` VALUES (12, 28, 9, '你好我是苹果', 0, NULL, NULL, '2025-11-24 21:51:35', 0, '2025-11-24 21:51:35', 0);
INSERT INTO `comment` VALUES (13, 30, 9, '听起来蛮有趣的', 0, NULL, NULL, '2025-11-24 21:52:48', 0, '2025-11-24 21:52:48', 0);
INSERT INTO `comment` VALUES (14, 30, 9, '123123', 1, NULL, NULL, '2025-11-24 21:57:30', 0, '2025-11-24 21:57:30', 0);
INSERT INTO `comment` VALUES (15, 30, 3, '111', 0, NULL, NULL, '2025-11-25 13:39:30', 0, '2025-11-25 13:39:30', 0);
INSERT INTO `comment` VALUES (16, 32, 10, '帖子点赞前端已正常', 0, NULL, NULL, '2025-11-27 21:20:29', 0, '2025-11-27 21:20:29', 0);
INSERT INTO `comment` VALUES (17, 32, 9, '确定', 0, NULL, NULL, '2025-11-27 21:35:22', 0, '2025-11-27 21:35:22', 0);
INSERT INTO `comment` VALUES (18, 32, 9, '？', 0, NULL, NULL, '2025-11-27 21:44:33', 0, '2025-11-27 21:44:33', 0);
INSERT INTO `comment` VALUES (19, 32, 9, '回复', 0, NULL, 9, '2025-11-27 21:50:45', 0, '2025-11-27 21:50:45', 0);
INSERT INTO `comment` VALUES (20, 32, 9, '回复', 0, NULL, NULL, '2025-11-27 21:54:08', 0, '2025-11-27 21:54:08', 0);
INSERT INTO `comment` VALUES (21, 32, 9, '测试回复', 0, NULL, 9, '2025-11-27 22:02:22', 0, '2025-11-27 22:02:22', 0);
INSERT INTO `comment` VALUES (22, 32, 9, '测试回复', 0, NULL, 9, '2025-11-27 22:02:22', 0, '2025-11-27 22:02:22', 0);
INSERT INTO `comment` VALUES (23, 32, 9, '回复功能已正常！', 0, NULL, 10, '2025-11-27 22:07:43', 0, '2025-11-27 22:07:43', 0);
INSERT INTO `comment` VALUES (24, 32, 9, '测试有无回复评论id', 0, NULL, NULL, '2025-11-27 22:11:51', 0, '2025-11-27 22:11:51', 0);
INSERT INTO `comment` VALUES (25, 32, 9, '测试回复评论id', 0, NULL, 9, '2025-11-27 22:12:04', 0, '2025-11-27 22:12:04', 0);
INSERT INTO `comment` VALUES (26, 32, 9, '测试回复评论id2', 0, 25, 9, '2025-11-27 22:13:56', 0, '2025-11-27 22:13:56', 0);
INSERT INTO `comment` VALUES (27, 30, 9, '评论！！', 0, NULL, NULL, '2025-11-27 22:27:53', 0, '2025-11-27 22:27:53', 0);
INSERT INTO `comment` VALUES (28, 30, 9, '回复！！！', 0, 27, 9, '2025-11-27 22:28:00', 0, '2025-11-27 22:28:00', 0);
INSERT INTO `comment` VALUES (29, 33, 9, '测试评论', 0, NULL, NULL, '2025-12-01 14:00:04', 0, '2025-12-01 14:00:04', 0);
INSERT INTO `comment` VALUES (30, 33, 9, '测试回复', 0, 29, 9, '2025-12-01 14:00:10', 0, '2025-12-01 14:00:10', 0);
INSERT INTO `comment` VALUES (31, 40, 9, '冬雪如梦似幻的乌托邦', 0, NULL, NULL, '2025-12-02 13:27:25', 0, '2025-12-02 13:27:25', 0);
INSERT INTO `comment` VALUES (32, 40, 9, '是的', 0, 31, 9, '2025-12-02 13:31:29', 0, '2025-12-02 13:31:29', 0);
INSERT INTO `comment` VALUES (33, 40, 9, '123', 1, NULL, NULL, '2025-12-02 13:32:02', 0, '2025-12-02 13:32:02', 0);
INSERT INTO `comment` VALUES (34, 40, 9, '123', 1, NULL, NULL, '2025-12-02 13:34:42', 0, '2025-12-02 13:34:42', 0);
INSERT INTO `comment` VALUES (35, 40, 9, '123', 1, NULL, NULL, '2025-12-02 13:35:16', 0, '2025-12-02 13:35:16', 0);
INSERT INTO `comment` VALUES (36, 41, 10, '后人哀之而不鉴之，使后人而复哀后人也。', 0, NULL, NULL, '2025-12-02 13:42:03', 0, '2025-12-02 13:42:03', 0);
INSERT INTO `comment` VALUES (37, 41, 12, '💕💕💕💕💕💕', 0, NULL, NULL, '2025-12-02 13:54:51', 0, '2025-12-02 13:54:51', 0);
INSERT INTO `comment` VALUES (38, 42, 9, '这就是艺术吗', 0, NULL, NULL, '2025-12-02 13:54:53', 0, '2025-12-02 13:54:53', 0);
INSERT INTO `comment` VALUES (39, 43, 9, '经典', 0, NULL, NULL, '2025-12-02 13:58:16', 0, '2025-12-02 13:58:16', 0);
INSERT INTO `comment` VALUES (40, 46, 9, '666', 0, NULL, NULL, '2025-12-08 19:28:57', 0, '2025-12-08 19:28:57', 0);
INSERT INTO `comment` VALUES (41, 47, 9, 'AI说的', 0, NULL, NULL, '2025-12-08 19:31:08', 0, '2025-12-08 19:31:08', 0);
INSERT INTO `comment` VALUES (42, 48, 9, '评论', 0, NULL, NULL, '2025-12-19 13:09:29', 0, '2025-12-19 13:09:29', 0);

-- ----------------------------
-- Table structure for diary
-- ----------------------------
DROP TABLE IF EXISTS `diary`;
CREATE TABLE `diary`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容（最大长度1000字符）',
  `is_public` tinyint NOT NULL DEFAULT 0 COMMENT '是否公开：0-私密，1-公开',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-发布，2-草稿，3-私有',
  `view_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览数',
  `like_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_status_created`(`status` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 81 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of diary
-- ----------------------------
INSERT INTO `diary` VALUES (1, 4, '今天又挂了一科', '高数期中只考了42分…感觉保研彻底没希望了。不想跟爸妈说，怕他们失望。', 0, 1, 0, 0, 0, '2024-10-03 23:15:00', '2024-10-03 23:15:00');
INSERT INTO `diary` VALUES (2, 4, '凌晨三点的代码', 'debug到天亮，终于跑通了。但明天还有早八，我真的好累。', 0, 1, 0, 0, 0, '2024-10-10 03:20:00', '2024-10-10 03:20:00');
INSERT INTO `diary` VALUES (3, 4, '和室友吵架了', '他总在熄灯后开灯打游戏，说了几次都不听。今天直接摔门出去了…', 0, 1, 0, 0, 0, '2024-10-18 22:40:00', '2024-10-18 22:40:00');
INSERT INTO `diary` VALUES (4, 4, '想家', '妈妈今天发来一篮子橙子，快递到了。剥开的时候突然哭了。', 0, 1, 0, 0, 0, '2024-10-25 19:05:00', '2024-10-25 19:05:00');
INSERT INTO `diary` VALUES (5, 4, '草稿：明天面试准备', '技术面可能会问Redis缓存穿透…还没复习完，好慌。（未完成）', 0, 2, 0, 0, 0, '2024-11-01 21:30:00', '2024-11-01 21:30:00');
INSERT INTO `diary` VALUES (6, 4, '今天阳光很好', '坐在图书馆靠窗的位置，晒着太阳，突然觉得一切也没那么糟。', 1, 1, 12, 3, 0, '2024-11-12 14:20:00', '2024-11-12 14:20:00');
INSERT INTO `diary` VALUES (7, 5, '失恋第三天', '删掉了所有合照，但他送的围巾还在抽屉里。不敢扔。', 0, 1, 0, 0, 0, '2024-10-05 01:10:00', '2024-10-05 01:10:00');
INSERT INTO `diary` VALUES (8, 5, '社团面试被拒', '明明准备得很充分，为什么还是不要我？是不是因为我不是学生会的？', 0, 1, 0, 0, 0, '2024-10-11 18:50:00', '2024-10-11 18:50:00');
INSERT INTO `diary` VALUES (9, 5, '食堂阿姨多打了肉', '今天心情突然变好了！原来幸福就是一块红烧肉。', 1, 1, 28, 7, 0, '2024-10-19 12:10:00', '2024-10-19 12:10:00');
INSERT INTO `diary` VALUES (10, 5, '论文查重58%', '怎么办…明明自己写的，怎么重复率这么高？', 0, 1, 0, 0, 0, '2024-10-27 22:00:00', '2024-10-27 22:00:00');
INSERT INTO `diary` VALUES (11, 5, '下雨没带伞', '淋成落汤鸡回宿舍，感冒了。今天真是倒霉透顶。', 0, 1, 0, 0, 0, '2024-11-03 17:45:00', '2024-11-03 17:45:00');
INSERT INTO `diary` VALUES (12, 5, '草稿：给他的道歉信', '其实那天是我太敏感了…但说不出口。（没发）', 0, 2, 0, 0, 0, '2024-11-15 23:00:00', '2024-11-15 23:00:00');
INSERT INTO `diary` VALUES (13, 6, '四级又没过', '第三次了。同学都过了，只有我还在原地踏步。', 0, 1, 0, 0, 0, '2024-10-07 20:30:00', '2024-10-07 20:30:00');
INSERT INTO `diary` VALUES (14, 6, '兼职发传单的一天', '站了六小时，赚了80块。手酸脚痛，但月底饭钱有着落了。', 0, 1, 0, 0, 0, '2024-10-13 19:20:00', '2024-10-13 19:20:00');
INSERT INTO `diary` VALUES (15, 6, '今天被老师表扬了', '小组汇报拿了第一！原来努力真的会被看见。', 1, 1, 35, 9, 0, '2024-10-21 16:40:00', '2024-10-21 16:40:00');
INSERT INTO `diary` VALUES (16, 6, '银行卡被盗刷', '半夜收到短信，少了500块。报警+挂失折腾到凌晨四点。', 0, 1, 0, 0, 0, '2024-10-29 04:10:00', '2024-10-29 04:10:00');
INSERT INTO `diary` VALUES (17, 6, '室友生日', '偷偷给他订了蛋糕，他感动得快哭了。嘿嘿。', 1, 1, 42, 15, 0, '2024-11-05 21:00:00', '2024-11-05 21:00:00');
INSERT INTO `diary` VALUES (18, 6, '不想上课', '外面下雪了，好想窝在被子里睡一天。', 0, 1, 0, 0, 0, '2024-11-18 08:15:00', '2024-11-18 08:15:00');
INSERT INTO `diary` VALUES (19, 7, '考研倒计时60天', '每天学12小时，但还是觉得自己什么都不会。焦虑到失眠。', 0, 1, 0, 0, 0, '2024-10-08 02:00:00', '2024-10-08 02:00:00');
INSERT INTO `diary` VALUES (20, 7, '妈妈生病了', '不敢告诉她我在学校吃泡面省钱，怕她担心。', 0, 1, 0, 0, 0, '2024-10-16 22:30:00', '2024-10-16 22:30:00');
INSERT INTO `diary` VALUES (21, 7, '捡到一只小猫', '在实验楼后面，瘦得皮包骨。偷偷喂了三天，它跟我回家了。', 1, 1, 105, 48, 0, '2024-10-23 18:00:00', '2024-10-23 18:00:00');
INSERT INTO `diary` VALUES (22, 7, '体测跑800米晕倒', '醒来在医务室，同学说我脸色惨白。以后再也不熬夜了。', 0, 1, 0, 0, 0, '2024-10-31 15:20:00', '2024-10-31 15:20:00');
INSERT INTO `diary` VALUES (23, 7, '奖学金名单公布了', '差0.3分！就差那么一点点…', 0, 1, 0, 0, 0, '2024-11-07 12:00:00', '2024-11-07 12:00:00');
INSERT INTO `diary` VALUES (24, 7, '草稿：未来规划', '如果考研失败，要不要gap一年？', 0, 2, 0, 0, 0, '2024-11-20 20:45:00', '2024-11-20 20:45:00');
INSERT INTO `diary` VALUES (25, 8, '第一次做家教', '小学生问我“1+1为什么等于2”，我竟答不上来…', 1, 1, 67, 22, 0, '2024-10-09 19:10:00', '2024-10-09 19:10:00');
INSERT INTO `diary` VALUES (26, 8, '手机掉进水池', '捞上来已经黑屏了。里面还有没交的作业照片！', 0, 1, 0, 0, 0, '2024-10-17 13:40:00', '2024-10-17 13:40:00');
INSERT INTO `diary` VALUES (27, 8, '暗恋的人坐我前排', '今天他回头借橡皮，我心跳快停了。', 0, 1, 0, 0, 0, '2024-10-24 10:25:00', '2024-10-24 10:25:00');
INSERT INTO `diary` VALUES (28, 8, '双十一剁手后悔', '买了一堆用不上的东西，饭卡余额只剩32块。', 0, 1, 0, 0, 0, '2024-11-13 23:50:00', '2024-11-13 23:50:00');
INSERT INTO `diary` VALUES (29, 8, '参加志愿者活动', '去养老院陪老人聊天，一位奶奶拉着我的手叫我“囡囡”。', 1, 1, 88, 31, 0, '2024-11-22 17:30:00', '2024-11-22 17:30:00');
INSERT INTO `diary` VALUES (30, 8, '草稿：辞职信', '不想干学生会了，太消耗情绪。', 0, 2, 0, 0, 0, '2024-11-25 01:20:00', '2024-11-25 01:20:00');
INSERT INTO `diary` VALUES (31, 9, '实验报告又迟交', '助教说再迟就挂我。可我真的做不出来…', 0, 1, 0, 0, 0, '2024-10-14 21:00:00', '2024-10-14 21:00:00');
INSERT INTO `diary` VALUES (32, 9, '和爸妈视频哭了', '他们问我“在学校开心吗”，我点头，然后屏幕一黑。', 0, 1, 0, 0, 0, '2024-10-20 22:15:00', '2024-10-20 22:15:00');
INSERT INTO `diary` VALUES (33, 9, '校运会跑了三千米', '最后一名，但全班都在喊我名字。值了。', 1, 1, 120, 40, 0, '2024-10-26 16:50:00', '2024-10-26 16:50:00');
INSERT INTO `diary` VALUES (34, 9, '误入男生宿舍', '送错快递，被宿管阿姨骂了半小时…', 0, 1, 0, 0, 0, '2024-11-02 11:30:00', '2024-11-02 11:30:00');
INSERT INTO `diary` VALUES (35, 9, '收到匿名纸条', '“你笑起来很好看”——是谁？', 0, 1, 0, 0, 0, '2024-11-09 14:00:00', '2024-11-09 14:00:00');
INSERT INTO `diary` VALUES (36, 9, '草稿：退学申请？', '读不下去了，想出去打工。', 0, 2, 0, 0, 0, '2024-11-28 03:00:00', '2024-11-28 03:00:00');
INSERT INTO `diary` VALUES (37, 10, '今天没吃午饭', '省下15块，给妹妹买了练习册。', 0, 1, 0, 0, 0, '2024-10-04 13:20:00', '2024-10-04 13:20:00');
INSERT INTO `diary` VALUES (38, 10, '英语演讲比赛', '忘词了，台下一片安静。好想找个地缝钻进去。', 0, 1, 0, 0, 0, '2024-10-12 19:40:00', '2024-10-12 19:40:00');
INSERT INTO `diary` VALUES (39, 10, '校园流浪狗生了', '在旧教学楼后面，五只小狗！偷偷喂了奶粉。', 1, 1, 210, 76, 0, '2024-10-22 18:30:00', '2025-12-25 17:42:16');
INSERT INTO `diary` VALUES (40, 10, '被导员叫去谈话', '因为旷课太多…其实是因为兼职。', 0, 1, 0, 0, 0, '2024-10-30 16:00:00', '2024-10-30 16:00:00');
INSERT INTO `diary` VALUES (41, 10, '第一次拿稿费', '校刊刊登了我的散文，50块钱！请室友吃了泡面加肠。', 1, 1, 55, 19, 0, '2024-11-06 20:10:00', '2025-12-25 17:42:15');
INSERT INTO `diary` VALUES (42, 10, '冬天来了', '宿舍没有暖气，写字手都是抖的。', 0, 1, 0, 0, 0, '2024-11-24 08:50:00', '2024-11-24 08:50:00');
INSERT INTO `diary` VALUES (43, 11, '实习面试失败', 'HR说我“缺乏实践经验”…可你们根本不给机会啊！', 0, 1, 0, 0, 0, '2024-10-02 17:20:00', '2024-10-02 17:20:00');
INSERT INTO `diary` VALUES (44, 11, '帮室友代课被抓', '现在我和他一起挂科了。真不该贪那20块钱。', 0, 1, 0, 0, 0, '2024-10-15 09:10:00', '2024-10-15 09:10:00');
INSERT INTO `diary` VALUES (45, 11, '看到爸妈的白头发', '视频通话时，爸爸把镜头转过去太快了，但我看到了。', 0, 1, 0, 0, 0, '2024-10-28 21:05:00', '2024-10-28 21:05:00');
INSERT INTO `diary` VALUES (46, 11, '篮球赛赢了', '最后一秒投进三分！兄弟们把我抬起来了。', 1, 1, 150, 52, 0, '2024-11-04 18:20:00', '2024-11-04 18:20:00');
INSERT INTO `diary` VALUES (47, 11, '感冒一周还没好', '没钱去医院，只能硬扛。', 0, 1, 0, 0, 0, '2024-11-11 10:40:00', '2024-11-11 10:40:00');
INSERT INTO `diary` VALUES (48, 11, '草稿：给未来的自己', '希望毕业那天，我能笑着走出校门。', 1, 2, 5, 2, 0, '2024-11-26 22:30:00', '2024-11-26 22:30:00');
INSERT INTO `diary` VALUES (49, 9, '新年第一天', '2025年了，希望今年能瘦十斤，四级过了，爸妈身体健康。', 0, 1, 0, 0, 0, '2025-01-01 00:30:00', '2025-01-01 00:30:00');
INSERT INTO `diary` VALUES (50, 9, '期末复习崩溃', '图书馆坐了一天，脑子还是浆糊。高数真的要挂了。', 0, 1, 0, 0, 0, '2025-01-10 22:15:00', '2025-01-10 22:15:00');
INSERT INTO `diary` VALUES (51, 9, '寒假回家', '火车硬座12小时，但看到妈妈在站台等我，一切都值得。', 1, 1, 42, 12, 0, '2025-01-20 18:40:00', '2025-01-20 18:40:00');
INSERT INTO `diary` VALUES (52, 9, '过年被催恋爱', '亲戚又问有没有对象，我说“学习要紧”，其实只是没人喜欢我吧。', 0, 1, 0, 0, 0, '2025-02-05 20:00:00', '2025-02-05 20:00:00');
INSERT INTO `diary` VALUES (53, 9, '返校路上', '行李箱轮子坏了，拖了一路。新学期，加油吧。', 0, 1, 0, 0, 0, '2025-02-24 15:20:00', '2025-02-24 15:20:00');
INSERT INTO `diary` VALUES (54, 9, '选修课抢到了！', '手速爆发，终于抢到《电影鉴赏》，不用早八了！', 1, 1, 35, 8, 0, '2025-03-03 12:10:00', '2025-03-03 12:10:00');
INSERT INTO `diary` VALUES (55, 9, '和室友闹别扭', '他把我的泡面吃了没说，虽然是小事，但心里很不舒服。', 0, 1, 0, 0, 0, '2025-03-12 21:45:00', '2025-03-12 21:45:00');
INSERT INTO `diary` VALUES (56, 9, '春招开始了', '投了20份简历，0回复。是不是我太差了？', 0, 1, 0, 0, 0, '2025-03-20 19:30:00', '2025-03-20 19:30:00');
INSERT INTO `diary` VALUES (57, 9, '樱花开了', '镜湖边的樱花全开了，一个人拍照也挺好看。', 1, 1, 68, 25, 0, '2025-03-28 16:00:00', '2025-03-28 16:00:00');
INSERT INTO `diary` VALUES (58, 9, '体测又来了', '引体向上0个，被体育老师笑了。好想消失。', 0, 1, 0, 0, 0, '2025-04-05 14:20:00', '2025-04-05 14:20:00');
INSERT INTO `diary` VALUES (59, 9, '兼职发传单', '站了一天，赚了60块。脚底起泡了，但月底饭钱有了。', 0, 1, 0, 0, 0, '2025-04-12 18:50:00', '2025-04-12 18:50:00');
INSERT INTO `diary` VALUES (60, 9, '小组作业背锅', '他们都没做，最后我熬夜做完，组长却说“大家合作很好”。', 0, 1, 0, 0, 0, '2025-04-18 03:10:00', '2025-04-18 03:10:00');
INSERT INTO `diary` VALUES (61, 9, '妈妈生日', '偷偷用兼职钱买了条围巾寄回家。她打电话哭了。', 1, 1, 55, 19, 0, '2025-04-25 20:30:00', '2025-04-25 20:30:00');
INSERT INTO `diary` VALUES (62, 9, '草稿：我想退学', '每天都很累，不知道读下去有什么意义。（未完成）', 0, 2, 0, 0, 0, '2025-05-02 01:15:00', '2025-05-02 01:15:00');
INSERT INTO `diary` VALUES (63, 9, '五一没回家', '票太贵了，留在学校。宿舍空荡荡的，有点想家。', 0, 1, 0, 0, 0, '2025-05-03 11:00:00', '2025-05-03 11:00:00');
INSERT INTO `diary` VALUES (64, 9, '实验报告通过了', '助教说写得不错！原来我也可以做好一件事。', 1, 1, 28, 7, 0, '2025-05-10 17:40:00', '2025-05-10 17:40:00');
INSERT INTO `diary` VALUES (65, 9, '下雨没带伞', '淋成落汤鸡回宿舍，感冒了。今天真是倒霉。', 0, 1, 0, 0, 0, '2025-05-18 18:20:00', '2025-05-18 18:20:00');
INSERT INTO `diary` VALUES (66, 9, '收到奖学金通知', '三等奖！虽然不多，但够交下学期书费了。', 1, 1, 92, 33, 0, '2025-05-25 10:05:00', '2025-05-25 10:05:00');
INSERT INTO `diary` VALUES (67, 9, '暗恋的人有对象了', '看到他朋友圈牵手照，默默删掉了收藏的表情包。', 0, 1, 0, 0, 0, '2025-06-01 23:50:00', '2025-06-01 23:50:00');
INSERT INTO `diary` VALUES (68, 9, '毕业照拍摄', '穿上学士服，突然意识到真的要离开了。', 1, 1, 120, 45, 0, '2025-06-15 09:30:00', '2025-06-15 09:30:00');
INSERT INTO `diary` VALUES (69, 9, '暑期实习第一天', '公司好大，同事都好厉害。我连打印机都不会用。', 0, 1, 0, 0, 0, '2025-07-05 19:00:00', '2025-07-05 19:00:00');
INSERT INTO `diary` VALUES (70, 9, '租房被骗', '押金2000块，房东失联了。报警也没用。', 0, 1, 0, 0, 0, '2025-07-12 14:10:00', '2025-07-12 14:10:00');
INSERT INTO `diary` VALUES (71, 9, '第一次发工资', '税后3280元！请自己吃了一顿火锅。', 1, 1, 47, 15, 0, '2025-07-31 20:20:00', '2025-07-31 20:20:00');
INSERT INTO `diary` VALUES (72, 9, '台风天', '上海台风，公司放假。终于能睡个好觉了。', 0, 1, 0, 0, 0, '2025-08-10 11:30:00', '2025-08-10 11:30:00');
INSERT INTO `diary` VALUES (73, 9, '实习结束', '主管说我“有潜力”，给了转正机会。但我想考研。', 0, 1, 0, 0, 0, '2025-08-30 16:45:00', '2025-08-30 16:45:00');
INSERT INTO `diary` VALUES (74, 9, '回家陪奶奶', '她记性越来越差，但还记得我爱吃糖醋排骨。', 1, 1, 78, 29, 0, '2025-09-08 18:00:00', '2025-09-08 18:00:00');
INSERT INTO `diary` VALUES (75, 9, '考研报名', '确认了学校。只剩100天了，拼一把！', 0, 1, 0, 1, 0, '2025-10-15 22:00:00', '2025-12-19 13:39:56');
INSERT INTO `diary` VALUES (76, 9, '秋招最后机会', '又面挂了。可能我真的不适合职场吧。', 0, 1, 0, 1, 0, '2025-10-28 19:15:00', '2025-12-19 13:39:55');
INSERT INTO `diary` VALUES (77, 9, '初雪', '上海下雪了！虽然就几分钟，但好浪漫。', 1, 1, 105, 42, 0, '2025-11-22 07:40:00', '2025-12-19 13:40:03');
INSERT INTO `diary` VALUES (78, 9, '今天12月1日', '离考研还有25天。希望一切顺利。', 0, 1, 0, 1, 0, '2025-12-01 08:00:00', '2025-12-19 13:39:36');
INSERT INTO `diary` VALUES (79, 9, '下雪', '今天又下雪了', 1, 1, 0, 0, 0, '2025-12-19 14:19:32', '2025-12-20 14:21:10');
INSERT INTO `diary` VALUES (80, 9, '测试', '123123', 0, 1, 0, 1, 0, '2025-12-20 13:56:54', '2025-12-25 13:39:44');

-- ----------------------------
-- Table structure for diary_images
-- ----------------------------
DROP TABLE IF EXISTS `diary_images`;
CREATE TABLE `diary_images`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `diary_id` bigint NOT NULL COMMENT '所属日志ID',
  `image_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片存储路径',
  `created_at` timestamp NULL DEFAULT NULL COMMENT '创建时间（由后端设置）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '日志图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diary_images
-- ----------------------------
INSERT INTO `diary_images` VALUES (1, 79, '/uploads/13673db4-9614-4248-bddf-b0be4efabbbd.png', '2025-12-19 14:19:32');

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID（主键）',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `is_public` tinyint NOT NULL COMMENT '是否公开（0否，1是）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` tinyint NOT NULL COMMENT '状态',
  `like_count` int NOT NULL COMMENT '点赞数',
  `is_deleted` tinyint NOT NULL COMMENT '逻辑删除',
  `creative_at` datetime NOT NULL COMMENT '创建时间',
  `update_at` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_creative_time`(`creative_at` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of log
-- ----------------------------

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容（最大长度1000字符）',
  `is_anonymous` tinyint NOT NULL DEFAULT 0 COMMENT '是否匿名：0-不匿名，1-匿名',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-发布，2-草稿，3-私有',
  `view_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览数',
  `like_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论数',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_status_created`(`status` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 3, '哎哟哈哈不甜', 'ad qui mollit ut', 1, 1, 0, 0, 0, 0, '2025-11-16 16:42:08', '2025-11-16 16:42:08');
INSERT INTO `post` VALUES (2, 3, '哎哟哈哈不甜', 'ad qui mollit ut', 1, 1, 0, 0, 0, 0, '2025-11-16 16:43:02', '2025-11-16 16:43:02');
INSERT INTO `post` VALUES (8, 3, '冬天来啦', '冬天真来了吗？来了来了！！！', 0, 1, 0, 0, 0, 0, '2025-11-23 18:15:59', '2025-11-23 18:15:59');
INSERT INTO `post` VALUES (9, 8, '出方便面', '就我头像这种，诚心出', 0, 1, 0, 0, 0, 0, '2025-11-24 12:48:47', '2025-11-24 12:48:47');
INSERT INTO `post` VALUES (10, 8, '出方便面啊~~~', '我再发一条，组长让我凑个数', 0, 1, 0, 0, 0, 0, '2025-11-24 12:50:05', '2025-11-24 12:50:05');
INSERT INTO `post` VALUES (11, 8, '出，方便面', '再来一条吧', 0, 1, 0, 0, 0, 0, '2025-11-24 12:52:45', '2025-11-24 12:52:45');
INSERT INTO `post` VALUES (12, 9, '打招呼', '大家好啊，我是新来的苹果', 0, 1, 0, 0, 0, 0, '2025-11-24 12:55:40', '2025-11-24 12:55:40');
INSERT INTO `post` VALUES (13, 9, '苹果', '其实我的头像是以前的项目里翻出来的', 0, 1, 0, 0, 0, 0, '2025-11-24 12:57:12', '2025-11-24 12:57:12');
INSERT INTO `post` VALUES (14, 9, '坚持', '坚持一下，马上就够了，已经9条了', 0, 1, 0, 0, 0, 0, '2025-11-24 12:58:24', '2025-11-24 12:58:24');
INSERT INTO `post` VALUES (15, 9, '十！！！！！', '第十条，已经够一页了！！', 0, 1, 0, 0, 0, 0, '2025-11-24 12:59:26', '2025-11-24 12:59:26');
INSERT INTO `post` VALUES (16, 10, '新一页', '大家好啊，第二分页来了', 0, 1, 0, 0, 0, 0, '2025-11-24 13:01:08', '2025-11-24 13:01:08');
INSERT INTO `post` VALUES (17, 10, 'bug', '好像没有成功展示第二页', 0, 1, 0, 0, 0, 0, '2025-11-24 13:01:40', '2025-11-24 13:01:40');
INSERT INTO `post` VALUES (18, 10, '修复', '已经可以正常展示！', 0, 1, 0, 0, 0, 0, '2025-11-24 13:22:33', '2025-11-24 13:22:33');
INSERT INTO `post` VALUES (19, 10, '凑数', '11111111111111111', 0, 1, 0, 0, 0, 0, '2025-11-24 13:23:21', '2025-11-24 13:23:21');
INSERT INTO `post` VALUES (20, 10, '头像', '顺带一提，这个头像时ai画的', 0, 1, 0, 1, 0, 0, '2025-11-24 13:24:35', '2025-12-02 15:44:03');
INSERT INTO `post` VALUES (21, 10, '名字', '因为头发时黄色的，所以名字叫yellow', 0, 1, 0, 0, 0, 0, '2025-11-24 13:26:37', '2025-11-24 13:26:37');
INSERT INTO `post` VALUES (22, 10, '方便面你是谁！', '谁知道方便面的联系方式啊', 0, 1, 0, 0, 0, 0, '2025-11-24 13:27:28', '2025-11-24 13:27:28');
INSERT INTO `post` VALUES (23, 11, 'hello', '还认得出我吗', 0, 1, 0, 0, 0, 0, '2025-11-24 13:29:21', '2025-11-24 13:29:21');
INSERT INTO `post` VALUES (24, 11, '测试一下', '嘿嘿嘿嘿嘿嘿嘿嘿', 0, 1, 0, 0, 0, 0, '2025-11-24 13:29:57', '2025-11-24 13:29:57');
INSERT INTO `post` VALUES (25, 11, '这样子呢', '你还认得出我吗', 1, 1, 0, 0, 0, 0, '2025-11-24 13:30:17', '2025-11-24 13:30:17');
INSERT INTO `post` VALUES (26, 11, '悄悄告诉你', '悄悄告诉你，虽然表面上是匿名的，但后台仍然知道你是谁哦', 1, 1, 0, 0, 0, 0, '2025-11-24 13:33:10', '2025-11-24 13:33:10');
INSERT INTO `post` VALUES (27, 11, '关键词', '下面测试关键词搜索', 0, 1, 0, 1, 0, 0, '2025-11-24 13:33:41', '2025-11-27 18:39:52');
INSERT INTO `post` VALUES (28, 10, '关键词测试', '关键词测试通过', 0, 1, 0, 2, 1, 0, '2025-11-24 18:26:38', '2025-12-19 23:33:00');
INSERT INTO `post` VALUES (29, 10, '评论区', '评论测试！！！', 0, 1, 0, 2, 1, 0, '2025-11-24 19:40:01', '2025-12-19 23:32:59');
INSERT INTO `post` VALUES (30, 9, '新，评论测试', '此处用于测试更新评论计数后的评论接口', 0, 1, 0, 2, 5, 0, '2025-11-24 21:52:32', '2025-11-27 22:28:00');
INSERT INTO `post` VALUES (31, 10, '点赞', '测试点赞功能', 0, 1, 0, 4, 0, 0, '2025-11-27 20:29:52', '2025-11-27 21:34:16');
INSERT INTO `post` VALUES (32, 10, '点赞', '修复点赞逻辑，点赞数据不一致的问题', 0, 1, 1, 2, 11, 0, '2025-11-27 20:33:52', '2025-11-28 10:24:16');
INSERT INTO `post` VALUES (33, 9, '测试java升级', '升级java版本8-17', 1, 1, 0, 1, 2, 0, '2025-12-01 13:59:40', '2025-12-19 23:32:56');
INSERT INTO `post` VALUES (34, 9, '测试最大字符数', '庆历四年春，滕子京谪守巴陵郡。\n越明年，政通人和，百废具兴。\n乃重修岳阳楼，增其旧制，刻唐贤今人诗赋于其上。\n属予作文以记之。\n(具通：俱)\n　　予观夫巴陵胜状，在洞庭一湖。\n衔远山，吞长江，浩浩汤汤，横无际涯；朝晖夕阴，气象万千。\n此则岳阳楼之大观也，前人之述备矣。\n然则北通巫峡，南极潇湘，迁客骚人，多会于此，览物之情，得无异乎？\n　　若夫淫雨霏霏，连月不开，阴风怒号，浊浪排空；日星隐耀，山岳潜形；商旅不行，樯倾楫摧；薄暮冥冥，虎啸猿啼。\n登斯楼也，则有去国怀乡，忧谗畏讥，满目萧然，感极而悲者矣。\n(隐耀一作：隐曜；淫雨通：霪雨)\n　　至若春和景明，波澜不惊，上下天光，一碧万顷；沙鸥翔集，锦鳞游泳；岸芷汀兰，郁郁青青。\n而或长烟一空，皓月千里，浮光跃金，静影沉璧，渔歌互答，此乐何极！\n登斯楼也，则有心旷神怡，宠辱偕忘，把酒临风，其喜洋洋者矣。\n　　嗟夫！\n予尝求古仁人之心，或异二者之为，何哉？\n不以物喜，不以己悲；居庙堂之高则忧其民；处江湖之远则忧其君。\n是进亦忧，退亦忧。\n然则何时而乐耶？\n其必曰：“先天下之忧而忧，后天下之乐而乐”乎。\n噫！\n微斯人，吾谁与归？\n时六年九月十五日。', 0, 1, 1, 1, 0, 0, '2025-12-01 14:01:12', '2025-12-19 23:32:55');
INSERT INTO `post` VALUES (35, 9, '讨贼檄文', '盖闻明主图危以制变，忠臣虑难以立权。是以有非常之人，然后有非常之事；有非常之事，然后立非常之功。\n　　夫非常者，固非常人所拟也。曩者，强秦弱主，赵高执柄，专制朝权，威福由己；时人迫胁，莫敢正言；终有望夷之败，祖宗焚灭，污辱至今，永为世鉴。及臻吕后季年，产禄专政，内兼二军，外统梁、赵；擅断万机，决事省禁；下陵上替，海内寒心。\n　　于是绛侯朱虚兴兵奋怒，诛夷逆暴，尊立太宗，故能王道兴隆，光明显融：此则大臣立权之明表也。司空曹操：祖父中常侍腾，与左悺、徐璜并作妖孽，饕餮放横，伤化虐民；父嵩，乞匄携养，因赃假位，舆金辇璧，输货权门，窃盗鼎司，倾覆重器。操赘阉遗丑，本无懿德，犭票狡锋协，好乱乐祸。\n　　幕府董统鹰扬，扫除凶逆；续遇董卓，侵官暴国。于是提剑挥鼓，发命东夏，收罗英雄，弃瑕取用；故遂与操同谘合谋，授以裨师，谓其鹰犬之才，爪牙可任。至乃愚佻短略，轻进易退，伤夷折衄，数丧师徒；幕府辄复分兵命锐，修完补辑，表行东郡，领兖州刺史，被以虎文，奖戚威柄，冀获秦师一克之报。而操遂承资跋扈，恣行凶忒，割剥元元，残贤害善。故九江太守边让，英才俊伟，天下知名；直言正色，论不阿谄；身首被枭悬之诛，妻孥受灰灭之咎。\n　　自是士林愤痛，民怨弥重；一夫奋臂，举州同声。故躬破于徐方，地夺于吕布；彷徨东裔，蹈据无所。幕府惟强干弱枝之义，且不登叛人之党，故复援旌擐甲，席卷起征，金鼓响振，布众奔沮；拯其死亡之患，复其方伯之位：则幕府无德于兖土之民，而有大造于操也。\n　　后会銮驾返旆，群虏寇攻。时冀州方有北鄙之警，匪遑离局；故使从事中郎徐勋，就发遣操，使缮修郊庙，翊卫幼主。\n　　操便放志：专行胁迁，当御省禁；卑侮王室，败法乱纪；坐领三台，专制朝政；爵赏由心，弄戮在口；所爱光五宗，所恶灭三族；群谈者受显诛，腹议者蒙隐戮；百僚钳口，道路以目；尚书记朝会，公卿充员品而已故太尉杨彪，典历二司，享国极位。操因缘眦睚，被以非罪；榜楚参并，五毒备至；触情任忒，不顾宪纲。又议郎赵彦，忠谏直言，义有可纳，是以圣朝含听，改容加饰。\n　　操欲迷夺时明，杜绝言路，擅收立杀，不俟报国。\n　　又梁孝王，先帝母昆，坟陵尊显；桑梓松柏，犹宜肃恭。而操帅将吏士，亲临发掘，破棺裸尸，掠取金宝。至令圣朝流涕，士民伤怀！操又特置发丘中郎将、摸金校尉，所过隳突，无骸不露。\n　　身处三公之位，而行桀虏之态，污国害民，毒施人鬼！加其细致惨苛，科防互设；罾缴充蹊，坑阱塞路；举手挂网罗，动足触机陷：是以兖、豫有无聊之民，帝都有吁嗟之怨。历观载籍，无道之臣，贪残酷烈，于操为甚！幕府方诘外奸，未及整训；加绪含容，冀可弥缝。而操豺狼野心，潜包祸谋，乃欲摧挠栋梁，孤弱汉室，除灭忠正，专为袅雄。往者伐鼓北征公孙瓒，强寇桀逆，拒围一年。操因其未破，阴交书命，外助王师，内相掩袭。会其行人发露，瓒亦枭夷，故使锋芒挫缩，厥图不果。今乃屯据敷仓，阻河为固，欲以螳螂之斧，御隆车之隧。\n　　幕府奉汉威灵，折冲宇宙；长戟百万，胡骑千群；奋中黄育获之士，骋良弓劲弩之势；并州越太行，青州涉济漯；大军泛黄河而角其前，荆州下宛叶而掎其后：雷震虎步，若举炎火以焫飞蓬，覆沧海以沃[火票]炭，有何不灭者哉？又操军吏士，其可战者，皆出自幽冀，或故营部曲，咸怨旷思归，流涕北顾。其余兖豫之民，及吕布张杨之余众，覆亡迫胁，权时苟从；各被创夷，人为仇敌。\n　　若回旆方徂，登高冈而击鼓吹，扬素挥以启降路，必土崩瓦解，不俟血刃。方今汉室陵迟，纲维弛绝；圣朝无一介之辅，股肱无折冲之势。方畿之内，简练之臣，皆垂头□翼，莫所凭恃；虽有忠义之佐，胁于暴虐之臣，焉能展其节？又操持部曲精兵七百，围守宫阙，外托宿卫，内实拘执。惧其篡逆之萌，因斯而作。此乃忠臣肝脑涂地之秋，烈士立功之会，可不勖哉！\n　　操又矫命称制，遣使发兵。恐边远州郡，过听给与，违众旅叛，举以丧名，为天下笑，则明哲不取也。即日幽并青冀四州并进。书到荆州，便勒现兵，与建忠将军协同声势。州郡各整义兵，罗落境界，举武扬威，并匡社稷：则非常之功于是乎著。\n　其得操首者，封五千户侯，赏钱五千万。部曲偏裨将校诸吏降者，勿有所问。广宜恩信，班扬符赏，布告天下，咸使知圣朝有拘迫之难。如律令！', 0, 1, 0, 1, 0, 0, '2025-12-01 14:03:38', '2025-12-19 23:32:54');
INSERT INTO `post` VALUES (36, 9, '测试', '图片上传~~', 0, 1, 0, 1, 0, 0, '2025-12-01 14:39:48', '2025-12-19 23:32:53');
INSERT INTO `post` VALUES (37, 9, '上传', '1111111', 0, 1, 0, 1, 0, 0, '2025-12-01 14:58:25', '2025-12-19 23:32:52');
INSERT INTO `post` VALUES (38, 9, '上传', '1111111', 0, 1, 1, 1, 0, 0, '2025-12-01 14:58:57', '2025-12-19 23:32:52');
INSERT INTO `post` VALUES (39, 9, '图传展示测试', 'abcdefghigklmn', 0, 1, 2, 0, 0, 0, '2025-12-01 15:41:30', '2025-12-08 15:42:02');
INSERT INTO `post` VALUES (40, 9, '冬日暖阳下的城市剪影', '十二月初的清晨，寒意悄然弥漫，但一缕柔和的阳光穿透薄雾，洒在城市的街道上，为冷冽的空气添上一抹温情。行人们裹紧外套，步履匆匆，却也不时抬头望向那片澄澈的天空，仿佛在与冬日的宁静对话。街角的咖啡店飘出阵阵香气，玻璃窗上凝结着淡淡的水汽，映出屋内温暖的灯光与笑语。这座城市，在寒冷中依然保持着它的节奏与温度——既有钢筋水泥的理性，也有人间烟火的柔软。冬日虽寒，却因这些细微的光与暖，让人感受到生活的踏实与希望。', 0, 1, 14, 1, 5, 0, '2025-12-01 16:07:34', '2025-12-20 13:11:12');
INSERT INTO `post` VALUES (41, 10, '阿房宫赋', '唐代·杜牧\n六王毕，四海一，蜀山兀，阿房出。覆压三百余里，隔离天日。骊山北构而西折，直走咸阳。二川溶溶，流入宫墙。五步一楼，十步一阁；廊腰缦回，檐牙高啄；各抱地势，钩心斗角。盘盘焉，囷囷焉，蜂房水涡，矗不知其几千万落。长桥卧波，未云何龙？复道行空，不霁何虹？高低冥迷，不知西东。歌台暖响，春光融融；舞殿冷袖，风雨凄凄。一日之内，一宫之间，而气候不齐。\n\n妃嫔媵嫱，王子皇孙，辞楼下殿，辇来于秦。朝歌夜弦，为秦宫人。明星荧荧，开妆镜也；绿云扰扰，梳晓鬟也；渭流涨腻，弃脂水也；烟斜雾横，焚椒兰也。雷霆乍惊，宫车过也；辘辘远听，杳不知其所之也。一肌一容，尽态极妍，缦立远视，而望幸焉；有不得见者三十六年。燕赵之收藏，韩魏之经营，齐楚之精英，几世几年，剽掠其人，倚叠如山；一旦不能有，输来其间，鼎铛玉石，金块珠砾，弃掷逦迤，秦人视之，亦不甚惜。\n\n嗟乎！一人之心，千万人之心也。秦爱纷奢，人亦念其家。奈何取之尽锱铢，用之如泥沙？使负栋之柱，多于南亩之农夫；架梁之椽，多于机上之工女；瓦缝参差，多于周身之帛缕；直栏横槛多于九土之城郭；钉头磷磷，多于在庾之粟粒；管弦呕哑，多于市人之言语。使天下之人，不敢言而敢怒。独夫之心，日益骄固。戍卒叫，函谷举，楚人一炬，可怜焦土！\n\n呜呼！灭六国者六国也，非秦也。族秦者秦也，非天下也。嗟夫！使六国各爱其人，则足以拒秦；使秦复爱六国之人，则递三世可至万世而为君，谁得而族灭也？秦人不暇自哀，而后人哀之；后人哀之而不鉴之，亦使后人而复哀后人也。\n\n【译文】：\n六国覆灭，天下统一。四川山林中的树木被砍伐一空，阿房宫殿得以建成。（它）覆盖了三百多里地，几乎遮蔽了天日。从骊山的北面建起，曲折地向西延伸，一直通到咸阳。渭水和樊川，浩浩荡荡地流进了宫墙。五步一座高楼，十步一座亭阁；长廊如带，迂回曲折，屋檐高挑，象鸟喙一样在半空飞啄。这些亭台楼阁啊，各自凭借不同的地势，参差环抱，回廊环绕象钩心，飞檐高耸象斗角。弯弯转转，曲折回环，象蜂房那样密集，如水涡那样套连，巍巍峨峨，不知道它们有几千万座。那长桥卧在水面上（象蛟龙），（可是）没有一点云彩，怎么会有蛟龙飞腾？那楼阁之间的通道架在半空（象彩虹），（可是）并非雨过天晴，怎么会有虹霓产生？高高低低的楼阁，幽冥迷离，使人辨不清南北西东。高台上传来歌声，使人感到暖意，如同春天一般温暖；大殿里舞袖飘拂，使人感到寒气，仿佛风雨交加那样凄冷。就在同一天内，同一座宫里，而气候冷暖却截然不同。\n\n（六国的）宫女妃嫔、诸侯王族的女儿孙女，辞别了故国的宫殿阁楼，乘坐辇车来到秦国。（她们）早上唱歌，晚上弹琴，成为秦皇的宫人。（清晨）只见星光闪烁，（原来是她们）打开了梳妆的明镜；又见乌云纷纷扰扰，（原来是她们）一早在梳理发鬓；渭水泛起一层油腻，（是她们）泼下的脂粉水呀；轻烟缭绕，香雾弥漫，是她们焚烧的椒兰异香。忽然雷霆般的响声震天，（原来是）宫车从这里驰过；辘辘的车轮声渐听渐远，不知它驶向何方。（宫女们）极力显示自己的妩媚娇妍，每一处肌肤，每一种姿态，都极为动人。（她们）久久地伫立着，眺望着，希望皇帝能宠幸光临；（可怜）有的人三十六年始终未曾见过皇帝的身影。燕国赵国收藏的奇珍，韩国魏国聚敛的金银，齐国楚国保存的瑰宝，都是多少年、多少代，从人民手中掠夺来的，堆积如山。一旦国家破亡，不能再占有，都运送到阿房宫中。（从此）宝鼎（看作）铁锅，宝玉（看作）石头，黄金（当成）土块，珍珠（当作）砂砾，乱丢乱扔，秦人看着，也不觉得可惜。\n\n唉！一个人所想的，也是千万人所想的。秦始皇喜欢繁华奢侈，老百姓也眷念着自己的家。为什么搜刮财宝时连一分一厘也不放过，挥霍起来却把它当作泥沙一样呢？甚至使得（阿房宫）支承大梁的柱子，比田里的农夫还要多；架在屋梁上的椽子，比织机上的织女还要多；参差不齐的瓦缝，比人们身上穿的丝缕还要多；直的栏杆，横的门槛，比九州的城廊还要多；琴声笛声，嘈杂一片，比闹市里的人声还要喧闹。（这）使天下人们口里虽不敢说，但心里却充满了愤怒。秦始皇这暴君的心却日益骄横顽固。于是陈胜吴广揭竿而起，刘邦攻破函谷关；项羽放了一把大火，可惜那豪华的宫殿就变成了一片焦土！\n\n唉！灭六国的是六国自己，不是秦国。灭秦国的是秦王自己，不是天下的人民。唉！如果六国的国君能各自爱抚自己的百姓，就足以抵抗秦国了；（秦统一后）如果也能爱惜六国的百姓，那就可以传位到三世以至传到万世做皇帝，谁能够灭亡他呢？秦国的统治者来不及为自己的灭亡而哀叹，却使后代人为它哀叹；如果后代人哀叹它而不引以为鉴，那么又要让更后的人来哀叹他们了。', 0, 1, 9, 1, 2, 0, '2025-12-02 13:40:39', '2025-12-20 13:00:48');
INSERT INTO `post` VALUES (42, 12, '我画的画', '这是艺术品', 0, 1, 11, 1, 1, 0, '2025-12-02 13:54:07', '2025-12-25 15:08:01');
INSERT INTO `post` VALUES (43, 12, '你的名字', '你的名字壁纸', 0, 1, 14, 0, 1, 0, '2025-12-02 13:58:01', '2025-12-20 13:00:44');
INSERT INTO `post` VALUES (44, 12, '新海诚', '新海诚电影', 0, 1, 15, 0, 0, 0, '2025-12-02 14:03:09', '2025-12-20 13:00:41');
INSERT INTO `post` VALUES (46, 13, '寒冷的夜', '谁来帮帮我把maven环境配了', 1, 1, 4, 1, 1, 0, '2025-12-08 19:27:45', '2025-12-20 13:00:02');
INSERT INTO `post` VALUES (47, 13, '骷髅打金服', '嘎嘎（为什么需要五个字符）', 0, 1, 11, 0, 1, 0, '2025-12-08 19:30:00', '2025-12-20 12:59:57');
INSERT INTO `post` VALUES (48, 9, '评论点赞', '测试评论点赞', 0, 1, 11, 0, 1, 0, '2025-12-19 13:09:22', '2025-12-20 13:19:56');
INSERT INTO `post` VALUES (49, 9, '找手机', '我今天在b教231丢了一部手机', 0, 1, 0, 0, 0, 0, '2025-12-26 16:37:04', '2025-12-26 16:37:04');
INSERT INTO `post` VALUES (50, 10, '古城正定', '今天，我踏上了河北省正定县这片充满历史韵味的土地。  \n\n古塔耸立，城墙斑驳，每一步都仿佛走在时光的回廊里。这里没有喧嚣的人潮，却有千年沉淀的文化气息扑面而来。一砖一瓦，都在静静诉说着燕赵大地的过往故事。\n\n一次出发，一场与历史的对话——正定，比想象中更值得一见。', 0, 1, 0, 0, 0, 0, '2025-12-26 16:41:02', '2025-12-26 16:41:02');

-- ----------------------------
-- Table structure for post_image
-- ----------------------------
DROP TABLE IF EXISTS `post_image`;
CREATE TABLE `post_image`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint UNSIGNED NOT NULL COMMENT '所属帖子ID',
  `image_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片存储路径',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_image
-- ----------------------------
INSERT INTO `post_image` VALUES (1, 38, '/uploads/6146ca7d-008c-4c70-984a-f14ff8f1c3a2.png', '2025-12-01 14:58:57');
INSERT INTO `post_image` VALUES (2, 38, '/uploads/f7705a69-4641-4249-8b10-e0331bc6df61.png', '2025-12-01 14:58:57');
INSERT INTO `post_image` VALUES (3, 38, '/uploads/8dcc4c45-36df-4543-9daa-dbc835bc01dc.png', '2025-12-01 14:58:57');
INSERT INTO `post_image` VALUES (4, 38, '/uploads/55991071-6727-4149-b8c7-d3708865575d.png', '2025-12-01 14:58:57');
INSERT INTO `post_image` VALUES (5, 39, '/uploads/7481013a-a52c-4e27-be8b-de5d933de5a9.png', '2025-12-01 15:41:30');
INSERT INTO `post_image` VALUES (6, 39, '/uploads/a0c45e92-ae01-4e63-bfcc-ccc1155538dd.png', '2025-12-01 15:41:30');
INSERT INTO `post_image` VALUES (7, 39, '/uploads/4859c03c-b144-4756-a142-ee431f1411d8.png', '2025-12-01 15:41:30');
INSERT INTO `post_image` VALUES (8, 39, '/uploads/a522f60f-824f-46d0-8ca2-6b7a2e58305b.png', '2025-12-01 15:41:30');
INSERT INTO `post_image` VALUES (9, 40, '/uploads/e52bd8b0-717a-4577-9ef4-0feeec1cd2b1.png', '2025-12-01 16:07:34');
INSERT INTO `post_image` VALUES (10, 40, '/uploads/28623dcb-f4e3-4b84-8f92-2682e26f8519.png', '2025-12-01 16:07:34');
INSERT INTO `post_image` VALUES (11, 41, '/uploads/9a0b3a4e-52ec-463d-ae8b-6e660b5d3697.jpg', '2025-12-02 13:40:39');
INSERT INTO `post_image` VALUES (12, 41, '/uploads/2d385fe4-2bd5-4727-afa3-777d402f4b60.jpg', '2025-12-02 13:40:39');
INSERT INTO `post_image` VALUES (13, 41, '/uploads/6e3d3fde-fddd-4c5a-bef3-a67bc99144e9.jpg', '2025-12-02 13:40:39');
INSERT INTO `post_image` VALUES (14, 42, '/uploads/dd4dc079-adc1-43f9-a17a-8206bf87a747.png', '2025-12-02 13:54:07');
INSERT INTO `post_image` VALUES (15, 43, '/uploads/d279c4ca-51e3-4416-9a0b-b92e132f2bd5.jpg', '2025-12-02 13:58:01');
INSERT INTO `post_image` VALUES (16, 44, '/uploads/49ddf7a0-64a8-4aaa-a66f-09d320f83e87.mp4', '2025-12-02 14:03:09');
INSERT INTO `post_image` VALUES (17, 45, '/uploads/8281e312-078e-4d3d-9801-657e3599e40c.mp3', '2025-12-02 14:05:27');
INSERT INTO `post_image` VALUES (18, 47, '/uploads/7be77c4e-f36a-4b99-a7b6-e0d6fdfece57.png', '2025-12-08 19:30:00');
INSERT INTO `post_image` VALUES (19, 48, '/uploads/aa56d4af-771d-40df-be85-d63f65128668.jpg', '2025-12-19 13:09:22');
INSERT INTO `post_image` VALUES (20, 49, '/uploads/076075bc-1f67-4eb7-a44d-8b53dbbe4662.jpg', '2025-12-26 16:37:04');
INSERT INTO `post_image` VALUES (21, 50, '/uploads/71993883-f4f6-418d-983b-ea382ebec022.jpeg', '2025-12-26 16:41:02');
INSERT INTO `post_image` VALUES (22, 50, '/uploads/dcfefb8e-eaa3-4877-85a6-dc7e748488bb.jpg', '2025-12-26 16:41:02');

-- ----------------------------
-- Table structure for post_tag
-- ----------------------------
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `post_id` bigint UNSIGNED NOT NULL COMMENT '帖子ID',
  `tag_id` bigint UNSIGNED NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_tag`(`post_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '帖子标签关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_tag
-- ----------------------------
INSERT INTO `post_tag` VALUES (1, 1, 1);
INSERT INTO `post_tag` VALUES (2, 1, 4);
INSERT INTO `post_tag` VALUES (3, 2, 1);
INSERT INTO `post_tag` VALUES (4, 2, 4);
INSERT INTO `post_tag` VALUES (8, 8, 1);
INSERT INTO `post_tag` VALUES (9, 8, 6);
INSERT INTO `post_tag` VALUES (10, 9, 4);
INSERT INTO `post_tag` VALUES (12, 10, 4);
INSERT INTO `post_tag` VALUES (11, 10, 6);
INSERT INTO `post_tag` VALUES (13, 11, 6);
INSERT INTO `post_tag` VALUES (14, 12, 6);
INSERT INTO `post_tag` VALUES (15, 13, 6);
INSERT INTO `post_tag` VALUES (17, 14, 6);
INSERT INTO `post_tag` VALUES (16, 14, 49);
INSERT INTO `post_tag` VALUES (18, 15, 6);
INSERT INTO `post_tag` VALUES (22, 16, 3);
INSERT INTO `post_tag` VALUES (23, 16, 4);
INSERT INTO `post_tag` VALUES (21, 16, 12);
INSERT INTO `post_tag` VALUES (20, 16, 14);
INSERT INTO `post_tag` VALUES (19, 16, 39);
INSERT INTO `post_tag` VALUES (24, 17, 6);
INSERT INTO `post_tag` VALUES (25, 18, 6);
INSERT INTO `post_tag` VALUES (26, 19, 21);
INSERT INTO `post_tag` VALUES (28, 19, 22);
INSERT INTO `post_tag` VALUES (29, 19, 23);
INSERT INTO `post_tag` VALUES (27, 19, 24);
INSERT INTO `post_tag` VALUES (30, 19, 25);
INSERT INTO `post_tag` VALUES (31, 20, 26);
INSERT INTO `post_tag` VALUES (32, 21, 6);
INSERT INTO `post_tag` VALUES (33, 22, 5);
INSERT INTO `post_tag` VALUES (34, 23, 47);
INSERT INTO `post_tag` VALUES (35, 24, 1);
INSERT INTO `post_tag` VALUES (36, 25, 1);
INSERT INTO `post_tag` VALUES (37, 26, 1);
INSERT INTO `post_tag` VALUES (38, 26, 6);
INSERT INTO `post_tag` VALUES (39, 27, 6);
INSERT INTO `post_tag` VALUES (40, 28, 26);
INSERT INTO `post_tag` VALUES (41, 29, 6);
INSERT INTO `post_tag` VALUES (42, 30, 5);
INSERT INTO `post_tag` VALUES (43, 31, 47);
INSERT INTO `post_tag` VALUES (44, 32, 4);
INSERT INTO `post_tag` VALUES (45, 33, 1);
INSERT INTO `post_tag` VALUES (46, 33, 47);
INSERT INTO `post_tag` VALUES (48, 34, 12);
INSERT INTO `post_tag` VALUES (47, 34, 13);
INSERT INTO `post_tag` VALUES (49, 35, 6);
INSERT INTO `post_tag` VALUES (50, 35, 47);
INSERT INTO `post_tag` VALUES (51, 36, 1);
INSERT INTO `post_tag` VALUES (52, 36, 4);
INSERT INTO `post_tag` VALUES (53, 37, 16);
INSERT INTO `post_tag` VALUES (54, 38, 16);
INSERT INTO `post_tag` VALUES (55, 39, 4);
INSERT INTO `post_tag` VALUES (57, 40, 27);
INSERT INTO `post_tag` VALUES (56, 40, 47);
INSERT INTO `post_tag` VALUES (58, 41, 10);
INSERT INTO `post_tag` VALUES (59, 42, 10);
INSERT INTO `post_tag` VALUES (60, 43, 20);
INSERT INTO `post_tag` VALUES (61, 44, 20);
INSERT INTO `post_tag` VALUES (62, 45, 21);
INSERT INTO `post_tag` VALUES (63, 46, 47);
INSERT INTO `post_tag` VALUES (64, 47, 8);
INSERT INTO `post_tag` VALUES (65, 48, 6);
INSERT INTO `post_tag` VALUES (66, 49, 2);
INSERT INTO `post_tag` VALUES (67, 50, 6);
INSERT INTO `post_tag` VALUES (68, 50, 19);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `use_count` int NULL DEFAULT NULL COMMENT '使用次数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '匿名', NULL, 6672);
INSERT INTO `tag` VALUES (2, '寻物', NULL, 124);
INSERT INTO `tag` VALUES (3, '表白', NULL, 66);
INSERT INTO `tag` VALUES (4, '出售闲置', NULL, 1006);
INSERT INTO `tag` VALUES (5, '寻人', NULL, 72);
INSERT INTO `tag` VALUES (6, '吐槽', NULL, 2016);
INSERT INTO `tag` VALUES (7, '交友', NULL, 120);
INSERT INTO `tag` VALUES (8, '游戏', NULL, 51);
INSERT INTO `tag` VALUES (9, '体育', NULL, 0);
INSERT INTO `tag` VALUES (10, '艺术', NULL, 2);
INSERT INTO `tag` VALUES (11, '拼车', NULL, 0);
INSERT INTO `tag` VALUES (12, '找搭子', NULL, 78);
INSERT INTO `tag` VALUES (13, '社团', NULL, 121);
INSERT INTO `tag` VALUES (14, '学习', NULL, 78);
INSERT INTO `tag` VALUES (15, '考研', NULL, 130);
INSERT INTO `tag` VALUES (16, '求职', NULL, 132);
INSERT INTO `tag` VALUES (17, '租房', NULL, 0);
INSERT INTO `tag` VALUES (18, '美食', NULL, 44);
INSERT INTO `tag` VALUES (19, '旅游', NULL, 34);
INSERT INTO `tag` VALUES (20, '电影', NULL, 34);
INSERT INTO `tag` VALUES (21, '音乐', NULL, 2);
INSERT INTO `tag` VALUES (22, '读书', NULL, 1);
INSERT INTO `tag` VALUES (23, '健身', NULL, 1);
INSERT INTO `tag` VALUES (24, '宠物', NULL, 1);
INSERT INTO `tag` VALUES (25, '科技', NULL, 1);
INSERT INTO `tag` VALUES (26, '编程', NULL, 2);
INSERT INTO `tag` VALUES (27, '摄影', NULL, 1);
INSERT INTO `tag` VALUES (28, '绘画', NULL, 0);
INSERT INTO `tag` VALUES (29, '舞蹈', NULL, 0);
INSERT INTO `tag` VALUES (30, '手工', NULL, 0);
INSERT INTO `tag` VALUES (31, '养生', NULL, 0);
INSERT INTO `tag` VALUES (33, '汽车', NULL, 0);
INSERT INTO `tag` VALUES (34, '金融', NULL, 0);
INSERT INTO `tag` VALUES (35, '创业', NULL, 0);
INSERT INTO `tag` VALUES (36, '二手交易', NULL, 0);
INSERT INTO `tag` VALUES (37, '兼职', NULL, 0);
INSERT INTO `tag` VALUES (38, '实习', NULL, 0);
INSERT INTO `tag` VALUES (39, '竞赛', NULL, 78);
INSERT INTO `tag` VALUES (40, '讲座', NULL, 0);
INSERT INTO `tag` VALUES (41, '志愿者', NULL, 0);
INSERT INTO `tag` VALUES (42, '环保', NULL, 0);
INSERT INTO `tag` VALUES (43, '公益', NULL, 0);
INSERT INTO `tag` VALUES (44, '心理健康', NULL, 0);
INSERT INTO `tag` VALUES (45, '法律咨询', NULL, 0);
INSERT INTO `tag` VALUES (46, '医疗健康', NULL, 0);
INSERT INTO `tag` VALUES (47, '校园生活', NULL, 506);
INSERT INTO `tag` VALUES (48, '工作经验', NULL, 0);
INSERT INTO `tag` VALUES (49, '情感交流', NULL, 1);
INSERT INTO `tag` VALUES (50, '时事讨论', NULL, 0);

-- ----------------------------
-- Table structure for user_comment_likes
-- ----------------------------
DROP TABLE IF EXISTS `user_comment_likes`;
CREATE TABLE `user_comment_likes`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `like_status` tinyint NOT NULL COMMENT '点赞状态：1-点赞，0-取消点赞',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_comment`(`user_id` ASC, `comment_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户评论点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_comment_likes
-- ----------------------------
INSERT INTO `user_comment_likes` VALUES (1, 9, 42, 0);

-- ----------------------------
-- Table structure for user_diary_likes
-- ----------------------------
DROP TABLE IF EXISTS `user_diary_likes`;
CREATE TABLE `user_diary_likes`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `diary_id` bigint NOT NULL COMMENT '日志ID',
  `like_status` tinyint NOT NULL COMMENT '点赞状态：1-点赞，0-取消点赞',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_diary`(`user_id` ASC, `diary_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户日志点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_diary_likes
-- ----------------------------
INSERT INTO `user_diary_likes` VALUES (1, 9, 78, 1);
INSERT INTO `user_diary_likes` VALUES (2, 9, 77, 1);
INSERT INTO `user_diary_likes` VALUES (4, 9, 76, 1);
INSERT INTO `user_diary_likes` VALUES (5, 9, 75, 1);
INSERT INTO `user_diary_likes` VALUES (7, 9, 79, 0);
INSERT INTO `user_diary_likes` VALUES (9, 9, 80, 1);
INSERT INTO `user_diary_likes` VALUES (10, 9, 41, 1);
INSERT INTO `user_diary_likes` VALUES (11, 9, 39, 1);

-- ----------------------------
-- Table structure for user_post_likes
-- ----------------------------
DROP TABLE IF EXISTS `user_post_likes`;
CREATE TABLE `user_post_likes`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID，关联用户表',
  `post_id` bigint NOT NULL COMMENT '帖子ID，关联帖子表',
  `like_status` tinyint NOT NULL DEFAULT 1 COMMENT '点赞状态：1-点赞，0-取消点赞',
  `created_at` datetime NOT NULL COMMENT '记录创建时间',
  `updated_at` datetime NOT NULL COMMENT '记录更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_post`(`user_id` ASC, `post_id` ASC) USING BTREE COMMENT '用户和帖子联合索引',
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE COMMENT '帖子ID索引，用于查询帖子点赞情况',
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE COMMENT '创建时间索引，用于按时间查询'
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户帖子点赞关系表，记录用户对帖子的点赞操作历史' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_post_likes
-- ----------------------------
INSERT INTO `user_post_likes` VALUES (1, 9, 28, 1, '2025-11-27 18:35:22', '2025-12-19 23:33:00');
INSERT INTO `user_post_likes` VALUES (2, 9, 30, 1, '2025-11-27 18:36:02', '2025-11-27 21:34:17');
INSERT INTO `user_post_likes` VALUES (3, 9, 29, 1, '2025-11-27 18:36:18', '2025-12-19 23:32:59');
INSERT INTO `user_post_likes` VALUES (4, 9, 27, 1, '2025-11-27 18:39:52', '2025-11-27 18:39:52');
INSERT INTO `user_post_likes` VALUES (5, 10, 30, 0, '2025-11-27 20:26:54', '2025-11-27 20:43:54');
INSERT INTO `user_post_likes` VALUES (6, 10, 31, 1, '2025-11-27 20:29:53', '2025-11-27 21:29:11');
INSERT INTO `user_post_likes` VALUES (7, 10, 32, 1, '2025-11-27 20:33:54', '2025-11-27 21:33:47');
INSERT INTO `user_post_likes` VALUES (8, 9, 32, 1, '2025-11-27 21:34:14', '2025-11-28 10:24:16');
INSERT INTO `user_post_likes` VALUES (9, 9, 31, 1, '2025-11-27 21:34:16', '2025-11-27 21:34:16');
INSERT INTO `user_post_likes` VALUES (10, 9, 40, 1, '2025-12-02 13:26:32', '2025-12-20 13:11:12');
INSERT INTO `user_post_likes` VALUES (11, 12, 42, 0, '2025-12-02 13:55:42', '2025-12-02 13:56:13');
INSERT INTO `user_post_likes` VALUES (12, 12, 41, 1, '2025-12-02 13:56:11', '2025-12-02 13:56:11');
INSERT INTO `user_post_likes` VALUES (13, 9, 42, 1, '2025-12-02 13:56:13', '2025-12-25 15:08:02');
INSERT INTO `user_post_likes` VALUES (14, 9, 43, 0, '2025-12-02 13:58:33', '2025-12-20 13:00:45');
INSERT INTO `user_post_likes` VALUES (15, 9, 45, 1, '2025-12-02 14:17:36', '2025-12-02 14:17:47');
INSERT INTO `user_post_likes` VALUES (16, 9, 39, 0, '2025-12-02 14:17:53', '2025-12-08 15:42:02');
INSERT INTO `user_post_likes` VALUES (17, 9, 44, 0, '2025-12-02 15:19:58', '2025-12-20 13:00:41');
INSERT INTO `user_post_likes` VALUES (18, 9, 41, 0, '2025-12-02 15:43:55', '2025-12-20 13:00:48');
INSERT INTO `user_post_likes` VALUES (19, 9, 20, 1, '2025-12-02 15:44:04', '2025-12-02 15:44:04');
INSERT INTO `user_post_likes` VALUES (20, 13, 46, 1, '2025-12-08 19:27:53', '2025-12-08 19:27:53');
INSERT INTO `user_post_likes` VALUES (21, 9, 46, 0, '2025-12-08 19:29:11', '2025-12-20 13:00:02');
INSERT INTO `user_post_likes` VALUES (22, 9, 47, 0, '2025-12-09 13:53:41', '2025-12-20 12:59:57');
INSERT INTO `user_post_likes` VALUES (23, 9, 48, 0, '2025-12-19 13:15:51', '2025-12-20 13:19:56');
INSERT INTO `user_post_likes` VALUES (24, 9, 38, 1, '2025-12-19 23:32:52', '2025-12-19 23:32:52');
INSERT INTO `user_post_likes` VALUES (25, 9, 37, 1, '2025-12-19 23:32:53', '2025-12-19 23:32:53');
INSERT INTO `user_post_likes` VALUES (26, 9, 36, 1, '2025-12-19 23:32:54', '2025-12-19 23:32:54');
INSERT INTO `user_post_likes` VALUES (27, 9, 35, 1, '2025-12-19 23:32:55', '2025-12-19 23:32:55');
INSERT INTO `user_post_likes` VALUES (28, 9, 34, 1, '2025-12-19 23:32:55', '2025-12-19 23:32:55');
INSERT INTO `user_post_likes` VALUES (29, 9, 33, 1, '2025-12-19 23:32:57', '2025-12-19 23:32:57');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_at` timestamp NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `student_id` bigint NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '0001', '123456789', '2025-11-03 20:41:14', '2025-11-03 20:41:14', '', 0, '', NULL);
INSERT INTO `users` VALUES (2, '0002', '123456789', '2025-11-03 20:42:38', '2025-11-03 20:42:38', '', 0, '', NULL);
INSERT INTO `users` VALUES (3, 'dewmark', 'abc123', '2025-11-15 16:20:57', '2025-11-15 16:20:57', '1259133575@qq.com', 2023101130, '13081002856', NULL);
INSERT INTO `users` VALUES (4, '仪敬阳', 'siPf2ZYQnP5cQRG', '2025-11-16 15:59:45', '2025-11-16 15:59:45', 'k1z.g4c80@yahoo.cn', 73, '72411037917', NULL);
INSERT INTO `users` VALUES (6, 'abc', 'abc123', '2025-11-17 18:41:50', '2025-11-17 18:41:50', '1259133575@qq.com', 2023111111, '18633444567', NULL);
INSERT INTO `users` VALUES (7, 'abcd', 'abc123', '2025-11-17 18:51:54', '2025-11-17 18:51:54', '1259133575@qq.com', 2023101111, '18633444567', NULL);
INSERT INTO `users` VALUES (8, '测试头像', 'abc123', '2025-11-24 10:28:05', '2025-11-24 10:28:05', '1259133575@qq.com', 2021101130, '19965853232', '/uploads/d1fa254a-f860-43c8-b7a6-9905649e84aa.png');
INSERT INTO `users` VALUES (9, 'apple', 'abc123', '2025-11-24 12:54:54', '2025-12-25 17:35:24', 'apple@155.com', 2023101111, '19987876565', '/uploads/cb3de55d-46d8-456a-85d1-04f5c32d2627.png');
INSERT INTO `users` VALUES (10, 'yellow', 'abc123', '2025-11-24 13:00:14', '2025-11-24 13:00:14', 'yellow@163.com', 2013321010, '19987876363', '/uploads/08858d67-b110-4f48-af5d-1016a27a06f8.png');
INSERT INTO `users` VALUES (11, 'y_white', 'abc123', '2025-11-24 13:28:18', '2025-11-24 13:28:18', 'y_white@66.com', 2021564987, '18874651321', '/uploads/507bf60f-c35b-46a7-8d9a-ff1380ed7b53.png');
INSERT INTO `users` VALUES (12, '小伟1', '1xiaolili', '2025-12-02 13:53:09', '2025-12-02 13:53:09', '2220807348@qq.com', 2023100645, '18346302335', '/uploads/0acda0e4-58b9-40f8-9eb3-82ef8880e73d.jpg');
INSERT INTO `users` VALUES (13, '阿森a', 'a123456', '2025-12-08 19:25:15', '2025-12-08 19:25:15', 'ztz12345671@qq.com', 2333333333, '18045468360', '');

SET FOREIGN_KEY_CHECKS = 1;
