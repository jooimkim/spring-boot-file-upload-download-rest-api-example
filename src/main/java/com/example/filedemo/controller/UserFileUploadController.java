package com.example.filedemo.controller;

import com.example.filedemo.model.UserFileInfo;
import com.example.filedemo.model.UserInfo;
import com.example.filedemo.service.FileUploadService;
import com.example.filedemo.service.StorageEncryptionService;
import com.example.filedemo.service.UserInfoService;
import com.example.filedemo.util.AppConstants;
import com.example.filedemo.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Controller
public class UserFileUploadController {
	private static final Logger logger = LoggerFactory.getLogger(UserFileUploadController.class);
	@Autowired
	private UserInfoService userService;
	@Autowired
	private FileUploadService fileUploadService;
	@Value("${upload.location}")
	private String UPLOAD_DIR_PATH;
	@Value("${tempUpload.location}")
	private String UPLOAD_TEMP_DIR_PATH;
	@Autowired StorageEncryptionService storageEncryptionService;
	@Value("${upload.maxsize}")
	private String UPLOAD_MAX_SIZE;
	@Value("${exclude.file_extension}")
	private String EXCLUDE_FILE_EXTENSIONS;
	
	/**
	 * render the File Upload Page
	 * @return
	 */
	@RequestMapping(value = {"/file_upload","/file_upload/upload" } ,method = RequestMethod.GET)
	public ModelAndView viewUploadPage() {
		//logs debug message
		if(logger.isDebugEnabled()){
			logger.debug("User File Uploade Controller is executed!");
		}
		ModelAndView model = new ModelAndView(AppConstants.URL_USER_UPLOAD);
		model.addObject("userFileInfo", new UserFileInfo());
		return model;
	}
	
