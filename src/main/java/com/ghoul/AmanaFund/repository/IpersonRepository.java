package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IpersonRepository extends JpaRepository<Person,Long> {
    List<Person> findAllByCIN(String CIN);

}
