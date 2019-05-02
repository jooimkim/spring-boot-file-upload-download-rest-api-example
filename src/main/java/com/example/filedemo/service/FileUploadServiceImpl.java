package com.example.filedemo.service;


import com.example.filedemo.dao.FileUploadDAO;
import com.example.filedemo.model.UserFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileUploadServiceImpl implements FileUploadService {
	@Autowired
	private FileUploadDAO fileUploadDao;
	public void saveUserFile(UserFileInfo fileInfo) {
		fileUploadDao.saveUserFile(fileInfo);
	}
	
	public void updateUserFile(UserFileInfo fileInfo) {
		fileUploadDao.updateUserFile(fileInfo);
	}
	
	public void deleteUserFile(UserFileInfo fileInfo) {
		fileUploadDao.deleteUserFile(fileInfo);
	}
	
	public UserFileInfo getFileById(long id) {
		return fileUploadDao.getFileById(id);
	}
	
	public UserFileInfo getFileByFileName(String fileName) {
		return fileUploadDao.getFileByFileName(fileName);
	}
	
	public UserFileInfo getFileByDocumentName(String documentName) {
		return fileUploadDao.getFileByDocumentName(documentName);
	}
	public boolean isFileExist(String fileName){
		return fileUploadDao.isFileExist(fileName);
	}
	public boolean isDocumentNameExist(String docName){
		return fileUploadDao.isDocumentNameExist(docName);
	}
	
	public List<UserFileInfo> getAllPermittedFilesForUserId(long userId){
		return fileUploadDao.getAllPermittedFilesForUserId(userId);
	}
	
	public boolean isUserAuthorizedToReadFile(long fileId, long userId){
		return fileUploadDao.isUserAuthorizedToReadFile(fileId, userId);
	}
	public boolean isUserAuthorizedToWriteFile(long fileId, long userId){
		return fileUploadDao.isUserAuthorizedToWriteFile(fileId, userId);
	}
	public boolean isUserAuthorizedToDeleteFile(long fileId, long userId){
		return fileUploadDao.isUserAuthorizedToDeleteFile(fileId, userId);
	}
	
	
}

