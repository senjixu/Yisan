package com.yisan.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.yisan.bean.LeagueBean;
import com.yisan.service.LeagueService;

public class LeagueHandler {
	private static final Logger log = LoggerFactory.getLogger(LeagueHandler.class);
	@Autowired
	@Qualifier("leagueQueue")
	LinkedBlockingQueue<LeagueBean> leagueQueue;
	
	@Autowired
	LeagueService leagueService;
	
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	
	public void dealQueueLeague(){
		
		try{
			Thread.sleep(10000);
			log.info("准备保存原始联赛，leagueQueue.size=" + leagueQueue.size());
			
			for(int i=0;i<5;i++){
				executor.execute(new Runnable(){
					
					public void run() {
						
						saveLeague();
					}
					
				});
			}
			
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}finally{
			executor.shutdown();
		}
		
		log.info("保存原始联赛工作结束");
	}

	protected void saveLeague() {
		
		List<LeagueBean> leagues = new ArrayList<LeagueBean>();
		Map<String,List<LeagueBean>> param = new HashMap<String,List<LeagueBean>>();
		while(!leagueQueue.isEmpty()){

			LeagueBean league = leagueQueue.poll();
			if(league != null){
				leagues.add(league);
			}
			if(leagues.size()>=100){
				param.put("leagues", leagues);
				leagueService.batchSaveLeague(param);
				leagues.clear();
			}
		}
		if(leagues.size()>0){
			param.put("leagueList", leagues);
			leagueService.batchSaveLeague(param);
			leagues.clear();
		}
		
	}
	
}
