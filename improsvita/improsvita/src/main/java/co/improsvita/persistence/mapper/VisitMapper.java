package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Visit;
import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.entities.VisitEntity;

public class VisitMapper {

    private VisitMapper() {
    }

    public static Visit toDomain(VisitEntity entity) {
        if (entity == null) return null;
        Visit visit = new Visit();
        visit.setId(entity.getVisitId());
        visit.setSowingId(entity.getSowing() != null ? entity.getSowing().getSowingId() : null);
        visit.setVisitDate(entity.getVisitDate());
        visit.setSuitableEstimated(entity.getSuitableEstimated());
        visit.setVisitMethod(mapMethod(entity.getVisitMethod()));
        visit.setResult(mapResult(entity.getResult()));
        visit.setActive(entity.getActive());
        return visit;
    }

    public static VisitEntity toEntity(Visit domain, SowingEntity sowing) {
        if (domain == null) return null;
        VisitEntity entity = new VisitEntity();
        entity.setVisitId(domain.getId());
        entity.setSowing(sowing);
        entity.setVisitDate(domain.getVisitDate());
        entity.setSuitableEstimated(domain.getSuitableEstimated());
        entity.setVisitMethod(mapMethod(domain.getVisitMethod()));
        entity.setResult(mapResult(domain.getResult()));
        entity.setActive(domain.getActive());
        return entity;
    }

    private static co.improsvita.domain.model.VisitMethod mapMethod(
            co.improsvita.persistence.enums.VisitMethod entityMethod) {
        if (entityMethod == null) return null;
        return co.improsvita.domain.model.VisitMethod.valueOf(entityMethod.name());
    }

    private static co.improsvita.persistence.enums.VisitMethod mapMethod(
            co.improsvita.domain.model.VisitMethod domainMethod) {
        if (domainMethod == null) return null;
        return co.improsvita.persistence.enums.VisitMethod.valueOf(domainMethod.name());
    }

    private static co.improsvita.domain.model.VisitResult mapResult(
            co.improsvita.persistence.enums.VisitResult entityResult) {
        if (entityResult == null) return null;
        return co.improsvita.domain.model.VisitResult.valueOf(entityResult.name());
    }

    private static co.improsvita.persistence.enums.VisitResult mapResult(
            co.improsvita.domain.model.VisitResult domainResult) {
        if (domainResult == null) return null;
        return co.improsvita.persistence.enums.VisitResult.valueOf(domainResult.name());
    }
}
