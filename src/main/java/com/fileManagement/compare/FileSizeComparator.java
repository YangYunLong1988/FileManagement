package com.fileManagement.compare;

import java.util.Comparator;
import com.fileManagement.tree.FileBean;

public class FileSizeComparator implements Comparator<FileBean> {

	@Override
	public int compare(FileBean o1, FileBean o2) {
		if (o1.getFileSizeCompare().equals(o2.getFileSizeCompare()))
			return 0;
		else
			return o1.getFileSizeCompare() > o2.getFileSizeCompare() ? -1 : 1;// 降序
	}
}
