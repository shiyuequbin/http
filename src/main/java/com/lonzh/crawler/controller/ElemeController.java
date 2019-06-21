package com.lonzh.crawler.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lonzh.crawler.service.ElemeTool;
import com.lonzh.crawler.tool.HttpUtil;


@Controller
@RequestMapping("eleme")
public class ElemeController {
	@Autowired
	private ElemeTool elemeTool;
	
	private static final Logger logger = Logger.getLogger(ElemeController.class);

	@RequestMapping(value = "captcha/{mobilePhone}")
	public @ResponseBody String getCode(@PathVariable("mobilePhone") String mobilePhone)
			throws ClientProtocolException, IOException {
		String jsonStr = null;
		String url = "https://passport.ele.me/api/captcha";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mobilePhone", mobilePhone);
		Map<String, String> headerMap = new HashMap<String, String>();
		CloseableHttpResponse response = HttpUtil.httpPost(url, params, headerMap);
		HttpEntity entity = response.getEntity();
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			jsonStr = EntityUtils.toString(entity, "UTF-8");
			response.close();
		} else {
			jsonStr = EntityUtils.toString(entity, "UTF-8");
			response.close();
			throw new IllegalArgumentException(jsonStr);
		}
		return jsonStr;
	}
	@SuppressWarnings("static-access")
	@RequestMapping(value = "captcha/{mobilePhone}/{captcha}")
	public @ResponseBody String getLogin(@PathVariable("mobilePhone") String mobilePhone,
			@PathVariable("captcha") String captcha) throws ClientProtocolException, IOException {
		String accountId = null;
		String token = null;
		String result = "";
		String url = "https://passport.ele.me/api/login";
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> headerMap = new HashMap<String, String>();
		params.put("loginPhone", mobilePhone);
		params.put("captcha", captcha);
		params.put("endPointType", "H5");
		params.put("domain", "AGENTS");
		try {
			CloseableHttpResponse response = HttpUtil.httpPost(url, params, headerMap);
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				if ("Set-Cookie".equals(header.getName())) {
					String[] accountIdOrTokens = header.getValue().split(";");
					for (String accountIdOrToken : accountIdOrTokens) {
						if (accountIdOrToken.contains("PASSPORT_ACCOUNT_ID")) {
							accountId = accountIdOrToken.split("=")[1];
							System.out.println(accountId);
						}
						if (accountIdOrToken.contains("PASSPORT_TOKEN")) {
							token = accountIdOrToken.split("=")[1];
							System.out.println(token);
						}
					}

				}
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		if (accountId != null && token != null) {
			final String passportAccountId=accountId;
			final String passportToken=token;
			ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadFactory() {
				@Override
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(runnable);
					thread.setName("Thread-ElemeController");
					return thread;
				}
			});
			// 饿了么抓取线程
			executor.submit(new Runnable() {
				@Override
				public void run() {
					logger.info("饿了么抓取线程开启");
					elemeTool.doCapture(passportAccountId, passportToken);
				}
			});
		} 
		return result;
	}
}
