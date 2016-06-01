package com.fileManagement.tree;

import java.io.Serializable;
import java.util.Comparator;

@SuppressWarnings("serial")
public class FileBean implements Serializable {
	private String filePath;
	private String fileName;
	private String fileSize;
	private Long fileSizeCompare;
	private String fileType;

	public FileBean() {
	}

	public FileBean(String filePath, String fileName, String fileSize,
			String fileType, long fileSizeCompare) {
		super();
		this.filePath = filePath;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileSizeCompare = fileSizeCompare;
		this.fileType = fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public Long getFileSizeCompare() {
		return fileSizeCompare;
	}

	public void setFileSizeCompare(long fileSizeCompare) {
		this.fileSizeCompare = fileSizeCompare;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
