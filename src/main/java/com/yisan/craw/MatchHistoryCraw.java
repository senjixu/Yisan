package com.yisan.craw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
	
	@Autowired
	@Qualifier("historyMatchQueue")
	private LinkedBlockingQueue<MatchBean> historyMatchQueue;
	
	@Autowired
	private LinkedBlockingQueue<TeamBean> teamQueue;
	
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
		try{
			for(OrgLeagueBean bean : list){
				detailUrl = url + "/cn/" + bean.getLeague_type() + "/" + bean.getYear() + "/" + bean.getLeague_id() + ".html";
				doc = getContent(detailUrl);
				
				if(doc == null){
					log.warn("爬取页面{} 失败",detailUrl);
					continue;
				}
				
				if("SubLeague".equals(bean.getLeague_type())){
					//解析附加赛决赛
					parse(deal(doc));
					
					String content = deal(doc);
					String[] subLeagues = dealSubLeague(content);
					if(subLeagues != null && subLeagues.length>0){
						if(subLeagues.length>1){
							//联赛 附加赛， 附加赛决赛在上面解析了
							for(int i = 0;i<subLeagues.length-1;i++){
								detailUrl = url + "/cn/" + bean.getLeague_type() + "/" + bean.getYear() + "/" + 
										bean.getLeague_id() + "_" + subLeagues[0] +  ".html";
								doc = getContent(detailUrl);
								parse(deal(doc));
							}
						}
					}
				}else{
					parse(deal(doc));
				}
				
				//睡眠3秒
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					
				}
			}
		}catch(Exception e){
			log.error("爬取历史赛事出错", e);
		}
		log.info("历史赛事爬取任务结束，耗时{}毫秒",System.currentTimeMillis() - begin);
	}
	
	//先去到主页，然后获取联赛、附加赛、附加赛决赛的编码，根据编码组合新的地址，再访爬取新组合的地址页面
	private String[] dealSubLeague(String content) {
		String arrSubLeague = StringUtils.substringAfter(content, "var arrSubLeague");
		
		if(arrSubLeague == null){
			return null;
		}
		
		arrSubLeague = StringUtils.substringAfter(content, "[[");
		arrSubLeague  = StringUtils.substringBefore(arrSubLeague, "]];");
		arrSubLeague = arrSubLeague.replace("],[", "#");
		String[] subLeagues = arrSubLeague.split("#");
		
		return subLeagues;
	}

	private String deal(Document doc) {
		if(doc == null){
			return null;
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
					return content;
				} catch (Exception e) {
					log.error("请求{}出错,结果{},异常反馈{}",url+jsUrl,content,e.getMessage());
					continue;
				}
			}
		}
		return content;
	}

	protected Object parse(String content){
		if(StringUtils.isBlank(content)){
			return null;
		}
		String afterArrTeam = StringUtils.substringAfter(content, "var arrTeam =");
		String team = StringUtils.substringBefore(afterArrTeam, "jh[");
		team = team.replace("[[", "").replace("]];", "").replace("'", "").replace("],[", "#");
		//String[] teams = StringUtils.split(team, "],[");
		String[] teams = team.split("#");
		
		//获取球队的信息
		int size = 6;
		Map<String,String> teamNameMap = new HashMap<String,String>();
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
				
				teamNameMap.put(teamItem[0].trim(), teamItem[1]);
				
				teamQueue.add(bean);
			}
		}
		
		//jh["R_1"] = [[37600,36,-1,'2003-08-16 19:00',32,33,'1-0','1-0','','',,,'','',0,1,0,0,0,0,''],[...]];jh["R_2"] = [[...]]
		//轮次信息
		String round = StringUtils.substringAfter(content, "jh[");
		round = StringUtils.substringBefore(round, "var");
		String[] rounds = round.split(";");
		//round = round.replace("],[", "#");
		String[] matchRound = null;
		String leagueId = "";
		int rd = 0;
		for(String r : rounds){
			r = StringUtils.substringAfter(round, "= [[");
			r = r.replace("],[", "#");
			r = r.replace("]];", "").replace("'", "");
			matchRound = r.split("#");
			rd++;
			
			String[] mrs = null;
			// [[1250439,3,0,2017-05-29 00:30,9,1068,,,,,,,,,0,1,0,0,0,0,,,#1250443,3,0,2017-05-29 00:30,1072,13,,,,,,,,,0,1,0,0,0,0,,,#1250442,3,0,2017-05-29 00:30,3100,14,,,,,,,,,0,1,0,0,0,0,,,#1250441,3,0,2017-05-29 00:30,10,8,,,,,,,,,0,1,0,0,0,0,,,#1250440,3,0,2017-05-29 00:30,12,2037,,,,,,,,,0,1,0,0,0,0,,,
 			for(String mr : matchRound){
 				System.out.println(mr);
 				mrs = mr.split(",");
 				leagueId = mrs[1];
 				MatchBean match = new MatchBean();
 				match.setMatch_id(mrs[0]);
 				match.setLeague_id(Long.valueOf(leagueId));
 				match.setMatch_time(DateUtils.parseDate(mrs[3]));
 				match.setHome_team_id(Long.valueOf(leagueId + mrs[4]));
 				match.setAway_team_id(Long.valueOf(leagueId + mrs[5]));
 				String homeScore = StringUtils.substringAfter(mrs[6], "-");
 				String awayScore = StringUtils.substringBefore(mrs[6], "-");
 				match.setHome_team_score(StringUtils.isBlank(homeScore)?null:Integer.valueOf(homeScore));
 				match.setAway_team_score(StringUtils.isBlank(awayScore)?null:Integer.valueOf(awayScore));
 				String fh_homeScore = StringUtils.substringAfter(mrs[7], "-");
 				String fh_awayScore = StringUtils.substringBefore(mrs[7], "-");
 				match.setHf_home_team_score(StringUtils.isBlank(fh_homeScore)?null:Integer.valueOf(fh_homeScore));
 				match.setHf_away_team_score(StringUtils.isBlank(fh_awayScore)?null:Integer.valueOf(fh_awayScore));
 				match.setHome_team_name(teamNameMap.get(mrs[4]));
 				match.setAway_team_name(teamNameMap.get(mrs[5]));
 				match.setRound(rd);
 				//未开始
 				if(match.getHf_away_team_score() == null){
 					match.setStatusId(1);
 				}else{
 					match.setStatusId(5);
 				}
 				
 				historyMatchQueue.add(match);
			}
		}
		teamNameMap.clear();
		
		return null;
	}

}
