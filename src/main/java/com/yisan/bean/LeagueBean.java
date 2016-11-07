package com.yisan.bean;

import java.util.Date;

public class LeagueBean {
	private long league_id;
	private String league_name_cn;
	private String league_name_en;
	private String league_name;
	private int is_hot;
	private int level;
	private String country;
	private Date crt_time;
	private Date update_time;
	private int status;
	private int type;
	private String year;
	
	
	public long getLeague_id() {
		return league_id;
	}
	public void setLeague_id(long league_id) {
		this.league_id = league_id;
	}
	public String getLeague_name_cn() {
		return league_name_cn;
	}
	public void setLeague_name_cn(String league_name_cn) {
		this.league_name_cn = league_name_cn;
	}
	public String getLeague_name_en() {
		return league_name_en;
	}
	public void setLeague_name_en(String league_name_en) {
		this.league_name_en = league_name_en;
	}
	public String getLeague_name() {
		return league_name;
	}
	public void setLeague_name(String league_name) {
		this.league_name = league_name;
	}
	public int getIs_hot() {
		return is_hot;
	}
	public void setIs_hot(int is_hot) {
		this.is_hot = is_hot;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	@Override
	public String toString() {
		return "LeagueBean [league_id=" + league_id + ", league_name_cn=" + league_name_cn + ", league_name_en="
				+ league_name_en + ", league_name=" + league_name + ", is_hot=" + is_hot + ", level=" + level
				+ ", country=" + country + ", crt_time=" + crt_time + ", update_time=" + update_time + ", status="
				+ status + ", type=" + type + ", year=" + year + "]";
	}

}
