package com.fileManagement.thread;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fileManagement.Model.FileManagementinfo;
import com.fileManagement.repository.FileNodeInfoDao;

@Transactional
public class SaveFileToDb  {

	private FileNodeInfoDao fileNodeInfoDao;

	private FileManagementinfo fileManagementinfo;

	public SaveFileToDb(FileNodeInfoDao fileNodeInfoDao, FileManagementinfo fileManagementinfo) {
		super();
		this.fileNodeInfoDao = fileNodeInfoDao;
		this.fileManagementinfo = fileManagementinfo;
	}


	public void start() {

		// List<FileManagementinfo> all = fileNodeInfoDao
		// .findByFilePath(fileManagementinfo.getFilePath());
		// if (all.isEmpty())
		fileNodeInfoDao.save(fileManagementinfo);
	}
}