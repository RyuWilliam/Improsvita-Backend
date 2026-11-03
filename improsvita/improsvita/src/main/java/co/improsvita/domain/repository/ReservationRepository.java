package co.improsvita.domain.repository;

import co.improsvita.domain.model.Reservation;

import java.util.List;

public interface ReservationRepository {
    List<Reservation> getAll();
    List<Reservation> getBySowingId(Integer sowingId);
    List<Reservation> getByClientId(Integer clientId);
    Reservation getById(Integer id);
    Reservation save(Reservation reservation);
    void deleteById(Integer id);
}
