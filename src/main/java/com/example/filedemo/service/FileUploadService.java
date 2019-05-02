package com.example.filedemo.service;

import com.example.filedemo.model.UserFileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FileUploadService {
	public void saveUserFile(UserFileInfo fileInfo);
	public void updateUserFile(UserFileInfo fileInfo);
	public void deleteUserFile(UserFileInfo fileInfo);
	public UserFileInfo getFileById(long id);
	public UserFileInfo getFileByFileName(String fileName);
	public UserFileInfo getFileByDocumentName(String documentName);
	public boolean isFileExist(String fileName);
	public boolean isDocumentNameExist(String docName);
	
	public boolean isUserAuthorizedToReadFile(long fileId, long userId);
	public boolean isUserAuthorizedToWriteFile(long fileId, long userId);
	public boolean isUserAuthorizedToDeleteFile(long fileId, long userId);
	
	public List<UserFileInfo> getAllPermittedFilesForUserId(long userId);
}
