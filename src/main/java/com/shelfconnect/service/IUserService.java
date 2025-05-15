package com.shelfconnect.service;

import com.shelfconnect.dto.req.UpdateProfileReq;
import com.shelfconnect.model.User;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;


public interface IUserService {
    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> createUser(User user);

    @Transactional
    Optional<User> updateUser(Long id, UpdateProfileReq updatedUser);

    boolean updatePassword(Long id, String oldPassword, String newPassword);
}
