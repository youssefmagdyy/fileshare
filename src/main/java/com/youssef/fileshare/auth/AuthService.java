package com.youssef.fileshare.auth;

import com.youssef.fileshare.User.Role;
import com.youssef.fileshare.User.UserRepo;
import com.youssef.fileshare.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.youssef.fileshare.User.User;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;

    public String register(String username, String password) {
        if(userRepo.findByUsername(username).isPresent())
            throw new IllegalStateException("Username already exists");

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();

        userRepo.save(user);

        return jwtService.generateToken(username, Map.of("role", user.getRole().name()));

    }
        public String login(String username, String password) {

            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            var user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

            return jwtService.generateToken(username,Map.of("role",user.getRole().name()));
        }
    }
