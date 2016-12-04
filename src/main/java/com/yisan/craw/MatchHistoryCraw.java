package com.yisan.craw;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
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
import com.yisan.service.LeagueService;
import com.yisan.util.ConfigReader;
import com.yisan.util.DateUtils;
import com.yisan.util.HttpUtil;

public class MatchHistoryCraw extends AbstractService{

	private static final Logger log = LoggerFactory.getLogger(MatchHistoryCraw.class);

	@Autowired
	private LeagueMapper leagueMapper;

	@Autowired
	@Qualifier("historyMatchQueue")
	private LinkedBlockingQueue<MatchBean> historyMatchQueue;

	@Autowired
	private LeagueService leagueService;

	@Autowired
	private LinkedBlockingQueue<TeamBean> teamQueue;

	private String url = ConfigReader.getProperty("url.007.league");

	private static boolean oneTime = true;

	@Override
	public boolean isWorking() {
		return "true".equals(ConfigReader.getProperty("history.match.job.run"));
	}

	@Override
	public void init() {

		if(!oneTime){
			//log.info("历史赛事任务只需要一个线程跑");
			return;
		}
		oneTime = false;
		List<OrgLeagueBean> list = leagueMapper.qryOraLeagueList(null);
		log.info("历史赛事爬取任务开始,未爬取的页面有{}个",list == null?0:list.size());
		long begin = System.currentTimeMillis();

		if(list == null || list.size()==0){
			return;
		}

		String detailUrl = "";
		Document doc = null;
		try{
			Map<String,Object> leagueMap = new HashMap<String,Object>();
			Boolean success = false;
			for(OrgLeagueBean bean : list){
				detailUrl = url + "/cn/" + bean.getLeague_type() + "/" + bean.getYear() + "/" + bean.getLeague_id() + ".html";
				doc = getContent(detailUrl);

				if(doc == null){
					log.warn("爬取页面{} 失败",detailUrl);
					continue;
				}

				if("SubLeague".equals(bean.getLeague_type())){
					//解析附加赛决赛
					String content = deal(doc);
					success = parse(content);

					String[] subLeagues = dealSubLeague(content);
					if(subLeagues != null && subLeagues.length>1){
						//联赛 附加赛， 附加赛决赛在上面解析了
						for(int i = 0;i<subLeagues.length-1;i++){
							detailUrl = url + "/cn/" + bean.getLeague_type() + "/" + bean.getYear() + "/" + 
									bean.getLeague_id() + "_" + subLeagues[0].split(",")[0] +  ".html";
							doc = getContent(detailUrl);
							success = parse(deal(doc));
							//睡眠3秒
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {}
						}
					}
				}else{
					success = parse(deal(doc));
				}

				if(success != null && success){
					leagueMap.put("league_id", bean.getLeague_id());
					leagueMap.put("year", bean.getYear());
					leagueService.markOrgLeagueCrawed(leagueMap);
				}

				//睡眠3秒
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
		}catch(Exception e){
			log.error("爬取历史赛事出错", e);
		}
		log.info("历史赛事爬取任务结束，耗时{}毫秒",System.currentTimeMillis() - begin);
	}

	//先去到主页，然后获取联赛、附加赛、附加赛决赛的编码，根据编码组合新的地址，再访爬取新组合的地址页面
	private String[] dealSubLeague(String content) {
		//var arrSubLeague = [[87,'联赛','联赛','League',1,46,46,0],[88,'附加赛','附加赛','Play-off',0,1,1,1],[90,'附加赛决赛','附加赛决赛','Play-off Final',0,1,1,0]];
		String arrSubLeague = StringUtils.substringAfter(content, "var arrSubLeague");

		if(arrSubLeague == null){
			return null;
		}

		arrSubLeague = StringUtils.substringAfter(arrSubLeague, "[[");
		arrSubLeague  = StringUtils.substringBefore(arrSubLeague, "]];");
		arrSubLeague = arrSubLeague.replace("],[", "#");
		String[] subLeagues = arrSubLeague.split("#");

		return subLeagues;
	}
	
	public static void main(String[] abc){
		String a = "124";
		String[] b = a.split(",");
		for(String b1 : b){
			System.out.println(b1);
		}
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

	protected Boolean parse(String content) throws ParseException{
		if(StringUtils.isBlank(content)){
			return null;
		}
		Map<String,String> teamNameMap = new HashMap<String,String>();
		String teamTmp = null;
		try{
			String afterArrTeam = StringUtils.substringAfter(content, "var arrTeam =");
			String team = StringUtils.substringBefore(afterArrTeam, "jh[");
			team = team.replace("[[", "").replace("]];", "").replace("'", "").replace("],[", "#");
			//String[] teams = StringUtils.split(team, "],[");
			String[] teams = team.split("#");

			if(content.contains("西乙") && ( content.contains("1130005") || content.contains("1246967"))){
				System.out.println();
			}
			
			//获取球队的信息
			int size = 6;
			String pic = null;
			for(String t: teams){
				teamTmp = t;
				String[] teamItem =  StringUtils.split(t,",");
				TeamBean bean = new TeamBean();
				size = teamItem.length;
				size = size>6?6:size;
				//[17,'米德尔斯堡','米杜士堡','Middlesbrough','米杜士堡','images/2013121225605.png']
				bean.setTeam_id(Long.valueOf(teamItem[0].trim()));
				bean.setTeam_name(teamItem[1]);
				bean.setTeam_name_hk(teamItem[2]);
				bean.setTeam_name_en(StringUtils.isBlank(teamItem[3])?null:teamItem[3]);
				try{
					//bean.setTeam_name_hk_s(teamItem[4]);
					pic = teamItem[5];
				}catch(Exception e2){
					pic = null;
					//bean.setTeam_name_hk_s("未知");
				}
				if(!StringUtils.isBlank(pic)){
					if(pic.contains("gif")||pic.contains("png")||pic.contains("jpg")||pic.contains("jpeg")){
						bean.setPic(pic);
					}
				}

				teamNameMap.put(teamItem[0].trim(), teamItem[1]);

				teamQueue.add(bean);
			}

			//jh["R_1"] = [[1609,1,-1,'2004-03-20 03:30',317,329,'2-1','0-0','','',,,'','',0,1,0,0,0,0,''],[1610,1,-1,'2004-03-20 03:45',315,177,'1-0','1-0','','',,,'','',0,1,0,0,0,0,''],
			//			  [1611,1,-1,'2004-03-20 03:45',332,312,'1-1','1-0','','',,,'','',0,1,0,0,0,0,''],
			//			  [1612,1,-1,'2004-03-20 04:00',324,331,'3-1','1-0','','',,,'','',0,1,0,0,0,0,''],
			//			  [1613,1,-1,'2004-03-23 03:45',322,180,'0-2','0-1','','',,,'','',0,1,0,0,0,0,'']];
			//jh["R_2"] = [[1614,1,-1,'2004-03-26 03:45',177,324,'2-1','2-0','','',,,'','',0,1,0,0,0,0,''],
			//	          [1615,1,-1,'2004-03-27 03:45',329,332,'0-1','0-1','','',,,'','',0,1,0,0,0,0,''],
			//			  [1616,1,-1,'2004-03-27 03:55',180,317,'2-2','2-1','','',,,'','',0,1,0,0,0,0,''],
			//			  [1617,1,-1,'2004-03-28 03:30',312,315,'0-2','0-0','','',,,'','',0,1,0,0,0,0,''],
			//			  [1618,1,-1,'2004-03-28 03:45',331,322,'0-1','0-1','','',,,'','',0,1,0,0,0,0,'']];
			//  ......
			// var scoreColor = [];
			//轮次信息
		}catch(Exception e){
			log.error("解析球队出错,team={}",teamTmp);
			log.error("解析球队出错",e);
		}
		String errorHappenData = null;
		try{
			String round = StringUtils.substringAfter(content, "jh[");
			round = StringUtils.substringBefore(round, "var");
			String[] rounds = round.split(";");
			//round = round.replace("],[", "#");
			String[] matchRound = null;
			String leagueId = "";
			int rd = 0;
			for(String r : rounds){
				if(StringUtils.isBlank(r)){
					break;
				}
				r = StringUtils.substringAfter(r, "[[");
				r = r.replace("],[", "#");
				//r = r.replace("]];", "").replace("'", "");
				r = StringUtils.substringBefore(r, "]]").replace("'", "");
				if(r.contains(",[")){
					r = StringUtils.substringAfter(r, ",[");
				}
				
				matchRound = r.split("#");
				rd++;
				errorHappenData = r;
				String[] mrs = null;
				// [[1250439,3,0,2017-05-29 00:30,9,1068,,,,,,,,,0,1,0,0,0,0,,,#1250443,3,0,2017-05-29 00:30,1072,13,,,,,,,,,0,1,0,0,0,0,,,#1250442,3,0,2017-05-29 00:30,3100,14,,,,,,,,,0,1,0,0,0,0,,,#1250441,3,0,2017-05-29 00:30,10,8,,,,,,,,,0,1,0,0,0,0,,,#1250440,3,0,2017-05-29 00:30,12,2037,,,,,,,,,0,1,0,0,0,0,,,
				for(String mr : matchRound){
					if(StringUtils.isBlank(mr)){
						continue;
					}
					if(mr.contains(",[")){
						mr = StringUtils.substringAfter(mr, ",[");
					}
					mrs = mr.split(",");
					leagueId = mrs[1];
					MatchBean match = new MatchBean();
					match.setMatch_id(mrs[0]);
					match.setLeague_id(Long.valueOf(leagueId));
					try{
						match.setMatch_time(DateUtils.parseToDate(mrs[3],"yyyy-MM-dd HH:mm"));
					}catch(Exception e){
						log.error("赛事时间转换出错,数据:{}",mr);
						match.setMatch_time(null);
					}
					match.setHome_team_id(Long.valueOf(mrs[4]));
					match.setAway_team_id(Long.valueOf(mrs[5]));
					String homeScore = StringUtils.substringBefore(mrs[6], "-");
					String awayScore = StringUtils.substringAfter(mrs[6], "-");
					try{
						match.setHome_team_score(StringUtils.isBlank(homeScore)?null:Integer.valueOf(homeScore));
						match.setAway_team_score(StringUtils.isBlank(awayScore)?null:Integer.valueOf(awayScore));
						
						String fh_homeScore = StringUtils.substringBefore(mrs[7], "-");
						String fh_awayScore = StringUtils.substringAfter(mrs[7], "-");
						match.setHf_home_team_score(StringUtils.isBlank(fh_homeScore)?null:Integer.valueOf(fh_homeScore));
						match.setHf_away_team_score(StringUtils.isBlank(fh_awayScore)?null:Integer.valueOf(fh_awayScore));
					}catch(Exception e1){
						log.error("解析比分出错,fh_homeScore={},fh_awayScore={}",homeScore,awayScore);
						match.setHome_team_score(100);
						match.setAway_team_score(100);
						match.setHf_home_team_score(100);
						match.setHf_away_team_score(100);
					}
					try{
						match.setHome_team_name(teamNameMap.get(mrs[4]));
						match.setAway_team_name(teamNameMap.get(mrs[5]));
						
					}catch(Exception e3){
						match.setHome_team_name("未知");
						match.setAway_team_name("未知");
						log.error("解析球队名出错",e3);
					}
					match.setRound(rd);
					//未开始
					if(match.getHf_away_team_score() == null){
						match.setStatusId(1);
					}else{
						match.setStatusId(5);
					}
				    log.debug("解析得到的赛事:{}",match);
					historyMatchQueue.add(match);
				}
			}
			teamNameMap.clear();
		}catch(Exception e){
			log.error("解析赛事发生错误的数据:{}",errorHappenData);
			log.error("解析赛事出错,",e);
			return false;
		}

		return true;
	}

}
