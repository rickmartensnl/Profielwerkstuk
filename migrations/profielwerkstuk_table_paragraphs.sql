
/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

-- --------------------------------------------------------

--
-- Table structure for table `paragraphs`
--

CREATE TABLE `paragraphs` (
  `uuid` char(36) NOT NULL,
  `name` varchar(20) NOT NULL,
  `flags` int NOT NULL DEFAULT '0',
  `chapter_uuid` char(36) NOT NULL,
  `creator_uuid` char(36) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Dumping data for table `paragraphs`
--
