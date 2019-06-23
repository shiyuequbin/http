package com.lonzh.crawler.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lonzh.crawler.tool.EhCacheFactory;
import com.lonzh.crawler.tool.FileUtils;
import com.lonzh.crawler.tool.FormatConvertor;
import com.lonzh.crawler.tool.FtpUtils;
import com.lonzh.crawler.tool.HttpUtil;
import com.lonzh.crawler.tool.PropertiesReader;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 饿了么逻辑层
 * 
 * @author LZ
 * 
 */
@Service("elemeTool")
public class ElemeTool {
	private static final Logger logger = Logger.getLogger(ElemeTool.class);

	private static Cache elemeCache;

	static {
		elemeCache = EhCacheFactory.getCacheInstance("eleme");
	}

	/**
	 * 获取配送中运单号列表
	 * 
	 * @return
	 */
	private static List<String> listTrackingId(String passportAccountId,String passportToken) {
		String result = null;
		// 饿了么获取订单列表URL
		String url = PropertiesReader.getProperty("eleme.listTrackingId.url");
		// 构造请求头文件
		Map<String, String> headerMap = new HashMap<String, String>(1);
//		String passportAccountId = PropertiesReader.getProperty("eleme.login.passport_account_id");
//		String passportToken = PropertiesReader.getProperty("eleme.login.passport_token");
		headerMap.put("Cookie", "PASSPORT_ACCOUNT_ID=" + passportAccountId + "; PASSPORT_TOKEN=" + passportToken);
		// 请求页码
		int pageIndex = 1;
		// 每页条数
		int pageSize = 20;
		// 配送状态 - 配送中
		int shippingState = 30;

		// 构造请求头文件
		Map<String, Object> params = new HashMap<String, Object>(25);
		Date now = new Date();
		String todayStr = FormatConvertor.formatDate(now);
		params.put("carrierDriver", "");
		params.put("customerComplainState", "");
		params.put("customerEvaluateState", "");
		params.put("customerOvertimeState", "");
		params.put("dateType", 0);
		params.put("deliveryType", "");
		params.put("exceptionCancelState", "");
		params.put("fraudState", "");
		params.put("pageIndex", pageIndex);
		params.put("gridId", "");
		params.put("merchantComplainState", "");
		params.put("merchantEvaluateState", "");
		params.put("pageSize", pageSize);
		params.put("reasonCode", "");
		params.put("shippingOption", "");
		params.put("shippingState", shippingState);
		params.put("stationId", "");
		params.put("trackingId", "");
		params.put("startDate", todayStr);
		params.put("endDate", todayStr);
		params.put("startTime", "00:00");
		params.put("endTime", "24:00");
		// 发送get请求
		result = HttpUtil.get(url, params, headerMap);
		List<String> resultList = new ArrayList<>();
		final String success = "成功";
		final String msg = "msg";
		if (null != result && !result.trim().isEmpty()) {
			JSONObject jsonObject = JSONObject.fromObject(result);
			if (Objects.equals(success, jsonObject.get(msg))) {
				JSONArray array = jsonObject.getJSONObject("data").getJSONArray("list");
				for (Object object : array) {
					JSONObject json = JSONObject.fromObject(object);
					// 运单号
					String trackingId = json.getString("trackingId");
					resultList.add(trackingId);
				}
			}
		}
		return resultList;
	}

	/**
	 * 根据运单号获取运单详情
	 * 
	 * @param trackingId
	 *            运单ID
	 * @return 运单详情
	 */
	private static String getTrackingDetailByTrackingId(String trackingId) {
		String url = PropertiesReader.getProperty("eleme.getTrackingDetailByTrackingId.url") + trackingId;
		// 构造请求头文件
		Map<String, String> headerMap = new HashMap<String, String>(1);
		String passportAccountId = PropertiesReader.getProperty("eleme.login.passport_account_id");
		String passportToken = PropertiesReader.getProperty("eleme.login.passport_token");
		headerMap.put("Cookie", "PASSPORT_ACCOUNT_ID=" + passportAccountId + "; PASSPORT_TOKEN=" + passportToken);
		return HttpUtil.get(url, null, headerMap);
	}

