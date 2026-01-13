package com.tp.frontend.service;

import com.tp.frontend.client.UsersApiClient;
import com.tp.frontend.dto.User.UserRequest;
import com.tp.frontend.dto.User.UserResponse;
import com.tp.frontend.dto.User.UserUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UsersApiClient usersApiClient;

    public UserService(UsersApiClient usersApiClient) {
        this.usersApiClient = usersApiClient;
    }

    public List<UserResponse> list(String jwt) {
        return usersApiClient.list(jwt);
    }

    public UserResponse get(Long id, String jwt) {
        return usersApiClient.getById(jwt, id);
    }

    public void create(UserRequest dto, String jwt) {
        usersApiClient.create(jwt, dto);
    }

    public void update(Long id, UserUpdate dto, String jwt) {
         usersApiClient.update(jwt, id, dto);
    }

    public void delete(Long id, String jwt) {
        usersApiClient.delete(jwt, id);
    }
}
