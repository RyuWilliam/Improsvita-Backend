package co.improsvita.domain.repository;

import co.improsvita.domain.model.Visit;

import java.util.List;

public interface VisitRepository {
    List<Visit> getBySowingId(Integer sowingId);
    Visit getById(Integer id);
    Visit save(Visit visit);
    void deleteById(Integer id);
}