	/**
	 * 解析订单信息，只留需要的字段，去除无用的字段
	 * 
	 * @param jsonObject
	 *            订单信息
	 * @return
	 */
	private static Map<String, String> parse(JSONObject jsonObject) {
		Map<String, String> resultMap = new HashMap<>(20);
		// 下单时间
		resultMap.put("platformOrderTime", jsonObject.getString("platformCreatedTime"));
		// 客户姓名
		resultMap.put("recipientName", jsonObject.getString("customerName"));
		// 客户电话
		resultMap.put("recipientPhone", jsonObject.getString("customerPhone"));
		// 客户地址
		resultMap.put("recipientAddr", jsonObject.getString("customerAddress"));
		// 骑手姓名
		resultMap.put("riderName", jsonObject.getString("carrierDriverName"));
		// 骑手电话
		resultMap.put("riderPhone", jsonObject.getString("carrierDriverPhone"));
		// 商家名称
		resultMap.put("poiName", jsonObject.getString("merchantName"));
		// 商家电话
		resultMap.put("poiPhone", jsonObject.getString("merchantPhone"));
		// 商家地址
		resultMap.put("poiAddr", jsonObject.getString("merchantAddress"));
		// 订单号
		resultMap.put("platformOrderId", jsonObject.getString("platformTrackingId"));
		// 订单价格
		resultMap.put("pkgPrice", ((JSONObject) jsonObject.get("shoppingCart")).getString("totalAmount"));
		// 区域名称
		resultMap.put("orgName", "");
		// 数据来源 1对应美团，2对应饿了么
		resultMap.put("laiyuan", "2");
		// 运单号
		resultMap.put("waybillid", jsonObject.getString("trackingId"));
		resultMap.put("shiwu", getDishName(jsonObject.getJSONObject("shoppingCart").getJSONArray("cartItem")));
		return resultMap;
	}

	/**
	 * 获取菜品名称拼接字符串
	 * 
	 * @param array
	 * @return
	 */
	private static String getDishName(JSONArray array) {
		String dishName = "";
		final String comma = ",";
		for (Object object : array) {
			dishName += JSONObject.fromObject(object).getString("name") + comma;
		}
		if (!dishName.isEmpty() && dishName.endsWith(comma)) {
			dishName = dishName.substring(0, dishName.length() - 1);
		}
		return dishName;
	}

	/**
	 * 与缓存中对比是否有更新
	 * 
	 * @param elementValueMap
	 * @return 更新后的订单详情
	 */
	private static List<String> compareUpdate(Map<String, String> elementValueMap, List<String> finalList) {
		String elementValue = JSONObject.fromObject(elementValueMap).toString();
		String platformOrderId = elementValueMap.get("platformOrderId");
		Element element = new Element(platformOrderId, elementValue);
		// 缓存中不存在该订单号，加入缓存，并添加到返回列表中
		if (elemeCache.get(platformOrderId) == null) {
			elemeCache.put(element);
			finalList.add(elementValue);
		} else { // 缓存中存在该订单号，对比内容是否更新，如果有更新，将更新后的内容刷新到缓存，并添加到返回列表中
			String cacheValueStr = elemeCache.get(platformOrderId).getObjectValue().toString().trim();
			if (!Objects.equals(cacheValueStr, elementValue)) {
				elemeCache.put(element);
				finalList.add(elementValue);
			}
		}
		return finalList;
	}

	/**
	 * 抓取方法
	 */
	public static void doCapture(String passportAccountId,String passportToken) {
		boolean flag = true;
		while (flag) {
			// 获取“配送中”订单
			List<String> trackingIdList = ElemeTool.listTrackingId(passportAccountId,passportToken);
			List<String> finalList = new ArrayList<String>();
			// 根据运单号获取运单详情
			for (String trackingId : trackingIdList) {
				String result = getTrackingDetailByTrackingId(trackingId);
				Map<String, String> elementValueMap = parse(JSONObject.fromObject(result).getJSONObject("data"));
				compareUpdate(elementValueMap, finalList);
			}
			elemeCache.flush();
			logger.info("饿了么缓存数量：" + EhCacheFactory.getCacheInstance("eleme").getSize());
			logger.info("List内数量：" + finalList.size());
			String filePath = PropertiesReader.getProperty("eleme.json.path");
			String fileName = "eleme#" + System.currentTimeMillis() + ".json";
			if (0 < finalList.size()) {
				try {
					FileUtils.writeUTFFile(filePath + File.separator + fileName, JSONArray.fromObject(finalList).toString());
					// ftp服务器信息
					String hostName = PropertiesReader.getProperty("ftp.hostName");
					Integer port = PropertiesReader.getIntProperty("ftp.port", 21);
					String userName = PropertiesReader.getProperty("ftp.userName");
					String password = PropertiesReader.getProperty("ftp.password");
					String path = PropertiesReader.getProperty("ftp.path");
					// 获取ftp工具类实例化对象
					FtpUtils ftpUtil = new FtpUtils(hostName, port, userName, password, path);
					// 登陆ftp服务器
					if (ftpUtil.loginFTP()) {
						// 上传文件
						if (ftpUtil.uploadFile(fileName + ".tmp", filePath + File.separator + fileName)) {
							// 将上传的文件重命名
							if (ftpUtil.renameFile(fileName + ".tmp", fileName)) {

							}
						}
					}
					// 关闭客户端
					ftpUtil.close();
				} catch (Exception e) {
					e.printStackTrace();
					flag = false;
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				flag = false;
			}
		}
		EhCacheFactory.shutdown();
		System.exit(0);
	}

}
