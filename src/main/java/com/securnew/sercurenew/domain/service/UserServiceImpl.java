package com.securnew.sercurenew.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepo.findByUsername(username);
        if(user==null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else{
            log.info("User found in the database: {}", username);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role->{
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), authorities);
    }


    @Override
    public AppUser saveUser(AppUser appUser) {
        log.info("Saving new user {} to the database",appUser.getUsername());
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
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
