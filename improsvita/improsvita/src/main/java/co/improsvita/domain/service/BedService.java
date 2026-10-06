package co.improsvita.domain.service;

import co.improsvita.domain.model.Bed;
import co.improsvita.domain.repository.BedRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BedService {

    private final BedRepository bedRepository;

    public BedService(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    public List<Bed> getAllBeds() {
        return bedRepository.getAll();
    }

    public Bed getBedById(Integer id) {
        return bedRepository.getById(id);
    }

    public Bed getBedByCode(String code) {
        return bedRepository.getByCode(code);
    }

    public Bed createBed(Bed bed) {
        return bedRepository.save(bed);
    }

    public Bed updateBed(Bed bed) {
        return bedRepository.save(bed);
    }

    public void deleteBedById(Integer id) {
        bedRepository.deleteById(id);
    }
}