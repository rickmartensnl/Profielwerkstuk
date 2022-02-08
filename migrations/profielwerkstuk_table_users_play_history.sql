
/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

-- --------------------------------------------------------

--
-- Table structure for table `users_play_history`
--

CREATE TABLE `users_play_history` (
  `uuid` char(36) NOT NULL,
  `answer` json NOT NULL,
  `flags` int NOT NULL,
  `correctPercentage` decimal(10,0) DEFAULT NULL,
  `variableValues` json NOT NULL,
  `question_uuid` char(36) NOT NULL,
  `user_uuid` char(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `users_play_history`
--
