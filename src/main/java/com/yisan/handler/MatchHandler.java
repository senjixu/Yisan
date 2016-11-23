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

import com.yisan.bean.MatchBean;
import com.yisan.service.MatchService;

public class MatchHandler {
	private static final Logger log = LoggerFactory.getLogger(MatchHandler.class);
	
	@Autowired
	@Qualifier("historyMatchQueue")
	private LinkedBlockingQueue<MatchBean> historyMatchQueue;
	
	@Autowired
	MatchService matchService;
	
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static final int thread_num = 5;
	
	public void dealQueueMatch(){
		try{
			Thread.sleep(10000);
			log.info("准备保存match，historyMatchQueue.size=" + historyMatchQueue.size());
			
			for(int i=0;i<thread_num;i++){
				executor.execute(new Runnable(){
					public void run() {
						saveMatch();
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

	protected void saveMatch() {
		
		List<MatchBean> matchs = new ArrayList<MatchBean>();
		Map<String,List<MatchBean>> param = new HashMap<String,List<MatchBean>>();
		try{
			while(!historyMatchQueue.isEmpty()){

				MatchBean match = historyMatchQueue.poll();
				if(match != null){
					matchs.add(match);
				}
				if(matchs.size()>=100){
					param.put("matchs", matchs);
					matchService.batchSaveMatch(param);
					matchs.clear();
				}
			}
			if(matchs.size()>0){
				param.put("matchs", matchs);
				matchService.batchSaveMatch(param);
				matchs.clear();
			}
		}catch(Exception e){
			log.error("保存赛事出错",e);
		}
	}
	
}
