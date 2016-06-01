package com.fileManagement.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fileManagement.thread.SaveFileToDb;
import com.fileManagement.Model.FileManagementinfo;
import com.fileManagement.compare.FileSizeComparator;
import com.fileManagement.repository.FileNodeInfoDao;
import com.fileManagement.tree.DirBean;
import com.fileManagement.tree.FileBean;
import com.fileManagement.tree.RootBean;

/**
 * @author Yangyunlong
 *
 */
@Service
@Transactional
public class IterateDirService {
	@Autowired
	private FileNodeInfoDao fileNodeInfoDao;

	private List<String> filepath = new ArrayList<String>();

	private Logger log = LoggerFactory.getLogger(getClass());

	public List<RootBean> getDiskInfo() {
		// get disk name
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

	public boolean deletefile(String delpath) throws Exception {

		fileNodeInfoDao.deleteAll();
		try {
			File file = new File(delpath);
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile;
					if (System.getProperty("os.name").toLowerCase().startsWith("win"))
						delfile = new File(delpath + "\\" + filelist[i]);
					else
						delfile = new File(delpath + "/" + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
						log.debug(delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						if (System.getProperty("os.name").toLowerCase().startsWith("win"))
							deletefile(delpath + "\\" + filelist[i]);
						else
							deletefile(delpath + "/" + filelist[i]);
					}
				}
				log.debug(file.getAbsolutePath() + "删除成功");
				file.delete();
			}

		} catch (FileNotFoundException e) {
			log.debug("deletefile() Exception:" + e.getMessage());
		}
		return true;
	}

	/*
	 * Get all the directories and files in a dirPath recursively.
	 */
	public DirBean getFiles(String dirPath) throws Exception {
		long foldersize;
		long StartTime = System.currentTimeMillis();
		File root = new File(dirPath);
		DirBean dirBean = new DirBean();

		if (root.exists()) {
			String dirSize = "";
			int dirCount = 0;
			List<FileBean> filelist = new LinkedList<FileBean>();
			if (root.isDirectory()) {
				File[] files = root.listFiles();
				if (files != null)
					for (File file : files) {
						FileBean fileBean = new FileBean();
						String realPath = file.getAbsolutePath();
						fileBean.setFilePath(realPath);
						fileBean.setFileName(getFileName(realPath));
						if (file.isDirectory()) {
							long sizefromdb = 0;
							List<FileManagementinfo> all = fileNodeInfoDao.findByFilePath(file.getAbsolutePath());
							if (all.isEmpty()) {

							} else {
								for (FileManagementinfo TmpFileManagementinfo : all) {
									sizefromdb = TmpFileManagementinfo.getFilesize();// 重复记录使用一次
									break;
								}
							}
							fileBean.setFileType("DIR");

							if (all.isEmpty()) {// No folder find in DB,get the
												// size by file system
								log.debug("即将计算Size的文件夹路径：" + file.getAbsolutePath());
								fileBean.setFileSize(FormetFileSize(foldersize = getFileSize(file)).toString());
								FileManagementinfo fileManagementinfo = new FileManagementinfo();
								fileManagementinfo.setFilePath(file.getAbsolutePath());
								fileManagementinfo.setFileProperty("Directory");
								fileManagementinfo.setFilesize(foldersize);
								fileBean.setFileSizeCompare(foldersize);
								SaveFileToDb saveFileToDbThread = new SaveFileToDb(fileNodeInfoDao, fileManagementinfo);
								saveFileToDbThread.start();
							} else {
								fileBean.setFileSize(FormetFileSize(sizefromdb).toString());
								fileBean.setFileSizeCompare(sizefromdb);
							}
						} else {
							fileBean.setFileType(getFileType(getFileName(realPath)));
							fileBean.setFileSize(FormetFileSize(getFileSizes(file)));

							fileBean.setFileSizeCompare(getFileSizes(file));
						}
						filelist.add(fileBean);
					}
			} else {
				dirSize = FormetFileSize(getFileSizes(root));
			}

			FileSizeComparator fileSizeComparator = new FileSizeComparator();
			Collections.sort(filelist, fileSizeComparator);

			dirBean.setDirCount(dirCount);
			dirBean.setDirSize(dirSize);
			dirBean.setDirPath(dirPath);
			dirBean.setFiles(filelist);
		} else {
			log.debug("文件或文件目录不存在");
		}
		log.debug("列出文件夹" + root.getName() + "里面所有的文件所需要的时间" + (System.currentTimeMillis() - StartTime) + "");
		return dirBean;
	}

	public String getFileType(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
	}

	// 获取文件名
	public String getFileName(String filePath) {
		String[] fileItems;
		if (System.getProperty("os.name").toLowerCase().startsWith("win"))
			fileItems = filePath.split("\\\\");
		else
			fileItems = filePath.split("/");
		return fileItems[fileItems.length - 1];
	}

	// 取得文件大小
	public long getFileSizes(File f) throws Exception {

		long s = 0;
		log.debug("获取单个文件的大小...");
		if (f.exists() && f != null) {
			s = f.length();
		} else {
			log.debug("文件不存在");
			s = 0;
		}
		return s;
	}

	// 取得文件夹大小
	public long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		if (flist != null)
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					if (isSymlink(flist[i])) {
						log.debug("跳过的链接文件夹：" + flist[i].getAbsolutePath());
						continue;
					}
					size = size + getFileSize(flist[i]);

				} else {
					size = size + flist[i].length();
				}
			}
		// log.debug("文件夹大小：" + size);
		// log.debug("文件夹路径：" + f.getAbsolutePath());
		if (f.isDirectory() && (!isSymlink(f))) {
			FileManagementinfo fileManagementinfo = new FileManagementinfo();
			fileManagementinfo.setFilePath(f.getAbsolutePath());
			fileManagementinfo.setFileProperty("Directory");
			fileManagementinfo.setFilesize(size);
			SaveFileToDb saveFileToDbThread = new SaveFileToDb(fileNodeInfoDao, fileManagementinfo);
			saveFileToDbThread.start();
		}
		if (isSymlink(f))
			size = 0;
		return size;
	}

	public String FormetFileSize(long fileS) {// 转换文件大小
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

	public long getlist(File f) {// 递归求取目录文件个数
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

	// 查找数据库对应的文件夹里面所有的文件大小，并且相加得到文件夹的大小
	public long Getfilesizefromdb() {
		long size = 0;
		for (String tmpfilepath : filepath) {
			List<FileManagementinfo> pathfromDB = fileNodeInfoDao.findByFilePath(tmpfilepath);
			for (FileManagementinfo tmppathfromDB : pathfromDB) {
				size += tmppathfromDB.getFilesize();
			}

		}

		return size;
	}

	// List all the files in the folder
	public void GetfilepathfromDir(File f) {

		File[] files = f.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				GetfilepathfromDir(file);
			} else {
				filepath.add(file.getAbsolutePath());
			}
		}

	}

	public boolean isSymlink(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("File must not be null");
		File canon;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File canonDir = file.getParentFile().getCanonicalFile();
			canon = new File(canonDir, file.getName());
		}
		return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}

}
