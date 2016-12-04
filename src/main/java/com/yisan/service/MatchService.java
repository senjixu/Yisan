package com.yisan.service;

import java.util.List;
import java.util.Map;

import com.yisan.bean.MatchBean;

public interface MatchService {

	void batchSaveMatch(Map<String,List<MatchBean>> param);
	void saveMatch(Map<String,Object> param);
	
}
