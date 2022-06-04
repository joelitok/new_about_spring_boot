package com.securnew.sercurenew.domain.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.securnew.sercurenew.domain.AppRole;
import com.securnew.sercurenew.domain.AppUser;
import com.securnew.sercurenew.domain.Repo.AppRoleRepo;
import com.securnew.sercurenew.domain.Repo.AppUserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class UserServiceImpl  implements UserService, UserDetailsService{

    private final AppUserRepo appUserRepo;
    private final AppRoleRepo appRoleRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
    

    @Override
    public AppUser saveUser(AppUser appUser) {
        log.info("Saving new user {} to the database",appUser.getUsername());
        return appUserRepo.save(appUser);
    }

    @Override
    public AppRole saveRole(AppRole appRole) {
        log.info("Saving new role {} to the database",appRole.getName());
        return appRoleRepo.save(appRole);
    }

    @Override
    public void addRoleToUser(String username, String rolename) {
        log.info("Adding  role {} to user {} ",username, rolename);
        AppUser appUser = appUserRepo.findByUsername(username);
        AppRole appRole =appRoleRepo.findByName(rolename);
        appUser.getRoles().add(appRole);

        
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Fetching  user {} ",username);
        return appUserRepo.findByUsername(username);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Fetching  all users {} ");
        return appUserRepo.findAll();
    }

  
    
}
