//package com.twd.BfiTradingApplication.service;
//
//import com.corundumstudio.socketio.SocketIOServer;
//import com.twd.BfiTradingApplication.dto.PositionDTO;
//import com.twd.BfiTradingApplication.entity.Currency;
//import com.twd.BfiTradingApplication.entity.Position;
//import com.twd.BfiTradingApplication.repository.CurrencyRepository;
//import com.twd.BfiTradingApplication.repository.PositionRepository;
//import jakarta.annotation.PostConstruct;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class PositionService {
//    private static final Logger logger = LoggerFactory.getLogger(TradingService.class);
//
//
//    @Autowired
//    private PositionRepository positionRepository;
//
//    @Autowired
//    private CurrencyRepository currencyRepository;
//
//    @Autowired
//    private SocketIOServer socketIOServer;
//
//    private List<Position> positionList;
//
//    @PostConstruct
//    public void init() {
//        // Charger toutes les positions au démarrage
//        positionList = positionRepository.findAll();
//        broadcastPositions();
//
//        // Envoyer les positions initiales à chaque nouveau client
//        socketIOServer.addConnectListener(client -> {
//            logger.info("Client connected: {}", client.getSessionId());
//            client.sendEvent("positionsUpdate", positionList); // Envoi initial au client spécifique
//        });
//
//        // Écouter une demande explicite de positions (optionnel)
//        socketIOServer.addEventListener("requestPositions", String.class, (client, data, ackSender) -> {
//            logger.info("Positions requested by client: {}", client.getSessionId());
//            client.sendEvent("positionsUpdate", positionList);
//        });
//    }
//
//
//    // Conversion Entité -> DTO
//    private PositionDTO toDTO(Position position) {
//        PositionDTO dto = new PositionDTO();
//        dto.setPk(position.getPk());
//        dto.setIdentifier(position.getCurrency().getIdentifier()); // Utilise identifier
//        dto.setMntDev(position.getMntDev());
//        dto.setBesoinDev(position.getBesoinDev());
//        return dto;
//    }
//
//    // Conversion DTO -> Entité
//    private Position toEntity(PositionDTO dto) {
//        Position position = new Position();
//        position.setPk(dto.getPk());
//        Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
//                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable pour l'identifiant : " + dto.getIdentifier()));
//        position.setCurrency(currency);
//        position.setMntDev(dto.getMntDev() != null ? dto.getMntDev() : BigDecimal.ZERO);
//        position.setBesoinDev(dto.getBesoinDev() != null ? dto.getBesoinDev() : BigDecimal.ZERO);
//
//        return position;
//    }
//
//    public List<PositionDTO> getAllPositions() {
//        return positionRepository.findAll().stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public Optional<PositionDTO> getPositionById(Integer id) {
//        return positionRepository.findById(id).map(this::toDTO);
//    }
//
//    public PositionDTO createPosition(PositionDTO positionDTO) {
//        Currency currency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
//                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable pour l'identifiant : " + positionDTO.getIdentifier()));
//
//        if (positionRepository.existsByCurrencyId(currency.getPk())) {
//            throw new IllegalStateException("Cette devise a déjà une position associée");
//        }
//
//        Position position = toEntity(positionDTO);
//        Position savedPosition = positionRepository.save(position);
//        broadcastPositions();
//        return toDTO(savedPosition);
//    }
//
//    // Nouvelle méthode pour créer une liste de positions
//    public List<PositionDTO> createPositions(List<PositionDTO> positionDTOs) {
//        return positionDTOs.stream().map(dto -> {
//            Currency currency = currencyRepository.findByIdentifier(dto.getIdentifier())
//                    .orElseThrow(() -> new IllegalArgumentException("Devise introuvable pour l'identifiant : " + dto.getIdentifier()));
//
//            if (positionRepository.existsByCurrencyId(currency.getPk())) {
//                throw new IllegalStateException("La devise avec l'identifiant " + dto.getIdentifier() + " a déjà une position associée");
//            }
//
//            Position position = toEntity(dto);
//            Position savedPosition = positionRepository.save(position);
//            broadcastPositions();
//            return toDTO(savedPosition);
//        }).collect(Collectors.toList());
//    }
//
//    public Optional<PositionDTO> updatePosition(Integer id, PositionDTO positionDTO) {
//        return positionRepository.findById(id).map(position -> {
//            Currency newCurrency = currencyRepository.findByIdentifier(positionDTO.getIdentifier())
//                    .orElseThrow(() -> new IllegalArgumentException("Devise introuvable pour l'identifiant : " + positionDTO.getIdentifier()));
//
//            if (!position.getCurrency().getPk().equals(newCurrency.getPk()) &&
//                    positionRepository.existsByCurrencyId(newCurrency.getPk())) {
//                throw new IllegalStateException("La nouvelle devise a déjà une position associée");
//            }
//
//            position.setCurrency(newCurrency);
//            position.setMntDev(positionDTO.getMntDev() != null ? positionDTO.getMntDev() : BigDecimal.ZERO);
//            position.setBesoinDev(positionDTO.getBesoinDev() != null ? positionDTO.getBesoinDev() : BigDecimal.ZERO);
//            Position updatedPosition = positionRepository.save(position);
//            broadcastPositions();
//            return toDTO(updatedPosition);
//        });
//    }
//
//    public boolean deletePosition(Integer id) {
//        if (positionRepository.existsById(id)) {
//            positionRepository.deleteById(id);
//            broadcastPositions();
//            return true;
//        }
//        broadcastPositions();
//        return false;
//    }
//
//    private void broadcastPositions() {
//        try {
//            socketIOServer.getBroadcastOperations().sendEvent("positionsUpdate", positionList);
//            logger.debug("Broadcasted {} positions", positionList.size());
//        } catch (Exception e) {
//            logger.error("Error broadcasting positions", e);
//        }
//    }
//}