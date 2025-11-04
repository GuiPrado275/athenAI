package com.guilhermeborges.athenAI.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guilhermeborges.athenAI.models.enums.ProfileEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = User.TABLE_NAME) //tabela do database
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    public static final String TABLE_NAME = "users";

    @Id
    @Column(name = "id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id is randomly generated
    private Long id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    @Size(min = 5, max = 16, message = "The username must be between 5 and 16 characters long.")
    @NotBlank(message = "The username cannot be blank.")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 60, message = "The password must be between 8 and 60 characters long.")
    @NotBlank(message = "The password cannot be blank.")
    private String password;

    @Column(name = "profile", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER) //load roles
    @CollectionTable(name = "user_profile")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Integer> profiles = new HashSet<>(); //list of unique values

    public Set<ProfileEnum> getProfiles(){
        return this.profiles.stream().map(x -> ProfileEnum.toEnum(x)).collect(java.util.stream.Collectors.toSet());
        //It retrieves all the numbers stored in profiles, converts each number to the corresponding enum (ProfileEnum),
        // and returns the converted set.
    }

    public void AddProfile(ProfileEnum profile){
        this.profiles.add(profile.getCode()); // Basically, it adds a profile to the user and saves it
    }

}
