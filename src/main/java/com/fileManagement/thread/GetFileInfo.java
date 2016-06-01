package com.fileManagement.thread;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.fileManagement.tree.DirBean;
import com.fileManagement.tree.FileBean;

public class GetFileInfo extends Thread {
	   private CountDownLatch count;
	List<File> filePathsList;
	List<FileBean> filelist = new LinkedList<FileBean>();
	int index = 0;
	DirBean dirBean;
	
	public DirBean getDirBean() {
		return dirBean;
	}

	public void setDirBean(DirBean dirBean) {
		this.dirBean = dirBean;
	}

	public GetFileInfo(DirBean dirBean1 ,List filePathsList1,CountDownLatch count1) {
		dirBean=dirBean1;
		this.filePathsList=filePathsList1;
		this.count=count1;
	}

	// private void getFileList(File f) {
	// File[] filePaths = f.listFiles();
	// for (File s : filePaths) {
	// filePathsList.add(s);
	// }
	// }

	@Override
	public void run() {
		File file = null;
		String dirSize = "";
		int dirCount = 0;
		
		while (index < filePathsList.size()) {
			synchronized (this) {
				if (index >= filePathsList.size()) {
					continue;
				}
				file = filePathsList.get(index);
				index++;
			}
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}

			try {
				
					FileBean fileBean = new FileBean();
					String realPath = file.getAbsolutePath();
					fileBean.setFilePath(realPath);
					fileBean.setFileName(getFileName(realPath));
					if (file.isDirectory()) {

						fileBean.setFileType("DIR");
						fileBean.setFileSize(FormetFileSize(
								getFileSize(file)).toString());

						fileBean.setFileSizeCompare(getFileSize(file));

					} else {
						fileBean.setFileType(getFileType(getFileName(realPath)));
						fileBean.setFileSize(FormetFileSize(getFileSizes(file)));

						fileBean.setFileSizeCompare(getFileSizes(file));
					}
					filelist.add(fileBean);
					
					dirBean.setDirCount(dirCount);
					dirBean.setDirSize(dirSize);
					dirBean.setDirPath(file.getAbsolutePath());
					dirBean.setFiles(filelist);
	
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		 this.count.countDown(); 
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

}
