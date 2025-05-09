package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.DTO.UsersDTO;
import com.ghoul.AmanaFund.entity.Users;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UsersDTOMapper implements Function<Users, UsersDTO> {

    @Override
    public UsersDTO apply(Users user) {
        return UsersDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .age(user.getAge())
                .address(user.getAddress())
                .civilStatus(user.getCivilStatus())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .enabled(user.getEnabled())
                .accountDeleted(user.getAccountDeleted())
                .accountLocked(user.getAccountLocked())
                .userScore(user.getUserScore())
                .createdDate(user.getCreatedDate())
                .numberOfSinistres(user.getNumberOfSinistres())
                .build();
    }
}
