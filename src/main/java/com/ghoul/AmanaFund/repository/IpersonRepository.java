package com.ghoul.AmanaFund.repository;

import com.ghoul.AmanaFund.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpersonRepository extends JpaRepository<Person,Long> {
}
