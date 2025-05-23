//package com.twd.BfiTradingApplication.controller;
//
//import com.twd.BfiTradingApplication.dto.ApiResponse;
//import com.twd.BfiTradingApplication.dto.PositionDTO;
//import com.twd.BfiTradingApplication.entity.Currency;
//import com.twd.BfiTradingApplication.entity.Position;
//import com.twd.BfiTradingApplication.entity.Transaction;
//import com.twd.BfiTradingApplication.entity.User;
//import com.twd.BfiTradingApplication.exception.TradingException;
//import com.twd.BfiTradingApplication.repository.CurrencyRepository;
//import com.twd.BfiTradingApplication.service.CurrencyService;
//import com.twd.BfiTradingApplication.service.TradingService;
//import com.twd.BfiTradingApplication.service.TransactionService;
//import com.twd.BfiTradingApplication.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@RestController
//@RequestMapping("/public/api/trading")
//public class TradingController {
//
//    @Autowired
//    private TransactionService transactionService;
//
//    @Autowired
//    private TradingService tradingService;
//
//    @Autowired
//    private CurrencyService currencyService;
//
//    @Autowired
//    private CurrencyRepository currencyRepository;
//
//    @Autowired
//    private UserService userService;
//
//
//    @PostMapping("/trade")
//    public ResponseEntity<?> executeTrade(
//            @RequestParam String crossParity,  // Changed to single string parameter
//            @RequestParam BigDecimal mntAcht,
//            @RequestParam String transactionType,
//            @RequestParam BigDecimal marketPrice) {
//        try {
//
//            String userMail=getAuthenticatedUserEmail();
//            User user = userService.getUserByEmail(userMail);
//            // Split crossParity into base and quote identifiers
//            String[] currencies = crossParity.split("/");
//            if (currencies.length != 2) {
//                throw new TradingException("Invalid crossParity format. Expected format: 'BASE/QUOTE' (e.g., 'EUR/USD')");
//            }
//            String baseIdentifier = currencies[0];
//            String quoteIdentifier = currencies[1];
//
//            // Fetch currency IDs based on identifiers
//            Currency baseCurrency = currencyRepository.findByIdentifier(baseIdentifier)
//                    .orElseThrow(() -> new TradingException("Base currency not found: " + baseIdentifier));
//            Currency quoteCurrency = currencyRepository.findByIdentifier(quoteIdentifier)
//                    .orElseThrow(() -> new TradingException("Quote currency not found: " + quoteIdentifier));
//
//            Integer baseCurrencyId = baseCurrency.getPk();
//            Integer quoteCurrencyId = quoteCurrency.getPk();
//
//            // Call the service with the extracted IDs
//            Transaction transaction = tradingService.executeTrade(
//                   user.getId(), baseCurrencyId, quoteCurrencyId, mntAcht, transactionType, marketPrice);
//
//            return ResponseEntity.ok().body(new ApiResponse(true,
//                    String.format("Trade successful: %s %s of %s executed at market price %s.",
//                            transactionType, transaction.getMntAcht(),
//                            transaction.getDevAchn().getIdentifier(), transaction.getPrice())
//            ));
//
//        } catch (TradingException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse(false, "An unexpected error occurred. Please contact support if the problem persists."));
//        }
//    }
//
//
//    @PostMapping("/needs")
//    public ResponseEntity<Void> setDailyNeeds(
//            @RequestParam Integer currencyId,
//            @RequestParam BigDecimal besoinDev) {
//        String userMail=getAuthenticatedUserEmail();
//        User user = userService.getUserByEmail(userMail);
//        tradingService.setDailyNeeds(currencyId, besoinDev, user);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/position/{currencyId}")
//    public ResponseEntity<BigDecimal> getPosition(@PathVariable Integer currencyId) {
//        return ResponseEntity.ok(tradingService.getPositionValue(currencyId));
//    }
//
//    @GetMapping("/positions/needs")
//    public ResponseEntity<List<Position>> getPositionsWithNeeds() {
//        return ResponseEntity.ok(tradingService.getPositionsWithNeeds());
//    }
//
//
//    @PostMapping("/bulk")
//    public ResponseEntity<List<PositionDTO>> createPositions(@RequestBody List<PositionDTO> positionDTOs) {
//        String userMail=getAuthenticatedUserEmail();
//        User user = userService.getUserByEmail(userMail);
//
//        List<PositionDTO> createdPositions = tradingService.createPositions(positionDTOs, user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdPositions);
//    }
//
//    @GetMapping("/transactions")
//    public List<Transaction> getAllTransactions() {
//        return transactionService.getAllTransactions();
//    }
//
//
//    private Integer getAuthenticatedUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        User user = userService.getUserByEmail(email);
//        return user.getId();
//    }
//
//    private String getAuthenticatedUserEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication.getName(); // Extract email from token
//    }
//}

