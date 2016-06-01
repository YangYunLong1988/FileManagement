package com.fileManagement.compare;

import java.util.Comparator;

import com.fileManagement.service.Fileinfo;
import com.fileManagement.tree.FileBean;

public class FileInfoSizeComparator implements Comparator<Fileinfo> {

	@Override
	public int compare(Fileinfo o1, Fileinfo o2) {
		if (o1.getSize().equals(o2.getSize()))
			return 0;
		else
			return o1.getSize() > o2.getSize() ? -1 : 1;// 降序
	}
}
