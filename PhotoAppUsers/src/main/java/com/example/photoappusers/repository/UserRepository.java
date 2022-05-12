package com.example.photoappusers.repository;

import com.example.photoappusers.dto.UserDto;
import com.example.photoappusers.model.CreateUserModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserRepository {
    Map<String, CreateUserModel> userDB = new HashMap<>();
    Map<String, CreateUserModel> emailDB = new HashMap<>();

    public CreateUserModel saveUser(CreateUserModel userModel){
        userDB.put(userModel.getUserId(), userModel);
        emailDB.put(userModel.getEmail(), userModel);
        return userModel;
    }

    public UserDto getUser(String id){
        ModelMapper modelMapper = new ModelMapper();
        CreateUserModel userModel = userDB.get(id);
        return modelMapper.map(userModel, UserDto.class);
    }

    public CreateUserModel getByEmail(String email){
        return emailDB.get(email);
    }
}
