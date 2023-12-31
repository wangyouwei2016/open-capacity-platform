//package com.open.capacity.uaa.httpclient;
//
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//
//import com.alibaba.fastjson.JSONObject;
///** 
//* @version 创建时间：2017年12月14日 上午11:13:53 
//* 类说明
//*  http 授权码模式认证服务器的token
//*/
//public class AuthCodeInvoker {
//	
//	private static final String BASE_URL="http://127.0.0.1:9900/api-user/users?page=1&limit=10";
//	private static final String AUTHORIZE_URL = "http://127.0.0.1:8000/api-auth/oauth/authorize?client_id=owen&redirect_uri=http://127.0.0.1:9997/dashboard/login&state=abc&scope=app&response_type=code";
//	private static final String TOKEN_URL = "http://127.0.0.1:8000/api-auth/oauth/token?grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s" ;
//	public static String access_token;
//
//	private HttpClientBuilder httpClientBuilder;
//	private CloseableHttpClient httpClient;
//
//	// private static final Object MAPPER = new ObjectMapper();
//	public void start() throws Exception {
//
//		httpClientBuilder = HttpClientBuilder.create();
//		// CloseableHttpClient httpClient = HttpClients.createDefault();
//		// http POST
//		// HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
//		
//		String RealTOKEN_URL = String.format(TOKEN_URL, "nqYtqx", "owen" ,"owen","http://127.0.0.1:9997/dashboard/login") ;
//		
//		HttpPost httpPost = new HttpPost(RealTOKEN_URL);
//		// HttpGet httpGet = new HttpGet(url);
//
//		httpClient = httpClientBuilder.build();
//		CloseableHttpResponse response = null;
//		try {
//
//			response = httpClient.execute(httpPost);
//
//			String content = EntityUtils.toString(response.getEntity(), "UTF-8");
//			
//			JSONObject  jsonObject = JSONObject.parseObject(content);
//			access_token = jsonObject.getString("access_token");
//			System.out.println("得到:access_token : " + access_token);
//		} finally {
//			if (response != null) {
//				response.close();
//			}
//
//			httpClient.close();
//
//		}
//		
//
//		
//		String content = doAnotherGet(access_token);
//		System.out.println(content);
//
//	}
//
//	private String doAnotherGet(String json) throws Exception {
//		// 创建代理地址实例
//		// HttpHost proxy = new HttpHost("10.1.249.58", 3128);
//		// 创建路由 使用DefaultProxyRoutePlanner
//		// DefaultProxyRoutePlanner routePlanner = new
//		// DefaultProxyRoutePlanner(proxy);
//		// 路由添加到httpclient 实例创建中
//		// CloseableHttpClient httpClient
//		// =HttpClients.custom().setRoutePlanner(routePlanner).build();
//		//
//		CloseableHttpClient httpClient = HttpClients.createDefault();
//		// 创建http POST请求
//		HttpGet httpGet = new HttpGet(BASE_URL);
//		// HttpGet httpGet = new HttpGet(url);
//		// URL url = new URL(BASE_URL);
//
//		httpGet.addHeader("Authorization", "Bearer " + json);
//
//		CloseableHttpResponse response = null;
//		try {
//			// 执行请求
//			response = httpClient.execute(httpGet);
//			// 判断返回状态是否为200
//
//			String content = EntityUtils.toString(response.getEntity(), "UTF-8");
//
//			return content;
//		} finally {
//			if (response != null) {
//				response.close();
//			}
//
//			httpClient.close();
//		}
//	}
//	 
//	
//	public static void main(String[] args) throws Exception {
//		new AuthCodeInvoker().start();
//
//	}
//	
//}