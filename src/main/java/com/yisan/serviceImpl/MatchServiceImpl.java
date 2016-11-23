package com.yisan.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yisan.bean.MatchBean;
import com.yisan.dao.mappers.MatchMapper;
import com.yisan.service.MatchService;

@Service
public class MatchServiceImpl implements MatchService {

	@Autowired
	MatchMapper matchMapper;
	
	public void batchSaveMatch(Map<String, List<MatchBean>> param) {
		matchMapper.batchSave(param);
	}

}
