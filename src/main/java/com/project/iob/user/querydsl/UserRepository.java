package com.project.iob.user.querydsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.iob.user.entity.User;
import com.project.iob.user.repository.jpa.UserRepositoryCustom;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

}