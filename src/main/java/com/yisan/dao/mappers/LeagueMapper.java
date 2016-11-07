package com.yisan.dao.mappers;

import java.util.List;
import java.util.Map;

import com.yisan.bean.LeagueBean;

public interface LeagueMapper {
	
	public void saveLeague(Map<String,Object> league);
	public void batchSaveLeague(Map<String,List<LeagueBean>> param);
}
