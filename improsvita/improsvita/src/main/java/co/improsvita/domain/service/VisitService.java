package co.improsvita.domain.service;

import co.improsvita.domain.model.Visit;
import co.improsvita.domain.model.VisitMethod;
import co.improsvita.domain.model.VisitResult;
import co.improsvita.domain.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public List<Visit> getVisitsBySowing(Integer sowingId) {
        return visitRepository.getBySowingId(sowingId);
    }

    public Visit getVisitById(Integer id) {
        return visitRepository.getById(id);
    }

    public Visit createVisit(Integer sowingId, LocalDate visitDate, Integer suitableEstimated,
                             VisitMethod visitMethod, VisitResult result) {
        requireSuitableEstimated(suitableEstimated);

        Visit visit = new Visit();
        visit.setSowingId(sowingId);
        visit.setVisitDate(visitDate != null ? visitDate : LocalDate.now());
        visit.setSuitableEstimated(suitableEstimated);
        visit.setVisitMethod(visitMethod != null ? visitMethod : VisitMethod.INSPECCION_VISUAL);
        visit.setResult(result != null ? result : VisitResult.ESTIMACION);
        visit.setActive(true);
        return visitRepository.save(visit);
    }

    public Visit updateVisit(Integer id, LocalDate visitDate, Integer suitableEstimated,
                             VisitMethod visitMethod, VisitResult result, Boolean active) {
        Visit visit = requireVisit(id);
        requireSuitableEstimated(suitableEstimated);

        visit.setVisitDate(visitDate != null ? visitDate : visit.getVisitDate());
        visit.setSuitableEstimated(suitableEstimated);
        visit.setVisitMethod(visitMethod != null ? visitMethod : visit.getVisitMethod());
        visit.setResult(result != null ? result : visit.getResult());
        if (active != null) {
            visit.setActive(active);
        }
        return visitRepository.save(visit);
    }

    public void deleteVisitById(Integer id) {
        requireVisit(id);
        visitRepository.deleteById(id);
    }

    private void requireSuitableEstimated(Integer suitableEstimated) {
        if (suitableEstimated == null || suitableEstimated < 0) {
            throw new IllegalArgumentException("La cantidad estimada de plántulas aptas debe ser mayor o igual a cero");
        }
    }

    private Visit requireVisit(Integer id) {
        Visit visit = visitRepository.getById(id);
        if (visit == null) {
            throw new IllegalArgumentException("Visita no encontrada: " + id);
        }
        return visit;
    }
}
