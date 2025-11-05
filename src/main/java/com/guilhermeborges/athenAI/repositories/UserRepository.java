package com.guilhermeborges.athenAI.repositories;

import com.guilhermeborges.athenAI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    @Transactional(readOnly = true)
    User findByUsername(String username); //For search user for username

}