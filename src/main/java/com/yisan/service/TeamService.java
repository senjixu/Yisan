package com.yisan.service;

import java.util.List;
import java.util.Map;

import com.yisan.bean.TeamBean;

public interface TeamService {

	void batchSaveTeam(Map<String,List<TeamBean>> param);

	void saveTeam(Map<String, Object> m);
	
}
