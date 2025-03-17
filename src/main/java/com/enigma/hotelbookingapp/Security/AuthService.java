package com.enigma.hotelbookingapp.Security;


import com.enigma.hotelbookingapp.DTO.LoginDTO;
import com.enigma.hotelbookingapp.DTO.SignUpRequestDTO;
import com.enigma.hotelbookingapp.DTO.UserDTO;
import com.enigma.hotelbookingapp.Entities.Enums.Role;
import com.enigma.hotelbookingapp.Entities.User;
import com.enigma.hotelbookingapp.Exceptions.ResourceNotFoundException;
import com.enigma.hotelbookingapp.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;


    public UserDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        User user = userRepository.findByEmail(signUpRequestDTO.getEmail()).orElse(null);
        if (user != null) {
            throw new ResourceNotFoundException("The user with mail id " + signUpRequestDTO.getEmail() + " already exists");
        }
        User newUser = modelMapper.map(signUpRequestDTO, User.class);
        newUser.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        newUser.setRoles(Set.of(Role.ADMIN));
        User savedUser = userRepository.save(newUser);
        UserDTO finalUser = modelMapper.map(savedUser, UserDTO.class);
        return finalUser;
    }

    public String[] login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        User user = (User) authentication.getPrincipal();

        String[] ans = new String[2];
        ans[0] = jwtService.generateAccessToken(user);
        ans[1] = jwtService.refreshToken(user);
        return ans;

    }

    public String refreshToken(String token) {
        Long userId = jwtService.getUserIdFromToken(token);

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The user doesn't exist"));

        String newToken = jwtService.generateAccessToken(user);
        return newToken;

    }

}
