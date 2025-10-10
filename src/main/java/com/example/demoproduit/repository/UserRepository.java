package com.example.demo.repository;

public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
