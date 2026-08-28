-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: training_system
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attempt_answer`
--

DROP TABLE IF EXISTS `attempt_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attempt_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attempt_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `selected_option_id` bigint DEFAULT NULL,
  `is_correct` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_answer_attempt_question` (`attempt_id`,`question_id`),
  KEY `fk_answer_question` (`question_id`),
  KEY `fk_answer_option` (`selected_option_id`),
  CONSTRAINT `fk_answer_attempt` FOREIGN KEY (`attempt_id`) REFERENCES `test_attempt` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_answer_option` FOREIGN KEY (`selected_option_id`) REFERENCES `question_option` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attempt_answer`
--

LOCK TABLES `attempt_answer` WRITE;
/*!40000 ALTER TABLE `attempt_answer` DISABLE KEYS */;
INSERT INTO `attempt_answer` VALUES (38,17,19,69,1),(39,17,20,74,1),(40,17,21,77,1);
/*!40000 ALTER TABLE `attempt_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `badge`
--

DROP TABLE IF EXISTS `badge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `badge` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `badge`
--

LOCK TABLES `badge` WRITE;
/*!40000 ALTER TABLE `badge` DISABLE KEYS */;
INSERT INTO `badge` VALUES (1,'CERT_1','Khởi đầu tốt','Hoàn thành khóa học đầu tiên.',NULL),(2,'CERT_5','Học viên tích cực','Hoàn thành năm khóa học.',NULL),(3,'CERT_10','Chuyên gia nội bộ','Hoàn thành mười khóa học.',NULL);
/*!40000 ALTER TABLE `badge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `certificate`
--

DROP TABLE IF EXISTS `certificate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificate` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `certificate_code` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pdf_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `issued_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_certificate_code` (`certificate_code`),
  UNIQUE KEY `uq_certificate_user_course` (`user_id`,`course_id`),
  KEY `idx_certificate_course` (`course_id`),
  CONSTRAINT `fk_certificate_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_certificate_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `certificate`
--

LOCK TABLES `certificate` WRITE;
/*!40000 ALTER TABLE `certificate` DISABLE KEYS */;
INSERT INTO `certificate` VALUES (11,137,8,'CERT-8-137-1786801782502','https://res.cloudinary.com/dx4i4a03w/raw/upload/v1786801769/training-system/lessons/0eecd23b-2a81-4e25-bec1-c0ec09169d01.pdf','2026-08-15 20:49:42');
/*!40000 ALTER TABLE `certificate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chain`
--

DROP TABLE IF EXISTS `chain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chain` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chain`
--

