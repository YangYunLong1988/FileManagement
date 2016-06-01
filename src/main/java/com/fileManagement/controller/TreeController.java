package com.fileManagement.controller;

import java.io.File;
import java.io.IOException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fileManagement.repository.FileNodeInfoDao;
import com.fileManagement.service.Fileinfo;
import com.fileManagement.service.IterateDirService;
import com.fileManagement.service.IterateDirServiceForLinux;
import com.fileManagement.service.ThreadOfIterateDirService;
import com.fileManagement.tree.DirBean;

@Controller("/")
@Transactional
public class TreeController {
	@Autowired
	IterateDirService iterateDirService;

	@Autowired
	IterateDirServiceForLinux iterateDirServiceForLinux;

	@Autowired
	FileNodeInfoDao fileNodeInfoDao;

	static String os = System.getProperty("os.name");

	@RequestMapping("/")
	public String index() {
		return "/index";
	}

	@RequestMapping("/GetSingleLevelNodes")
	@ResponseBody
	public Fileinfo[] GetSingleLevelNodes(String path) throws IOException {
		File file = new File(path);
		if ((path == "") || !(file.exists())) {
			System.out.println("目录可能为空或者不存在");
			if (os.toLowerCase().startsWith("win")) {
				path = "C:\\";
				;
			} else
				path = "/";

		}

		Fileinfo fileinfo = new Fileinfo(path);
		return fileinfo.readSingleLevelfiles(path, fileNodeInfoDao);
	}

	@RequestMapping("/asncReadSingleLevelfiles")
	@ResponseBody
	public Fileinfo[] asncReadSingleLevelfiles(String path) throws IOException {
		Fileinfo fileinfo = new Fileinfo(path);
		return fileinfo.asncReadSingleLevelfiles(path, fileNodeInfoDao);
	}

	@RequestMapping("/IterateDirChildren")
	@ResponseBody
	public DirBean IterateDirChildren(String path) throws Exception {
		System.out.println("IterateDirService getFileName");
		DirBean dirBean = new DirBean();
		if (path == null)
			return null;
		if (os.toLowerCase().startsWith("win"))
			dirBean = iterateDirService.getFiles(path);
		else
			dirBean = iterateDirServiceForLinux.getFiles(path);
		return dirBean;
	}

	// Only For thread study
	@RequestMapping("/ThreadIterateDirChildren")
	@ResponseBody
	public DirBean ThreadIterateDirChildren(String path) throws Exception {
		System.out.println("ThreadIterateDirChildren getFileName");
		if (path == null)
			return null;
		ThreadOfIterateDirService threadOfIterateDirService = new ThreadOfIterateDirService();
		DirBean dirBean = threadOfIterateDirService.getFiles(path);
		return dirBean;
	}

	@RequestMapping("/delTreeNode")
	@ResponseBody
	public boolean delTreeNode(String path) throws Exception {
		boolean executionResult = iterateDirService.deletefile(path);
		return executionResult;
	}
}
