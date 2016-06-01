package com.fileManagement.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "FileManagementinfo")
@EntityListeners(AuditingEntityListener.class)
public class FileManagementinfo extends AbstractEntity {

	@Column(name = "FileFatherId")
	private int FileFatherId;

	@Column(name = "FileSize")
	private long Filesize;

	@Column(name = "FilePath")
	private String FilePath;

	@Column(name = "FileName")
	private String FileName;

	@Column(name = "FileProperty")
	private String FileProperty;

	public int getFileFatherId() {
		return FileFatherId;
	}

	public void setFileFatherId(int fileFatherId) {
		FileFatherId = fileFatherId;
	}

	public long getFilesize() {
		return Filesize;
	}

	public void setFilesize(long s) {
		Filesize = s;
	}

	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getFileProperty() {
		return FileProperty;
	}

	public void setFileProperty(String fileProperty) {
		FileProperty = fileProperty;
	}

}
