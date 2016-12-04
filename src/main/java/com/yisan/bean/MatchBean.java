package com.yisan.bean;

import java.util.Date;

public class MatchBean {
	private String match_id;
	private Long league_id;
	private String home_team_name;
	private Long home_team_id;
	private String away_team_name;
	private Long away_team_id;
	private Integer home_team_score;
	private Integer away_team_score;
	private Date match_time;
	private Date crt_time;
	private Date update_time;
	private Integer status;
	private Integer statusId;
	private String league_name;
	private int round;
	private Integer hf_home_team_score;
	private Integer hf_away_team_score;
	public String getMatch_id() {
		return match_id;
	}
	public void setMatch_id(String match_id) {
		this.match_id = match_id;
	}
	public Long getLeague_id() {
		return league_id;
	}
	public void setLeague_id(Long league_id) {
		this.league_id = league_id;
	}
	public String getHome_team_name() {
		return home_team_name;
	}
	public void setHome_team_name(String home_team_name) {
		this.home_team_name = home_team_name;
	}
	public Long getHome_team_id() {
		return home_team_id;
	}
	public void setHome_team_id(Long home_team_id) {
		this.home_team_id = home_team_id;
	}
	public String getAway_team_name() {
		return away_team_name;
	}
	public void setAway_team_name(String away_team_name) {
		this.away_team_name = away_team_name;
	}
	public Long getAway_team_id() {
		return away_team_id;
	}
	public void setAway_team_id(Long away_team_id) {
		this.away_team_id = away_team_id;
	}
	public Integer getHome_team_score() {
		return home_team_score;
	}
	public void setHome_team_score(Integer home_team_score) {
		this.home_team_score = home_team_score;
	}
	public Integer getAway_team_score() {
		return away_team_score;
	}
	public void setAway_team_score(Integer away_team_score) {
		this.away_team_score = away_team_score;
	}
	public Date getMatch_time() {
		return match_time;
	}
	public void setMatch_time(Date match_time) {
		this.match_time = match_time;
	}
	public Date getCrt_time() {
		return crt_time;
	}
	public void setCrt_time(Date crt_time) {
		this.crt_time = crt_time;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
	public String getLeague_name() {
		return league_name;
	}
	public void setLeague_name(String league_name) {
		this.league_name = league_name;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public Integer getHf_home_team_score() {
		return hf_home_team_score;
	}
	public void setHf_home_team_score(Integer hf_home_team_score) {
		this.hf_home_team_score = hf_home_team_score;
	}
	public Integer getHf_away_team_score() {
		return hf_away_team_score;
	}
	public void setHf_away_team_score(Integer hf_away_team_score) {
		this.hf_away_team_score = hf_away_team_score;
	}
	@Override
	public String toString() {
		return "MatchBean [match_id=" + match_id + ", league_id=" + league_id + ", home_team_name=" + home_team_name
				+ ", home_team_id=" + home_team_id + ", away_team_name=" + away_team_name + ", away_team_id="
				+ away_team_id + ", home_team_score=" + home_team_score + ", away_team_score=" + away_team_score
				+ ", match_time=" + match_time + ", crt_time=" + crt_time + ", update_time=" + update_time + ", status="
				+ status + ", statusId=" + statusId + ", league_name=" + league_name + ", round=" + round
				+ ", hf_home_team_score=" + hf_home_team_score + ", hf_away_team_score=" + hf_away_team_score + "]";
	}


}
