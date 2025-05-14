//    package com.twd.BfiTradingApplication.controller;
//
//    import com.twd.BfiTradingApplication.dto.PendingOrderDTO;
//    import com.twd.BfiTradingApplication.entity.User;
//    import com.twd.BfiTradingApplication.service.PendingOrderService;
//    import com.twd.BfiTradingApplication.service.TradingService;
//    import com.twd.BfiTradingApplication.service.UserService;
//    import org.springframework.beans.factory.annotation.Autowired;
//    import org.springframework.http.HttpStatus;
//    import org.springframework.http.ResponseEntity;
//    import org.springframework.security.core.Authentication;
//    import org.springframework.security.core.context.SecurityContextHolder;
//    import org.springframework.security.core.userdetails.UserDetails;
//    import org.springframework.web.bind.annotation.*;
//
//    import java.util.List;
//
//    @RestController
//    @RequestMapping("/api/pending-orders")
//    public class PendingOrderController {
//        @Autowired
//        private UserService userService;
//
//        private final PendingOrderService pendingOrderService;
//        private final TradingService tradingService;
//
//        @Autowired
//        public PendingOrderController(PendingOrderService pendingOrderService, TradingService tradingService) {
//            this.pendingOrderService = pendingOrderService;
//            this.tradingService = tradingService;
//        }
//
//        @PostMapping
//        public ResponseEntity<?> createPendingOrder(@RequestBody PendingOrderDTO pendingOrderDTO, Authentication authentication) {
//            try {
//                String userMail = getAuthenticatedUserEmail();
//                User user = userService.getUserByEmail(userMail);
//
//                PendingOrderDTO createdOrder = pendingOrderService.createPendingOrder(pendingOrderDTO, user.getId());
//                return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
//            } catch (IllegalArgumentException e) {
//                return ResponseEntity.badRequest().body(e.getMessage());
//            } catch (Exception e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("An error occurred while creating the pending order: " + e.getMessage());
//            }
//        }
//
//        @GetMapping
//        public ResponseEntity<List<PendingOrderDTO>> getPendingOrders(Authentication authentication) {
//            try {
//                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                User user = tradingService.getUserByEmail(userDetails.getUsername());
//
//                List<PendingOrderDTO> pendingOrders = pendingOrderService.getPendingOrdersByUser(user.getId());
//                return ResponseEntity.ok(pendingOrders);
//            } catch (Exception e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//
//
//
//        @GetMapping("/cancelled")
//        public ResponseEntity<List<PendingOrderDTO>> getCancelledOrders(Authentication authentication) {
//            try {
//                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                User user = tradingService.getUserByEmail(userDetails.getUsername());
//
//                List<PendingOrderDTO> pendingOrders = pendingOrderService.getCancelledOrdersByUser(user.getId());
//                return ResponseEntity.ok(pendingOrders);
//            } catch (Exception e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//
//
//        @GetMapping("/expired")
//        public ResponseEntity<List<PendingOrderDTO>> getExpiredOrders(Authentication authentication) {
//            try {
//                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                User user = tradingService.getUserByEmail(userDetails.getUsername());
//
//                List<PendingOrderDTO> pendingOrders = pendingOrderService.getExpiredOrdersByUser(user.getId());
//                return ResponseEntity.ok(pendingOrders);
//            } catch (Exception e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            }
//        }
//
//        @DeleteMapping("/{orderId}")
//        public ResponseEntity<?> cancelPendingOrder(@PathVariable Integer orderId, Authentication authentication) {
//            try {
//                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                User user = tradingService.getUserByEmail(userDetails.getUsername());
//
//                return pendingOrderService.cancelPendingOrder(orderId, user.getId())
//                        .map(cancelledOrder -> ResponseEntity.ok(cancelledOrder))
//                        .orElse(ResponseEntity.notFound().build());
//            } catch (Exception e) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("An error occurred while cancelling the pending order: " + e.getMessage());
//            }
//        }
//
//        private String getAuthenticatedUserEmail() {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            return authentication.getName();
//        }
//    }

package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.PendingOrderDTO;
import com.twd.BfiTradingApplication.entity.User;
import com.twd.BfiTradingApplication.service.PendingOrderService;
import com.twd.BfiTradingApplication.service.TradingService;
import com.twd.BfiTradingApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pending-orders")
public class PendingOrderController {
    @Autowired
    private UserService userService;

    private final PendingOrderService pendingOrderService;
    private final TradingService tradingService;

    @Autowired
    public PendingOrderController(PendingOrderService pendingOrderService, TradingService tradingService) {
        this.pendingOrderService = pendingOrderService;
        this.tradingService = tradingService;
    }

    @PostMapping
    public ResponseEntity<?> createPendingOrder(@RequestBody PendingOrderDTO pendingOrderDTO, Authentication authentication) {
        try {
            String userMail = getAuthenticatedUserEmail();
            User user = userService.getUserByEmail(userMail);

            PendingOrderDTO createdOrder = pendingOrderService.createPendingOrder(pendingOrderDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the pending order: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PendingOrderDTO>> getPendingOrders(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = tradingService.getUserByEmail(userDetails.getUsername());

            List<PendingOrderDTO> pendingOrders = pendingOrderService.getPendingOrdersByUser(user.getId());
            return ResponseEntity.ok(pendingOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/cancelled")
    public ResponseEntity<List<PendingOrderDTO>> getCancelledOrders(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = tradingService.getUserByEmail(userDetails.getUsername());

            List<PendingOrderDTO> pendingOrders = pendingOrderService.getCancelledOrdersByUser(user.getId());
            return ResponseEntity.ok(pendingOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/expired")
    public ResponseEntity<List<PendingOrderDTO>> getExpiredOrders(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = tradingService.getUserByEmail(userDetails.getUsername());

            List<PendingOrderDTO> pendingOrders = pendingOrderService.getExpiredOrdersByUser(user.getId());
            return ResponseEntity.ok(pendingOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> cancelPendingOrder(@PathVariable Integer orderId, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = tradingService.getUserByEmail(userDetails.getUsername());

            return pendingOrderService.cancelPendingOrder(orderId, user.getId())
                    .map(cancelledOrder -> ResponseEntity.ok(cancelledOrder))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while cancelling the pending order: " + e.getMessage());
        }
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}