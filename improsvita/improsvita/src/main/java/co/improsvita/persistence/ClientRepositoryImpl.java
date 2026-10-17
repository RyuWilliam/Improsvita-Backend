package co.improsvita.persistence;

import co.improsvita.domain.model.Client;
import co.improsvita.domain.repository.ClientRepository;
import co.improsvita.persistence.crud.ClientJpaRepository;
import co.improsvita.persistence.entities.ClientEntity;
import co.improsvita.persistence.mapper.ClientMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository clientJpaRepository;

    public ClientRepositoryImpl(ClientJpaRepository clientJpaRepository) {
        this.clientJpaRepository = clientJpaRepository;
    }

    @Override
    public List<Client> getAll() {
        return clientJpaRepository.findAll()
                .stream()
                .map(ClientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Client getById(Integer id) {
        return clientJpaRepository.findById(id)
                .map(ClientMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Client getByName(String name) {
        return clientJpaRepository.findByName(name)
                .map(ClientMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Client save(Client client) {
        if (client.getEmail() != null) {
            clientJpaRepository.findByEmail(client.getEmail())
                    .filter(existing -> !existing.getClientId().equals(client.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalStateException("Ya existe un cliente con el email " + client.getEmail());
                    });
        }
        ClientEntity saved = clientJpaRepository.save(ClientMapper.toEntity(client));
        return ClientMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Integer id) {
        clientJpaRepository.deleteById(id);
    }
}
