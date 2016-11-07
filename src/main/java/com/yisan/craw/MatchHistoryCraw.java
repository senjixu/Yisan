package com.yisan.craw;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yisan.util.ConfigReader;

public class MatchHistoryCraw extends AbstractService{

	private static final Logger log = LoggerFactory.getLogger(MatchHistoryCraw.class);
	
	@Override
	public boolean isWorking() {
		return "true".equals(ConfigReader.getProperty("history.match.job.run"));
	}

	@Override
	public void init() {
		log.info("历史赛事爬取任务开始");
		//Document doc = getContent(url);
		//getLeagueMatch(url);
		
		/*if(doc != null){
			parse(doc);
		}else{
			log.error("爬取任务失败，获取内容为空");
		}*/
	}
	
	protected Object parse(Document doc){
		System.out.println(doc.text());
		Elements es = doc.getElementsByClass("inner_ul");
		for(Element e : es){
			Elements hrefs = e.getElementsByAttribute("href");
			for(Element href : hrefs){
				System.out.println(href.val());
			}
		}
		return null;
	}

}
