package com.micro.secureapp.repo;


import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.micro.secureapp.model.User;

@Repository
public interface UserRepo extends MongoRepository<User, UUID> {

}
