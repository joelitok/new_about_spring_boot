package com.securnew.sercurenew.domain.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.securnew.sercurenew.domain.AppRole;

public interface AppRoleRepo extends JpaRepository <AppRole,Long>{
    AppRole findByName(String name);
}


