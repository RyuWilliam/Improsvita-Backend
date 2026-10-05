package co.improsvita.domain.service;

import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;
import co.improsvita.domain.repository.SowingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class SowingService {

    private static final Set<SowingStatus> TERMINAL = Set.of(
            SowingStatus.COMPLETED, SowingStatus.FAILED, SowingStatus.CANCELLED);

    private final SowingRepository sowingRepository;

    public SowingService(SowingRepository sowingRepository) {
        this.sowingRepository = sowingRepository;
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

    public Sowing updateGermination(Integer id, BigDecimal germinatedQuantity) {
        Sowing sowing = requireSowing(id);
        if (germinatedQuantity == null || germinatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad germinada debe ser mayor o igual a cero");
        }
        if (sowing.getQuantitySown() != null && germinatedQuantity.compareTo(sowing.getQuantitySown()) > 0) {
            throw new IllegalArgumentException("La cantidad germinada no puede superar lo sembrado (" + sowing.getQuantitySown() + ")");
        }
        sowing.setGerminatedQuantity(germinatedQuantity);
        return sowingRepository.save(sowing);
    }

    public Sowing changeStatus(Integer id, SowingStatus status) {
        Sowing sowing = requireSowing(id);
        if (TERMINAL.contains(sowing.getStatus()) && sowing.getStatus() != status) {
            throw new IllegalStateException("La siembra " + id + " está en estado terminal " + sowing.getStatus() + " y no admite cambios");
        }
        sowing.setStatus(status);
        return sowingRepository.save(sowing);
    }

    public void deleteSowingById(Integer id) {
        sowingRepository.deleteById(id);
    }

    private Sowing requireSowing(Integer id) {
        Sowing sowing = sowingRepository.getById(id);
        if (sowing == null) {
            throw new IllegalArgumentException("Siembra no encontrada: " + id);
        }
        return sowing;
    }
}