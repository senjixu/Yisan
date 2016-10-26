CREATE TABLE `odds_europe` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `match_id` varchar(15) NOT NULL,
  `company_id` int(11) DEFAULT NULL,
  `is_master` int(11) DEFAULT NULL,
  `is_first` int(11) DEFAULT NULL,
  `win` double DEFAULT NULL,
  `lost` double DEFAULT NULL,
  `draw` double DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `match_id` (`match_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
