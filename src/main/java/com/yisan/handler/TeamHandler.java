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

import com.yisan.bean.TeamBean;
import com.yisan.service.TeamService;

public class TeamHandler {
	private static final Logger log = LoggerFactory.getLogger(TeamHandler.class);
	
	@Autowired
	@Qualifier("teamQueue")
	private LinkedBlockingQueue<TeamBean> teamQueue;
	
	@Autowired
	TeamService teamService;
	
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static final int thread_num = 5;
	
	public void dealQueueMatch(){
		try{
			Thread.sleep(10000);
			log.info("准备保存team，teamQueue.size=" + teamQueue.size());
			
			for(int i=0;i<thread_num;i++){
				executor.execute(new Runnable(){
					public void run() {
						saveTeam();
					}
				});
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}finally{
			executor.shutdown();
		}
		
		log.info("保存球队工作结束");
	}

	protected void saveTeam() {
		
		List<TeamBean> teams = new ArrayList<TeamBean>();
		Map<String,List<TeamBean>> param = new HashMap<String,List<TeamBean>>();
		try{
			while(!teamQueue.isEmpty()){

				TeamBean team = teamQueue.poll();
				if(team != null){
					teams.add(team);
				}
				if(teams.size()>=50){
					param.put("teams", teams);
					teamService.batchSaveTeam(param);
					teams.clear();
				}
			}
			if(teams.size()>0){
				param.put("teams", teams);
				teamService.batchSaveTeam(param);
				teams.clear();
			}
		}catch(Exception e){
			log.error("保存球队出错",e);
		}
	}
	
}
