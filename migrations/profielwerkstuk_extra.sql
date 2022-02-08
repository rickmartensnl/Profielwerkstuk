
--
-- Indexes for dumped tables
--

--
-- Indexes for table `chapters`
--
ALTER TABLE `chapters`
  ADD PRIMARY KEY (`uuid`),
  ADD KEY `subject_uuid` (`subject_uuid`),
  ADD KEY `creator_uuid` (`creator_uuid`);

--
-- Indexes for table `paragraphs`
--
ALTER TABLE `paragraphs`
  ADD PRIMARY KEY (`uuid`),
  ADD KEY `chapter_uuid` (`chapter_uuid`),
  ADD KEY `creator_uuid` (`creator_uuid`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`uuid`),
  ADD KEY `paragraph_id` (`paragraph_uuid`),
  ADD KEY `creator_id` (`creator_uuid`);

--
-- Indexes for table `subjects`
--
ALTER TABLE `subjects`
  ADD PRIMARY KEY (`uuid`),
  ADD KEY `creator_uuid` (`creator_uuid`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`uuid`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `users_play_history`
--
ALTER TABLE `users_play_history`
  ADD PRIMARY KEY (`uuid`),
  ADD KEY `question_uuid` (`question_uuid`),
  ADD KEY `user_uuid` (`user_uuid`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chapters`
--
ALTER TABLE `chapters`
  ADD CONSTRAINT `chapters_ibfk_1` FOREIGN KEY (`subject_uuid`) REFERENCES `subjects` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `chapters_ibfk_2` FOREIGN KEY (`creator_uuid`) REFERENCES `users` (`uuid`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `paragraphs`
--
ALTER TABLE `paragraphs`
  ADD CONSTRAINT `paragraphs_ibfk_1` FOREIGN KEY (`creator_uuid`) REFERENCES `users` (`uuid`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `paragraphs_ibfk_2` FOREIGN KEY (`chapter_uuid`) REFERENCES `chapters` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `questions`
--
ALTER TABLE `questions`
  ADD CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`creator_uuid`) REFERENCES `users` (`uuid`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `questions_ibfk_2` FOREIGN KEY (`paragraph_uuid`) REFERENCES `paragraphs` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `subjects`
--
ALTER TABLE `subjects`
  ADD CONSTRAINT `subjects_ibfk_1` FOREIGN KEY (`creator_uuid`) REFERENCES `users` (`uuid`) ON DELETE SET NULL ON UPDATE CASCADE;

/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

--
-- Constraints for table `users_play_history`
--
ALTER TABLE `users_play_history`
  ADD CONSTRAINT `users_play_history_ibfk_1` FOREIGN KEY (`question_uuid`) REFERENCES `questions` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `users_play_history_ibfk_2` FOREIGN KEY (`user_uuid`) REFERENCES `users` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE;
