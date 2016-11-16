package com.yisan.craw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.yisan.bean.MatchBean;
import com.yisan.bean.OrgLeagueBean;
import com.yisan.bean.TeamBean;
import com.yisan.dao.mappers.LeagueMapper;
import com.yisan.util.ConfigReader;
import com.yisan.util.HttpUtil;

public class MatchHistoryCraw extends AbstractService{

	private static final Logger log = LoggerFactory.getLogger(MatchHistoryCraw.class);
	
	@Autowired
	private LeagueMapper leagueMapper;
	
	private ArrayBlockingQueue<OrgLeagueBean> queue = null;
	
	private String url = ConfigReader.getProperty("url.007.league");
	
	@Override
	public boolean isWorking() {
		return "true".equals(ConfigReader.getProperty("history.match.job.run"));
	}

	@Override
	public void init() {
		
		List<OrgLeagueBean> list = leagueMapper.qryOraLeagueList(null);
		log.info("历史赛事爬取任务开始,未爬取的页面有{}个",list == null?0:list.size());
		long begin = System.currentTimeMillis();
		
		String detailUrl = "";
		Document doc = null;
		for(OrgLeagueBean bean : list){
			detailUrl = url + bean.getLeague_type() + "/" + bean.getYear() + "/" + bean.getLeague_id() + ".html";
			doc = getContent(detailUrl);
			
			if(doc == null){
				log.warn("爬取页面{} 失败",detailUrl);
				continue;
			}
			
			if("SubLeague".equals(bean.getLeague_type())){
				dealSubLeague(doc);
			}else{
				deal(doc);
			}
			
			//睡眠3秒
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				
			}
		}
		
		log.info("历史赛事爬取任务结束，耗时{}毫秒",System.currentTimeMillis() - begin);
	}
	
	//先去到主页，然后获取联赛、附加赛、附加赛决赛的编码，根据编码组合新的地址，再访爬取新组合的地址页面
	private void dealSubLeague(Document doc) {
		
		
	}

	private void deal(Document doc) {
		if(doc == null){
			return;
		}
		
		//从js里提取数据
		Elements scripts = doc.select("script");
		String jsUrl = null;
		String content = null;
		String pageUrl = null;
		for(Element es : scripts){
			jsUrl = es.attr("src");
			//包含了赛事信息的js
			if(jsUrl != null && jsUrl.contains("matchResult")){
				try {
					pageUrl = url + jsUrl;
					content = HttpUtil.get(url + jsUrl);
					if(content.contains("对不起！你查看的页面不存在")){
						log.error("请求{}出错,页面不存在",pageUrl);
						continue;
					}
				} catch (Exception e) {
					log.error("请求{}出错,结果{},异常反馈{}",url+jsUrl,content,e.getMessage());
					continue;
				}
				
				parse(content);
			}
		}
	}

	protected Object parse(String content){
		
		String afterArrTeam = StringUtils.substringAfter(content, "var arrTeam =");
		String team = StringUtils.substringBefore(afterArrTeam, "jh[");
		team = team.replace("[[", "").replace("]];", "").replace("'", "").replace("],[", "#");
		//String[] teams = StringUtils.split(team, "],[");
		String[] teams = team.split("#");
		
		//获取球队的信息
		int size = 6;
		List<TeamBean> teamList = new ArrayList<TeamBean>();
		for(String t: teams){
			String[] teamItem =  StringUtils.split(t,",");
			TeamBean bean = new TeamBean();
			size = teamItem.length;
			size = size>6?6:size;
			for(int i=0;i<size;i++){
				//[17,'米德尔斯堡','米杜士堡','Middlesbrough','米杜士堡','images/2013121225605.png']
				bean.setTeam_id(Long.valueOf(teamItem[0].trim()));
				bean.setTeam_name(teamItem[1]);
				bean.setTeam_name_hk(teamItem[2]);
				bean.setTeam_name_en(teamItem[3]);
				bean.setTeam_name_hk_s(teamItem[4]);
				bean.setPic(teamItem[5]);
			}
			teamList.add(bean);
		}
		
		//jh["R_1"] = [[37600,36,-1,'2003-08-16 19:00',32,33,'1-0','1-0','','',,,'','',0,1,0,0,0,0,''],[...]];jh["R_2"] = [[...]]
		//轮次信息
		String round = StringUtils.substringAfter(content, "jh[");
		String[] rounds = round.split(";");
		//round = round.replace("],[", "#");
		String[] matchRound = null;
		for(String r : rounds){
			r = StringUtils.substringAfter(round, "= [[");
			r = r.replace("],[", "#");
			r = r.replace("]];", "");
			matchRound = r.split("#");
			
			String[] mrs = null;
 			for(String mr : matchRound){
 				mrs = mr.split(",");
 				MatchBean match = new MatchBean();
 				match.setMatch_id(mrs[0]);
			}
		}
		
		
		return null;
	}

}
