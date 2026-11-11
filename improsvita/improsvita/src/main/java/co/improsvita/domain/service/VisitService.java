package co.improsvita.domain.service;

import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.Visit;
import co.improsvita.domain.model.VisitMethod;
import co.improsvita.domain.model.VisitResult;
import co.improsvita.domain.repository.SowingRepository;
import co.improsvita.domain.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VisitService {

    private static final int MAX_NOTES_LENGTH = 500;

    private final VisitRepository visitRepository;
    private final SowingRepository sowingRepository;

    public VisitService(VisitRepository visitRepository, SowingRepository sowingRepository) {
        this.visitRepository = visitRepository;
        this.sowingRepository = sowingRepository;
    }

    public List<Visit> getVisitsBySowing(Integer sowingId) {
        return visitRepository.getBySowingId(sowingId);
    }

    public Visit getVisitById(Integer id) {
        return visitRepository.getById(id);
    }

    /** Plántulas aptas de la última visita activa, o null si la siembra no tiene visitas. */
    public Integer getLatestAvailableSeedlings(Integer sowingId) {
        Visit latest = visitRepository.getLatestActiveBySowingId(sowingId);
        return latest != null ? latest.getSuitableEstimated() : null;
    }

    public Visit createVisit(Integer sowingId, LocalDate visitDate, Integer suitableEstimated,
                             VisitMethod visitMethod, VisitResult result) {
        return createVisit(sowingId, visitDate, suitableEstimated, visitMethod, result, null);
    }

    public Visit createVisit(Integer sowingId, LocalDate visitDate, Integer suitableEstimated,
                             VisitMethod visitMethod, VisitResult result, String notes) {
        Sowing sowing = requireSowing(sowingId);
        LocalDate date = visitDate != null ? visitDate : LocalDate.now();
        validateSuitableEstimated(suitableEstimated, sowing);
        validateVisitDate(date, sowing);

        Visit visit = new Visit();
        visit.setSowingId(sowingId);
        visit.setVisitDate(date);
        visit.setSuitableEstimated(suitableEstimated);
        visit.setVisitMethod(visitMethod != null ? visitMethod : VisitMethod.INSPECCION_VISUAL);
        visit.setResult(result != null ? result : VisitResult.ESTIMACION);
        visit.setNotes(cleanNotes(notes));
        visit.setActive(true);
        return visitRepository.save(visit);
    }

    public Visit updateVisit(Integer id, LocalDate visitDate, Integer suitableEstimated,
                             VisitMethod visitMethod, VisitResult result, Boolean active) {
        return updateVisit(id, visitDate, suitableEstimated, visitMethod, result, active, null);
    }

    public Visit updateVisit(Integer id, LocalDate visitDate, Integer suitableEstimated,
                             VisitMethod visitMethod, VisitResult result, Boolean active, String notes) {
        Visit visit = requireVisit(id);
        Sowing sowing = requireSowing(visit.getSowingId());
        LocalDate date = visitDate != null ? visitDate : visit.getVisitDate();
        validateSuitableEstimated(suitableEstimated, sowing);
        validateVisitDate(date, sowing);

        visit.setVisitDate(date);
        visit.setSuitableEstimated(suitableEstimated);
        visit.setVisitMethod(visitMethod != null ? visitMethod : visit.getVisitMethod());
        visit.setResult(result != null ? result : visit.getResult());
        if (notes != null) {
            visit.setNotes(cleanNotes(notes));
        }
        if (active != null) {
            visit.setActive(active);
        }
        return visitRepository.save(visit);
    }

    public void deleteVisitById(Integer id) {
        requireVisit(id);
        visitRepository.deleteById(id);
    }

    private String cleanNotes(String notes) {
        if (notes == null || notes.isBlank()) {
            return null;
        }
        String clean = notes.trim();
        if (clean.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException(
                    "Las observaciones no pueden superar los " + MAX_NOTES_LENGTH + " caracteres");
        }
        return clean;
    }

    private void validateSuitableEstimated(Integer suitableEstimated, Sowing sowing) {
        if (suitableEstimated == null || suitableEstimated < 0) {
            throw new IllegalArgumentException(
                    "La cantidad estimada de plántulas aptas debe ser mayor o igual a cero");
        }
        if (sowing.getQuantitySown() != null && suitableEstimated > sowing.getQuantitySown()) {
            throw new IllegalArgumentException("La cantidad estimada de plántulas aptas ("
                    + suitableEstimated + ") no puede superar la cantidad sembrada ("
                    + sowing.getQuantitySown() + ")");
        }
    }

    private void validateVisitDate(LocalDate date, Sowing sowing) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de la visita no puede ser futura");
        }
        if (sowing.getSowingDate() != null && date.isBefore(sowing.getSowingDate())) {
            throw new IllegalArgumentException(
                    "La fecha de la visita no puede ser anterior a la fecha de siembra");
        }
    }

    private Sowing requireSowing(Integer sowingId) {
        Sowing sowing = sowingRepository.getById(sowingId);
        if (sowing == null) {
            throw new IllegalArgumentException("Siembra no encontrada: " + sowingId);
        }
        return sowing;
    }

    private Visit requireVisit(Integer id) {
        Visit visit = visitRepository.getById(id);
        if (visit == null) {
            throw new IllegalArgumentException("Visita no encontrada: " + id);
        }
        return visit;
    }
}
