package com.yisan.craw;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService{
	private static final Logger log = LoggerFactory.getLogger(AbstractService.class);
	
	protected String url = "";
	
	protected static final int max_try_times = 5;

	public void run(){
		
		if(!isWorking()){
			return;
		}
		exe();
	}
	
	public abstract boolean isWorking();
	
	public abstract void init();
	
	public void exe(){
		init();
	}
	
	protected Document getContent(String url) {
		log.info("爬取地址:{}",url);
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		}catch(SocketTimeoutException se){
			try {
				doc = Jsoup.connect(url).timeout(10000).get();
				return doc;
			} catch (IOException e) {
				log.error("重试爬取出错,url={}", url,e);
			}
		}catch (Exception e) {
			log.error("爬取出错,url={}", url,e);
			try {
				Thread.sleep(5000);
				doc = Jsoup.connect(url).timeout(10000).get();
				return doc;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return doc;
	}
	
	protected Object parse(Document doc){
		
		return null;
	}

	//初始化Connection
	protected Connection initConnection(String url) {
    	Connection conn = Jsoup.connect(url);
    	conn.header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
    	conn.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    	conn.header("accept-language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
    	conn.header("connection", "keep-alive");
    	conn.header("referer", url);
    	conn.header("host", url.replace("http://", ""));
    	//conn.header("Cookie", "fAnalyCookie=null; CNZZDATA1831853=cnzz_eid%3D608147958-1434093024-http%253A%252F%252F61.143.225.173%253A88%252F; detailCookie=null; Cookie=null");
    	conn.ignoreContentType(true);
    	conn.timeout(30000);
        return conn;
    }
	
	/**
	 * 获取url页面文本，请求失败情况下最多重复请求5次
	 * @param url
	 * @return
	 * @throws IOException
	 */
	protected Document getContentDoc(String url) throws IOException{
		Document doc = null;
		int times = 0;
		
		while(times<max_try_times){
			try {
				times++;
				return doc = initConnection(url).url(url).get();
			} catch (IOException e) {
				if(times == max_try_times){
					throw e;
				}
			}
		}
		return doc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
