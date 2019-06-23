package com.lonzh.crawler.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 文件操作工具类
 * 
 * @author LZ
 * 
 */
public class FileUtils {

	/**
	 * 创建一个文件
	 * 
	 * @param destFileName
	 *            带路径的文件eg： D:/test/dir.txt （若没有test文件夹，则会自动创建该文件夹）
	 * @return boolean
	 * @throws IOException
	 */
	public final static boolean createFile(String destFileName) throws IOException {
		File file = new File(destFileName);
		boolean flag = false;
		if (!file.exists()) {
			FileUtils.createDir(file.getParentFile().getPath());
			// 创建目标文件
			file.createNewFile();
			flag = true;
		}
		file = null;
		return flag;
	}

	/**
	 * @param destFileName
	 *            带绝对路径的文件eg： D:/test/dir.txt
	 * @return
	 * @throws IOException
	 */
	public final static boolean deleteFile(String destFileName) throws IOException {
		File file = new File(destFileName);
		boolean flag = false;
		if (file.exists()) {
			flag = file.delete();
		}
		return flag;
	}

	/**
	 * 在某一个路径（位置）创建文件夹
	 * 
	 * @param destDirName
	 *            D:/test/dir
	 * @return boolean
	 */
	public final static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		boolean flag = false;
		if (!dir.exists()) {
			if (!destDirName.endsWith(File.separator)) {
				destDirName = stringConcat(true, new StringBuilder(), destDirName);
			}
			dir.mkdirs();
			flag = true;
		}
		dir = null;
		return flag;
	}

	/**
	 * 移动一个文件夹里的所有文件(文件夹除外)到另一个文件夹
	 * 
	 * @param oldPath文件所在夹子路径
	 * @param newPath目标文件夹路径
	 */
	public final static void removeDirFilesToDir(String oldPath, String newPath) {
		createDir(newPath);
		File dir = new File(oldPath);
		// 该文件目录下文件全部放入数组
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					String fileName = files[i].getName();
					File oldP = new File(oldPath, fileName);
					File newP = new File(newPath, fileName);
					if (newP.exists()) {
						newP.delete();
					}
					oldP.renameTo(newP);
				}
			}
		}
	}

	/**
	 * 多个参数拼接，拼接后获得新字符串 true为路径拼接false字符串拼接
	 * 
	 * @param isPath
	 * @param msgs
	 *            一个或是多个String
	 * @return String
	 */
	public final static String stringConcat(boolean isPath, StringBuilder stringBuilder, String... msgs) {
		String str = "";
		if (msgs != null) {
			for (String s : msgs) {
				stringBuilder.append(s);
				if (isPath) {
					stringBuilder.append(File.separator);
				}
			}
			str = stringBuilder.toString();
			if (str.lastIndexOf(File.separator) != -1) {
				str = str.substring(0, str.lastIndexOf(File.separator));
			}
			stringBuilder = null;
		}
		return str;
	}

	/**
	 * 文件重命名
	 * 
	 * @param path
	 *            文件目录
	 * @param oldname
	 *            原来的文件名
	 * @param newname
	 *            新文件名
	 * @return boolean
	 */
	public final static boolean renameFile(String path, String oldname, String newname) {
		// 新的文件名和以前文件名不同时,才有必要进行重命名
		if (!oldname.equals(newname)) {
			File oldfile = new File(path + "/" + oldname);
			File newfile = new File(path + "/" + newname);
			// 重命名文件不存在
			if (!oldfile.exists()) {
				return false;
			}
			// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
			if (newfile.exists()) {
				return false;
			} else {
				oldfile.renameTo(newfile);
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 生成UTF-8文件. 如果文件内容中没有中文内容，则生成的文件为ANSI编码格式； 如果文件内容中有中文内容，则生成的文件为UTF-8编码格式。
	 * 
	 * @param filePath
	 *            待生成的文件名（含完整路径）
	 * @param fileBody
	 *            文件内容（利用\r\n写入时换行，可拼接到文件内容中）
	 * @return boolean
	 * @throws IOException
	 */
	public final static boolean writeUTFFile(String filePath, String fileBody) throws IOException {
		FileUtils.createFile(filePath);
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			// true标识追加
			fos = new FileOutputStream(filePath, true);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(fileBody);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取文件内容到String
	 * 
	 * @param filePath
	 *            文件所在路径<eg:C:\\dir\\a.txt>
	 * @return String
	 * @throws IOException
	 */
	public final static String getFileContent(String filePath) throws IOException {
		File file = new File(filePath);
		String content = "";
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = reader.readLine()) != null) {
			content += (line + "\r\n");
		}
		if (reader != null) {
			reader.close();
		}
		return content;
	}

	/**
	 * DateToString
	 * 
	 * @param dateStr
	 *            日期(字符串)
	 * @param ref
	 *            指定日期格式(int类型)： 0.yyyy/MM/dd; 1.yyyy/MM/dd HH:mm; 2.yyyy/MM/dd
	 *            HH:mm:ss; 3.yyyy/MM/dd HH:mm:ss.SSS; 4.yyyy-MM-dd; 5.yyyy-MM-dd
	 *            HH:mm; 6.yyyy-MM-dd HH:mm:ss; 7.yyyy-MM-dd HH:mm:ss.SSS;
	 *            8.yyyy年MM月dd日; 9.yyyy年MM月dd日 hh时mm分; 10.yyyy年MM月dd日 hh时mm分ss秒 ;
	 * @return String
	 */
	public final static String formateDateToString(Date date, int ref) {
		String strRef = null;
		// 定义日期格式
		switch (ref) {
		case 0:
			strRef = "yyyy/MM/dd";
			break;
		case 1:
			strRef = "yyyy/MM/dd HH:mm";
			break;
		case 2:
			strRef = "yyyy/MM/dd HH:mm:ss";
			break;
		case 3:
			strRef = "yyyy/MM/dd HH:mm:ss.SSS";
			break;
		case 4:
			strRef = "yyyy-MM-dd";
			break;
		case 5:
			strRef = "yyyy-MM-dd HH:mm";
			break;
		case 6:
			strRef = "yyyy-MM-dd HH:mm:ss";
			break;
		case 7:
			strRef = "yyyy-MM-dd HH:mm:ss.SSS";
			break;
		case 8:
			strRef = "yyyy年MM月dd日";
			break;
		case 9:
			strRef = "yyyy年MM月dd日 hh时mm分";
			break;
		case 10:
			strRef = "yyyy年MM月dd日 hh时mm分ss秒 ";
			break;
		default:
			break;
		}
		DateFormat df = new SimpleDateFormat(strRef);
		return df.format(date);
	}

	public static void main(String[] args) throws IOException {
		// String filePath = "C://test/aaa.java";
		// String fileContent =
		// "#项目1\r\n#projectId=QPnBORn_MRt5Ewe9IUG29w\r\n#项目2\r\n#projectId=QPnBORn_MRt5Ewe9IUG29w\r\n#项目3\r\n#projectId=QPnBORn_MRt5Ewe9IUG29w";
		//
		// System.out.println(LonZhUtils.writeUTFFile(filePath, fileContent));

		removeDirFilesToDir("C:\\nas\\excel\\", "C:\\nas\\temp\\");
	}

}
