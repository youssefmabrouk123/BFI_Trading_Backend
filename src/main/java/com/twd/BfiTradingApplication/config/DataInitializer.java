package com.twd.BfiTradingApplication.config;


import com.twd.BfiTradingApplication.entity.Counterparty;
import com.twd.BfiTradingApplication.repository.CounterpartyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCounterparties(CounterpartyRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                List<Counterparty> counterparties = List.of(
                        newCounterparty("Société Tunisienne de Banque", "STB", "Banque", "Tunisie"),
                        newCounterparty("Banque Nationale Agricole", "BNA", "Banque", "Tunisie"),
                        newCounterparty("BIAT", "BIAT", "Banque", "Tunisie"),
                        newCounterparty("Amen Bank", "AMEN", "Banque", "Tunisie"),
                        newCounterparty("Attijari Bank", "ATTIJARI", "Banque", "Tunisie"),
                        newCounterparty("UBCI", "UBCI", "Banque", "Tunisie"),
                        newCounterparty("UIB", "UIB", "Banque", "Tunisie"),
                        newCounterparty("Banque de Tunisie", "BT", "Banque", "Tunisie"),
                        newCounterparty("Citibank Tunisie", "CITI", "Banque", "USA"),
                        newCounterparty("QNB Tunisie", "QNB", "Banque", "Qatar"),
                        newCounterparty("Bank ABC Tunisie", "ABC", "Banque", "Bahreïn"),
                        newCounterparty("Tunis International Bank", "TIB", "Banque", "Tunisie"),
                        newCounterparty("Banque Zitouna", "ZITOUNA", "Banque Islamique", "Tunisie"),
                        newCounterparty("Al Baraka Bank", "ALBARAKA", "Banque Islamique", "Tunisie"),
                        newCounterparty("TSB", "TSB", "Banque", "Tunisie"),
                        newCounterparty("BTE", "BTE", "Banque", "Tunisie"),
                        newCounterparty("BTK", "BTK", "Banque", "Tunisie"),
                        newCounterparty("BFT", "BFT", "Banque", "Tunisie"),
                        newCounterparty("Banque Africaine de Tunisie", "BAT", "Banque", "Tunisie"),
                        newCounterparty("Banque Franco-Tunisienne", "BFT", "Banque", "Tunisie"),
                        newCounterparty("Client XYZ SARL", "XYZ", "Client", "Tunisie"),
                        newCounterparty("Client ABC Corp", "ABC", "Client", "France"),
                        newCounterparty("Interactive Brokers", "IBKR", "Broker", "USA"),
                        newCounterparty("Saxo Bank", "SAXO", "Broker", "Danemark"),
                        newCounterparty("OANDA", "OANDA", "Broker", "Canada"),
                        newCounterparty("IG Markets", "IG", "Broker", "Royaume-Uni"),
                        newCounterparty("FXCM", "FXCM", "Broker", "Royaume-Uni"),
                        newCounterparty("Exness", "EXNESS", "Broker", "Chypre"),
                        newCounterparty("Pepperstone", "PEPPER", "Broker", "Australie"),
                        newCounterparty("CMC Markets", "CMC", "Broker", "Royaume-Uni"),
                        newCounterparty("XM", "XM", "Broker", "Chypre"),
                        newCounterparty("FBS", "FBS", "Broker", "Belize"),
                        newCounterparty("OctaFX", "OCTA", "Broker", "Saint Vincent"),
                        newCounterparty("Swissquote", "SWISS", "Broker", "Suisse"),
                        newCounterparty("Dukascopy", "DUKA", "Broker", "Suisse"),
                        newCounterparty("Banque Centrale de Tunisie", "BCT", "Banque Centrale", "Tunisie"),
                        newCounterparty("Tunisian Foreign Bank", "TFB", "Banque", "France"),
                        newCounterparty("Banque Nord Africaine Internationale", "BNAI", "Banque", "Tunisie"),
                        newCounterparty("Crédit Agricole Tunisie", "CAT", "Banque", "France"),
                        newCounterparty("HSBC Middle East", "HSBC", "Banque", "Émirats"),
                        newCounterparty("Barclays ME", "BARCLAYS", "Banque", "Royaume-Uni"),
                        newCounterparty("Banque Islamique de Développement", "BID", "Banque de développement", "Arabie Saoudite"),
                        newCounterparty("European Investment Bank", "EIB", "Institution", "UE"),
                        newCounterparty("Banque Mondiale", "WB", "Institution", "USA"),
                        newCounterparty("Bloomberg", "BBG", "Data Provider", "USA"),
                        newCounterparty("Reuters", "REUTERS", "Data Provider", "Royaume-Uni"),
                        newCounterparty("TradingView", "TV", "Data Provider", "USA")
                );
                repo.saveAll(counterparties);
                System.out.println("Contreparties initialisées.");
            }
        };
    }

    private Counterparty newCounterparty(String name, String shortName, String type, String country) {
        Counterparty cp = new Counterparty();
        cp.setName(name);
        cp.setShortName(shortName);
        cp.setType(type);
        cp.setCountry(country);
        return cp;
    }
}