LOCK TABLES `chain` WRITE;
/*!40000 ALTER TABLE `chain` DISABLE KEYS */;
INSERT INTO `chain` VALUES (11,'Điện Máy Xanh','2026-08-15 18:47:07'),(12,'Thế Giới Di Động','2026-08-15 18:47:07'),(13,'Nhà Thuốc An Khang','2026-08-15 18:47:07'),(14,'AVAKids','2026-08-15 18:47:07'),(15,'TopZone','2026-08-15 18:47:07'),(16,'Thợ Điện Máy Xanh','2026-08-15 18:47:07'),(17,'Erablue','2026-08-15 18:47:07');
/*!40000 ALTER TABLE `chain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_history`
--

DROP TABLE IF EXISTS `chat_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `session_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `question` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `answer` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_chat_user` (`user_id`),
  KEY `idx_chat_session` (`session_id`),
  CONSTRAINT `fk_chat_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_history`
--

LOCK TABLES `chat_history` WRITE;
/*!40000 ALTER TABLE `chat_history` DISABLE KEYS */;
INSERT INTO `chat_history` VALUES (4,137,'bf368f4d-feca-48df-a323-db483937dc31','hello',NULL,'2026-08-15 13:50:08'),(5,137,'bf368f4d-feca-48df-a323-db483937dc31','xin chào',NULL,'2026-08-15 13:51:14'),(6,137,'bf368f4d-feca-48df-a323-db483937dc31','Hôm nay học bài gì>',NULL,'2026-08-15 13:51:40'),(7,137,'bf368f4d-feca-48df-a323-db483937dc31','Quy tắc 4C là gì','Quy tắc 4 chữ C vàng trong phục vụ khách hàng bao gồm:\n\n*   **Chủ động:** \n    *   Luôn quan sát để phục vụ và lắng nghe để hiểu rõ mong đợi của khách hàng.\n    *   Tạo cho khách hàng cảm giác đang trò chuyện với người thân.\n    *   Giải quyết tận cùng các vấn đề cho đến khi khách hàng không còn thắc mắc và xác nhận sự hài lòng.\n    *   Làm những điều khách hàng muốn ngay cả khi họ chưa nghĩ ra hoặc chưa nói ra.\n\n*   **Cười:** Duy trì tiếp xúc mắt (mắt gặp mắt) và luôn mỉm cười với khách hàng.\n\n*   **Chào:** \n    *   Chủ động chào khách hàng qua lời nói và cử chỉ.\n    *   Khi chào cần kết hợp tiếp xúc bằng mắt.\n\n*   **Cảm ơn:** \n    *   Nói lời cảm ơn và thể hiện qua cử chỉ khi hoàn tất giao dịch hoặc tương tác.\n    *   Duy trì tiếp xúc bằng mắt khi cảm ơn khách hàng.','2026-08-15 13:52:10');
/*!40000 ALTER TABLE `chat_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_by` bigint DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `certificate_pdf_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_course_creator` (`created_by`),
  CONSTRAINT `fk_course_creator` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (8,'Khoá Văn hoá công ty','Học về Văn hoá công ty',139,1,'2026-08-15 19:49:09','2026-08-15 20:49:30','https://res.cloudinary.com/dx4i4a03w/image/upload/v1786798150/training-system/courses/93060086-f040-4cdc-84d0-fae7e684e88b.png','https://res.cloudinary.com/dx4i4a03w/raw/upload/v1786801769/training-system/lessons/0eecd23b-2a81-4e25-bec1-c0ec09169d01.pdf');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_chain`
--

DROP TABLE IF EXISTS `course_chain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_chain` (
  `course_id` bigint NOT NULL,
  `chain_id` bigint NOT NULL,
  PRIMARY KEY (`course_id`,`chain_id`),
  KEY `chain_id` (`chain_id`),
  CONSTRAINT `course_chain_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `course_chain_ibfk_2` FOREIGN KEY (`chain_id`) REFERENCES `chain` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_chain`
--

LOCK TABLES `course_chain` WRITE;
/*!40000 ALTER TABLE `course_chain` DISABLE KEYS */;
INSERT INTO `course_chain` VALUES (8,11);
/*!40000 ALTER TABLE `course_chain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_region`
--

DROP TABLE IF EXISTS `course_region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_region` (
  `course_id` bigint NOT NULL,
  `region_id` bigint NOT NULL,
  PRIMARY KEY (`course_id`,`region_id`),
  KEY `region_id` (`region_id`),
  CONSTRAINT `course_region_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `course_region_ibfk_2` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_region`
--

LOCK TABLES `course_region` WRITE;
/*!40000 ALTER TABLE `course_region` DISABLE KEYS */;
/*!40000 ALTER TABLE `course_region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollment`
--

DROP TABLE IF EXISTS `enrollment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `progress_percent` int NOT NULL DEFAULT '0',
  `enrolled_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `completed_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_enrollment_user_course` (`user_id`,`course_id`),
  KEY `idx_enrollment_course` (`course_id`),
  CONSTRAINT `fk_enrollment_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_enrollment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_enrollment_progress` CHECK ((`progress_percent` between 0 and 100))
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollment`
--

LOCK TABLES `enrollment` WRITE;
/*!40000 ALTER TABLE `enrollment` DISABLE KEYS */;
INSERT INTO `enrollment` VALUES (21,137,8,100,'2026-08-15 20:48:42','2026-08-15 13:49:42');
/*!40000 ALTER TABLE `enrollment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_answer`
--

DROP TABLE IF EXISTS `forum_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `forum_question_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `forum_question_id` (`forum_question_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `forum_answer_ibfk_1` FOREIGN KEY (`forum_question_id`) REFERENCES `forum_question` (`id`),
  CONSTRAINT `forum_answer_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_answer`
--

LOCK TABLES `forum_answer` WRITE;
/*!40000 ALTER TABLE `forum_answer` DISABLE KEYS */;
INSERT INTO `forum_answer` VALUES (3,4,139,'hihi','2026-08-15 21:36:14'),(4,4,139,'Bạn cần gì?','2026-08-15 21:36:20'),(5,4,137,'Hôm nay cần gì?','2026-08-15 21:53:06'),(6,4,139,'tui ko biết','2026-08-15 21:53:20');
/*!40000 ALTER TABLE `forum_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_question`
--

DROP TABLE IF EXISTS `forum_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `forum_question_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`),
  CONSTRAINT `forum_question_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_question`
--

LOCK TABLES `forum_question` WRITE;
/*!40000 ALTER TABLE `forum_question` DISABLE KEYS */;
INSERT INTO `forum_question` VALUES (4,8,137,'haha','2026-08-15 21:33:42'),(5,8,137,'Hôm nay','2026-08-15 21:48:31');
/*!40000 ALTER TABLE `forum_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lesson`
--

DROP TABLE IF EXISTS `lesson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lesson` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `slide_pdf_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `order_index` int NOT NULL DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_lesson_course_order` (`course_id`,`order_index`),
  KEY `idx_lesson_course` (`course_id`),
  CONSTRAINT `fk_lesson_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_lesson_order` CHECK ((`order_index` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lesson`
--

LOCK TABLES `lesson` WRITE;
/*!40000 ALTER TABLE `lesson` DISABLE KEYS */;
INSERT INTO `lesson` VALUES (15,8,'Bài học 01','https://res.cloudinary.com/dx4i4a03w/raw/upload/v1786798175/training-system/lessons/d9a5c849-829f-40bc-83f7-5a30d3edefaa.pdf',0,'2026-08-15 19:49:33'),(16,8,'Bài học 02','https://res.cloudinary.com/dx4i4a03w/raw/upload/v1786798185/training-system/lessons/e4e23710-e385-4f6b-a76d-f51c213297fd.pdf',1,'2026-08-15 19:49:43'),(17,8,'Bài học 03','https://res.cloudinary.com/dx4i4a03w/raw/upload/v1786798195/training-system/lessons/2e5fb202-c86b-40b8-a151-a76e0d219f05.pdf',2,'2026-08-15 19:49:53');
/*!40000 ALTER TABLE `lesson` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lesson_progress`
--

DROP TABLE IF EXISTS `lesson_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lesson_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `enrollment_id` bigint NOT NULL,
  `lesson_id` bigint NOT NULL,
  `is_completed` tinyint(1) NOT NULL DEFAULT '0',
  `viewed_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_enrollment_lesson` (`enrollment_id`,`lesson_id`),
  KEY `fk_lp_lesson` (`lesson_id`),
  CONSTRAINT `fk_lp_enrollment` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_lp_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lesson_progress`
--

LOCK TABLES `lesson_progress` WRITE;
/*!40000 ALTER TABLE `lesson_progress` DISABLE KEYS */;
INSERT INTO `lesson_progress` VALUES (44,21,15,1,'2026-08-15 13:48:58'),(45,21,16,1,'2026-08-15 13:49:34'),(46,21,17,1,'2026-08-15 13:49:35');
/*!40000 ALTER TABLE `lesson_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notification_user` (`user_id`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (24,137,'Ghi danh khoá học','Bạn được ghi danh khoá học Khoá Văn hoá công ty',1,'2026-08-15 20:48:42','/courses/8'),(25,137,'Kết quả bài kiểm tra','Bạn đạt 100 điểm cho bài \"Quan trọng\" - ĐẠT',1,'2026-08-15 20:49:42','/attempts/17/result'),(26,137,'Chứng chỉ mới','Bạn đã hoàn thành khoá học Khoá Văn hoá công ty và nhận được chứng chỉ',1,'2026-08-15 20:49:42','/my-certificates'),(27,88,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',0,'2026-08-15 20:50:08','/chat/queue'),(28,95,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',0,'2026-08-15 20:50:08','/chat/queue'),(29,139,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',0,'2026-08-15 20:50:08','/chat/queue'),(30,88,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',0,'2026-08-15 20:51:14','/chat/queue'),(31,95,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',0,'2026-08-15 20:51:14','/chat/queue'),(32,139,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',0,'2026-08-15 20:51:14','/chat/queue'),(33,88,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',0,'2026-08-15 20:51:40','/chat/queue'),(34,95,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',0,'2026-08-15 20:51:40','/chat/queue'),(35,139,'Có câu hỏi mới cần trả lời','Thái Lê Hùng vừa đặt 1 câu hỏi đang chờ hỗ trợ trả lời',1,'2026-08-15 20:51:40','/chat/queue'),(36,139,'Có câu hỏi mới trong diễn đàn','Thái Lê Hùng vừa đặt câu hỏi trong diễn đàn khoá học Khoá Văn hoá công ty',0,'2026-08-15 21:33:42','/courses/8?tab=forum'),(37,137,'Câu hỏi diễn đàn đã được trả lời','Câu hỏi \"haha\" của bạn trong khoá học Khoá Văn hoá công ty đã có người trả lời',0,'2026-08-15 21:36:14','/courses/8?tab=forum'),(38,137,'Câu hỏi diễn đàn đã được trả lời','Câu hỏi \"haha\" của bạn trong khoá học Khoá Văn hoá công ty đã có người trả lời',0,'2026-08-15 21:36:20','/courses/8?tab=forum'),(39,139,'Có câu hỏi mới trong diễn đàn','Thái Lê Hùng vừa đặt câu hỏi trong diễn đàn khoá học Khoá Văn hoá công ty',0,'2026-08-15 21:48:31','/courses/8?tab=forum'),(40,139,'Có phản hồi mới trong diễn đàn','Thái Lê Hùng vừa trả lời tiếp trong diễn đàn khoá học Khoá Văn hoá công ty',0,'2026-08-15 21:53:06','/manage/courses/8/forum'),(41,137,'Câu hỏi diễn đàn đã được trả lời','Câu hỏi \"haha\" của bạn trong khoá học Khoá Văn hoá công ty đã có người trả lời',0,'2026-08-15 21:53:20','/courses/8?tab=forum');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `point_rule`
--

DROP TABLE IF EXISTS `point_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `point_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `points` int NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `action_type` (`action_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `point_rule`
--

LOCK TABLES `point_rule` WRITE;
/*!40000 ALTER TABLE `point_rule` DISABLE KEYS */;
INSERT INTO `point_rule` VALUES (1,'LESSON_COMPLETED',10,'Hoàn thành một bài học.'),(2,'TEST_PASSED',30,'Vượt qua một bài kiểm tra.'),(3,'COURSE_COMPLETED',100,'Hoàn thành một khóa học.');
/*!40000 ALTER TABLE `point_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `point_transaction`
--

DROP TABLE IF EXISTS `point_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `point_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `rule_id` bigint DEFAULT NULL,
  `points` int NOT NULL,
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_transaction_rule` (`rule_id`),
  KEY `idx_transaction_user` (`user_id`),
  CONSTRAINT `fk_transaction_rule` FOREIGN KEY (`rule_id`) REFERENCES `point_rule` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_transaction_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `point_transaction`
--

LOCK TABLES `point_transaction` WRITE;
/*!40000 ALTER TABLE `point_transaction` DISABLE KEYS */;
INSERT INTO `point_transaction` VALUES (33,137,1,10,'Hoàn thành bài học: Bài học 01','2026-08-15 19:52:52'),(34,137,1,10,'Hoàn thành bài học: Bài học 02','2026-08-15 19:53:08'),(35,137,1,10,'Hoàn thành bài học: Bài học 03','2026-08-15 19:53:10'),(36,137,2,30,'Vượt qua bài kiểm tra: Kiểm tra lần 1','2026-08-15 19:58:18'),(37,137,3,100,'Hoàn thành khoá học: Khoá Văn hoá công ty','2026-08-15 19:58:18'),(38,137,1,10,'Hoàn thành bài học: Bài học 03','2026-08-15 20:21:25'),(39,137,1,10,'Hoàn thành bài học: Bài học 01','2026-08-15 20:21:27'),(40,137,1,10,'Hoàn thành bài học: Bài học 02','2026-08-15 20:21:28'),(41,137,2,30,'Vượt qua bài kiểm tra: Kiểm tra lần 1','2026-08-15 20:23:48'),(42,137,1,10,'Hoàn thành bài học: Bài học 01','2026-08-15 20:23:58'),(43,137,1,10,'Hoàn thành bài học: Bài học 02','2026-08-15 20:24:00'),(44,137,1,10,'Hoàn thành bài học: Bài học 03','2026-08-15 20:24:01'),(45,137,2,30,'Vượt qua bài kiểm tra: Quan trọng','2026-08-15 20:24:19'),(46,137,3,100,'Hoàn thành khoá học: Khoá Văn hoá công ty','2026-08-15 20:24:19'),(47,137,1,10,'Hoàn thành bài học: Bài học 01','2026-08-15 20:48:58'),(48,137,1,10,'Hoàn thành bài học: Bài học 02','2026-08-15 20:49:34'),(49,137,1,10,'Hoàn thành bài học: Bài học 03','2026-08-15 20:49:35'),(50,137,2,30,'Vượt qua bài kiểm tra: Quan trọng','2026-08-15 20:49:42'),(51,137,3,100,'Hoàn thành khoá học: Khoá Văn hoá công ty','2026-08-15 20:49:42');
/*!40000 ALTER TABLE `point_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `test_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_question_test` (`test_id`),
  CONSTRAINT `fk_question_test` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (16,8,'Khách hàng phàn nàn về sản phẩm lỗi, bước đầu tiên nhân viên nên làm gì?',1,'2026-08-15 20:20:27'),(17,8,'Thời gian bảo hành tiêu chuẩn cho phần lớn thiết bị điện máy tại cửa hàng là bao lâu?',1,'2026-08-15 20:20:27'),(18,8,'Đâu KHÔNG phải là một trong 4 chữ C vàng trong phục vụ khách hàng?',1,'2026-08-15 20:20:27'),(19,9,'Khách hàng phàn nàn về sản phẩm lỗi, bước đầu tiên nhân viên nên làm gì?',1,'2026-08-15 20:23:04'),(20,9,'Thời gian bảo hành tiêu chuẩn cho phần lớn thiết bị điện máy tại cửa hàng là bao lâu?',1,'2026-08-15 20:23:04'),(21,9,'Đâu KHÔNG phải là một trong 4 chữ C vàng trong phục vụ khách hàng?',1,'2026-08-15 20:23:04');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_option`
--

DROP TABLE IF EXISTS `question_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_option` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL,
  `option_text` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_correct` tinyint(1) NOT NULL DEFAULT '0',
  `order_index` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_option_question_order` (`question_id`,`order_index`),
  CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_option`
--

LOCK TABLES `question_option` WRITE;
/*!40000 ALTER TABLE `question_option` DISABLE KEYS */;
INSERT INTO `question_option` VALUES (59,16,'Xin lỗi và lắng nghe khách hàng trình bày',1,0),(60,16,'Từ chối tiếp nhận vì không đúng quy trình',0,1),(61,16,'Gọi ngay quản lý xử lý thay mình',0,2),(62,16,'Yêu cầu khách hàng ra khỏi cửa hàng',0,3),(63,17,'6 tháng',0,0),(64,17,'12 tháng',1,1),(65,18,'Chào đón',0,0),(66,18,'Chăm sóc',0,1),(67,18,'Cạnh tranh',1,2),(68,18,'Cảm ơn',0,3),(69,19,'Xin lỗi và lắng nghe khách hàng trình bày',1,0),(70,19,'Từ chối tiếp nhận vì không đúng quy trình',0,1),(71,19,'Gọi ngay quản lý xử lý thay mình',0,2),(72,19,'Yêu cầu khách hàng ra khỏi cửa hàng',0,3),(73,20,'6 tháng',0,0),(74,20,'12 tháng',1,1),(75,21,'Chào đón',0,0),(76,21,'Chăm sóc',0,1),(77,21,'Cạnh tranh',1,2),(78,21,'Cảm ơn',0,3);
/*!40000 ALTER TABLE `question_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `region` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
INSERT INTO `region` VALUES (13,'Tây Nam Bộ','2026-08-15 18:47:07'),(14,'Hồ Chí Minh','2026-08-15 18:47:07'),(15,'Đông Cao Nguyên','2026-08-15 18:47:07'),(16,'Duyên Hải','2026-08-15 18:47:07'),(17,'Trung Bộ','2026-08-15 18:47:07'),(18,'Hà Nội +','2026-08-15 18:47:07'),(19,'Đồng bằng Sông Hồng','2026-08-15 18:47:07'),(20,'Đông Tây Bắc','2026-08-15 18:47:07');
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_st` varchar(4) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `chain_id` bigint NOT NULL,
  `region_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `uq_store_ma_st` (`ma_st`),
  KEY `fk_store_chain` (`chain_id`),
  KEY `fk_store_region` (`region_id`),
  CONSTRAINT `fk_store_chain` FOREIGN KEY (`chain_id`) REFERENCES `chain` (`id`),
  CONSTRAINT `fk_store_region` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store`
--

LOCK TABLES `store` WRITE;
/*!40000 ALTER TABLE `store` DISABLE KEYS */;
INSERT INTO `store` VALUES (14,'Điện Máy Xanh Tây Nam Bộ','1001','2026-08-15 18:47:07',11,13),(15,'Điện Máy Xanh Hồ Chí Minh','1002','2026-08-15 18:47:07',11,14),(16,'Điện Máy Xanh Đông Cao Nguyên','1003','2026-08-15 18:47:07',11,15),(17,'Điện Máy Xanh Duyên Hải','1004','2026-08-15 18:47:07',11,16),(18,'Điện Máy Xanh Trung Bộ','1005','2026-08-15 18:47:07',11,17),(19,'Điện Máy Xanh Hà Nội +','1006','2026-08-15 18:47:07',11,18),(20,'Điện Máy Xanh Đồng bằng Sông Hồng','1007','2026-08-15 18:47:07',11,19),(21,'Điện Máy Xanh Đông Tây Bắc','1008','2026-08-15 18:47:07',11,20),(22,'Thế Giới Di Động Tây Nam Bộ','1009','2026-08-15 18:47:07',12,13),(23,'Thế Giới Di Động Hồ Chí Minh','1010','2026-08-15 18:47:07',12,14),(24,'Thế Giới Di Động Đông Cao Nguyên','1011','2026-08-15 18:47:07',12,15),(25,'Thế Giới Di Động Duyên Hải','1012','2026-08-15 18:47:07',12,16),(26,'Thế Giới Di Động Trung Bộ','1013','2026-08-15 18:47:07',12,17),(27,'Thế Giới Di Động Hà Nội +','1014','2026-08-15 18:47:07',12,18),(28,'Thế Giới Di Động Đồng bằng Sông Hồng','1015','2026-08-15 18:47:07',12,19),(29,'Thế Giới Di Động Đông Tây Bắc','1016','2026-08-15 18:47:07',12,20),(30,'Nhà Thuốc An Khang Tây Nam Bộ','1017','2026-08-15 18:47:07',13,13),(31,'Nhà Thuốc An Khang Hồ Chí Minh','1018','2026-08-15 18:47:07',13,14),(32,'Nhà Thuốc An Khang Đông Cao Nguyên','1019','2026-08-15 18:47:07',13,15),(33,'Nhà Thuốc An Khang Duyên Hải','1020','2026-08-15 18:47:07',13,16),(34,'Nhà Thuốc An Khang Trung Bộ','1021','2026-08-15 18:47:07',13,17),(35,'Nhà Thuốc An Khang Hà Nội +','1022','2026-08-15 18:47:07',13,18),(36,'Nhà Thuốc An Khang Đồng bằng Sông Hồng','1023','2026-08-15 18:47:07',13,19),(37,'Nhà Thuốc An Khang Đông Tây Bắc','1024','2026-08-15 18:47:07',13,20),(38,'AVAKids Tây Nam Bộ','1025','2026-08-15 18:47:07',14,13),(39,'AVAKids Hồ Chí Minh','1026','2026-08-15 18:47:07',14,14),(40,'AVAKids Đông Cao Nguyên','1027','2026-08-15 18:47:07',14,15),(41,'AVAKids Duyên Hải','1028','2026-08-15 18:47:07',14,16),(42,'AVAKids Trung Bộ','1029','2026-08-15 18:47:07',14,17),(43,'AVAKids Hà Nội +','1030','2026-08-15 18:47:07',14,18),(44,'AVAKids Đồng bằng Sông Hồng','1031','2026-08-15 18:47:07',14,19),(45,'AVAKids Đông Tây Bắc','1032','2026-08-15 18:47:07',14,20),(46,'TopZone Tây Nam Bộ','1033','2026-08-15 18:47:07',15,13),(47,'TopZone Hồ Chí Minh','1034','2026-08-15 18:47:07',15,14),(48,'TopZone Đông Cao Nguyên','1035','2026-08-15 18:47:07',15,15),(49,'TopZone Duyên Hải','1036','2026-08-15 18:47:07',15,16),(50,'TopZone Trung Bộ','1037','2026-08-15 18:47:07',15,17),(51,'TopZone Hà Nội +','1038','2026-08-15 18:47:07',15,18),(52,'TopZone Đồng bằng Sông Hồng','1039','2026-08-15 18:47:07',15,19),(53,'TopZone Đông Tây Bắc','1040','2026-08-15 18:47:07',15,20),(54,'Thợ Điện Máy Xanh Tây Nam Bộ','1041','2026-08-15 18:47:07',16,13),(55,'Thợ Điện Máy Xanh Hồ Chí Minh','1042','2026-08-15 18:47:07',16,14),(56,'Thợ Điện Máy Xanh Đông Cao Nguyên','1043','2026-08-15 18:47:07',16,15),(57,'Thợ Điện Máy Xanh Duyên Hải','1044','2026-08-15 18:47:07',16,16),(58,'Thợ Điện Máy Xanh Trung Bộ','1045','2026-08-15 18:47:07',16,17),(59,'Thợ Điện Máy Xanh Hà Nội +','1046','2026-08-15 18:47:07',16,18),(60,'Thợ Điện Máy Xanh Đồng bằng Sông Hồng','1047','2026-08-15 18:47:07',16,19),(61,'Thợ Điện Máy Xanh Đông Tây Bắc','1048','2026-08-15 18:47:07',16,20),(62,'Erablue Tây Nam Bộ','1049','2026-08-15 18:47:07',17,13),(63,'Erablue Hồ Chí Minh','1050','2026-08-15 18:47:07',17,14),(64,'Erablue Đông Cao Nguyên','1051','2026-08-15 18:47:07',17,15),(65,'Erablue Duyên Hải','1052','2026-08-15 18:47:07',17,16),(66,'Erablue Trung Bộ','1053','2026-08-15 18:47:07',17,17),(67,'Erablue Hà Nội +','1054','2026-08-15 18:47:07',17,18),(68,'Erablue Đồng bằng Sông Hồng','1055','2026-08-15 18:47:07',17,19),(69,'Erablue Đông Tây Bắc','1056','2026-08-15 18:47:07',17,20);
/*!40000 ALTER TABLE `store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Bài kiểm tra cuối khóa',
  `pass_score` int NOT NULL DEFAULT '80',
  `max_attempts` int NOT NULL DEFAULT '3',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `is_important` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_test_course` (`course_id`),
  CONSTRAINT `fk_test_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_test_max_attempts` CHECK ((`max_attempts` > 0)),
  CONSTRAINT `chk_test_pass_score` CHECK ((`pass_score` between 0 and 100))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
INSERT INTO `test` VALUES (8,8,'Kiểm tra lần 1',70,3,1,0),(9,8,'Quan trọng',70,3,1,1);
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_attempt`
--

DROP TABLE IF EXISTS `test_attempt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test_attempt` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `test_id` bigint NOT NULL,
  `attempt_no` int NOT NULL DEFAULT '1',
  `score` int NOT NULL DEFAULT '0',
  `passed` tinyint(1) NOT NULL DEFAULT '0',
  `started_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `submitted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_attempt_user_test_no` (`user_id`,`test_id`,`attempt_no`),
  KEY `idx_attempt_user` (`user_id`),
  KEY `idx_attempt_test` (`test_id`),
  CONSTRAINT `fk_attempt_test` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_attempt_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_attempt_no` CHECK ((`attempt_no` > 0)),
  CONSTRAINT `chk_attempt_score` CHECK ((`score` between 0 and 100))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_attempt`
--

LOCK TABLES `test_attempt` WRITE;
/*!40000 ALTER TABLE `test_attempt` DISABLE KEYS */;
INSERT INTO `test_attempt` VALUES (17,137,9,1,100,1,'2026-08-15 20:49:37','2026-08-15 13:49:42');
/*!40000 ALTER TABLE `test_attempt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('EMPLOYEE','TRAINER','ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'EMPLOYEE',
  `store_id` bigint DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_user_department` (`store_id`),
  CONSTRAINT `fk_user_department` FOREIGN KEY (`store_id`) REFERENCES `store` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Quản trị hệ thống','admin','admin@tlh.vn','$2a$10$i0bzDg1zUmztMlfigA2Fa.BOrVFW3XRtpmXyv7twhW8MaIya9t3km','ADMIN',NULL,1,'2026-07-01 08:30:00','2026-07-01 08:30:00'),(88,'Lê Thị Ngọc Hà','trainer.dmx','trainer.dmx@tlh.vn','$2a$10$eKRH0Zr0Ctqme/dk/p/d2uHppfv.Hl9Pme32pLXEfJsqcYCxngQ1e','TRAINER',15,1,'2026-08-15 19:41:05','2026-08-15 19:41:05'),(89,'Phạm Thị Bình','trainer.tgdd','trainer.tgdd@tlh.vn','$2a$10$k0B4bQcanHbTZZHeaynNh.coVr2M0lDZnb9xpsM4mGHnzf77mcxvq','TRAINER',23,1,'2026-08-15 19:41:11','2026-08-15 19:41:11'),(90,'Nguyễn Thị Ngọc Hà','trainer.ntak','trainer.ntak@tlh.vn','$2a$10$MlMSZVLQxe.GAw/dTmZaDeC9u6so3m4pcxSEA4l.ZTWS1R52cz8RG','TRAINER',31,1,'2026-08-15 19:41:15','2026-08-15 19:41:15'),(91,'Lê Hoàng Phúc','trainer.avakids','trainer.avakids@tlh.vn','$2a$10$baxJjvvMi1FMdTWuhtzfOOFBaeAWejxM9CB4M7gJPmI0I5kY9C/u6','TRAINER',39,1,'2026-08-15 19:41:19','2026-08-15 19:41:19'),(92,'Huỳnh Thị Mỹ Duyên','trainer.topzone','trainer.topzone@tlh.vn','$2a$10$RzDz5rUBor/HFS.8XPJuzOWv.fLBChJOSmbGSBLVMO2I/HhPwFSEq','TRAINER',47,1,'2026-08-15 19:41:24','2026-08-15 19:41:24'),(93,'Hoàng Thị Bình','trainer.tdmx','trainer.tdmx@tlh.vn','$2a$10$V/PlponA5TdhrDNx70e7l./GOq09eW6b7Sp2U7mJPCSnkVNna.11u','TRAINER',55,1,'2026-08-15 19:41:28','2026-08-15 19:41:28'),(94,'Lê Thị Thanh','trainer.erablue','trainer.erablue@tlh.vn','$2a$10$32//7wGeRSCdz8GASGOmWOF0hASDbOY08z.Aidyify062QW5cuveq','TRAINER',63,1,'2026-08-15 19:41:33','2026-08-15 19:41:33'),(95,'Vũ Thị Thanh','trainer.toanhe','trainer.toanhe@tlh.vn','$2a$10$BBmR2L6BTQI2TMPVNF9t9.SfRIedtEcFrqW1xBi/eqtWd6O61KwT.','TRAINER',NULL,1,'2026-08-15 19:41:39','2026-08-15 19:41:39'),(96,'Huỳnh Hoàng Phúc','nv.dmx.tnb','nv.dmx.tnb@tlh.vn','$2a$10$BbC2vSxCip.463GTEoxrZem8RW7pNcxiLcd./DTkKrAmtXD5nMB9O','EMPLOYEE',14,1,'2026-08-15 19:41:43','2026-08-15 19:41:43'),(97,'Trần Thị Bình','nv.dmx.hcm','nv.dmx.hcm@tlh.vn','$2a$10$ZHQGDSwuydzQ5LfkGp0mH.AUKJ47/owtPNN1xzxIdvjppzAQPNZAe','EMPLOYEE',15,1,'2026-08-15 19:41:48','2026-08-15 19:41:48'),(98,'Huỳnh Văn An','nv.dmx.dcn','nv.dmx.dcn@tlh.vn','$2a$10$poC3KJgyuRLiMj.CX07Ml.aEYBoSoM8iMPODjxnJgYGpHhbgm7Fk2','EMPLOYEE',16,1,'2026-08-15 19:41:53','2026-08-15 19:41:53'),(99,'Lê Minh Đức','nv.dmx.dh','nv.dmx.dh@tlh.vn','$2a$10$fjtHiv5cGrtZa7U3oiH0o.nWG2DCXqIpc4PcQtKRLM6wHwFi.lGO.','EMPLOYEE',17,1,'2026-08-15 19:41:57','2026-08-15 19:41:57'),(100,'Huỳnh Minh Đức','nv.dmx.tb','nv.dmx.tb@tlh.vn','$2a$10$3wzGP3U.eLZYnmP2GUZU3e.luMfa.8B.Id/8sc8WkqmKrZVdMkBxe','EMPLOYEE',18,1,'2026-08-15 19:42:01','2026-08-15 19:42:01'),(101,'Phạm Thị Ngọc Hà','nv.dmx.hn','nv.dmx.hn@tlh.vn','$2a$10$Q6rZtGK21oBl.AzEggmuLuJjbX9efZY1aiyPwNMD9PNAbEG0aZw8a','EMPLOYEE',19,1,'2026-08-15 19:42:05','2026-08-15 19:42:05'),(102,'Lê Văn An','nv.dmx.dbsh','nv.dmx.dbsh@tlh.vn','$2a$10$CUSlVgnrAU9o7cQTRb6JeuEiv6lxHc5wbUyDy2fx/oDfx8eFMdICW','EMPLOYEE',20,1,'2026-08-15 19:42:10','2026-08-15 19:42:10'),(103,'Lê Thị Mỹ Duyên','nv.dmx.dtb','nv.dmx.dtb@tlh.vn','$2a$10$cF6T6nUuN5rRMbYSX6yMTeAaypKfxwP5zrDC/TW7Rpn5/YayQyrru','EMPLOYEE',21,1,'2026-08-15 19:42:14','2026-08-15 19:42:14'),(104,'Hoàng Quốc Bảo','nv.tgdd.tnb','nv.tgdd.tnb@tlh.vn','$2a$10$aEAik4SXEJNOu5G0pmzgHOuFuIHREUCkqou.FF0L6XWgyRKI1SpY2','EMPLOYEE',22,1,'2026-08-15 19:42:18','2026-08-15 19:42:18'),(105,'Huỳnh Thị Bình','nv.tgdd.hcm','nv.tgdd.hcm@tlh.vn','$2a$10$EXGjFKQCvsil5vB.Mi26TeU57gSUlboaRxoY0.AvO6Epa3dgUT9iG','EMPLOYEE',23,1,'2026-08-15 19:42:22','2026-08-15 19:42:22'),(106,'Phạm Thị Mỹ Duyên','nv.tgdd.dcn','nv.tgdd.dcn@tlh.vn','$2a$10$eeMO9/KTL1XZ5eiczakMs.KB0DZZ4Y5uLPh.SLSMM4O1JAozKA5DK','EMPLOYEE',24,1,'2026-08-15 19:42:28','2026-08-15 19:42:28'),(107,'Lê Quốc Bảo','nv.tgdd.dh','nv.tgdd.dh@tlh.vn','$2a$10$2buO.xLlUYWqgCGL2Xj/Z.sJEFq8eQ4cBfO/dKskWwgFYiIcKrmsS','EMPLOYEE',25,1,'2026-08-15 19:42:32','2026-08-15 19:42:32'),(108,'Trần Thị Thanh','nv.tgdd.tb','nv.tgdd.tb@tlh.vn','$2a$10$9vYFoYm5FXHucQ0po3GOzeCTH.fRmwkKiISfeKAyWG81cOLOrt1RG','EMPLOYEE',26,1,'2026-08-15 19:42:36','2026-08-15 19:42:36'),(109,'Phan Minh Đức','nv.tgdd.hn','nv.tgdd.hn@tlh.vn','$2a$10$O49wTv9dG1WZ3qGLXYj7oeSKNtr8PX.1SYixUDP.HJRWxv1187GxK','EMPLOYEE',27,1,'2026-08-15 19:42:43','2026-08-15 19:42:43'),(110,'Hoàng Thị Ngọc Hà','nv.tgdd.dbsh','nv.tgdd.dbsh@tlh.vn','$2a$10$CqRWQzIxQ5DZ9JFX2PKikOsJDROJbNToFyVFqOkDwLJHYOLPcZzVq','EMPLOYEE',28,1,'2026-08-15 19:42:47','2026-08-15 19:42:47'),(111,'Phan Văn An','nv.tgdd.dtb','nv.tgdd.dtb@tlh.vn','$2a$10$3HZXKEctcEHwYhufpNEOFeKDb3nLfP6ciuzaWlTyEQUHqaxHNOR1W','EMPLOYEE',29,1,'2026-08-15 19:42:51','2026-08-15 19:42:51'),(112,'Nguyễn Thị Thanh','nv.ntak.tnb','nv.ntak.tnb@tlh.vn','$2a$10$vA3ZbK886dvT1CLbNQRHMeHrsv6sDYPWYFOT9eaxLY4GC57eKkWKq','EMPLOYEE',30,1,'2026-08-15 19:42:55','2026-08-15 19:42:55'),(113,'Phạm Hoàng Phúc','nv.ntak.hcm','nv.ntak.hcm@tlh.vn','$2a$10$zofG6ZROHOO9HTEM2nP8heyiqeQek5b4CQmeDEdqEh.2mroKYLTt2','EMPLOYEE',31,1,'2026-08-15 19:42:59','2026-08-15 19:42:59'),(114,'Phạm Văn An','nv.ntak.dcn','nv.ntak.dcn@tlh.vn','$2a$10$avqy4ghHw/E3oPI8oxj5qerhJr9CnmpBGLo7kvzGkUag4e5YwDLrW','EMPLOYEE',32,1,'2026-08-15 19:43:04','2026-08-15 19:43:04'),(115,'Phan Thị Ngọc Hà','nv.ntak.dh','nv.ntak.dh@tlh.vn','$2a$10$BmT3Fj9b6nTFKhfY4HEWUu3lh0nwmsmYZLbTBxIOgHnfy7c5XAuge','EMPLOYEE',33,1,'2026-08-15 19:43:08','2026-08-15 19:43:08'),(116,'Nguyễn Quốc Bảo','nv.ntak.tb','nv.ntak.tb@tlh.vn','$2a$10$KmbvoKJILAynKXS8bpECUO7Yr8bTeaVUYfpk7NSOE.kBWTWU4PDcm','EMPLOYEE',34,1,'2026-08-15 19:43:11','2026-08-15 19:43:11'),(117,'Vũ Thị Bình','nv.ntak.hn','nv.ntak.hn@tlh.vn','$2a$10$Yy0ef3zgRktfbMLhnieIleWAj0rDWzvzCWSLFj1snunNv3CDcG5hy','EMPLOYEE',35,1,'2026-08-15 19:43:15','2026-08-15 19:43:15'),(118,'Phan Hoàng Phúc','nv.ntak.dbsh','nv.ntak.dbsh@tlh.vn','$2a$10$80p5Q9Lk.IaDT/NpoGHZmuNMRfCNPKnskFDHBmgBanPh7vJ4TF/EG','EMPLOYEE',36,1,'2026-08-15 19:43:19','2026-08-15 19:43:19'),(119,'Phan Thị Bình','nv.ntak.dtb','nv.ntak.dtb@tlh.vn','$2a$10$hdoElcXXQnNKS26nl01.pOIa2tzqsA2RRCjLTJjKAkEespLcq8fWq','EMPLOYEE',37,1,'2026-08-15 19:43:24','2026-08-15 19:43:24'),(120,'Trần Minh Đức','nv.avakids.tnb','nv.avakids.tnb@tlh.vn','$2a$10$TE2A18ycNBuQSRTYd.YMUeuGjJicysAf3Sv1DWhJWYtRq3KL57PWS','EMPLOYEE',38,1,'2026-08-15 19:43:29','2026-08-15 19:43:29'),(121,'Nguyễn Văn An','nv.avakids.hcm','nv.avakids.hcm@tlh.vn','$2a$10$JS5AzNKFOE3d2JDIM9wGfebavzEUFQvJLwnrnOlaiicZwz/m58Tqi','EMPLOYEE',39,1,'2026-08-15 19:43:34','2026-08-15 19:43:34'),(122,'Vũ Quốc Bảo','nv.avakids.dcn','nv.avakids.dcn@tlh.vn','$2a$10$VYuq4B0gL0.jkOwECI8SiOgiYf4gyH/u/jOm1Dw2Zz5bWVRHmVpUi','EMPLOYEE',40,1,'2026-08-15 19:43:38','2026-08-15 19:43:38'),(123,'Phạm Quốc Bảo','nv.avakids.dh','nv.avakids.dh@tlh.vn','$2a$10$cuGGGuN8ivktERy.lofQputfRdNXNKFCd5BXw80DZ8n311gwD20dy','EMPLOYEE',41,1,'2026-08-15 19:43:42','2026-08-15 19:43:42'),(124,'Huỳnh Quốc Bảo','nv.avakids.tb','nv.avakids.tb@tlh.vn','$2a$10$qclBjMgSUVd512jG8NHbpe6xnDUN9hvgRpSRDkegmz1kVPBn3rR8q','EMPLOYEE',42,1,'2026-08-15 19:43:47','2026-08-15 19:43:47'),(125,'Phạm Minh Đức','nv.avakids.hn','nv.avakids.hn@tlh.vn','$2a$10$g4CQDVjJ/Xm1mYqcxxRHw./9u8UJbr4Xxow4Bx0XWFzGamHFexsU2','EMPLOYEE',43,1,'2026-08-15 19:43:51','2026-08-15 19:43:51'),(126,'Phan Quốc Bảo','nv.avakids.dbsh','nv.avakids.dbsh@tlh.vn','$2a$10$4NZUChYYlKGs0.5QMHQFWe3AYeXDAvbwdsPNRZ9bctpbh4CHW.vKq','EMPLOYEE',44,1,'2026-08-15 19:43:55','2026-08-15 19:43:55'),(127,'Trần Quốc Bảo','nv.avakids.dtb','nv.avakids.dtb@tlh.vn','$2a$10$YTQO/2.qn27DHw91IR6zPe9hGxIB5s6gbbJjUG7IOmjpck0W2wcUK','EMPLOYEE',45,1,'2026-08-15 19:44:00','2026-08-15 19:44:00'),(128,'Hoàng Thị Thanh','nv.topzone.tnb','nv.topzone.tnb@tlh.vn','$2a$10$WXDG2hCXSzTMCmetrMTz0u0pnOKVd91YB8yrww1qElPCKIjWZjRWS','EMPLOYEE',46,1,'2026-08-15 19:44:05','2026-08-15 19:44:05'),(129,'Phan Thị Mỹ Duyên','nv.topzone.hcm','nv.topzone.hcm@tlh.vn','$2a$10$vqfI8IVmV6XlJQFhEG0Ji..mwAlSiVmDLtmbaq0eoUkWT5S63lMpG','EMPLOYEE',47,1,'2026-08-15 19:44:09','2026-08-15 19:44:09'),(130,'Hoàng Hoàng Phúc','nv.topzone.dcn','nv.topzone.dcn@tlh.vn','$2a$10$hbxF4xG1bI2Ge5H5nTUrFe4prQ/1IczIdYI4AyQo27RzGID.5dYNa','EMPLOYEE',48,1,'2026-08-15 19:44:13','2026-08-15 19:44:13'),(131,'Hoàng Văn An','nv.topzone.dh','nv.topzone.dh@tlh.vn','$2a$10$pRkZIz4PDJQucOtyIOt5M.8r41RDVZ0rTMosIltzQ6L0noVuCmTjW','EMPLOYEE',49,1,'2026-08-15 19:44:17','2026-08-15 19:44:17'),(132,'Vũ Minh Đức','nv.topzone.tb','nv.topzone.tb@tlh.vn','$2a$10$sac9ilenZ4ZaGnfKC1ftW.0sqtnbyvseDvvfUqmHDr6lI8Vx9nnXW','EMPLOYEE',50,1,'2026-08-15 19:44:21','2026-08-15 19:44:21'),(133,'Trần Thị Mỹ Duyên','nv.topzone.hn','nv.topzone.hn@tlh.vn','$2a$10$3VxWZrDtOs6aZK2ond9DIe8U77yTwVs8nwAzCyrpEGOG6N4/ltKwq','EMPLOYEE',51,1,'2026-08-15 19:44:25','2026-08-15 19:44:25'),(134,'Phan Thị Thanh','nv.topzone.dbsh','nv.topzone.dbsh@tlh.vn','$2a$10$RxWZu/WW6wBxqfFo0lqdiO3J2z1r/0DN62DlPf8w08h/Kr5R7XWEi','EMPLOYEE',52,1,'2026-08-15 19:44:29','2026-08-15 19:44:29'),(135,'Vũ Hoàng Phúc','nv.topzone.dtb','nv.topzone.dtb@tlh.vn','$2a$10$DITR6dZtpIOUV0KFvTfpFeqZfKaHfm0idRonLrEGlJr7HigwhdMsK','EMPLOYEE',53,1,'2026-08-15 19:44:33','2026-08-15 19:44:33'),(136,'Nguyễn Minh Đức','nv.tdmx.tnb','nv.tdmx.tnb@tlh.vn','$2a$10$9sNcBr72RY4nfytD2oCqVukVrA3DGu.uRp1Gi.AFHTfJ.FcrQc5h.','EMPLOYEE',54,1,'2026-08-15 19:44:38','2026-08-15 19:44:38'),(137,'Thái Lê Hùng','nv.dmx.th','thaihung.me05@gmail.com','$2a$10$QIPzNY80aolGSF82IOkoKu8r4uk3KKRtU1bVbLGjeNfHQSDLF36BO','EMPLOYEE',15,1,'2026-08-15 19:44:42','2026-08-15 19:46:02'),(138,'Phạm Thị Thanh','nv.tdmx.hcm','nv.tdmx.hcm@tlh.vn','$2a$10$h9jswYJ6xXdOzubeORQDGuvXmfNikdXfOjbHPl9cEJlrLp9LvwrQq','EMPLOYEE',55,1,'2026-08-15 19:44:43','2026-08-15 19:44:43'),(139,'Nguyễn Dương Thu Ngọc','trainer.tgdd.tn','thaihung.work05@gmail.com','$2a$10$ThriOktI0FWzNVrUskxjpOEmh.THUL3LAGs3XqBle6NNYSDsr0WdS','TRAINER',15,1,'2026-08-15 19:44:45','2026-08-15 20:48:04'),(140,'Hoàng Thị Mỹ Duyên','nv.tdmx.dcn','nv.tdmx.dcn@tlh.vn','$2a$10$NEdq6qID0iaUfAB1JDKZhuPs3FmNpDMW3Dcvl31ifBvkdctApFiOq','EMPLOYEE',56,1,'2026-08-15 19:44:46','2026-08-15 19:44:46'),(141,'Nguyễn Thị Mỹ Duyên','nv.tdmx.dh','nv.tdmx.dh@tlh.vn','$2a$10$iNb7WxorkIpkgHHFqsOcw.r6wJjSl.7FWeWY4mpUN1Ph/gQJxQOxO','EMPLOYEE',57,1,'2026-08-15 19:44:51','2026-08-15 19:44:51'),(142,'Hoàng Minh Đức','nv.tdmx.tb','nv.tdmx.tb@tlh.vn','$2a$10$kD2ldIp3kXxM3mqZzK7exufCpOMrYjDV0sWFXe/HqYG8OodLLEMre','EMPLOYEE',58,1,'2026-08-15 19:44:56','2026-08-15 19:44:56'),(143,'Vũ Văn An','nv.tdmx.hn','nv.tdmx.hn@tlh.vn','$2a$10$6wP/b1Ygk6TB6HwOHi75gOQPv7t.MZHvNtEHAic.sXnm9KEHPyOZ6','EMPLOYEE',59,1,'2026-08-15 19:45:00','2026-08-15 19:45:00'),(144,'Huỳnh Thị Thanh','nv.tdmx.dbsh','nv.tdmx.dbsh@tlh.vn','$2a$10$8uksIFKR6TkvLcdxF49rouCPRIPLGuMevSGjPz8aT0TV1OVdgj72.','EMPLOYEE',60,1,'2026-08-15 19:45:04','2026-08-15 19:45:04'),(145,'Nguyễn Hoàng Phúc','nv.tdmx.dtb','nv.tdmx.dtb@tlh.vn','$2a$10$swM/5fdxfTGrHU0Fy./1ueFKx.rzBcxd.rcuwuUqEutR7LugT6Ajy','EMPLOYEE',61,1,'2026-08-15 19:45:09','2026-08-15 19:45:09'),(146,'Vũ Thị Mỹ Duyên','nv.erablue.tnb','nv.erablue.tnb@tlh.vn','$2a$10$AJT40WqGfd7ZIzPlcQaA4ud0UwX7qAnmTQxLZpshl5v5zVs0mbDMS','EMPLOYEE',62,1,'2026-08-15 19:45:14','2026-08-15 19:45:14'),(147,'Trần Văn An','nv.erablue.hcm','nv.erablue.hcm@tlh.vn','$2a$10$rAktXtsvdpI1ktTip14NTO0WnEN78br44f6ZalCt2da5Fs7iI4N96','EMPLOYEE',63,1,'2026-08-15 19:45:19','2026-08-15 19:45:19'),(148,'Vũ Thị Ngọc Hà','nv.erablue.dcn','nv.erablue.dcn@tlh.vn','$2a$10$ze78hkCng2lv8VBJdr0MQeW8.YK2/JkuGKkN/Y46IP.hPFC/ncNVm','EMPLOYEE',64,1,'2026-08-15 19:45:24','2026-08-15 19:45:24'),(149,'Trần Thị Ngọc Hà','nv.erablue.dh','nv.erablue.dh@tlh.vn','$2a$10$jIUjnB9pW1pBa2FYr.KO3uWiIIcMpPVz5SVGTHFC2W5F8YTbVP9bq','EMPLOYEE',65,1,'2026-08-15 19:45:29','2026-08-15 19:45:29'),(150,'Lê Thị Bình','nv.erablue.tb','nv.erablue.tb@tlh.vn','$2a$10$WJTQ0xH49qdFISKmJ2ZR9.cJN2CCvLneo7z7UvCZcx3VYISmDPx9i','EMPLOYEE',66,1,'2026-08-15 19:45:34','2026-08-15 19:45:34'),(151,'Huỳnh Thị Ngọc Hà','nv.erablue.hn','nv.erablue.hn@tlh.vn','$2a$10$aOrtYt6HNtCESSFQvc9prep8RW4vfeQWv2wsRBN/6aZI.HmIKHfpu','EMPLOYEE',67,1,'2026-08-15 19:45:38','2026-08-15 19:45:38'),(152,'Nguyễn Thị Bình','nv.erablue.dbsh','nv.erablue.dbsh@tlh.vn','$2a$10$CvGsMLYYbY0RV153fBtbr.i86CLsEFQYlannRnVPt0sCBtK5eD126','EMPLOYEE',68,1,'2026-08-15 19:45:42','2026-08-15 19:45:42'),(153,'Trần Hoàng Phúc','nv.erablue.dtb','nv.erablue.dtb@tlh.vn','$2a$10$CMI3xuW1itI.96i59nKd4eN7X3ACkmueT3saKQkc//1LUxU8N4zyC','EMPLOYEE',69,1,'2026-08-15 19:45:46','2026-08-15 19:45:46');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_badge`
--

DROP TABLE IF EXISTS `user_badge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_badge` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `badge_id` bigint NOT NULL,
  `awarded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_badge` (`user_id`,`badge_id`),
  KEY `fk_userbadge_badge` (`badge_id`),
  CONSTRAINT `fk_userbadge_badge` FOREIGN KEY (`badge_id`) REFERENCES `badge` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_userbadge_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_badge`
--

LOCK TABLES `user_badge` WRITE;
/*!40000 ALTER TABLE `user_badge` DISABLE KEYS */;
INSERT INTO `user_badge` VALUES (5,137,1,'2026-08-15 19:58:18');
/*!40000 ALTER TABLE `user_badge` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-17 23:00:26
