package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository<User, Long> da tu dong cung cap san cac ham CRUD co ban
// (save, findById, findAll, deleteById...) - khong can tu viet
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA tu sinh cau lenh SQL dua tren TEN HAM:
    // "findByEmail" -> tu dong hieu la "SELECT * FROM users WHERE email = ?"
    // Can ham nay vi luc dang nhap minh chi co email, chua co id, phai tra theo email truoc
    Optional<User> findByEmail(String email);
}