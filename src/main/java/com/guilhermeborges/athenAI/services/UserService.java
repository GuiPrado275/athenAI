package com.guilhermeborges.athenAI.services;

import com.guilhermeborges.athenAI.models.User;
import com.guilhermeborges.athenAI.models.dto.UserCreateDTO;
import com.guilhermeborges.athenAI.models.dto.UserUpdateDTO;
import com.guilhermeborges.athenAI.models.enums.ProfileEnum;
import com.guilhermeborges.athenAI.repositories.UserRepository;
import com.guilhermeborges.athenAI.security.UserSpringSecurity;
import com.guilhermeborges.athenAI.services.exceptions.AuthorizationException;
import com.guilhermeborges.athenAI.services.exceptions.DataBindingViolationException;
import com.guilhermeborges.athenAI.services.exceptions.ObjectNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id){
        UserSpringSecurity userSpringSecurity = authenticated();
        if(!Objects.nonNull(userSpringSecurity)
                || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId()))
            throw new AuthorizationException("Access denied!"); //to ensure that the user is authenticated and
                                                                //has the right to access the resource
                                                                //search user by id
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
                "User not found! Id: " + id + ", Type: " + User.class.getName()));
    }

    @Transactional
    public User create(User user){
        user.setId(null);
        user.setPassword((this.bCryptPasswordEncoder.encode(user.getPassword()))); //to encrypt the password
        user.setProfiles((Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet())));
        user = this.userRepository.save(user);
        return user;
    }

    @Transactional
    public User update(User updatedUser) {
        User existingUser = findById(updatedUser.getId());

        if (updatedUser.getUsername() != null && //If the username has been changed
                !updatedUser.getUsername().equals(existingUser.getUsername())) {

            if (userRepository.existsByUsername(updatedUser.getUsername())) { // Checks if another user with that username already exists.
                throw new IllegalArgumentException("Username is already in use.!");
            }

            existingUser.setUsername(updatedUser.getUsername());
        }

        //Update password, if provided.
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public void delete(Long id){
        findById(id);
        try{
            this.userRepository.deleteById(id); //to delete id
        } catch (Exception e){ //if it doesn't have a relationship with other entities
            throw new DataBindingViolationException("Cannot delete because this user has an entities relationship!");
            //error to delete the user
        }
    }

    public static UserSpringSecurity authenticated(){
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            return null;
        }
    }

    public User fromDTO(@Valid UserCreateDTO obj){
        User user = new User();
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }

    public User fromDTO(@Valid UserUpdateDTO obj){
        User user = new User();
        user.setId(obj.getId());
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }

}
