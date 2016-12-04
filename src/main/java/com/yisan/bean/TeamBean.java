package com.yisan.bean;

import java.util.Date;

public class TeamBean {
	
	private Long team_id;
	private String team_name;
	private String team_name_hk;
	private String team_name_hk_s;
	private String team_name_en;
	private int status;
	private int rank;
	private String country;
	private String pic;
	private Date crt_time;
	private Date update_time;
	
	
	public Long getTeam_id() {
		return team_id;
	}
	public void setTeam_id(Long team_id) {
		this.team_id = team_id;
	}
	public String getTeam_name() {
		return team_name;
	}
	public void setTeam_name(String team_name) {
		this.team_name = team_name;
	}
	public String getTeam_name_hk() {
		return team_name_hk;
	}
	public void setTeam_name_hk(String team_name_hk) {
		this.team_name_hk = team_name_hk;
	}
	public String getTeam_name_hk_s() {
		return team_name_hk_s;
	}
	public void setTeam_name_hk_s(String team_name_hk_s) {
		this.team_name_hk_s = team_name_hk_s;
	}
	public String getTeam_name_en() {
		return team_name_en;
	}
	public void setTeam_name_en(String team_name_en) {
		this.team_name_en = team_name_en;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
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
	@Override
	public String toString() {
		return "TeamBean [team_id=" + team_id + ", team_name=" + team_name + ", team_name_hk=" + team_name_hk
				+ ", team_name_hk_s=" + team_name_hk_s + ", team_name_en=" + team_name_en + ", status=" + status
				+ ", rank=" + rank + ", country=" + country + ", pic=" + pic + ", crt_time=" + crt_time
				+ ", update_time=" + update_time + "]";
	}
	
}
