package com.fileManagement.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import com.fileManagement.compare.FileSizeComparator;
import com.fileManagement.thread.GetFileInfo;
import com.fileManagement.tree.DirBean;
import com.fileManagement.tree.FileBean;
import com.fileManagement.tree.RootBean;

public class ThreadOfIterateDirService {
	/*
	 * 获取根目录信息
	 */
	public static List<RootBean> getDiskInfo() {
		// 获取盘符
		File[] files = File.listRoots();
		List<RootBean> roots = new ArrayList<RootBean>();
		for (File file : files) {
			if (file.getTotalSpace() != 0) {
				RootBean rootBean = new RootBean();
				rootBean.setDiskPath(file.getAbsolutePath());
				rootBean.setDiskName(file.getAbsolutePath().charAt(0) + "");
				rootBean.setDiskSize(FormetFileSize(file.getTotalSpace()));
				rootBean.setAvilableSize(FormetFileSize(file.getFreeSpace()));
				roots.add(rootBean);
			}
		}
		return roots;
	}

	public static boolean deletefile(String delpath) throws Exception {
		try {

			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile;
					if (System.getProperty("os.name").toLowerCase()
							.startsWith("win"))
						delfile = new File(delpath + "\\" + filelist[i]);
					else
						delfile = new File(delpath + "/" + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
						System.out
								.println(delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						if (System.getProperty("os.name").toLowerCase()
								.startsWith("win"))
							deletefile(delpath + "\\" + filelist[i]);
						else
							deletefile(delpath + "/" + filelist[i]);
					}
				}
				System.out.println(file.getAbsolutePath() + "删除成功");
				file.delete();
			}

		} catch (FileNotFoundException e) {
			System.out.println("deletefile() Exception:" + e.getMessage());
		}
		return true;
	}

	/*
	 * 通过递归得到某一路径下所有的目录及其文件
	 */
	public static DirBean getFiles(String dirPath) throws Exception {
		System.out.println("getFiles");
		File root = new File(dirPath);
		List<File> filePathsList = new ArrayList<File>();
		File[] filePaths = root.listFiles();
		for (File s : filePaths) {
			filePathsList.add(s);
		}
		DirBean dirBean = new DirBean();

		CountDownLatch count = new CountDownLatch(4);
		GetFileInfo mythread1 = new GetFileInfo(dirBean, filePathsList, count);
		mythread1.start();

		GetFileInfo mythread2 = new GetFileInfo(dirBean, filePathsList, count);
		mythread2.start();

		GetFileInfo mythread3 = new GetFileInfo(dirBean, filePathsList, count);
		mythread3.start();

		GetFileInfo mythread4 = new GetFileInfo(dirBean, filePathsList, count);
		mythread4.start();

		try {
			count.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return dirBean;
	}

	private static String getFileType(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
	}

	// 获取文件名
	static String getFileName(String filePath) {
		String[] fileItems;
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
			fileItems = filePath.split("\\\\");
		else
			fileItems = filePath.split("/");
		return fileItems[fileItems.length - 1];
	}

	// 取得文件大小
	public static long getFileSizes(File f) throws Exception {
		long s = 0;
		System.out.println("getFileSizes");
		if (f.exists() && f != null) {
			s = f.length();
		} else {
			System.out.println("文件不存在");
			s = 0;
		}
		return s;
	}

	// 取得文件夹大小
	public static long getFileSize(File f) throws Exception {

		long size = 0;

		File flist[] = f.listFiles();
		if (flist != null)
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					if (flist[i].getPath().contains("/dev/")
							|| f.getPath().contains("/sys/")
							|| f.getPath().contains("/proc/"))

					{
						System.out.println(flist[i].getPath());
						return 0;
					}
					size = size + getFileSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}
		System.out.println(size);
		return size;
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static long getlist(File f) {// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;

	}
}
