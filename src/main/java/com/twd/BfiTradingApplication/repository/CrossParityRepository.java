package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.CrossParity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrossParityRepository extends JpaRepository<CrossParity, Integer> {
}
