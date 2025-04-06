package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserId(Integer userId); // Pour le tableau de bord
}
