package com.micro.secureapp.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.micro.secureapp.secure.Encrypted;

@Document
public class User {
	@Id
	private UUID _id;
	
	@Field
	@JsonProperty("first_name")
	private String firstName;
	
	@Field
	@JsonProperty("last_name")
	private String lastName;
	
	@Field
	@Encrypted
	@JsonProperty("email")
	private String email;
	
	@Field
	@Encrypted
	@JsonProperty("social_security_number")
	private String ssnNo;
	
	@Field
//	@Encrypted Use explicit encrypt and decrypt instead.
	@JsonProperty("mobile_no")
	private String mobileNo;
	
	
	public UUID get_id() {
		return _id;
	}
	public void set_id(UUID _id) {
		this._id = _id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSsnNo() {
		return ssnNo;
	}
	public void setSsnNo(String ssnNo) {
		this.ssnNo = ssnNo;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	@Override
	public String toString() {
		return "User [_id=" + _id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", ssnNo=" + ssnNo + ", mobileNo=" + mobileNo + "]";
	}
}
