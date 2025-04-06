package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Integer> {
    List<UserAction> findByUserId(Integer userId);
    List<UserAction> findByUserIdAndActionType(Integer userId, String actionType);
}
