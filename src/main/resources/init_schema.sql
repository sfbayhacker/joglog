CREATE DATABASE `jogdb`;

USE `jogdb`;

CREATE TABLE `roles` (
	`id` varchar(20) NOT NULL DEFAULT '',
	`name` varchar(50) DEFAULT NULL,
	`created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deleted` char(1) NOT NULL DEFAULT 'N',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `users` (
	`id` int NOT NULL AUTO_INCREMENT,
	`email` varchar(50) DEFAULT NULL,
	`password` varchar(20) DEFAULT NULL,
	`name` varchar(50) DEFAULT NULL,
	`role_id` varchar(20) NOT NULL,
	`created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deleted` char(1) NOT NULL DEFAULT 'N',
	PRIMARY KEY (`id`),
	CONSTRAINT `user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `entries` (
	`id` int NOT NULL AUTO_INCREMENT,
	`date` date NOT NULL,
	`time` int NOT NULL,
	`user_id` int NOT NULL,
	`created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`deleted` char(1) NOT NULL DEFAULT 'N',
	PRIMARY KEY (`id`),
	CONSTRAINT `ce_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;