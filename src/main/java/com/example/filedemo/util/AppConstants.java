package com.example.filedemo.util;

public class AppConstants {
	
	public static final String APP_NAME = "CloudFileUpload";
	// salt strings
	public static final String SALT_PRE_STRING = "$2u!*t&p$l#k@3#$";
	public static final String SALT_POST_STRING ="$#45a_g4~em&#w$";
	public static final String UPLOAD_FILE_PREFIX="cfp-";
	
	
	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_DELETED = "DELETED";
	public static final int YES =1;
	public static final int NOT =0;
	
	// Role id
	public static final int ADMIN_ROLE_ID =1001;
	public static final int CUSTOMER_ROLE_ID =1002;
	public static final int READ_PERMISSION=1;
	public static final int WRITE_PERMISSION=2;
	public static final int READ_WRITE_PERMISSION=3;
	
	// Application URLs
	public static final String URL_ADMIN_LOGIN = "/admin/admin_login";
	public static final String URL_ADMIN_HOME = "/admin/admin_home";
	public static final String URL_ADMIN_USER = "/admin/add_user";
	public static final String URL_ADMIN_EDIT_USER = "/admin/edit_user";
	public static final String URL_ADMIN_FEEDBACK = "/admin/feedback_report";
	public static final String URL_USER_LOGIN = "/index";
	public static final String URL_USER_HOME = "/home";
	public static final String URL_USER_UPLOAD = "/upload";
	public static final String URL_USER_FEEDBACK = "/feedback";
	public static final String URL_ERROR = "/error";
	public static final String URL_DOWNLOAD_FILE = "/download";
	
	// messages
	public static final String MSG_REQUIRED_USERNAME = "Email/username is required!";
	public static final String MSG_REQUIRED_PASSWORD = "Password is required!";
	public static final String MSG_BAD_LOGIN_INPUT = "Bad login credentials!";
	public static final String MSG_LOGOUT_SUCCESS = "Logout successfully.";
	public static final String MSG_FORM_CONTAINS_EROOR = "The form contains error.";
	public static final String MSG_USER_SAVED="The User information is stored successfully.";
	public static final String MSG_USER_UPDATED="The User information is updated successfully.";
	public static final String MSG_USER_DELETED="The User is deleted successfully.";
	public static final String MSG_USER_NOT_EXIST="User does not exist.";
	public static final String MSG_SELECT_DROPDOWN_USER="Please select the User from dropdown list.";
	public static final String MSG_INVALID_USER="Please login to access application.";
	
	public static final String MSG_UNAUTHORIZED_READ_FILE="User is not authorized to read this file.";
	public static final String MSG_UNAUTHORIZED_WRITE_FILE="User is not authorized to edit this file.";
	public static final String MSG_UNAUTHORIZED_DELETE_FILE="User is not authorized to delete this file.";
	
	public static final String MSG_FILE_NAME_EXIST="File name is already exist.";
	public static final String MSG_DOCUMENT_NAME_EXIST="Document name is already exist.";
	
	public static final String FEEDBACK_SAVED="The Feedback is submitted successfully.";
	
	
	// Error Codes
	public static final int INVALID_LOGIN_CODE = 9001;
	public static final int INVALID_READ_AUTH_CODE = 9002;
	public static final int INVALID_FILE_CODE = 9003;
	public static final Object MSG_ACCOUNT_LOCKED = "Your account is locked. Wait 30 minutes and try again.";
}
