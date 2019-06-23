package com.lonzh.crawler.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * FTP工具类
 * 
 * @author LZ
 * 
 */
public class FtpUtils {
	private static final Logger logger = Logger.getLogger(FtpUtils.class);

	/**
	 * ftp服务器地址
	 */
	private String hostName;

	/**
	 * ftp服务器端口号默认为21
	 */
	private Integer port;

	/**
	 * ftp登录账号
	 */
	private String userName;

	/**
	 * ftp登录密码
	 */
	private String password;

	/**
	 * 操作文件路径
	 */
	private String path;

	/**
	 * ftp客户端实例对象
	 */
	private FTPClient ftpClient;

	public FtpUtils(String hostName, Integer port, String userName, String password, String path) {
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.path = path;
	}

	/**
	 * 登陆ftp服务器
	 * 
	 * @return 是否登陆成功
	 */
	public boolean loginFTP() {
		this.ftpClient = new FTPClient();
		ftpClient.setControlEncoding("utf-8");
		try {
			// 连接ftp服务器
			ftpClient.connect(this.hostName, this.port);
			// 登录ftp服务器
			ftpClient.login(this.userName, this.password);
			// 是否成功登录服务器
			int replyCode = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				logger.error("连接ftp服务器失败:" + this.hostName + ":" + this.port);
				ftpClient.disconnect();
				return false;
			}
			logger.info("连接ftp服务器成功:" + this.hostName + ":" + this.port);
			ftpClient.makeDirectory(this.path);
			ftpClient.changeWorkingDirectory(this.path);
		} catch (MalformedURLException e) {
			logger.error("异常信息为", e);
		} catch (IOException e) {
			logger.error("异常信息为", e);
		}
		return true;
	}

	/**
	 * 上传文件
	 * 
	 * @param fileName
	 *            上传到ftp的文件名
	 * @param originFileName
	 *            上传文件的原始绝对路径
	 * @return
	 */
	public boolean uploadFile(String fileName, String originFileName) {
		boolean result = false;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(originFileName));
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			result = ftpClient.storeFile(fileName, inputStream);
			inputStream.close();
			if (result) {
				logger.info("上传文件成功 " + fileName);
			}
		} catch (Exception e) {
			logger.error("上传文件失败 " + fileName);
			logger.error("异常信息为 " + e.getMessage());
			this.close();
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 判断ftp服务器文件是否存在
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean existFile(String fileName) {
		boolean flag = false;
		FTPFile[] ftpFileArr;
		try {
			ftpFileArr = ftpClient.listFiles(fileName);
			if (ftpFileArr.length > 0) {
				flag = true;
				logger.info("查找文件成功，且存在 " + fileName);
			} else {
				logger.info("查找文件不存在 " + fileName);
			}
		} catch (IOException e) {
			logger.error("查找文件失败 " + fileName);
			logger.error("异常信息为 ", e);
			this.close();
		}
		return flag;
	}

	/**
	 * * 下载文件 *
	 * 
	 * @param filename
	 *            文件名称 *
	 * @param localpath
	 *            下载后的文件路径 *
	 * @return
	 */
	public boolean downloadFile(String fileName, String localpath) {
		boolean flag = false;
		OutputStream os = null;
		try {
			FTPFile[] ftpFiles = ftpClient.listFiles();
			for (FTPFile file : ftpFiles) {
				if (fileName.equalsIgnoreCase(file.getName())) {
					File localFile = new File(localpath + File.separator + file.getName());
					os = new FileOutputStream(localFile);
					ftpClient.retrieveFile(file.getName(), os);
					os.close();
				}
			}
			flag = true;
			logger.info("下载文件成功 " + fileName);
		} catch (Exception e) {
			logger.error("下载文件失败 " + fileName);
			logger.error("异常信息为 ", e);
			this.close();
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					logger.error("异常信息为 ", e);
				}
			}
		}
		return flag;
	}

	/**
	 * * 删除文件 *
	 * 
	 * @param pathname
	 *            FTP服务器保存目录 *
	 * @param filename
	 *            要删除的文件名称 *
	 * @return
	 */
	public boolean deleteFile(String fileName) {
		boolean flag = false;
		try {
			if (FTPReply.FILE_ACTION_OK == ftpClient.dele(fileName)) {
				flag = true;
				logger.info("删除文件成功 " + fileName);
			} else {
				logger.info("删除文件失败 " + fileName);
			}
		} catch (Exception e) {
			logger.error("删除文件失败 " + fileName);
			logger.error("异常信息为 " + e.getMessage());
		}
		return flag;
	}

	/**
	 * 重命名文件
	 * 
	 * @param srcFname
	 *            原文件名
	 * @param targetFname
	 *            新文件名
	 * @return
	 */
	public boolean renameFile(String srcFname, String targetFname) {
		boolean flag = false;
		if (ftpClient != null) {
			try {
				flag = ftpClient.rename(srcFname, targetFname);
				if (flag) {
					logger.info("重命名文件成功，原文件名为 " + srcFname + "，重命名后文件名为 " + targetFname);
				}
			} catch (IOException e) {
				logger.error("重命名文件失败，原文件名为 " + srcFname);
				logger.error("异常信息为 ", e);
				this.close();
			}
		}
		return flag;
	}

	/**
	 * 关闭ftp客户端，释放资源
	 */
	public void close() {
		if (null != ftpClient) {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					logger.error("异常信息为 ", e);
				}
			}
		}
	}

	public static void main(String[] args) {
		// ftp服务器信息
		String hostName = PropertiesReader.getProperty("ftp.hostName");
		Integer port = PropertiesReader.getIntProperty("ftp.port", 21);
		String userName = PropertiesReader.getProperty("ftp.userName");
		String password = PropertiesReader.getProperty("ftp.password");
		String path = PropertiesReader.getProperty("ftp.path");
		// 获取ftp工具类实例化对象
		FtpUtils ftpUtil = new FtpUtils(hostName, port, userName, password, path);
		// 登陆ftp服务器
		ftpUtil.loginFTP();
		// 上传文件
		ftpUtil.uploadFile("1.txt.tmp", "D:\\1.txt");
		// 判断指定文件是否存在
		boolean existFlag = ftpUtil.existFile("1.txt");
		if (existFlag) {
			// 如果存在下载后删除
			ftpUtil.downloadFile("1.txt", "D:\\data");
			ftpUtil.deleteFile("1.txt");
		}
		// 将上传的文件重命名
		ftpUtil.renameFile("1.txt.tmp", "1.txt");
		// 关闭客户端
		ftpUtil.close();
	}

}
