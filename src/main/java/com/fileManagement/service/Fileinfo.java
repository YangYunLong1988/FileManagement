package com.fileManagement.service;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fileManagement.Model.FileManagementinfo;
import com.fileManagement.compare.FileInfoSizeComparator;
import com.fileManagement.repository.FileNodeInfoDao;

@Transactional
public class Fileinfo extends File {
	private int id;
	private int fid;
	private Long size = (long) 0;
	private boolean isParent = false;
	private Logger log = LoggerFactory.getLogger(getClass());

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFid() {
		return fid;
	}

	public void setFid(int fid) {
		this.fid = fid;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public Fileinfo(String pathname) {
		super(pathname);
	}

	//
	public Fileinfo[] readfiles(String path) {
		long a = System.currentTimeMillis();

		LinkedList<Fileinfo> list = new LinkedList<Fileinfo>();
		LinkedList<Fileinfo> listForResponse = new LinkedList<Fileinfo>();
		Fileinfo dir = new Fileinfo(path);
		dir.setFid(0);
		dir.setId(1);
		int index = 1;
		File file[] = dir.listFiles();
		for (int i = 0; i < file.length; i++) {
			Fileinfo tem = new Fileinfo(file[i].getAbsolutePath());
			tem.setId(++index);
			tem.setFid(dir.getId());
			log.debug("id: " + tem.getId() + " fid:" + tem.getFid() + " " + tem.getAbsolutePath());
			if (file[i].isDirectory()) {
				list.add(tem);
				listForResponse.add(tem);
			} else {

			}
		}

		Fileinfo tmp;
		while (!list.isEmpty()) {
			tmp = (Fileinfo) list.removeFirst();
			if (tmp.isDirectory()) {
				file = tmp.listFiles();
				if (file == null) {
					continue;
				}
				for (int i = 0; i < file.length; i++) {
					Fileinfo tem = new Fileinfo(file[i].getAbsolutePath());
					tem.setFid(tmp.getId());
					tem.setId(++index);
					listForResponse.add(tem);
					if (file[i].isDirectory()) {
						list.add(tem);

					}

					else {

					}
					log.debug("id: " + tem.getId() + " fid:" + tem.getFid() + " " + tem.getAbsolutePath());
				}
			} else {
				System.out.println(tmp.getAbsolutePath());
			}
		}

		log.debug(System.currentTimeMillis() - a + "");

		return listForResponse.toArray(new Fileinfo[listForResponse.size()]);

	}

	// 第一次单步加载
	public Fileinfo[] readSingleLevelfiles(String path, FileNodeInfoDao fileNodeInfoDao) {
		long a = System.currentTimeMillis();
		int index = 0;
		LinkedList<Fileinfo> list = new LinkedList<Fileinfo>();
		LinkedList<Fileinfo> listForResponse = new LinkedList<Fileinfo>();
		Fileinfo dir = new Fileinfo(path);
		dir.setFid(0);
		dir.setId(index++);

		if (dir.isDirectory())
			dir.setIsParent(true);

		File file[] = dir.listFiles();
		if (file != null)
			for (int i = 0; i < file.length; i++) {
				Fileinfo tem = new Fileinfo(file[i].getAbsolutePath());
				tem.setId(index++);
				tem.setFid(dir.getId());
				log.debug("id: " + tem.getId() + " fid:" + tem.getFid() + " " + tem.getAbsolutePath());
				if (file[i].isDirectory()) {
					tem.setIsParent(true);
					long sizefromdb = 0;
					List<FileManagementinfo> all = fileNodeInfoDao.findByFilePath(file[i].getAbsolutePath());
					if (all.isEmpty()) {

					} else {
						for (FileManagementinfo TmpFileManagementinfo : all) {
							sizefromdb = TmpFileManagementinfo.getFilesize();// 重复记录使用一次
							tem.setSize(sizefromdb);
							// log.debug("通过数据库得到文件夹的，查找的路径是：" + p.toString() +
							// "	Size:" + sizefromdb);
							break;
						}
					}
					list.add(tem);
					listForResponse.add(tem);
				} else {
					tem.setSize(file[i].length());
					list.add(tem);
					listForResponse.add(tem);

				}
			}

		listForResponse.add(dir);
		FileInfoSizeComparator fileInfoSizeComparator = new FileInfoSizeComparator();
		Collections.sort(listForResponse, fileInfoSizeComparator);
		log.debug(System.currentTimeMillis() - a + "");
		Fileinfo[] arr = listForResponse.toArray(new Fileinfo[listForResponse.size()]);
		return arr;

	}

	// 异步加载
	public Fileinfo[] asncReadSingleLevelfiles(String path, FileNodeInfoDao fileNodeInfoDao) {
		long a = System.currentTimeMillis();
		int index = 1;
		LinkedList<Fileinfo> list = new LinkedList<Fileinfo>();
		LinkedList<Fileinfo> listForResponse = new LinkedList<Fileinfo>();
		Fileinfo dir = new Fileinfo(path);

		if (dir.isDirectory())
			dir.setIsParent(true);

		File file[] = dir.listFiles();
		if (file != null)
			for (int i = 0; i < file.length; i++) {
				Fileinfo tem = new Fileinfo(file[i].getAbsolutePath());
				tem.setId(index++);
				log.debug("id: " + tem.getId() + " fid:" + tem.getFid() + " " + tem.getAbsolutePath());
				if (file[i].isDirectory()) {
					tem.setIsParent(true);
					long sizefromdb = 0;
					List<FileManagementinfo> all = fileNodeInfoDao.findByFilePath(file[i].getAbsolutePath());
					if (all.isEmpty()) {

					} else {
						for (FileManagementinfo TmpFileManagementinfo : all) {
							sizefromdb = TmpFileManagementinfo.getFilesize();// 重复记录使用一次
							tem.setSize(sizefromdb);
							// log.debug("通过数据库得到文件夹的，查找的路径是：" + p.toString() +
							// "	Size:" + sizefromdb);
							break;
						}
					}
					list.add(tem);
					listForResponse.add(tem);
				} else {
					tem.setSize(file[i].length());
					list.add(tem);
					listForResponse.add(tem);

				}
			}
		FileInfoSizeComparator fileInfoSizeComparator = new FileInfoSizeComparator();
		Collections.sort(listForResponse, fileInfoSizeComparator);
		log.debug(System.currentTimeMillis() - a + "");
		Fileinfo[] arr = listForResponse.toArray(new Fileinfo[listForResponse.size()]);
		return arr;

	}

}
