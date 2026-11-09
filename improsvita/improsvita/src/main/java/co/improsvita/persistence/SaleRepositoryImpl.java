package co.improsvita.persistence;

import co.improsvita.domain.model.Sale;
import co.improsvita.domain.model.SaleType;
import co.improsvita.domain.repository.SaleRepository;
import co.improsvita.persistence.crud.ContactJpaRepository;
import co.improsvita.persistence.crud.ReservationJpaRepository;
import co.improsvita.persistence.crud.SaleJpaRepository;
import co.improsvita.persistence.crud.SowingJpaRepository;
import co.improsvita.persistence.entities.ContactEntity;
import co.improsvita.persistence.entities.ReservationEntity;
import co.improsvita.persistence.entities.SaleEntity;
import co.improsvita.persistence.entities.SowingEntity;
import co.improsvita.persistence.mapper.SaleMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class SaleRepositoryImpl implements SaleRepository {

    private final SaleJpaRepository saleJpaRepository;
    private final ContactJpaRepository contactJpaRepository;
    private final SowingJpaRepository sowingJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    public SaleRepositoryImpl(SaleJpaRepository saleJpaRepository,
                              ContactJpaRepository contactJpaRepository,
                              SowingJpaRepository sowingJpaRepository,
                              ReservationJpaRepository reservationJpaRepository) {
        this.saleJpaRepository = saleJpaRepository;
        this.contactJpaRepository = contactJpaRepository;
        this.sowingJpaRepository = sowingJpaRepository;
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public List<Sale> getAll(Integer contactId, SaleType type, LocalDate from, LocalDate to) {
        Sort newestFirst = Sort.by(Sort.Order.desc("saleDate"), Sort.Order.desc("saleId"));
        return saleJpaRepository.findAll(historyFilter(contactId, mapType(type), from, to), newestFirst)
                .stream()
                .map(SaleMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Arma el filtro del historial agregando predicados solo cuando el valor
     * viene informado. Evita el "? IS NULL" que PostgreSQL no puede tipar.
     */
    private Specification<SaleEntity> historyFilter(Integer contactId,
                                                    co.improsvita.persistence.enums.SaleType type,
                                                    LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (contactId != null) {
                predicates.add(cb.equal(root.get("contact").get("contactId"), contactId));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("saleType"), type));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("saleDate"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("saleDate"), to));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<Sale> getBySowingId(Integer sowingId) {
        return saleJpaRepository.findBySowing_SowingIdOrderBySaleDateDesc(sowingId)
                .stream()
                .map(SaleMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Sale getById(Integer id) {
        return saleJpaRepository.findById(id)
                .map(SaleMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Integer getSoldQuantity(Integer sowingId) {
        Integer sold = saleJpaRepository.sumActiveQuantityBySowing(sowingId);
        return sold != null ? sold : 0;
    }

    @Override
    public Sale getByReservationId(Integer reservationId) {
        return saleJpaRepository.findByReservation_ReservationId(reservationId)
                .stream()
                .filter(entity -> Boolean.TRUE.equals(entity.getActive()))
                .findFirst()
                .map(SaleMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public Sale save(Sale sale) {
        ContactEntity contact = contactJpaRepository.getReferenceById(sale.getContactId());
        SowingEntity sowing = sowingJpaRepository.getReferenceById(sale.getSowingId());
        ReservationEntity reservation = sale.getReservationId() != null
                ? reservationJpaRepository.getReferenceById(sale.getReservationId())
                : null;
        return SaleMapper.toDomain(saleJpaRepository.save(
                SaleMapper.toEntity(sale, contact, sowing, reservation)));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        saleJpaRepository.deleteById(id);
    }

    private co.improsvita.persistence.enums.SaleType mapType(SaleType type) {
        return type != null ? co.improsvita.persistence.enums.SaleType.valueOf(type.name()) : null;
    }
}
