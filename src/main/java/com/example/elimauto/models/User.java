package com.example.elimauto.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Номер телефона обязателен!")
    @Pattern(
            regexp = "^(\\+?\\d{1,3})?\\d{10}$",
            message = "Номер телефона должен быть в формате +1234567890 или 1234567890"
    )
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @ToString.Exclude
    @NotBlank(message = "Пароль обязателен!")
    @Size(min = 8, max = 20, message = "Пароль должен быть длиной от 6 до 20 символов")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Имя не может быть пустым!")
    @Column(name = "name")
    @Size(min = 1, max = 20, message = "Поле 'Имя' должно быть длиной от 1 до 20 символов")
    private String name;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
