CREATE TABLE `t_league` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `league_id` int(11) NOT NULL,
  `league_name_cn` varchar(10) DEFAULT NULL,
  `league_name_en` varchar(30) DEFAULT NULL,
  `league_name` varchar(10) DEFAULT NULL,
  `is_hot` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `country` varchar(15) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `league_id` (`league_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
