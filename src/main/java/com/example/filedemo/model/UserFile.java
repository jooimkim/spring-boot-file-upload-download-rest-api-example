package com.example.filedemo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "userFile")
public class UserFile {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", nullable = false, updatable = false)
	private String id;
	
	@Column(name = "filename", nullable = false)
	private String filename;
	
	@Column(name = "uploadTime", nullable = false)
	private Date uploadTime;
	
	@PrePersist
	protected void onCreate() {
		uploadTime = new Date();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public Date getUploadTime() {
		return uploadTime;
	}
	
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	
	@Override
	public String toString() {
		return "UserFile{" +
				"id='" + id + '\'' +
				", filename='" + filename + '\'' +
				", uploadTime=" + uploadTime +
				'}';
	}
}
