-- Create the notification table
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version` int NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `updated_by` varchar(320) DEFAULT NULL,
  `date_scheduled` date NOT NULL,
  `recipient_uuid` varchar(36) NOT NULL,
  `notification_status` varchar(50) NOT NULL,
  `notification_type` varchar(50) NOT NULL,
  `notification_metadata` json DEFAULT NULL,
  `date_time_processed` datetime DEFAULT NULL,
  `notification_status_metadata` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;