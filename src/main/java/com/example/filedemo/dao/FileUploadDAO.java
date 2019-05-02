package com.example.filedemo.dao;

import com.example.filedemo.model.UserFileInfo;

import java.util.List;

public interface FileUploadDAO {
	public void saveUserFile(UserFileInfo fileInfo);
	public void updateUserFile(UserFileInfo fileInfo);
	public void deleteUserFile(UserFileInfo fileInfo);
	public UserFileInfo getFileById(long id);
	public UserFileInfo getFileByFileName(String fileName);
	public UserFileInfo getFileByDocumentName(String fileName);
	public boolean isFileExist(String fileName);
	public boolean isDocumentNameExist(String docName);
	
	public boolean isUserAuthorizedToReadFile(long fileId, long userId);
	public boolean isUserAuthorizedToWriteFile(long fileId, long userId);
	public boolean isUserAuthorizedToDeleteFile(long fileId, long userId);
	
	public List<UserFileInfo> getAllPermittedFilesForUserId(long userId);
}
