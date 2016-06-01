package com.fileManagement.tree;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RootBean implements Serializable {
	private String diskPath;
	private String diskName;
	private String diskSize;
	private String avilableSize;

	public RootBean(String diskPath, String diskName, String diskSize,
			String avilableSize) {
		super();
		this.diskPath = diskPath;
		this.diskName = diskName;
		this.diskSize = diskSize;
		this.avilableSize = avilableSize;
	}

	public RootBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getDiskPath() {
		return diskPath;
	}

	public void setDiskPath(String diskPath) {
		this.diskPath = diskPath;
	}

	public String getDiskName() {
		return diskName;
	}

	public void setDiskName(String diskName) {
		this.diskName = diskName;
	}

	public String getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(String diskSize) {
		this.diskSize = diskSize;
	}

	public String getAvilableSize() {
		return avilableSize;
	}

	public void setAvilableSize(String avilableSize) {
		this.avilableSize = avilableSize;
	}

}
