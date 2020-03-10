DROP SCHEMA IF EXISTS `music`;
CREATE SCHEMA `music`;

DROP TABLE IF EXISTS `music`.`playlist`;
CREATE TABLE `music`.`playlist` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);

DROP TABLE IF EXISTS `music`.`audio`;
CREATE TABLE `music`.`audio` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `playlist_id` INT UNSIGNED NOT NULL,
  `file_path` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `AUDIO_PLAYLIST_PK_idx` (`playlist_id` ASC) VISIBLE,
  CONSTRAINT `AUDIO_PLAYLIST_PK`
    FOREIGN KEY (`playlist_id`)
    REFERENCES `music`.`playlist` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

DROP TABLE IF EXISTS `music`.`video`;
CREATE TABLE `music`.`video` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `playlist_id` INT UNSIGNED NOT NULL,
  `file_path` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `VIDEO_PLAYLIST_PK_idx` (`playlist_id` ASC) VISIBLE,
  CONSTRAINT `VIDEO_PLAYLIST_PK`
    FOREIGN KEY (`playlist_id`)
    REFERENCES `music`.`playlist` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

DROP TABLE IF EXISTS `music`.`con_stream_status`;
CREATE TABLE `music`.`con_stream_status` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);

DROP TABLE IF EXISTS `music`.`stream`;
CREATE TABLE `music`.`stream` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `playlist_id` INT UNSIGNED NOT NULL,
  `status_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
  INDEX `STREAM_STREAM_STATUS_PK_idx` (`status_id` ASC) VISIBLE,
  CONSTRAINT `STREAM_STREAM_STATUS_PK`
    FOREIGN KEY (`status_id`)
    REFERENCES `music`.`con_stream_status` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

    ALTER TABLE `music`.`stream`
ADD COLUMN `compilation_iteration` INT NULL AFTER `status_id`;


INSERT INTO `music`.`con_stream_status` (`id`, `value`) VALUES ('1', 'CREATED');
INSERT INTO `music`.`con_stream_status` (`id`, `value`) VALUES ('2', 'COMPILED');
INSERT INTO `music`.`con_stream_status` (`id`, `value`) VALUES ('3', 'PLAYING');
INSERT INTO `music`.`con_stream_status` (`id`, `value`) VALUES ('4', 'STOPPED');


