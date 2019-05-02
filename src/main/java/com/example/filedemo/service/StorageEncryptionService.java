package com.example.filedemo.service;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class StorageEncryptionService {
	public String generateRandomKey() {
		return "aaaa";
	}
	
	public void encrypt(byte[] bytes, File serverFile, String encryptionKey) {
	}
}
