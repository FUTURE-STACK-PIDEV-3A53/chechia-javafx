-- Script de création de la table 'reservation'
CREATE TABLE IF NOT EXISTS `reservation` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `event_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `nombre_personnes` INT NOT NULL,
  `date_reservation` VARCHAR(20) NOT NULL,
  FOREIGN KEY (`event_id`) REFERENCES `event`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  INDEX `idx_event` (`event_id`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_date` (`date_reservation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Ajouter quelques données de test
INSERT INTO `reservation` (`event_id`, `user_id`, `nombre_personnes`, `date_reservation`) VALUES
(1, 1, 2, '2023-05-15'),
(2, 1, 5, '2023-06-20'),
(1, 2, 3, '2023-05-18'); 