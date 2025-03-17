package com.enigma.hotelbookingapp.Services;

import com.enigma.hotelbookingapp.Entities.User;

public interface UserService {

    User findUserById(Long userId);
}
