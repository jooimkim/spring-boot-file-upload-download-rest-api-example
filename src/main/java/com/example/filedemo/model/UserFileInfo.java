package com.example.filedemo.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="file_info")
public class UserFileInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="file_name", unique = true, nullable = false, length = 30)
	private String fileName;
	private String description;
	@Column(name="document_name", unique = true, nullable = false, length = 30)
	private String documentName;
	@Column(name="file_owner", nullable = false, length = 30)
	private long fileOwner;
	@Column(name="encryption_key", length=30, nullable=false, updatable=false)
	private String encryptionKey;
	
	private int file_revision;
	@Transient
	private String FileOwnerName;
	@Transient
	private Set<Long> readPermissionUsers = new HashSet<Long>(); //contains the user id
	@Transient
	private Set<Long> writePermissionUsers = new HashSet<Long>();
	@Transient
	private Set<Long> readWritePermissionUsers = new HashSet<Long>();
	
	@Transient
	private List<UserFilePermissionInfo> usrFilePermInfo=new ArrayList<UserFilePermissionInfo>();
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public long getFileOwner() {
		return fileOwner;
	}
	public void setFileOwner(long fileOwner) {
		this.fileOwner = fileOwner;
	}
	public String getFileOwnerName() {
		return FileOwnerName;
	}
	public void setFileOwnerName(String fileOwnerName) {
		FileOwnerName = fileOwnerName;
	}
	public int getFile_revision() {
		return file_revision;
	}
	public void setFile_revision(int file_revision) {
		this.file_revision = file_revision;
	}
	public Set<Long> getReadPermissionUsers() {
		return readPermissionUsers;
	}
	public void setReadPermissionUsers(Set<Long> readPermissionUsers) {
		this.readPermissionUsers = readPermissionUsers;
	}
	public Set<Long> getWritePermissionUsers() {
		return writePermissionUsers;
	}
	public void setWritePermissionUsers(Set<Long> writePermissionUsers) {
		this.writePermissionUsers = writePermissionUsers;
	}
	public Set<Long> getReadWritePermissionUsers() {
		return readWritePermissionUsers;
	}
	public void setReadWritePermissionUsers(Set<Long> readWritePermissionUsers) {
		this.readWritePermissionUsers = readWritePermissionUsers;
	}
	public List<UserFilePermissionInfo> getUsrFilePermInfo() {
		return usrFilePermInfo;
	}
	public void setUsrFilePermInfo(List<UserFilePermissionInfo> usrFilePermInfo) {
		this.usrFilePermInfo = usrFilePermInfo;
	}
	public String toString(){
		return "id "+id+ " fileName "+fileName+" documentName "+documentName+" fileOwner "+ fileOwner+" description "+description;
	}
	
	/**
	 * @return the encryptionKey
	 */
	public String getEncryptionKey() {
		return encryptionKey;
	}
	
	/**
	 * @param encryptionKey the encryptionKey to set
	 */
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	
	
	
	
}