//
//
//package com.twd.BfiTradingApplication.controller;
//
//import com.twd.BfiTradingApplication.dto.ApiResponse;
//import com.twd.BfiTradingApplication.dto.PositionDTO;
//import com.twd.BfiTradingApplication.entity.Currency;
//import com.twd.BfiTradingApplication.entity.Position;
//import com.twd.BfiTradingApplication.entity.Transaction;
//import com.twd.BfiTradingApplication.entity.User;
//import com.twd.BfiTradingApplication.exception.TradingException;
//import com.twd.BfiTradingApplication.repository.CurrencyRepository;
//import com.twd.BfiTradingApplication.service.CurrencyService;
//import com.twd.BfiTradingApplication.service.TradingService;
//import com.twd.BfiTradingApplication.service.TransactionService;
//import com.twd.BfiTradingApplication.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/public/api/trading")
//public class TradingController {
//
//    @Autowired
//    private TransactionService transactionService;
//
//    @Autowired
//    private TradingService tradingService;
//
//    @Autowired
//    private CurrencyService currencyService;
//
//    @Autowired
//    private CurrencyRepository currencyRepository;
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/trade")
//    public ResponseEntity<ApiResponse> executeTrade(
//            @RequestParam String crossParity,
//            @RequestParam BigDecimal mntAcht,
//            @RequestParam BigDecimal mntVen,
//            @RequestParam String transactionType,
//            @RequestParam BigDecimal price,
//            @RequestParam Integer counterpartyId,
//            @RequestParam String valueDate) {
//        try {
//            // Get authenticated user's email
//            String userMail = getAuthenticatedUserEmail();
//            User user = userService.getUserByEmail(userMail);
//
//            // Parse valueDate to LocalDate
//            LocalDate parsedValueDate = LocalDate.parse(valueDate);
//
//            // Call the service method
//
//            Transaction response = tradingService.executeTrade(
//                     user.getId(),  crossParity, mntAcht, mntVen, transactionType, price, counterpartyId);
//
//            // Return success response
//            return ResponseEntity.ok(new ApiResponse(true, "success"));
//
//        } catch (TradingException e) {
//            // Handle known exceptions with a 400 response
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage()));
//        } catch (Exception e) {
//            // Handle unexpected errors with a 500 response
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse(false, "An unexpected error occurred. Please contact support if the problem persists."));
//        }
//    }
//
//
//
//    @PostMapping("/needs")
//    public ResponseEntity<Void> setDailyNeeds(
//            @RequestParam Integer currencyId,
//            @RequestParam BigDecimal besoinDev) {
//        String userMail = getAuthenticatedUserEmail();
//        User user = userService.getUserByEmail(userMail);
//        tradingService.setDailyNeeds(currencyId, besoinDev, user);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/position/{currencyId}")
//    public ResponseEntity<BigDecimal> getPosition(@PathVariable Integer currencyId) {
//        return ResponseEntity.ok(tradingService.getPositionValue(currencyId));
//    }
//
//    @GetMapping("/positions/needs")
//    public ResponseEntity<List<Position>> getPositionsWithNeeds() {
//        return ResponseEntity.ok(tradingService.getPositionsWithNeeds());
//    }
//
//    @PostMapping("/bulk")
//    public ResponseEntity<List<PositionDTO>> createPositions(@RequestBody List<PositionDTO> positionDTOs) {
//        String userMail = getAuthenticatedUserEmail();
//        User user = userService.getUserByEmail(userMail);
//        List<PositionDTO> createdPositions = tradingService.createPositions(positionDTOs, user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdPositions);
//    }
//
//    @GetMapping("/transactions")
//    public List<Transaction> getAllTransactions() {
//        return transactionService.getAllTransactions();
//    }
//
//    private Integer getAuthenticatedUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//        User user = userService.getUserByEmail(email);
//        return user.getId();
//    }
//
//    private String getAuthenticatedUserEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication.getName();
//    }
//}



package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.ApiResponse;
import com.twd.BfiTradingApplication.dto.PositionDTO;
import com.twd.BfiTradingApplication.entity.Currency;
import com.twd.BfiTradingApplication.entity.Position;
import com.twd.BfiTradingApplication.entity.Transaction;
import com.twd.BfiTradingApplication.entity.User;
import com.twd.BfiTradingApplication.exception.TradingException;
import com.twd.BfiTradingApplication.repository.CurrencyRepository;
import com.twd.BfiTradingApplication.service.CurrencyService;
import com.twd.BfiTradingApplication.service.TradingService;
import com.twd.BfiTradingApplication.service.TransactionService;
import com.twd.BfiTradingApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/public/api/trading")
public class TradingController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TradingService tradingService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/trade")
    public ResponseEntity<ApiResponse> executeTrade(
            @RequestParam String crossParity,
            @RequestParam BigDecimal mntAcht,
            @RequestParam BigDecimal mntVen,
            @RequestParam String transactionType,
            @RequestParam BigDecimal price,
            @RequestParam Integer counterpartyId,
            @RequestParam String valueDate) {
        try {
            String userMail = getAuthenticatedUserEmail();
            User user = userService.getUserByEmail(userMail);

            Transaction response = tradingService.executeTrade(
                    user.getId(), crossParity, mntAcht, mntVen, transactionType, price, counterpartyId , LocalDate.parse(valueDate));

            return ResponseEntity.ok(new ApiResponse(true, String.format(
                    "Trade successful: %s %s of %s executed at price %s",
                    transactionType, mntAcht, crossParity, price)));

        } catch (TradingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred. Please contact support if the problem persists."));
        }
    }

    @PostMapping("/needs")
    public ResponseEntity<Void> setDailyNeeds(
            @RequestParam Integer currencyId,
            @RequestParam BigDecimal besoinDev) {
        String userMail = getAuthenticatedUserEmail();
        User user = userService.getUserByEmail(userMail);
        tradingService.setDailyNeeds(currencyId, besoinDev, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/position/{currencyId}")
    public ResponseEntity<BigDecimal> getPosition(@PathVariable Integer currencyId) {
        return ResponseEntity.ok(tradingService.getPositionValue(currencyId));
    }

    @GetMapping("/positions/needs")
    public ResponseEntity<List<Position>> getPositionsWithNeeds() {
        return ResponseEntity.ok(tradingService.getPositionsWithNeeds());
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<PositionDTO>> createPositions(@RequestBody List<PositionDTO> positionDTOs) {
        String userMail = getAuthenticatedUserEmail();
        User user = userService.getUserByEmail(userMail);
        List<PositionDTO> createdPositions = tradingService.createPositions(positionDTOs, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPositions);
    }

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    private Integer getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        return user.getId();
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}