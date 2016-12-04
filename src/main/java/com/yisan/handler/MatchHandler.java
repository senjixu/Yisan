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
	private static final int thread_num = 6;
	
	public void dealQueueMatch(){
		long begin = System.currentTimeMillis();
		try{
			
			log.info("准备保存match,赛事队列长度是=" + historyMatchQueue.size());
			if(!historyMatchQueue.isEmpty()){
				for(int i=0;i<thread_num;i++){
					executor.execute(new Runnable(){
						public void run() {
							saveMatch();
						}
					});
				}
				
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}finally{
			//executor.shutdown();
		}
		
		log.info("保存历史赛事任务结束,耗时:{}毫秒",System.currentTimeMillis()-begin);
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
					synchronized (this) {
						matchService.batchSaveMatch(param);
						matchs.clear();
					}
					
				}
			}
			if(matchs.size()>0){
				param.put("matchs", matchs);
				synchronized (this) {
					matchService.batchSaveMatch(param);
					matchs.clear();
				}
			}
		}catch(Throwable e){
				synchronized (this){
					Map<String,Object> m = new HashMap<String,Object>();
					for(MatchBean ma : matchs){
						try{
							m.put("match", ma);
							matchService.saveMatch(m);
						}catch(Throwable e1){
							log.error("保存赛事出错,match={}",ma);
							log.error("保存赛事出错",e1);
							continue;
						}
					}
				}
				matchs.clear();
			
			log.error("保存赛事出错",e);
		}
	}
	
}
