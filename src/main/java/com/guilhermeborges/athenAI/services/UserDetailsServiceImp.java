package com.guilhermeborges.athenAI.services;

import com.guilhermeborges.athenAI.models.User;
import com.guilhermeborges.athenAI.repositories.UserRepository;
import com.guilhermeborges.athenAI.security.UserSpringSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username);
        if (Objects.isNull(user)){
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new UserSpringSecurity(user.getId(), user.getUsername(), user.getPassword(), user.getProfiles());
        //This go to UserDetaisService that transforms the ID,username, password and profiles in authorities
    }
}
