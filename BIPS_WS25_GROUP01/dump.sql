CREATE DATABASE `bips_ws_2526`;

CREATE TABLE `bips_ws_2526`.`user` (
  `id_user` int NOT NULL,
  `username` varchar(16) NOT NULL,
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`id_user`)
);