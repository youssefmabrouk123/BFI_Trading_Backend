package com.twd.BfiTradingApplication.repository;
import com.twd.BfiTradingApplication.entity.CrossParity;
import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {

    Optional<Position> findByCurrency(Currency currency);

    @Query("SELECT COUNT(p) > 0 FROM Position p WHERE p.currency.pk = :currencyId")
    boolean existsByCurrencyId(@Param("currencyId") Integer currencyId);

    List<Position> findByUserId(Integer userId); // Pour récupérer les positions d'un utilisateur
}