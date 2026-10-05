package co.improsvita.domain.repository;

import co.improsvita.domain.model.Sowing;
import co.improsvita.domain.model.SowingStatus;

import java.util.List;

public interface SowingRepository {
    List<Sowing> getAll();
    Sowing getById(Integer id);
    Sowing save(Sowing sowing);
    void deleteById(Integer id);
    List<Sowing> getByLotId(Integer lotId);
    List<Sowing> getByBedId(Integer bedId);
    List<Sowing> getByStatus(SowingStatus status);
    List<Sowing> getByBedCode(String code);
}