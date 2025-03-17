package com.enigma.hotelbookingapp.Controllers;


import com.enigma.hotelbookingapp.DTO.LoginDTO;
import com.enigma.hotelbookingapp.DTO.LoginResponseDTO;
import com.enigma.hotelbookingapp.DTO.SignUpRequestDTO;
import com.enigma.hotelbookingapp.DTO.UserDTO;
import com.enigma.hotelbookingapp.Security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RequestMapping(path = "/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) throws Exception {
        return new ResponseEntity<>(authService.signUp(signUpRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping(path = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String[] ans = authService.login(loginDTO);
        Cookie cookie = new Cookie("refreshToken", ans[1]);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        httpServletResponse.addCookie(cookie);
        return new ResponseEntity<>(new LoginResponseDTO(ans[0]), HttpStatus.OK);
    }


    @PostMapping(path = "/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(HttpServletRequest httpServletRequest) throws Exception {
        Cookie[] cookies = httpServletRequest.getCookies();
        String refreshToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
                break;
            }
        }
        if (refreshToken.isEmpty()) {
            throw new AuthenticationException("Refresh Toekn not found at all");
        }

        String newToken = authService.refreshToken(refreshToken);
        return new ResponseEntity<>(new LoginResponseDTO(newToken), HttpStatus.OK);
    }


}
