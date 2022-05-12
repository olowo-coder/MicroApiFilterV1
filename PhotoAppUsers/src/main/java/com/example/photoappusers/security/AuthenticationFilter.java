package com.example.photoappusers.security;

import com.example.photoappusers.dto.LoginRequest;
import com.example.photoappusers.model.CreateUserModel;
import com.example.photoappusers.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;

    public AuthenticationFilter(UserService userService,
                                Environment env, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.env = env;
        super.setAuthenticationManager(authenticationManager);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            LoginRequest creds = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            System.out.println(creds.toString());
            return getAuthenticationManager()
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            creds.getEmail(), creds.getPassword(), new ArrayList<>()));

        }
        catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
            String username = ((User)authResult.getPrincipal()).getUsername();
            CreateUserModel userModel = userService.getUserByEmail(username);

            String token = Jwts.builder()
                    .setSubject(userModel.getUserId())
                    .setExpiration(new Date(System.currentTimeMillis() +
                            Long.parseLong(Objects.requireNonNull(env.getProperty("token.expiration")))))
                    .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                    .compact();

            response.setHeader("token", token);
            response.setHeader("userId", userModel.getUserId());
    }
}
