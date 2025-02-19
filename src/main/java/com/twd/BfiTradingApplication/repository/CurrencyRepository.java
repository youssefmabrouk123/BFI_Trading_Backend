package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}