package com.example.photoappusers.service;

import com.example.photoappusers.dto.UserDto;
import com.example.photoappusers.model.CreateUserModel;
import com.example.photoappusers.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CreateUserModel saveUser(UserDto userDto){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CreateUserModel userModel = modelMapper.map(userDto, CreateUserModel.class);
        final String userId = UUID.randomUUID().toString();
        userModel.setUserId(userId);
        userModel.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.saveUser(userModel);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CreateUserModel userModel = userRepository.getByEmail(username);
        return new User(userModel.getEmail(),
                userModel.getPassword(), true, true,true, true, new ArrayList<>());
    }

    public CreateUserModel getUserByEmail(String email){
        return userRepository.getByEmail(email);
    }
}
