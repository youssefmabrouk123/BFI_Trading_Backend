package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Integer> {
}
