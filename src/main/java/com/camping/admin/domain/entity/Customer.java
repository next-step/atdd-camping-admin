package com.camping.admin.domain.entity;

import com.camping.admin.domain.vo.Email;
import com.camping.admin.domain.vo.PhoneNumber;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "customers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_number"))
    private PhoneNumber phoneNumber;

    public Customer(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = new Email(email);
        this.phoneNumber = phoneNumber != null ? new PhoneNumber(phoneNumber) : null;
    }
}