	/**
	 * Upload file
	 */
	@RequestMapping(value = "/file_upload/upload", method = RequestMethod.POST)
	public String uploadFileHandler(@ModelAttribute(value="userFileInfo") @Validated UserFileInfo userFileInfo, BindingResult result,
									@RequestParam("fileName") MultipartFile fileName, Model model, RedirectAttributes redir,
									HttpServletRequest request, HttpSession session) {
		
		boolean isValidFormFields=true;
		boolean isEditPage=false;
		UserFileInfo dbFileInfo=null;
		long fileId = userFileInfo.getId();
		
		session = request.getSession(false);
		UserInfo user = (UserInfo)session.getAttribute("user");
		
		if(user==null)  return "redirect:/invalidUser";
		
		try {
			isValidFormFields = validateForm(userFileInfo,model); // field validation
		} catch (ValidationException e1) {
			isValidFormFields=false;
			e1.printStackTrace();
		}
		if(!isValidFormFields){
			model.addAttribute("errorMessage", AppConstants.MSG_FORM_CONTAINS_EROOR);
			return AppConstants.URL_USER_UPLOAD;
		}
		
		//check if file id exist in form (in case of edit file)
		if(fileId>0){
			isEditPage=true;
			dbFileInfo = fileUploadService.getFileById(fileId);
		}
		
		if (!fileName.isEmpty()) {
			try {
				if(UPLOAD_MAX_SIZE.isEmpty())UPLOAD_MAX_SIZE="0";
				String username = user.getUser_name();
				String uploadFileName = fileName.getOriginalFilename();
				long ownerId = user.getId();
				
				// check for invalid file if size is more then specified in property file
				if(!AppUtil.isValidFileSize(fileName.getSize(),Long.valueOf(UPLOAD_MAX_SIZE.trim()))){
					model.addAttribute("errorMessage", "File is not valid.Can not upload more then "+Long.valueOf(UPLOAD_MAX_SIZE.trim())/(1024*1024)+" MB file!");
					return AppConstants.URL_USER_UPLOAD;
				}
				
				// check for invalid extensions, if specified in property file
				if(!AppUtil.isValidFileExtenstion(uploadFileName, EXCLUDE_FILE_EXTENSIONS)){
					model.addAttribute("errorMessage", "File is invalid.Can not upload files having extensions "+EXCLUDE_FILE_EXTENSIONS+".");
					return AppConstants.URL_USER_UPLOAD;
				}
				
				// check if file is exist in database
				if((isEditPage && !uploadFileName.equals(dbFileInfo.getFileName()))|| !isEditPage){
					if(fileUploadService.isFileExist(uploadFileName)){
						model.addAttribute("errorMessage", AppConstants.MSG_FILE_NAME_EXIST);
						return AppConstants.URL_USER_UPLOAD;
					}
				}
				
				// check if document name is exist in database
				if((isEditPage && !userFileInfo.getDocumentName().equals(dbFileInfo.getDocumentName())) || !isEditPage){
					if(fileUploadService.isDocumentNameExist(userFileInfo.getDocumentName())){
						model.addAttribute("errorMessage", AppConstants.MSG_DOCUMENT_NAME_EXIST);
						return AppConstants.URL_USER_UPLOAD;
					}
				}
				
				
				// create new objects if no user is set from permission dropdown
				if(userFileInfo.getReadPermissionUsers()==null)
					userFileInfo.setReadPermissionUsers(new HashSet<Long>());
				if(userFileInfo.getWritePermissionUsers()==null)
					userFileInfo.setWritePermissionUsers(new HashSet<Long>());
				if(userFileInfo.getReadWritePermissionUsers()==null)
					userFileInfo.setReadWritePermissionUsers(new HashSet<Long>());
				
				// set read and write permission if user selected only users from 'WRITE' and 'READ & WRITE' dropdown
				userFileInfo = setReadAndWritePermissionIfNeeded(userFileInfo);
				
				// if edit file user is not owner then set it's previous permissions
				if(isEditPage){
					long loginUserId = user.getId();
					ownerId = dbFileInfo.getFileOwner();
					if(dbFileInfo.getReadPermissionUsers().contains(loginUserId))
						userFileInfo.getReadPermissionUsers().add(loginUserId);
					if(dbFileInfo.getWritePermissionUsers().contains(loginUserId))
						userFileInfo.getWritePermissionUsers().add(loginUserId);
					if(dbFileInfo.getReadWritePermissionUsers().contains(loginUserId))
						userFileInfo.getReadWritePermissionUsers().add(loginUserId);
				}
				userFileInfo.setFileName(uploadFileName);
				userFileInfo.setFileOwner(ownerId);
				userFileInfo.getReadPermissionUsers().add(ownerId);
				userFileInfo.getWritePermissionUsers().add(ownerId);
				userFileInfo.getReadWritePermissionUsers().add(ownerId);
				userFileInfo.setDescription(ESAPI.encoder().canonicalize(userFileInfo.getDescription()));
				String uploadDirPath = AppUtil.getUploadFilePath(UPLOAD_DIR_PATH, username, ownerId);
				byte[] bytes = fileName.getBytes();
				String newFileName=AppUtil.getUploadFileNameWithPrefix(uploadFileName);
				
				//if edit file then create a revision of file.
				if(isEditPage)AppUtil.checkIfFileExistInServerAndRename(uploadDirPath, newFileName, userFileInfo.getFile_revision());
				uploadDirPath = uploadDirPath + File.separator + newFileName;
				
				//check the zip file, is it containing valid files or not.
				if(AppUtil.isCompressedFile(uploadFileName)){
					File uploadTempDirPath = new File(UPLOAD_TEMP_DIR_PATH);
					if(!uploadTempDirPath.exists()) uploadTempDirPath.mkdirs();
					String uploadTempFile = uploadTempDirPath+File.separator+uploadFileName;
					BufferedOutputStream stream = new BufferedOutputStream( new FileOutputStream(uploadTempFile));
					stream.write(bytes);
					stream.close();
					boolean isContainsInvalidFiles = AppUtil.isContainsInvalidFilesInZip(uploadTempFile, EXCLUDE_FILE_EXTENSIONS);
					if(isContainsInvalidFiles){
						new File(uploadTempFile).delete();
						model.addAttribute("errorMessage", "Zip File is invalid.Can not upload files having extensions "+EXCLUDE_FILE_EXTENSIONS+".");
						return AppConstants.URL_USER_UPLOAD;
					}
					new File(uploadTempFile).delete();
				}
				
				// Create the file on server
				File serverFile = new File(uploadDirPath);
				userFileInfo.setEncryptionKey(storageEncryptionService.generateRandomKey());
				storageEncryptionService.encrypt(bytes, serverFile, userFileInfo.getEncryptionKey());
				
				if(isEditPage){
					//update record
					userFileInfo.setFile_revision(userFileInfo.getFile_revision()+1);
					fileUploadService.updateUserFile(userFileInfo);
				}else{
					// save file information to db
					fileUploadService.saveUserFile(userFileInfo);
				}
				
				logger.info("Server File Location=" + serverFile.getAbsolutePath());
				redir.addFlashAttribute("successMessage", "You successfully uploaded file " + ESAPI.encoder().encodeForHTML(uploadFileName));
				return "redirect:/file_upload";
			} catch (Exception e) {
				redir.addFlashAttribute("errorMessage", "An internal error occured, failed to upload file.");
				e.printStackTrace();
				return "redirect:/file_upload";
			}
		} else {
			model.addAttribute("errorMessage", "Failed to upload because the file was empty.");
			return AppConstants.URL_USER_UPLOAD;
		}
	}
	
