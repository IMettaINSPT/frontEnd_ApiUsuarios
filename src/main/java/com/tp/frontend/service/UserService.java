package com.tp.frontend.service;

import com.tp.frontend.client.UsersApiClient;
import com.tp.frontend.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UsersApiClient usersApiClient;

    public UserService(UsersApiClient usersApiClient) {
        this.usersApiClient = usersApiClient;
    }

    public List<UserDTO> getAllUsers(String jwt) {
        return usersApiClient.list(jwt);
    }

    public void deleteUser(Long id, String jwt) {
        usersApiClient.delete(id, jwt);
    }
}
