package com.example.filedemo.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.filedemo.model.UserFileInfo;
import com.example.filedemo.model.UserFilePermissionInfo;
import com.example.filedemo.util.AppConstants;
import com.example.filedemo.util.ApplicationException;
import org.apache.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FileUploadDAOImpl implements FileUploadDAO {
	private static final Logger logger = Logger.getLogger(FileUploadDAOImpl.class);
	@Autowired
	private SessionFactory sessionFactory;
	
	public void saveUserFile(UserFileInfo fileInfo) {
		// Session session = HibernateUtil.getCurruntSession();
		Session session = null;
		Transaction tx = null;
		long id = 0;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			id = (Long) session.save(fileInfo);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at saving file information ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		processFilePermissionData(fileInfo, id);
	}
	
	public void updateUserFile(UserFileInfo fileInfo) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			session.update(fileInfo);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at updating file information ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		deleteFilePermissionsByFileId(fileInfo.getId());
		processFilePermissionData(fileInfo, fileInfo.getId());
	}
	
	public void deleteUserFile(UserFileInfo fileInfo) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			session.delete(fileInfo);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at deleting file information ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
	}
	
	public UserFileInfo getFileById(long id) {
		Session session = null;
		Transaction tx = null;
		UserFileInfo userFileInfo = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			userFileInfo = (UserFileInfo) session.get(UserFileInfo.class, id);
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at getFileById function ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		return getFilePermissionInfo(userFileInfo);
	}
	
	public UserFileInfo getFileByFileName(String fileName) {
		Session session = null;
		Transaction tx = null;
		UserFileInfo userFileInfo = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Criteria crit = session.createCriteria(UserFileInfo.class);
			Criterion file_name = Restrictions.eq("fileName", fileName);
			crit.add(file_name);
			crit.setMaxResults(1);
			userFileInfo = (UserFileInfo) crit.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at getFileByName ", e);
			if (tx != null) {
				tx.rollback();
				e.printStackTrace();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		return userFileInfo;
	}
	
	public UserFileInfo getFileByDocumentName(String documentName) {
		Session session = null;
		Transaction tx = null;
		UserFileInfo userFileInfo = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Criteria crit = session.createCriteria(UserFileInfo.class);
			Criterion document_name = Restrictions.eq("documentName",
					documentName);
			crit.add(document_name);
			crit.setMaxResults(1);
			userFileInfo = (UserFileInfo) crit.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at getFileByDocumentName ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		return userFileInfo;
	}
	
	/**
	 * Iterate over set of permissions
	 *
	 * @param userIds
	 * @param fileId
	 * @param permissionId
	 */
	private void processFilePermissionData(UserFileInfo fileInfo, long fileId) {
		if (fileInfo.getReadPermissionUsers().size() > 0) {
			for (long user_id : fileInfo.getReadPermissionUsers()) {
				saveFilePermissionInfo(user_id, fileId,
						AppConstants.READ_PERMISSION);
			}
		}
		if (fileInfo.getWritePermissionUsers().size() > 0) {
			for (long user_id : fileInfo.getWritePermissionUsers()) {
				saveFilePermissionInfo(user_id, fileId,
						AppConstants.WRITE_PERMISSION);
			}
		}
		if (fileInfo.getReadWritePermissionUsers().size() > 0) {
			for (long user_id : fileInfo.getReadWritePermissionUsers()) {
				saveFilePermissionInfo(user_id, fileId,
						AppConstants.READ_WRITE_PERMISSION);
			}
		}
	}
	
	/**
	 * save all permissions to database
	 *
	 * @param userId
	 * @param fileId
	 * @param permissionId
	 */
	private void saveFilePermissionInfo(long userId, long fileId,
										int permissionId) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("insert into file_user_permission (file_id, user_id, permission_id) values(?,?,?)");
			query.setLong(0, fileId);
			query.setLong(1, userId);
			query.setInteger(2, permissionId);
			int rowsCopied = query.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at saveFilePermisiion ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		// session.;
		
	}
	
	/**
	 * check file exist with file name
	 *
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		// Session session = HibernateUtil.getCurruntSession();
		Session session = null;
		Transaction tx = null;
		long count = Long.valueOf(0);
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createQuery("select count(*) from UserFileInfo file where file.fileName=:file_name");
			query.setString("file_name", fileName);
			count = (Long) query.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at function isFileExist ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		if (count > 0)
			return true;
		return false;
	}
	
	/**
	 * check file exist with document name
	 *
	 * @param fileName
	 * @return
	 */
	public boolean isDocumentNameExist(String docName) {
		Session session = null;
		Transaction tx = null;
		long count = Long.valueOf(0);
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createQuery("select count(*) from UserFileInfo file where file.documentName=:document_name");
			query.setString("document_name", docName);
			count = (Long) query.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at is DocumentNameExist ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		if (count > 0)
			return true;
		return false;
	}
	
	/**
	 * get file list for login user
	 */
	@SuppressWarnings("unchecked")
	public List<UserFileInfo> getAllFilesByUser(String username) {
		Session session = null;
		Transaction tx = null;
		List<UserFileInfo> list = new ArrayList<UserFileInfo>();
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			/*
			 * Criteria crit = session.createCriteria(UserFileInfo.class);
			 * Criterion owner = Restrictions.eq("file_owner",username);
			 * crit.add(owner);
			 */
			Query query = session
					.createQuery("from UserFileInfo where file_owner=?");
			query.setString(0, username);
			list = query.list();
			// List<UserFileInfo> list = (List<UserFileInfo>)crit.list();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at getAllFilesByUser function ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserFileInfo> getAllPermittedFilesForUserId(long userId) {
		List<UserFileInfo> listOfUserFile = new ArrayList<UserFileInfo>();
		List<UserFileInfo> listOfFiles = new ArrayList<UserFileInfo>();
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("select distinct fi.file_name, fi.id, fi.document_name,fi.file_owner, fi.description, ui.user_name  "
							+ "from file_info fi, file_user_permission fp, user_info ui where fi.id=fp.file_id and ui.id=fi.file_owner and fp.user_id=:userId");
			query.setLong("userId", userId);
			// List<UserFileInfo> listOfUserFile
			// =(List<UserFileInfo>)query.list();
			List<Object[]> rows = query.list();
			tx.commit();
			for (Object[] row : rows) {
				UserFileInfo fileInfo = new UserFileInfo();
				fileInfo.setId(Long.parseLong(row[1].toString()));
				fileInfo.setFileName(row[0].toString());
				fileInfo.setDocumentName(row[2].toString());
				fileInfo.setFileOwner(Long.parseLong(row[3].toString()));
				fileInfo.setDescription(row[4].toString());
				fileInfo.setFileOwnerName(row[5].toString());
				listOfUserFile.add(fileInfo);
			}
			for (UserFileInfo userFileInfo : listOfUserFile) {
				listOfFiles.add(getFilePermissionInfo(userFileInfo));
			}
		} catch (Exception e) {
			logger.error("Error at getAllPermittedFilesForUserId ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		return listOfFiles;
	}
	
	private UserFileInfo getFilePermissionInfo(UserFileInfo userFileInfo) {
		Set<Long> userIdWithReadPermission = new HashSet<Long>();
		Set<Long> userIdWithWritePermission = new HashSet<Long>();
		Set<Long> userIdWithReadWritePermission = new HashSet<Long>();
		List<UserFilePermissionInfo> listFilePermission = new ArrayList<UserFilePermissionInfo>();
		long fileId = userFileInfo.getId();
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("select ui.user_name, fup.user_id, fup.permission_id from file_user_permission fup, "
							+ "user_info ui where fup.user_id=ui.id and fup.file_id=:file_id");
			query.setLong("file_id", fileId);
			List<Object[]> rows = query.list();
			tx.commit();
			for (Object[] row : rows) {
				UserFilePermissionInfo filePermissionInfo = new UserFilePermissionInfo();
				// UserFileInfo userFileInfo = new UserFileInfo();
				// userFileInfo.setId(Long.parseLong(row[0].toString()));
				long user_id = Long.parseLong(row[1].toString());
				long permission_id = Long.parseLong(row[2].toString());
				
				filePermissionInfo.setFile_id(fileId);
				filePermissionInfo.setPermission_id(permission_id);
				filePermissionInfo.setUser_id(user_id);
				filePermissionInfo.setUsername(row[0].toString());
				listFilePermission.add(filePermissionInfo);
				
				if (AppConstants.READ_PERMISSION == permission_id) {
					userIdWithReadPermission.add(user_id);
				} else if (AppConstants.WRITE_PERMISSION == permission_id) {
					userIdWithWritePermission.add(user_id);
				} else {
					userIdWithReadWritePermission.add(user_id);
				}
			}
			userFileInfo.setReadPermissionUsers(userIdWithReadPermission);
			userFileInfo.setWritePermissionUsers(userIdWithWritePermission);
			userFileInfo
					.setReadWritePermissionUsers(userIdWithReadWritePermission);
			userFileInfo.setUsrFilePermInfo(listFilePermission);
		} catch (Exception e) {
			logger.error("Error at getFilePermissionInfo function ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		return userFileInfo;
	}
	
	/**
	 * get all permissions from database for particular file
	 *
	 * @param userId
	 */
	/*
	 * public Set<Long> getFilePermissionsByFileId(long fileId) { Session
	 * session = sessionFactory.getCurrentSession(); Transaction tx =
	 * session.beginTransaction(); Query query = session.createSQLQuery(
	 * "select into file_user_permission (file_id, user_id, permission_id) values(?,?,?)"
	 * ); query.setLong(0, fileId ); int rowsCopied=query.executeUpdate();
	 * //session.; tx.commit(); }
	 */
	
	/**
	 * Check user is authorized to read or view the uploaded file
	 */
	public boolean isUserAuthorizedToReadFile(long fileId, long userId) {
		Session session = null;
		Transaction tx = null;
		BigInteger count = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("select count(*) from file_user_permission fp where fp.file_id=:fileId and fp.user_id=:userId and fp.permission_id=:permissionId");
			query.setLong("fileId", fileId);
			query.setLong("userId", userId);
			query.setLong("permissionId", AppConstants.READ_PERMISSION);
			count = (BigInteger) query.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at function isAuthorizedToReadFile ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		if (count.longValue() > 0)
			return true;
		return false;
	}
	
	/**
	 * Check user is authorized to write or edit the uploaded file
	 */
	public boolean isUserAuthorizedToWriteFile(long fileId, long userId) {
		Session session = null;
		Transaction tx = null;
		BigInteger count = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("select count(*) from file_user_permission fp where fp.file_id=:fileId and fp.user_id=:userId and fp.permission_id=:permissionId");
			query.setLong("fileId", fileId);
			query.setLong("userId", userId);
			query.setLong("permissionId", AppConstants.WRITE_PERMISSION);
			count = (BigInteger) query.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at function isUserAuthorizedToWriteFile ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		if (count.longValue() > 0)
			return true;
		return false;
	}
	
	/**
	 * Check user is authorized to delete the uploaded file
	 */
	public boolean isUserAuthorizedToDeleteFile(long fileId, long userId) {
		Session session = null;
		Transaction tx = null;
		BigInteger count = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("select count(*) from file_user_permission fp where fp.file_id=:fileId and fp.user_id=:userId and fp.permission_id=:permissionId");
			query.setLong("fileId", fileId);
			query.setLong("userId", userId);
			query.setLong("permissionId", AppConstants.READ_WRITE_PERMISSION);
			count = (BigInteger) query.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at function isUserAuthorizedToDeleteFile ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		if (count.longValue() > 0)
			return true;
		return false;
	}
	
	/**
	 * Delete the records of file permissions using file id but retains the
	 * login user permission info
	 */
	public boolean deleteFilePermissionsByFileId(long fileId) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.getCurrentSession();
			tx = session.beginTransaction();
			Query query = session
					.createSQLQuery("delete from file_user_permission where file_id=:fileId");
			query.setLong("fileId", fileId);
			query.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			logger.error("Error at function deleteFilePermissionsByFileId ", e);
			if (tx != null) {
				tx.rollback();
				throw new ApplicationException(1111, "Database Exception.");
			}
		}
		return true;
	}
	
}