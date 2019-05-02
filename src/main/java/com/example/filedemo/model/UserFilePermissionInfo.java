package com.example.filedemo.model;

public class UserFilePermissionInfo {
	private long file_id;
	private long user_id;
	private long permission_id;
	private String username;
	public long getFile_id() {
		return file_id;
	}
	public void setFile_id(long file_id) {
		this.file_id = file_id;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public long getPermission_id() {
		return permission_id;
	}
	public void setPermission_id(long permission_id) {
		this.permission_id = permission_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
