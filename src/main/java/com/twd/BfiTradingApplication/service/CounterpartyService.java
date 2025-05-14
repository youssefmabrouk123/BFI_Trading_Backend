package com.twd.BfiTradingApplication.service;

import com.twd.BfiTradingApplication.entity.Counterparty;
import com.twd.BfiTradingApplication.repository.CounterpartyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CounterpartyService {

    private final CounterpartyRepository repository;

    public CounterpartyService(CounterpartyRepository repository) {
        this.repository = repository;
    }

    public List<Counterparty> getAllCounterparties() {
        return repository.findAll();
    }

    public List<String> getAllShortNames() {
        return repository.findAll()
                .stream()
                .map(Counterparty::getShortName)
                .collect(Collectors.toList());
    }

    public Optional<Counterparty> getCounterpartyById(Integer id) {
        return repository.findById(id);
    }

    public Counterparty addCounterparty(Counterparty counterparty) {
        return repository.save(counterparty);
    }

    public void deleteCounterparty(Integer id) {
        repository.deleteById(id);
    }
}
