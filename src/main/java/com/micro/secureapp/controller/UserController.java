package com.micro.secureapp.controller;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.micro.secureapp.crypt.CryptVault;
import com.micro.secureapp.model.User;
import com.micro.secureapp.repo.UserRepo;


@RestController
public class UserController {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserRepo userRepository;
	
	@Autowired
	private CryptVault cryptVault;

	@RequestMapping(value = "/page/{page_no}", method = RequestMethod.GET)
	public ResponseEntity<Page<User>> getAllUsers(@PathVariable("page_no") Integer page_no) {
		System.out.println("Get all users");
		LOG.info("Getting all users.");
		Page<User> users = userRepository.findAll(PageRequest.of(page_no, 10));
		LOG.info("Users "+users);
		return ResponseEntity.ok().body(users);
	}

	@RequestMapping(value = "/id/{userId}", method = RequestMethod.GET)
	public ResponseEntity<User> getUser(@PathVariable("userId") String userId) {
		LOG.info("Getting user with ID: {}.", userId);
		Optional<User> optUser = userRepository.findById(UUID.fromString(userId));
		if(optUser.isPresent()) {
			User user = optUser.get();
			user.setMobileNo(new String(cryptVault.decrypt(Base64.getDecoder().decode(user.getMobileNo()))));
			return ResponseEntity.ok().body(user);
		}
		else 
			return ResponseEntity.notFound().build();
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<User> addNewUsers(@RequestBody User user) {
		LOG.info("Saving user.");
		user.set_id(UUID.randomUUID());
		user.setMobileNo(Base64.getEncoder().encodeToString(cryptVault.encrypt(user.getMobileNo().getBytes())));
		User userSaved = userRepository.save(user);
		return ResponseEntity.ok().body(userSaved);
	}

}
