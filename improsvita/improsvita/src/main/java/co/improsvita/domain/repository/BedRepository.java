package co.improsvita.domain.repository;

import co.improsvita.domain.model.Bed;

import java.util.List;

public interface BedRepository {
    List<Bed> getAll();
    Bed getById(Integer id);
    Bed getByCode(String code);
    Bed save(Bed bed);
    void deleteById(Integer id);
}