	/*
	 * Method used to populate the user list in view.
	 * Note that here you can call external systems to provide real data.
	 */
	@ModelAttribute("listUser")
	public List<UserInfo> initUserList(HttpServletRequest request){
		UserInfo loginUser = (UserInfo)request.getSession(false).getAttribute("user");
		List<UserInfo> userList = userService.getUserListWithCustomerRole();
		Iterator<UserInfo> iterator = userList.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getId()==loginUser.getId()) {
				iterator.remove();
				break;
			}
		}
		return userList;
	}
	
	/**
	 * validate the upload form fields
	 * @param userFileInfo
	 * @param model
	 * @return
	 * @throws ValidationException
	 */
	
	private boolean validateForm(UserFileInfo userFileInfo, Model model) throws ValidationException{
		if(userFileInfo.getDocumentName().isEmpty()){
			model.addAttribute("documentNameError", "Document name required.");
			return false;
		}else if(!AppUtil.isValidInput("document name", userFileInfo.getDocumentName(), "AlphaNumericChar", 60, false)){
			model.addAttribute("documentNameError", "Document name invalid (e.g Myfile or Myfile12).");
			return false;
		}
		return true;
	}
	
	/**
	 * If user select only write permission then this function will set the read permission also for selected user
	 * If user select only READ & WRITE permission, then this function will set read , write and delete permission for selected user
	 * @param userFileInfo
	 * @return
	 */
	private UserFileInfo setReadAndWritePermissionIfNeeded(UserFileInfo userFileInfo){
		
		if(userFileInfo.getWritePermissionUsers().size()>0){
			for(long read_write_perms : userFileInfo.getWritePermissionUsers()){
				if(!userFileInfo.getReadPermissionUsers().contains(read_write_perms)){
					userFileInfo.getReadPermissionUsers().add(read_write_perms);
				}
			}
		}
		
		if(userFileInfo.getReadWritePermissionUsers().size()>0){
			for(long read_write_perms : userFileInfo.getReadWritePermissionUsers()){
				if(!userFileInfo.getReadPermissionUsers().contains(read_write_perms)){
					userFileInfo.getReadPermissionUsers().add(read_write_perms);
				}
				if(!userFileInfo.getWritePermissionUsers().contains(read_write_perms)){
					userFileInfo.getWritePermissionUsers().add(read_write_perms);
				}
			}
		}
		return userFileInfo;
	}
	
}
