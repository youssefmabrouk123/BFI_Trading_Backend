package com.twd.BfiTradingApplication.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

    @Data
    @Entity
    @Table(name = "users")
    public class User implements UserDetails {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String firstName;
        private String lastName;
        @Column(name ="email", nullable = false , unique = true)
        private String email;
        private String password;
        private String role ="USER";

        @ManyToMany
        @JoinTable(
                name = "user_favorite_cross_parity",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "cross_parity_id")
        )
        private List<CrossParity> favoriteCrossParities = new ArrayList<>(); // Liste des favoris


        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(role));
        }
    
    
        @Override
        public String getUsername() {
            return email;
        }
    
    
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
    
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
    
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
    
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
