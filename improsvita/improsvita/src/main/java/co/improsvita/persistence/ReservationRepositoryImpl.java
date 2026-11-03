package co.improsvita.persistence;

import co.improsvita.domain.model.Reservation;
import co.improsvita.domain.repository.ReservationRepository;
import co.improsvita.persistence.crud.ClientJpaRepository;
import co.improsvita.persistence.crud.ReservationJpaRepository;
import co.improsvita.persistence.crud.SowingJpaRepository;
import co.improsvita.persistence.entities.ClientEntity;
import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.mapper.ReservationMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class ReservationRepositoryImpl implements ReservationRepository {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "reservationDate");

    private final ReservationJpaRepository reservationJpaRepository;
    private final SowingJpaRepository sowingJpaRepository;
    private final ClientJpaRepository clientJpaRepository;

    public ReservationRepositoryImpl(ReservationJpaRepository reservationJpaRepository,
                                     SowingJpaRepository sowingJpaRepository,
                                     ClientJpaRepository clientJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
        this.sowingJpaRepository = sowingJpaRepository;
        this.clientJpaRepository = clientJpaRepository;
    }

    @Override
    public List<Reservation> getAll() {
        return reservationJpaRepository.findAll(NEWEST_FIRST)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> getBySowingId(Integer sowingId) {
        return reservationJpaRepository.findBySowing_SowingIdOrderByReservationDateDesc(sowingId)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> getByClientId(Integer clientId) {
        return reservationJpaRepository.findByClient_ClientIdOrderByReservationDateDesc(clientId)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Reservation getById(Integer id) {
        return reservationJpaRepository.findById(id)
                .map(ReservationMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        SowingEntity sowing = sowingJpaRepository.getReferenceById(reservation.getSowingId());
        ClientEntity client = clientJpaRepository.getReferenceById(reservation.getClientId());
        return ReservationMapper.toDomain(
                reservationJpaRepository.save(ReservationMapper.toEntity(reservation, sowing, client)));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        reservationJpaRepository.deleteById(id);
    }
}
