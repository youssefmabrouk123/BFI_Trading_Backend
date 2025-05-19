package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.*;
import com.twd.BfiTradingApplication.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardAdminService {

    private final UserRepository userRepository;
    private final CrossParityRepository crossParityRepository;
    private final CurrencyRepository currencyRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final PositionRepository positionRepository;

    @Autowired
    public DashboardAdminService(
            UserRepository userRepository,
            CrossParityRepository crossParityRepository,
            CurrencyRepository currencyRepository,
            CounterpartyRepository counterpartyRepository,
            PositionRepository positionRepository) {
        this.userRepository = userRepository;
        this.crossParityRepository = crossParityRepository;
        this.currencyRepository = currencyRepository;
        this.counterpartyRepository = counterpartyRepository;
        this.positionRepository = positionRepository;
    }

    // User Management
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public User updateUser(Integer id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // CrossParity Management
    @PreAuthorize("hasRole('ADMIN')")
    public List<CrossParity> getAllCrossParities() {
        return crossParityRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<CrossParity> getCrossParityById(Integer id) {
        return crossParityRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CrossParity createCrossParity(CrossParity crossParity) {
        if (crossParity.getIdentifier() == null || crossParity.getIdentifier().isEmpty()) {
            throw new IllegalArgumentException("Identifier is required");
        }
        if (crossParityRepository.findByIdentifier(crossParity.getIdentifier())!=null) {
            throw new IllegalArgumentException("Identifier already exists");
        }
        if (crossParity.getBaseCurrency() == null || crossParity.getQuoteCurrency() == null) {
            throw new IllegalArgumentException("Base and quote currencies are required");
        }
        if (crossParity.getRate() == null) {
            throw new IllegalArgumentException("Rate is required");
        }
        if (crossParity.getQuotity() == null) {
            throw new IllegalArgumentException("Quotity is required");
        }
        return crossParityRepository.save(crossParity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CrossParity updateCrossParity(Integer id, CrossParity crossParityDetails) {
        CrossParity crossParity = crossParityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CrossParity not found with ID: " + id));
        if (crossParityDetails.getIdentifier() != null && !crossParityDetails.getIdentifier().equals(crossParity.getIdentifier())) {
            if (crossParityRepository.findByIdentifier(crossParityDetails.getIdentifier())!=null) {
                throw new IllegalArgumentException("Identifier already exists");
            }
            crossParity.setIdentifier(crossParityDetails.getIdentifier());
        }
        if (crossParityDetails.getDescription() != null) {
            crossParity.setDescription(crossParityDetails.getDescription());
        }
        if (crossParityDetails.getBaseCurrency() != null) {
            crossParity.setBaseCurrency(crossParityDetails.getBaseCurrency());
        }
        if (crossParityDetails.getQuoteCurrency() != null) {
            crossParity.setQuoteCurrency(crossParityDetails.getQuoteCurrency());
        }
        if (crossParityDetails.getRate() != null) {
            crossParity.setRate(crossParityDetails.getRate());
        }
        if (crossParityDetails.getQuotity() != null) {
            crossParity.setQuotity(crossParityDetails.getQuotity());
        }
        return crossParityRepository.save(crossParity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteCrossParity(Integer id) {
        if (!crossParityRepository.existsById(id)) {
            throw new IllegalArgumentException("CrossParity not found with ID: " + id);
        }
        crossParityRepository.deleteById(id);
    }

    // Currency Management
    @PreAuthorize("hasRole('ADMIN')")
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Currency> getCurrencyById(Integer id) {
        return currencyRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Currency createCurrency(Currency currency) {
        if (currency.getIdentifier() == null || currency.getIdentifier().isEmpty()) {
            throw new IllegalArgumentException("Identifier is required");
        }
        if (currencyRepository.findByIdentifier(currency.getIdentifier()).isPresent()) {
            throw new IllegalArgumentException("Identifier already exists");
        }
        if (currency.getDescription() == null) {
            throw new IllegalArgumentException("Description is required");
        }
        return currencyRepository.save(currency);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Currency updateCurrency(Integer id, Currency currencyDetails) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found with ID: " + id));
        if (currencyDetails.getIdentifier() != null && !currencyDetails.getIdentifier().equals(currency.getIdentifier())) {
            if (currencyRepository.findByIdentifier(currencyDetails.getIdentifier()).isPresent()) {
                throw new IllegalArgumentException("Identifier already exists");
            }
            currency.setIdentifier(currencyDetails.getIdentifier());
        }
        if (currencyDetails.getDescription() != null) {
            currency.setDescription(currencyDetails.getDescription());
        }
        if (currencyDetails.getNbrDec() != null) {
            currency.setNbrDec(currencyDetails.getNbrDec());
        }
        return currencyRepository.save(currency);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteCurrency(Integer id) {
        if (!currencyRepository.existsById(id)) {
            throw new IllegalArgumentException("Currency not found with ID: " + id);
        }
        currencyRepository.deleteById(id);
    }

    // Counterparty Management
    @PreAuthorize("hasRole('ADMIN')")
    public List<Counterparty> getAllCounterparties() {
        return counterpartyRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Counterparty> getCounterpartyById(Integer id) {
        return counterpartyRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Counterparty createCounterparty(Counterparty counterparty) {
        if (counterparty.getName() == null || counterparty.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        return counterpartyRepository.save(counterparty);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Counterparty updateCounterparty(Integer id, Counterparty counterpartyDetails) {
        Counterparty counterparty = counterpartyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Counterparty not found with ID: " + id));
        if (counterpartyDetails.getName() != null) {
            counterparty.setName(counterpartyDetails.getName());
        }
        if (counterpartyDetails.getShortName() != null) {
            counterparty.setShortName(counterpartyDetails.getShortName());
        }
        if (counterpartyDetails.getType() != null) {
            counterparty.setType(counterpartyDetails.getType());
        }
        if (counterpartyDetails.getCountry() != null) {
            counterparty.setCountry(counterpartyDetails.getCountry());
        }
        return counterpartyRepository.save(counterparty);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteCounterparty(Integer id) {
        if (!counterpartyRepository.existsById(id)) {
            throw new IllegalArgumentException("Counterparty not found with ID: " + id);
        }
        counterpartyRepository.deleteById(id);
    }

    // Position Management
    @PreAuthorize("hasRole('ADMIN')")
    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Position> getPositionById(Integer id) {
        return positionRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Position createPosition(Position position) {
        if (position.getCurrency() == null) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (position.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }
        if (position.getMntDev() == null) {
            position.setMntDev(java.math.BigDecimal.ZERO);
        }
        if (position.getBesoinDev() == null) {
            position.setBesoinDev(java.math.BigDecimal.ZERO);
        }
        return positionRepository.save(position);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Position updatePosition(Integer id, Position positionDetails) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Position not found with ID: " + id));
        if (positionDetails.getCurrency() != null) {
            position.setCurrency(positionDetails.getCurrency());
        }
        if (positionDetails.getUser() != null) {
            position.setUser(positionDetails.getUser());
        }
        if (positionDetails.getMntDev() != null) {
            position.setMntDev(positionDetails.getMntDev());
        }
        if (positionDetails.getBesoinDev() != null) {
            position.setBesoinDev(positionDetails.getBesoinDev());
        }
        return positionRepository.save(position);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deletePosition(Integer id) {
        if (!positionRepository.existsById(id)) {
            throw new IllegalArgumentException("Position not found with ID: " + id);
        }
        positionRepository.deleteById(id);
    }
}