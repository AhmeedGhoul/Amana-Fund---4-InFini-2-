package com.ghoul.AmanaFund.specification;

import com.ghoul.AmanaFund.entity.Users;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class UserSpecification {

    public static Specification<Users> searchUsers(
            String firstName, String lastName, String email,
            Integer age, String phoneNumber, LocalDate dateOfBirth, Boolean enabled) {

        return (root, query, criteriaBuilder) -> {
            Specification<Users> spec = Specification.where(null);

            if (firstName != null && !firstName.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.like(cb.lower(root1.get("firstName")), "%" + firstName.toLowerCase() + "%"));
            }
            if (lastName != null && !lastName.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.like(cb.lower(root1.get("lastName")), "%" + lastName.toLowerCase() + "%"));
            }
            if (email != null && !email.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.like(cb.lower(root1.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (age != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("age"), age));
            }
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                spec = spec.and((root1, query1, cb) -> cb.like(root1.get("phoneNumber"), "%" + phoneNumber + "%"));
            }
            if (dateOfBirth != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("dateOfBirth"), dateOfBirth));
            }
            if (enabled != null) {
                spec = spec.and((root1, query1, cb) -> cb.equal(root1.get("enabled"), enabled));
            }

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}
