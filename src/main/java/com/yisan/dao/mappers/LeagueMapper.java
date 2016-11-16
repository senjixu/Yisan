package com.yisan.dao.mappers;

import java.util.List;
import java.util.Map;

import com.yisan.bean.LeagueBean;
import com.yisan.bean.OrgLeagueBean;

public interface LeagueMapper {
	
	public void saveOrgLeague(Map<String,Object> league);
	public void batchSaveOrgLeague(Map<String,List<LeagueBean>> param);
	public List<OrgLeagueBean> qryOraLeagueList(Map<String,Object> param);
	
	/**
	 * 标记联赛对应的赛事比分已经爬
	 * @param param
	 * @return
	 */
	public int markOrgLeagueCrawed(Map<String,Object> param);
	
	public int countOraLeague(Map<String,Object> param);
}
