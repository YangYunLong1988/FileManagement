package com.fileManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.fileManagement.Model.FileManagementinfo;


public interface FileNodeInfoDao extends CrudRepository<FileManagementinfo, Long> {
	@Query("from FileManagementinfo t where t.FilePath=?1 order by created_date desc")
	List<FileManagementinfo> findByFilePath(String FilePath);
}
