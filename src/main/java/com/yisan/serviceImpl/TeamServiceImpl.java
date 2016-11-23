package com.yisan.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yisan.bean.TeamBean;
import com.yisan.dao.mappers.TeamMapper;
import com.yisan.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	TeamMapper teamMapper;
	

	public void batchSaveTeam(Map<String, List<TeamBean>> param) {
		teamMapper.batchSave(param);
		
	}

}
