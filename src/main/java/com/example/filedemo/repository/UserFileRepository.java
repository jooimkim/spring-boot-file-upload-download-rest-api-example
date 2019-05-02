package com.example.filedemo.repository;

import com.example.filedemo.model.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFileRepository extends JpaRepository<UserFile, String> {

	public UserFile save(UserFile userFile);
	
	public List<UserFile> findAll();
	
//	public boolean exists(String id);
	
}
