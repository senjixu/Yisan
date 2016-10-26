CREATE TABLE `t_match` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `match_id` varchar(15) NOT NULL,
  `league_id` int(11) NOT NULL,
  `home_team_name` varchar(15) NOT NULL,
  `home_team_id` int(11) DEFAULT NULL,
  `away_team_name` varchar(15) NOT NULL,
  `away_team_id` int(11) DEFAULT NULL,
  `home_team_score` int(11) DEFAULT NULL,
  `away_team_score` int(11) DEFAULT NULL,
  `match_time` datetime DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `match_id` (`match_id`),
  KEY `home_team_name` (`home_team_name`),
  KEY `away_team_name` (`away_team_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;