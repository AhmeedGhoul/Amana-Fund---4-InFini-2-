package com.ghoul.AmanaFund.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String name;
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<Users> users;
    @org.springframework.data.annotation.CreatedDate
    @Column(updatable = false,nullable = false)
    private LocalDate CreatedDate;
    @org.springframework.data.annotation.LastModifiedDate
    @Column(insertable = false)
    private LocalDate LastModifiedDate;


}
