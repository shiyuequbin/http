package com.lonzh.crawler.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 配置文件读取类
 * 
 * @author LZ
 * 
 */
public class PropertiesReader {
	private static Logger msgLogInfo = Logger.getLogger("msgInfo");

	private static String PROPFILE = "config.properties";

	private static Properties properties = new Properties();

	static {
		loadFile();
	}

	private static void loadFile() {
		InputStream file = PropertiesReader.class.getClassLoader().getResourceAsStream(PROPFILE);
		try {
			properties.load(file);
			file.close();
		} catch (IOException e) {
			msgLogInfo.error("加载配置文件config.properties出现异常!");
			e.printStackTrace();
		}
	}

	public static String getProperty4Reload(String name) {
		String value = properties.getProperty(name);
		if (value == null) {
			loadFile();
			value = properties.getProperty(name);
			if (value == null) {
				msgLogInfo.warn("文件config.properties在重新读取后，获取key:" + name + "的值依然为null!");
			}
		}
		return value;
	}

	public static String getProperty(String name) {
		return properties.getProperty(name);
	}

	public static int getIntProperty(String name, int number) {
		String str = properties.getProperty(name);
		if (str == null) {
			return number;
		}
		return Integer.parseInt(str);
	}

	public static long getLongProperty(String name, long number) {
		String str = properties.getProperty(name);
		if (str == null) {
			return number;
		}
		return Long.parseLong(str);
	}

	public static boolean getBooleanProperty(String name, boolean bool) {
		String str = properties.getProperty(name);
		if (str == null) {
			return bool;
		}
		return Boolean.parseBoolean(str);
	}

}
