package co.improsvita.domain.service;

import co.improsvita.domain.model.MovementType;
import co.improsvita.domain.model.SeedLot;
import co.improsvita.domain.model.SeedLotStatus;
import co.improsvita.domain.model.SeedMovement;
import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.SeedLotRepository;
import co.improsvita.domain.repository.SeedMovementRepository;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.domain.repository.ReservationRepository;
import co.improsvita.domain.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class SowingService {

    private static final Logger log = LoggerFactory.getLogger(SowingService.class);

    private static final Set<SowingStatus> TERMINAL = Set.of(
            SowingStatus.COMPLETED, SowingStatus.FAILED, SowingStatus.CANCELLED);

    private final SowingRepository sowingRepository;
    private final SeedMovementRepository seedMovementRepository;
    private final SeedLotRepository seedLotRepository;
    private final VisitRepository visitRepository;
    private final ReservationRepository reservationRepository;

    public SowingService(SowingRepository sowingRepository,
                         SeedMovementRepository seedMovementRepository,
                         SeedLotRepository seedLotRepository,
                         VisitRepository visitRepository,
                         ReservationRepository reservationRepository) {
        this.sowingRepository = sowingRepository;
        this.seedMovementRepository = seedMovementRepository;
        this.seedLotRepository = seedLotRepository;
        this.visitRepository = visitRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Sowing> getAllSowings() {
        return sowingRepository.getAll();
    }

    public Sowing getSowingById(Integer id) {
        return sowingRepository.getById(id);
    }

    public List<Sowing> getSowingsByLot(Integer lotId) {
        return sowingRepository.getByLotId(lotId);
    }

    public List<Sowing> getSowingsByBed(Integer bedId) {
        return sowingRepository.getByBedId(bedId);
    }

    public List<Sowing> getSowingsByStatus(SowingStatus status) {
        return sowingRepository.getByStatus(status);
    }

    public List<Sowing> getSowingsByBedCode(String code) {
        return sowingRepository.getByBedCode(code);
    }

    public Sowing changeStatus(Integer id, SowingStatus status) {
        Sowing sowing = requireSowing(id);
        SowingStatus previous = sowing.getStatus();
        if (TERMINAL.contains(previous) && previous != status) {
            log.warn("Cambio de estado RECHAZADO en siembra id={}: {} -> {} (usuario={}). El estado actual es terminal",
                    id, previous, status, currentUser());
            throw new IllegalStateException("La siembra " + id + " está en estado terminal " + previous + " y no admite cambios");
        }
        sowing.setStatus(status);
        Sowing saved = sowingRepository.save(sowing);
        if (previous != status) {
            log.info("Cambio de estado en siembra id={}: {} -> {} (usuario={})", id, previous, status, currentUser());
        }
        return saved;
    }

    /**
     * Borra una siembra devolviendo al lote el stock que había consumido.
     *
     * Registrar una siembra descuenta stock y crea un movimiento EXIT ligado a ella.
     * Antes de este cambio, borrarla dejaba el stock consumido y el movimiento huérfano
     * en el kardex para siempre.
     */
    @Transactional
    public void deleteSowingById(Integer id) {
        Sowing sowing = requireSowing(id);

        if (!visitRepository.getBySowingId(id).isEmpty()) {
            throw new IllegalStateException("La siembra " + id
                    + " tiene visitas registradas. Elimina primero sus visitas.");
        }

        if (!reservationRepository.getBySowingId(id).isEmpty()) {
            throw new IllegalStateException("La siembra " + id
                    + " tiene reservas registradas. Elimina primero sus reservas.");
        }

        List<SeedMovement> exits = seedMovementRepository.getBySowingId(id).stream()
                .filter(movement -> movement.getMovementType() == MovementType.EXIT)
                .toList();

        int consumed = exits.stream()
                .mapToInt(SeedMovement::getQuantity)
                .sum();

        if (consumed > 0) {
            restoreStock(sowing.getLotId(), consumed);
            seedMovementRepository.save(reversalMovement(sowing.getLotId(), consumed, id));
            seedMovementRepository.detachFromSowing(id);
        }

        sowingRepository.deleteById(id);
    }

    private void restoreStock(Integer lotId, int consumed) {
        SeedLot lot = seedLotRepository.getById(lotId);
        if (lot == null) {
            return;
        }
        lot.setAvailableQuantity(lot.getAvailableQuantity() + consumed);
        if (lot.getStatus() == SeedLotStatus.DEPLETED) {
            lot.setStatus(SeedLotStatus.AVAILABLE);
        }
        seedLotRepository.save(lot);
    }

    private SeedMovement reversalMovement(Integer lotId, int consumed, Integer sowingId) {
        SeedMovement reversal = new SeedMovement();
        reversal.setLotId(lotId);
        reversal.setSupplierId(null);
        reversal.setMovementType(MovementType.ADJUSTMENT);
        reversal.setQuantity(consumed);
        reversal.setMovementDate(LocalDateTime.now());
        reversal.setReason("Reversión por eliminación de siembra " + sowingId);
        return reversal;
    }

    private Sowing requireSowing(Integer id) {
        Sowing sowing = sowingRepository.getById(id);
        if (sowing == null) {
            throw new IllegalArgumentException("Siembra no encontrada: " + id);
        }
        return sowing;
    }

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "sistema";
    }
}
