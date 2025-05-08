package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpersonRepository extends JpaRepository<Person,Long> {
    Optional<Person> findByCIN(String CIN);

}
