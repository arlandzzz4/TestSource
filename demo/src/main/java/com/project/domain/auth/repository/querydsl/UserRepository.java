package com.project.domain.auth.repository.querydsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.domain.auth.entity.AuthUser;
import com.project.domain.auth.repository.jpa.UserRepositoryCustom;

@Repository
public interface UserRepository extends JpaRepository<AuthUser, Long>, UserRepositoryCustom {

}