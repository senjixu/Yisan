package com.yisan.craw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.yisan.bean.LeagueBean;
import com.yisan.util.ConfigReader;
import com.yisan.util.HttpUtil;

public class LeagueCraw extends AbstractService{

	private static final Logger log = LoggerFactory.getLogger(LeagueCraw.class);
	
	@Autowired
	@Qualifier("leagueQueue")
	LinkedBlockingQueue<LeagueBean> leagueQueue;

	@Override
	public boolean isWorking() {
		return "true".equals(ConfigReader.getProperty("league.job.run"));
	}

	@Override
	public void init() {
		log.info("历史赛事爬取任务开始");
		getLeagueMatch(url);

	}

	protected Object getLeagueMatch(String url) {

		try {
			Document doc = getContentDoc(url);
			Elements script = doc.select("script");
			//ScriptEngineManager manager = new ScriptEngineManager();
			//ScriptEngine engine = manager.getEngineByName("js");
			Document docs = null;
			for (Element sc : script) {
				try {
					String content = HttpUtil.get("http://zq.win007.com/" + sc.attr("src"), 10000);
					if (sc.attr("src").contains("infoHeader.js")) {
						docs = Jsoup.parse(content);
						if (docs.text() == null || docs.text().length() == 0 || docs.text().contains("你查看的页面不存在")) {
							continue;
						}
						parse(docs.text());

					}
				} catch (Exception e) {
					if(e instanceof ScriptException){
						log.error("无法解析{}", "http://zq.win007.com/" + url);
					}else{
						log.error("解析{}时发生错误,地址{}:", docs.html(), "http://zq.win007.com/" + url, e);
					}
				}
			}

		} catch (IOException e) {
			log.error("",e);
		}


		return null;
	}

	private void parse(String text) {
		String[] arr = text.split("\"1\",");
		int index = 0;
		String[] brr;
		String[] leagues;
		String[] years;
		String pre = null;
		String aft = null;
		LeagueBean league = null;
		
		for(String s : arr){
			index = s.indexOf("\"]");
			if(index == -1) continue;
			s = s.replace("[\"", "");

			s = s.substring(0,index-2);
			brr = s.split("\",\"");
			for(String b : brr){
				//1217,意女甲,1,1,2016-2017,2015-2016,2014-2015,2013-2014,2012-2013
				pre = StringUtils.substringBefore(b, ",20");
				aft = StringUtils.substringAfter(b, pre+",");
				
				leagues = pre.split(",");
				
				years = aft.split(",");
				
				for(String year : years){
					league = new LeagueBean();
					league.setLeague_id(Long.valueOf(leagues[0]));
					league.setLeague_name(leagues[1]);
					league.setYear(year);
					
					if("2".equals(leagues[2])){
						league.setType(2);
					}else if("1".equals(leagues[2]) && "0".equals(leagues[3])){
						league.setType(1);
					}else{
						league.setType(0);
					}
					leagueQueue.add(league);
				}
				
			}
		}

	}

}
