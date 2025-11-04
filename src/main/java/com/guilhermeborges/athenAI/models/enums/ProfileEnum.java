package com.guilhermeborges.athenAI.models.enums;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ProfileEnum {

    ADMIN(1,"ROLE_ADMIN"), //two roles, admin and user
    USER(2, "ROLE_USER");

    private Integer code;
    private String description;

    public static ProfileEnum toEnum(Integer code){

        if(Objects.isNull(code)){ //if code is null, return null
            return null;
        }
        for(ProfileEnum x : ProfileEnum.values()){ //for each enum value
            if(code.equals(x.getCode())){
                return x;
            }
        }

        throw new IllegalArgumentException("Código inválido! " + code); //if code is not found, throw exception
    }

}
