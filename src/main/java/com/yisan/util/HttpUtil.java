package com.yisan.util;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	private final static int DEFAULT_TIME_OUT = 10000;
	private final static String DEFAULT_CHAR_SET = "utf-8";

	public static String get(String url) throws ClientProtocolException, IOException{
		return get(url,null,null);
	}
	public static String get(String url,Integer timeOut) throws ClientProtocolException, IOException{
		return get(url,null,timeOut);
	}
	public static String get(String url,Map<String,Object> params) throws ClientProtocolException, IOException{
		return get(url,params,null);
	}
	public static String get(String url,Map<String,Object> params,Integer timeout) throws ClientProtocolException, IOException{
		String content = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			timeout = timeout == null?DEFAULT_TIME_OUT:timeout;
			RequestConfig  requestConfig = RequestConfig.custom().setConnectTimeout(timeout).
					setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();

			url = getUrl(url,params);
			HttpGet get = new HttpGet(url);
			get.setConfig(requestConfig);

			content = httpclient.execute(get, new ResponseHandler<String>() {
				
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity,DEFAULT_CHAR_SET) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
			});
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				
			}
		}

		return content;
	}
	private static String getUrl(String url, Map<String, Object> params) {
		if(params == null || params.size()==0){
			return url;
		}

		Set<String> keys = params.keySet();

		Object val = "";
		StringBuilder sb = new StringBuilder();

		for(String key : keys){
			val = params.get(key);
			if(val != null){
				sb.append(key).append("=").append(val.toString()).append("&");
			}
		}
		url = "?" + sb.toString().substring(0, sb.length()-1);
		return url;
	}
}
