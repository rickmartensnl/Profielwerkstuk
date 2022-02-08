
/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `uuid` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `question` text NOT NULL,
  `information` text CHARACTER SET utf8 COLLATE utf8_general_ci,
  `flags` int NOT NULL DEFAULT '9',
  `answer` json NOT NULL,
  `variables` json NOT NULL,
  `tips` json DEFAULT NULL,
  `paragraph_uuid` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `creator_uuid` char(36) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `questions`
--
