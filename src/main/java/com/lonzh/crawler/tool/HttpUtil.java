package com.lonzh.crawler.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

public class HttpUtil {
	/**
	 * HTTP GET请求
	 *
	 * @see此get请求若无参数,则params传null
	 *
	 * @param url
	 * @param params
	 * @param headerMap
	 *
	 * @return
	 * @throws ParseException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String get(String url, Map<String, Object> params, Map<String, String> headerMap) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		String result = "";
		try {
			// 通过址默认配置创建一个httpClient实例
			httpClient = HttpClients.createDefault();
			// 创建httpGet远程连接实例
			HttpGet httpGet = new HttpGet(getOrPutNewUrl(url, params));
			// 设置请求头信息，鉴权
			if (null != headerMap && !headerMap.isEmpty()) {
				for (String key : headerMap.keySet()) {
					httpGet.setHeader(key, headerMap.get(key));
				}
			}
			// 设置配置请求参数
			// 连接主机服务超时时间
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)
					// 请求超时时间
					.setConnectionRequestTimeout(35000)
					// 数据读取超时时间
					.setSocketTimeout(60000).build();
			// 为httpGet实例设置配置
			httpGet.setConfig(requestConfig);
			// 执行get请求得到返回对象
			response = httpClient.execute(httpGet);
			// 通过返回对象获取返回数据
			HttpEntity entity = response.getEntity();
			// 通过EntityUtils中的toString方法将结果转换为字符串
			result = EntityUtils.toString(entity);
			httpGet.releaseConnection();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
			if (null != response) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != httpClient) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * HTTP PUT请求
	 * 
	 * @see此put请求若无参数,则params传null
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String put(String url, Map<String, Object> params) throws ClientProtocolException, IOException {
		String jsonStr = null;
		HttpPut httpPut = new HttpPut(getOrPutNewUrl(url, params));
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = httpClient.execute(httpPut);
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

	/**
	 * 封装get请求参数
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws ParseException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private static String getOrPutNewUrl(String url, Map<String, Object> params) {
		if (null != params && !params.isEmpty()) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
			for (String key : params.keySet()) {
				pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
			}
			try {
				url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, "UTF-8"));
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return url;
	}

	/**
	 * HTTP POST请求
	 * 
	 * @see此post请求若无参数,则params传null
	 * 
	 * @param url
	 * @param params
	 * @param headerMap
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static CloseableHttpResponse httpPost(String url, Map<String, Object> params, Map<String, String> headerMap)
			throws ClientProtocolException, IOException {
//		String jsonStr = null;
//		String accountId = null;
//		String token = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		if (null != headerMap && !headerMap.isEmpty()) {
			for (String key : headerMap.keySet()) {
				httpPost.setHeader(key, headerMap.get(key));
			}
		}
		if (null != params && !params.isEmpty()) {
			httpPost.setEntity(new StringEntity(JSONObject.fromObject(params).toString(), "UTF-8"));
			System.out.println(JSONObject.fromObject(params).toString());
		}

		CloseableHttpResponse response = httpClient.execute(httpPost);
		return response;
//		Header[] headers = response.getAllHeaders();
//		for (Header header : headers) {
//			if ("Set-Cookie".equals(header.getName())) {
//				String[] accountIdOrTokens = header.getValue().split(";");
//				for (String accountIdOrToken : accountIdOrTokens) {
//					if (accountIdOrToken.contains("PASSPORT_ACCOUNT_ID")) {
//						accountId = accountIdOrToken.split("=")[1];
//						System.out.println(accountId);
//					}
//					if (accountIdOrToken.contains("PASSPORT_TOKEN")) {
//						token = accountIdOrToken.split("=")[1];
//						System.out.println(token);
//					}
//				}
//
//			}
//			// System.out.println(header.getName() + "-->" + header.getValue());
//		}
//		HttpEntity entity = response.getEntity();
//		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//			jsonStr = EntityUtils.toString(entity, "UTF-8");
//			response.close();
//		} else {
//			jsonStr = EntityUtils.toString(entity, "UTF-8");
//			response.close();
//			throw new IllegalArgumentException(jsonStr);
//		}
//		if (accountId != null && token != null) {
//			return jsonStr =  accountId + "," + token;
//		} else {
//			return jsonStr;
//		}
	}

	/**
	 * 发送POST请求
	 * 
	 * @param url
	 *            URL地址
	 * @param headerMap
	 *            头文件
	 * @param bodyMap
	 *            body体
	 * @return
	 */
	public static String post(String url, Map<String, String> headerMap, Map<String, Object> bodyMap) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			// 拼接头文件
			if (null != headerMap) {
				for (String key : headerMap.keySet()) {
					String val = headerMap.get(key);
					httpPost.setHeader(key, val);
				}
			}
			// 拼接body体
			MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
			if (null != bodyMap) {
				for (String key : bodyMap.keySet()) {
					Object obj = bodyMap.get(key);
					if (obj instanceof File) {
						mEntityBuilder.addBinaryBody(key, (File) obj);
					} else if (obj instanceof String) {
						mEntityBuilder.addTextBody(key, URLEncoder.encode(obj.toString(), "UTF-8"));
					}
				}
			}
			httpPost.setEntity(mEntityBuilder.build());
			// 执行POST请求
			response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				result = EntityUtils.toString(resEntity);
				EntityUtils.consume(resEntity);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			HttpClientUtils.closeQuietly(httpclient);
			HttpClientUtils.closeQuietly(response);
		}
		return result;
	}

	/**
	 * 宇视HTTP DELETE请求
	 * 
	 * @see此delete请求仅限于宇视接口调用
	 * 
	 * @param url
	 * @param params
	 * @param personDbCode
	 *            人脸库编码
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String delete(String url, String personDbCode, Map<String, String> headerMap)
			throws ClientProtocolException, IOException {
		String jsonStr = null;
		HttpDelete httpDelete = new HttpDelete(url + "/" + personDbCode);
		if (null != headerMap && !headerMap.isEmpty()) {
			for (String key : headerMap.keySet()) {
				httpDelete.setHeader(key, headerMap.get(key));
			}
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = httpClient.execute(httpDelete);
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

	/**
	 * 返回响应正文(无乱码)
	 * 
	 * @param response
	 * @return String
	 */
	public static String getResponseString(HttpResponse response) {
		// 响应实体类
		HttpEntity entity = response.getEntity();
		// 响应正文
		StringBuilder result = new StringBuilder();
		if (entity != null) {
			byte[] bytes = new byte[4096];
			int size;
			InputStream instream = null;
			try {
				instream = entity.getContent();
				while ((size = instream.read(bytes)) > 0) {
					String str = new String(bytes, 0, size, "utf-8");
					result.append(str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != instream) {
						instream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result.toString();
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		// 测试get请求---无参
		// String jsonStr = HttpUtil.get("http://localhost:8080/SLSD1/testGet", null);
		// System.out.println(jsonStr);
		// 测试get请求---有参<无文件>
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("a", "aaa");
		// map.put("b", 666);
		// String jsonStr = HttpUtil.get("http://localhost:8080/SLSD1/testGetAndParam",
		// map);
		// System.out.println(jsonStr);
		// 测试put请求---无参
		// String jsonStr = HttpUtil.put("http://localhost:8080/SLSD1/testPut", null);
		// System.out.println(jsonStr);
		// 测试put请求---有参<无文件>
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("a", "aaa");
		// map.put("b", 666);
		// String jsonStr = HttpUtil.put("http://localhost:8080/SLSD1/testPutAndParam",
		// map);
		// System.out.println(jsonStr);
		// 测试post请求---无参
		// String jsonStr = HttpUtil.postUrl("http://localhost:8080/SLSD1/testPost",
		// null);
		// System.out.println(jsonStr);
		// 测试post请求---有参<无文件>
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("a", "aaa");
		// map.put("b", 666);
		// String jsonStr =
		// HttpUtil.post("http://localhost:8080/SLSD1/testPostAndParam", map, null,
		// null);
		// System.out.println(jsonStr);
		// 测试post请求---有参<有文件>
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("a", "aaa");
		// map.put("b", 666);
		// String jsonStr =
		// HttpUtil.post("http://localhost:8080/SLSD1/testPostAndParamAndFile", map, new
		// File("C:/Users/DaiHaijiao/Pictures/ll.jpg"), "imgFile");
		// System.out.println(jsonStr);
	}

}
