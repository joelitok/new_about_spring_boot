package com.securnew.sercurenew.domain.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.securnew.sercurenew.domain.AppUser;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
