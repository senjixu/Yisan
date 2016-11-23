package com.yisan.service;

import java.util.List;
import java.util.Map;

import com.yisan.bean.LeagueBean;

public interface LeagueService {

	void batchSaveLeague(Map<String,List<LeagueBean>> param);
	
}
