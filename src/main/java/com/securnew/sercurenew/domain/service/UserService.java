package com.securnew.sercurenew.domain.service;

import java.util.List;

import com.securnew.sercurenew.domain.AppRole;
import com.securnew.sercurenew.domain.AppUser;

public interface UserService {
    AppUser saveUser(AppUser appUser);

    AppRole saveRole(AppRole appRole);

    void addRoleToUser(String username, String rolename);

    AppUser getUser(String username);

    List<AppUser> getUsers();

}
