package com.example.filedemo.repository;

import com.example.filedemo.entity.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by jooimkim on 2019. 4. 30..
 */
@Repository
public interface UserFileRepository extends JpaRepository<UserFile, String> {

    public UserFile save(UserFile userFile);

    public List<UserFile> findAll();

    @Override
    public boolean existsById(String id);

}
