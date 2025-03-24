package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PositionUpdateService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendPositionUpdate(List<Position> positions) {
        messagingTemplate.convertAndSend("/topic/positions", positions);
    }
}