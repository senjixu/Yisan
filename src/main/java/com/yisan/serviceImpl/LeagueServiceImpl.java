package com.yisan.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yisan.bean.LeagueBean;
import com.yisan.dao.mappers.LeagueMapper;
import com.yisan.service.LeagueService;

@Service
public class LeagueServiceImpl implements LeagueService {

	@Autowired
	LeagueMapper leagueMapper;
	
	public void batchSaveLeague(Map<String,List<LeagueBean>> param) {
		leagueMapper.batchSave(param);
	}

	public void markOrgLeagueCrawed(Map<String, Object> param) {
		leagueMapper.markOrgLeagueCrawed(param);
	}

}
