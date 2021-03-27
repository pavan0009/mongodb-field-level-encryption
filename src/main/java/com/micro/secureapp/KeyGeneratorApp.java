package com.micro.secureapp;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyGeneratorApp {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // for example
		SecretKey secretKey = keyGen.generateKey();
		System.out.println(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
	}
}
