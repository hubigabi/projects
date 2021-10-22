package com.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users", indexes = {@Index(name = "idx_last_name", columnList = "last_name")})
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends RepresentationModel<User> implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false, unique = true)
    private String username;

    @NotNull
    private String password;

    @Enumerated
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Size(min = 3, message = "First name must contain at least {min} characters.")
    @Column(name = "first_name", nullable = false, length = 50)
    protected String firstName;

    @Size(min = 3, message = "Last name must contain at least {min} characters.")
    @Column(name = "last_name", nullable = false, length = 100)
    protected String lastName;

    @Email
    @Column(unique = true)
    protected String email;

    public User(Long ID, String username, String password, UserRole userRole, String lastName, String firstName, String email) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }

    public User(String username, String password, UserRole userRole, String lastName, String firstName, String email) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }

    @Override
    public final Collection<? extends GrantedAuthority> getAuthorities() {
        return userRole.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public final boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public final boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }
}
